package smarthome.query;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HomeStatusReadModel {

    private final ConcurrentHashMap<String, HomeStatusView> byHomeId = new ConcurrentHashMap<>();

    public void upsert(HomeStatusView view) {
        byHomeId.put(view.getHomeId(), view);
    }

    public Optional<HomeStatusView> findByHomeId(String homeId) {
        return Optional.ofNullable(byHomeId.get(homeId));
    }
}