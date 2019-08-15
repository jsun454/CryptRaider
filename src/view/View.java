package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.Controller;
import model.Tile;

/**
 * This class handles the visual aspects of the game
 * 
 * @author Jeffrey Sun
 */
public class View extends JFrame {
	private static final long serialVersionUID = -5856476386594461049L;
	
	private static final int TILE_LENGTH = 35;
	private static final int BOARD_WIDTH = 18;
	private static final int BOARD_HEIGHT = 12;
    
	private BufferedImage hardSand, granite, softSand, background, rock, bomb, orb, portal, player, mummy, menu, nextLevel, gameOver;
    
	private JFrame window;
	private GamePanel game;

	private int windowWidth;
	private int windowHeight;

	private Controller controller;
    
	/**
	 * Constructor that creates the view class with a reference to the controller
	 * 
	 * @param controller a reference to the controller of this model
	 */
	public View(Controller controller) {
		this.controller = controller;
        
		windowWidth = TILE_LENGTH * BOARD_WIDTH;
		windowHeight = TILE_LENGTH * BOARD_HEIGHT;

		loadImages();
		drawWindow();
	}
    
	/**
	 * Returns the game window
	 * 
	 * @return the game window
	 */
	public JFrame getWindow() {
		return window;
	}
    
	/**
	 * Updates the board
	 */
	public void updateBoard() {
		game.repaint();
	}

	/**
	 * Loads the images corresponding to each tile
	 */
	private void loadImages() {
		try {
			hardSand = ImageIO.read(new File("images/hardSand.png"));
			granite = ImageIO.read(new File("images/granite.png"));
			softSand = ImageIO.read(new File("images/softSand.png"));
			background = ImageIO.read(new File("images/background.png"));
			rock = ImageIO.read(new File("images/rock.png"));
			bomb = ImageIO.read(new File("images/bomb.png"));
			orb = ImageIO.read(new File("images/orb.png"));
			portal = ImageIO.read(new File("images/portal.png"));
			player = ImageIO.read(new File("images/guy.png"));
			mummy = ImageIO.read(new File("images/mummy.png")); 
			menu = ImageIO.read(new File("images/menu.png"));   
			nextLevel = ImageIO.read(new File("images/nextLevel.png")); 
			gameOver = ImageIO.read(new File("images/gameOver.png"));   
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
    
	/**
	 * Creates the window for the game to be displayed in
	 */
	private void drawWindow() {
		// Creates window
		window = new JFrame("Crypt Raider");
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		// Creates game screen
		game = new GamePanel();
		game.setPreferredSize(new Dimension(windowWidth, windowHeight));
		game.setBorder(BorderFactory.createEtchedBorder());
        
		window.getContentPane().add(game);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
    
	/**
	 * This class keeps track of the game's state and displays different screens accordingly
	 */
	private class GamePanel extends JPanel {
		private static final long serialVersionUID = -8851704944356287602L;
		
		@Override
		protected void paintComponent(Graphics g) {
			if(controller.state == Controller.START_MENU) {
				g.drawImage(menu, 0, 0, game.getWidth(), game.getHeight(), null);
			} else if(controller.state == Controller.PLAYING) {
				showLevel(controller.getBoard(), g);
			} else if(controller.state == Controller.TRANSITION_STATE) {
				g.drawImage(nextLevel, 0, 0, game.getWidth(), game.getHeight(), null);
			} else if(controller.state == Controller.GAME_OVER) {
				g.drawImage(gameOver, 0, 0, game.getWidth(), game.getHeight(), null);
			} else if(controller.state == Controller.END_MENU) {
				g.drawImage(menu, 0, 0, game.getWidth(), game.getHeight(), null);
			}
		}
	}

	/**
	 * This class draws the current level
	 * 
	 * @param board the board to draw
	 * 
	 * @param g the object to draw with
	 */
	private void showLevel(Tile[][] board, Graphics g) {
		if(board.length == 0) {
			return;
		}
		for(int row = 0; row != BOARD_HEIGHT; ++row) {
			for(int col = 0; col != BOARD_WIDTH; ++col) {
				int xPos = col * TILE_LENGTH;
				int yPos = row * TILE_LENGTH;
				switch(board[row][col].getType()) {
				case 'H':
					g.drawImage(hardSand, xPos, yPos, TILE_LENGTH, TILE_LENGTH, null);
					break;
				case 'G':
					g.drawImage(granite, xPos, yPos, TILE_LENGTH, TILE_LENGTH, null);
					break;
				case 'S':
					g.drawImage(softSand, xPos, yPos, TILE_LENGTH, TILE_LENGTH, null);
					break;
				case '0':
					g.drawImage(background, xPos, yPos, TILE_LENGTH, TILE_LENGTH, null);
					break;
				case 'R':
					g.drawImage(rock, xPos, yPos, TILE_LENGTH, TILE_LENGTH, null);
					break;
				case 'B':
					g.drawImage(bomb, xPos, yPos, TILE_LENGTH, TILE_LENGTH, null);
					break;
				case 'O':
					g.drawImage(orb, xPos, yPos, TILE_LENGTH, TILE_LENGTH, null);
					break;
				case 'P':
					g.drawImage(portal, xPos, yPos, TILE_LENGTH, TILE_LENGTH, null);
					break;
				case 'U':
					g.drawImage(player, xPos, yPos, TILE_LENGTH, TILE_LENGTH, null);
					break;
				case 'M':
					g.drawImage(mummy, xPos, yPos, TILE_LENGTH, TILE_LENGTH, null);
					break;
				}
			}
		}
	}
}