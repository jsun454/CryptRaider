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
	private List<Tile> enemyList; // List of enemies
	
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
		
		currentLevel = 0;
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
	
	/*
	 * Lowers all gravity-affected objects by one tile if possible
	 */
	public void gravity() {
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
				int adjustment = explode(t.getRow(), t.getCol(), t.getRow(), t.getCol()); // Create an explosion
																						  // centered around the tile
				i -= adjustment; // Shift i to account for objects destroyed in explosion
			} else {
				t.setFalling(false); // TODO: test if this works for when bombs fall 1 unit
									 // TODO: test if adjustment works for multiple bombs
									 // TODO: fix rock dropping on guy not killing guy
									 // TODO: fix bug where moving the orb into the portal super fast leaves the rock
									 //       in the same place in the next level for some reason
			}
		}
	}
	
	/*
	 * Explodes the target tile and its 8 surrounding tiles. Surrounding bombs trigger a chain of explosions. This
	 * function returns the total number of gravity-affected objects that were exploded to the left/above the initial
	 * target in order to adjust the loop index of the gravity function
	 * 
	 * @param row row of the explosion target
	 * 
	 * @param col column of the explosion target
	 * 
	 * @param initRow row of the original explosion target
	 * 
	 * @param initCol column of the original explosion target
	 * 
	 * @return the total number of gravity-afftected objects exploded to the left/above the initial explosion target
	 */
	public int explode(int row, int col, int initRow, int initCol) {
		int adjustIndex = 0; // Number of gravity-affected objects exploded left/above the initial target
		for(int r = -1; r != 2; ++r) {
			for(int c = -1; c != 2; ++c) {
				Tile t = board[row+r][col+c];
				if(t.getType() == PLAYER) {
					controller.displayGameOverView(); // Game over if player explodes
					break;
				}
				if(t.canExplode()) {
					if(gravityList.contains(t)) {
						gravityList.remove(t);
						if(10*(row+r) + (col+c) < 10*initRow + initCol) {
							++adjustIndex;
						}
					}
					if(t.getType() == BOMB && (r != 0 || c != 0)) {
						t.setType(BACKGROUND);
						adjustIndex += explode(row+r, col+c, initRow, initCol); // Causes a chain explosion
					} else {
						t.setType(BACKGROUND);
					}
				}
			}
		}
		return adjustIndex;
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
		board = levelList.get(++currentLevel);
		setTileTrackingVars();
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