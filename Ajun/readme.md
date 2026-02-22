Um rabbitmq zu starten, in Ordner Ajun:<br>
docker compose up --build

--> läuft auf http://localhost:15672/<br>
username und passwort: guest



Befehle Windows:<br>
curl.exe -X POST "http://localhost:8080/arrive?homeId=home1&personId=alice"<br>
curl.exe -i "http://localhost:8080/homes/home1/status"

für Mac?<br>
curl -X POST "http://localhost:8080/arrive?homeId=home1&personId=alice" <br>
curl "http://localhost:8080/homes/home1/status"

----
<br>
wenn manuell starten:<br>
Maven benötigt<br>
mvn --version<br>
Rechtsklick pom.xml --> Add as maven project
