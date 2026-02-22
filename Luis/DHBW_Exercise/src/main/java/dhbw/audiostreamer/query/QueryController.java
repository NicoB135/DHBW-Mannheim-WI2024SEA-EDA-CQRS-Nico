package dhbw.audiostreamer.query;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queries")
public class QueryController {
	
	public record VolumeStats(double maxDb, double averageDb, int count) {}
	
	private final VolumeStatsProjection projection;
	
	public QueryController(VolumeStatsProjection projection) {
        this.projection = projection;
    }
	
	@GetMapping("/stats")
    public VolumeStats getStats() {
        return new VolumeStats(
            projection.getMaxDb(),
            projection.getAverageDb(),
            projection.getCount()
        );
    }
}
