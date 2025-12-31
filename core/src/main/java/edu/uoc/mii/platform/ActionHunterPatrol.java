package edu.uoc.mii.platform;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author Marco Rodriguez
 */
public class ActionHunterPatrol extends LeafTask<HunterEnemy> {
    @Override
    public Status execute() {

        patrol();
        //If return running, on next step the tree is not reevaluated.
//        return Status.RUNNING;
        return Status.SUCCEEDED;
    }
    
    @Override protected Task<HunterEnemy> copyTo(Task<HunterEnemy> task) { 
        return task; 
    }
    
    public void patrol(){
        HunterEnemy e = getObject();
        if(!e.isAlive){
            return;            
        }        
        e.debugActualStatus = "PATROL";

        if (e.velocity.x == 0) e.velocity.x = e.patrolVelocity;

        boolean isFloor = e.isFloorInFront();
        boolean isWall = e.isWallInFron();

        if (!isFloor || isWall) {
            e.velocity.x *= -1; 
            Vector2 newPos = new Vector2(e.position.x + (e.velocity.x > 0 ? 1 : -1), e.position.y);
            e.updatePosition(newPos); 
        }
        
        float dir = Math.signum(e.velocity.x);
        e.velocity.x = dir * e.patrolVelocity;        
    }
}
