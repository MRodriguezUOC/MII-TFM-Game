package edu.uoc.mii.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 *
 * @author Marco Rodriguez
 */
public class BitmapFontUtils {

    public static BitmapFont createPixelFont(String fontPath, int size) {
        //NOT Work on WEB target
//        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
//        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
//
//        parameter.size = size; 
//
//        parameter.minFilter = Texture.TextureFilter.Nearest;
//        parameter.magFilter = Texture.TextureFilter.Nearest;
//
//        BitmapFont font = generator.generateFont(parameter);
//        generator.dispose(); 
//
//        return font;
        return null;
    }
    
}
