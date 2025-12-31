package edu.uoc.mii.platform;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.audio.AudioManager;

/**
 *
 * @author Marco Rodriguez
 */
public class Nanobot {

    public static float GRAVITY = -450f;
    public static float JUMP_FORCE = 250f;
    public static float SPEED = 10f;
    public static float MAX_SPEED = 150f;
    public static float STOP_SPEED = 0.9f;
    public static float WIDTH = 32f;
    public static float HEIGHT = 32f;
    public static float COLISION_CORRECTION_X = 6f;

    private final Vector2 position;
    private final PlatformMap map;
    public Vector2 velocity;
    public Rectangle bounds;
    public Rectangle colisionBounds;

    public boolean isFacinRight = true;
    public boolean isGrounded = false;
    public boolean isFalling = false;
    public boolean isRunning = false;
    public boolean slowFallingZone = false;


    public Nanobot(PlatformMap map, float x, float y) {
        this.map = map;
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        bounds = new Rectangle(x, y, WIDTH, HEIGHT);
        colisionBounds = new Rectangle(x + COLISION_CORRECTION_X, y, bounds.width - (COLISION_CORRECTION_X * 2), bounds.height - 1);
    }
    
    public void update(float deltaTime) {
        float gravity = isFalling && slowFallingZone ? Nanobot.GRAVITY + 350 : Nanobot.GRAVITY;
        updateVerticalVelocity(gravity * deltaTime);
        if (velocity.y < map.MAX_VELOCITY_Y) {
            setVerticalVelocity(map.MAX_VELOCITY_Y);
        }

        updateY(position.y + velocity.y * deltaTime);
        JumpCorrection jc = map.checkFloorCollision(colisionBounds, velocity.y > 0);
        if (!jc.isNONE()) {
            isGrounded = velocity.y <= 0;
            isFalling = !isGrounded && velocity.y < 0;
            if (jc.dy != 0) {
                if (jc.dy > 0) {
                    AudioManager.getInstance().playOneSound(AssetPaths.TRAMPOLINE_SOUND);
                }
                updateVerticalVelocity(jc.dy);
            } else {
                if (velocity.y == map.MAX_VELOCITY_Y) {
                    AudioManager.getInstance().playSound(AssetPaths.HURT_SOUND);
                }
                setVerticalVelocity(0);
                updateY(jc.y);
                slowFallingZone = false;
            }
        }

        if (velocity.x != 0) {
            updateX(position.x + velocity.x * deltaTime);
            float correction = map.checkWallCollision(colisionBounds, velocity.x > 0);
            if (correction != 0) {
                velocity.x = 0;
                updateOnColisionX(correction);
            }
        }
    }

    public String posVel2String() {
        StringBuilder sb = new StringBuilder(32);
        sb.append("x: ").append(position.x);
        sb.append(", y: ").append(position.y);
        sb.append(", vx: ").append(velocity.x);
        sb.append(", vy: ").append(velocity.y).append("\n");
        sb.append(", w: ").append(bounds.width);
        sb.append(", h: ").append(bounds.height);
        sb.append(", s: ").append(SPEED);
        sb.append(", jf: ").append(JUMP_FORCE);
        sb.append(", g: ").append(GRAVITY);
        sb.append(", gd: ").append(isGrounded);
        sb.append(", f: ").append(isFalling);

        return sb.toString();
    }

    public void updateX(float x) {
        position.x = x;
        bounds.x = x;
        colisionBounds.x = x + COLISION_CORRECTION_X;
    }

    public void updateOnColisionX(float correction) {
        updateX(correction - COLISION_CORRECTION_X);
    }

    public void updateY(float y) {
        position.y = bounds.y = colisionBounds.y = y;
    }

    public void updatePosition(Vector2 v) {
        updateX(v.x);
        updateY(v.y);
    }

    public final Vector2 getPosition() {
        return position;
    }

    public void setVelocity(float x, float y) {
        velocity.x = x;
        setVerticalVelocity(y);
    }

    public void updateVerticalVelocity(float dy) {
        setVerticalVelocity(velocity.y + dy);
    }

    public void setVerticalVelocity(float y) {
        velocity.y = y;
        //y == 0 when passing from + -> -
        //isGrounded = y == 0;
        isFalling = y < 0;
    }

}
