# Anleitung für dein neues Smarthome

1. Mit "docker compose up -d" Docker Container für RabbitMQ starten
2. Mit ".\mvnw.cmd clean spring-boot:run" im smarthome-Ordner das Programm starten
3. Probier, was in der Kommandozeile steht: 
- "[Name] arrives": jemand kommt an
- "[Name] leaves": jemand geht
    - Hier wird geschaut, ob es auch ein Bewohner des Hauses ist ;)
- "status": Hausstatus: Wer ist da? Licht an? Wie warm ist die Heizung?
- "exit": Programm verlassen
4. Docker-Container am Ende mit "docker compose down" stoppen

**Viel Spaß im Smarthome!**