package view;

/**
 * Write a description of class Driver here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Driver
{
    // instance variables - replace the example below with your own
    public static void main(String[] args) {
		StartScreen startScreen = new StartScreen();
		startScreen.makeBoard();
		GameOverScreen gameOver = new GameOverScreen();
		gameOver.makeBoard();
		NextLevel nextLevel = new NextLevel();
		nextLevel.makeBoard();
		FinishGame finishGame = new FinishGame();
		finishGame.makeBoard();
	   }
}