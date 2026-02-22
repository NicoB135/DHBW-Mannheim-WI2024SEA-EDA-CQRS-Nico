package smarthome.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smarthome.query.HomeStatusView;
import smarthome.query.HomeStatusReadModel;

@RestController
@RequestMapping("/homes")
public class QueryController {

    private final HomeStatusReadModel readModel;

    public QueryController(HomeStatusReadModel readModel) {
        this.readModel = readModel;
    }

    @GetMapping("/{homeId}/status")
    public ResponseEntity<HomeStatusView> status(@PathVariable("homeId") String homeId) {
        return readModel.findByHomeId(homeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}