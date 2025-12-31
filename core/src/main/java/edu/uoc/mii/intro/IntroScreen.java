package edu.uoc.mii.intro;

import edu.uoc.mii.utils.TypewriterAction;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import edu.uoc.mii.Main;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.audio.AudioManager;
import edu.uoc.mii.conf.GameConfig;
import edu.uoc.mii.conf.PreferenceManager;
import edu.uoc.mii.screen.ScreenManager;
import edu.uoc.mii.screen.ScreenType;

/**
 *
 * @author Marco Rodriguez
 */
public class IntroScreen implements Screen {

    private static final float TIME_PER_CHAR = 0.10f;
    private final Main game;
    private final Stage stage;
    private final ScreenType nextScreen;
    private final Image backgroundImage;
    private final Label narrativeText;
    private final String dataFile;
    private Skin skin;

    private Array<SlideData> slides;
    private Texture currentTexture;
    private Label.LabelStyle labelStyle;
    private Runnable onCharTyped;
    private boolean keySound;

    int currentIndex = -1;

    public IntroScreen(Main game, ScreenType nextScreen, String dataFile) {
        this(game, null, null, nextScreen, dataFile);
    }

    public IntroScreen(Main game, BitmapFont font, Color color, ScreenType nextScreen, String dataFile) {
        this(game, font, color, nextScreen, dataFile, null);
    }
    
    public IntroScreen(Main game, BitmapFont font, Color color, ScreenType nextScreen, String dataFile, boolean keySound) {
        this(game, font, color, nextScreen, dataFile, null);
        this.keySound = keySound;
    }
    
    public IntroScreen(Main game, BitmapFont font, Color color, ScreenType nextScreen, String dataFile, Runnable onCharTyped) {
        this.game = game;
        this.nextScreen = nextScreen;
        this.dataFile = dataFile;
        this.onCharTyped = onCharTyped;

        stage = new Stage(new FitViewport(GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT));

        backgroundImage = new Image();
        backgroundImage.setFillParent(true);

        if (font != null) {
            labelStyle = new Label.LabelStyle(font, color);
            narrativeText = new Label("", labelStyle);
        } else {
            skin = game.assetManager.get(AssetPaths.C64_SKIN);
            narrativeText = new Label("", skin, "optional");
        }

        narrativeText.setWrap(true);
        narrativeText.setAlignment(Align.center);
        narrativeText.setPosition(10, 10, Align.left);
        narrativeText.setWidth(GameConfig.VIRTUAL_WIDTH - 20);
        narrativeText.setHeight(GameConfig.VIRTUAL_HEIGHT - 20);

        stage.addActor(backgroundImage);
        stage.addActor(narrativeText);

        loadIntroData();
    }

    private void loadIntroData() {
        Gdx.app.log("IntroScreen", "loadIntroData");
        slides = new Array<>();

        FileHandle file = Gdx.files.internal(dataFile);
        String rawData = file.readString("UTF-8");

        String[] rawBlocks = rawData.split("---");

        for (String block : rawBlocks) {
            block = block.trim();
            if (block.isEmpty()) {
                continue;
            }
            int firstLineIndex = block.indexOf("\n");

            if (firstLineIndex != -1) {
                String blockData = block.substring(0, firstLineIndex).trim();
                String textBody = block.substring(firstLineIndex + 1).trim();
                SlideData slideData = new SlideData(blockData, textBody);
                slides.add(slideData);
                //TODO: preload data here?
            } else {
                Gdx.app.error("IntroScreen", "Formato incorrecto en bloque: " + block);
            }
        }
    }

    private void showNextSlide() {
        currentIndex++;
        Gdx.app.log("IntroScreen", "showNextSlide: " + currentIndex);
        if (currentIndex >= slides.size) {
            narrativeText.clearActions();
            endIntro();
            return;
        }

        SlideData data = slides.get(currentIndex);

        if (data.haveMusic) {
            try {
                AssetDescriptor<Music> descriptor = data.getMusicDescriptor();
                game.assetManager.load(descriptor);
                game.assetManager.finishLoading();
                AudioManager.getInstance().playMusic(descriptor, true);
            } catch (Exception e) {
                Gdx.app.error("IntroScreen", "Error al reproducir la música: " + data.musicName);
                Gdx.app.error("Exception", e.toString());
                return;
            }
        }

        if (data.haveImage) {
            try {
                AssetDescriptor<Texture> descriptor = data.getImageDescriptor();
                game.assetManager.load(descriptor);
                game.assetManager.finishLoading();
                currentTexture = game.assetManager.get(descriptor);
                backgroundImage.setDrawable(new TextureRegionDrawable(new TextureRegion(currentTexture)));
                backgroundImage.getColor().a = 1f;
            } catch (Exception e) {
                Gdx.app.error("IntroScreen", "No se encontró la imagen: " + data.imageName);
                Gdx.app.error("Exception", e.toString());
                return;
            }
        }

        narrativeText.setText("");
        narrativeText.setColor(Color.WHITE);
        narrativeText.getColor().a = 1f;
        narrativeText.clearActions();
        
        TypewriterAction tAction;
        if(keySound){
            tAction = new TypewriterAction(data.text, TIME_PER_CHAR, keySound);
        }else{
            tAction = new TypewriterAction(data.text, TIME_PER_CHAR, onCharTyped);
        }

        narrativeText.addAction(Actions.sequence(
                Actions.delay(0.5f),
                tAction,
                Actions.delay(3f),
                Actions.fadeOut(0.5f),
                Actions.run(() -> {
                    showNextSlide();
                })
        ));
    }

    private void skipNext() {
        Gdx.app.log("IntroScreen", "skipNext: " + currentIndex);
        narrativeText.clearActions();
        backgroundImage.getColor().a = 0f;
        narrativeText.getColor().a = 0f;

        showNextSlide();
    }

    private void endIntro() {
        PreferenceManager.getInstance().setSkipIntro(true);
        Gdx.app.postRunnable(() -> {
            ScreenManager.getInstance().setScreen(nextScreen);
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        if (game.assetManager.update()) {
            stage.act(delta);
            stage.draw();
        } else {
            float progress = game.assetManager.getProgress();
            System.out.println("Cargando... " + (progress * 100) + "%");
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                skipNext();
                return super.touchUp(screenX, screenY, pointer, button);
            }

            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.ESCAPE:
                        endIntro();
                        break;
                    case Input.Keys.N:
                    case Input.Keys.S:
                        skipNext();
                        break;
                    default:
                        Gdx.app.debug("IntroScreen", "Keycode: " + keycode);
                }
                return true;
            }
        });
        showNextSlide();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        if(skin != null){
            skin.dispose();
        }
        currentTexture.dispose();
    }

}
