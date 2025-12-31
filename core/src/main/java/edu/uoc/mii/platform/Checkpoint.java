package edu.uoc.mii.platform;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author Marco Rodriguez
 */
public class Checkpoint {

    public final Vector2 position;
    public final Rectangle colisionBounds;
    public State state = State.NO_CHECK;
    public float openTime = 1f;
    private float transcurredTime = 0f;

    public enum State{
        NO_CHECK,
        CHECKED,
        OPENED
    }
    
    public Checkpoint(Vector2 pos, Rectangle col) {
        position = new Vector2(pos);
        colisionBounds = new Rectangle(col);
    }

    public void update(float dt){
        if(state == State.CHECKED){
            transcurredTime += dt;
            if(transcurredTime > openTime){
                state = State.OPENED;
            }
        }
    }
    
    public void checked(){
        if(state == State.NO_CHECK){
            state = State.CHECKED;
        }
    }
    
    public void unChecked(){
        state = State.NO_CHECK;
        transcurredTime = 0f;
    }
}
