package edu.uoc.mii;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 *
 * @author Marco Rodriguez
 */
public class GameVirtualControlsHud implements Disposable {

    private final Stage stage;
    private final Viewport viewport;
    private final Actor[] touchingActors = new Actor[10];

    private float scale;
    private int gameGutter;
    private int btnSize;
    private Table rootTable;
    private TextButton btnLeft;
    private TextButton btnRight;
    private TextButton btnJump;
    private TextButton btnDown;    
    private final Vector2 tempCoords = new Vector2();
    
    public boolean isLeftPressed, isRightPressed, isJumpPressed, isDownPressed;

    public GameVirtualControlsHud(Main game) {
        viewport = new ExtendViewport(800, 480);
        stage = new Stage(viewport, game.getBatch());

        createControls();
    }

    private void createControls() {
        rootTable = new Table();
        rootTable.setFillParent(true);
//        rootTable.setDebug(true);
        stage.addActor(rootTable);

        BitmapFont font;
        font = new BitmapFont();
        font.getData().setScale(3f);
        TextButtonStyle btnStyle = new TextButtonStyle();
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        //TODO calculate button size bases on screen size
        btnSize = 100;
        btnStyle.font = font;
        btnStyle.up = createBox(btnSize, btnSize, new Color(0, 0, 0, 0.5f));
        Drawable btnStyleDown = createBox(btnSize, btnSize, new Color(1, 1, 1, 0.5f));
        btnStyle.down = btnStyleDown;
        btnStyle.checked = btnStyleDown;

        btnLeft = new TextButton("<", btnStyle);
        btnRight = new TextButton(">", btnStyle);
        //Unicode \u0245 (Ʌ) dont exist on default font
        //"^" is little
        btnJump = new TextButton("v", btnStyle);
        btnJump.setTransform(true);
        btnJump.setOrigin(Align.center);
        btnJump.setRotation(180);
        btnDown = new TextButton("v", btnStyle);

        btnLeft.setTouchable(Touchable.disabled);
        btnRight.setTouchable(Touchable.disabled);
        btnJump.setTouchable(Touchable.disabled);
        btnDown.setTouchable(Touchable.disabled);

        rootTable.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                pointerUpdated(pointer, x, y);
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                pointerUpdated(pointer, x, y);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (pointer < touchingActors.length) {
                    touchingActors[pointer] = null;
                }
                calculateStates();
            }

            private void pointerUpdated(int pointer, float x, float y) {
                if (pointer >= touchingActors.length) {
                    return;
                }
                Actor hitActor = null;

                if (isTouchInside(btnLeft, x, y)) {
                    hitActor = btnLeft;
                } else if (isTouchInside(btnRight, x, y)) {
                    hitActor = btnRight;
                } else if (isTouchInside(btnJump, x, y)) {
                    hitActor = btnJump;
                } else if (isTouchInside(btnDown, x, y)) {
                    hitActor = btnDown;
                }

                touchingActors[pointer] = hitActor;

                calculateStates();
            }

            private void calculateStates() {
                isLeftPressed = false;
                isRightPressed = false;
                isJumpPressed = false;
                isDownPressed = false;

                for (Actor actor : touchingActors) {
                    if (actor == null) {
                        continue;
                    }
                    if (actor == btnLeft) {
                        isLeftPressed = true;
                    }
                    if (actor == btnRight) {
                        isRightPressed = true;
                    }
                    if (actor == btnJump) {
                        isJumpPressed = true;
                    }
                    if (actor == btnDown) {
                        isDownPressed = true;
                    }
                }

                btnLeft.setChecked(isLeftPressed);
                btnRight.setChecked(isRightPressed);
                btnJump.setChecked(isJumpPressed);
                btnDown.setChecked(isDownPressed);
            }
        });
        rootTable.setTouchable(Touchable.enabled);
        
        redesign();
    }

    private boolean isTouchInside(Actor actor, float x, float y) {
        tempCoords.set(x,y);
        rootTable.localToDescendantCoordinates(actor, tempCoords);
        
        return tempCoords.x >= 0 && tempCoords.x <= actor.getWidth() &&
               tempCoords.y >= 0 && tempCoords.y <= actor.getHeight();        
    }

    private Drawable createBox(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new TextureRegionDrawable(new TextureRegion(texture));
    }
    
    private void redesign(){
        // Clear drop buttons and listeners
        //rootTable.clear();
        rootTable.clearChildren();
        rootTable.bottom().left();
        rootTable.add(btnLeft).size(btnSize, btnSize).padLeft(20).padBottom(20);
        rootTable.add(btnRight).size(btnSize, btnSize).padLeft(20).padBottom(20);
        rootTable.add().expandX();

        Gdx.app.log("GameVirtualControlsHud", "redesign, gameGutter: " + gameGutter + ", scale: " + scale + ", btnSize: " + btnSize);

        boolean horizontalArrange = (gameGutter * scale) > (btnSize * 2) + 20;
        Table actionTable = new Table();    
        if (horizontalArrange) {
            actionTable.add(btnDown).size(btnSize, btnSize).pad(10);
            actionTable.add(btnJump).size(btnSize, btnSize).pad(10);
        } else {
            actionTable.add(btnJump).size(btnSize, btnSize).pad(5).row();
            actionTable.add(btnDown).size(btnSize, btnSize).pad(5);
        }        
        
        rootTable.add(actionTable).bottom().right();
        rootTable.setTouchable(Touchable.enabled);
    }

    public void render(float delta) {
        viewport.apply();
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height, int gutter) {
        viewport.update(width, height, true);
        scale = stage.getWidth() / (float)width;
        gameGutter = gutter;
        redesign();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public Stage getStage() {
        return stage;
    }

}
