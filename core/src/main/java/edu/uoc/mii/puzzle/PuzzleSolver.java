package edu.uoc.mii.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Arrays;

/**
 *
 * @author Marco Rodriguez
 */
public class PuzzleSolver {

    public static Array<PuzzleMove> solve(int[] currentBoard) {
        validateBoard(currentBoard);

        Array<PuzzleMove> moves = new Array<>();
        int[] simulation;

        simulation = new int[currentBoard.length];
        System.arraycopy(currentBoard, 0, simulation, 0, simulation.length);      

        Gdx.app.log("PuzzleSolver", "currentBoard: " + currentBoard.length + ", simulation:" + simulation.length);
        
        int maxSafeMoves = currentBoard.length * 2;

        for (int i = 0; i < simulation.length; i++) {
            while (simulation[i] != i) {
                if (moves.size > maxSafeMoves) {
                    throw new GdxRuntimeException("ERROR CRÍTICO: Bucle infinito detectado. "
                            + "El algoritmo no puede resolver este estado: " + Arrays.toString(currentBoard)
                            + ". Posiblemente hay piezas duplicadas o IDs incorrectos en 'simulation'.");
                }

                int pieceHere = simulation[i];
                int targetIndex = pieceHere;

                swap(simulation, i, targetIndex);
                moves.add(new PuzzleMove(i, targetIndex));
            }
        }
        return moves;
    }

    private static void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

    private static void validateBoard(int[] board) {
        int n = board.length;
        boolean[] seen = new boolean[n];

        for (int val : board) {
            if (val < 0 || val >= n) {
                throw new GdxRuntimeException("DATOS INVÁLIDOS: El valor " + val + " está fuera del rango del array (0-" + (n - 1) + ")");
            }
            if (seen[val]) {
                throw new GdxRuntimeException("DATOS INVÁLIDOS: El valor " + val + " está DUPLICADO en el array: " + Arrays.toString(board));
            }
            seen[val] = true;
        }
    }
}
