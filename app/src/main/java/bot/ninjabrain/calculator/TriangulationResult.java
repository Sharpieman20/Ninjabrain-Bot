package bot.ninjabrain.calculator;

import java.util.Locale;

import bot.Main;
import bot.ninjabrain.io.NinjabrainBotPreferences;

public class TriangulationResult extends Chunk {

	public final int fourfour_x, fourfour_z;
	public final boolean success;
	public final int distance;
	
	/**
	 * Creates a triangulation result.
	 */
	public TriangulationResult(Chunk chunk, Throw playerPos) {
		super(chunk.x, chunk.z, chunk.weight);
		this.fourfour_x = 16 * chunk.x + 4;
		this.fourfour_z = 16 * chunk.z + 4;
		this.success = Double.isFinite(chunk.weight) && chunk.weight > 0.0005;
		distance = playerPos != null ? getDistance(playerPos) : 0;
	}
	
	/**
	 * Creates a failed triangulation result.
	 */
	public TriangulationResult() {
		this(new Chunk(0, 0), null);
	}
	
	public String format() {
		switch (Main.preferences.strongholdDisplayType.get()) {
		case NinjabrainBotPreferences.FOURFOUR:
			return String.format(Locale.US, "Location: (%d, %d), %d blocks away ", fourfour_x, fourfour_z, distance);
		case NinjabrainBotPreferences.EIGHTEIGHT:
			return String.format(Locale.US, "Location: (%d, %d), %d blocks away ", fourfour_x + 4, fourfour_z + 4, distance);
		case NinjabrainBotPreferences.CHUNK:
			return String.format(Locale.US, "Chunk: (%d, %d), %d blocks away ", x, z, distance);
		}
		return String.format(Locale.US, "Chunk: (%d, %d), %d blocks away ", x, z, distance);
	}

}
