package bot.ninjabrain.calculator;

import java.util.ArrayList;

/**
 * A prior computed only close to a ray.
 */
public class RayApproximatedPrior implements IPrior {

	ArrayList<Chunk> chunks;
	
	public RayApproximatedPrior(Ray r) {
		this(r, 1.0 / 180.0 * Math.PI); // 1 degree tolerance
	}

	public RayApproximatedPrior(Ray r, double tolerance) {
		long t0 = System.currentTimeMillis();
		construct(r, tolerance);
		System.out.println("Time to construct prior: " + (System.currentTimeMillis() - t0)/1000f + " seconds.");
	}
	
	private void construct(Ray r, double tolerance) {
		double range = 5000.0 / 16;
		chunks = new ArrayList<Chunk>();
		double phi = r.alpha() / 180.0 * Math.PI;
		// direction vector
		double dx = -Math.sin(phi);
		double dz = Math.cos(phi);
		// boundary vectors
		double ux = -Math.sin(phi - tolerance);
		double uz = Math.cos(phi - tolerance);
		double vx = -Math.sin(phi + tolerance);
		double vz = Math.cos(phi + tolerance);
		// Is the major direction X or Z?
		boolean majorX = Math.cos(phi) * Math.cos(phi) < 0.5;
		boolean majorPositive = majorX ? -Math.sin(phi) > 0 : Math.cos(phi) > 0;
		// Subtract 0.5 to center grid at (8,8).
		double origin_major = (majorX ? r.x() : r.z()) / 16.0 - 0.5;
		double origin_minor = (majorX ? r.z() : r.x()) / 16.0 - 0.5;
		double iter_start_major = getIterStartMajor(origin_major, origin_minor, ux, uz, vx, vz, majorX, majorPositive);
		double uk = majorX ? uz / ux : ux / uz;
		double vk = majorX ? vz / vx : vx / vz;
		boolean rightPositive = majorPositive ? vk - uk > 0 : uk - vk > 0;
		int i = (int) (majorPositive ? Math.ceil(iter_start_major) : Math.floor(iter_start_major));
		while ((majorX ? (i - iter_start_major) / dx : (i - iter_start_major) / dz) < range) {
		//while (i - iter_start_major < range) {
			double minor_u = origin_minor + uk * (i - origin_major);
			double minor_v = origin_minor + vk * (i - origin_major);
			int j = (int) (rightPositive ? Math.ceil(minor_u) : Math.floor(minor_u));
			if (j < -StrongholdConstants.maxChunk)
				j = -StrongholdConstants.maxChunk;
			if (j > StrongholdConstants.maxChunk)
				j = StrongholdConstants.maxChunk;
			while (rightPositive ? j < minor_v : j > minor_v && j <= StrongholdConstants.maxChunk && j >= -StrongholdConstants.maxChunk) {
				Chunk chunk = majorX ? new Chunk(i, j) : new Chunk(j, i);
				chunk.weight = ApproximatedDensity.density(chunk.x, chunk.z);
				chunks.add(chunk);
				j += rightPositive ? 1 : -1;
			}
			i += majorPositive ? 1 : -1;
		}
	}
	
	/**
	 * Returns the major coord at which to start looking for chunks to add to the
	 * prior. If the player is outside the 8th ring this is necessary because the
	 * nearest stronghold might be very far away.
	 */
	private double getIterStartMajor(double o_major, double o_minor, double ux, double uz, double vx, double vz, boolean majorX, boolean majorPositive) {
		if (o_major * o_major + o_minor * o_minor <= StrongholdConstants.maxChunk * StrongholdConstants.maxChunk) // in 8 rings
			return o_major;
		double ox = majorX ? o_major : o_minor;
		double oz = majorX ? o_minor : o_major;
		// Determine if (0,0) is in the frustum
		double u_orth_mag = orthogonalComponent(-ox, -oz, ux, uz);
		double v_orth_mag = orthogonalComponent(-ox, -oz, vx, vz);
		// (0,0) is in the frustum if it is to the right of u and to the left of v
		if (u_orth_mag > 0 && v_orth_mag < 0) {
			// intersection
			double o_mag = Math.sqrt(ox * ox + oz * oz);
			double ix = ox / o_mag * StrongholdConstants.maxChunk;
			double iz = oz / o_mag * StrongholdConstants.maxChunk;
			double m1 = o_major + projectAndGetMajorComponent(ix - ox, iz - oz, ux, uz, majorX);
			double m2 = o_major + projectAndGetMajorComponent(ix - ox, iz - oz, vx, vz, majorX);
			return majorPositive ^ m1 > m2 ? m1 : m2;
		}
		double i_u_major = findCircleIntersection(ox, oz, ux, uz, StrongholdConstants.maxChunk, majorX);
		double i_v_major = findCircleIntersection(ox, oz, vx, vz, StrongholdConstants.maxChunk, majorX);
		if (i_u_major != 0 || i_v_major != 0) {
			if (i_u_major != 0 && i_v_major != 0) {
				return majorPositive ^ i_u_major > i_v_major ? i_u_major : i_v_major;
			}
			if (i_u_major != 0)
				return i_u_major;
			return i_v_major;
		}
		return o_major;
	}

	/**
	 * Returns the magnitude of the vector pointing orthogonally from u to a (positive = right). u is a
	 * unit vector.
	 */
	private double orthogonalComponent(double ax, double az, double ux, double uz) {
		double u_par_mag = ux * ax + uz * az;
		double u_par_x = ux * u_par_mag;
		double u_par_z = uz * u_par_mag;
		double u_orth_x = u_par_x - ax;
		double u_orth_z = u_par_z - az;
		double u_orth_mag = uz * u_orth_x - ux * u_orth_z;
		return u_orth_mag;
	}
	
	/**
	 * Projects a onto the unit vector u and returns the major component.
	 */
	private double projectAndGetMajorComponent(double ax, double az, double ux, double uz, boolean majorX) {
		double proj_mag = ax * ux + az * uz;
		return majorX ? (ux * proj_mag) : (uz * proj_mag);
	}
	
	private double findCircleIntersection(double ox, double oz, double ux, double uz, double r, boolean majorX) {
		double o_dot_u = ox * ux + oz * uz;
		double a = o_dot_u * o_dot_u + r * r - ox * ox - oz * oz;
		if (a < 0) // no intersection
			return 0;
		double b = -o_dot_u - Math.sqrt(a);
		return majorX ? ox + b * ux : oz + b * uz;
	}

	@Override
	public Iterable<Chunk> getChunks() {
		return chunks;
	}

}
