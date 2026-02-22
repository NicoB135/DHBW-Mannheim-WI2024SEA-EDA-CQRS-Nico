package dhbw.audiostreamer.event;

import java.io.Serializable;
import java.time.Instant;

public record VolumeMeasuredEvent(double decibel, Instant measuredAt) implements Serializable {

}
