# DHBW-Mannheim-WI2024SEA-EDA-CQRS

1. Fork anlegen
2. Eigenen Ordner anlegen
3. Aufgabe 3+4 bearbeiten (10 Punkte)

# README – Steuerung der TP-Link Tapo L530E über Python

Dieses Projekt ermöglicht das Steuern einer TP‑Link Tapo L530E Smart‑Glühbirne über Python mithilfe eines Producer-Consumer-Systems.

---

## Voraussetzungen

- TP-Link Tapo L530E Smart-Glühbirne
- Smartphone mit Tapo App (Android oder iOS)
- Laptop/PC mit Python 3.9 oder höher
- Alle Geräte (Laptop, Glühbirne, Smartphone) müssen im selben WLAN sein
- Installation aller Pakete aus requirements.txt
- [Installation von RabbitMQ](https://www.rabbitmq.com/docs/download) und [Erlang OTP](https://www.erlang.org/downloads)
- vor Start von consumer.py und producer.py RabbitMQ server lokal starten

---

## Installation der Python-Abhängigkeiten

Vor dem Start müssen alle benötigten Pakete installiert werden:

```bash
pip install -r requirements.txt
``



1. Tapo App vorbereiten

Lade die Tapo App auf dein Smartphone.
Erstelle ein Konto oder melde dich an.

2. Glühbirne koppeln

Schließe die Tapo L530E Glühbirne an eine Steckdose oder Lampenfassung an.
Schalte den physischen Ein-/Ausschalter fünfmal hintereinander an und aus.
Die Glühbirne beginnt schnell zu blinken → Pairing-Modus aktiv.

3. Glühbirne in der App hinzufügen

Öffne die Tapo App.
Wähle „Gerät hinzufügen“ → „Glühbirne“ → „Tapo L530E“.
Folge dem Einrichtungsprozess, bis das Gerät verbunden ist.

4. IP-Adresse der Glühbirne ermitteln

Öffne das Gerät in der Tapo App.
Gehe zu den Geräteeinstellungen.
Notiere die dort angezeigte IP-Adresse der Glühbirne.

5. In Consumer.py die Anmeldedaten und Ip Adresse eintragen.
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
