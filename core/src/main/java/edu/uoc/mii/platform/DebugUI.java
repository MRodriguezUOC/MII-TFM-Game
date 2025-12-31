package edu.uoc.mii.platform;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.conf.RetroScreenResolution;

/**
 *
 * @author Marco Rodriguez
 */
public class DebugUI {

    private final Stage stage;
    private final Skin skin;
    private final Viewport viewport;

    public DebugUI(AssetManager assetManager) {
        //stage = new Stage(new ScreenViewport());
        // ScreenViewport On the web, it doesn't look good with some screen 
        // ratios, especially elongated ones, and it will probably also fail if they are very stretched.
        viewport = new FitViewport(RetroScreenResolution.PC_VGA.getWidth(), RetroScreenResolution.PC_VGA.getHeight());
        stage = new Stage(viewport);

        skin = assetManager.get(AssetPaths.C64_SKIN);

        Window window = new Window("Debug Físicas", skin);
        window.setPosition(10, 10);
        window.pack();

        window.add(new Label("Gravedad:", skin));
        window.row();
        final Slider gravitySlider = new Slider(-500f, -5f, 1f, false, skin);
        gravitySlider.setValue(Nanobot.GRAVITY);
        gravitySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Nanobot.GRAVITY = gravitySlider.getValue();
            }
        });
        window.add(gravitySlider).width(200);

        window.row(); 
        window.add(new Label("Velocidad:", skin));
        window.row();
        final Slider speedSlider = new Slider(50f, 300f, 5f, false, skin);
        speedSlider.setValue(Nanobot.SPEED);
        speedSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Nanobot.SPEED = speedSlider.getValue();
            }
        });
        window.add(speedSlider).width(200);

        window.row();
        window.add(new Label("Salto:", skin));
        window.row();
        final Slider jumpSlider = new Slider(50f, 300f, 5f, false, skin);
        jumpSlider.setValue(Nanobot.JUMP_FORCE);
        jumpSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Nanobot.JUMP_FORCE = jumpSlider.getValue();
            }
        });
        window.add(jumpSlider).width(200);

        window.row();
        window.add(new Label("Ancho:", skin));
        window.row();
        final Slider withSlider = new Slider(1f, 64f, 1f, false, skin);
        withSlider.setValue(Nanobot.WIDTH);
        withSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Nanobot.WIDTH = withSlider.getValue();
            }
        });
        window.add(withSlider).width(95);

        window.row();
        window.add(new Label("Alto:", skin));
        window.row();
        final Slider heighSlider = new Slider(1f, 64f, 1f, false, skin);
        heighSlider.setValue(Nanobot.HEIGHT);
        heighSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Nanobot.HEIGHT = heighSlider.getValue();
            }
        });
        window.add(heighSlider).width(95);

        window.pack();
        stage.addActor(window);
    }

    public void render() {
        viewport.apply();
        stage.act();
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public Stage getStage() {
        return stage;
    }
}
