package edu.uoc.mii.platform;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author Marco Rodriguez
 */
public class Door {

    public Door(Vector2 position) {
        this.position = new Vector2(position);
        isOpen = false;
    }
    
    Vector2 position;
    Rectangle collisionBounds;
    boolean isOpen;
    //TODO: load from map
    int closeIds = 0;
    int openIdx = 4;    
}
