package bot.sharpie;

public class Stronghold extends Point {

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
