package smarthome.query;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import smarthome.command.PresenceDetectedEvent;
import smarthome.config.RabbitConfig;

@Service
public class PresenceReadModelUpdater {

    private final HomeStatusReadModel readModel;

    public PresenceReadModelUpdater(HomeStatusReadModel readModel) {
        this.readModel = readModel;
    }

    @RabbitListener(queues = RabbitConfig.Q_EVENTS_QUERY)
    public void onPresenceDetected(PresenceDetectedEvent event) {

        HomeStatusView view = new HomeStatusView(
                event.getHomeId(),
                true,
                event.getPersonId(),
                event.getTs()
        );

        readModel.upsert(view);

        System.out.println("[READMODEL] updated home=" + event.getHomeId()
                + " lastPerson=" + event.getPersonId());
    }
}