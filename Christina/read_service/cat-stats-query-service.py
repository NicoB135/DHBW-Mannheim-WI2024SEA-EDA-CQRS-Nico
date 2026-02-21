from flask import Flask, jsonify, send_from_directory, request
from flask_cors import CORS
import threading, pika, json, os, time


app = Flask(__name__, static_folder='/cat-stats-query-service', static_url_path='')
CORS(app)

stats = {"feed_count": 0, "last_fed": None}
QUEUE = "HELLO"
EXCHANGE = "cats_exchange"

def consume_events():
    while True:
        try:
            print("Verbinde zu RabbitMQ...")
            host = os.environ.get('RABBITMQ_HOST', 'localhost')
            connection = pika.BlockingConnection(pika.ConnectionParameters(host=host))

            channel = connection.channel()
            # Deklariere Exchange und Queue mit durable=True
            channel.exchange_declare(exchange=EXCHANGE, exchange_type='direct', durable=True)
            channel.queue_declare(queue=QUEUE, durable=True)
            channel.queue_bind(exchange=EXCHANGE, queue=QUEUE, routing_key=QUEUE)
            print(f"Event-Listener bereit auf Queue: {QUEUE}")

            def callback(ch, method, properties, body):
                try:
                    data = json.loads(body)
                    print(f"Event erhalten: {data}")
                    if data['event'] == 'cat.fed':
                        stats["feed_count"] += 1
                        stats["last_fed"] = data['data']['name']
                        print(f"Readmodel aktualisiert: {stats}")
                except Exception as e:
                    print(f"Fehler beim Verarbeiten des Events: {e}")

            channel.basic_consume(queue=QUEUE, on_message_callback=callback, auto_ack=True)
            print("Höre auf Events...")
            channel.start_consuming()
        except Exception as e:
            print(f"Fehler in consume_events: {e}")
            time.sleep(5)

# Sevent consumer starten in eigenen thread
consumer_thread = threading.Thread(target=consume_events, daemon=True)
consumer_thread.start()

@app.route('/')
def index():
    return send_from_directory('/cat-stats-query-service', 'index.html')

@app.route('/stats', methods=['GET', 'OPTIONS'])
def get_stats():
    if request.method == 'OPTIONS':
        return '', 204
    return jsonify(stats)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)