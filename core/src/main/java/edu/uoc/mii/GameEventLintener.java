package edu.uoc.mii;

/**
 *
 * @author Marco Rodriguez
 */
public interface GameEventLintener {
    void onPlayerDead();
    void onLevelCompleted();
    void onEndGame(Runnable onSuccess);
    void onGameOver(Runnable onSuccess);
}
