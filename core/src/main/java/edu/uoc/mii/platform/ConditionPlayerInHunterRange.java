package edu.uoc.mii.platform;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.Vector2;
import static edu.uoc.mii.platform.HunterEnemy.MIN_PLAYER_DIST;

/**
 *
 * @author Marco Rodriguez 
 */
public class ConditionPlayerInHunterRange extends LeafTask<HunterEnemy>{

    @Override
    public Status execute() {        
        if(isPlayerInRange()){
            return Status.SUCCEEDED;
        }
        
        return Status.FAILED;
    }

    @Override
    protected Task<HunterEnemy> copyTo(Task<HunterEnemy> task) {
        return task;
    }
    
    public boolean isPlayerInRange(){        
        HunterEnemy e = getObject();
        if(!e.isAlive){
            return false;            
        }
        Vector2 pPos = e.getController().getPlayer().getPosition();
        float dist = e.playerDistance();
        boolean sameHight = Math.abs(e.position.y - pPos.y) < 40;
        return dist < MIN_PLAYER_DIST && sameHight;
    }    
    
}
