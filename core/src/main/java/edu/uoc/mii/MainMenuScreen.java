package edu.uoc.mii;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;


/**
 * Base iniciada con gemini.
 * Prompt: Hola Gemini. Quiero crear un videojuego
 * utilizando libGDX (para que sea multiplataforma). Es de estética retro, por
 * lo que las pantallas tienen que ser pixelart, con paleta de colores limitada,
 * por ejemplo a 64 colores. Al entrar al juego, la primera pantalla tiene que
 * mostrar una imagen de fondo, un botón que diga "Jugar" y otro que diga
 * "Salir" ¿ Me ayudas a hacerlo?
 * 
 * @author Marco Rodriguez
*/
public class MainMenuScreen implements Screen {
    
    private final Game game;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;
   
public MainMenuScreen(final Game game) {
        this.game = game;

        /* 1. Create the Viewport and the Stage
              The Stage knows where the actors are.
              The viewport is what the player sees.
              A fitViewport maintains the aspect ratio.
        */
        stage = new Stage(new FitViewport(RetroScreen.VIRTUAL_WIDTH, RetroScreen.VIRTUAL_HEIGHT));

        // 2. Load the Skin (which defines the style of the buttons)
        
        try {
            skin = new Skin(Gdx.files.internal("ui/ui.json"));
        } catch (Exception e) {
            Gdx.app.error("MainMenuScreen", "No se pudo cargar el skin", e);
            // Create a default skin if it fails so it doesn't crash
            skin = new Skin(); 
        }

        // 3. Load the background texture and apply the NEAREST filter
        backgroundTexture = new Texture(Gdx.files.internal("01-Portada.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        Image backgroundImage = new Image(backgroundTexture);

        // 4. Create the table that will organize everything
        Table mainTable = new Table();
        mainTable.setFillParent(true); 

        // 5. Add the background image to the table
        // .expand() and .fill() make the image fill the entire table space
        mainTable.add(backgroundImage).expand().fill();
        
        // 6. Create another table for the buttons, overlaid on the background.
        Table buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.center(); // Centra los botones

        // 7. Create the buttons using the Skin
        TextButton playButton = new TextButton("Jugar", skin);
        TextButton exitButton = new TextButton("Salir", skin);

        // 8. Add Listeners (actions) to the buttons
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("MainMenuScreen", "Boton 'Jugar' pulsado");
                game.setScreen(new PuzzleScreen(game,1));
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
        buttonTable.add(playButton).pad(10).width(100).height(30);
        buttonTable.row(); // Nueva fila
        buttonTable.add(exitButton).pad(10).width(100).height(30);

        // 10. Add the tables to the Stage
        stage.addActor(mainTable);  // The background (is drawn first)
        stage.addActor(buttonTable); // The buttons (are drawn on top)
    }    
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        //When returning from a level, if the screen size has been changed,
        //it displays incorrectly until it is resized.
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());        
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
        if(width <= 0 || height <= 0) return;

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
        dispose();
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        stage.dispose();
        skin.dispose();
        backgroundTexture.dispose();
    }
}