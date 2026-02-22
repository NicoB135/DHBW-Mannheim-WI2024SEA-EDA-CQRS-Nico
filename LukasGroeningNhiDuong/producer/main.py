import time
from fastapi import FastAPI
import pika
import uvicorn
from fastapi.middleware.cors import CORSMiddleware

RABBITMQ_HOST = 'rabbitmq'
EXCHANGE_NAME = 'button_events'
EVENT_MESSAGE = 'button_pressed'

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

def get_rabbitmq_connection(retries=5, delay=2) -> pika.BlockingConnection:
    for attempt in range(retries):
        try:
            return pika.BlockingConnection(pika.ConnectionParameters(host=RABBITMQ_HOST))
        except Exception:
            print(f"Versuch {attempt+1}/{retries} fehlgeschlagen, warte {delay}s...", flush=True)
            time.sleep(delay)
    raise RuntimeError("RabbitMQ nicht erreichbar nach mehreren Versuchen")


@app.post("/press_button")
def press_button() -> dict:
    connection = get_rabbitmq_connection()
    channel = connection.channel()

    channel.exchange_declare(exchange=EXCHANGE_NAME, exchange_type='fanout')

    channel.basic_publish(
        exchange=EXCHANGE_NAME, 
        routing_key='', 
        body=EVENT_MESSAGE
    )
    
    connection.close()
    
    return {
        "status": "success", 
        "event": f"Sent: {EVENT_MESSAGE}"
    }


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)