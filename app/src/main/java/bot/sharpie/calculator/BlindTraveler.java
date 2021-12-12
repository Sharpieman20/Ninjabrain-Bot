package bot.sharpie.calculator;

import bot.ninjabrain.calculator.Chunk;
import bot.ninjabrain.calculator.Throw;
import bot.ninjabrain.calculator.TriangulationResult;
import bot.ninjabrain.calculator.Triangulator;

import java.util.ArrayList;

public class BlindTraveler extends Triangulator {

    public BlindTraveler() {

        super();
    }

    @Override
    public TriangulationResult triangulate(ArrayList<Throw> eyeThrows) {

        double targetX = eyeThrows.get(0).x();
        double targetZ = eyeThrows.get(0).z();

        double myDist = eyeThrows.get(0).magnitude();
        if (myDist < 190) {

            targetX *= (190 / myDist);
            targetZ *= (190 / myDist);
        } else if (myDist > 270) {

            targetX *= (270 / myDist);
            targetZ *= (270 / myDist);
        }

        Chunk predictedChunk = new Chunk((int)targetX, (int)targetZ);
        predictedChunk.weight = 1.0;

        return new TriangulationResult(predictedChunk, eyeThrows.get(eyeThrows.size() - 1), true);
    }
}
