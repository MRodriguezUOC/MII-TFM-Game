package edu.uoc.mii.platform;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author Marco Rodriguez
 */
public class ObjectRenderer {
    protected final SpriteBatch batch;
    protected final Animation<TextureRegion> anim;
    protected final Vector2 position;    
    private TextureRegion currentFrame;    
    protected float stateTime = 0f;
    public boolean loop = true;
    
    public ObjectRenderer(SpriteBatch batch, Animation<TextureRegion> anim, Vector2 position) {
        this.batch = batch;
        this.anim = anim;
        this.position = position;
    }                
    
    public void render(float dt){
        update(dt);
        currentFrame = getCurrentFrame();
        batch.draw(currentFrame, position.x, position.y);
    }
    
    protected void update(float dt){
        stateTime += dt;        
    }
    
    protected TextureRegion getCurrentFrame(){
        return anim.getKeyFrame(stateTime,loop);
    }
    
}
