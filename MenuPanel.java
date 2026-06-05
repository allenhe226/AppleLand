/*
Name: Allen He // Date: 01/17/24 // MenuPanel.java
Course: ICS3U7-02 with Ms. Strelkovska
Description: Menu panel class for creating and handling the menu panel that the user can use to select their level.
*/

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.event.*;

public class MenuPanel extends JPanel implements ActionListener {
	
	// variable declaration
	private JButton[] startLevels;
	private int count, levelToStart;
	private Timer timer;
	private static ImageIcon titlecard = new ImageIcon("images/titlecard.png");
	private static BufferedImage numberedbuttons = null;
	private boolean startGame;
	
	// constructor for creating a menu panel
	public MenuPanel() {
		this.setLayout(null);
		this.count = 0;
		// if sprite images have not been loaded, load them
		if (numberedbuttons == null) {
			try {
				numberedbuttons = ImageIO.read(new File("images/numberedbuttons.png"));
			} catch (Exception e) {
				System.out.println("File \"numberedbuttons.png\" not found.");
			}
		}
		
		// creates 10 buttons for 10 levels and add them to the panel
		startLevels = new JButton[Platformer.LEVELS];
		for (int i = 0; i < Platformer.LEVELS; i++) {
			startLevels[i] = new JButton(new ImageIcon(numberedbuttons.getSubimage(i * 100, 0, 100, 100)));
			startLevels[i].addActionListener(this);
			startLevels[i].setBounds((i % 5 + 1) * 150, (i / 5 + 4) * 120, 100, 100);
			startLevels[i].setOpaque(false);
			startLevels[i].setContentAreaFilled(false);
			startLevels[i].setBorderPainted(false);
			startLevels[i].setFocusPainted(false);
			startLevels[i].setFocusable(true);
		}
		for (int i = 0; i < startLevels.length; i++) {
			this.add(startLevels[i]);
		}
		
		startGame = false;
		levelToStart = 0;
		
		
		// declares and starts the timer
		timer = new Timer(5, this);
		timer.start();
	}
	
	// method that checks for any performed action
	public void actionPerformed(ActionEvent e) {
		
		// if a button is pressed, start the game
		for (int i = 0; i < startLevels.length; i++) {
			if (!startGame) {
				if (e.getSource() == startLevels[i]) {
					Platformer.inTransition = true;
					startGame = true;
					levelToStart = i+1;
				}
			}
		} 
		
		// each time the timer ticks, set the buttons visible according to whether or not the program is in transition
		if (e.getSource() == timer){
			for (int j = 0; j < 10; j++) {
				startLevels[j].setVisible(!Platformer.inTransition);
			}
			repaint();
		}
	}
	
	// method for drawing components onto the window
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		count++;
		
		// draws two backgrounds that scroll towards the left as count increases
		g.drawImage(Platformer.background.getImage(), 0-(count/2)%Platformer.SCREENWIDTH, 0, Platformer.SCREENWIDTH, Platformer.SCREENHEIGHT, null);
		g.drawImage(Platformer.background.getImage(), Platformer.SCREENWIDTH-(count/2)%Platformer.SCREENWIDTH, 0, Platformer.SCREENWIDTH, Platformer.SCREENHEIGHT, null);
		
		// draws the title card that increases and decreases in size accordingly to a sine wave function
		int sine = (int) (30 * Math.sin(count/30.0));
		g.drawImage(titlecard.getImage(), Platformer.SCREENWIDTH/2 - 200 - sine, Platformer.SCREENHEIGHT/2 - 300 - sine, 400 + 2 * sine, 200 + sine, null);
		
		if (startGame) {
			// if the game has started, display the transition until the frames have gone below 0
			if (Platformer.transition_frames >= 0) {
				Platformer.displayTransition(g, Platformer.SCREENWIDTH/2, Platformer.SCREENHEIGHT/2, true);
			} 
			// then switch to the game panel, giving it all the focus and initializing its map
			else {
				Platformer.cards.show(Platformer.c, "Game");
				Platformer.menu.setFocusable(false);
				Platformer.game.setFocusable(true);
				Platformer.game.requestFocusInWindow();
				Platformer.game.initializeMap(levelToStart);
				startGame = false;
			}
		}
	}
}