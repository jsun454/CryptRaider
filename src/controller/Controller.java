package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Timer;

import model.Model;
import model.Tile;
import view.View;

/**
 * This class handles communication between game data and visuals
 * 
 * @author Jeffrey Sun
 */
public class Controller {
	
	// Game states
	public static final int START_MENU = 0;
	public static final int PLAYING = 1;
	public static final int TRANSITION_STATE = 2;
	public static final int GAME_OVER = 3;
	public static final int END_MENU = 4;

	public static final int DELAY = 250; // Time in between in-game events (gravity tick, enemy movement)
	public static final int INPUT_COOLDOWN = 30;

	public int state;

	private Model model;
	private View view;
	private Timer timer;

	/**
	 * Constructor that creates the controller class with a model and view
	 */
	public Controller() {
		model = new Model(this);
		view = new View(this);
		view.getWindow().addKeyListener(new CustomKeyListener());
		
		state = START_MENU;

		startTimer();
	}
	
	/**
	 * Returns the game board stored by the model
	 * 
	 * @return the game board stored by the model
	 */
	public Tile[][] getBoard() {
		return model.getBoard();
	}
	
	/**
	 * Sets the state to the starting menu
	 */
	public void startMenu() {
		state = START_MENU;
	}
	
	/**
	 * Sets the state to the current level
	 */
	public void showLevel() {
		state = PLAYING;
	}
	
	/**
	 * Sets the state to transitioning between levels
	 */
	public void goToNextLevel() {
		state = TRANSITION_STATE;
	}
	
	/**
	 * Sets the state to game over
	 */
	public void gameOver() {
		state = GAME_OVER;
	}
	
	/**
	 * Sets the state to the ending menu
	 */
	public void endMenu() {
		state = END_MENU;
	}

	/**
	 * This class listens for the user's key presses
	 */
	private class CustomKeyListener implements KeyListener {
		long prevTime = System.currentTimeMillis();
		@Override
		public void keyPressed(KeyEvent e) {
			// Key presses that occur during the input cooldown window are ignored
			if(System.currentTimeMillis() - prevTime < INPUT_COOLDOWN) {
				return;
			} else {
				prevTime = System.currentTimeMillis();
			}
			
			if(state == START_MENU) {
				state = PLAYING;
			} else if(e.getKeyCode() == KeyEvent.VK_UP) {
				model.playerMove(-1, 0);
				view.updateBoard();
			} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
				model.playerMove(1, 0);
				view.updateBoard();
			} else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
				model.playerMove(0, -1);
				view.updateBoard();	
			} else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
				model.playerMove(0, 1);
				view.updateBoard();
			} else if(e.getKeyCode() == KeyEvent.VK_SPACE && state == TRANSITION_STATE) {
				model.nextLevel();
			} else if(state == GAME_OVER || state == END_MENU) {
				view.getWindow().setVisible(false);
				view.getWindow().dispose();
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {}
		@Override
		public void keyTyped(KeyEvent e) {}
	}
	
	/**
	 * Starts a timer that calls a set of regularly-repeated game functions
	 */
	private void startTimer() {
		timer = new Timer(DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.gravity();
				model.enemyMove();
				view.updateBoard();
			}
		});
		timer.start();
	}
}