package main

import (
	"context"
	"encoding/json"
	"html/template"
	"log"
	"net/http"
	"sync"
	"time"

	"github.com/ThreeDotsLabs/watermill"
	"github.com/ThreeDotsLabs/watermill-amqp/v3/pkg/amqp"
	"github.com/ThreeDotsLabs/watermill/message"
)

var amqpURI = "amqp://guest:guest@localhost:5672/"

type MessageBoard struct {
	Messages []BoardMessage `json:"messages"`
	mu       sync.RWMutex
}

type BoardMessage struct {
	ID        string    `json:"id"`
	Content   string    `json:"content"`
	Timestamp time.Time `json:"timestamp"`
}

var board = &MessageBoard{
	Messages: []BoardMessage{},
}

const htmlTemplate = `
<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Message Subscriber ≽^•⩊•^≼</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Courier New', monospace;
            background-color: #2a2a2a;
            padding: 20px;
            min-height: 100vh;
        }
        
        .container {
            max-width: 1000px;
            margin: 0 auto;
        }
        
        h1 {
            text-align: center;
            color: #ffd700;
            margin-bottom: 30px;
            font-size: 32px;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
        }
        
        .board-container {
            background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
            border: 8px solid #4a4a4a;
            border-radius: 12px;
            padding: 30px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5), inset 0 1px 0 rgba(255, 255, 255, 0.1);
            min-height: 400px;
        }
        
        .board-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
            padding-bottom: 15px;
            border-bottom: 2px solid #ffd700;
        }
        
        .board-title {
            font-size: 20px;
            color: #ffd700;
            font-weight: bold;
        }
        
        .message-count {
            background-color: #ffd700;
            color: #2a2a2a;
            padding: 5px 15px;
            border-radius: 20px;
            font-weight: bold;
        }
        
        .messages-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 20px;
            min-height: 300px;
        }
        
        .message-card {
            background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
            color: #2a2a2a;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
            position: relative;
            transform: rotate(-1deg);
            transition: all 0.3s ease;
            border: 1px solid rgba(0, 0, 0, 0.1);
        }
        
        .message-card:nth-child(even) {
            transform: rotate(1deg);
        }
        
        .message-card:hover {
            transform: rotate(0deg) scale(1.05);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.4);
        }
        
        .message-content {
            word-wrap: break-word;
            word-break: break-word;
            margin-bottom: 10px;
            line-height: 1.6;
            font-size: 14px;
        }
        
        .message-timestamp {
            font-size: 11px;
            color: rgba(0, 0, 0, 0.6);
            text-align: right;
            border-top: 1px solid rgba(0, 0, 0, 0.1);
            padding-top: 8px;
        }
        
        .empty-message {
            grid-column: 1 / -1;
            text-align: center;
            color: #888;
            padding: 60px 20px;
            font-size: 18px;
        }
        
        @keyframes pulse {
            0%, 100% {
                opacity: 1;
            }
            50% {
                opacity: 0.5;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Schwarzes Brett ≽^•⩊•^≼</h1>
        
        <div class="board-container">
            <div class="board-header">
                <div class="board-title">Eingegangene Nachrichten ≽(•ᆺ •マ≼</div>
                <div class="message-count" id="message-count">0</div>
            </div>
            
            <div class="messages-grid" id="messages-container">
                <div class="empty-message">Aktuell keine Nachrichten ₍^. .^₎Ⳋ</div>
            </div>
        </div>
    </div>
    
    <script>
        function fetchMessages() {
            fetch('/api/messages')
                .then(response => response.json())
                .then(data => {
                    updateUI(data.messages || []);
                })
                .catch(error => console.error('Fehler beim Abrufen der Nachrichten:', error));
        }
        
        function updateUI(messages) {
            const container = document.getElementById('messages-container');
            const countElement = document.getElementById('message-count');
            
            countElement.textContent = messages.length;
            
            if (messages.length === 0) {
                container.innerHTML = '<div class="empty-message">Keine Nachrichten vorhanden. Warte auf Nachrichten...</div>';
                return;
            }
            
            container.innerHTML = messages.map(msg => {
                const timestamp = new Date(msg.timestamp);
                const timeString = timestamp.toLocaleString('de-DE');
                
                return '<div class="message-card">' +
                    '<div class="message-content">' + escapeHtml(msg.content) + '</div>' +
                    '<div class="message-timestamp">' + timeString + '</div>' +
                    '</div>';
            }).join('');
        }
        
        function escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }
        
        // Initial load and then refresh every 2 seconds
        fetchMessages();
        setInterval(fetchMessages, 2000);
    </script>
</body>
</html>
`

func main() {
	amqpConfig := amqp.NewDurableQueueConfig(amqpURI)

	subscriber, err := amqp.NewSubscriber(amqpConfig, watermill.NewStdLogger(false, false))
	if err != nil {
		panic(err)
	}

	messages, err := subscriber.Subscribe(context.Background(), "example-topic")
	if err != nil {
		panic(err)
	}

	// Start message processing in goroutine
	go process(messages)

	// Start HTTP server
	http.HandleFunc("/", handleIndex)
	http.HandleFunc("/api/messages", handleMessages)

	if err := http.ListenAndServe(":8081", nil); err != nil {
		panic(err)
	}
}

func process(messages <-chan *message.Message) {
	for msg := range messages {
		content := string(msg.Payload)
		log.Printf("Received message: %s, payload: %s", msg.UUID, content)

		// Add message to board
		board.mu.Lock()
		board.Messages = append(board.Messages, BoardMessage{
			ID:        msg.UUID,
			Content:   content,
			Timestamp: time.Now(),
		})
		board.mu.Unlock()

		// Acknowledge the message
		msg.Ack()
	}
}

func handleIndex(w http.ResponseWriter, r *http.Request) {
	tmpl, err := template.New("board").Parse(htmlTemplate)
	if err != nil {
		http.Error(w, "Template Error", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "text/html; charset=utf-8")
	tmpl.Execute(w, nil)
}

func handleMessages(w http.ResponseWriter, r *http.Request) {
	board.mu.RLock()
	defer board.mu.RUnlock()

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]interface{}{
		"messages": board.Messages,
	})
}
