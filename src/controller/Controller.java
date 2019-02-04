package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.Timer;

import model.Model;
import model.Tile;
import view.View;

public class Controller {
	private Model model;
	private View view;
	
	private Timer timer;
	
	private static final int DELAY = 250;
	
	private static final int DISP_LEVEL = 0;
    private static final int DISP_NEXT_LEVEL = 1;
    private static final int DISP_GAME_OVER = 2;
    private static final int DISP_GAME_WON = 3;
	
	public Controller() {
		model = new Model(this);
		view = new View(this);
		view.setState(DISP_LEVEL);
		
		view.getWindow().addKeyListener(new CustomKeyListener());
		view.getWindow().addMouseListener(new CustomOnReleaseListener());
		
		startTimer();
	}
	
	public class CustomKeyListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) {
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
			if (e.getKeyCode() == KeyEvent.VK_SPACE && view.getState() == DISP_NEXT_LEVEL) {
				model.loadNextLevel();
				view.repaint();
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {}
		@Override
		public void keyTyped(KeyEvent e) {}
	}
	
	public class CustomOnReleaseListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			//Do stuff
		}
	}
	
	public void startTimer() {
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
	
	public Tile[][] getBoard() {
		return model.getBoard();
	}
	
	public void displayGameOverView() {
		view.setState(DISP_GAME_OVER);
	}
	
	public void displayGameWonView() {
		view.setState(DISP_GAME_WON);		
	}
	
	public void displayNextView() {
		view.setState(DISP_NEXT_LEVEL);
	}
	
	public void displayLevel() {
		view.setState(DISP_LEVEL);		
	}
}