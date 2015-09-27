package synergy.cs530.ccsu.mobileauthentication;

/**
 * Created by ramyarad on 9/24/2015.
 */
public class TapModel {

    private int X;
    private int Y;
    private long time;

    public TapModel() {
    }

    public TapModel(int x, int y, long time) {
        X = x;
        Y = y;
        this.time = time;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

