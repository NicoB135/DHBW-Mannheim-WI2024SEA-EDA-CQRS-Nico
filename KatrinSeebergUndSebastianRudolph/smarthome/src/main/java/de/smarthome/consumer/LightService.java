package de.smarthome.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import de.smarthome.event.PersonArrivedEvent;
import de.smarthome.event.PersonLeftEvent;

@Service
public class LightService {

    private boolean lightsOn = false;
    private int personsHome = 0;
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @RabbitListener(queues = "light-queue")
    public void handleMessage(Message message) throws Exception {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        String body = new String(message.getBody());

        if (routingKey.equals("person.arrived")) {
            PersonArrivedEvent event = objectMapper.readValue(body, PersonArrivedEvent.class);
            personsHome++;
            lightsOn = true;
            System.out.println("Lights turned on for " + event.getPerson());
        } else if (routingKey.equals("person.left")) {
            PersonLeftEvent event = objectMapper.readValue(body, PersonLeftEvent.class);
            personsHome--;
            if (personsHome <= 0) {
                personsHome = 0;
                lightsOn = false;
                System.out.println("Lights turned off - nobody's home.");
            } else {
                System.out.println(event.getPerson() + " left the house, light stays on");
            }
        }
    }
}