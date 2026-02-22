package smarthome.devices;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import smarthome.command.PresenceDetectedEvent;
import smarthome.config.RabbitConfig;

@Service
public class Actuators {

    @RabbitListener(queues = RabbitConfig.Q_EVENTS_ACTUATORS)
    public void onPresenceDetected(PresenceDetectedEvent event) {

        System.out.println("=================================");
        System.out.println("[LIGHT] ON  home=" + event.getHomeId());
        System.out.println("[HEAT]  ON  target=21°C home=" + event.getHomeId());
        System.out.println("=================================");
    }
}