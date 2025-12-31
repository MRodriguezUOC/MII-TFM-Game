package edu.uoc.mii.conf;

import edu.uoc.mii.utils.SecurityUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 *
 * @author Marco Rodriguez
 */
public class PreferenceManager {
    private static final String PREFS_NAME = "edu.uoc.mii.IAsRevenge";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String VALID_CREDENTIALS = "validCredentials";
    private static final String SKIP_INTRO = "skipIntro";
    private static final String SKIP_PUZZLE_INTRO = "skipPuzzleIntro";
    private static final String SKIP_PLATFORM_INTRO = "skipPlatformIntro";
    private static final String DEBUG_ENABLED = "debugEnabled";
    private static final String MUSIC_ENABLED = "musicEnabled";
    private static final String SOUND_ENABLED = "soundEnabled";
    private static final String FORCE_VIRTUAL_CONTROLS = "forceVirtualControls";
    private static final String L2_START_X = "level2.start.x";
    private static final String L2_START_Y = "level2.start.y";
    private static final int KEY_LENGTH = 32;
    private final Preferences prefs;
    
    private static PreferenceManager instance;
    
    public static PreferenceManager getInstance(){
        if(instance == null){
            instance = new PreferenceManager();
        }
        
        return instance;
    }
    
    private PreferenceManager(){
        prefs = Gdx.app.getPreferences(PREFS_NAME);        
    }
    
    public String getUsername(){
        return prefs.getString(USERNAME);
    }
    
    public void setUsernamePassword(String username, String password){
        String key = SecurityUtils.generateKey(username, KEY_LENGTH);
        SecurityUtils su = new SecurityUtils(key);                
        prefs.putString(USERNAME, username);
        prefs.putString(PASSWORD, su.encrypt(password));
        prefs.flush();
    }
    
    public String getPassword(){
        String username = this.prefs.getString(USERNAME);
        String key = SecurityUtils.generateKey(username, KEY_LENGTH);
        SecurityUtils su = new SecurityUtils(key);
        return su.decrypt(prefs.getString(PASSWORD));
    }
    
    public boolean haveValidCredentials(){
        return prefs.getBoolean(VALID_CREDENTIALS);        
    }
    
    public void setValidCredentials(boolean isValid){
        prefs.putBoolean(VALID_CREDENTIALS, isValid);
        prefs.flush();
    }

    public boolean skipIntro() {
        return prefs.getBoolean(SKIP_INTRO, false);
    }
    
    public void setSkipIntro(boolean val){
        prefs.putBoolean(SKIP_INTRO, val);
        prefs.flush();
    }

    public boolean idDebugEnabled() {        
        return prefs.getBoolean(DEBUG_ENABLED, false);
    }

    public boolean skipPuzzleIntro() {
        return prefs.getBoolean(SKIP_PUZZLE_INTRO, false);
    }

    public void setSkipPuzzleIntro(boolean val) {
        prefs.putBoolean(SKIP_PUZZLE_INTRO, val);
        prefs.flush();
    }
    
    public boolean skipPlatformIntro() {
        return prefs.getBoolean(SKIP_PLATFORM_INTRO, false);
    }

    public void setSkipPlatformIntro(boolean val) {
        prefs.putBoolean(SKIP_PLATFORM_INTRO, val);
        prefs.flush();
    }
    
    public boolean isMusicEnable() {
        return prefs.getBoolean(MUSIC_ENABLED, true);
    }

    public void setMusicEnable(boolean val) {
        prefs.putBoolean(MUSIC_ENABLED, val);
        prefs.flush();
    }
    
    public boolean isSoundEnable() {
        return prefs.getBoolean(SOUND_ENABLED, true);
    }

    public void setSouncEnable(boolean val) {
        prefs.putBoolean(SOUND_ENABLED, val);
        prefs.flush();
    }
    
    public float getLevel2StartX(){
        return prefs.getFloat(L2_START_X, -1);
    }
    
    public float getLevel2StartY(){
        return prefs.getFloat(L2_START_Y, -1);
    }
    
    public boolean isForceVirtualControls(){
        return prefs.getBoolean(FORCE_VIRTUAL_CONTROLS, false);
    }
    
}
