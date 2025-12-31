package edu.uoc.mii.intro;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;

/**
 *
 * @author Marco Rodriguez
 */
public class SlideData {

    public final String imageName;
    public String musicName;
    public final String text;
    public boolean haveMusic;
    public boolean haveImage;

    public SlideData(String blockData, String text) {
        String[] data = blockData.split(";");
        this.text = text;
        imageName = data[0].trim();
        haveImage = imageName.length() > 0;
        if (data.length >= 2) {
            musicName = data[1].trim();
            haveMusic = musicName.length() > 0;
        }
    }

    public AssetDescriptor<Music> getMusicDescriptor() {
        return new AssetDescriptor<>(musicName, Music.class);
    }

    public AssetDescriptor<Texture> getImageDescriptor() {
        return new AssetDescriptor<>(imageName, Texture.class);
    }

}
