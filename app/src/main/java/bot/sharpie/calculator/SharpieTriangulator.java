package bot.sharpie.calculator;

import java.util.ArrayList;

import bot.ninjabrain.calculator.*;

public class SharpieTriangulator extends Triangulator {

    public SharpieTriangulator() {

        super();
    }

    @Override
    public TriangulationResult triangulate(ArrayList<Throw> eyeThrows) {
        Chunk predictedChunk = new Chunk(0, 0);
        return new TriangulationResult(predictedChunk, eyeThrows.get(eyeThrows.size() - 1));
    }
}
