package dhbw.audiostreamer.command;


import dhbw.audiostreamer.config.RabbitMQConfig;
import dhbw.audiostreamer.event.VolumeMeasuredEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AddVolumeCommandHandler {

	private static final Logger logger = LoggerFactory.getLogger(AddVolumeCommandHandler.class);
	
	private final RabbitTemplate rabbitTemplate;
	
	public AddVolumeCommandHandler(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
	
	public void handle(AddVolumeCommand command) {
		if (command.getDecibel() < 0) {
	        throw new IllegalArgumentException("Wie du hast negative dB? Das verdient einen Physikpreis!: " + command.getDecibel());
	    }
	    if (command.getDecibel() > 194) {
	        throw new IllegalArgumentException("Decibel maximal zu hoch, pass auf deine Ohren auf!: " + command.getDecibel());
	    }
        VolumeMeasuredEvent event = new VolumeMeasuredEvent(command.getDecibel(), Instant.now());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "audio.measured", event);
        logger.info("{}dB um {} angekommen", event.decibel(), event.measuredAt());
    }
	
}
