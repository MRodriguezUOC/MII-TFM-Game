package edu.uoc.mii.audio;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import edu.uoc.mii.conf.PreferenceManager;

/**
 *
 * @author Marco Rodriguez
 */
public class AudioManager {

    private static AudioManager instance;
    private AssetManager assetManager;

    private float soundVolume = 1.0f;
    private float musicVolume = 0.8f;
    private boolean soundEnabled = true;
    private boolean musicEnabled = true;

    private Music currentMusic;
    private final Array<AssetDescriptor<Music>> playlist;
    private int currentSong;
    private long currentSoundId;
    private Music currentSound;

    private AudioManager() {
        playlist = new Array<>(5);
        currentSong = -1;
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void initialize(AssetManager assetManager) {
        this.assetManager = assetManager;
        soundEnabled = PreferenceManager.getInstance().isSoundEnable();
        musicEnabled = PreferenceManager.getInstance().isMusicEnable();
    }

    public void playMusic(AssetDescriptor<Music> descriptor, boolean loop) {
        Gdx.app.log("AudioManager", "Request: " + descriptor.toString());

        if (assetManager.isLoaded(descriptor.fileName)) {
            Gdx.app.log("AudioManager", "Playing: " + descriptor.toString());
            Music newMusic = assetManager.get(descriptor);
            if (currentMusic == newMusic && currentMusic.isPlaying()) {
                return;
            }

            if (currentMusic != null) {
                currentMusic.stop();
            }

            currentMusic = newMusic;
            currentMusic.setVolume(musicVolume);
            currentMusic.setLooping(loop);
            if (!loop) {
                currentMusic.setOnCompletionListener((Music music) -> {
                    nextSong();
                });
            }
            if (musicEnabled) {
                currentMusic.play();
            }
        }
    }

    public void clearPlaylist() {
        playlist.clear();
    }

    public void addPlaylist(AssetDescriptor<Music> descriptor) {
        playlist.add(descriptor);
    }

    public void startPlaylist() {
        startPlaylist(false);
    }

    public void startPlaylist(boolean randomStart) {
        if (playlist.size > 0) {
            if (randomStart) {
                currentSong = MathUtils.random(0, playlist.size - 1);
            } else {
                currentSong = 0;
            }
            //TODO: workarround, on web only sound one song from playlist.
            boolean loop = false;
            if (Gdx.app.getType() == Application.ApplicationType.WebGL){
                loop = true;
            }            
            playMusic(playlist.get(currentSong), loop);
        }
    }

    public void nextSong() {
        Gdx.app.log("AudioManager", "Next song, current: " + currentSong);
        if (playlist.size > 0) {
            currentSong++;
            if (currentSong >= playlist.size) {
                currentSong = 0;
            }
            playMusic(playlist.get(currentSong), false);
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void pauseMusic() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    public void playMusic() {
        if (currentMusic != null) {
            currentMusic.play();
        }
    }

    public void playSound(AssetDescriptor<Sound> descriptor) {
        if (!soundEnabled) {
            return;
        }

        Gdx.app.log("AudioManager", "Request: " + descriptor.toString());

        if (assetManager.isLoaded(descriptor.fileName)) {
            Gdx.app.log("AudioManager", "Playing: " + descriptor.toString());
            Sound sound = assetManager.get(descriptor);
            sound.play(soundVolume);
        }
    }
    
    public void playOneSound(AssetDescriptor<Music> descriptor) {
        if (!soundEnabled) {
            return;
        }
        Gdx.app.log("AudioManager", "Request: " + descriptor.toString());

        if (assetManager.isLoaded(descriptor.fileName)) {
            if(currentSound != null && currentSound.isPlaying()){
                return;
            }
            Gdx.app.log("AudioManager", "Playing: " + descriptor.toString());
            
            currentSound = assetManager.get(descriptor);
            currentSound.play();
        }
    }

    public void playSoundWithPitch(AssetDescriptor<Sound> descriptor, float minPitch, float maxPitch) {
        if (!soundEnabled) {
            return;
        }

        if (assetManager.isLoaded(descriptor.fileName)) {
            Sound sound = assetManager.get(descriptor);
            float randomPitch = MathUtils.random(minPitch, maxPitch);

            sound.play(soundVolume, randomPitch, 0);
        }
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        if (currentMusic != null) {
            currentMusic.setVolume(volume);
        }
    }

    public void setSoundVolume(float volume) {
        this.soundVolume = volume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }

}
