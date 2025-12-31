package edu.uoc.mii.score;

/**
 *
 * @author Marco Rodriguez
 */
public class ScoreSubmission {
    public String username;
    public int points;

    public ScoreSubmission() {
    }

    public ScoreSubmission(String username, int points) {
        this.username = username;
        this.points = points;
    }
}
