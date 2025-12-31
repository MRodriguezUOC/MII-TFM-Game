package edu.uoc.mii;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.conf.GameConfig;
import edu.uoc.mii.utils.StringFormat;

/**
 *
 * @author Marco Rodriguez
 */
public class GameHud implements Disposable {

    private final GameManager gameManager;
    private final Stage stage;
    private final Skin dlgSkin;
    private final Viewport viewport;

    private Label scoreLabel;
    private Label healthLabel;
    private Label messageLabel;
    private Label bottomLeftLabel;
    private Label bottomCenterLabel;
    private Label bottomRightLabel;
    private Dialog dialog;

    public GameHud(Main game) {
        gameManager = GameManager.getInstance();
        viewport = new FitViewport(GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.getBatch());
        dlgSkin = game.assetManager.get(AssetPaths.C64_SKIN);

        setupUI();
    }

    private void setupUI() {
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        BitmapFont font = new BitmapFont();
        font.getData().setScale(0.75f);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        healthLabel = new Label("Vidas: " + gameManager.getLives(), labelStyle);
        //String.format() not available on GWT
        scoreLabel = new Label(StringFormat.intLeftPad("000000", gameManager.getScore()), labelStyle);
        messageLabel = new Label("", labelStyle);
        bottomLeftLabel = new Label("", labelStyle);
        bottomCenterLabel = new Label("", labelStyle);
        bottomRightLabel = new Label("", labelStyle);

        table.add(healthLabel).expandX().padTop(1).left().padLeft(10);
        table.add(messageLabel).expandX().padTop(1).center().padLeft(10).padRight(10);
        table.add(scoreLabel).expandX().padTop(1).right().padRight(10);
        table.row().padTop(GameConfig.VIRTUAL_HEIGHT - healthLabel.getHeight() * 3);
        table.add(bottomLeftLabel).expandX().padBottom(1).left().padLeft(10);
        table.add(bottomCenterLabel).expandX().padBottom(1).center().padLeft(10).padRight(10);
        table.add(bottomRightLabel).expandX().padBottom(1).right().padRight(10);

        stage.addActor(table);
    }

    public void showDialog(String title, String message, String btnLbl, final Runnable onSuccess) {
        dialog = new Dialog(title, dlgSkin) {
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    if (onSuccess != null) {
                        onSuccess.run();
                    } else {
                        dialog.hide();
                    }
                }
            }
        };

        dialog.text(message);
        dialog.button(btnLbl, true);
        dialog.setModal(true);
        dialog.show(stage);
    }

    public void render(float delta) {
        viewport.apply();
        healthLabel.setText("Vidas: " + gameManager.getLives());
        scoreLabel.setText(StringFormat.intLeftPad("000000", gameManager.getScore()));
        messageLabel.setText(gameManager.getHudMessage());

        if (gameManager.isShowFPS()) {
            bottomRightLabel.setText(Gdx.graphics.getFramesPerSecond() + "fps");
        } else {
            bottomRightLabel.setText("");
        }
        if (gameManager.isShowPPV()) {
            bottomCenterLabel.setText(GameManager.getInstance().getPlayerInfo());
        } else {
            bottomCenterLabel.setText("");
        }

        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public Stage getStage() {
        return stage;
    }

}
