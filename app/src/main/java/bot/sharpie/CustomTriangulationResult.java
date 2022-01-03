package bot.sharpie;

import bot.ninjabrain.calculator.Chunk;
import bot.ninjabrain.calculator.TriangulationResult;

import java.util.Locale;

public class CustomTriangulationResult extends TriangulationResult {

    public CustomTriangulationResult(Chunk chunk) {
        super(chunk, null);
    }

    public String format() {
        int xMove = Math.abs(x);
        String xDir = "East";
        if (x < 0) {
            xDir = "West";
        }
        int zMove = Math.abs(z);
        String zDir = "South";
        if (z < 0) {
            zDir = "North";
        }
        String xStr = xMove + " " + xDir;
        String zStr = zMove + " " + zDir;
        String result = xStr + ", " + zStr;
        if (xMove > zMove) {
            result = zStr + ", " + xStr;
        }
        return result;
    }
}
