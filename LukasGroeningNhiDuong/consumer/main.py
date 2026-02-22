import threading
import time
from fastapi import FastAPI
import pika
import uvicorn
from fastapi.middleware.cors import CORSMiddleware

RABBITMQ_HOST = "rabbitmq"
EXCHANGE_NAME = "button_events"

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

light_status = {
    "is_light_on": False,
    "last_update": "Nie"
}

def toggle_light() -> None:
    light_status["is_light_on"] = not light_status["is_light_on"]
    light_status["last_update"] = time.strftime("%H:%M:%S")

    print(f"--- LICHT GETOGGLED: Jetzt {'AN' if light_status['is_light_on'] else 'AUS'} ---", flush=True)


def start_event_listener():
    connection = None
    while not connection:
        try:
            connection = pika.BlockingConnection(pika.ConnectionParameters(host=RABBITMQ_HOST))
        except:
            time.sleep(5)

    channel = connection.channel()
    channel.exchange_declare(exchange=EXCHANGE_NAME, exchange_type='fanout')
    result = channel.queue_declare(queue='', exclusive=True)
    queue_name = result.method.queue
    channel.queue_bind(exchange=EXCHANGE_NAME, queue=queue_name)

    def callback(ch, method, properties, body):
        event = body.decode()
        print(f"Received event: {event}")
        if event == "button_pressed":
            toggle_light()

    channel.basic_consume(queue=queue_name, on_message_callback=callback, auto_ack=True)
    print("Waiting for button press events...")
    channel.start_consuming()

threading.Thread(target=start_event_listener, daemon=True).start()

@app.get("/status")
def get_status() -> dict:
    return light_status

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
