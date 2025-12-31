package edu.uoc.mii.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import edu.uoc.mii.Main;

/**
 *
 * @author Marco Rodriguez
 */
public class LoadingScreen extends ScreenAdapter {

    private final SpriteBatch batch;
    private final AssetManager assetManager;
    private final ScreenType nextScreenType;
    private final ShapeRenderer shapeRenderer;

    private float progress = 0f;
    private final static float BAR_WIDTH = 200f;
    private final static float BAR_HEIGHT = 20f;

    public LoadingScreen(Main game, ScreenType nextScreenType) {
        this.batch = game.getBatch();
        this.assetManager = game.assetManager;
        this.nextScreenType = nextScreenType;
        this.shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (assetManager.update()) {
            goToNextScreen();
        } else {
            float targetProgress = assetManager.getProgress();
            progress = MathUtils.lerp(progress, targetProgress, 0.1f);
            System.out.println("Cargando... " + (progress * 100) + "%");
            renderProgressBar(batch, shapeRenderer, progress);
        }
    }
    
    /**
     * Base iniciada con gemini. Prompt: Para el LoadingScreen, como hago 
     * una progressbar sin utilizar ningúna skin.?
     */    
    public static void renderProgressBar(SpriteBatch batch, ShapeRenderer shapeRenderer, float progress){
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float x = (screenWidth - BAR_WIDTH) / 2;
        float y = (screenHeight - BAR_HEIGHT) / 2;
        
        // Important: SetProjectionMatrix to use screen coordinates
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix()); 
        // Note: If you use a camera in mainGame, use: 
        //shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);

        // A. Draw the background (Dark gray)
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(x, y, BAR_WIDTH, BAR_HEIGHT);

        // B. Draw the progress (Green or White)
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(x, y, BAR_WIDTH * progress, BAR_HEIGHT);

        shapeRenderer.end();
        
        // C. (Optional) Draw a white border around (Line)
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(x, y, BAR_WIDTH, BAR_HEIGHT);
        shapeRenderer.end();
    }

    private void goToNextScreen() {
        ScreenManager.getInstance().finishLoading(nextScreenType);
    }
}
