package edu.uoc.mii.platform;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 *
 * @author Marco Rodriguez
 */
public class PushButtonRenderer extends ObjectRenderer{
    private final PushButton pushButton;

    public PushButtonRenderer(SpriteBatch batch, Animation<TextureRegion> anim, PushButton pushButton) {
        super(batch, anim, pushButton.position);
        this.pushButton = pushButton;
        loop = false;
        anim.setPlayMode(Animation.PlayMode.NORMAL);
    }

    @Override
    protected void update(float dt) {
        if(pushButton.isPushed){
            stateTime += dt;
        }else{
            stateTime = 0;
        }
    }
    
}
