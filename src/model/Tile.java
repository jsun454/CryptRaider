package model;
 
public class Tile {
 	
	private char type; // The type of object that is shown.
	private int row; // The row of the object on the board.
	private int col; // The column of the object on the board.
	 	
	public Tile(char type, int row, int col) {
 		this.type = type;
 		this.row = row;
 		this.col = col;
 	}
 	
	// Returns the object's type
	public final char getType() {
 		return type;
 	}
 	
	// Returns the object's row.
	public final int getRow() {
		return row;
	}
	
	// Returns the object's column.
	public final int getCol() {
		return col;
	}
	
	// Sets the object's type.
	public void setType(char type) {
		this.type = type;
	}
	
	// Sets the object's row.
	public void setRow(int row) {
		this.row = row;
	}
	
	// Sets the object's column.
	public void setCol(int col) {
		this.col = col;
	}
 }