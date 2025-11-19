package edu.uoc.mii;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        // Iniciar el juego estableciendo la pantalla del menú principal
        setScreen(new MainMenuScreen(this));
        //setScreen(new TestScreen());
    }
    
    @Override
    public void render() {
        // 'super.render()' llama al método render() de la pantalla activa
        super.render();
    }

    @Override
    public void dispose() {
        // El 'super.dispose()' llamará al dispose() de la pantalla activa
        super.dispose();
    }
}