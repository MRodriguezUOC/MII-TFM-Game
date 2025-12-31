package edu.uoc.mii.score;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import edu.uoc.mii.Main;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.audio.AudioManager;
import edu.uoc.mii.conf.RetroScreenResolution;
import edu.uoc.mii.screen.ScreenManager;
import edu.uoc.mii.screen.ScreenType;
import edu.uoc.mii.utils.LocalDateTimeStringParser;
import java.util.ArrayList;

/**
 *
 * @author Marco Rodriguez
 */
public class LeaderboardScreen extends ScreenAdapter {

    private final Main game;
    private final Stage stage;
    private final Skin skin;
    private Table contentTable;
    private Label statusLabel;

    public LeaderboardScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(RetroScreenResolution.PC_VGA.getWidth(), RetroScreenResolution.PC_VGA.getHeight()));
        this.skin = game.assetManager.get(AssetPaths.C64_SKIN);
    }

    private void setupUI() {
        Table root = new Table();
        root.setFillParent(true);

        Label title = new Label("Puntuaciones Globales", skin, "title");
        root.add(title).pad(10).row();

        contentTable = new Table(this.skin);
        contentTable.background("window");
        contentTable.top();

        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        root.add(scrollPane).expand().fill().row();

        statusLabel = new Label("Cargando...", skin);
        root.add(statusLabel).pad(5).row();

        TextButton backButton = new TextButton("Volver", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().setScreen(ScreenType.MAIN_MENU);
            }

        });
        root.add(backButton).padBottom(10).padTop(0).size(200, 50);

        stage.addActor(root);
    }

    private void fetchScores() {
        ScoreManager.getInstance().fetchScores(new ScoreManager.Callback() {
            @Override
            public void onSuccess(String json) {
                Gdx.app.postRunnable(() -> parseAndDisplay(json));
            }

            @Override
            public void onError(String message) {
                Gdx.app.postRunnable(() -> {
                    statusLabel.setText(message);
                });
            }
        });
    }

    private void parseAndDisplay(String jsonResponse) {
        Json json = new Json();
        json.setIgnoreUnknownFields(true);

        try {
            ArrayList<ScoreEntry> scores = json.fromJson(ArrayList.class, ScoreEntry.class, jsonResponse);

            contentTable.clear();
            if (scores == null || scores.isEmpty()) {
                statusLabel.setText("¡Aún no hay puntuaciones!");
                return; 
            }
            statusLabel.setText(""); 

            contentTable.add(new Label("Nombre", skin)).pad(10).expandX();
            contentTable.add(new Label("Cuando", skin)).pad(10).expandX();
            contentTable.add(new Label("Puntos", skin)).pad(10).expandX();
            contentTable.row();
            Image separator = new Image(skin.getDrawable("white"));
            separator.setColor(com.badlogic.gdx.graphics.Color.GRAY);
            contentTable.add(separator).height(2).fillX().colspan(3).row();

            for (ScoreEntry entry : scores) {
                contentTable.add(new Label(entry.getUsername(), skin)).pad(5);
                contentTable.add(new Label(LocalDateTimeStringParser.format(entry.getPlayedAt()),skin)).pad(5);
                contentTable.add(new Label(String.valueOf(entry.getPoints()), skin)).pad(5);
                contentTable.row();
            }

        } catch (Exception e) {
            statusLabel.setText("Error leyendo datos");
            Gdx.app.error("API", "Error parseando JSON", e);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        AudioManager.getInstance().clearPlaylist();
        for(AssetDescriptor ad: ScreenType.LEADERBOARD.getAssetsDescriptor()){
            if(ad.type.equals(Music.class)){
                AudioManager.getInstance().addPlaylist(ad);
            }
        }    
        AudioManager.getInstance().startPlaylist(true);
        setupUI();
        fetchScores();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
