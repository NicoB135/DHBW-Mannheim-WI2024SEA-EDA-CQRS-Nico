package main

import (
	"html/template"
	"net/http"

	"github.com/ThreeDotsLabs/watermill"
	"github.com/ThreeDotsLabs/watermill-amqp/v3/pkg/amqp"
	"github.com/ThreeDotsLabs/watermill/message"
)

var amqpURI = "amqp://guest:guest@localhost:5672/"
var publisher message.Publisher

const htmlTemplate = `
<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Message Publisher ≽^•⩊•^≼</title>
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
            max-width: 600px;
            margin: 50px auto;
            background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
            border: 8px solid #4a4a4a;
            border-radius: 12px;
            padding: 30px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5), inset 0 1px 0 rgba(255, 255, 255, 0.1);
        }
        
        h1 {
            color: #ffd700;
            text-align: center;
            margin-bottom: 30px;
            font-size: 32px;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
        }
        
        form {
            display: flex;
            flex-direction: column;
        }
        
        label {
            margin-bottom: 8px;
            font-weight: bold;
            color: #ffd700;
            font-size: 16px;
        }
        
        input[type="text"], textarea {
            padding: 12px;
            margin-bottom: 20px;
            border: 2px solid #4a4a4a;
            border-radius: 8px;
            font-family: 'Courier New', monospace;
            font-size: 14px;
            background-color: #1a1a1a;
            color: #ffd700;
            transition: all 0.3s ease;
        }
        
        input[type="text"]:focus, textarea:focus {
            outline: none;
            border-color: #ffd700;
            box-shadow: 0 0 10px rgba(255, 215, 0, 0.3);
        }
        
        textarea {
            resize: vertical;
            min-height: 120px;
        }
        
        input[type="text"]::placeholder, textarea::placeholder {
            color: #888;
        }
        
        input[type="submit"] {
            padding: 15px;
            background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
            color: #2a2a2a;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 16px;
            font-weight: bold;
            font-family: 'Courier New', monospace;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
            transition: all 0.3s ease;
        }
        
        input[type="submit"]:hover {
            transform: scale(1.05);
            box-shadow: 0 6px 20px rgba(255, 215, 0, 0.4);
        }
        
        input[type="submit"]:active {
            transform: scale(0.98);
        }
        
        .message {
            margin-top: 20px;
            padding: 15px;
            border-radius: 8px;
            text-align: center;
            font-weight: bold;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
        }
        
        .success {
            background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
            color: #2a2a2a;
            border: 1px solid rgba(0, 0, 0, 0.1);
        }
        
        .error {
            background: linear-gradient(135deg, #ff6b6b 0%, #ff8787 100%);
            color: #1a1a1a;
            border: 1px solid rgba(0, 0, 0, 0.1);
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Message Publisher ≽^•⩊•^≼</h1>
        <form method="POST" action="/publish">
            <label for="message">Nachricht:</label>
            <textarea id="message" name="message" required placeholder="Hier Nachricht eingeben"></textarea>
            <input type="submit" value="Senden">
        </form>
        {{if .Message}}
            <div class="message {{.MessageType}}">
                {{.Message}}
            </div>
        {{end}}
    </div>
</body>
</html>
`

type TemplateData struct {
	Message     string
	MessageType string
}

func main() {
	var err error
	amqpConfig := amqp.NewDurableQueueConfig(amqpURI)

	publisher, err = amqp.NewPublisher(amqpConfig, watermill.NewStdLogger(false, false))
	if err != nil {
		panic(err)
	}

	http.HandleFunc("/", handleIndex)
	http.HandleFunc("/publish", handlePublish)

	if err := http.ListenAndServe(":8080", nil); err != nil {
		panic(err)
	}
}

func handleIndex(w http.ResponseWriter, r *http.Request) {
	tmpl, err := template.New("index").Parse(htmlTemplate)
	if err != nil {
		http.Error(w, "Template-Fehler", http.StatusInternalServerError)
		return
	}

	data := TemplateData{}
	tmpl.Execute(w, data)
}

func handlePublish(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Redirect(w, r, "/", http.StatusSeeOther)
		return
	}

	topic := "example-topic"
	messageText := r.FormValue("message")

	if messageText == "" {
		http.Redirect(w, r, "/", http.StatusSeeOther)
		return
	}

	msg := message.NewMessage(watermill.NewUUID(), []byte(messageText))
	err := publisher.Publish(topic, msg)
	if err != nil {
		println("Nachricht konnte nicht versendet werden ≽(•ᆺ •マ≼")
		return
	}

	tmpl, _ := template.New("success").Parse(htmlTemplate)
	data := TemplateData{
		Message:     "Nachricht versendet ₍^. .^₎Ⳋ",
		MessageType: "success",
	}
	w.Header().Set("Content-Type", "text/html; charset=utf-8")
	tmpl.Execute(w, data)
}
