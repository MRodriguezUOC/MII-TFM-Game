package edu.uoc.mii.score;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 *
 * @author Marco Rodriguez
 */
public class ScoreEntry implements Json.Serializable{
    private long id;
    private String username;
    private int points;
    private String playedAt;
    
    public ScoreEntry(){}
    
    public ScoreEntry(String username, int points) {        
        this.username = username;
        this.points = points;
    }    

    public ScoreEntry(long id, String username, int points, String playedAt) {
        this(username, points);
        this.id = id;
        this.playedAt = playedAt;
    }
    
    @Override
    public void write(Json json) {
        json.writeValue("id", id);
        json.writeValue("username", username);
        json.writeValue("points", points);
        json.writeValue("playedAt", playedAt);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        if (jsonData.has("id")) {
            this.id = jsonData.getLong("id"); 
        }
        
        this.username = jsonData.getString("username");
        this.points = jsonData.getInt("points");
        
        this.playedAt = jsonData.getString("playedAt", "Sin fecha");
    }    
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(String playedAt) {
        this.playedAt = playedAt;
    }

    void addPoints(int points) {
        this.points += points;
    }
    
}
