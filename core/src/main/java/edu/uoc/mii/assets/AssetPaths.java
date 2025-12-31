package edu.uoc.mii.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 *
 * @author Marco Rodriguez
 */
public class AssetPaths {    
// <editor-fold desc="UIs"> 
    public static final AssetDescriptor<Skin> UI_SKIN = 
        new AssetDescriptor<>("ui/ui.json", Skin.class);
    public static final AssetDescriptor<Skin> C64_SKIN = 
        new AssetDescriptor<>("ui/C64/uiskin.json", Skin.class);
// </editor-fold>     
// <editor-fold desc="Fonts"> 
    public static final AssetDescriptor<BitmapFont> C64_FONT = 
        new AssetDescriptor<>("ui/C64/commodore-64.fnt", BitmapFont.class);
    public static final AssetDescriptor<BitmapFont> ZXSPECTRUM_FONT = 
        new AssetDescriptor<>("ui/ZXSpectrum7-10.fnt", BitmapFont.class);
    public static final AssetDescriptor<BitmapFont> GAMEPLAY_FONT = 
        new AssetDescriptor<>("ui/Gameplay.fnt", BitmapFont.class);
    public static final AssetDescriptor<BitmapFont> PRESS_START2P_FONT = 
        new AssetDescriptor<>("ui/PressStart2P-Regular.fnt", BitmapFont.class);
// </editor-fold>     
// <editor-fold desc="Maps"> 
    public static final AssetDescriptor<TiledMap> LEVEL2_MAP1 = 
        new AssetDescriptor<>("level2/map1.tmx", TiledMap.class);    
// </editor-fold>     
// <editor-fold desc="Atlas"> 
    public static final AssetDescriptor<TextureAtlas> INDUSTRIAL_ATLAS = 
        new AssetDescriptor<>("level2/Industrial.atlas", TextureAtlas.class);
    public static final AssetDescriptor<TextureAtlas> NES_START_ATLAS = 
        new AssetDescriptor<>("level2/nes_start.atlas", TextureAtlas.class);
    public static final AssetDescriptor<TextureAtlas> LEVEL2_ATLAS = 
        new AssetDescriptor<>("level2/level2.atlas", TextureAtlas.class);
// </editor-fold>     
// <editor-fold desc="Backgrounds"> 
    public static final AssetDescriptor<Texture> MAIN_MENU_BG = 
        new AssetDescriptor<>("01-Portada.png", Texture.class);
    public static final AssetDescriptor<Texture> LEVEL2_BG = 
        new AssetDescriptor<>("level2/Background.png", Texture.class);
    public static final AssetDescriptor<Texture> L1_GAME_INVFOR_1_BG = 
        new AssetDescriptor<>("level1/ZX81Game-InvasionForce-1.png", Texture.class);
    public static final AssetDescriptor<Texture> L1_GAME_INVFOR_2_BG = 
        new AssetDescriptor<>("level1/ZX81Game-InvasionForce-2.png", Texture.class);
    public static final AssetDescriptor<Texture> L1_ZX_Loading_BG = 
        new AssetDescriptor<>("level1/ZX-Loaading.png", Texture.class);
    public static final AssetDescriptor<Texture> NEXTINTRO_BG = 
        new AssetDescriptor<>("02-NextIntro.png", Texture.class);
// </editor-fold>     
// <editor-fold desc="Sounds"> 
    public static final AssetDescriptor<Sound> JUMP_SOUND =
        new AssetDescriptor<>("sound/jump.wav", Sound.class);
    public static final AssetDescriptor<Sound> COIN_SOUND =
        new AssetDescriptor<>("sound/162805__timgormly__8-bit-coin11.wav", Sound.class);
    public static final AssetDescriptor<Sound> HURT_SOUND =
        new AssetDescriptor<>("sound/hurt.wav", Sound.class);
    public static final AssetDescriptor<Sound> EXPLOSION_SOUND =
        new AssetDescriptor<>("sound/explosion.wav", Sound.class);
    public static final AssetDescriptor<Sound> EVIL_LAUGH_SOUND =
        new AssetDescriptor<>("sound/Evil_Laugh.wav", Sound.class);
    public static final AssetDescriptor<Music> TRAMPOLINE_SOUND =
        new AssetDescriptor<>("sound/Trampoline.wav", Music.class);
    public static final AssetDescriptor<Sound> POWERUP_SOUND =
        new AssetDescriptor<>("sound/Powerup.wav", Sound.class);
    public static final AssetDescriptor<Sound> MENUIN_SOUND =
        new AssetDescriptor<>("sound/Menu_In.wav", Sound.class);
    public static final AssetDescriptor<Sound> MENUOUT_SOUND =
        new AssetDescriptor<>("sound/Menu_Out.wav", Sound.class);
    public static final AssetDescriptor<Sound> CANCEL_SOUND =
        new AssetDescriptor<>("sound/Cancel.wav", Sound.class);
    public static final AssetDescriptor<Music> ZX_SPECTRUM_TAPE =
        new AssetDescriptor<>("sound/zx-spectrum-reading-a-tape.wav", Music.class);
    public static final AssetDescriptor<Sound> TYPEWRITER_KEY =
        new AssetDescriptor<>("sound/TypewriterKey.wav", Sound.class);
    public static final AssetDescriptor<Sound> TYPEWRITER_SPACE =
        new AssetDescriptor<>("sound/TypewriterSpace.wav", Sound.class);
    public static final AssetDescriptor<Sound> TYPEWRITER_BELL =
        new AssetDescriptor<>("sound/TypewriterBell.wav", Sound.class);
    public static final AssetDescriptor<Music> MORSE_DOT =
        new AssetDescriptor<>("sound/morse-dot.wav", Music.class);
    public static final AssetDescriptor<Music> MORSE_DASH =
        new AssetDescriptor<>("sound/morse-dash.wav", Music.class);
    public static final AssetDescriptor<Sound> NET_START_SHUTTER =
        new AssetDescriptor<>("sound/NES-Start-shutter.wav", Sound.class);
    public static final AssetDescriptor<Sound> BEEP_CENSORSHIP =
        new AssetDescriptor<>("sound/BEEP_Censorship.wav", Sound.class);
    public static final AssetDescriptor<Sound> INTERNET_CONNECTION =
        new AssetDescriptor<>("sound/InternetConnection.wav", Sound.class);
// </editor-fold>     
// <editor-fold desc="Music"> 
    public static final AssetDescriptor<Music> TRAP_LOOP =
        new AssetDescriptor<>("music/539860__yipyep__arcade-trap-loop.wav", Music.class);
    public static final AssetDescriptor<Music> HORROR_LOOP2 =
        new AssetDescriptor<>("music/530277__mrthenoronha__horror-theme-loop-2.wav", Music.class);
    public static final AssetDescriptor<Music> GAMEMUSIC_LOOP =
        new AssetDescriptor<>("music/684511__seth_makes_sounds__simple-game-music-loop.wav", Music.class);
    public static final AssetDescriptor<Music> FUNNY_8BITS =
        new AssetDescriptor<>("music/623085__imataco__funny-8-bit-song.wav", Music.class);
    public static final AssetDescriptor<Music> HAPPY_8BITS =
        new AssetDescriptor<>("music/616329__ash_rez__happy-8-bit-music.mp3", Music.class);
    public static final AssetDescriptor<Music> RETRO_BEAT =
        new AssetDescriptor<>("music/625166__seth_makes_sounds__retro-beat.wav", Music.class);
    public static final AssetDescriptor<Music> BG_MUSIC =
        new AssetDescriptor<>("music/684184__seth_makes_sounds__some-game-background-music-or-something.wav", Music.class);
    public static final AssetDescriptor<Music> EDM_MUSIC =
        new AssetDescriptor<>("music/663444__seth_makes_sounds__chiptune-edm-song.wav", Music.class);
    public static final AssetDescriptor<Music> BOS_MUSIC =
        new AssetDescriptor<>("music/683457__seth_makes_sounds__dope-video-game-boss-music.wav", Music.class);
    public static final AssetDescriptor<Music> GM120BPM_MUSIC =
        new AssetDescriptor<>("music/713035__seth_makes_sounds__some-game-music-120bpm.wav", Music.class);
    public static final AssetDescriptor<Music> NES_START_CUT1_MUSIC =
        new AssetDescriptor<>("sound/NES-Start-cut1.ogg", Music.class);
// </editor-fold>     

}
