package bot.sharpie;

public class Point {

    public double x;
    public double z;

    public Point(long x, long z) {

        this.x = ((double)x) / 1000.0;
        this.z = ((double)z) / 1000.0;
    }

    public Point(double x, double z) {

        this.x = x;
        this.z = z;
    }

    public double distanceFromOrigin() {

        return this.distanceTo(new Point(0, 0));
    }

    public double distanceTo(Point other) {

        double xDiff = x - other.x;
        double zDiff = z - other.z;

        return Math.sqrt((xDiff*xDiff)+(zDiff*zDiff));
    }

    public double angleTo(Point other) {

        double xRel = other.x - x;
        double zRel = other.z - z;

        return Math.atan2(zRel, xRel);
    }

    public Point difference(Point other) {

        double xRel = other.x - x;
        double zRel = other.z - z;

        return new Point(xRel, zRel);
    }


    public int hashCode() {

        return Double.valueOf(x).hashCode() * Double.valueOf(z).hashCode();
    }

    public boolean equals(Object other) {

        if (!(other instanceof Point)) {

            return false;
        }

        Point otherPoint = (Point) other;

        if (Math.round(x*1000)!=Math.round(otherPoint.x*1000)) {
            return false;
        }

        if (Math.round(z*1000)!=Math.round(otherPoint.z*1000)) {
            return false;
        }

        return true;
    }


    public String toString() {

        return this.x + " " + this.z;
    }
}
