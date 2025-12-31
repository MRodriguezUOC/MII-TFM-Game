package edu.uoc.mii;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.audio.AudioManager;
import edu.uoc.mii.conf.GameConfig;
import edu.uoc.mii.screen.ScreenManager;
import edu.uoc.mii.screen.ScreenType;
import edu.uoc.mii.user.LoginDialog;
import edu.uoc.mii.user.LoginManager;

/**
 * Base iniciada con gemini. Prompt: Hola Gemini. Quiero crear un videojuego
 * utilizando libGDX (para que sea multiplataforma). Es de estética retro, por
 * lo que las pantallas tienen que ser pixelart, con paleta de colores limitada,
 * por ejemplo a 64 colores. Al entrar al juego, la primera pantalla tiene que
 * mostrar una imagen de fondo, un botón que diga "Jugar" y otro que diga
 * "Salir" ¿ Me ayudas a hacerlo?
 *
 * @author Marco Rodriguez
 */
public class MainMenuScreen implements Screen {

    private final Main game;
    private final AssetManager assetManager;
    private final Stage stage;
    private final Texture backgroundTexture;
    private Skin skin;
    private Skin c64Skin;

    private Table buttonTable = new Table();
    private final TextButton playButton;
    private final TextButton playL2Button;
    private final TextButton exitButton;
    private final TextButton topButton;
    private final TextButton loginButton;
    private final TextButton introButton;
    private final Label versionLabel;

    public MainMenuScreen(final Main game) {
        this.game = game;
        assetManager = game.assetManager;

        /* 1. Create the Viewport and the Stage
              The Stage knows where the actors are.
              The viewport is what the player sees.
              A fitViewport maintains the aspect ratio.
         */
        stage = new Stage(new FitViewport(GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT));

        // 2. Load the Skin (which defines the style of the buttons)
        try {
            skin = assetManager.get(AssetPaths.UI_SKIN);
        } catch (Exception e) {
            Gdx.app.error("MainMenuScreen", "No se pudo cargar el skin", e);
            // Create a default skin if it fails so it doesn't crash
            skin = new Skin();
        }

        // 3. Load the background texture and apply the NEAREST filter
        backgroundTexture = assetManager.get(AssetPaths.MAIN_MENU_BG);
        backgroundTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        Image backgroundImage = new Image(backgroundTexture);

        // 4. Create the table that will organize everything
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // 5. Add the background image to the table
        // .expand() and .fill() make the image fill the entire table space
        mainTable.add(backgroundImage).expand().fill();

        // 6. Create another table for the buttons, overlaid on the background.
        buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.center(); // Centra los botones

        // 7. Create the buttons using the Skin
        playButton = new TextButton("Jugar", skin);
        playL2Button = new TextButton("Nivel2", skin);
        exitButton = new TextButton("Salir", skin);
        topButton = new TextButton("Top", skin);
        introButton = new TextButton("Intro", skin);
        if (LoginManager.getInstance().isLoggedIn()) {
            loginButton = new TextButton("Logout", skin);
        } else {
            loginButton = new TextButton("Login", skin);
        }

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("MainMenuScreen", "Boton 'login' pulsado");
                if (!LoginManager.getInstance().isLoggedIn()) {

                    Runnable alTerminar = () -> {
                        Gdx.app.log("MainMenuScreen", "¡El usuario ya está dentro!");
                        loginButton.setText("Logout");
                        fillButtons();
                    };

                    c64Skin = assetManager.get(AssetPaths.C64_SKIN);
                    LoginDialog loginDialog = new LoginDialog(game, "Identificarse", c64Skin, alTerminar);
                    loginDialog.show(stage);

                } else {
                    LoginManager.getInstance().logout();
                    loginButton.setText("Login");
                    fillButtons();
                    Gdx.app.debug("MainMenuScreen", "Ya estabas logueado");
                }
            }
        });

        // 8. Add Listeners (actions) to the buttons
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("MainMenuScreen", "Boton 'Jugar' pulsado");
                GameManager.getInstance().startGame();
                Gdx.app.debug("MainMenuScreen", "setScreen");
            }
        });
        playL2Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("MainMenuScreen", "Boton 'Nivel2' pulsado");
                GameManager.getInstance().startGame(2);
                Gdx.app.debug("MainMenuScreen", "setScreen");
            }
        });

        topButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("MainMenuScreen", "Boton 'Top' pulsado");
                ScreenManager.getInstance().setScreen(ScreenType.LEADERBOARD);
                Gdx.app.debug("MainMenuScreen", "setScreen");
            }
        });

        introButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("MainMenuScreen", "Boton 'intro' pulsado");
                ScreenManager.getInstance().setScreen(ScreenType.INTRO);
                Gdx.app.debug("MainMenuScreen", "setScreen");
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("MainMenuScreen", "Botón 'Salir' pulsado");
                Gdx.app.exit();
            }
        });

        // 9. Add the buttons to the button table
        fillButtons();
        
        BitmapFont font = new BitmapFont();
        //font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        versionLabel = new Label(game.version, style);
        versionLabel.setFontScale(0.5f);

        Table bottomTable = new Table();
        bottomTable.setFillParent(true);
        bottomTable.bottom().right().add(versionLabel).pad(5);
        
        // 10. Add the tables to the Stage
        stage.addActor(mainTable);
        stage.addActor(bottomTable);
        stage.addActor(buttonTable);
    }

    private void fillButtons() {
        buttonTable.clear();

        buttonTable.add(playButton).pad(10).width(100).height(30);
        buttonTable.add(topButton).pad(10).width(50).height(30);
        buttonTable.row();
        buttonTable.add(loginButton).pad(10).width(100).height(30);
        buttonTable.add(introButton).pad(10).width(100).height(30);
        buttonTable.row();
        buttonTable.add(exitButton).pad(10).width(100).height(30);
        buttonTable.add(playL2Button).pad(10).width(100).height(30);
        buttonTable.pack();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        //When returning from a level, if the screen size has been changed,
        //it displays incorrectly until it is resized.
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        AudioManager.getInstance().clearPlaylist();
        for(AssetDescriptor ad: ScreenType.MAIN_MENU.getAssetsDescriptor()){
            if(ad.type.equals(Music.class)){
                AudioManager.getInstance().addPlaylist(ad);
            }
        }
        AudioManager.getInstance().startPlaylist(true);
    }

    @Override
    public void render(float delta) {
        // Draw screen. "delta" is the time since last render in seconds.
        // Clean screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update and draw the Stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if (width <= 0 || height <= 0) {
            return;
        }

        // Resize your screen here. The parameters represent the new window size.
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
        Gdx.input.setInputProcessor(null);
        // Dispose call from assetsManager
//        dispose();
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        stage.dispose();
        skin.dispose();
        backgroundTexture.dispose();
        if (c64Skin != null) {
            c64Skin.dispose();
        }
    }
}
