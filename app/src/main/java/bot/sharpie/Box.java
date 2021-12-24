package bot.sharpie;

import static bot.sharpie.Utils.convert;

public class Box extends Point {
    double angle;
    double width;

    public Box(double dist, double width, double angle) {

        super(dist * Math.cos(angle), dist * Math.sin(angle));
        this.angle = angle;
        this.width = width;
    }

    public Point getFrontSide() {

        Point front = null;

        for (int i = -1; i <= 1; i += 2) {

            for (int j = -1; j <= 1; j+=2) {

                double cornerX = x + i*width*0.5;
                double cornerZ = z + j*width*0.5;

                Point corner = new Point(cornerX, cornerZ);

                if (front == null || corner.distanceFromOrigin() < front.distanceFromOrigin()) {

                    front = corner;
                }
            }
        }

        return front;
    }

    public Point getLeftSide() {

        Point left = null;

        for (int i = -1; i <= 1; i += 2) {

            for (int j = -1; j <= 1; j+=2) {

                double cornerX = x + i*width*0.5;
                double cornerZ = z + j*width*0.5;

                Point corner = new Point(cornerX, cornerZ);

                Point base = new Point(0, 0);

                if (left == null || convert(base.angleTo(corner)) < convert(base.angleTo(left))) {

                    left = corner;
                }
            }
        }

        return left;
    }

    public Point getRightSide() {

        Point right = null;

        for (int i = -1; i <= 1; i += 2) {

            for (int j = -1; j <= 1; j+=2) {

                double cornerX = x + i*width*0.5;
                double cornerZ = z + j*width*0.5;

                Point corner = new Point(cornerX, cornerZ);

                Point base = new Point(0, 0);

                if (right == null || convert(base.angleTo(corner)) > convert(base.angleTo(right))) {

                    right = corner;
                }
            }
        }

        return right;
    }
}