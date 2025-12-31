package edu.uoc.mii.platform;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author Marco Rodriguez
 */
public class DrillEnemy extends Enemy implements Telegraph {
    public static final float MIN_PLAYER_DIST = 32 * 1.4142f * 2;
    public static final float MIN2_PLAYER_DIST = 32 * 1.4142f * 3;
    public static final float VELOCITY = 64f;
    
    private enum DrillState{
        REST,
        MOVING_AWAY,
        MOVING_IN,
        DRILLING_IN,
        DRILLING_OUT,
        PROTECTING,
        ESCAPE
    }
    
    private StateMachine<DrillEnemy, DrillEnemyStates> stateMachine;
    private DrillState drillState = DrillState.REST;
    public DrillEnemy scrumLeader = null;
    public Vector2 newPos;
    public Vector2 newVel;
    
    public DrillEnemy(PlatformController controller, Vector2 initialPosition) {
        super(controller, initialPosition, Type.DRILL);
        
        stateMachine = new DefaultStateMachine<>(this, DrillEnemyStates.DRILLING);
        MessageManager.getInstance().addListener(this, DrillMessage.MSG_SCRUM_ENTER);
        MessageManager.getInstance().addListener(this, DrillMessage.MSG_SCRUM_LEAVE);
        newPos = new Vector2(initialPosition);
        newVel = new Vector2(0,0);
    }
    
    @Override
    public void update(float dt){
        super.update(dt);
        stateMachine.update();

        if(position.x != newPos.x){
            position.x = MathUtils.lerp(position.x, newPos.x, lerpFactor * dt);
        }else{
            velocity.x = newVel.x = 0;
        }
        
        if(position.y != newPos.y){
            if(velocity.y == 0){
                velocity.y = VELOCITY * (position.y < newPos.y ? 1 : -1);
            }
            velocity.y = MathUtils.lerp(velocity.y, newVel.y, lerpFactor * dt);
            position.y += velocity.y * dt;
            if(velocity.y > 0){
                if(position.y >= newPos.y){
                    position.y = newPos.y;
                }
            }else{
                if(position.y <= newPos.y){
                    position.y = newPos.y;
                }
            }
        }else{
            velocity.y = newVel.y = 0;
        }
        positionUpdated();
    }
    
    @Override
    public boolean handleMessage(Telegram telegram) {
        return stateMachine.handleMessage(telegram);
    }    
    
    @Override
    public void dispose() {
        super.dispose();
        MessageManager.getInstance().removeListener(this, DrillMessage.MSG_SCRUM_ENTER);
        MessageManager.getInstance().removeListener(this, DrillMessage.MSG_SCRUM_LEAVE);
    }    

    public void drilling() {
        if(position.y != newPos.y){
            newPos.y = initialPosition.y;
        }
        if(velocity.x == 0){
            if(position.equals(initialPosition)){
                drillState = DrillState.DRILLING_IN;
                velocity.x = 1;
                stateTime = 0;
                newPos.x = initialPosition.x + 5;
            }else{
                newPos.set(initialPosition);
            }
        }
        
        switch (drillState) {
            case MOVING_AWAY:
                if(stateTime > 1){
                    drillState = DrillState.MOVING_IN;
                    velocity.x = 1;
                    stateTime = 0;
                    newPos.x = initialPosition.x;
                }
                break;
            case MOVING_IN:
                if(stateTime > 1){
                    drillState = DrillState.DRILLING_IN;
                    velocity.x = 1;
                    stateTime = 0;
                    newPos.x = initialPosition.x + 5;
                }
                break;
            case DRILLING_IN:
                if(stateTime > MathUtils.random(0.2f, 0.4f)){
                    drillState = DrillState.DRILLING_OUT;
                    velocity.x = -1;
                    stateTime = 0;
                    newPos.x = initialPosition.x - 2;
                }     
                break;
            case DRILLING_OUT:
                if(stateTime > MathUtils.random(0.2f, 0.4f)){
                    drillState = DrillState.DRILLING_IN;
                    velocity.x = 1;
                    stateTime = 0;
                    newPos.x = initialPosition.x + 5;
                }                  
                break;
            case ESCAPE:
            case PROTECTING:
                drillState = DrillState.DRILLING_IN;
                break;
            default:
        }
    }

    public void stopMove() {
        velocity.x = 0;
        velocity.y = 0;
        newPos.set(initialPosition);
    }

    public void resetTimer() {
        stateTime = 0;
    }

    public void updateTimer() {
    }

    public float getTimer() {
        return stateTime;
    }

    public void escape() {
        newPos.x = initialPosition.x;
        newPos.y = initialPosition.y + 32;
        newVel.y = VELOCITY;
        drillState = DrillState.ESCAPE;
    }
    
    public void protecting() {
        if(scrumLeader != null && drillState != DrillState.PROTECTING){
            float dist = Math.abs(scrumLeader.initialPosition.y - initialPosition.y);
            int pos = (int)dist / 64;
            float sign = (scrumLeader.initialPosition.y > initialPosition.y ? 1 : -1);
            
            float y = initialPosition.y + (((32 * pos) -16) * sign);
            newPos.y = y;
            newPos.x = initialPosition.x;
            newVel.y = VELOCITY * (scrumLeader.position.y > position.y ? 1 : -1);
            drillState = DrillState.PROTECTING;
        }
    }    
    
    public StateMachine<DrillEnemy, DrillEnemyStates> getStateMachine(){
        return stateMachine;
    }    
    
    public boolean isDrilling(){
        return drillState == DrillState.DRILLING_IN;
    }
    
    public boolean isProtecting(){
        return drillState == DrillState.PROTECTING || drillState == DrillState.ESCAPE;
    }
    
}
