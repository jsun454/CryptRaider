/*
 * This class handles communication between game data and visuals
 * 
 * @author Jeffrey Sun
 */

package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Timer;

import model.Model;
import model.Tile;
import view.View;

public class Controller {
	private Model model;
	private View view;
	private Timer timer;
	
	private static final int DELAY = 250;
	
	private static final int PLAYING = 0;
    private static final int TRANSITION_STATE = 1;
    private static final int GAME_OVER = 2;
    private static final int GAME_WON = 3;
    
    /*
     * Constructor that creates the controller class with a model and view
     */
	public Controller() {
		model = new Model(this);
		view = new View(this);
		
		view.getWindow().addKeyListener(new CustomKeyListener());
		
		startTimer();
	}
	
	/*
	 * Returns the game board stored by the model
	 * 
	 * @return the game board stored by the model
	 */
	public Tile[][] getBoard() {
		return model.getBoard();
	}
	
	/*
	 * Sets the view's state to display the game over screen
	 */
	public void gameOver() {
		view.setState(GAME_OVER);
	}
	
	/*
	 * Sets the view's state to display the game won screen
	 */
	public void gameWon() {
		view.setState(GAME_WON);		
	}
	
	/*
	 * Sets the view's state to display the transition-to-next-level screen
	 */
	public void goToNextLevel() {
		view.setState(TRANSITION_STATE);
	}
	
	/*
	 * Sets the view's state to display the current level
	 */
	public void showLevel() {
		view.setState(PLAYING);		
	}
	
	/*
	 * This class listens for the user's key presses
	 */
	private class CustomKeyListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) { // TODO: fix infinite game over screen, add delay to inputs
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				model.playerMove(-1, 0);
				view.updateBoard(model.getBoard());
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				model.playerMove(1, 0);
				view.updateBoard(model.getBoard());
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				model.playerMove(0, -1);
				view.updateBoard(model.getBoard());	
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				model.playerMove(0, 1);
				view.updateBoard(model.getBoard());
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE && view.getState() == TRANSITION_STATE) {
				model.nextLevel();
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {}
		@Override
		public void keyTyped(KeyEvent e) {}
	}
	
	/*
	 * Starts a timer that calls a set of regularly-repeated game functions
	 */
	private void startTimer() {
		timer = new Timer(DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.gravity();
				model.enemyMove();
				view.updateBoard(model.getBoard());
			}
		});
		timer.start();
	}
}