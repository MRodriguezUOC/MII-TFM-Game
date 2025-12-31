package edu.uoc.mii;

import static com.badlogic.gdx.Application.LOG_DEBUG;
import static com.badlogic.gdx.Application.LOG_INFO;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PropertiesUtils;
import edu.uoc.mii.audio.AudioManager;
import edu.uoc.mii.conf.PreferenceManager;
import edu.uoc.mii.screen.ScreenManager;
import edu.uoc.mii.screen.ScreenType;
import edu.uoc.mii.utils.PlatformInput;

/**
 * Base inicial creada desde gdx-liftoff.
 *
 * @author Marco Rodriguez
 */
/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends Game {

    private SpriteBatch batch;

    public AssetManager assetManager;
    public PlatformInput platformInput;
    public String version;

    public Main(PlatformInput platformInput) {
        this.platformInput = platformInput;
        this.assetManager = new AssetManager();        
        assetManager.setErrorListener((AssetDescriptor asset, Throwable throwable) -> {
            System.err.println("Error cargando asset: " + asset.fileName);
            throwable.printStackTrace();
        });
        //Gdx.app is null here.
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void create() {
        ScreenManager.getInstance().initialize(this);        
        AudioManager.getInstance().initialize(assetManager);
        if (PreferenceManager.getInstance().idDebugEnabled()) {
            Gdx.app.setLogLevel(LOG_DEBUG);
            Gdx.app.debug("Main", "Debug Level Enabled");
        } else {
            Gdx.app.setLogLevel(LOG_INFO);
        }
        
        try {
            FileHandle fileHandle = Gdx.files.internal("version.properties");
            if (fileHandle.exists()) {
                ObjectMap<String, String> props = new ObjectMap<>();
                PropertiesUtils.load(props, fileHandle.reader());
                version = "v" + props.get("version", "Unknown");
            }else{
                version = "v-Unknown";
            }
        } catch (Exception e) {
            Gdx.app.error("Main", "Error leyendo version", e);
            if(version == null) version = "v-Unknown";
        }        
        
        GameManager.initialize(this);
        batch = new SpriteBatch();

        ScreenType next;
        ScreenType now;
        if (PreferenceManager.getInstance().haveValidCredentials()) {
            next = ScreenType.AUTO_LOGIN;
        } else {
            Gdx.app.log("Main", "Sin credenciales guardadas");
            next = ScreenType.MAIN_MENU;
        }

        if (PreferenceManager.getInstance().skipIntro()) {
            now = next;
        } else {
            now = ScreenType.INTRO;
        }

        ScreenManager.getInstance().setScreen(now);
    }

    @Override
    public void render() {
        // 'super.render()' calls the render() method of the active screen
        super.render();
    }

    @Override
    public void dispose() {
        // El 'super.dispose()' will call the dispose() function of the active screen
        batch.dispose();
        super.dispose();
    }

}
