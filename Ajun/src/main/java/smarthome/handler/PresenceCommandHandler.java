package smarthome.handler;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import smarthome.command.ArriveHomeCommand;
import smarthome.command.PresenceDetectedEvent;
import smarthome.config.RabbitConfig;

@Service
public class PresenceCommandHandler {

    private final RabbitTemplate rabbit;

    public PresenceCommandHandler(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @RabbitListener(queues = RabbitConfig.Q_COMMANDS)
    public void handleArrive(ArriveHomeCommand command) {

        System.out.println("[COMMAND] Person arrived: home="
                + command.getHomeId() + " person=" + command.getPersonId());

        PresenceDetectedEvent event = new PresenceDetectedEvent(
                command.getHomeId(),
                command.getPersonId(),
                System.currentTimeMillis()
        );

        System.out.println("[EVENT] Presence detected -> publish event");

        rabbit.convertAndSend(
                RabbitConfig.EXCHANGE,
                "evt.presence.detected",
                event
        );
    }
}