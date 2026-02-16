from flask import Flask, request, jsonify, send_from_directory
from flask_cors import CORS
import pika, json

app = Flask(__name__, static_folder='/feeding-command-service')
CORS(app)

def send_event(event_type, data):
    # Verbindung zu RabbitMQ aufbauen (Host aus docker-compose)
    try:
        connection = pika.BlockingConnection(pika.ConnectionParameters(host='rabbitmq'))
        channel = connection.channel()
        channel.exchange_declare(exchange='cat_events', exchange_type='fanout')
        
        message = json.dumps({'event': event_type, 'data': data})
        channel.basic_publish(exchange='cat_events', routing_key='', body=message)
        print(f"✅ Event gesendet: {message}")
        connection.close()
    except Exception as e:
        print(f"❌ Fehler beim Senden des Events: {e}")

@app.route('/')
def index():
    return send_from_directory('/feeding-command-service', 'index.html')

@app.route('/feed', methods=['GET', 'POST', 'OPTIONS'])
def feed_cat():
    if request.method == 'OPTIONS':
        return '', 204
    
    # Hier würde normalerweise die Validierung/Logik stehen [cite: 62]
    try:
        data = request.get_json(force=True) if request.data else {}
    except:
        data = {}
    
    cat_name = data.get('name', 'Unbekannte Katze')
    
    # Event auslösen: "Fütterung durchgeführt" [cite: 70, 83]
    send_event('cat.fed', {'name': cat_name, 'amount': '50g'})
    
    return jsonify({"status": "Command gesendet: Katze wird gefüttert!"}), 202

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)