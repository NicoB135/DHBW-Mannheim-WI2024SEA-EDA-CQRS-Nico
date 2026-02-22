package de.smarthome.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import de.smarthome.event.PersonArrivedEvent;
import de.smarthome.event.PersonLeftEvent;

@Service
public class HeatingService {

    private int temperature = 16;
    private int personsHome = 0;
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @RabbitListener(queues = "heating-queue")
    public void handleMessage(Message message) throws Exception {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        String body = new String(message.getBody());

        if (routingKey.equals("person.arrived")) {
            PersonArrivedEvent event = objectMapper.readValue(body, PersonArrivedEvent.class);
            personsHome++;
            temperature = 22;
            System.out.println("Temperature set to 22°C for " + event.getPerson());
        } else if (routingKey.equals("person.left")) {
            PersonLeftEvent event = objectMapper.readValue(body, PersonLeftEvent.class);
            personsHome--;
            if (personsHome <= 0) {
                personsHome = 0;
                temperature = 16;
                System.out.println("Temperature set to 16°C - nobody's home");
            } else {
                System.out.println(event.getPerson() + " left the house, temperature stays at 22°C");
            }
        }
    }
}