package edu.uoc.mii;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.MathUtils;

/**
 * Base iniciada con gemini. Prompt: Me ayudas con la creación del primer nivel
 * del juego? Te recuerdo, el video juego es de estética retro, osea en pixel
 * art. Y estoy usando libGDX. Mi idea, para este nivel, es un puzle, de piezas
 * cuadradas del mismo tamaño. Dependiendo de la dificultad de juego, las piezas
 * serán mas o menos grandes. Me ayudas a programarlo en libGDX?
 *
 * @author Marco Rodriguez
 */
public class PuzzleScreen extends ScreenAdapter {

    final Game game;

    Viewport viewport;
    OrthographicCamera camera;
    SpriteBatch batch;
    BitmapFont font;
    Texture puzzleImage;
    Texture bgImage;
    ShapeRenderer shapeRenderer;
    Array<PuzzlePiece> pieces;
    PuzzlePiece selectedPiece = null;
    PuzzlePiece[][] board;

    boolean isLevelCompleted = false;
    boolean isGrayEnabled = false;
    boolean isMapEnabled = false;
    boolean isBGEnabled = true;
    boolean isDebug = false;

    int difficulty;
    int rows, cols;
    float pieceWidth, pieceHeight;
    float boardOffsetX, boardOffsetY;

    public PuzzleScreen(Game game, int difficulty) {
        this.game = game;
        this.difficulty = difficulty;

        camera = new OrthographicCamera();
        viewport = new FitViewport(RetroScreen.VIRTUAL_WIDTH, RetroScreen.VIRTUAL_HEIGHT, camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();
        //font = BitmapFontUtils.createPixelFont("ui/Gameplay.ttf", 14);
        //font = new BitmapFont(Gdx.files.internal("ui/Gameplay.fnt"));
        font = new BitmapFont(Gdx.files.internal("ui/PressStart2P-Regular.fnt"));
        batch = new SpriteBatch();
        pieces = new Array<>();
        shapeRenderer = new ShapeRenderer();

        // 1. Load Pixel Art Images
        bgImage = new Texture("level1/ZXScreen.png");
        puzzleImage = new Texture("level1/ULA.png");
        // IMPORTANT: To make the Pixel Art look sharp
        bgImage.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        puzzleImage.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        setDifficulty(difficulty);

    }

    private void setDifficulty(int i) {
        difficulty = i;
        if (i < 4) {
            isGrayEnabled = true;
        }
        isLevelCompleted = false;
        // Level 1: 2x2, Level 2: 3x3, Level 3: 4x4...
        rows = cols = difficulty + 1;
        board = new PuzzlePiece[rows][cols];
        pieces.clear();

        createPuzzlePieces();
        shufflePieces();
        float totalPuzzleWidth = cols * pieceWidth;
        float totalPuzzleHeight = rows * pieceHeight;

        // Calculate the margin to center it
        // (WorldWidth - PuzzleWidth) / 2 = Left Margin
        boardOffsetX = (viewport.getWorldWidth() - totalPuzzleWidth) / 2;
        boardOffsetY = (viewport.getWorldHeight() - totalPuzzleHeight) / 2;

        Gdx.app.debug("PuzzleScreen", "World [W: " + viewport.getWorldWidth() + ", H: " + viewport.getWorldHeight() + "]");
        Gdx.app.debug("PuzzleScreen", "Image [W: " + puzzleImage.getWidth() + ", H: " + puzzleImage.getHeight() + "]");
        Gdx.app.debug("PuzzleScreen", "Puzzle [W: " + totalPuzzleWidth + ", H: " + totalPuzzleHeight + "]");
        Gdx.app.debug("PuzzleScreen", "Offset [X: " + boardOffsetX + ", Y: " + boardOffsetY + "]");
        Gdx.app.debug("PuzzleScreen", "Dificulty: " + difficulty);
        Gdx.app.debug("PuzzleScreen", "Puzzle [C: " + cols + ", R: " + rows + ", P:" + pieces.size + "]");
        Gdx.app.debug("PuzzleScreen", "Piece [W: " + pieceWidth + ", H: " + pieceHeight + "]");
    }

    private void createPuzzlePieces() {
        int textureWidth = puzzleImage.getWidth();
        int textureHeight = puzzleImage.getHeight();

        float targetWidth = 300f;
        float targetHeight = targetWidth * (((float) textureHeight) / textureWidth);

        Gdx.app.log("PuzzleScreen", "Target Size [W: " + targetWidth + ", H: " + targetHeight + "]");

        // 1. Calculate piece size 
        int tileWidthInt = textureWidth / cols;
        int tileHeightInt = textureHeight / rows;

        this.pieceWidth = targetWidth / cols;
        this.pieceHeight = targetHeight / rows;

        // 2. Cut the image
        TextureRegion[][] tmp = TextureRegion.split(puzzleImage, tileWidthInt, tileHeightInt);

        // 3. Create the pieces 
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                TextureRegion region = tmp[row][col];
                PuzzlePiece piece = new PuzzlePiece(region, col, row);
                board[col][row] = piece;
                pieces.add(piece);
            }
        }

