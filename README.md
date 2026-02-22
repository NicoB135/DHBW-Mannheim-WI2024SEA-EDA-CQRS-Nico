# DHBW-Mannheim-WI2024SEA-EDA-CQRS

1. Fork anlegen
2. Eigenen Ordner anlegen
3. Aufgabe 3+4 bearbeiten (10 Punkte)

---

## Architektur

### EDA – Event-Driven Architecture

Das System kommuniziert ausschließlich über Events. Kein Modul ruft ein anderes direkt auf.

```
[Command] → [AuctionHandler] → [EventBus] → [NotificationHandler]  (Consumer 1: Terminal)
 PlaceBid     (Producer)          │
                                  └──────→ [AuditLogHandler]        (Consumer 2: Log-Datei)
```

### CQRS – Command Query Responsibility Segregation

| Seite    | Zweck                              | Beispiele                                 |
|----------|------------------------------------|-------------------------------------------|
| Command  | Zustand **ändern**                 | `CreateAuction`, `PlaceBid`, `EndAuction` |
| Query    | Zustand **lesen** (keine Änderung) | `get_current_highest_bid`, `get_bid_history` |

---

## Projektstruktur

```
online_auction/
├── core/
│   ├── event_bus.py            # Zentraler Event Bus (Verteiler)
│   ├── base_command.py         # Basisklasse für Commands
│   └── base_event.py           # Basisklasse für Events
├── commands/
│   ├── create_auction.py       # Command: Auktion erstellen
│   ├── place_bid.py            # Command: Gebot abgeben
│   └── end_auction.py          # Command: Auktion beenden
├── events/
│   ├── auction_created.py      # Event: Auktion wurde erstellt
│   ├── bid_placed.py           # Event: Gebot wurde abgegeben
│   ├── bid_beaten.py           # Event: Gebot wurde überboten
│   └── auction_ended.py        # Event: Auktion ist beendet
├── handlers/
│   ├── auction_handler.py      # PRODUCER: verarbeitet Commands, publiziert Events
│   ├── notification_handler.py # CONSUMER 1: gibt Meldungen im Terminal aus
│   └── audit_log_handler.py    # CONSUMER 2: schreibt alle Events in auction_audit.log
└── queries/
    └── auction_queries.py      # Nur lesende Abfragen (CQRS Query-Seite)
main.py                         # Demo-Programm
```

---


## Ausführen

```bash
python main.py
```

Benötigt Python 3.10+, keine externen Bibliotheken.
