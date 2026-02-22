# DHBW AudioDB Streamer

Ein System, in dem man eine dB Anzahl hochladen kann und einem maxDb, AverageDb und die Anzahl zurückgibt. Messwerte werden als Commands übermittelt, über RabbitMQ als Events verteilt und in einem Read-Model abfragbar gemacht.

## Starten

```bash
docker-compose up --build
```

Die App ist danach erreichbar unter `http://localhost:8080`.

Das RabbitMQ Management UI ist erreichbar unter `http://localhost:15672` (User: `guest`, Passwort: `guest`).

## API

### Command — Messwert hinzufügen

```bash
curl -X POST http://localhost:8080/commands/volume \
  -H "Content-Type: application/json" \
  -d "{\"decibel\": 85.5}"
```

**Validierung:**
- Dezibel muss zwischen `0` und `194` liegen
- Bei ungültigem Wert: `400 Bad Request` mit Fehlermeldung

### Query — Stats abfragen

```bash
curl http://localhost:8080/queries/stats
```

**Antwort:**
```json
{
  "maxDb": 95.0,
  "averageDb": 84.26,
  "count": 3
}
```
## Technologien

- Java 21
- Spring Boot 4.0.2
- Spring AMQP / RabbitMQ
- Docker & Docker Compose
