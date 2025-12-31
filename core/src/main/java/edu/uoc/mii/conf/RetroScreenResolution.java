package edu.uoc.mii.conf;

/**
 * RestroScreen contains the measurements of the screen.
 *
 * @author Marco Rodriguez
 */

public enum RetroScreenResolution{
    D4_3(320,240),
    SPECTRUM(256, 192),
    NES(256, 240),
    A500(320, 256),
    PC_CGA(320, 200),
    PC_VGA(640, 480);
    
    private final int width;
    private final int height;

    private RetroScreenResolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
        
}
