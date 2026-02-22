# Smart Home System - EDA & CQRS Demo

Dieses Projekt demonstriert eine einfache Implementierung von **EDA** und **CQRS** mittels Python, RabbitMQ und Docker.

## Architektur
- **Producer (Command Side):** Ein Service auf Port `8001`, der Befehle entgegennimmt und Events an den Message Broker sendet.
- **RabbitMQ (Event Bus):** Verteilt Events asynchron zwischen den Services.
- **Consumer (Query Side):** Ein Service auf Port `8002`, der auf Events hört, seinen internen Zustand (Read-Model) aktualisiert und diesen über eine API bereitstellt.



## Voraussetzungen
- Docker & Docker Compose

## Starten des Systems
1. Navigiere in das Projektverzeichnis.
2. Starte die Container-Infrastruktur:
   ```bash
   docker-compose up --build -d
3. Öffne index.html