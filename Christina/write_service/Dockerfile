FROM python:3.9-slim

# Arbeitsverzeichnis im Container
WORKDIR /feeding-command-service

# Abhängigkeiten kopieren und installieren
RUN pip install --no-cache-dir flask pika flask-cors

# Den Code kopieren
COPY . .

# Den Service starten (Port 5000 ist Flask-Standard)
CMD ["python", "feeding-command-service.py"]