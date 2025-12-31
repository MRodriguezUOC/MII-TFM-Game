package edu.uoc.mii;

/**
 *
 * @author Marco Rodriguez
 */
public interface GameController {
    public void startGame();
    public void gameOver();
    public void resetLevel();
    public void nextLevel();
    public void update(float delta);
}
