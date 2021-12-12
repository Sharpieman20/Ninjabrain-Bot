package bot.ninjabrain.calculator;

import java.util.Locale;

import bot.Main;
import bot.ninjabrain.io.NinjabrainBotPreferences;

public class TriangulationResult extends Chunk {

	public final int fourfour_x, fourfour_z;
	public final boolean success;
	public final int distance;
	public final boolean isBlindTravelResult;
	public final int divineTravelResult;
	private Throw playerPos;

	/**
	 * Creates a failed triangulation result.
	 */
	public TriangulationResult() {
		this(new Chunk(0, 0), null);
	}

	public TriangulationResult(Chunk chunk, Throw playerPos) {
		this(chunk, playerPos, false);
	}

	public TriangulationResult(Chunk chunk, Throw playerPos, boolean isBlindTravelResult) {
		this(chunk, playerPos, false, -1);
	}
	
	/**
	 * Creates a triangulation result.
	 */
	public TriangulationResult(Chunk chunk, Throw playerPos, boolean isBlindTravelResult, int divineTravelValue) {
		super(chunk.x, chunk.z, chunk.weight);
		this.fourfour_x = 16 * chunk.x + 4;
		this.fourfour_z = 16 * chunk.z + 4;
		this.success = Double.isFinite(chunk.weight) && chunk.weight > 0.0005;
		this.playerPos = playerPos;
		this.isBlindTravelResult = isBlindTravelResult;
		this.divineTravelResult = divineTravelValue;
		distance = playerPos != null ? getDistance(playerPos) : 0;
	}
	

	
	public String format() {
		if (divineTravelResult != -1) {
//			double angle = Calibrator.getAlpha(this, playerPos.x, playerPos.z);
			return String.format(Locale.US, "Divine is %d", divineTravelResult);
		}
		if (isBlindTravelResult) {
			double angle = Calibrator.getAlpha(this, playerPos.x, playerPos.z);
			return String.format(Locale.US, "Location: (%d, %d), %d angle", x, z, (int) angle);
		}
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
