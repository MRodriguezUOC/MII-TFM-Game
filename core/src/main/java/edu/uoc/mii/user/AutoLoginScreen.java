package edu.uoc.mii.user;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import edu.uoc.mii.Main;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.conf.GameConfig;
import edu.uoc.mii.conf.PreferenceManager;
import edu.uoc.mii.screen.ScreenManager;
import edu.uoc.mii.screen.ScreenType;

/**
 *
 * @author Marco Rodriguez
 */
public class AutoLoginScreen implements Screen {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;
    private Dialog loginDialog;
    private Label messageLabel;

    public AutoLoginScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        skin = game.assetManager.get(AssetPaths.C64_SKIN);

        backgroundTexture = game.assetManager.get(AssetPaths.MAIN_MENU_BG);
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        loginDialog = new Dialog("Estado", skin) {
            @Override
            protected void result(Object object) {
                if (object instanceof Boolean && (Boolean) object) {
                    ScreenManager.getInstance().setScreen(ScreenType.MAIN_MENU);
                    //dispose();
                }
            }
        };
        messageLabel = new Label("Login...", skin);
        loginDialog.getContentTable().add(messageLabel).pad(15);
        loginDialog.show(stage);

        login();
    }

    private void login() {
        Gdx.app.log("AutoLoginScreen", "Login start");

        // HTML dont support threads.
        String username = PreferenceManager.getInstance().getUsername();
        String password = PreferenceManager.getInstance().getPassword();

        LoginManager.getInstance().login(username, password, new LoginManager.Callback() {
            @Override
            public void onSuccess() {
                Gdx.app.log("AutoLoginScreen", "Login OK");
                Gdx.app.postRunnable(() -> {
                    loginEnd("Hola " + username);
                });
            }

            @Override
            public void onError(String message) {
                Gdx.app.log("AutoLoginScreen", "Fallo al entrar: " + message);
                Gdx.app.postRunnable(() -> {
                    loginEnd("Error: " + message);
                });
                // TODO: launch user registry if not exists?
            }
        });

    }

    private void loginEnd(String msg) {
        messageLabel.setText(msg);
        loginDialog.button("Cerrar", true);
        loginDialog.pack();
        loginDialog.setPosition(
                (stage.getWidth() - loginDialog.getWidth()) / 2,
                (stage.getHeight() - loginDialog.getHeight()) / 2
        );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
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
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
