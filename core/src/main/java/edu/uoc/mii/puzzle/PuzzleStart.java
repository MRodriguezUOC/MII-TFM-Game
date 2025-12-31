package edu.uoc.mii.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import edu.uoc.mii.Main;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.audio.AudioManager;
import edu.uoc.mii.conf.PreferenceManager;
import edu.uoc.mii.screen.LoadingScreen;
import edu.uoc.mii.screen.ScreenManager;
import edu.uoc.mii.screen.ScreenType;

/**
 *
 * @author Marco Rodriguez
 */
public class PuzzleStart extends ScreenAdapter {

    private final Main game;

    private SpriteBatch batch;
    private Texture img1, img2, img3;
    private ShapeRenderer shapeRenderer;

    float stateTime = 0f;
    float progress = 0f;
    boolean enabled = false;

    public PuzzleStart(Main game) {
        this.game = game;

        batch = game.getBatch();
    }

    @Override
    public void show() {
        if (PreferenceManager.getInstance().skipPuzzleIntro()) {
            ScreenManager.getInstance().setScreen(ScreenType.LEVEL_PUZZLE);
        } else {
            PreferenceManager.getInstance().setSkipPuzzleIntro(true);
            shapeRenderer = new ShapeRenderer();
            game.assetManager.load(AssetPaths.L1_GAME_INVFOR_1_BG);
            game.assetManager.load(AssetPaths.L1_ZX_Loading_BG);
            game.assetManager.load(AssetPaths.L1_GAME_INVFOR_2_BG);
            game.assetManager.load(AssetPaths.ZX_SPECTRUM_TAPE);
        }
    }

    private void init() {
        img1 = game.assetManager.get(AssetPaths.L1_GAME_INVFOR_1_BG);
        img2 = game.assetManager.get(AssetPaths.L1_ZX_Loading_BG);
        img3 = game.assetManager.get(AssetPaths.L1_GAME_INVFOR_2_BG);

        enabled = true;
    }

    // Base iniciada con gemini. Prompt: en libGDX quiero mostrar una imagen de 
    // fundo durante 2 segundos, luego cambiar la imagen y mostrarla durante 2 
    // segundos pero haciendo que se mueva de derecha a izquierda rápido como si 
    // hubiera un fallo, y luego volvera mostrar otra imagen.
    @Override
    public void render(float delta) {
//        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);        
        ScreenUtils.clear(0, 0, 0, 1);

        if (!game.assetManager.update()) {
            float targetProgress = game.assetManager.getProgress();
            progress = MathUtils.lerp(progress, targetProgress, 0.1f);
            System.out.println("Cargando... " + (progress * 100) + "%");
            LoadingScreen.renderProgressBar(batch, shapeRenderer, progress);
            return;
        }

        if (!enabled) {
            init();
        }

        if (enabled) {
            stateTime += Gdx.graphics.getDeltaTime();
            batch.begin();

            if (stateTime < 0.5f) {
                batch.draw(img1, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            } else if (stateTime < 2.5f) {
                AudioManager.getInstance().playOneSound(AssetPaths.ZX_SPECTRUM_TAPE);
                glitch(img2);
            } else if (stateTime < 3.0f) {
                batch.draw(img3, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            } else if (stateTime < 5.0f) {
                glitch(img2);
            } else {
                ScreenManager.getInstance().setScreen(ScreenType.PUZLE_INTRO);
            }

            batch.end();
        }
    }

    private void glitch(Texture img) {
        float localTime = stateTime - 2.0f;
        float speed = 2500f;

        // We calculate the X position. We use the modulo (%) so that if the 
        // image goes off-screen, it reappears on the right (fast loop).
        float xPos = Gdx.graphics.getWidth() - (localTime * speed) % Gdx.graphics.getWidth();
        // We added a random wobble in Y to make it look like a real glitch.
        float shakeY = MathUtils.random(-15, 15);

        batch.draw(img, xPos, shakeY, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // A second copy right behind to avoid black gaps if it's going very fast.
        batch.draw(img, xPos + Gdx.graphics.getWidth(), shakeY, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void dispose() {
//        batch.dispose();
        if(shapeRenderer != null){
            shapeRenderer.dispose();
        }
    }
}
