package model;
 
 public class Tile {
 	
	private char type; // The type of object that is shown.
	private int row; // The row of the object on the board.
	private int col; // The col of the object on the board.
	 	
	public Tile(char type, int row, int col) {
 		this.type = type;
 		this.row = row;
 		this.col = col;
 	}
 	
	// Returns the object being shown
	public char getType() {
 		return type;
 	}
 	
	// Returns the row.
	public int getRow() {
		return row;
	}
	
	// Returns the column.
	public int getCol() {
		return col;
	}
	
	// Sets the type of the object.
	public void setType(char type) {
		this.type = type;
	}
	
	// Sets the row.
	public void setRow(int row) {
		this.row = row;
	}
	
	// Sets the column.
	public void setCol(int col) {
		this.col = col;
	}
 }