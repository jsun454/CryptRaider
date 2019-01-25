package model;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import controller.Controller;

public class Model {
	private Controller controller;
	
	private final int BOARD_WIDTH = 18; // Width of the board.
	private final int BOARD_HEIGHT = 12; // Height of the board.
	
	// Variables to represent all the different objects in the game.
	private final char HARD_SAND = 'H';
	private final char GRANITE = 'G';
	private final char SOFT_SAND = 'S';
	private final char BACKGROUND = '0';

	private final char BOMB = 'B';
	private final char ORB = 'O';
	private final char ROCK = 'R';
	private final char PORTAL = 'P';

	private final char PLAYER = 'U';
	private final char MUMMY = 'M';

	// Levels
	private Tile[][] level1;
	private Tile[][] level2;
	private Tile[][] level3;
	private Tile[][] level4;
//	private Tile[][] level5;
//	private Tile[][] level6;
//	private Tile[][] level7;
//	private Tile[][] level8;
//	private Tile[][] level9;
//	private Tile[][] level10;
//	private Tile[][] level11;
//	private Tile[][] level12;
//	private Tile[][] level13;
//	private Tile[][] level14;
//	private Tile[][] level15;
//	private Tile[][] level16;
//	private Tile[][] level17;
//	private Tile[][] level18;
//	private Tile[][] level19;
//	private Tile[][] level20;

	private ArrayList<Tile[][]> levelList; // List of all the levels.
	private int currentLevel; // Level that the board is currently showing.

	private Tile[][] board; // Board that will actually be sent to controller.
	
	private ArrayList<Tile> gravityList; // List of objects affected by gravity.
	private ArrayList<Tile> mummyList; // List of mummies.
	
	private int playerRow; // Row that the player is in.
	private int playerCol; // Column that the player is in.
	
	private int numOrbs;

	public Model(Controller controller) {
		this.controller = controller;
		
		// Load all the level files.
		level1 = loadLevel(new File("levels/level1.txt"));
		level2 = loadLevel(new File("levels/level2.txt"));
		level3 = loadLevel(new File("levels/level3.txt"));
		level4 = loadLevel(new File("levels/level4.txt"));
//		level5 = loadLevel(new File("levels/level5.txt"));
//		level6 = loadLevel(new File("levels/level6.txt"));
//		level7 = loadLevel(new File("levels/level7.txt"));
//		level8 = loadLevel(new File("levels/level8.txt"));
//		level9 = loadLevel(new File("levels/level9.txt"));
//		level10 = loadLevel(new File("levels/level10.txt"));
//		level11 = loadLevel(new File("levels/level11.txt"));
//		level12 = loadLevel(new File("levels/level12.txt"));
//		level13 = loadLevel(new File("levels/level13.txt"));
//		level14 = loadLevel(new File("levels/level14.txt"));
//		level15 = loadLevel(new File("levels/level15.txt"));
//		level16 = loadLevel(new File("levels/level16.txt"));
//		level17 = loadLevel(new File("levels/level17.txt"));
//		level18 = loadLevel(new File("levels/level18.txt"));
//		level19 = loadLevel(new File("levels/level19.txt"));
//		level20 = loadLevel(new File("levels/level20.txt"));
		
		
		// Add all the levels to an array of levels. Currently only 1 level.
		levelList = new ArrayList<Tile[][]>();
		levelList.add(level1);
		levelList.add(level2);
		levelList.add(level3);
		levelList.add(level4);
//		levelList.add(level5);
//		levelList.add(level6);
//		levelList.add(level7);
//		levelList.add(level8);
//		levelList.add(level9);
//		levelList.add(level10);
//		levelList.add(level11);
//		levelList.add(level12);
//		levelList.add(level13);
//		levelList.add(level14);
//		levelList.add(level15);
//		levelList.add(level16);
//		levelList.add(level17);
//		levelList.add(level18);
//		levelList.add(level19);
//		levelList.add(level20);
		
		// Set the current level to be the Tile[][] at position 0 of levelList.
		currentLevel = 1;

		// Set the board to show the first level.
		board = levelList.get(currentLevel);
		
		// Initialize gravity and mummy lists.
		gravityList = new ArrayList<Tile>();
		mummyList = new ArrayList<Tile>();
		
		// Add appropriate tiles tiles to gravity and mummy lists.
		updateGravityList();
		updateMummyList();
		
		numOrbs = getNumOrbs();
		
		// Update the player's position on the board.
		updatePlayerPosition();
	}

