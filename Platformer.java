/*
Name: Allen He // Date: 01/17/24 // Platformer.java
Course: ICS3U7-02 with Ms. Strelkovska
Description: Platformer class for creating the game and menu panels that the user navigates through to play the game, which is a platformer styled game with
ten unique maps featuring various types of tiles, enemies, and powerups. Inspired by the famous video game Super Mario Bros. All images and code were done by myself.
*/

// import modules
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import java.io.*;

// the main class that runs the program
public class Platformer {
	
	// declare static variables
	public static CardLayout cards;
	public static Container c;
	
	public static MenuPanel menu;
	public static GamePanel game;
	
	public static final int LEVELS = 10;
	public static final int SCREENWIDTH = 1000, SCREENHEIGHT = 800;
	public static final int TOTAL_TRANSITION_FRAMES = 50, OFFSET = 1000;
	public static boolean inTransition = false;
	public static int transition_frames = 50;
	public static ImageIcon deathscreen = new ImageIcon("images/deathscreen.png");
	public static ImageIcon background = new ImageIcon("images/background.png");
	
	// the main method that runs the program
	public static void main(String[] args) {
		
		// creates a JFrame and container
		JFrame frame = new JFrame();
		c = frame.getContentPane();
		
		// sets up the card layout used with a menu panel and a game panel
		cards = new CardLayout();
		c.setLayout(cards);
		menu = new MenuPanel();
		game = new GamePanel();
		c.add("Menu", menu);
		c.add("Game", game);
		
		// sets up the window used for the program
		frame.setSize(SCREENWIDTH,SCREENHEIGHT);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	// static method used for transitioning in and out of different panels, taking an x and y coordinate as parameters for the transitioning
	public static void displayTransition(Graphics g, int x, int y, boolean zoomIn) {
		
		// creates four black rectangles that surround the x and y coordinate depending on the frame
		g.setColor(Color.BLACK);
		g.fillRect(x-OFFSET, y-OFFSET, 2000, (TOTAL_TRANSITION_FRAMES - transition_frames) * (int)(OFFSET/TOTAL_TRANSITION_FRAMES) + 1);
		g.fillRect(x-OFFSET, y-OFFSET, (TOTAL_TRANSITION_FRAMES - transition_frames) * (int)(OFFSET/TOTAL_TRANSITION_FRAMES) + 1, 2000);
		g.fillRect(x-OFFSET, y+OFFSET-(TOTAL_TRANSITION_FRAMES - transition_frames) * (int)(OFFSET/TOTAL_TRANSITION_FRAMES) - 1, 2000, (TOTAL_TRANSITION_FRAMES - transition_frames) * (int)(OFFSET/TOTAL_TRANSITION_FRAMES));
		g.fillRect(x+OFFSET-(TOTAL_TRANSITION_FRAMES - transition_frames) * (int)(OFFSET/TOTAL_TRANSITION_FRAMES) - 1, y-OFFSET, (TOTAL_TRANSITION_FRAMES - transition_frames) * (int)(OFFSET/TOTAL_TRANSITION_FRAMES), 2000);
		
		// creates the hollow circle in the middle of the four rectangles
		int circleLeft = x - OFFSET + (TOTAL_TRANSITION_FRAMES - transition_frames) * (int)(OFFSET/TOTAL_TRANSITION_FRAMES);
		int circleTop = y - OFFSET + (TOTAL_TRANSITION_FRAMES - transition_frames) * (int)(OFFSET/TOTAL_TRANSITION_FRAMES);
		int circleRight = x + OFFSET - (TOTAL_TRANSITION_FRAMES - transition_frames) * (int)(OFFSET/TOTAL_TRANSITION_FRAMES);
		int circleBottom = y + OFFSET - (TOTAL_TRANSITION_FRAMES - transition_frames) * (int)(OFFSET/TOTAL_TRANSITION_FRAMES);
		g.drawImage(deathscreen.getImage(), circleLeft, circleTop, circleRight - circleLeft, circleBottom - circleTop, null);
		
		// changes the frame count to either zoom in or zoom out
		if (zoomIn) {
			transition_frames--;
		} else {
			transition_frames++;
		}
	}
}