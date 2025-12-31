package edu.uoc.mii.platform;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author Marco Rodriguez
 */
public class ActionPlayerHunt extends LeafTask<HunterEnemy> {

    @Override
    public Status execute() {
        
        playerHunt();

        return Status.SUCCEEDED;
    }

    @Override
    protected Task<HunterEnemy> copyTo(Task<HunterEnemy> task) {
        return task;
    }

    public void playerHunt() {
        HunterEnemy e = getObject();
        if(!e.isAlive){
            return;            
        }
        e.debugActualStatus = "HUNTING";
        Vector2 pPos = e.getController().getPlayer().getPosition();

        e.velocity.x = e.huntingVelocity;
        if (e.position.x > pPos.x) {
            e.velocity.x *= -1;
        }
    }

}