        // 4. Recalculate centering
        boardOffsetX = (viewport.getWorldWidth() - (cols * pieceWidth)) / 2;
        boardOffsetY = (viewport.getWorldHeight() - (rows * pieceHeight)) / 2;
    }

    private void shufflePieces() {
        // Exchange logical positions randomly
        int nCol, nRow, oCol, oRow;
        PuzzlePiece pt;
        do {
            Gdx.app.debug("PuzzleScreen", "shufflePieces");
            for (PuzzlePiece p : pieces) {
                nRow = MathUtils.random(rows - 1);
                nCol = MathUtils.random(cols - 1);

                pt = board[nRow][nCol];
                if (p != pt) {
                    board[nRow][nCol] = p;
                    oRow = p.row;
                    oCol = p.col;
                    p.row = nRow;
                    p.col = nCol;
                    pt.row = oRow;
                    pt.col = oCol;
                    board[oRow][oCol] = pt;
                }
            }
            checkWinCondition();
        }while(isLevelCompleted);
    }

    private void checkWinCondition() {
        for (PuzzlePiece p : pieces) {
            if (!p.isCorrect()) {
                isLevelCompleted = false;
                return;
            }
        }

        isLevelCompleted = true;
    }

    private void onLevelComplete() {
        Gdx.app.debug("PuzzleScreen", "¡Nivel Completado!");

        // Reset the selection so that no "blue" piece remains.
        selectedPiece = null;

        // TODO: Play victory sound
        // winSound.play();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            if (isLevelCompleted) {
                difficulty++;
                if (difficulty < 5) {
                    //game.setScreen(new PuzzleScreen(game, difficulty));
                    setDifficulty(difficulty);
                    return;
                } else {
                    game.setScreen(new MainMenuScreen(game));
                }
            }
            // 1. Unproject (Screen -> World) 
            // Fails if the screen does not have the same aspect ratio as the viewport. 
            // For the camera, the screen always has the defined size, if 
            // the proportion is changed, stretches the pixels in width or height. 
            // It doesn't affect whether the viewport behaves the same, but when using a 
            // FitViewPort, this will leave bands, horizontal or vertical, and 
            // then the mouse coordinates do not match the area that is
            // pointing.
            //Vector3 touchPos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            Vector3 touchPos = viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            // 2. Convert to local board coordinates
            float localX = touchPos.x - boardOffsetX;
            float localY = touchPos.y - boardOffsetY;

            // 3. Boundary check (Click inside the entire rectangle?)
            float totalWidth = cols * pieceWidth;
            float totalHeight = rows * pieceHeight;

            Gdx.app.debug("PuzzleScreen", "Click: [tpX:" + touchPos.x + ", tpY" + touchPos.y + "]:[lX:" + localX + ", lY:" + localY + "], total: [W" + totalWidth + ", H" + totalHeight + "]");
            if (localX >= 0 && localX < totalWidth && localY >= 0 && localY < totalHeight) {

                // 4. Calculate column (X)
                int clickedCol = (int) (localX / pieceWidth);

                // 5. Calculate row (Y)
                int rawRowFromBottom = (int) (localY / pieceHeight);
                int clickedRow = (rows - 1) - rawRowFromBottom;

                Gdx.app.debug("PuzzleScreen", "Click: en Grid: [R" + clickedRow + ", C" + clickedCol + "]");
                // 6. Final validation of indexes
                if (clickedCol >= 0 && clickedCol < cols && clickedRow >= 0 && clickedRow < rows) {
                    handlePieceClick(clickedCol, clickedRow);
                }
            } else {
                for (PuzzlePiece p : pieces) {
                    Gdx.app.log("PuzzleScreen", p.degug());
                }
            }
        }
    }

    private void handlePieceClick(int row, int col) {
        PuzzlePiece clickedPiece = getPieceAt(row, col);

        if (clickedPiece == null) {
            return;
        }

        if (selectedPiece == null) {
            selectedPiece = clickedPiece;
            // TODO: Play "selection" sound
        } else {
            if (selectedPiece == clickedPiece) {
                selectedPiece = null;
            } else {
                swapPieces(selectedPiece, clickedPiece);
                selectedPiece = null;
                checkWinCondition();
                if(isLevelCompleted){
                    onLevelComplete();
                }
            }
        }
    }

    private PuzzlePiece getPieceAt(int row, int col) {
//        for (PuzzlePiece p : pieces) {
//            if (p.row == row && p.col == col) {
//                return p;
//            }
//        }
//        return null; // Should not happen if the grid is full
        return board[row][col];
    }

    private void swapPieces(PuzzlePiece p1, PuzzlePiece p2) {
        int tempX = p1.row;
        int tempY = p1.col;

        p1.row = p2.row;
        p1.col = p2.col;

        p2.row = tempX;
        p2.col = tempY;

        board[p1.row][p1.col] = p1;
        board[p2.row][p2.col] = p2;

        // TODO: Add an tweening animation so the pieces slide instead of teleporting?
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();

        camera.update();
        viewport.apply();

        if (isDebug) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.DARK_GRAY);

            int cellSize = 10;
            float worldWidth = viewport.getWorldWidth();
            float worldHeight = viewport.getWorldHeight();

            // Vertical lines
            for (float x = 0; x <= worldWidth; x += cellSize) {
                shapeRenderer.line(x, 0, x, worldHeight);
            }

            // Horizontal lines
            for (float y = 0; y <= worldHeight; y += cellSize) {
                shapeRenderer.line(0, y, worldWidth, y);
            }

            // (Opcional) Dibujar una caja ROJA alrededor de donde debería ir el puzle
            // para ver si tus cálculos de boardOffset son correctos
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(boardOffsetX - 1, boardOffsetY - 1, cols * pieceWidth + 2, rows * pieceHeight + 2);

            shapeRenderer.end();
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (isBGEnabled) {
            batch.draw(bgImage, 0, 0);
        }

        if (isLevelCompleted) {
            // 1. Draw the complete perfect cabbage image (without cuts)
            batch.setColor(Color.WHITE);
            batch.draw(puzzleImage, boardOffsetX, boardOffsetY, cols * pieceWidth, rows * pieceHeight);

            // 2. Draw a text message on top
            font.setColor(Color.YELLOW);
            font.draw(batch, "¡NIVEL COMPLETADO!", boardOffsetX, boardOffsetY + ((rows / 2) * pieceHeight) + 40);
            font.draw(batch, "Toca para continuar...", boardOffsetX, boardOffsetY + ((rows / 2) * pieceHeight) - 20);

        } else {
            for (PuzzlePiece p : pieces) {
                float drawX = boardOffsetX + (p.row * pieceWidth);
                float drawY = boardOffsetY + ((rows - 1 - p.col) * pieceHeight);

                if (p == selectedPiece) {
                    batch.setColor(0.5f, 0.5f, 1f, 1f); // Dye light blue
                } else if (!p.isCorrect() && isGrayEnabled) {
                    batch.setColor(0.75f, 0.75f, 0.75f, 1f);
                } else {
                    batch.setColor(1f, 1f, 1f, 1f);
                }

                batch.draw(p.textureRegion, drawX, drawY, pieceWidth, pieceHeight);
            }

            batch.setColor(Color.WHITE);

            if (isMapEnabled && puzzleImage != null) {
                int mapW = 100;
                int mapH = (int) (((float) mapW) / puzzleImage.getWidth() * puzzleImage.getHeight());
                batch.draw(puzzleImage, boardOffsetX, boardOffsetY, mapW, mapH);
            }
        }
        batch.end();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.ESCAPE:
                        game.setScreen(new MainMenuScreen(game));
                        break;
                    case Input.Keys.U:
                        if (difficulty < 10) {
                            setDifficulty(difficulty + 1);
                        }
                        break;
                    case Input.Keys.B:
                        isBGEnabled = !isBGEnabled;
                        break;
                    case Input.Keys.G:
                        isGrayEnabled = !isGrayEnabled;
                        break;
                    case Input.Keys.M:
                        isMapEnabled = !isMapEnabled;
                        break;
                    case Input.Keys.R:
                        shufflePieces();
                        break;
                    case Input.Keys.D:
                        isDebug = !isDebug;
                        if (isDebug) {
                            Gdx.app.setLogLevel(Application.LOG_DEBUG);
                        } else {
                            Gdx.app.setLogLevel(Application.LOG_INFO);
                        }
                        break;
                    default:
                        Gdx.app.debug("PuzzleScreen", "Keycode: " + keycode);
                }
                return true;
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        puzzleImage.dispose();
        shapeRenderer.dispose();
    }
}
