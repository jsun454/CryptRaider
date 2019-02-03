package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.Timer;

import controller.Controller;
import model.Tile;

public class View extends JFrame {
	private static final long serialVersionUID = -5856476386594461049L;

	// instance variables
    private Controller controller;

    private MyDrawingPanel drawingPanel;
    public Tile[][] board;

    Graphics g; 
    MyDrawingPanel b;

    private int DISP_LEVEL = 0;
    private int DISP_NEXT_LEVEL = 1;
    private int DISP_GAME_OVER = 2;
    private int DISP_GAME_WON = 3;
    private int DISP_MENU = 4;

    private int STATE;

    private final int WINDOW_WIDTH = 585;
    private final int WINDOW_HEIGHT = 450;

    private final int PIXEL = 30;
    private final int BOARD_WIDTH = 18;
    private final int BOARD_HEIGHT = 12;

    BufferedImage hardSand, granite, softSand, background, rock, bomb, orb, portal, player, mummy, menu, nextLevel, gameOver;

    Timer timer;
    JFrame window;
    int speed = 250;
    int time = 0;

    String[][] imageBoard = new String[BOARD_WIDTH][BOARD_HEIGHT];

    public JButton nextLevelBtn;

    public View(Controller controller) {
        STATE = DISP_MENU;

        this.controller = controller;
        board = controller.getBoard();

        //Load all the images
        try{
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
        }catch(IOException i){
            System.out.println(i.getMessage());
        }

        // Create Java Window
        window = new JFrame("Crypt Raider");
        window.setBounds(100, 100, WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // JPanel to draw in
        drawingPanel = new MyDrawingPanel();
        drawingPanel.setBounds(20, 20, BOARD_WIDTH*PIXEL, BOARD_HEIGHT*PIXEL);
        drawingPanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.add(drawingPanel);

        window.getContentPane().add(mainPanel);

        //Menu Stuff
        JMenuBar myMenuBar = new JMenuBar();

        JMenu optionMenu = new JMenu("Options");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem newGameItem = new JMenuItem("New Game", 'n');
        JMenuItem exitItem = new JMenuItem("Exit", 'e');
        JMenuItem howToPlayItem = new JMenuItem("How To Play", 'h');
        JMenuItem aboutItem = new JMenuItem("About", 'a');

        optionMenu.add(newGameItem);
        optionMenu.add(exitItem);
        helpMenu.add(howToPlayItem);
        helpMenu.add(aboutItem);

        myMenuBar.add(optionMenu);
        myMenuBar.add(helpMenu);

        window.setJMenuBar(myMenuBar);
        window.setVisible(true);
    }

    public void setState(int state) {
        STATE = state;
    }

    public int getState() {
        return STATE;
    }

    private class MyDrawingPanel extends JPanel {
        // Not required, but gets rid of the serialVersionUID warning.
        static final long serialVersionUID = 1234567890L;
        
        protected void paintComponent(Graphics g) {

            board = controller.getBoard();
            if (STATE == DISP_LEVEL) {
                g.setColor(Color.white);
                g.fillRect(2, 2, this.getWidth()-2, this.getHeight()-2);

                g.setColor(Color.lightGray);
                for (int x = 0; x < this.getWidth(); x += 30)
                    g.drawLine(x, 0, x, this.getHeight());

                for (int y = 0; y < this.getHeight(); y += 30)
                    g.drawLine(0, y, this.getWidth(), y);

                loadLevel(controller.getBoard(), g, this);

            } else if (STATE == DISP_NEXT_LEVEL) {
                loadNextLevel(g, drawingPanel);
            } else if (STATE == DISP_GAME_OVER) {
                GameOverScreen gameOver = new GameOverScreen();
                gameOver.makeBoard();
            }else if (STATE == DISP_GAME_WON) {
                FinishGame finishGame = new FinishGame();
                finishGame.makeBoard();
            }
        }
    }

    public void loadLevel(Tile[][] a, Graphics g, MyDrawingPanel b) {
        int countX = 0;
        int countY = 0;

        for (int i = 0; i < b.getWidth(); i += PIXEL) {
            for (int j = 0; j < b.getHeight(); j += PIXEL) {
                countX = j/PIXEL;
                countY = i/PIXEL;

                if(a[countX][countY].getType() == 'H')
                    g.drawImage(hardSand, i, j, PIXEL, PIXEL, null);
                else if(a[countX][countY].getType() == 'G')
                    g.drawImage(granite, i, j, PIXEL, PIXEL, null);
                else if(a[countX][countY].getType() == 'S')
                    g.drawImage(softSand, i, j, PIXEL, PIXEL, null);
                else if(a[countX][countY].getType() == '0')
                    g.drawImage(background, i, j, PIXEL, PIXEL, null);
                else if(a[countX][countY].getType() == 'R')
                    g.drawImage(rock, i, j, PIXEL, PIXEL, null);
                else if(a[countX][countY].getType() == 'B')
                    g.drawImage(bomb, i, j, PIXEL, PIXEL, null);
                else if(a[countX][countY].getType() == 'O')
                    g.drawImage(orb, i, j, PIXEL, PIXEL, null);
                else if(a[countX][countY].getType() == 'P')
                    g.drawImage(portal, i, j, PIXEL, PIXEL, null);
                else if(a[countX][countY].getType() == 'U')
                    g.drawImage(player, i, j, PIXEL, PIXEL, null);
                else if(a[countX][countY].getType() == 'M')
                    g.drawImage(mummy, i, j, PIXEL, PIXEL, null);
            }
        }
    }

    public void loadNextLevel(Graphics g, MyDrawingPanel drawingPanel) {
    	if(g != null){
    		g.drawImage(nextLevel, 0, 0, drawingPanel.getWidth(), drawingPanel.getHeight(), null);
    	}
    }

    private void loadGameWon() {
        // TODO Auto-generated method stub
    }

    public void homeScreen() {
        // Displays GUI that corresponds with the Crypt Raider's initial screen
        // Includes "New Game" and "Instructions" buttons
    }

    public JFrame getWindow() {
        return window;
    }

    public void updateBoard(Tile[][] moveUp) {
        board = moveUp;
        drawingPanel.repaint();
    }
}