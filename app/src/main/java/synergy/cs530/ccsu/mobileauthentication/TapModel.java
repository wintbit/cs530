package synergy.cs530.ccsu.mobileauthentication;

/**
 * Created by ramyarad on 9/24/2015.
 */
public class TapModel {

    private float X;
    private float Y;
    private float timeDown;
    private float timeUp;

    public TapModel() {
    }


    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public float getTimeDown() {
        return timeDown;
    }

    public void setTimeDown(float timeDown) {
        this.timeDown = timeDown;
    }

    public float getTimeUp() {
        return timeUp;
    }

    public void setTimeUp(float timeUp) {
        this.timeUp = timeUp;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s", X, Y, timeDown, timeUp);
    }
}

