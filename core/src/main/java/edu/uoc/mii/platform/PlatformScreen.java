package edu.uoc.mii.platform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.ScreenUtils;
import edu.uoc.mii.GameEventLintener;
import edu.uoc.mii.GameHud;
import edu.uoc.mii.GameManager;
import edu.uoc.mii.GameVirtualControlsHud;
import edu.uoc.mii.Main;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.audio.AudioManager;

/**
 *
 * @author Marco Rodriguez
 */
public class PlatformScreen extends ScreenAdapter {

    private final Main game;
    private final GameHud hud;
    private final GameManager gameManager;
    private final GameVirtualControlsHud controls;
    private final PlatformController controller;
    private final PlatformRenderer renderer;
    private final PlatformMap map;
    private final DebugUI debugUI;

    private boolean paused;

    public PlatformScreen(Main game) {
        this.game = game;
        hud = new GameHud(game);        
        gameManager = GameManager.getInstance();
        gameManager.setListener(new GameEventLintener() {
            @Override
            public void onPlayerDead() {
                hud.showDialog("Reintentar", " Nanobot \n averiado", "Reintentar", () -> {
                    controller.resetLevel();
                });
            }

            @Override
            public void onLevelCompleted() {
                hud.showDialog("Superado", "¡Bien hecho!", "Continuar", () -> {
                    controller.nextLevel();
                });
            }

            @Override
            public void onEndGame(Runnable onSuccess) {
                hud.showDialog("Perfecto", " Has salvado \n la GameBoy", "Continuar", onSuccess);
            }

            @Override
            public void onGameOver(Runnable onSuccess) {
                hud.showDialog("GameOver", " La IA ganó \n     :-(", "Salir", onSuccess);
            }

        });

        controls = new GameVirtualControlsHud(game);
        
        //TODO: add TiledMap loader to assetManager.
        //TiledMap tMap = game.assetManager.get(AssetPaths.LEVEL2_MAP1);
        TmxMapLoader mapLoader = new TmxMapLoader();
        TiledMap tMap = mapLoader.load("level2/map1.tmx");
        map = new PlatformMap(tMap);
        controller = new PlatformController(game, map, controls);
        renderer = new PlatformRenderer(game, map, controller);
        debugUI = new DebugUI(game.assetManager);
        paused = true;
    }

    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hud.getStage());
        multiplexer.addProcessor(controls.getStage());
        multiplexer.addProcessor(debugUI.getStage());
        multiplexer.addProcessor(controller);
        multiplexer.addProcessor(gameManager);
        Gdx.input.setInputProcessor(multiplexer);
        AudioManager.getInstance().clearPlaylist();
        AudioManager.getInstance().playMusic(AssetPaths.TRAP_LOOP, true);
        controller.startGame();
        paused = false;
    }
    
    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void render(float delta) {
//        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        ScreenUtils.clear(0, 0, 0, 1);
        //If paused, delta can be very large 
        float dt = Math.min(delta, 1 / 30f);
        if (!paused) {
            controller.update(dt);
        }
        renderer.render(dt);
        if (gameManager.isShowHUD()) {
            hud.render(dt);
        }
        if (gameManager.isDebugUI()) {
            debugUI.render();
        }
        if(gameManager.showVirtualControls()){
            controls.render(dt);
        }
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
        debugUI.resize(width, height);
        hud.resize(width, height);
        controls.resize(width, height, renderer.getLeftGutterWidth());
    }

    @Override
    public void dispose() {
        map.dispose();
        debugUI.dispose();
        controller.dispose();
        hud.dispose();
        controls.dispose();
    }

}
