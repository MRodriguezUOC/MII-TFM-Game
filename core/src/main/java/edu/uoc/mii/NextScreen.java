package edu.uoc.mii;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.audio.AudioManager;
import edu.uoc.mii.conf.GameConfig;
import edu.uoc.mii.screen.ScreenType;

/**
 *
 * @author Marco Rodriguez
 */
public class NextScreen extends ScreenAdapter {

    private final Main game;

    Texture backgroundTexture;
    private final Stage stage;
    private Skin skin;
    private boolean enableButton;

    public NextScreen(Main game) {
        this.game = game;

        GameManager.getInstance().setListener(new GameEventLintener(){
            @Override
            public void onPlayerDead() {
            }

            @Override
            public void onLevelCompleted() {
            }

            @Override
            public void onEndGame(Runnable onSuccess) {
                onSuccess.run();
            }

            @Override
            public void onGameOver(Runnable onSuccess) {
                onSuccess.run();
            }
        });
        stage = new Stage(new FitViewport(GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT));

        try {
            skin = game.assetManager.get(AssetPaths.UI_SKIN);
        } catch (Exception e) {
            Gdx.app.error("MainMenuScreen", "No se pudo cargar el skin", e);
            skin = new Skin();
        }

        backgroundTexture = game.assetManager.get(AssetPaths.NEXTINTRO_BG);
        backgroundTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        Image backgroundImage = new Image(backgroundTexture);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.add(backgroundImage).expand().fill();

        Table buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.bottom();
        buttonTable.padBottom(10);

        TextButton exitButton = new TextButton("Salir", skin);

        enableButton = true;
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (enableButton) {
                    enableButton = false;
                    Gdx.app.debug("NextScreen", "Botón 'Salir' pulsado");
                    GameManager.getInstance().exitGame();
                }
            }
        });

        buttonTable.add(exitButton).pad(10).width(100).height(30);
        buttonTable.pack();
        stage.addActor(mainTable);
        stage.addActor(buttonTable);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        AudioManager.getInstance().clearPlaylist();
        for (AssetDescriptor ad : ScreenType.NEXT_INTRO.getAssetsDescriptor()) {
            if (ad.type.equals(Music.class)) {
                AudioManager.getInstance().addPlaylist(ad);
            }
        }
        AudioManager.getInstance().startPlaylist(false);
    }

    @Override
    public void render(float delta) {
//        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);        
        ScreenUtils.clear(0, 0, 0, 1);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        stage.dispose();
        skin.dispose();
    }
}
