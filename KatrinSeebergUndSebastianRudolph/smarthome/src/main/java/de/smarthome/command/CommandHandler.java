package de.smarthome.command;

import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import de.smarthome.event.PersonArrivedEvent;
import de.smarthome.event.PersonLeftEvent;

@Service
public class CommandHandler {
    
    private RabbitTemplate rabbitTemplate;
    private String [] residents = {"Alice", "Bob", "Charlie"};

    public CommandHandler(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    //Checkt ob die Person ein Bewohner ist
    private boolean isResident(String person) {
        for (String resident : residents) {
            if (resident.equalsIgnoreCase(person)) {
                return true;
            }
        }
        return false;
    }

    // Handler-Methoden für die Commands
    public void handlePersonArrivedCommand(PersonArrivedCommand command) {
        if (isResident(command.getPerson())) {
            PersonArrivedEvent event = new PersonArrivedEvent(command.getPerson());
            rabbitTemplate.convertAndSend("smarthome-exchange", "person.arrived", event);
        } else {
            System.out.println("Unknown person arrived: " + command.getPerson());
        }
    }

    public void handlePersonLeftCommand(PersonLeftCommand command) {
        if (isResident(command.getPerson())) {
            PersonLeftEvent event = new PersonLeftEvent(command.getPerson());
            rabbitTemplate.convertAndSend("smarthome-exchange", "person.left", event);
        } else {
            System.out.println("Unknown person left: " + command.getPerson());
        }
    }

}
