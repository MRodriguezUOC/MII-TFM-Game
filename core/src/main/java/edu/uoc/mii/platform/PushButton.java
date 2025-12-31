package edu.uoc.mii.platform;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author Marco Rodriguez
 */
public class PushButton {

    public PushButton(Vector2 position) {
        this.position = new Vector2(position);
        isPushed = false;
    }
    Vector2 position;
    Rectangle collisionBounds;
    boolean isPushed;
}
