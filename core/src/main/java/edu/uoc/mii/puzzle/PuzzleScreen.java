package edu.uoc.mii.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import edu.uoc.mii.GameEventLintener;
import edu.uoc.mii.GameHud;
import edu.uoc.mii.GameManager;
import edu.uoc.mii.Main;
import edu.uoc.mii.audio.AudioManager;
import edu.uoc.mii.screen.ScreenType;

/**
 * Base iniciada con gemini. Prompt: Me ayudas con la creación del primer nivel
 * del juego? Te recuerdo, el video juego es de estética retro, osea en pixel
 * art. Y estoy usando libGDX. Mi idea, para este nivel, es un puzle, de piezas
 * cuadradas del mismo tamaño. Dependiendo de la dificultad de juego, las piezas
 * serán mas o menos grandes. Me ayudas a programarlo en libGDX?
 *
 * @author Marco Rodriguez
 */
public class PuzzleScreen extends ScreenAdapter {

    private final Main game;
    private final GameManager gameManager;
    private final PuzzleController controller;
    private final PuzzleRenderer renderer;
    private final GameHud hud;

    public PuzzleScreen(Main game) {
        this.game = game;
        gameManager = GameManager.getInstance();
        gameManager.setListener(new GameEventLintener() {
            @Override
            public void onPlayerDead() {
                hud.showDialog("Reintentar", " Se te acabaron\n los movimientos", "Reintentar", null);
            }

            @Override
            public void onLevelCompleted() {
                hud.showDialog("Superado", "¡Bien hecho!", "Continuar", null);
            }

            @Override
            public void onEndGame(Runnable onSuccess) {
                hud.showDialog("Perfecto", " Has salvado \n al ZXSpectrum", "Continuar", onSuccess);
            }

            @Override
            public void onGameOver(Runnable onSuccess) {
                hud.showDialog("GameOver", " La IA ha ganado \n       :-(", "Salir", onSuccess);
            }

        });

        controller = new PuzzleController(game);
        renderer = new PuzzleRenderer(game, controller);
        hud = new GameHud(game);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (game.assetManager.update()) {
            renderer.render();
        } else {
            float progress = game.assetManager.getProgress();
            System.out.println("Cargando... " + (progress * 100) + "%");
        }
        if (gameManager.isShowHUD()) {
            hud.render(delta);
        }
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
        hud.resize(width, height);
    }

    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hud.getStage());
        multiplexer.addProcessor(controller);
        multiplexer.addProcessor(renderer);
        multiplexer.addProcessor(gameManager);
        Gdx.input.setInputProcessor(multiplexer);
        AudioManager.getInstance().clearPlaylist();
        for (AssetDescriptor ad : ScreenType.LEVEL_PUZZLE.getAssetsDescriptor()) {
            if (ad.type.equals(Music.class)) {
                AudioManager.getInstance().addPlaylist(ad);
            }
        }
        AudioManager.getInstance().startPlaylist(true);
        controller.startGame();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
//        dispose();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }

}
