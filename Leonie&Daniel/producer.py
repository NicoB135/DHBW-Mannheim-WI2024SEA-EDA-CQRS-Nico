# ============================================
# Lightbulb Control System
# Project by Daniel and Leonie
# ============================================

import pika

connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()
channel.queue_declare(queue='events')

COLORS = {
    'r': 'red',
    'g': 'green',
    'b': 'blue',
    'y': 'yellow',
    'w': 'white'
}

def display_menu():
    print("\n" + "="*50)
    print("Project from Daniel and Leonie: {\"Die Erleuchtung\"}")
    print("="*50)
    
    print("\n📋 Commands & Queries")
    print("-" * 50)
    
    print("\n🎨 Change Color:")
    for code, color in COLORS.items():
        print(f"  {code} = {color}")
    
    print("\n⚙️  Other Options:")
    print("  ?    = Show current color")
    print("  esc  = Exit program")
    print("-" * 50 + "\n")

def sendEvent():
    channel.basic_publish(exchange='',
                      routing_key='events',
                      body='Hello World!')
    print("Success")

if __name__ == "__main__":
    display_menu()

sendEvent()
connection.close()