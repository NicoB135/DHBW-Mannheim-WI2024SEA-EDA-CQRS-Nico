from flask import Flask, request, jsonify, send_from_directory
from flask_cors import CORS
import pika, json, os

app = Flask(__name__, static_folder='/feeding-command-service')
CORS(app)

QUEUE = "HELLO"
EXCHANGE = "cats_exchange"

def send_event(event_type, data):
    try:
        print("Verbinde zu RabbitMQ...")
        host = os.environ.get('RABBITMQ_HOST', 'localhost')
        connection = pika.BlockingConnection(pika.ConnectionParameters(host=host))

        channel = connection.channel()
        # Deklariere Queue und Exchange
        channel.exchange_declare(exchange=EXCHANGE, exchange_type='direct', durable=True)
        channel.queue_declare(queue=QUEUE, durable=True)
        channel.queue_bind(exchange=EXCHANGE, queue=QUEUE, routing_key=QUEUE)
        
        message = json.dumps({'event': event_type, 'data': data})
        # Persistent Message
        channel.basic_publish(
            exchange=EXCHANGE, 
            routing_key=QUEUE, 
            body=message,
            properties=pika.BasicProperties(delivery_mode=2)
        )
        print(f"Event gesendet: {message}")
        connection.close()
    except Exception as e:
        print(f"Fehler beim Senden des Events: {e}")

@app.route('/')
def index():
    return send_from_directory('/feeding-command-service', 'index.html')

@app.route('/feed', methods=[ 'POST', 'OPTIONS'])
def feed_cat():
    if request.method == 'OPTIONS':
        return '', 204
    
    try:
        data = request.get_json(force=True) if request.data else {}
    except Exception as e:
        print(f"Warnung: Ungültige Request Daten empfangen. Fehler: {e}")
        data = {}
    
    cat_name = data.get('name', 'Unbekannte Katze :0')
    
    send_event('cat.fed', {'name': cat_name})
    
    return jsonify({"status": "Command gesendet: Katze wird gefüttert! :3"}), 202

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)