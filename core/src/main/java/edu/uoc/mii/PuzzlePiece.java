package edu.uoc.mii;

/**
 * This class contains the main data of each piece: current position and correct
 * position.
 * 
 * @author Marco Rodriguez
*/
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PuzzlePiece{
   public TextureRegion textureRegion;
   public int row, col; //Actual position in grid
   private final int rRow, rCol; //Correct position

   public PuzzlePiece(TextureRegion region, int row, int col){
		   this.textureRegion = region;
		   this.rRow = this.row = row;
		   this.rCol = this.col= col;
   }

   public boolean isCorrect(){
		   return rRow == row && rCol == col;
   }
   
   public String degug(){
       StringBuilder sb = new StringBuilder("row: "+row);
       sb.append(", rRow: ").append(rRow);
       sb.append(", col: ").append(col);
       sb.append(", rCol: ").append(rCol);
       sb.append(", isCorrect: ").append(isCorrect());

       return sb.toString();
   }

}