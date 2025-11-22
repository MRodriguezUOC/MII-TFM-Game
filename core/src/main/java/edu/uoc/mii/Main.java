package edu.uoc.mii;

import com.badlogic.gdx.Game;

/**
 * Base inicial creada desde gdx-liftoff.
 * 
 * @author Marco Rodriguez
*/
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        // Start the game by setting the main menu screen.
        setScreen(new MainMenuScreen(this));
        //setScreen(new TestScreen());
    }
    
    @Override
    public void render() {
        // 'super.render()' calls the render() method of the active screen
        super.render();
    }

    @Override
    public void dispose() {
        // El 'super.dispose()' will call the dispose() function of the active screen
        super.dispose();
    }
}