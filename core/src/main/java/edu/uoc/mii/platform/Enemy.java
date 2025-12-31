package edu.uoc.mii.platform;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

/**
 *
 * @author Marco Rodriguez
 */
public class Enemy implements Disposable {

    public static float GRAVITY = -450f;
    public final PlatformController controller;

    protected final Vector2 initialPosition;
    public final Vector2 position;
    public final Vector2 velocity;
    public final Rectangle bounds;
    public final Rectangle colisionBounds;
    protected float stateTime = 0.0f;
    protected float lerpFactor = 5f;

    public boolean isFacinRight = true;
    public boolean isGrounded = false;
    public boolean isFalling = false;
    public boolean isRunning = false;
    public boolean isAlive = true;

    public enum Type {
        DRILL {
            @Override
            public float getWidth() {
                return 32f;
            }

            @Override
            public float getHeight() {
                return 16f;
            }

            @Override
            public float getXCorrection() {
                return 0f;
            }

            @Override
            public float getYCorrection() {
                return 0f;
            }

            @Override
            public float getWidthCorrection() {
                return 0f;
            }

            @Override
            public float getHeighCorrection() {
                return 0f;
            }
        }, HUNTER {
            @Override
            public float getWidth() {
                return 32f;
            }

            @Override
            public float getHeight() {
                return 36f;
            }

            @Override
            public float getXCorrection() {
                return 2f;
            }

            @Override
            public float getYCorrection() {
                return 0f;
            }

            @Override
            public float getWidthCorrection() {
                return -4f;
            }

            @Override
            public float getHeighCorrection() {
                return -8f;
            }
        }, T3 {
            @Override
            public float getWidth() {
                return 48f;
            }

            @Override
            public float getHeight() {
                return 48f;
            }

            @Override
            public float getXCorrection() {
                return 8f;
            }

            @Override
            public float getYCorrection() {
                return 16f;
            }

            @Override
            public float getWidthCorrection() {
                return -16f;
            }

            @Override
            public float getHeighCorrection() {
                return -24f;
            }
        };

        public abstract float getWidth();

        public abstract float getHeight();

        public abstract float getXCorrection();

        public abstract float getYCorrection();

        public abstract float getWidthCorrection();

        public abstract float getHeighCorrection();
    }

    public final Type type;

    public Enemy(PlatformController controller, Vector2 initialPosition, Type type) {
        this.controller = controller;
        this.type = type;
        this.initialPosition = initialPosition;

        position = new Vector2(initialPosition);
        velocity = new Vector2(0, 0);
        bounds = new Rectangle(position.x, position.y, type.getWidth(), type.getHeight());
        colisionBounds = new Rectangle(position.x + type.getXCorrection(), position.y + type.getYCorrection(), type.getWidth() + type.getWidthCorrection(), type.getHeight() + type.getHeighCorrection());
    }

    public void updatePosition(Vector2 pos) {
        position.x = bounds.x = pos.x;
        position.y = bounds.y = pos.y;

        positionUpdated();
    }

    public void positionUpdated() {
        bounds.x = position.x;
        bounds.y = position.y;
        colisionBounds.x = position.x + type.getXCorrection();
        colisionBounds.y = position.y + type.getYCorrection();
    }

    public void update(float delta) {
        stateTime += delta;
    }
    
    public void died(){
        isAlive = false;
        updatePosition(initialPosition);
    }

    public float playerDistance() {
        return position.dst(controller.getPlayer().getPosition());
    }
    
    public PlatformController getController(){
        return controller;
    }

    public String posVel2String() {
        StringBuilder sb = new StringBuilder(32);
        sb.append("x: ").append(position.x);
        sb.append(", y: ").append(position.y);
        sb.append(", vx: ").append(velocity.x);
        sb.append(", vy: ").append(velocity.y).append("\n");
        sb.append(", w: ").append(bounds.width);
        sb.append(", h: ").append(bounds.height);
        sb.append(", gd: ").append(isGrounded);

        return sb.toString();
    }

    @Override
    public void dispose() {
    }

    public float getStateTime() {
        return stateTime;
    }
}
