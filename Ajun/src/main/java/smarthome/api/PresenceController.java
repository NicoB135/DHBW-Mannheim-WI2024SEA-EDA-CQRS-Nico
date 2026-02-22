package smarthome.api;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;
import smarthome.command.ArriveHomeCommand;
import smarthome.config.RabbitConfig;

@RestController
public class PresenceController {

    private final RabbitTemplate rabbit;

    public PresenceController(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @PostMapping("/arrive")
    public void arrive(@RequestParam("homeId") String homeId,
                       @RequestParam("personId") String personId) {

        ArriveHomeCommand command = new ArriveHomeCommand(
                homeId,
                personId,
                System.currentTimeMillis()
        );

        rabbit.convertAndSend(RabbitConfig.EXCHANGE, "cmd.presence.arrive", command);

        System.out.println("[API] sent cmd.presence.arrive home=" + homeId + " person=" + personId);
    }
}