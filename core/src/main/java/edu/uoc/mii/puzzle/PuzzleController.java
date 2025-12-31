package edu.uoc.mii.puzzle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import edu.uoc.mii.GameController;
import edu.uoc.mii.GameManager;
import edu.uoc.mii.Main;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.audio.AudioManager;
import edu.uoc.mii.utils.Config;

/**
 *
 * @author Marco Rodriguez
 */
public class PuzzleController implements InputProcessor, GameController {

    private final Main game;
    private final GameManager gameManager;
    private final AudioManager audioManager;

    private PuzzleBoard board;
    private PuzzlePiece selectedPiece = null;

    private boolean levelCompleted = false;
    private boolean grayEnabled = false;
    private boolean mapEnabled = false;
    private boolean bgEnabled = true;
    private boolean debug = false;

    private int difficulty;
    private int currentMoves;
    private int minMoves;
    private int maxMoves;
    private int maxLevels;
    private int currentLevel;

    private Callback callback;

    public interface Callback {

        public void onNewBoard();
    }

    public PuzzleController(Main game) {
        this.game = game;
        gameManager = GameManager.getInstance();
        audioManager = AudioManager.getInstance();

        init();
    }

    private void init() {
        Config conf = new Config("level1/config.properties");

        setDifficulty(conf.getInt("difficulty", 1));
        currentLevel = 0;
        maxLevels = conf.getInt("maxLevels", 1);
    }

    @Override
    public void startGame() {
        currentLevel = 1;
        newBoard();
    }

    @Override
    public void gameOver() {
    }

    @Override
    public void nextLevel() {
        currentLevel++;
        if (currentLevel <= maxLevels) {
            gameManager.levelCompleted();
            setDifficulty(difficulty + 1);
            newBoard();
            levelCompleted = false;
        } else {
            endGame();
        }
    }

    @Override
    public void resetLevel() {
        if (!gameManager.isGameOver()) {
            board.shufflePieces();
            minMoves = solve().size;
            maxMoves = (int) (minMoves * 1.50f);
            setCurrentMoves(0);
        }
    }

    @Override
    public void update(float delta) {
    }

    public void endGame() {
        gameManager.endGame();
    }

    private void newBoard() {
        board = new PuzzleBoard(difficulty + 1, difficulty + 1);
        if (callback != null) {
            callback.onNewBoard();
        }

        resetLevel();
    }

    private void onLevelComplete() {
        Gdx.app.debug("PuzzleController", "¡Nivel Completado!");
        levelCompleted = true;
        // Reset the selection so that no "blue" piece remains.
        selectedPiece = null;
        updateScore();

        audioManager.playSound(AssetPaths.POWERUP_SOUND);
        nextLevel();
    }

    private void updateScore() {
        int p = 1000 * difficulty; //1000 -> 3000 -> 6000
        Gdx.app.log("PuzzleController", "Level points: " + p);
        p -= (currentMoves - minMoves) * 200;
        if (p < 0) {
            p = 0;
        }
        Gdx.app.log("PuzzleController", "Final level points: " + p);
        gameManager.updateScore(p);
    }

    public void pieceClick(int row, int col) {
        Gdx.app.debug("PuzzleController", "pieceClick: r" + row + ", c" + col);
        PuzzlePiece clickedPiece = board.getPieceAt(row, col);

        if (clickedPiece == null) {
            return;
        }

        if (selectedPiece == null) {
            selectedPiece = clickedPiece;
            audioManager.playSound(AssetPaths.MENUIN_SOUND);
        } else {
            if (selectedPiece == clickedPiece) {
                selectedPiece = null;
                audioManager.playSound(AssetPaths.MENUOUT_SOUND);
            } else {
                board.swapPieces(selectedPiece, clickedPiece);
                audioManager.playSound(AssetPaths.CANCEL_SOUND);
                incrementCurrentMoves();
                selectedPiece = null;
                if (board.checkWinCondition()) {
                    onLevelComplete();
                } else if (currentMoves >= maxMoves) {
                    gameManager.losesLifes();
                    audioManager.playSound(AssetPaths.EVIL_LAUGH_SOUND);
                    resetLevel();
                }
            }
        }
    }

    public Array<PuzzleMove> solve() {
        Gdx.app.log("PuzzleController", "Solve, size: " + board.getPieces().size);
        int[] sBoard = new int[board.getPieces().size];

        PuzzlePiece p;
        int i = 0;
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                p = board.getPieceAt(row, col);
                sBoard[i] = p.id;
                i++;
                Gdx.app.log("PuzzleController", "PieceID: " + p.id);
            }
        }

        Array<PuzzleMove> solution = PuzzleSolver.solve(sBoard);
        Gdx.app.log("PuzzleController", "Movimientos necesarios: " + solution.size);
        for (PuzzleMove move : solution) {
            Gdx.app.log("PuzzleController", "Mov: " + move.toString());
        }

        return solution;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean processed = false;
        switch (keycode) {
            case Input.Keys.ESCAPE:
                endGame();
                processed = true;
                break;
            case Input.Keys.N:
                nextLevel();
                processed = true;
                break;
            case Input.Keys.U:
                if (difficulty < 10) {
                    difficulty++;
                    newBoard();
                }
                processed = true;
                break;
            case Input.Keys.B:
                bgEnabled = !bgEnabled;
                processed = true;
                break;
            case Input.Keys.G:
                grayEnabled = !grayEnabled;
                processed = true;
                break;
            case Input.Keys.H:
                mapEnabled = !mapEnabled;
                processed = true;
                break;
            case Input.Keys.R:
                resetLevel();
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
            default:
                Gdx.app.debug("PuzzleController", "Keycode: " + keycode);
        }
        return processed;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

// <editor-fold desc="Unused InputProcessor methods">     
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

// <editor-fold desc="Getters and Setters">    
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        if (difficulty < 4) {
            grayEnabled = true;
        }
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public PuzzleBoard getBoard() {
        return board;
    }

    public PuzzlePiece getSelectedPiece() {
        return selectedPiece;
    }

    public boolean isLevelCompleted() {
        return levelCompleted;
    }

    public boolean isGrayEnabled() {
        return grayEnabled;
    }

    public boolean isMapEnabled() {
        return mapEnabled;
    }

    public boolean isBGEnabled() {
        return bgEnabled;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setCurrentMoves(int i) {
        currentMoves = i;
        gameManager.setHudMessage("Movimientos: " + currentMoves + "/" + maxMoves);
    }

    public void incrementCurrentMoves() {
        setCurrentMoves(currentMoves + 1);
    }

// </editor-fold>
}
