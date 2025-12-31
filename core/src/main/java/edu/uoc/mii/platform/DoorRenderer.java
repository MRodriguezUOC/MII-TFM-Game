package edu.uoc.mii.platform;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 *
 * @author Marco Rodriguez
 */
public class DoorRenderer extends ObjectRenderer {

    private final Door door;
    private final float finalFrameTime;

    public DoorRenderer(SpriteBatch batch, Animation<TextureRegion> anim, Door door) {
        super(batch, anim, door.position);
        this.door = door;
        finalFrameTime = anim.getFrameDuration() * door.openIdx;
        loop = false;
        anim.setPlayMode(Animation.PlayMode.NORMAL);
    }

    @Override
    protected void update(float dt) {
        if(door.isOpen){
            stateTime += dt;
            if(stateTime > finalFrameTime){
                stateTime = finalFrameTime;
            }
        }else{
            stateTime = 0;
        }
    }

}
