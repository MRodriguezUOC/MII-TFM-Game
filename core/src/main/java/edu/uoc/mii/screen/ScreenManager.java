package edu.uoc.mii.screen;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;
import edu.uoc.mii.Main;

/**
 *
 * @author Marco Rodriguez
 */
public class ScreenManager {

    private static ScreenManager instance;
    private Main game;

    private ScreenManager() {
    }

    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    public void initialize(Main game) {
        this.game = game;
    }

    public void setScreen(ScreenType type) {
        Screen currentScreen = game.getScreen();
        if (currentScreen != null) {
            currentScreen.dispose();
        }

        unloadCurrentLevelAssets();
        queueAssets(type);
        game.setScreen(new LoadingScreen(game, type));
    }

    private void queueAssets(ScreenType screenType) {
        for (AssetDescriptor ad : screenType.getAssetsDescriptor()) {
            Gdx.app.log("ScreenManager", "Loading asset: " + ad.toString());
            game.assetManager.load(ad);
        }
    }

    public void finishLoading(ScreenType type) {
        Screen newScreen = type.getScreen(game);
        if (Gdx.app.getType() == ApplicationType.WebGL) {
            warmUpAudio(type);
        }
        game.setScreen(newScreen);
    }

    private void warmUpAudio(ScreenType screenType) {
        for (AssetDescriptor ad : screenType.getAssetsDescriptor()) { 
            if(ad.type == Sound.class){
                Sound s = (Sound) game.assetManager.get(ad);
                s.play(0);
            }
        }
    }

    private void unloadCurrentLevelAssets() {
        game.assetManager.clear();
    }
}
