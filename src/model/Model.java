package model;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import controller.Controller;

public class Model {
	
	// Game objects
	protected static final char HARD_SAND = 'H';
	protected static final char GRANITE = 'G';
	protected static final char SOFT_SAND = 'S';
	protected static final char BACKGROUND = '0';

	protected static final char BOMB = 'B';
	protected static final char ORB = 'O';
	protected static final char ROCK = 'R';
	protected static final char PORTAL = 'P';

	protected static final char PLAYER = 'U';
	protected static final char MUMMY = 'M';
	
	// Game information
	private static final int NUM_LEVELS = 20;
	
	private static final int BOARD_WIDTH = 18;
	private static final int BOARD_HEIGHT = 12;
	
	// Level information
	private List<Tile[][]> levelList;
	private int currentLevel;

	private Tile[][] board;
	
	private List<Tile> gravityList; // List of objects affected by gravity
	private List<Tile> enemyList; // List of mummies
	
	private int numOrbs;
	
	// Player information
	private int playerRow;
	private int playerCol;
	
	private Controller controller;
	
	/*
 	 * Constructor that creates the model class with a reference to the controller
 	 * 
 	 * @param controller a reference to the controller of this model
 	 */
	public Model(Controller controller) {
		this.controller = controller;
		
		levelList = new ArrayList<Tile[][]>();		
		loadLevels();
		
		currentLevel = 2; // First level is 0
		board = levelList.get(currentLevel);
		
		gravityList = new ArrayList<Tile>();
		enemyList = new ArrayList<Tile>();
		numOrbs = 0;
		
		setTileTrackingVars();
	}
	
	/*
 	 * Loads each level from its respective text file in the levels folder 
	 */
	private void loadLevels() {
		for(int i = 1; i <= NUM_LEVELS; i++) {
			levelList.add(fileToLevel(new File("levels/level" + i + ".txt")));
		}
	}

