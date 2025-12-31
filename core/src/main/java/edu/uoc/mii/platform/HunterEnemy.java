package edu.uoc.mii.platform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author Marco Rodriguez
 */
public class HunterEnemy extends Enemy {

    public static final float MIN_PLAYER_DIST = 32 * 5;
    public final BehaviorTree<HunterEnemy> behaviorTree;
    private final PlatformMap map;
    public String debugActualStatus = "unknow";
    public boolean isDebugIA = false;
    private Vector2 newPos = new Vector2();

    float patrolVelocity = 50f;
    float huntingVelocity = 100f;

    public HunterEnemy(PlatformController controller, PlatformMap map, Vector2 initialPosition) {
        super(controller, initialPosition, Type.HUNTER);

        BehaviorTreeParser<HunterEnemy> parser = new BehaviorTreeParser<>(BehaviorTreeParser.DEBUG_LOW);
        behaviorTree = parser.parse(Gdx.files.internal("level2/hunter-ia.btree"), this);

        this.map = map;
    }

    public void update(float dt) {
        super.update(dt);

        behaviorTree.step();

        velocity.y += GRAVITY * dt;
        newPos.y = position.y + velocity.y * dt;
        
        JumpCorrection jc = map.checkFloorCollision(colisionBounds, velocity.y > 0);
        if (!jc.isNONE()) {
            isGrounded = velocity.y <= 0;
            isFalling = !isGrounded && velocity.y < 0;
            velocity.y = 0;
            newPos.y = jc.y;
        }

        newPos.x = position.x + velocity.x * dt;
        updatePosition(newPos);

        isFacinRight = velocity.x > 0;
    }

    public boolean isFloorInFront() {
        float x = isFacinRight ? (position.x + bounds.width) : (position.x - 5);

        return map.isCellBlocked(x, position.y - 1);
    }

    public boolean isWallInFron() {
        float x = isFacinRight ? (position.x + bounds.width + 2) : (position.x - 2);

        return map.isCellBlocked(x, position.y + bounds.height / 2);
    }

}
