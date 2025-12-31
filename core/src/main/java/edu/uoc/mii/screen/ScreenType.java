package edu.uoc.mii.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import edu.uoc.mii.Main;
import edu.uoc.mii.MainMenuScreen;
import edu.uoc.mii.NextScreen;
import edu.uoc.mii.assets.AssetPaths;
import edu.uoc.mii.audio.AudioManager;
import edu.uoc.mii.intro.IntroScreen;
import edu.uoc.mii.platform.PlatformScreen;
import edu.uoc.mii.platform.PlatformStart;
import edu.uoc.mii.puzzle.PuzzleScreen;
import edu.uoc.mii.puzzle.PuzzleStart;
import edu.uoc.mii.score.LeaderboardScreen;
import edu.uoc.mii.user.AutoLoginScreen;

/**
 *
 * @author Marco Rodriguez
 */
public enum ScreenType {
    LOADING {
        @Override
        public Screen getScreen(Main game) {
            return new MainMenuScreen(game);
        }

        @Override
        public Array<AssetDescriptor> getAssetsDescriptor() {
            return new Array<>(0);
        }
    },
    MAIN_MENU {
        @Override
        public Screen getScreen(Main game) {
            return new MainMenuScreen(game);
        }

        @Override
        public Array<AssetDescriptor> getAssetsDescriptor() {
            Array<AssetDescriptor> assets = new Array<>(8);
            assets.add(AssetPaths.UI_SKIN);
            assets.add(AssetPaths.C64_SKIN);
            assets.add(AssetPaths.MAIN_MENU_BG);
            assets.add(AssetPaths.BG_MUSIC);
            assets.add(AssetPaths.EDM_MUSIC);
            assets.add(AssetPaths.TRAP_LOOP);
            assets.add(AssetPaths.BOS_MUSIC);
            assets.add(AssetPaths.GM120BPM_MUSIC);
            return assets;
        }
    },
    AUTO_LOGIN {
        @Override
        public Screen getScreen(Main game) {
            return new AutoLoginScreen(game);
        }

        @Override
        public Array<AssetDescriptor> getAssetsDescriptor() {
            Array<AssetDescriptor> assets = new Array<>(1);
            assets.add(AssetPaths.C64_SKIN);
            return assets;
        }
    },
    INTRO {
        @Override
        public Screen getScreen(Main game) {
            BitmapFont font = game.assetManager.get(AssetPaths.C64_FONT);
            return new IntroScreen(game, font, Color.WHITE, MAIN_MENU, "intro/intro.txt");
        }

        @Override
        public Array<AssetDescriptor> getAssetsDescriptor() {
            Array<AssetDescriptor> assets = new Array<>(2);
            assets.add(AssetPaths.C64_FONT);
            assets.add(AssetPaths.HORROR_LOOP2);
            return assets;
        }
    },
    LEADERBOARD {
        @Override
        public Screen getScreen(Main game) {
            return new LeaderboardScreen(game);
        }

        @Override
        public Array<AssetDescriptor> getAssetsDescriptor() {
            Array<AssetDescriptor> assets = new Array<>(3);
            assets.add(AssetPaths.C64_SKIN);
            assets.add(AssetPaths.FUNNY_8BITS);
            assets.add(AssetPaths.HAPPY_8BITS);
            return assets;
        }
    },
    LEVEL_PUZZLE {
        @Override
        public Screen getScreen(Main game) {
            return new PuzzleScreen(game);
        }

        @Override
        public Array<AssetDescriptor> getAssetsDescriptor() {
            Array<AssetDescriptor> assets = new Array<>(9);
            assets.add(AssetPaths.C64_SKIN);
            assets.add(AssetPaths.BG_MUSIC);
            assets.add(AssetPaths.EDM_MUSIC);
            assets.add(AssetPaths.MENUIN_SOUND);
            assets.add(AssetPaths.MENUOUT_SOUND);
            assets.add(AssetPaths.CANCEL_SOUND);
            assets.add(AssetPaths.EVIL_LAUGH_SOUND);
            assets.add(AssetPaths.POWERUP_SOUND);
            return assets;
        }
    },
    LEVEL_PLATFORM {
        @Override
        public Screen getScreen(Main game) {
            return new PlatformScreen(game);
        }

        @Override
        public Array<AssetDescriptor> getAssetsDescriptor() {
            Array<AssetDescriptor> assets = new Array<>(8);
//            assets.add(AssetPaths.LEVEL2_MAP1);
            assets.add(AssetPaths.C64_SKIN);
            assets.add(AssetPaths.LEVEL2_BG);
            assets.add(AssetPaths.LEVEL2_ATLAS);
            assets.add(AssetPaths.TRAP_LOOP);
            assets.add(AssetPaths.JUMP_SOUND);
            assets.add(AssetPaths.HURT_SOUND);
            assets.add(AssetPaths.EXPLOSION_SOUND);
            assets.add(AssetPaths.TRAMPOLINE_SOUND);
            assets.add(AssetPaths.EVIL_LAUGH_SOUND);
            assets.add(AssetPaths.POWERUP_SOUND);
            return assets;
        }
    },
    PUZLE_START {
        @Override
        public Screen getScreen(Main game) {
            return new PuzzleStart(game);
        }

        @Override
        public Array<AssetDescriptor> getAssetsDescriptor() {
            Array<AssetDescriptor> assets = new Array<>(0);
            return assets;
        }
    },
    PUZLE_INTRO {
        @Override
        public Screen getScreen(Main game) {
            BitmapFont font = game.assetManager.get(AssetPaths.ZXSPECTRUM_FONT);
            return new IntroScreen(game, font, Color.BLACK, LEVEL_PUZZLE, "level1/intro.txt", true);
        }

        @Override
        public Array<AssetDescriptor> getAssetsDescriptor() {
            Array<AssetDescriptor> assets = new Array<>(4);
            assets.add(AssetPaths.ZXSPECTRUM_FONT);
            assets.add(AssetPaths.TYPEWRITER_KEY);
            assets.add(AssetPaths.TYPEWRITER_SPACE);
            assets.add(AssetPaths.TYPEWRITER_BELL);
            return assets;
        }
    },
    PLATFORM_START {
        @Override
        public Screen getScreen(Main game) {
            return new PlatformStart(game);
        }

        @Override
        public Array<AssetDescriptor> getAssetsDescriptor() {
            Array<AssetDescriptor> assets = new Array<>(0);
            return assets;
        }
    },
    PLATFORM_INTRO {
        @Override
        public Screen getScreen(Main game) {
            BitmapFont font = game.assetManager.get(AssetPaths.C64_FONT);
            return new IntroScreen(game, font, Color.WHITE, LEVEL_PLATFORM, "level2/intro.txt", () -> {
                switch (MathUtils.random(0, 1)) {
                    case 0:
                        AudioManager.getInstance().playOneSound(AssetPaths.MORSE_DOT);
                        break;
                    default:
                        AudioManager.getInstance().playOneSound(AssetPaths.MORSE_DASH);
                }
            });
        }

        @Override
        public Array<AssetDescriptor> getAssetsDescriptor() {
            Array<AssetDescriptor> assets = new Array<>(3);
            assets.add(AssetPaths.C64_FONT);
            assets.add(AssetPaths.MORSE_DOT);
            assets.add(AssetPaths.MORSE_DASH);
            return assets;
        }
    },
    NEXT_INTRO {
        @Override
        public Screen getScreen(Main game) {
            return new NextScreen(game);
        }

        @Override
        public Array<AssetDescriptor> getAssetsDescriptor() {
            Array<AssetDescriptor> assets = new Array<>(1);
            assets.add(AssetPaths.UI_SKIN);
            assets.add(AssetPaths.NEXTINTRO_BG);
            assets.add(AssetPaths.GAMEMUSIC_LOOP);
            return assets;
        }
    };

    public abstract Screen getScreen(Main game);

    public abstract Array<AssetDescriptor> getAssetsDescriptor();
}
