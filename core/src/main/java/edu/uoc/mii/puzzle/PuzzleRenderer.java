package edu.uoc.mii.puzzle;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.uoc.mii.Main;
import edu.uoc.mii.conf.GameConfig;

/**
 * Base iniciada con gemini. Prompt: Me ayudas con la creación del primer nivel 
 * del juego? Te recuerdo, el video juego es de estética retro, osea en pixel 
 * art. Y estoy usando libGDX. Mi idea, para este nivel, es un puzle, de piezas 
 * cuadradas del mismo tamaño. Dependiendo de la dificultad de juego, las piezas 
 * serán mas o menos grandes. Me ayudas a programarlo en libGDX?
 * @author Marco Rodriguez
 */
public class PuzzleRenderer implements Disposable, InputProcessor {

    private final Main game;
    private final SpriteBatch batch;
    private final PuzzleController controller;

    Viewport viewport;
    OrthographicCamera camera;
    BitmapFont font;
    Texture bgImage;
    Texture puzzleImage;
    TextureRegion[] regions;

    ShapeRenderer shapeRenderer;

    String[] levels;
    float pieceWidth, pieceHeight;
    float boardOffsetX, boardOffsetY;

    public PuzzleRenderer(Main game, PuzzleController controller) {
        this.game = game;
        this.batch = game.getBatch();
        this.controller = controller;

        controller.setCallback(() -> {
            this.newBoard();
        });

        init();
    }

    private void init() {

        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT, camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();

        shapeRenderer = new ShapeRenderer();

        loadData();

    }

    private void loadData() {
        FileHandle file = Gdx.files.internal("level1/data.txt");
        String rawData = file.readString("UTF-8");
        levels = rawData.split("\n");
        for (String level : levels) {
            AssetDescriptor<Texture> imgDesc = new AssetDescriptor<>(level, Texture.class);
            game.assetManager.load(imgDesc);
        }
        game.assetManager.finishLoading();
        Gdx.app.log("PuzzleRenderer", "Loaded levels: " + (levels.length - 1));
        if (levels.length <= 1) {
            Gdx.app.error("PuzzleRenderer", "No levels data");
        }
    }

    private void newBoard() {
        int difficulty = controller.getDifficulty();
        int cols = controller.getBoard().getCols();
        int rows = controller.getBoard().getRows();
        int level = controller.getCurrentLevel();

        if (puzzleImage != null) {
            puzzleImage.dispose();
        }
        if (level > levels.length) {
            level = levels.length;
            Gdx.app.error("PuzzleRenderer", "Levels.length " + levels.length + " < " + level);
        }
        puzzleImage = game.assetManager.get(levels[level]);
        puzzleImage.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        createPuzzlePieces(rows, cols);

        float totalPuzzleWidth = cols * pieceWidth;
        float totalPuzzleHeight = rows * pieceHeight;

        // Calculate the margin to center it
        // (WorldWidth - PuzzleWidth) / 2 = Left Margin
        boardOffsetX = (viewport.getWorldWidth() - totalPuzzleWidth) / 2;
        boardOffsetY = (viewport.getWorldHeight() - totalPuzzleHeight) / 2;

        Gdx.app.debug("PuzzleRenderer", "World [W: " + viewport.getWorldWidth() + ", H: " + viewport.getWorldHeight() + "]");
        Gdx.app.debug("PuzzleRenderer", "Image [W: " + puzzleImage.getWidth() + ", H: " + puzzleImage.getHeight() + "]");
        Gdx.app.debug("PuzzleRenderer", "Puzzle [W: " + totalPuzzleWidth + ", H: " + totalPuzzleHeight + "]");
        Gdx.app.debug("PuzzleRenderer", "Offset [X: " + boardOffsetX + ", Y: " + boardOffsetY + "]");
        Gdx.app.debug("PuzzleRenderer", "Dificulty: " + difficulty);
        Gdx.app.debug("PuzzleRenderer", "Puzzle [C: " + cols + ", R: " + rows + ", P:" + controller.getBoard().getPieces().size + "]");
        Gdx.app.debug("PuzzleRenderer", "Piece [W: " + pieceWidth + ", H: " + pieceHeight + "]");
    }

