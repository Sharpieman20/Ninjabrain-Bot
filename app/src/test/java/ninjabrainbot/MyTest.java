package ninjabrainbot;

import static org.junit.Assert.assertEquals;

import bot.ninjabrain.calculator.Throw;
import bot.ninjabrain.calculator.TriangulationResult;
import bot.ninjabrain.calculator.Triangulator;
import bot.sharpie.Point;
import bot.sharpie.Stronghold;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyTest {

    private void doTriangulation(List<Throw> throwList) {


    }

    private Stronghold wrapSimSituation(Point basePoint, double angle, double sigma) {

        Triangulator triangulator = new Triangulator(0.002);

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
    void setUp() throws Exception {

    }

    @Test
    void testMultiply() {


    }
}