package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Tile {

	private char type; // The type of object that is shown
	private int row; // The row of the object on the board
	private int col; // The column of the object on the board
	private boolean isFalling; // Stores whether the object is falling

	// Type-based variables
	private boolean canFall; // Stores whether the object can fall due to gravity
	private boolean canExplode; // Stores whether the object can explode
	private List<Character> explodesOn; // Stores tile types the object will explode on when falling
	private List<Character> canMoveInto; // Stores tile types the object can move into

	/*
	 * Constructor that creates a new tile based on the tile type
	 * 
	 * @param type character representing the object's type
	 * 
	 * @param row the object's row on the board
	 * 
	 * @param col the object's column on the board
	 */
	public Tile(char type, int row, int col) {
		this.type = type;
		this.row = row;
		this.col = col;
		isFalling = false;

		explodesOn = new ArrayList<Character>();
		canMoveInto = new ArrayList<Character>();

		setTypeBasedVars();
	}

	/*
	 * Returns the object's type
	 * 
	 * @return character representing the object's type
	 */
	public final char getType() {
		return type;
	}

	/*
	 * Returns the object's row
	 * 
	 * @return the object's row
	 */
	public final int getRow() {
		return row;
	}

	/*
	 * Returns the object's column
	 * 
	 * @return the object's column
	 */
	public final int getCol() {
		return col;
	}

	/*
	 * Returns whether the object is falling
	 * 
	 * @return whether the object is currently falling
	 */
	public final boolean isFalling() {
		return isFalling;
	}

	/*
	 * Returns whether the object can fall due to gravity
	 * 
	 * @return whether the object is affected by gravity
	 */
	public final boolean canFall() {
		return canFall;
	}

	/*
	 * Returns whether the object can be exploded by other objects
	 * 
	 * @return whether the object can be exploded by other objects
	 */
	public boolean canExplode() {
		return canExplode;
	}

	/*
	 * Returns a list of tile types that will cause the object to explode if the object falls onto them
	 * 
	 * @return the list of characters representing tiles the object can fall and explode on
	 */
	public boolean explodesOn(char type) {
		return explodesOn.contains(type);
	}

	/*
	 * Returns a list of tile types that the object can move into
	 * 
	 * @return the list of characters representing tiles the object can move into
	 */
	public boolean canMoveInto(char type) {
		return canMoveInto.contains(type);
	}

	/*
	 * Sets the object's type to the given type
	 * 
	 * @param type the new type of the object
	 */
	public final void setType(char type) {
		this.type = type;
		setTypeBasedVars();
	}

	/*
	 * Sets the object's row to the given row
	 * 
	 * @param row the new row of the object
	 */
	public final void setRow(int row) {
		this.row = row;
	}

	/*
	 * Sets the object's column to the given column
	 * 
	 * @param col the new column of the object
	 */
	public final void setCol(int col) {
		this.col = col;
	}

	/*
	 * Sets whether the object is currently falling
	 * 
	 * @param isFalling whether the object is currently falling
	 */
	public final void setFalling(boolean isFalling) {
		this.isFalling = isFalling;
	}

	/*
	 * Sets the type-based variables according to the object's current type
	 */
	private final void setTypeBasedVars() {
		explodesOn.clear();
		canMoveInto.clear();

		switch(type) {
		case Model.HARD_SAND:
			canFall = false;
			canExplode = false;
			break;
		case Model.GRANITE:
			canFall = false;
			canExplode = true;
			break;
		case Model.SOFT_SAND:
			canFall = false;
			canExplode = true;
			break;
		case Model.BACKGROUND:
			canFall = false;
			canExplode = false;
			break;
		case Model.BOMB:
			canFall = true;
			canExplode = true;
			explodesOn.addAll(Arrays.asList(new Character[] {
					Model.HARD_SAND,
					Model.GRANITE,
					Model.SOFT_SAND,
					Model.BOMB,
					Model.ORB,
					Model.ROCK,
					Model.PORTAL,
					Model.PLAYER,
					Model.MUMMY
			}));
			canMoveInto.add(Model.BACKGROUND);
			break;
		case Model.ORB:
			canFall = true;
			canExplode = true;
			explodesOn.addAll(Arrays.asList(new Character[] {
					Model.BOMB,
					Model.PLAYER,
					Model.MUMMY
			}));
			canMoveInto.addAll(Arrays.asList(new Character[] { Model.BACKGROUND, Model.PORTAL }));
			break;
		case Model.ROCK:
			canFall = true;
			canExplode = true;
			explodesOn.addAll(Arrays.asList(new Character[] {
					Model.BOMB,
					Model.PLAYER,
					Model.MUMMY
			}));
			canMoveInto.add(Model.BACKGROUND);
			break;
		case Model.PORTAL:
			canFall = false;
			canExplode = false;
			break;
		case Model.PLAYER:
			canFall = false;
			canExplode = true;
			canMoveInto.addAll(Arrays.asList(new Character[] {
					Model.SOFT_SAND,
					Model.BACKGROUND,
					Model.BOMB,
					Model.ORB,
					Model.ROCK,
					Model.MUMMY
			}));
			break;
		case Model.MUMMY:
			canFall = false;
			canExplode = true;
			canMoveInto.addAll(Arrays.asList(Model.BACKGROUND, Model.PLAYER));
		}
	}
}