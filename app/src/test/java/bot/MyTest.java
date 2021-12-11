package bot;

import static org.junit.Assert.assertEquals;

import bot.ninjabrain.calculator.Throw;
import bot.ninjabrain.calculator.TriangulationResult;
import bot.ninjabrain.calculator.Triangulator;
import bot.ninjabrain.io.NinjabrainBotPreferences;
import bot.sharpie.Point;
import bot.sharpie.Stronghold;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.lang.*;
import java.security.*;

import bot.ninjabrain.calculator.Throw;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyTest {

    private void doTriangulation(List<Throw> throwList) {


    }


    static ArrayList<Stronghold> strongholds;


    static class Point {

        double x;
        double z;

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

    static class Stronghold extends Point {

        double weight = 1.0;

        public Stronghold(int x, int z) {

            super(x*16*1000+8000, z*16*1000+8000);

            // System.out.println(this.x + " " + this.z);

            // System.exit(0);
        }

        public Stronghold(double x, double z) {

            this((int) Math.round((x-8.0) / 16.0), (int) Math.round((z-8.0) / 16.0));
        }
    }


    public static double getChanceOfBeingNearest(Stronghold possible, Point base) {

        int loops = 1000;
        int count = 0;

        for (int i = 0; i < loops; i++) {

            ArrayList<Stronghold> posLocs = genRandomPosAlternatesForStronghold(possible);
            posLocs.add(possible);

            if (getClosestStrongholdInSet(base, posLocs).equals(possible)) {

                count++;
            }
        }

        // System.out.println(count);

        return ((double)count)/loops;
        // return 1.0;
    }

    public static ArrayList<Stronghold> genRandomPosAlternatesForStronghold(Stronghold stronghold) {

        Random random = new Random();
        random.setSeed(random.nextLong());

        // double angle = random.nextDouble() * Math.PI * 2.0;

        double d = Math.tan(stronghold.z/stronghold.x);

        // int o = (int)Math.round(Math.cos(d) * e);
        // int p = (int)Math.round(Math.sin(d) * e);


        int i = 32;
        int j = 128;
        int k = 3;
        int l = 0;
        int m = 0;

        d += Math.PI * 2 / ((double)k);
        strongholds = new ArrayList<Stronghold>();
        for (int n = 0; n < 3; ++n) {
            double e = (double)(4 * i + i * m * 6) + (random.nextDouble() - 0.5) * ((double)i * 2.5);
            int o = (int)Math.round(Math.cos(d) * e);
            int p = (int)Math.round(Math.sin(d) * e);
            strongholds.add(new Stronghold(o, p));
            // System.out.println(strongholds.get(n));
        }
        return strongholds;

    }

    public static void genStrongholds() {
        Random random = new Random();
        random.setSeed(random.nextLong());
        // double d = random.nextDouble() * Math.PI * 2.0;
        double d = random.nextDouble() * Math.PI * 2.0;
        int i = 32;
        int j = 128;
        int k = 3;
        int l = 0;
        int m = 0;
        strongholds = new ArrayList<Stronghold>();
        for (int n = 0; n < 3; ++n) {
            double e = (double)(4 * i + i * m * 6) + (random.nextDouble() - 0.5) * ((double)i * 2.5);
            int o = (int)Math.round(Math.cos(d) * e);
            int p = (int)Math.round(Math.sin(d) * e);
            strongholds.add(new Stronghold(o, p));
            // System.out.println(strongholds.get(n));
            d += Math.PI * 2 / ((double)k);
            // if (++l != k) continue;
            // l = 0;
            // k += 2 * k / (++m + 1);
            // k = Math.min(k, j - n);
            // d += random.nextDouble() * Math.PI * 2.0;
        }
    }


    // public static

    public static Stronghold getClosestStronghold(Point base) {

        return getClosestStrongholdInSet(base, strongholds);
    }


    public static Stronghold getClosestStrongholdInSet(Point base, ArrayList<Stronghold> strongholds) {

        Stronghold closest = null;
        double minDist = 999999.0;

        for (Stronghold stronghold : strongholds) {

            double dist = base.distanceTo(stronghold);

            if (dist < minDist) {

                minDist = dist;
                closest = stronghold;
            }
        }

        return closest;
    }




    public static Stronghold guessClosestStronghold(Point base, double angle) {

        Triangulator triangulator = new Triangulator(0.002);

        angle = Math.toDegrees(angle);

        angle = -90.0+angle;

        Throw myThrow = new Throw(base.x, base.z, angle);

        ArrayList<Throw> throwsList = new ArrayList<Throw>();

        throwsList.add(myThrow);

        TriangulationResult result = triangulator.triangulate(throwsList);

        Stronghold strongholdResult = new Stronghold(result.x, result.z);

        return strongholdResult;
    }


    @Test
    public void test_Sim() {

        Main.preferences = new NinjabrainBotPreferences();

        int trials = 10_000;

        long baseMod = 1000;

        int depth = 1;

        int loops = depth;
        // int loops = 1;

        long offset = 300l;

        double best = 0.0;

        long baseX = 0l;
        long baseZ = 0l;


        HashSet<Long> validMod = new HashSet<Long>();

        Random rand;

        try {

            rand = SecureRandom.getInstance("NativePRNGNonBlocking");
            // System.out.println("faster pls");
        } catch (NoSuchAlgorithmException nsae) {

            rand = new SecureRandom();
        }

        int wrong = 0;

        double wrongError = 0.0;

        ArrayList<Double> errors = new ArrayList<Double>();

        HashMap<Long, HashMap<Long, Double>> scores = new HashMap<Long, HashMap<Long, Double>>();

        double minDist = 1400.0;
        double maxDist = 3000.0;

        for (long x = 0; x < loops; x++) {

            for (long z = 0; z < loops; z++) {


                int count = 0;

                for (int i = 0; i < trials; i++) {

                    long xCoord = 0;

                    long zCoord = 0;

                    while (Math.sqrt(Math.pow(xCoord/1000.0, 2.0)+Math.pow(zCoord/1000.0, 2.0))<minDist||Math.sqrt(Math.pow(xCoord/1000.0, 2.0)+Math.pow(zCoord/1000.0, 2.0))>maxDist) {


                        xCoord = ((long)(x + rand.nextDouble()*2500)) * baseMod + offset;
                        xCoord = (xCoord / (16*baseMod)) * 16*baseMod;
                        xCoord += offset;

                        zCoord = ((long)(z + rand.nextDouble()*2500)) * baseMod + offset;
                        zCoord = (zCoord / (16*baseMod)) * 16*baseMod;
                        zCoord += offset;

                        if (rand.nextInt(2) == 0) {

                            xCoord *= -1;
                        }

                        if (rand.nextInt(2) == 0) {

                            zCoord *= -1;
                        }
                    }

                    Point base = new Point(xCoord + (baseX * baseMod) - (depth/2*baseMod), zCoord + (baseZ * baseMod) - (depth/2*baseMod));

                    Point distCheck = new Point(xCoord - 2*offset, zCoord - 2*offset);

                    genStrongholds();

                    Stronghold closest = getClosestStronghold(base);

                    double angle = base.angleTo(closest);

                    angle = Math.toDegrees(angle);

                    double tmpangle = -90.0+angle;

                    if (tmpangle > 180.0) {

                        tmpangle -= 360.0;
                    }

                    if (tmpangle < -180.0) {

                        tmpangle += 360.0;
                    }

                    double ang = Math.round((tmpangle)*100.0)/100.0;

                    String fmt = ""+ang;

                    angle = Math.round(angle*100.0)/100.0;

                    angle = Math.toRadians(angle);

                    Stronghold guess = guessClosestStronghold(base, angle);

                    double distAngle = distCheck.angleTo(closest);

                    distAngle = Math.toDegrees(distAngle);

                    distAngle = Math.round(distAngle*100.0)/100.0;

                    distAngle = Math.toRadians(distAngle);

                    Stronghold other = guessClosestStronghold(distCheck, distAngle);

                    Point point = base.difference(guess);

                    long guessX = Math.round((point.x / 16.0));
                    long guessZ = Math.round((point.z / 16.0));
                    if (guess.equals(closest)) {

                        count++;
                    } else {

                        wrong++;
                        wrongError += guess.distanceTo(closest);
                        errors.add(guess.distanceTo(closest));
                    }
                }

                if (!scores.containsKey(x)) {

                    scores.put(x, new HashMap<Long, Double>());
                }



                HashMap<Long, Double> sub = scores.get(x);

                sub.put(z, ((double)count)/trials);

                if (sub.get(z) > best) {

                    best = sub.get(z);
                }
            }
        }

        double avgError = wrongError / wrong;

        double totalSquaredError = 0.0;

        for (double error : errors) {

            totalSquaredError += Math.pow(error - avgError,2.0);
        }

        double sd = totalSquaredError / wrong;

        sd = Math.sqrt(sd);

        System.out.println(best);
        System.out.println(sd);
    }

    private Stronghold wrapSimSituation(Point basePoint, double angle, double sigma) {

        Triangulator triangulator = new Triangulator(0.008);

        angle = Math.toDegrees(angle);

        angle = -90.0+angle;

        Throw myThrow = new Throw(basePoint.x, basePoint.z, angle);

        ArrayList<Throw> throwsList = new ArrayList<Throw>();

        throwsList.add(myThrow);

        TriangulationResult result = triangulator.triangulate(throwsList);

        Stronghold strongholdResult = new Stronghold(result.x, result.z);

        return strongholdResult;
    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testMultiply() {

        System.out.println("good test");
    }
}