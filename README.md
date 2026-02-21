# DHBW-Mannheim-WI2024SEA-EDA-CQRS

1. Fork anlegen
2. Eigenen Ordner anlegen
3. Aufgabe 3+4 bearbeiten (10 Punkte)

## Write:

http://localhost:5001/

## Read:

http://localhost:5002/

## Aufbau
- Frontend kann nicht direkt mit RabbitMQ sprechen
- Browser spricht über HTTP oder WebSockets, aber RabbitMQ spricht standardmäßig AMQP
- Vermittler ist flask Webserver, welcher Eingabe vom Frontend über HTTP entgegenimmt und über pika an Rabbitmq weiterleutet
