package edu.uoc.mii.platform;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 *
 * @author Marco Rodriguez
 */
public class AiDebugRenderer {

    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    
    public AiDebugRenderer() {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.YELLOW);
        font.getData().setScale(0.8f);
    }

    public void render(HunterEnemy enemy, OrthographicCamera camera, SpriteBatch batch) {
        batch.end();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.circle(enemy.position.x + enemy.bounds.width/2, enemy.position.y + enemy.bounds.height/2, HunterEnemy.MIN_PLAYER_DIST); 
        
        float origenX = enemy.isFacinRight ? (enemy.position.x + enemy.bounds.width) : enemy.position.x;
        float origenY = enemy.position.y;
        float destinoX = origenX; 
        float destinoY = origenY - 20; 
        
        if (enemy.isFloorInFront()) {
            shapeRenderer.setColor(Color.GREEN);
        } else {
            shapeRenderer.setColor(Color.RED);
        }
        shapeRenderer.line(origenX, origenY, destinoX, destinoY);
        shapeRenderer.end();

        batch.begin();
        String actualStatue = actualTaskName(enemy);
        font.draw(batch, actualStatue, enemy.position.x, enemy.position.y + enemy.bounds.height + 20);
    }

    private String actualTaskName(HunterEnemy enemy) {
        return enemy.debugActualStatus;
    }
    
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }
}
