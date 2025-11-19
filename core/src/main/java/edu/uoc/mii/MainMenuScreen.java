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

/** First screen of the application. Displayed after the application is created. */
public class MainMenuScreen implements Screen {
    
    private final Game game;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;

    // Asumimos una resolución "virtual" retro, por ejemplo 320x180
    private static final int VIRTUAL_WIDTH = 320;
    private static final int VIRTUAL_HEIGHT = 320;    
    
public MainMenuScreen(final Game game) {
        this.game = game;

        // 1. Crear el Viewport y el Stage
        // FitViewport mantiene la relación de aspecto (ideal para pixel art)
        stage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));

        // 2. Cargar el Skin (que define el estilo de los botones)
        // Asegúrate de tener los archivos ui.json, ui.atlas y la fuente .fnt en la carpeta "ui"
        try {
            skin = new Skin(Gdx.files.internal("ui/ui2.json"));
        } catch (Exception e) {
            Gdx.app.error("MainMenuScreen", "No se pudo cargar el skin", e);
            // Crea un skin por defecto si falla para que no crashee
            skin = new Skin(); 
        }

        // 3. Cargar la textura de fondo y aplicar filtro NEAREST
        backgroundTexture = new Texture(Gdx.files.internal("01-Portada.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        Image backgroundImage = new Image(backgroundTexture);

        // 4. Crear la tabla que organizará todo
        Table mainTable = new Table();
        mainTable.setFillParent(true); // Hace que la tabla ocupe toda la pantalla

        // 5. Añadir la imagen de fondo a la tabla
        // .expand() y .fill() hacen que la imagen ocupe todo el espacio de la tabla
        mainTable.add(backgroundImage).expand().fill();
        
        // 6. Crear otra tabla para los botones, superpuesta al fondo
        Table buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.center(); // Centra los botones

        // 7. Crear los botones usando el Skin
        TextButton playButton = new TextButton("Jugar", skin);
        TextButton exitButton = new TextButton("Salir", skin);

        // 8. Añadir Listeners (acciones) a los botones
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen", "Botón 'Jugar' pulsado");
                // Aquí cambiarías a la pantalla de juego
                // game.setScreen(new GameScreen(game)); 
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen", "Botón 'Salir' pulsado");
                Gdx.app.exit(); // Cierra el juego
            }
        });

        // 9. Añadir los botones a la tabla de botones
        buttonTable.add(playButton).pad(10).width(100).height(30);
        buttonTable.row(); // Nueva fila
        buttonTable.add(exitButton).pad(10).width(100).height(30);

        // 10. Añadir las tablas al Stage
        stage.addActor(mainTable);  // El fondo (se dibuja primero)
        stage.addActor(buttonTable); // Los botones (se dibujan encima)
    }    
    
    @Override
    public void show() {
        // Prepare your screen here.
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        // Limpiar la pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizar y dibujar el Stage (la UI)
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
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        stage.dispose();
        skin.dispose();
        backgroundTexture.dispose();
    }
}