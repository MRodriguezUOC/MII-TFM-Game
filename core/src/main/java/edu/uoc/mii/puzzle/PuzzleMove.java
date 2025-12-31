package edu.uoc.mii.puzzle;

/**
 *
 * @author Marco Rodriguez
 */
public class PuzzleMove {
        public int fromIndex;
        public int toIndex;

        public PuzzleMove(int a, int b) {
            this.fromIndex = a;
            this.toIndex = b;
        }

        @Override
        public String toString() {
            return "Swap: " + fromIndex + " <-> " + toIndex;
        }    
}

