package de.smarthome.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import de.smarthome.event.PersonArrivedEvent;
import de.smarthome.event.PersonLeftEvent;
import de.smarthome.readmodel.HomeStatus;

@Service
public class QueryService {

    private HomeStatus status = new HomeStatus();
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @RabbitListener(queues = "query-queue")
    public void handleMessage(Message message) throws Exception {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        String body = new String(message.getBody());

        if (routingKey.equals("person.arrived")) {
            PersonArrivedEvent event = objectMapper.readValue(body, PersonArrivedEvent.class);
            status.getPresentPersons().add(event.getPerson());
            status.setLightsOn(true);
            status.setTemperature(22);
        } else if (routingKey.equals("person.left")) {
            PersonLeftEvent event = objectMapper.readValue(body, PersonLeftEvent.class);
            status.getPresentPersons().remove(event.getPerson());
            if (status.getPresentPersons().isEmpty()) {
                status.setLightsOn(false);
                status.setTemperature(16);
            }
        }
    }

    public HomeStatus getStatus() {
        return status;
    }
}