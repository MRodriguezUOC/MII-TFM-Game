package edu.uoc.mii.platform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.uoc.mii.Main;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.conf.PreferenceManager;
import edu.uoc.mii.screen.LoadingScreen;
import edu.uoc.mii.screen.ScreenManager;
import edu.uoc.mii.screen.ScreenType;

/**
 * Base iniciada con gemini. Prompt: Para el juego retro, multiplataforma, con
 * libGDX que estoy haciendo, quiero hace una intro para el nivel 2, que muestre
 * unos segundos de un video que tengo en formato webm, luego se quede como
 * pausado en un fotograma, el sonido con un pitido, lluego que empiecen a hacer
 * efectos de interferencias y finalmente se quede la pantalla en negro ¿me
 * ayudas?
 *
 * @author Marco Rodriguez
 */
public class PlatformStart extends ScreenAdapter {

    private enum State {
        PLAYING_VIDEO,
        CD_SKIP,
        PAUSED_BEEP,
        GLITCHING,
        BLACK_SCREEN
    }

    private State currentState = State.PLAYING_VIDEO;

    private final Main game;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Viewport viewport;

    private TextureAtlas atlas;
    private Animation<TextureRegion> videoAnimation;
    private ShaderProgram glitchShader;
    private ShapeRenderer shapeRenderer;

    private Music videoMusic;
    private Sound stutterSound;
    private Sound beepSound;
    private Sound modemSound;

    private long stutterId = -1;
    private long modemId = -1;
    private long pauseId = -1;

    private final float VIDEO_DURATION = 11.0f;
    private final float SKIP_DURATION = 1.0f;
    private final float PAUSE_DURATION = 1.0f;
    private final float GLITCH_DURATION = 5.0f;

    private float stateTime = 0f;
    private float sequenceTimer = 0f;
    private float progress = 0f;
    private boolean initialiced = false;
    
    public PlatformStart(Main game) {
        this.game = game;

        batch = game.getBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(320, 200, camera); // Resolución retro

    }

    @Override
    public void show() {
        if (PreferenceManager.getInstance().skipPlatformIntro()) {
            ScreenManager.getInstance().setScreen(ScreenType.LEVEL_PLATFORM);
        } else {
            PreferenceManager.getInstance().setSkipPlatformIntro(true);
            shapeRenderer = new ShapeRenderer();
            game.assetManager.load(AssetPaths.NES_START_ATLAS);
            game.assetManager.load(AssetPaths.NES_START_CUT1_MUSIC);
            game.assetManager.load(AssetPaths.NET_START_SHUTTER);
            game.assetManager.load(AssetPaths.BEEP_CENSORSHIP);
            game.assetManager.load(AssetPaths.INTERNET_CONNECTION);
        }
    }

    private void init() {
        // 1. Load Animation
        atlas = game.assetManager.get(AssetPaths.NES_START_ATLAS);
        // 0.066f are 15 FPS aprox
        videoAnimation = new Animation<>(0.066f, atlas.findRegions("frames_nes_start"), Animation.PlayMode.NORMAL);

        // 2. Load Sound
        videoMusic = game.assetManager.get(AssetPaths.NES_START_CUT1_MUSIC);
        videoMusic.setLooping(false);

        stutterSound = game.assetManager.get(AssetPaths.NET_START_SHUTTER);
        beepSound = game.assetManager.get(AssetPaths.BEEP_CENSORSHIP);
        modemSound = game.assetManager.get(AssetPaths.INTERNET_CONNECTION);

        // 3. Load Shader
        String vertexShader = SpriteBatch.createDefaultShader().getVertexShaderSource();
        String fragmentShader = Gdx.files.internal("level2/glitch.frag").readString();
        glitchShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!glitchShader.isCompiled()) {
            Gdx.app.error("Shader", glitchShader.getLog());
        }

        videoMusic.play();
        initialiced = true;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if (!game.assetManager.update()) {
            float targetProgress = game.assetManager.getProgress();
            progress = MathUtils.lerp(progress, targetProgress, 0.1f);
            System.out.println("Cargando... " + (progress * 100) + "%");
            LoadingScreen.renderProgressBar(batch, shapeRenderer, progress);
            return;
        }
        
        if(!initialiced){
            init();
        }

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        updateSequence(delta);

        if (currentState == State.BLACK_SCREEN) {
            return;
        }

        batch.begin();
        if (currentState == State.GLITCHING) {
            batch.setShader(glitchShader);
            glitchShader.setUniformf("u_time", stateTime);
            glitchShader.setUniformf("u_active", 1.0f);
        } else {
            batch.setShader(null);
        }

        // Get actual frame. 
        // If we are paused or in glitch, we use the last frame of the video (VIDEO_DURATION)
        float frameTime;
        if(currentState == State.PLAYING_VIDEO){
            frameTime = stateTime;

        }else{
            frameTime = VIDEO_DURATION - MathUtils.random(0f, 0.5f);
        }
        TextureRegion currentFrame = videoAnimation.getKeyFrame(frameTime);

        batch.draw(currentFrame, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        batch.end();
    }

    private void updateSequence(float delta) {
        stateTime += delta;
        sequenceTimer += delta;

        switch (currentState) {
            case PLAYING_VIDEO:
                if (stateTime >= VIDEO_DURATION) {
                    changeState(State.CD_SKIP);
                }
                break;
            case CD_SKIP:
                if (sequenceTimer >= SKIP_DURATION) {
                    changeState(State.PAUSED_BEEP);
                }
                break;
            case PAUSED_BEEP:
                if (sequenceTimer >= PAUSE_DURATION) {
                    changeState(State.GLITCHING);
                }
                break;
            case GLITCHING:
                if (sequenceTimer >= GLITCH_DURATION) {
                    changeState(State.BLACK_SCREEN);
                }
                break;
            case BLACK_SCREEN:
                ScreenManager.getInstance().setScreen(ScreenType.PLATFORM_INTRO);
                break;
        }
    }

    private void changeState(State newState) {
        currentState = newState;
        sequenceTimer = 0f;

        switch (newState) {
            case CD_SKIP:
                videoMusic.stop();
                stutterId = stutterSound.loop();
                // stutterSound.setPitch(stutterId, 1.2f); 
                break;
            case PAUSED_BEEP:
                stutterSound.stop(stutterId);
                pauseId = beepSound.play(1.0f);
                break;
            case GLITCHING:
                modemId = modemSound.loop(1.0f);
                beepSound.stop(pauseId);
                break;
            case BLACK_SCREEN:
                modemSound.stop(modemId);
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (glitchShader != null) {
            glitchShader.dispose();
        }
        if(shapeRenderer != null){
            shapeRenderer.dispose();
        }
    }
}
