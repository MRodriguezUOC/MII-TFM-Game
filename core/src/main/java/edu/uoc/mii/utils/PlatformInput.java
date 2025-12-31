package edu.uoc.mii.utils;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 *
 * @author Marco Rodriguez
 */
public interface PlatformInput {
    void showInput(String defaultText, boolean password, InputCallback callback);
    void showInputFor(TextField textField, boolean password, InputCallback callback);
    void addStageTouchListener(Stage stage, TextField... fields);
    void hide();

    interface InputCallback {
        void onResult(String value);
    }
}
