package edu.uoc.mii.utils;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 *
 * @author Marco Rodriguez
 */
public class DummyPlatformInput implements PlatformInput {
    
    @Override
    public void addStageTouchListener(Stage stage, TextField... fields){
    }
    
    @Override
    public void showInput(String defaultText, boolean password, InputCallback callback) {
    }
    
    @Override
    public void showInputFor(TextField textField, boolean passwordm, PlatformInput.InputCallback callback){
    }

    @Override
    public void hide() {
    }
}
