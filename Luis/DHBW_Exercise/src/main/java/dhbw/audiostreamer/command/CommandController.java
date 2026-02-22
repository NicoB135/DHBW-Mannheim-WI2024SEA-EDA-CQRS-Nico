package dhbw.audiostreamer.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/commands")
public class CommandController {
	
	private static final Logger logger = LoggerFactory.getLogger(CommandController.class);
	private final AddVolumeCommandHandler commandHandler;
	
	public CommandController(AddVolumeCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }
	
	@PostMapping("/volume")
    public ResponseEntity<String> addVolume(@RequestBody AddVolumeCommand command) {
        try {
            commandHandler.handle(command);
            return ResponseEntity.ok("Messerte passen boss!");
        } catch (IllegalArgumentException e) {
            logger.warn("Messwerte nicht gut Boss :( {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
