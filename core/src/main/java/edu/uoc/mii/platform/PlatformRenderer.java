package edu.uoc.mii.platform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.uoc.mii.Main;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.conf.GameConfig;
import static edu.uoc.mii.platform.Checkpoint.State.CHECKED;
import static edu.uoc.mii.platform.Checkpoint.State.NO_CHECK;
import static edu.uoc.mii.platform.Checkpoint.State.OPENED;

/**
 *
 * @author Marco Rodriguez
 */
public class PlatformRenderer implements Disposable {

    private final AssetManager assetManager;
    private final SpriteBatch batch;
    private final PlatformController controller;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Viewport viewport;
    private OrthographicCamera camera;
    private Texture background;
    private PlatformMap map;
    private ShapeRenderer shapeRenderer;
    private TextureAtlas level2Atalas;
    private AiDebugRenderer aiDebugRenderer;

    private int minCameraX;
    private int minCameraY;
    private int maxCameraX;
    private int maxCameraY;

    private float checkpointStateTime = 0;

    private Animation<TextureRegion> checkpointIdle;
    private Animation<TextureRegion> checkpointOpening;
    private Animation<TextureRegion> checkpointOpened;
    private Animation<TextureRegion> transporter;
    private Animation<TextureRegion> enemy1;
    private Animation<TextureRegion> enemy2Idle, enemy2Walk, enemy2Fall;
    private Animation<TextureRegion> enemy3Idle, enemy3Fly, enemy3Attack, enemy3Hit;
    private Animation<TextureRegion> mainCharacterRunning;
    private Animation<TextureRegion> mainCharacterIdle;
    private Animation<TextureRegion> mainCharacterJump;
    private Animation<TextureRegion> mainCharacterFall;
    private Animation<TextureRegion> hitMedium;
    private Animation<TextureRegion> door;
    private Animation<TextureRegion> pushButton;
    private Array<ObjectRenderer> objects;

    public PlatformRenderer(Main game, PlatformMap map, PlatformController controller) {
        this.batch = game.getBatch();
        this.assetManager = game.assetManager;
        this.controller = controller;
        this.map = map;

        //Fix (bg and sprite white).
        batch.setShader(null);
        //Fix alpha chanel of dialog hide (bg and sprites black)
        batch.setColor(Color.WHITE);
        // Reset blending
//        batch.enableBlending();
//        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        objects = new Array<>();

        init();
    }

    private void init() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT, camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();

        mapRenderer = new OrthogonalTiledMapRenderer(map.getTiledMap());

        minCameraX = (int) viewport.getWorldWidth() / 2;
        minCameraY = (int) viewport.getWorldHeight() / 2;
        maxCameraX = map.worldWidth - minCameraX;
        maxCameraY = map.worldHeight - minCameraY;

        background = assetManager.get(AssetPaths.LEVEL2_BG);

        level2Atalas = assetManager.get(AssetPaths.LEVEL2_ATLAS);

        checkpointIdle = loadAnim("Checkpoint_No_Flag", 48, 48, level2Atalas);
        checkpointOpening = loadAnim("Checkpoint_Flag_Out2", 48, 48, level2Atalas);
        checkpointOpened = loadAnim("Checkpoint_Flag_Idle2", 48, 48, level2Atalas);
        controller.getCheckPoint().openTime = checkpointOpening.getAnimationDuration();
        transporter = loadAnim("Transporter", 384 / 4, 32, level2Atalas);
        enemy1 = loadAnim("Enemy01", (int) Enemy.Type.DRILL.getWidth(), (int) Enemy.Type.DRILL.getHeight(), level2Atalas);
        enemy2Idle = loadAnim("Enemy02_Idle", (int) Enemy.Type.HUNTER.getWidth(), (int) Enemy.Type.HUNTER.getHeight(), level2Atalas);
        enemy2Fall = loadAnim("Enemy02_Fall", (int) Enemy.Type.HUNTER.getWidth(), (int) Enemy.Type.HUNTER.getHeight(), level2Atalas);
        enemy2Walk = loadAnim("Enemy02_Walk", (int) Enemy.Type.HUNTER.getWidth(), (int) Enemy.Type.HUNTER.getHeight(), level2Atalas);
        enemy3Idle = loadAnim("Enemy03_Idle", (int) Enemy.Type.T3.getWidth(), (int) Enemy.Type.T3.getHeight(), level2Atalas);
        enemy3Fly = loadAnim("Enemy03_Fly", (int) Enemy.Type.T3.getWidth(), (int) Enemy.Type.T3.getHeight(), level2Atalas);
        enemy3Attack = loadAnim("Enemy03_Attack", (int) Enemy.Type.T3.getWidth(), (int) Enemy.Type.T3.getHeight(), level2Atalas);
        enemy3Hit = loadAnim("Enemy03_Hit", (int) Enemy.Type.T3.getWidth(), (int) Enemy.Type.T3.getHeight(), level2Atalas);
        mainCharacterIdle = loadAnim("MainCharacter_Idle", (int) Nanobot.WIDTH, (int) Nanobot.HEIGHT, level2Atalas);
        mainCharacterRunning = loadAnim("MainCharacter_Run", (int) Nanobot.WIDTH, (int) Nanobot.HEIGHT, level2Atalas);
        mainCharacterJump = loadAnim("MainCharacter_Jump", (int) Nanobot.WIDTH, (int) Nanobot.HEIGHT, level2Atalas);
        mainCharacterFall = loadAnim("MainCharacter_Fall", (int) Nanobot.WIDTH, (int) Nanobot.HEIGHT, level2Atalas);
        hitMedium = loadAnim("Hit_Medium", 32, 32, level2Atalas);

