package synergy.cs530.ccsu.mobileauthentication;

/**
 * Created by ramyarad on 9/24/2015.
 */
public class TapModel {

    private int X;
    private int Y;
    private long timeDown;
    private long timeUp;

    public TapModel() {
    }

    public TapModel(int x, int y, long time) {
        X = x;
        Y = y;
        this.timeDown = time;
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

    public long getTimeDown() {
        return timeDown;
    }

    public void setTimeDown(long timeDown) {
        this.timeDown = timeDown;
    }

    public long getTimeUp() {
        return timeUp;
    }

    public void setTimeUp(long timeUp) {
        this.timeUp = timeUp;
    }
}

