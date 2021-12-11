package bot.ninjabrain.calculator;

import java.util.List;
import java.util.Objects;

public class Chunk {
	
	public final int x;
	public final int z;
	public double weight;
	
	public Chunk(int x, int z) {
		this.x = x;
		this.z = z;
		weight = 0.0;
	}
	
	public Chunk(int x, int z, double w) {
		this.x = x;
		this.z = z;
		weight = w;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, z);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (getClass() != obj.getClass())
			 return false;
		Chunk other = (Chunk) obj;
		return x == other.x && z == other.z;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + z + ", " + weight + ")";
	}
	
	@Override
	protected Chunk clone() {
		Chunk c = new Chunk(x, z);
		c.weight = this.weight;
		return c;
	}
	
	/**
	 * Returns the distance (number of blocks) to the predicted location from the given throw.
	 */
	public int getDistance(Throw t) {
		double deltax = 16 * x + 8 - t.x;
		double deltaz = 16 * z + 8 - t.z;
		return (int) Math.sqrt(deltax * deltax + deltaz * deltaz);
	}
	
	public double[] getAngleErrors(List<Throw> eyeThrows) {
		double[] errors = new double[eyeThrows.size()];
		for (int i = 0; i < errors.length; i++) {
			Throw t = eyeThrows.get(i);
			double deltax = x * 16 + 8 - t.x;
			double deltaz = z * 16 + 8 - t.z;
			double gamma = -180 / Math.PI * Math.atan2(deltax, deltaz);
			double delta = (t.alpha - gamma) % 360.0;
			if (delta < -180) delta += 360;
			if (delta > 180) delta -= 360;
			errors[i] = delta;
		}
		return errors;
	}
	
}
