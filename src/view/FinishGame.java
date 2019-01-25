package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.Timer;
  
public class FinishGame
{
    // instance variables
    MyDrawingPanel drawingPanel;
    JFrame customColor;
    JPanel myColor;
    Timer timer;
    BufferedImage Menu;
    int startLength = 400;
    //JButton Start;

    public void makeBoard(){
        //Images
        try{
            Menu = ImageIO.read(new File("images/wellDone.jpg"));
        }catch(IOException i){
            System.out.println(i.getMessage());
        }
        
        // Create Java Window
        JFrame window = new JFrame("Finish Crypt Raider");
        window.setBounds(100, 100, 445, 600);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // JPanel to draw in
        drawingPanel = new MyDrawingPanel();
        drawingPanel.setBounds(20, 20, startLength,startLength);
        drawingPanel.setBorder(BorderFactory.createEtchedBorder());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);

        JPanel colorPanel = new JPanel();
        
        colorPanel.setBounds(135, 450, 150, 70);

        mainPanel.add(drawingPanel);

        mainPanel.add(colorPanel);

        window.getContentPane().add(mainPanel);

        //Menu Stuff
        JMenuBar myMenuBar = new JMenuBar();

        JMenu menu = new JMenu("Menu");
        JMenu optionMenu = new JMenu("Options");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem newGameItem = new JMenuItem("New Game", 'n');
        JMenuItem exitItem = new JMenuItem("Exit", 'e');
        JMenuItem howToPlayItem = new JMenuItem("How To Play", 't');
        JMenuItem aboutItem = new JMenuItem("About", 't');

        menu.add(newGameItem);
        menu.add(exitItem);
        helpMenu.add(howToPlayItem);
        helpMenu.add(aboutItem);

        myMenuBar.add(menu);
        myMenuBar.add(optionMenu);
        myMenuBar.add(helpMenu);

        window.setJMenuBar(myMenuBar);

        window.setVisible(true);

        //JColorChooser
        myColor = new JPanel(new FlowLayout());

        myColor.setMinimumSize(new Dimension(500, 50));
        myColor.setMaximumSize(new Dimension(500, 50));
        myColor.setPreferredSize(new Dimension(500, 50));

        customColor = new JFrame("Custom Color");
        customColor.setBounds(100, 100, 610, 350);
        customColor.setResizable(false);
        customColor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        customColor.add(myColor);

        customColor.add(myColor, BorderLayout.PAGE_END);
    }

    private class MyDrawingPanel extends JPanel {
        // Not required, but gets rid of the serialVersionUID warning.
        static final long serialVersionUID = 1234567890L;

        public void paintComponent(Graphics g) {
            g.drawImage(Menu, 0, 0, startLength, startLength, null);
        }
    }
}