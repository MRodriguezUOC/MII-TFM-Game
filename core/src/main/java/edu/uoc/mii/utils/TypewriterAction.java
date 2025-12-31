package edu.uoc.mii.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.audio.AudioManager;

/**
 *
 * @author Marco Rodriguez
 */
public class TypewriterAction extends Action {

    private String fullText;
    private float timePerChar;
    private float timer;
    private int currentIndex;
    private boolean soundEnabled;

    private Runnable onCharTyped;

    public TypewriterAction(String text, float timePerChar) {
        this.fullText = text;
        this.timePerChar = timePerChar;
        this.timer = 0;
        this.currentIndex = 0;
    }

    public TypewriterAction(String text, float timePerChar, boolean soundEnabled) {
        this(text, timePerChar);
        this.soundEnabled = soundEnabled;
    }

    public TypewriterAction(String text, float timePerChar, Runnable onCharTyped) {
        this(text, timePerChar);
        this.onCharTyped = onCharTyped;
    }

    @Override
    public boolean act(float delta) {
        Label label = (Label) getActor();

        if (currentIndex == 0 && label.getText().length() > 0) {
            label.setText("");
        }

        timer += delta;

        while (timer >= timePerChar && currentIndex < fullText.length()) {
            timer -= timePerChar;
            currentIndex++;

            label.setText(fullText.substring(0, currentIndex));

            if (onCharTyped != null) {
                onCharTyped.run();
            }

            if (soundEnabled) {
                char c = fullText.charAt(currentIndex -1);
                switch (c) {
                    case ' ':
                        AudioManager.getInstance().playSound(AssetPaths.TYPEWRITER_SPACE);
                        break;
                    case '\n':
                    case '\r':
                        //Audio is not sinchronized, on bell is more evident
                        //AudioManager.getInstance().playSound(AssetPaths.TYPEWRITER_BELL);
                        break;
                    default:
                        AudioManager.getInstance().playSound(AssetPaths.TYPEWRITER_KEY);
                }
            }
        }

        return currentIndex >= fullText.length();
    }
}
