package dhbw.audiostreamer.query;

import dhbw.audiostreamer.config.RabbitMQConfig;
import dhbw.audiostreamer.event.VolumeMeasuredEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class VolumeStatsProjection {
	
	private static final Logger logger = LoggerFactory.getLogger(VolumeStatsProjection.class);
	
	private double maxDb = 0;
    private double totalDb = 0;
    private int count = 0;
    
  
	@RabbitListener(queues = RabbitMQConfig.QUEUE)
	public void onVolumeMeasured(VolumeMeasuredEvent event) {
		count ++;
		totalDb += event.decibel();
		
		if(event.decibel() > maxDb) {
			maxDb = event.decibel();
			logger.info("Neuer Peak (PEAK RAA) {}dB um {}",maxDb,event.measuredAt());
		}
	}
	
	public double getMaxDb() {
		return maxDb;
	}
	
	 public double getAverageDb() {
		 if (count == 0) return 0;
		 return totalDb / count;
	 }
	 public int getCount() {
		 return count;
	 }
}
