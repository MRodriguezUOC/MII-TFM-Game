package edu.uoc.mii;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import edu.uoc.mii.audio.AudioManager;
import edu.uoc.mii.conf.PreferenceManager;
import edu.uoc.mii.score.ScoreManager;
import edu.uoc.mii.screen.ScreenManager;
import edu.uoc.mii.screen.ScreenType;

/**
 *
 * @author marco
 */
public class GameManager implements InputProcessor {

    private final Main game;
    private int lives = 0;
    private int currentLevel = 0;
    private int currentScore = 0;
    private boolean showFPS = false;
    private boolean showPPV = false;
    private boolean showHUD = true;
    private boolean debug = false;
    private boolean debugUI = false;
    private String hudMessage;
    private String playerInfo;
    private GameEventLintener listener;

    private final ScoreManager scoreManager = ScoreManager.getInstance();
    private final AudioManager audioManager = AudioManager.getInstance();

    private static GameManager instance;

    public static void initialize(Main game) {
        if (instance == null) {
            instance = new GameManager(game);
        }
    }

    public static GameManager getInstance() {
        if (instance == null) {
            Gdx.app.error("GameManager", "Instance not initiliced");
            throw new IllegalStateException("Instance not initialiced");
        }
        return instance;
    }

    private GameManager(Main game) {
        this.game = game;
    }

    public void startGame() {
        startGame(1);
    }

    public void startGame(int level) {
        lives = 3;
        currentLevel = level;
        currentScore = 0;
        hudMessage = "";
        scoreManager.newScore();
        nextGame(currentLevel);
    }

    private void endGame(boolean showLeaderBoard) {
        listener.onGameOver(() -> {
            if (showLeaderBoard) {
                ScreenManager.getInstance().setScreen(ScreenType.LEADERBOARD);
            } else {
                ScreenManager.getInstance().setScreen(ScreenType.MAIN_MENU);
            }
        });
    }

    public void endGame() {
        listener.onEndGame(() -> nextGame());
    }
    
    public void exitGame() {
        submitScore();
    }

    public void nextGame() {
        //TODO: while not have more games, submit score
        submitScore();
        currentLevel++;
        nextGame(currentLevel);
    }

    private void nextGame(int level) {
        switch (level) {
            case 1:
                ScreenManager.getInstance().setScreen(ScreenType.PUZLE_START);
                break;
            case 2:
                ScreenManager.getInstance().setScreen(ScreenType.PLATFORM_START);
                break;
            case 3:
                ScreenManager.getInstance().setScreen(ScreenType.NEXT_INTRO);
                break;
            default:
                gameOver();
        }
    }

    public void levelCompleted() {
        listener.onLevelCompleted();
    }

    public void gameOver() {
        submitScore();
    }

    private void submitScore() {
        if (currentScore > 0) {
            scoreManager.addPoint(currentScore);
            scoreManager.submitScore(new ScoreManager.Callback() {
                @Override
                public void onSuccess(String message) {
                    GameManager.this.endGame(true);
                }

                @Override
                public void onError(String message) {
                    GameManager.this.endGame(false);
                }
            });
        } else {
            endGame(false);
        }

    }

    public void addLives(int lives) {
        if (lives < 0) {
            Gdx.app.error("GameManager", "lives can't be negative: " + lives);
        }
        this.lives += lives;
    }

    public void losesLifes() {
        lives--;

        if (lives <= 0) {
            gameOver();
        } else {
            listener.onPlayerDead();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean processed = false;
        switch (keycode) {
            case Input.Keys.ESCAPE:
                endGame();
                processed = true;
                break;
            case Input.Keys.H:
                showHUD = !showHUD;
                processed = true;
                break;
            case Input.Keys.F:
                showFPS = !showFPS;
                processed = true;
                break;
            case Input.Keys.I:
                showPPV = !showPPV;
                processed = true;
                break;
            case Input.Keys.J:
                debugUI = !debugUI;
                processed = true;
                break;
            case Input.Keys.D:
                debug = !debug;
                if (debug) {
                    Gdx.app.setLogLevel(Application.LOG_DEBUG);
                } else {
                    Gdx.app.setLogLevel(Application.LOG_INFO);
                }
                processed = true;
                break;
            case Input.Keys.M:
                if (audioManager.isMusicEnabled()) {
                    audioManager.pauseMusic();
                    audioManager.setMusicEnabled(false);
                } else {
                    audioManager.playMusic();
                    audioManager.setMusicEnabled(true);
                }
                processed = true;
                break;
            case Input.Keys.S:
                audioManager.setSoundEnabled(!audioManager.isSoundEnabled());
                processed = true;
                break;
            case Input.Keys.L:
                addLives(1);
                processed = true;
                break;                
            default:
                Gdx.app.debug("GameManager", "Keycode: " + keycode);
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
    public int getLives() {
        return lives;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    //TODO: Delete score from GameManager? set only on ScoreController?
    public int updateScore(int val) {
        currentScore += val;
        return currentScore;
    }

    public int getScore() {
        return currentScore;
    }

    public void setScore(int val) {
        currentScore = val;
    }

    public String getHudMessage() {
        return hudMessage;
    }

    public void setHudMessage(String hudMessage) {
        this.hudMessage = hudMessage;
    }

    public GameEventLintener getListener() {
        return listener;
    }

    public void setListener(GameEventLintener listener) {
        this.listener = listener;
    }

    public boolean isShowFPS() {
        return showFPS;
    }

    public void setShowFPS(boolean val) {
        showFPS = val;
    }

    public boolean isShowPPV() {
        return showPPV;
    }

    public void setShowPPV(boolean showPPV) {
        this.showPPV = showPPV;
    }

    public String getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(String playerInfo) {
        this.playerInfo = playerInfo;
    }

    public boolean isShowHUD() {
        return showHUD;
    }

    public void setShowHUD(boolean showHUD) {
        this.showHUD = showHUD;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isDebugUI() {
        return debugUI;
    }

    public boolean showVirtualControls() {
        if(PreferenceManager.getInstance().isForceVirtualControls()){
            return true;
        }
        
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            return false;
        }
    
        return !Gdx.input.isPeripheralAvailable(Input.Peripheral.HardwareKeyboard);
    }

// </editor-fold>      

}
