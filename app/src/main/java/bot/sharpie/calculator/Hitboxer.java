package bot.sharpie.calculator;

import bot.ninjabrain.calculator.Chunk;
import bot.ninjabrain.calculator.Throw;
import bot.ninjabrain.calculator.TriangulationResult;
import bot.ninjabrain.calculator.Triangulator;
import bot.sharpie.Box;
import bot.sharpie.Point;

import java.util.ArrayList;

import static bot.sharpie.Utils.convert;

public class Hitboxer extends Triangulator {

    Triangulator innerTriangulator;

    public Hitboxer() {

        super();
    }

    public static final double EYE_DIST = 12.0;
    public static final double EYE_BOX_WIDTH = 0.25;

    private static double getAdjustedAngle(Point base, double target) {

        double result = 0.0;
        double guess = target;

        int maxLoops = 100000;
        int loops = 0;

        while (loops < maxLoops && Math.abs(target - result) > 0.001) {
            guess += 0.2*(target - result)+(0.8*(Math.random()-0.5)*(target - result)*((double)loops)/maxLoops);

            if (guess < -180) {
                guess += 360.0;
            }
            if (guess > 180) {
                guess -= 360;
            }
            double angle = Math.toRadians(guess+90);

            Box box = new Box(EYE_DIST, EYE_BOX_WIDTH, angle);

            double angleToFront = base.angleTo(box.getFrontSide());


            double dispRealAngle = convert(angle);
            double dispViewedAngle = convert(angleToFront);

            result = dispViewedAngle;
            loops++;
        }

        return guess;
    }

    public void setTriangulator(Triangulator triangulator) {

        innerTriangulator = triangulator;
    }

    @Override
    public TriangulationResult triangulate(ArrayList<Throw> eyeThrows) {

        if (eyeThrows.size() < 2) {

            return innerTriangulator.triangulate(eyeThrows);
        }

        Point base = new Point(eyeThrows.get(0).x, eyeThrows.get(0).z);
        Point measurePoint = new Point(eyeThrows.get(1).x, eyeThrows.get(1).z);
        double angle = eyeThrows.get(1).alpha;

        Point relativeMeasurePoint = new Point(measurePoint.x - base.x, measurePoint.z - base.z);


        double adjustedAngle = getAdjustedAngle(relativeMeasurePoint, angle);

        Throw adjustedThrow = new Throw(base.x, base.z, adjustedAngle);

        ArrayList<Throw> throwList = new ArrayList<Throw>();

        throwList.add(adjustedThrow);

        System.out.println("using adjusted throw" + adjustedThrow);

        return innerTriangulator.triangulate(throwList);
    }
}