        Animation<TextureRegion> anim = loadAnim("Entry", 32, 64, level2Atalas);
        ObjectRenderer objectRenderer = new DoorRenderer(batch, anim, controller.getDoor());
        objects.add(objectRenderer);

        anim = loadAnim("Trap1", 48, 48, level2Atalas);
        objectRenderer = new PushButtonRenderer(batch, anim, controller.getPushButton());
        objects.add(objectRenderer);

    }

    private Animation<TextureRegion> loadAnim(String name, int x, int y, TextureAtlas atlas) {
        TextureRegion sheet = atlas.findRegion(name);
        TextureRegion[][] tmp = sheet.split(x, y);
        Array<TextureRegion> frames = new Array<>(tmp[0].length);
        for (TextureRegion item : tmp[0]) {
            frames.add(item);
        }
        return new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
    }

    //TODO: change to a class
    private float stateTime = 0f;

    public void render(float delta) {
        Nanobot player = controller.getPlayer();

        updateCamera(player);
        viewport.apply();
        //This paint background fixed position but is needed to fix bgImage small
        //when use controllersHud.
        batch.setProjectionMatrix(camera.combined);
        //Fix alpha chanel of dialog hide (bg and sprites black)
        batch.setColor(Color.WHITE);

        if (controller.isBgEnabled()) {
            batch.begin();
            //Move BG with camera
            float bgX = camera.position.x - (camera.viewportWidth / 2);
            float bgY = camera.position.y - (camera.viewportHeight / 2);
            batch.draw(background, bgX, bgY, viewport.getWorldWidth(), viewport.getWorldHeight());
            batch.end();
        }

        mapRenderer.setView(camera);
        mapRenderer.render();

        if (controller.isGridEnabled()) {
            renderGrid(player);
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (!controller.isPlayerDead() && !controller.isLevelCompleted()) {
            stateTime += delta;
        }

        if (controller.isCheckPointEnabled()) {
            renderCheckPoint(delta);
        }

        if (controller.getCheckPoint().state == OPENED) {
            TextureRegion currentFrame = transporter.getKeyFrame(stateTime, true);
            Vector2 tPoint = map.getTransporter();
            batch.draw(currentFrame, tPoint.x, tPoint.y);
        }

        renderObjects(delta);
        renderEnemies();
        renderMainCharacter(player);

        batch.end();

    }

    private void renderObjects(float delta) {
        for (ObjectRenderer objectRenderer : objects) {
            objectRenderer.render(delta);
        }
    }

    private void renderCheckPoint(float dt) {
        TextureRegion currentFrame;
        switch (controller.getCheckPoint().state) {
            case NO_CHECK:
                currentFrame = checkpointIdle.getKeyFrame(checkpointStateTime, true);
                break;
            case CHECKED:
                checkpointStateTime += dt;
                if (checkpointStateTime > controller.getCheckPoint().openTime) {
                    checkpointStateTime = controller.getCheckPoint().openTime;
                }
                currentFrame = checkpointOpening.getKeyFrame(checkpointStateTime, false);
                break;
            case OPENED:
                checkpointStateTime = 0f;
                currentFrame = checkpointOpened.getKeyFrame(stateTime, true);
                break;
            default:
                throw new AssertionError();
        }

        Vector2 point = map.getCheckPoint();
        batch.draw(currentFrame, point.x, point.y);
    }

    private void updateCamera(Nanobot player) {
        camera.position.x = player.getPosition().x;
        camera.position.y = player.getPosition().y;
        if (camera.position.x < minCameraX) {
            camera.position.x = minCameraX;
        }
        if (camera.position.x > maxCameraX) {
            camera.position.x = maxCameraX;
        }
        if (camera.position.y < minCameraY) {
            camera.position.y = minCameraY;
        }
        if (camera.position.y > maxCameraY) {
            camera.position.y = maxCameraY;
        }
        camera.update();
        viewport.apply();
    }

    private void renderEnemies() {
        for (Enemy e : controller.getEnemies()) {
            if (e.isAlive) {
                switch (e.type) {
                    case DRILL:
                        renderEnemy1((DrillEnemy)e);
                        break;
                    case HUNTER:
                        renderEnemy2((HunterEnemy)e);
                        break;
                    case T3:
                        renderEnemy3(e);
                        break;
                }
            }
        }
    }

    private void renderEnemy1(DrillEnemy e) {
        //idle
        TextureRegion currentFrame = enemy1.getKeyFrame(stateTime, true);
        renderEnemy(e, currentFrame);

        if (((DrillEnemy) e).isDrilling()) {
            currentFrame = hitMedium.getKeyFrame(e.getStateTime(), true);
            renderTextureRegion(currentFrame, e.position.x + 18, e.position.y - 8, 32, 32, true);
        } else if (((DrillEnemy) e).isProtecting()) {
            currentFrame = hitMedium.getKeyFrame(e.getStateTime(), true);
            renderTextureRegion(currentFrame, e.position.x - 18, e.position.y - 8, 32, 32, true);
        }
    }

    private void renderEnemy2(HunterEnemy e) {
        //idle, fall, walk
        TextureRegion currentFrame = enemy2Idle.getKeyFrame(stateTime, true);
        renderEnemy(e, currentFrame);
        if (controller.isDebugIA()) {
            if (aiDebugRenderer == null) {
                aiDebugRenderer = new AiDebugRenderer();
            }

            aiDebugRenderer.render(e, camera, batch);
        }
    }

    private void renderEnemy3(Enemy e) {
        //idle, fly=walk, attack, hit
        int idx1 = enemy3Attack.getKeyFrameIndex(stateTime);
        TextureRegion currentFrame = enemy3Attack.getKeyFrame(stateTime, true);
        renderEnemy(e, currentFrame);
        if (idx1 > 3) {
            Object[] frames = hitMedium.getKeyFrames();
            currentFrame = (TextureRegion) frames[idx1 - 4];
            renderTextureRegion(currentFrame, e.position.x + 8, e.position.y - 16, 32, 32, true);
        }
    }

    private void renderEnemy(Enemy e, TextureRegion currentFrame) {
        renderTextureRegion(currentFrame, e.position.x, e.position.y, e.bounds.width, e.bounds.height, e.isFacinRight);
    }

    private void renderMainCharacter(Nanobot player) {
        TextureRegion mainChar;
        if (player.isFalling) {
            mainChar = mainCharacterFall.getKeyFrame(stateTime, true);
        } else if (!player.isGrounded) {
            mainChar = mainCharacterJump.getKeyFrame(stateTime, true);
        } else if (player.isRunning) {
            mainChar = mainCharacterRunning.getKeyFrame(stateTime, true);
        } else {
            mainChar = mainCharacterIdle.getKeyFrame(stateTime, true);
        }

        renderTextureRegion(mainChar, player.getPosition().x, player.getPosition().y, player.bounds.width, player.bounds.height, player.isFacinRight);
    }

    private void renderTextureRegion(TextureRegion character, float x, float y, float w, float h, boolean isFacinRight) {
        if (isFacinRight) {
            batch.draw(character, x, y);
        } else {
            batch.draw(
                    character,
                    x + w, y,
                    0, 0,
                    -w, h,
                    1, 1,
                    0
            );
        }
    }

    private void renderGrid(Nanobot player) {
        if (shapeRenderer == null) {
            shapeRenderer = new ShapeRenderer();
        }
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.DARK_GRAY);

        // Vertical lines
        for (float x = 0; x <= map.worldWidth; x += map.tileWidth) {
            shapeRenderer.line(x, 0, x, map.worldHeight);
        }

        // Horizontal lines
        for (float y = 0; y <= map.worldHeight; y += map.tileHeight) {
            shapeRenderer.line(0, y, map.worldWidth, y);
        }

        renderCharacterGrid(player.bounds, player.colisionBounds);

        for (Enemy e : controller.getEnemies()) {
            if (e.isAlive) {
                renderCharacterGrid(e.bounds, e.colisionBounds);
            }
        }

        renderRectangleGrid(map.getIceFloors(), Color.BLUE);
        renderRectangleGrid(map.getUpFloors(), Color.GREEN);
        renderRectangleGrid(map.getDeadZones(), Color.RED);

        renderRectangleGrid(map.getFinishArea(), Color.BLUE);
        renderRectangleGrid(controller.getCheckPoint().colisionBounds, Color.BLUE);

        shapeRenderer.end();

    }

    private void renderCharacterGrid(Rectangle bounds, Rectangle colisionBounds) {
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(colisionBounds.x, colisionBounds.y, colisionBounds.width, colisionBounds.height);
    }

    private void renderRectangleGrid(Array<Rectangle> rectangles, Color color) {
        shapeRenderer.setColor(color);
        for (Rectangle rect : rectangles) {
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }
    }

    private void renderRectangleGrid(Rectangle rect, Color color) {
        shapeRenderer.setColor(color);
        shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        Gdx.app.log("PlatformRenderer", "worldWidth: " + viewport.getWorldWidth() + ", worldHeight: " + viewport.getWorldHeight());
    }
    
    public int getLeftGutterWidth(){
        return viewport.getLeftGutterWidth();
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
    }

}
