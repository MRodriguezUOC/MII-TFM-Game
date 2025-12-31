package edu.uoc.mii.platform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import edu.uoc.mii.GameController;
import edu.uoc.mii.GameManager;
import edu.uoc.mii.GameVirtualControlsHud;
import edu.uoc.mii.Main;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.audio.AudioManager;
import edu.uoc.mii.utils.StringFormat;

/**
 *
 * @author Marco Rodriguez
 */
public class PlatformController implements GameController, InputProcessor, Disposable {

    public static final float DEFAULT_LEVEL_TIME = 30.0f;

    private final Main game;
    private final GameVirtualControlsHud controls;
    private final GameManager gameManager;
    private final AudioManager audioManager;
    private final Nanobot player;
    private final PlatformMap map;

    private final Array<Enemy> enemies;
    private Vector2 startPoint;
    private Checkpoint checkPoint;
    private PushButton pushButton;
    private Door door;

    private boolean gridEnabled = false;
    private boolean mapEnabled = false;
    private boolean bgEnabled = true;
    private boolean levelCompleted = false;
    private boolean playerDead = false;
    private boolean endEnabled = false;
    private boolean checkPointEnabled = false;
    private boolean timeEnabled = true;
    private boolean invencibleEnabled = false;
    private boolean debugIA = false;

    private float time2Go = DEFAULT_LEVEL_TIME;
    private final int previousScore;

    public PlatformController(Main game, PlatformMap map, GameVirtualControlsHud controls) {
        this.controls = controls;
        this.game = game;
        this.map = map;
        gameManager = GameManager.getInstance();
        audioManager = AudioManager.getInstance();
        previousScore = gameManager.getScore();

        player = new Nanobot(map, 0, 64);
        startPoint = map.getStartPoint();
        if (startPoint != null) {
            player.updatePosition(startPoint);
        }

        checkPoint = new Checkpoint(map.getCheckPoint(), map.getCheckPointArea());
        pushButton = new PushButton(map.getPushButton());
        door = new Door(map.getDoor());

        enemies = new Array<>(map.getEnemiesStart().size);
        for (PointMapObject object : map.getEnemiesStart()) {
            Enemy.Type eType;
            Enemy e;
            switch (object.getName()) {
                case "Enemy1":
                    e = new DrillEnemy(this, object.getPoint());
                    break;
                case "Enemy2":
                    e = new HunterEnemy(this, map, object.getPoint());
                    break;
                case "Enemy3":
                    eType = Enemy.Type.T3;
                    e = new Enemy(this, object.getPoint(), eType);
                    break;
                default:
                    eType = Enemy.Type.DRILL;
                    e = new Enemy(this, object.getPoint(), eType);
                    Gdx.app.log("PlatformController", "Unknow enemyType: " + object.getName());
            }
            enemies.add(e);
        }
    }

    @Override
    public void startGame() {
        Gdx.app.log("PlatformController", "startGame()");
    }

    @Override
    public void gameOver() {
        Gdx.app.log("PlatformController", "gameOver()");
    }

    @Override
    public void nextLevel() {
        Gdx.app.log("PlatformController", "nextLevel()");
        //TODO: load new map
        gameManager.endGame();
    }

    @Override
    public void resetLevel() {
        Gdx.app.log("PlatformController", "resetLevel()");
        if (!invencibleEnabled) {
            player.updatePosition(startPoint);
        }
        for (Enemy e : enemies) {
            e.isAlive = true;
        }
        time2Go = DEFAULT_LEVEL_TIME;
        gameManager.setScore(previousScore);
        checkPoint.unChecked();
        door.isOpen = false;
        pushButton.isPushed = false;
        levelCompleted = playerDead = checkPointEnabled = endEnabled = false;
    }

    @Override
    public void update(float deltaTime) {
        if (levelCompleted || playerDead) {
            return;
        }

        if (timeEnabled) {
            time2Go -= deltaTime;
            if (time2Go < 0) {
                playerDead();
                return;
            }
            gameManager.setHudMessage(StringFormat.numberZeroLeftPad((int) time2Go, 3));
        }

        handleInput();
        checkSpecialZones();
        
        player.update(deltaTime);        
        checkEnemies(deltaTime);
        checkPoint.update(deltaTime);
        gameManager.setPlayerInfo(player.posVel2String());
    }

    private void checkEnemies(float dt) {
        int c = 0;
        for (Enemy e : enemies) {
            if (e.isAlive) {
                e.update(dt);
                c++;
                if (player.colisionBounds.overlaps(e.colisionBounds)) {
                    Gdx.app.log("PlatformController", "Player overlaps enemy");
                    if (e.type == Enemy.Type.DRILL) {
                        Rectangle intersection = new Rectangle();
                        Intersector.intersectRectangles(player.colisionBounds, e.colisionBounds, intersection);
                        boolean verticalCol = intersection.width > intersection.height;
                        boolean isAbove = player.getPosition().y > e.position.y;
                        if (verticalCol && isAbove && player.isFalling) {
                            enemyDie(e);
                        } else {
                            playerDead();
                        }
                    } else if (player.isFalling) {
                        enemyDie(e);
                    } else {
                        playerDead();
                    }
                }
            }
        }

        checkPointEnabled = c == 0;
    }

    private void enemyDie(Enemy e) {
        e.died();
        //TODO: read point and time from enemy
        gameManager.updateScore(1000);
        time2Go += 10;
        audioManager.playSound(AssetPaths.EXPLOSION_SOUND);
    }