    private void createPuzzlePieces(int rows, int cols) {
        regions = new TextureRegion[rows * cols];

        int textureWidth = puzzleImage.getWidth();
        int textureHeight = puzzleImage.getHeight();

        float targetWidth = 300f;
        float targetHeight = targetWidth * (((float) textureHeight) / textureWidth);

        Gdx.app.log("PuzzleRenderer", "Target Size [W: " + targetWidth + ", H: " + targetHeight + "]");

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
                PuzzlePiece piece = controller.getBoard().getPieceAt(col, row);
                regions[piece.id] = region;
            }
        }

        // 4. Recalculate centering
        boardOffsetX = (viewport.getWorldWidth() - (cols * pieceWidth)) / 2;
        boardOffsetY = (viewport.getWorldHeight() - (rows * pieceHeight)) / 2;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (controller.isLevelCompleted()) {
            return false;
        }

        PuzzleBoard board = controller.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();
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

        Gdx.app.debug("PuzzleRenderer", "Click: [tpX:" + touchPos.x + ", tpY" + touchPos.y + "]:[lX:" + localX + ", lY:" + localY + "], total: [W" + totalWidth + ", H" + totalHeight + "]");
        if (localX >= 0 && localX < totalWidth && localY >= 0 && localY < totalHeight) {

            // 4. Calculate column (X)
            int clickedCol = (int) (localX / pieceWidth);

            // 5. Calculate row (Y)
            int rawRowFromBottom = (int) (localY / pieceHeight);
            int clickedRow = (rows - 1) - rawRowFromBottom;

            Gdx.app.debug("PuzzleRenderer", "Click: en Grid: [R" + clickedRow + ", C" + clickedCol + "]");
            // 6. Final validation of indexes
            if (clickedCol >= 0 && clickedCol < cols && clickedRow >= 0 && clickedRow < rows) {
                handlePieceClick(clickedCol, clickedRow);
            }
        } else {
            for (PuzzlePiece p : board.getPieces()) {
                Gdx.app.log("PuzzleRenderer", p.degug());
            }
        }

        return true;
    }

    private void handlePieceClick(int row, int col) {
        controller.pieceClick(row, col);
    }

    public void render() {
        PuzzleBoard board = controller.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();

        camera.update();
        viewport.apply();

        if (controller.isDebug()) {
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

            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(boardOffsetX - 1, boardOffsetY - 1, cols * pieceWidth + 2, rows * pieceHeight + 2);

            shapeRenderer.end();
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (controller.isBGEnabled()) {
            if (bgImage == null) {
                bgImage = game.assetManager.get(levels[0]);
                bgImage.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            }
            batch.draw(bgImage, 0, 0);
        }
        batch.end();

        if (!controller.isLevelCompleted()) {
            batch.begin();
            for (PuzzlePiece p : board.getPieces()) {
                float drawX = boardOffsetX + (p.row * pieceWidth);
                float drawY = boardOffsetY + ((rows - 1 - p.col) * pieceHeight);

                if (p == controller.getSelectedPiece()) {
                    batch.setColor(0.5f, 0.5f, 1f, 1f); // Dye light blue
                } else if (!p.isCorrect() && controller.isGrayEnabled()) {
                    batch.setColor(0.75f, 0.75f, 0.75f, 1f);
                } else {
                    batch.setColor(1f, 1f, 1f, 1f);
                }

                batch.draw(regions[p.id], drawX, drawY, pieceWidth, pieceHeight);
            }

            batch.setColor(Color.WHITE);

            if (controller.isMapEnabled() && puzzleImage != null) {
                int mapW = 100;
                int mapH = (int) (((float) mapW) / puzzleImage.getWidth() * puzzleImage.getHeight());
                batch.draw(puzzleImage, boardOffsetX, boardOffsetY, mapW, mapH);
            }

            batch.end();

            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            Gdx.gl.glLineWidth(2);
            shapeRenderer.setColor(Color.BLACK);

            for (PuzzlePiece p : board.getPieces()) {
                if (p.isCorrect()) {
                    continue;
                }

                float drawX = boardOffsetX + (p.row * pieceWidth);
                float drawY = boardOffsetY + ((rows - 1 - p.col) * pieceHeight);

                if (p == controller.getSelectedPiece()) {
                    shapeRenderer.setColor(Color.YELLOW);
                } else if (controller.isGrayEnabled()) {
                    shapeRenderer.setColor(Color.BLACK);
                }
                shapeRenderer.rect(drawX, drawY, pieceWidth, pieceHeight);
            }
            shapeRenderer.end();

            if (controller.isMapEnabled() && puzzleImage != null) {
                batch.begin();
                int mapW = 100;
                int mapH = (int) (((float) mapW) / puzzleImage.getWidth() * puzzleImage.getHeight());
                batch.draw(puzzleImage, boardOffsetX, boardOffsetY, mapW, mapH);
                batch.end();
            }
        }
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

// <editor-fold desc="Unused InputProcessor methods">
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
// </editor-fold>
}
