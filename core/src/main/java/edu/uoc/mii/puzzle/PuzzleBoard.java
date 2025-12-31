package edu.uoc.mii.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 *
 * @author Marco Rodriguez
 */
public class PuzzleBoard {

    private final Array<PuzzlePiece> pieces;
    private final PuzzlePiece[][] matrix; //[rows][cols]
    private final int rows, cols;

    public PuzzleBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        pieces = new Array<>(rows * cols);
        matrix = new PuzzlePiece[rows][cols];

        initBoard();
    }

    private void initBoard() {
        int i = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                PuzzlePiece piece = new PuzzlePiece(row, col);
                piece.id = i++;
                matrix[row][col] = piece;
                pieces.add(piece);
            }
        }
    }

    public void shufflePieces() {
        int nCol, nRow, oCol, oRow;
        PuzzlePiece tp;

        do {
            Gdx.app.debug("PuzzleBoard", "shufflePieces");
            for (PuzzlePiece p : pieces) {
                nRow = MathUtils.random(rows - 1);
                nCol = MathUtils.random(cols - 1);

                tp = matrix[nRow][nCol];
                if (p != tp) {
                    matrix[nRow][nCol] = p;
                    oRow = p.row;
                    oCol = p.col;
                    p.row = nRow;
                    p.col = nCol;
                    tp.row = oRow;
                    tp.col = oCol;
                    matrix[oRow][oCol] = tp;
                }
            }
        } while (checkWinCondition());
    }

    public boolean checkWinCondition() {
        for (PuzzlePiece p : pieces) {
            if (!p.isCorrect()) {
                return false;
            }
        }

        return true;
    }

    public PuzzlePiece getPieceAt(int row, int col) {
        return matrix[row][col];
    }

    public void swapPieces(PuzzlePiece p1, PuzzlePiece p2) {
        int tempX = p1.row;
        int tempY = p1.col;

        p1.row = p2.row;
        p1.col = p2.col;

        p2.row = tempX;
        p2.col = tempY;

        matrix[p1.row][p1.col] = p1;
        matrix[p2.row][p2.col] = p2;
    }

    public Array<PuzzlePiece> getPieces() {
        return pieces;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

}