    private void checkSpecialZones() {
        if (map.inDeadZone(player.colisionBounds)) {
            Gdx.app.log("PlatformController", "Player Dead");
            playerDead();
        } else if (map.inTransporter(player.colisionBounds)) {
            float x = player.velocity.x;
            player.setVelocity(x == 0 ? 100 : x, player.velocity.y);
            player.isGrounded = true;
        } else if(map.inPushButtonAction(player.colisionBounds)){
            pushButton.isPushed = true;
            map.doorOpen = door.isOpen = true;
            player.slowFallingZone = player.isFalling;
        } else if (checkPointEnabled && checkPoint.state == Checkpoint.State.NO_CHECK
                && (player.colisionBounds.contains(checkPoint.position)
                || player.colisionBounds.overlaps(checkPoint.colisionBounds))) {
            Gdx.app.log("PlatformController", "Player open check point");
            checkPoint.checked();
        } else if (checkPoint.state == Checkpoint.State.OPENED
                && map.getFinishArea().contains(player.colisionBounds)) {
            Gdx.app.log("PlatformController", "Player on final zone");
            endEnabled = true;
        } else {
            endEnabled = false;
        }

    }

    private void endGame() {
        Gdx.app.log("PlatformController", "endGame()");
        gameManager.endGame();
    }

    private void levelComplete() {
        Gdx.app.log("PlatformController", "levelcomplete()");
        gameManager.updateScore(2000);
        levelCompleted = true;
        audioManager.playSound(AssetPaths.POWERUP_SOUND);
        gameManager.levelCompleted();
    }

    private void playerDead() {
        Gdx.app.log("PlatformController", "playerDead()");
        if (!invencibleEnabled) {
            playerDead = true;
            audioManager.playSound(AssetPaths.EVIL_LAUGH_SOUND);
            gameManager.losesLifes();
        }
    }

    private void jump(float s) {
        if (player.isGrounded && s > 0) {
            player.velocity.y = s;
            player.isGrounded = false;
            audioManager.playSound(AssetPaths.JUMP_SOUND);
        }
    }

    private void moveHorizontal(float step) {
        if (step == 0) {
            if (player.velocity.x == 0) {
                return;
            }
            if (map.inIceFloor(player.bounds)) {
                float ns = player.velocity.x * 0.99f;
                player.velocity.x = Math.abs(ns) > 0.1 ? ns : 0f;
            } else {
                player.velocity.x = 0;
            }
        } else {
            player.velocity.x += step;
            if (Math.abs(player.velocity.x) > Nanobot.MAX_SPEED) {
                player.velocity.x = Nanobot.MAX_SPEED * Math.signum(player.velocity.x);
            }
        }
    }

    private void handleInput() {
        float vSpeed = 0;
        float hSpeed = 0;
        player.isRunning = false;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || controls.isRightPressed) {
            hSpeed += Nanobot.SPEED;
            player.isFacinRight = true;
            player.isRunning = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || controls.isLeftPressed) {
            hSpeed -= Nanobot.SPEED;
            player.isFacinRight = false;
            player.isRunning = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)
                || Gdx.input.isKeyPressed(Input.Keys.SPACE)
                || controls.isJumpPressed) {
            vSpeed += Nanobot.JUMP_FORCE;
        }
        if (endEnabled && (Gdx.input.isKeyPressed(Input.Keys.DOWN) ||
                controls.isDownPressed)) {
            Gdx.app.log("PlatformController", "Player win");
            levelComplete();
        }

        moveHorizontal(hSpeed);
        jump(vSpeed);
    }

    @Override
    public void dispose() {
        for (Enemy e : enemies) {
            e.dispose();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean processed = false;
        switch (keycode) {
            case Input.Keys.R:
                resetLevel();
                processed = true;
                break;
            case Input.Keys.N:
                break;
            case Input.Keys.H:
                mapEnabled = !mapEnabled;
                processed = true;
                break;
            case Input.Keys.K:
                for (Enemy e : enemies) {
                    if (e.isAlive) {
                        e.isAlive = false;
                        checkPointEnabled = true;
                        audioManager.playSound(AssetPaths.EXPLOSION_SOUND);
                    }
                }
                processed = true;
                break;
            case Input.Keys.X:
                invencibleEnabled = !invencibleEnabled;
                processed = true;
                break;
            case Input.Keys.B:
                bgEnabled = !bgEnabled;
                processed = true;
                break;
            case Input.Keys.G:
                gridEnabled = !gridEnabled;
                processed = true;
                break;
            case Input.Keys.T:
                timeEnabled = !timeEnabled;
                processed = true;
                break;
            case Input.Keys.Y:
                debugIA = !debugIA;
                processed = true;
                break;
            default:
                Gdx.app.debug("PlatformController", "Keycode: " + keycode);
        }
        return processed;
    }

// <editor-fold desc="Unused InputProcessor methods">     
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
// </editor-fold>    

// <editor-fold desc="Setters/Getters">    
    public Nanobot getPlayer() {
        return player;
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public boolean isMapEnabled() {
        return mapEnabled;
    }

    public boolean isBgEnabled() {
        return bgEnabled;
    }

    public boolean isGridEnabled() {
        return gridEnabled;
    }

    public boolean isCheckPointEnabled() {
        return checkPointEnabled;
    }

    public boolean isLevelCompleted() {
        return levelCompleted;
    }

    public boolean isPlayerDead() {
        return playerDead;
    }

    public Checkpoint getCheckPoint() {
        return checkPoint;
    }

    public boolean isEndEnabled() {
        return endEnabled;
    }
    
    public Door getDoor(){
        return door;
    }
    
    public PushButton getPushButton(){
        return pushButton;
    }
    
    public boolean isDebugIA(){
        return debugIA;
    }

// </editor-fold>     
}