	// Loads the levels
	private Tile[][] loadLevel(File file) {
		ArrayList<Tile> tileList = new ArrayList<Tile>(); // Temporary list to keep track of all the tiles read, in order.
		Tile[][] tile = new Tile[BOARD_HEIGHT][BOARD_WIDTH]; // 2D array of tiles that will be returned.

		try {
			Scanner sc = new Scanner(file);

			while(sc.hasNext()) {
				char type = imageStringToChar(sc.next().substring(1)); // Reads the next object to be displayed.

				sc.next(); // Read and ignore the boolean for walkable. 

				tileList.add(new Tile(type, -1, -1)); // Adds a new tile to the tile list, with row and col set to -1 temporarily.
			}

			sc.close();		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for(int row = 0; row < tile.length; row++) {
			for(int col = 0; col < tile[0].length; col++) {
				tileList.get(0).setRow(row); // Officially set the row of the tile (previously set to -1).
				tileList.get(0).setCol(col); // Officially set the column of the tile.
				tile[row][col] = tileList.remove(0); // Add the tile to the 2D array of tiles and remove it from the tile list.
			}
		}
		
		return tile;
	}

	// Converts the image strings from the level files into characters
	private char imageStringToChar(String s) {
		char c = ' ';
		switch (s) {
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
		default:
			break;
		}

		return c;
	}
	
	public Tile[][] gravity() {
		updateGravityList();
		for(int i = 0; i < gravityList.size(); i++) {
			Tile t = gravityList.get(i);
			if(inBounds(t.getRow() + 1, t.getCol()) && board[t.getRow() + 1][t.getCol()].getType() == BACKGROUND) {
				board[t.getRow()][t.getCol()] = new Tile(BACKGROUND, t.getRow(), t.getCol());
				board[t.getRow() + 1][t.getCol()] = new Tile(t.getType(), t.getRow() + 1, t.getCol());
				if (board[t.getRow() + 1][t.getCol()].getType() == BOMB && (canBeExploded(board[t.getRow() + 2][t.getCol()].getType()) || board[t.getRow() + 2][t.getCol()].getType() == HARD_SAND)) {
					board[t.getRow() + 1][t.getCol()] = new Tile(BACKGROUND, t.getRow() + 1, t.getCol());
					explode(t.getRow() + 1, t.getCol());
				}
				if(board[t.getRow() + 1][t.getCol()].getType() == ROCK && board[t.getRow() + 2][t.getCol()].getType() == BOMB) {
					explode(t.getRow() + 2, t.getCol());
				}
				
				gravityList.set(i, board[t.getRow() + 1][t.getCol()]);
			}
		}
		
		return board;
	}
	
	public Tile[][] move(int dRow, int dCol) {
		if(inBounds(playerRow + dRow, playerCol + dCol) && canBeMovedOn(board[playerRow + dRow][playerCol + dCol])) {
			if(board[playerRow + dRow][playerCol + dCol].getType() == MUMMY) { // Man -> Mummy
				loadGameOverView();
			}
			else if(board[playerRow + dRow][playerCol + dCol].getType() == SOFT_SAND || board[playerRow + dRow][playerCol + dCol].getType() == BACKGROUND) { // Man -> Soft Sand
				board[playerRow + dRow][playerCol + dCol] = board[playerRow][playerCol]; // Move player forward.
				board[playerRow][playerCol] = new Tile(BACKGROUND, playerRow, playerCol); // Set player's old position to background.
				
				playerRow = playerRow + dRow;
				playerCol = playerCol + dCol;
			} 
			else if(inBounds(playerRow + 2 * dRow, playerCol + 2 * dCol) && (board[playerRow + 2 * dRow][playerCol + 2 * dCol].getType() == BACKGROUND)) { // Man -> Movable Object -> BACKGROUND

				if (board[playerRow + dRow][playerCol + dCol].getType() == BOMB) { // Clears surrounding positions
					board[playerRow + 2 * dRow][playerCol + 2 * dCol] = board[playerRow + dRow][playerCol + dCol]; // Move the object in front of the player forward.
				} else {
					board[playerRow + 2 * dRow][playerCol + 2 * dCol] = board[playerRow + dRow][playerCol + dCol]; // Move the object in front of the player forward.
				}
				board[playerRow + dRow][playerCol + dCol] = board[playerRow][playerCol]; // Move the player forward.
				board[playerRow][playerCol] = new Tile(BACKGROUND, playerRow, playerCol); // Set player's old position to background.
				board[playerRow + 2 * dRow][playerCol + 2 * dCol].setRow(playerRow + 2 * dRow);
				board[playerRow + 2 * dRow][playerCol + 2 * dCol].setCol(playerCol + 2 * dCol);
				
				playerRow = playerRow + dRow;
				playerCol = playerCol + dCol;
			} else if (inBounds(playerRow + 2 * dRow, playerCol + 2 * dCol) && (board[playerRow + dRow][playerCol + dCol].getType() == ORB) && (board[playerRow + 2 * dRow][playerCol + 2 * dCol].getType() == PORTAL)) { // Orb is pushed into portal
				board[playerRow + dRow][playerCol + dCol] = board[playerRow][playerCol]; // Move the player forward.
				board[playerRow][playerCol] = new Tile(BACKGROUND, playerRow, playerCol); // Set player's old position to background.
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
			
			if(board[tempRow][tempCol].getType() == PLAYER) {
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
 	
	// Returns whether the tile can be moved on.
 	private boolean canBeMovedOn(Tile tile) {
 		return (tile.getType() != HARD_SAND && tile.getType() != GRANITE && tile.getType() != PORTAL);
 	}
 	
 	// Return whether the tile can be moved on by a mummy.
  	private boolean mummyCanBeMovedOn(Tile tile) {
  		return(tile.getType() == BACKGROUND || tile.getType() == PLAYER);
  	}
 	
 	// Updates player position.
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
  		for(int i = 0; i < mummyList.size(); i++) {
  			Tile t = mummyList.get(i);
  			
  			int newRow = (int)getBestMummyMove(t.getRow(), t.getCol()).getX();
  			int newCol = (int)getBestMummyMove(t.getRow(), t.getCol()).getY();
  			
  			board[t.getRow()][t.getCol()] = new Tile(BACKGROUND, t.getRow(), t.getCol());
  			
  			if(board[newRow][newCol].getType() == PLAYER) {
  				loadGameOverView();
  			}
  			
  			board[newRow][newCol] = new Tile(MUMMY, newRow, newCol);
  			mummyList.set(i, board[newRow][newCol]);
  		}
  		return board;
  	}
  	
  	// Find the move that will decrease the distance between the mummy and the player the most.
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
		gravityList.clear(); // Clear the list from the previous level.
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; col < board[0].length; col++) {
				if(board[row][col].getType() == BOMB || board[row][col].getType() == ROCK || board[row][col].getType() == ORB) {
					gravityList.add(board[row][col]); // Add the tile to the 2D array of gravity tiles.
				}
			}
		}
	}
	
	public void updateMummyList() {
		mummyList.clear(); // Clear the list from the previous level.
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; col < board[0].length; col++) {
				if(board[row][col].getType() == MUMMY) {
					mummyList.add(board[row][col]);
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
	
	// Returns the player's row.
	public int getPlayerRow() {
		return playerRow;
	}
	
	// Returns the player's column.
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