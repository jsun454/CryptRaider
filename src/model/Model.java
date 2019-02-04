package model;

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
		
		currentLevel = 3;
		board = levelList.get(currentLevel);
		
		gravityList = new ArrayList<Tile>();
		enemyList = new ArrayList<Tile>();
		numOrbs = 0;
		
		setTileTrackingVars();
	}
	
	/*
	 * Move the player one tile in the given direction if possible, and have him push pushable objects in the same
	 * direction
	 * 
	 * @param dRow the desired change in horizontal position
	 * 
	 * @param dCol the desired change in vertical position
	 */
	public void playerMove(int dRow, int dCol) {
		Tile player = board[playerRow][playerCol];
		Tile target = board[playerRow+dRow][playerCol+dCol];
		if(player.canMoveInto(target.getType())) {
			// Check if the player can move into the target tile
			if(target.getType() == SOFT_SAND || target.getType() == BACKGROUND) {
				target.setType(PLAYER);
				player.setType(BACKGROUND);
				playerRow = playerRow + dRow;
				playerCol = playerCol + dCol;
			} else if(target.getType() == MUMMY) {
				// Explode the player if he moves into an enemy
				explode(player.getRow(), player.getCol());
			} else {
				Tile nextNext = board[playerRow + 2*dRow][playerCol + 2*dCol];
				Tile belowTarget = board[playerRow+dRow+1][playerCol+dCol];
				
				// Player pushes the object if there is nothing behind it and it isn't falling
				if(target.canMoveInto(nextNext.getType()) && !target.isFalling() && !(target.canFall() &&
						target.canMoveInto(belowTarget.getType()))) {
					
					nextNext.setType(target.getType());
					nextNext.setFalling(target.isFalling());
					target.setType(PLAYER);
					target.setFalling(false);
					
					if(gravityList.contains(target)) {
						gravityList.set(gravityList.indexOf(target), nextNext);
					}
					
					player.setType(BACKGROUND);
					playerRow = playerRow + dRow;
					playerCol = playerCol + dCol;
				}
			}
		}
	}
	
	/*
	 * Move each enemy to the square within its range that's closest to the player
	 */
	public void enemyMove() {
		for(int i = enemyList.size() - 1; i != -1; --i) {
			if(enemyList.get(i) == null) {
				enemyList.remove(i);
				continue;
			}
			Tile enemy = enemyList.get(i);
			mummyMove(enemy.getRow(), enemy.getCol());
		}
	}
	
	/*
	 * Lowers all gravity-affected objects by one tile if possible
	 */
	public void gravity() {
		for(int i = gravityList.size()-1; i != -1; --i) {
			if(gravityList.get(i) == null) {
				gravityList.remove(i);
				continue;
			}
			Tile t = gravityList.get(i);
			Tile below = board[t.getRow()+1][t.getCol()];
			if(t.canMoveInto(below.getType())) {
				if(below.getType() == PORTAL) {
					// If the object is an orb and it falls into the portal, remove it from the board
					gravityList.set(i, null);
					if(--numOrbs == 0) {
						controller.displayNextView();
					}
				} else {
					// Lower the object by one tile
					below.setType(t.getType());
					below.setFalling(true);
					gravityList.set(i, below);
				}
				t.setType(BACKGROUND);
				t.setFalling(false);
			} else if(t.isFalling() && t.explodesOn(below.getType())) {
				explode(t.getRow(), t.getCol()); // Create an explosion centered around the tile
			} else {
				t.setFalling(false);
			}
		}
	}
	
	/*
	 * Load the board for the next level
	 */
	public void nextLevel() {
		board = levelList.get(++currentLevel);
		setTileTrackingVars();
		controller.displayLevel();
	}
	
	/*
	 * Returns the current game board
	 * 
	 * @return the current game board
	 */
	public Tile[][] getBoard() {
		return board;
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
	 * Initialize the lists and variables for tracking the certain tile locations
	 */
	private void setTileTrackingVars() {
		gravityList.clear();
		enemyList.clear();
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
	 * Explodes the target tile and its 8 surrounding tiles. Surrounding bombs trigger a chain of explosions.
	 * 
	 * @param row row of the explosion target
	 * 
	 * @param col column of the explosion target
	 */
	private void explode(int row, int col) {
		for(int r = -1; r != 2; ++r) {
			for(int c = -1; c != 2; ++c) {
				Tile target = board[row+r][col+c];
				if(target.canExplode()) {
					// Game over if player explodes
					if(target.getType() == PLAYER) {
						if(r != 0 || c != 0) {
							target.setType(BACKGROUND);
							explode(row+r, col+c); // Causes a chain explosion
						} else {
							target.setType(BACKGROUND);
						}
						controller.displayGameOverView();
						return;
					}
					
					// Remove object from gravity-affected list (set to null)
					if(gravityList.contains(target)) {
						gravityList.set(gravityList.indexOf(target), null);
					}
					
					// Remove object from enemy list (set to null)
					if(enemyList.contains(target)) {
						enemyList.set(enemyList.indexOf(target), null);
					}
					
					// Explode object
					if((target.getType() == BOMB || target.getType() == MUMMY) && (r != 0 || c != 0)) {
						target.setType(BACKGROUND);
						explode(row+r, col+c); // Causes a chain explosion
					} else {
						target.setType(BACKGROUND);
					}
				}
			}
		}
	}
	
	/*
	 * Moves the mummy at the given row and column to the nearby tile closest to the player
	 * 
	 * @param row the mummy's current row
	 * 
	 * @param col the mummy's current column
	 */
	private void mummyMove(int row, int col) {
		Tile mummy = board[row][col];
		int bestRow = row, bestCol = col;
		double lowestDistance = distance(bestRow, bestCol, playerRow, playerCol);

		if(mummy.canMoveInto(board[row+1][col].getType())
				&& distance(row+1, col, playerRow, playerCol) < lowestDistance) {
			bestRow = row + 1;
			bestCol = col;
			lowestDistance = distance(bestRow, bestCol, playerRow, playerCol);
		} else if(mummy.canMoveInto(board[row-1][col].getType())
				&& distance(row-1, col, playerRow, playerCol) < lowestDistance) {
			bestRow = row - 1;
			bestCol = col;
			lowestDistance = distance(bestRow, bestCol, playerRow, playerCol);
		}
		
		if(mummy.canMoveInto(board[row][col+1].getType())
				&& distance(row, col+1, playerRow, playerCol) < lowestDistance) {
			bestRow = row;
			bestCol = col + 1;
		} else if(mummy.canMoveInto(board[row][col-1].getType())
				&& distance(row, col-1, playerRow, playerCol) < lowestDistance) {
			bestRow = row;
			bestCol = col - 1;
		}
		
		if(bestRow != row || bestCol != col) {
			if(board[bestRow][bestCol].getType() == PLAYER) {
				explode(row, col);
				return;
			}
			board[bestRow][bestCol].setType(MUMMY);
			mummy.setType(BACKGROUND);
			enemyList.set(enemyList.indexOf(mummy), board[bestRow][bestCol]);
		}
	}
	
	/*
	 * Finds the distance between two points
	 * 
	 * @param x1 x-coordinate of the first point
	 * 
	 * @param y1 y-coordinate of the first point
	 * 
	 * @param x2 x-coordinate of the second point
	 * 
	 * @param y2 y-coordinate of the second point
	 * 
	 * @return the distance between the two points
	 */
	private double distance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow(x2-x1, 2.0) + Math.pow(y2-y1, 2.0));
	}
}