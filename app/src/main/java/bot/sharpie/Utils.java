package bot.sharpie;

public class Utils {

    public static double convert(double angle) {

        double ingame = Math.toDegrees(angle)-90;

        if (ingame > 180) {
            ingame -= 360;
        }

        if (ingame < -180) {
            ingame += 360;
        }

        return ingame;
    }
}
