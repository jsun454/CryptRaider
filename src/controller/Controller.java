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
	
	int numRows;
	int numCols;
	
	Timer timer;
	int time = 0;
	
	Tile[][] board;
	
	private static final int DELAY = 250;
	
	private static final int DISP_LEVEL = 0;
    private static final int DISP_NEXT_LEVEL = 1;
    private static final int DISP_GAME_OVER = 2;
    private static final int DISP_GAME_WON = 3;
    private static final int DISP_MENU = 4;
	
	public Controller() {
		createModel();
		createView();
		
		//view.addActionListener(new CustomActionListener());
		view.getWindow().addKeyListener(new CustomKeyListener());
		view.getWindow().addMouseListener(new CustomOnReleaseListener());
		
		startTimer();
		
//		view.startTimer(); //---------------------------------DO THIS ONCE GAME HAS STARTED
//		view.homeScreen(); // Displays the home screen
	}
	
	private void createModel() {
		model = new Model(this);
		board = model.getBoard();
		numRows = board.length;
		numCols = board[0].length;
	}
	
	private void createView() {
		view = new View(this);
		view.setState(DISP_LEVEL);
		view.setVisible(true);
		view.setResizable(false);
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public class CustomKeyListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				view.updateBoard(model.move(-1, 0));
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				view.updateBoard(model.move(1, 0));
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				view.updateBoard(model.move(0, -1));	
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				view.updateBoard(model.move(0, 1));
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
	
	public class CustomActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == view.nextLevelBtn) {
				model.loadNextLevel();
			}
//			if (e.getSource() == view.mntmBeginner) {
//				//Do Stuff
//			}
		}
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
				view.incrementTime();
				model.gravity();
				view.updateBoard(model.getBoard());
				view.updateBoard(model.updateMummyPosition());
			}
		});
		timer.start();
	}
	
	public int getTime() {
		return time;
	}
	
	public Tile[][] getBoard() {
		return model.getBoard();
	}
	
	public void displayGameOverView() {
		view.setState(DISP_GAME_OVER);	
		view.loadGameOver();
	}
	
	public void displayGameWonView() {
		view.setState(DISP_GAME_WON);		
	}
	
	public void displayMenuView() {
		view.setState(DISP_MENU);		
	}
	
	public void displayNextView() {
		view.setState(DISP_NEXT_LEVEL);
	}
	
	public void displayLevel() {
		view.setState(DISP_LEVEL);		
	}
	
	public void reloadGUI() {
		view.repaint();
	}
}