	/*
	 * Extracts a level from a given level file
	 * 
	 * @param file the name of the file to extract the level from
	 * 
	 * @return the 2d array of tiles representing the level
	 */
	private Tile[][] fileToLevel(File file) {
		Tile[][] level = new Tile[BOARD_HEIGHT][BOARD_WIDTH];
		
		int row = 0, col = 0;
		try {
			Scanner sc = new Scanner(file);
			while(sc.hasNext()) {
				
				// Add the next tile to the level array
				char tileType = imageStringToChar(sc.next().substring(1));
				Tile t = new Tile(tileType, row, col);
				level[row][col] = t;
				
				// Update row and column to be the next tile's position
				if(col == BOARD_WIDTH - 1) {
					++row;
					col = 0;
				} else {
					++col;
				}
				
				sc.next(); // Discard unused tile information from file
			}

			sc.close();		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return level;
	}
	
	/*
	 * Initialize the lists and variables for tracking the certain tile locations
	 */
	private void setTileTrackingVars() {
		for(int row = 0; row != board.length; ++row) {
			for(int col = 0; col != board[0].length; ++col) {
				Tile t = board[row][col];
				
				// Add objects that can fall to the gravity list
				if(t.canFall()) {
					gravityList.add(t);
				}
				
				// Update variables for tracking enemies, player, and orbs
				if(t.getType() == MUMMY) {
					enemyList.add(t);
				} else if(t.getType() == PLAYER) {
					playerRow = t.getRow();
					playerCol = t.getCol();
				} else if(t.getType() == ORB) {
					++numOrbs;
				}
			}
		}
	}
	
	/*
	 * Converts a given image file string to its corresponding character
	 * 
	 * @param s the string containing the name of an image file
	 * 
	 * @return the character representing the image file
	 */
	private char imageStringToChar(String s) {
		char c = '\0';
		
		switch(s) {
		case "hardSand.png":
			c = HARD_SAND;
			break;
		case "granite.png":
			c = GRANITE;
			break;
		case "softSand.png":
			c = SOFT_SAND;
			break;
		case "background.png":
			c = BACKGROUND;
			break;
		case "bomb.png":
			c = BOMB;
			break;
		case "orb.png":
			c = ORB;
			break;
		case "rock.png":
			c = ROCK;
			break;
		case "portal.png":
			c = PORTAL;
			break;
		case "guy.png":
			c = PLAYER;
			break;
		case "mummy.png":
			c = MUMMY;
			break;
		}
		
		assert(c != '\0');
		return c;
	}
	
	public void gravity() {
//		updateGravityList();
//		for(int i = 0; i < gravityList.size(); i++) {
//			Tile t = gravityList.get(i);
//			if(inBounds(t.getRow() + 1, t.getCol()) && board[t.getRow() + 1][t.getCol()].getType() == BACKGROUND) {
//				board[t.getRow()][t.getCol()] = new Tile(BACKGROUND, t.getRow(), t.getCol());
//				board[t.getRow() + 1][t.getCol()] = new Tile(t.getType(), t.getRow() + 1, t.getCol());
//				if (board[t.getRow() + 1][t.getCol()].getType() == BOMB && (canBeExploded(board[t.getRow() + 2][t.getCol()].getType()) || board[t.getRow() + 2][t.getCol()].getType() == HARD_SAND)) {
//					board[t.getRow() + 1][t.getCol()] = new Tile(BACKGROUND, t.getRow() + 1, t.getCol());
//					explode(t.getRow() + 1, t.getCol());
//				}
//				if(board[t.getRow() + 1][t.getCol()].getType() == ROCK && board[t.getRow() + 2][t.getCol()].getType() == BOMB) {
//					explode(t.getRow() + 2, t.getCol());
//				}
//				
//				gravityList.set(i, board[t.getRow() + 1][t.getCol()]);
//			}
//		}
		for(int i = gravityList.size()-1; i != -1; --i) {
			Tile t = gravityList.get(i);
			Tile below = board[t.getRow()+1][t.getCol()];
			if(t.canMoveInto(below.getType())) {
				if(below.getType() == PORTAL) {
					gravityList.remove(i); // If the object is an orb and it falls into the portal, remove it from the
										   // board
					if(--numOrbs == 0) {
						controller.displayNextView();
					}
				} else {
					below.setType(t.getType()); // Lower the object by one tile
					below.setFalling(true);
					gravityList.set(i, below);
				}
				t.setType(BACKGROUND);
			} else if(t.isFalling() && t.explodesOn(below.getType())) {
				explode(below.getRow(), below.getCol()); // Create an explosion centered around the tile below
				// TODO: move i to the correct position after explode deletes a bunch of items
			}
		}
	}
	
	public Tile[][] move(int dRow, int dCol) {
		if(inBounds(playerRow + dRow, playerCol + dCol) && canBeMovedOn(board[playerRow + dRow][playerCol + dCol])) {
			if(board[playerRow + dRow][playerCol + dCol].getType() == MUMMY) { // Man -> Mummy
				loadGameOverView();
			}
			else if(board[playerRow + dRow][playerCol + dCol].getType() == SOFT_SAND || board[playerRow + dRow][playerCol + dCol].getType() == BACKGROUND) { // Man -> Soft Sand
				board[playerRow + dRow][playerCol + dCol] = board[playerRow][playerCol]; // Move player forward
				board[playerRow][playerCol] = new Tile(BACKGROUND, playerRow, playerCol); // Set player's old position to background
				
				playerRow = playerRow + dRow;
				playerCol = playerCol + dCol;
			} 
			else if(inBounds(playerRow + 2 * dRow, playerCol + 2 * dCol) && (board[playerRow + 2 * dRow][playerCol + 2 * dCol].getType() == BACKGROUND)) { // Man -> Movable Object -> BACKGROUND

				if (board[playerRow + dRow][playerCol + dCol].getType() == BOMB) { // Clears surrounding positions
					board[playerRow + 2 * dRow][playerCol + 2 * dCol] = board[playerRow + dRow][playerCol + dCol]; // Move the object in front of the player forward
				} else {
					board[playerRow + 2 * dRow][playerCol + 2 * dCol] = board[playerRow + dRow][playerCol + dCol]; // Move the object in front of the player forward
				}
				board[playerRow + dRow][playerCol + dCol] = board[playerRow][playerCol]; // Move the player forward
				board[playerRow][playerCol] = new Tile(BACKGROUND, playerRow, playerCol); // Set player's old position to background
				board[playerRow + 2 * dRow][playerCol + 2 * dCol].setRow(playerRow + 2 * dRow);
				board[playerRow + 2 * dRow][playerCol + 2 * dCol].setCol(playerCol + 2 * dCol);
				
				playerRow = playerRow + dRow;
				playerCol = playerCol + dCol;
			} else if (inBounds(playerRow + 2 * dRow, playerCol + 2 * dCol) && (board[playerRow + dRow][playerCol + dCol].getType() == ORB) && (board[playerRow + 2 * dRow][playerCol + 2 * dCol].getType() == PORTAL)) { // Orb is pushed into portal
				board[playerRow + dRow][playerCol + dCol] = board[playerRow][playerCol]; // Move the player forward
				board[playerRow][playerCol] = new Tile(BACKGROUND, playerRow, playerCol); // Set player's old position to background
				playerRow = playerRow + dRow;
				playerCol = playerCol + dCol;
				
				// Know when the level is completed
				numOrbs--;
				if (numOrbs <= 0) {
					controller.displayNextView();
				}
			}
		}
		return board;
	}
	
	public void explode(int row, int col) {
		int[] dCol = {-1, 0, 1, -1, 1, -1, 0, 1};
		int[] dRow = {-1, -1, -1, 0,  0, 1, 1, 1};
		
		board[row][col] = new Tile(BACKGROUND, row, col);
		
		for (int i = 0; i < dRow.length; i++) {
			int tempRow = dRow[i] + row;
			int tempCol = dCol[i] + col;
			
			if(inBounds(tempRow, tempCol) && board[tempRow][tempCol].getType() == PLAYER) {
				loadGameOverView();
			}
			
			if (inBounds(tempRow, tempCol) && canBeExploded(board[tempRow][tempCol].getType())) {
				boolean ex = false;
				if(board[tempRow][tempCol].getType() == BOMB) {
					ex = true;
				}
				board[tempRow][tempCol] = new Tile(BACKGROUND, tempRow, tempCol);
				if(ex) {
					explode(tempRow, tempCol);
				}
			}
		}
	}
	
	public boolean canBeExploded(char type) {
		if (type == ORB) {
			numOrbs--;
			loadGameOverView();
		}
		return (type == GRANITE || type == SOFT_SAND || type == BOMB || type == ROCK || type == MUMMY);
	}
	
	
	// Returns whether the tile is in bounds.
	private boolean inBounds(int row, int col) {
 		return (row >= 0 && col >= 0 && row < board.length && col < board[0].length);
 	}
 	
	// Returns whether the tile can be moved on
 	private boolean canBeMovedOn(Tile tile) {
 		return (tile.getType() != HARD_SAND && tile.getType() != GRANITE && tile.getType() != PORTAL);
 	}
 	
 	// Return whether the tile can be moved on by a mummy
  	private boolean mummyCanBeMovedOn(Tile tile) {
  		return(tile.getType() == BACKGROUND || tile.getType() == PLAYER);
  	}
 	
 	// Updates player position
 	public void updatePlayerPosition() {
 		for(int row = 0; row < board.length; row++) {
 			for(int col = 0; col < board[0].length; col++) {
 				if(board[row][col].getType() == PLAYER) {
 					playerRow = row;
 					playerCol = col;
 				}
 			}
 		}
 	}
 	
 	// Updates mummy position
  	public Tile[][] updateMummyPosition() {
  		updateMummyList();
  		for(int i = 0; i < enemyList.size(); i++) {
  			Tile t = enemyList.get(i);
  			
  			int newRow = (int)getBestMummyMove(t.getRow(), t.getCol()).getX();
  			int newCol = (int)getBestMummyMove(t.getRow(), t.getCol()).getY();
  			
  			board[t.getRow()][t.getCol()] = new Tile(BACKGROUND, t.getRow(), t.getCol());
  			
  			if(board[newRow][newCol].getType() == PLAYER) {
  				loadGameOverView();
  			}
  			
  			board[newRow][newCol] = new Tile(MUMMY, newRow, newCol);
  			enemyList.set(i, board[newRow][newCol]);
  		}
  		return board;
  	}
  	
  	// Find the move that will decrease the distance between the mummy and the player the most
  	public Point getBestMummyMove(int row, int col) {
  		double minDistance = 1000; // Temporary distance
  		int newRow = -1;
  		int newCol = -1;
  		for(int r = -1; r <= 1; r++) {
  			for(int c = -1; c <= 1; c++) {
  				if(Math.abs(r) + Math.abs(c) == 1 && inBounds(row + r, col + c) && mummyCanBeMovedOn(board[row + r][col + c]) && Point2D.distance((double)playerRow, (double)playerCol, (double)row + r, (double)col + c) < minDistance) {
  					newRow = row + r;
  					newCol = col + c;
  					minDistance = Point2D.distance((double)playerRow, (double)playerCol, (double)row + r, (double)col + c);
  				}
  			}
  		}

  		if(newRow != -1 && newCol != -1) {
  			return (new Point(newRow, newCol));
  		}
  		else {
  			return (new Point(row, col));
  		}
  	}
 	
 	public int getNumOrbs() {
 		int numOrbs = 0;
 		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[0].length; col++) {
				if (board[row][col].getType() == ORB) {
					numOrbs++;
				}
			}
		}
 		return numOrbs;
 	}
	
	// Sets the current board to the next level
	public void loadNextLevel() {
		currentLevel++;
		board = levelList.get(currentLevel);
		updatePlayerPosition();
		numOrbs = getNumOrbs();
		controller.displayLevel();
	}
	
	public void updateGravityList() {
		gravityList.clear(); // Clear the list from the previous level
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; col < board[0].length; col++) {
				if(board[row][col].getType() == BOMB || board[row][col].getType() == ROCK || board[row][col].getType() == ORB) {
					gravityList.add(board[row][col]); // Add the tile to the 2D array of gravity tiles
				}
			}
		}
	}
	
	public void updateMummyList() {
		enemyList.clear(); // Clear the list from the previous level
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; col < board[0].length; col++) {
				if(board[row][col].getType() == MUMMY) {
					enemyList.add(board[row][col]);
				}
			}
		}
	}
	
	public void loadGameOverView() {
		controller.displayGameOverView();
	}
	
	public void loadGameWonView() {
		controller.displayGameWonView();
	}
	
	public void loadMenuView() {
		controller.displayMenuView();
	}
	
	// Returns the current board being used
	public Tile[][] getBoard() {
		return board;
	}
	
	// Returns the player's row
	public int getPlayerRow() {
		return playerRow;
	}
	
	// Returns the player's column
	public int getPlayerCol() {
		return playerCol;
	}
	
	public void printBoard(Tile[][] gen) {
		System.out.print("    ");
		for (int i = 0; i < gen.length; i++) {
			System.out.print(i % 10);
		}
		System.out.println();
		for (int row = 0; row < gen.length; row++) {
			if (row < 10) {
				System.out.print(" " + row);
			} else {
				System.out.print(row);
			}
			System.out.print("  ");
			for (int col = 0; col < gen[0].length; col++) {
				System.out.print(gen[row][col].getType());
			}
			System.out.println();
		}
	}
}