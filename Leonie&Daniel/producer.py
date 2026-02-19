# ============================================
# Lightbulb Control System
# Project by Daniel and Leonie
# ============================================

import pika, re, json

connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()
channel.queue_declare(queue='events')

ANSI_PATTERN = re.compile(r'\x1B\[[0-?]*[ -/]*[@-~]')

def strip_ansi(text):
    return ANSI_PATTERN.sub('', text)

COLORS = {
    '\033[31m' + 'r' + '\033[0m': ('\033[31m' + 'red' + '\033[0m', (0,100,100)), #red
    '\033[32m' + 'g' + '\033[0m': ('\033[32m' + 'green' + '\033[0m', (120,100,100)), #green
    '\033[34m' + 'b' + '\033[0m': ('\033[34m' + 'blue' + '\033[0m', (240,100,100)), #blue
    '\033[33m' + 'y' + '\033[0m': ('\033[33m' + 'yellow' + '\033[0m', (60,100,100)), #yellow
    '\033[37m' + 'w' + '\033[0m': ('\033[37m' + 'white' + '\033[0m', (0,0,100)) #white
}

def display_menu():
    print("\n" + "="*50)
    print("Project from Daniel and Leonie: {\"Die Erleuchtung\"}")
    print("="*50)
    
    print("\n📋 Commands & Queries")
    print("-" * 50)
    
    print("\n🎨 Change Color:")
    for char, color in COLORS.items():
        print(f"  {char} = {color[0]}")
    
    print("\n⚙️  Other Options:")
    print("  ?    = Show current color")
    print("  Ctrl+C  = Exit program")
    print("-" * 50 + "\n")

def sendEvent(data):
    #message = json.dumps(event_data).encode()

    channel.basic_publish(
        exchange='',
        routing_key="events",
        body=data,
        properties=pika.BasicProperties(
            content_type='application/json',
            delivery_mode=2
        )
    )
    print("Data successfully sent")

def getHexColorCode(input_char):
    for color_char, color in COLORS.items():
        if (strip_ansi(color_char) == input_char):
            return color[1]
    print("Kein Command für diesen Input :(")
    return None

def create_data(color):
    data = {
        "color": color,
    }
    return json.dumps(data).encode()

def create_event():
    sendEvent(
        create_data(
            getHexColorCode(
                input("Input:"))))

if __name__ == "__main__":
    display_menu()
while(True):
    create_event()
connection.close()