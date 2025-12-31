package edu.uoc.mii.platform;

/**
 *
 * @author Marco Rodriguez
 */
public class JumpCorrection {

    public final float y;
    public final float dy;

    public static final JumpCorrection NONE = new JumpCorrection(0f, 0f);

    public JumpCorrection(float y, float dy) {
        this.y = y;
        this.dy = dy;
    }

    public boolean isNONE() {
        return y == 0 && dy == 0;
    }
    
    @Override
    public String toString(){
        return "y: " + y + ", dy: " + dy;
    }
}
