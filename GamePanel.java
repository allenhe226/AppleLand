/*
Name: Allen He // Date: 01/17/24 // GamePanel.java
Course: ICS3U7-02 with Ms. Strelkovska
Description: Game panel class for creating and handling the game panel that contains the map, player, enemies, and projectiles.
*/

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener{
	
	// variable declaration
	private JButton pauseButton, menuButton;
	private static boolean pressedA = false, pressedD = false, pressedW = false, pressedE = false;
	private static final int SCROLL_THRESHOLD_LEFT = 300, SCROLL_THRESHOLD_RIGHT = Platformer.SCREENWIDTH - 300, SCROLL_THRESHOLD_UP = 200, SCROLL_THRESHOLD_DOWN = Platformer.SCREENHEIGHT - 200;
	
	public static int gamex, gamey, score, level;
	private boolean paused, manualPaused, complete;
	private static ImageIcon instructions = new ImageIcon("images/instructions.png");
	
	private Player player;
	private Timer timer;
	private Map map;
	
	// constructor for creating a game panel
	public GamePanel() {
		this.setLayout(null);
		
		// create a menu button that returns to the menu panel
		menuButton = new JButton(new ImageIcon("images/menubutton.png"));
		menuButton.addActionListener(this);
		menuButton.setBounds(850, 10, 50, 50);
		menuButton.setOpaque(false);
		menuButton.setContentAreaFilled(false);
		menuButton.setBorderPainted(false);
		menuButton.setFocusPainted(false);
		menuButton.setFocusable(true);
		menuButton.setVisible(false);
		
		// creates a pause button that pauses the game and displays instructions
		pauseButton = new JButton(new ImageIcon("images/pausebutton.png"));
		pauseButton.addActionListener(this);
		pauseButton.setBounds(920, 10, 50, 50);
		pauseButton.setOpaque(false);
		pauseButton.setContentAreaFilled(false);
		pauseButton.setBorderPainted(false);
		pauseButton.setFocusPainted(false);
		pauseButton.setFocusable(true);
		pauseButton.setVisible(false);
		
		// add the buttons to the panel
		menuButton.addKeyListener(this);
		pauseButton.addKeyListener(this);
		
		this.add(menuButton);
		this.add(pauseButton);
		
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.addKeyListener(this);
		
		// initialize the first level
		initializeMap(1);
		
		// declares and starts the timer
		timer = new Timer(5, this);
		timer.start();
	}
	
	// method for initializing the map for any level on the panel
	public void initializeMap(int level) {
		
		// changes the level, map, and player according to the new level
		this.level = level;
		map = new Map("maps/map" + level + ".txt");
		player = new Player(map.spawnPoint()[0]*Tile.TILESIZE, map.spawnPoint()[1]*Tile.TILESIZE);
		
		// reset all relevant variables
		complete = false;
		pressedA = false;
		pressedD = false;
		pressedW = false;
		pressedE = false;
		gamex = 0;
		gamey = 0;
		score = 0;
		paused = false;
		manualPaused = false;
	}
	
	// method for completing a level
	public void complete() {
		complete = true;
	}
	
	// method for drawing components onto the window
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		// draws two backgrounds that scroll accordingly to the x position of the game
		g.drawImage(Platformer.background.getImage(), 0-(gamex/5)%Platformer.SCREENWIDTH, 0, Platformer.SCREENWIDTH, Platformer.SCREENHEIGHT, null);
		g.drawImage(Platformer.background.getImage(), Platformer.SCREENWIDTH-(gamex/5)%Platformer.SCREENWIDTH, 0, Platformer.SCREENWIDTH, Platformer.SCREENHEIGHT, null);
		
		// draws all the tiles from the map
		for (int r = 0; r < map.tiles.length; r++) {
			for (int c = 0; c < map.tiles[r].length; c++) {
				if ((c+1) * Tile.TILESIZE > gamex && c * Tile.TILESIZE < gamex + Platformer.SCREENWIDTH && (r+1) * Tile.TILESIZE > gamey && r * Tile.TILESIZE < gamey + Platformer.SCREENHEIGHT) {
					map.tiles[r][c].draw(g);
				}
			}
		}
		
		// draws the player and its projectiles
		player.draw(g);
		for (Projectile projectile : player.projectiles) {
			projectile.draw(g);
		}
		
		// draws the enemies and their projectiles
		for (Enemy enemy : map.enemies) {
			enemy.draw(g);
			for (Projectile projectile : enemy.projectiles) {
				projectile.draw(g);
			}
		}
		
		// display the score
		showScore(g);
		
		// display the instructions if the game is manually paused
		if (manualPaused) {
			g.drawImage(instructions.getImage(), 100, 100, 800, 580, null);
		}
		
		// if the player dies or the level is complete, display the transition
		if (!player.getAlive() || complete) {
			Platformer.inTransition = true;
			paused = true;
			Platformer.displayTransition(g, (int) player.getX() - gamex + player.getW() / 2, (int) player.getY() - gamey + player.getH() / 2, true);
		} else if (Platformer.transition_frames < Platformer.TOTAL_TRANSITION_FRAMES) {
			Platformer.inTransition = true;
			Platformer.displayTransition(g, map.spawnPoint()[0]*Tile.TILESIZE, map.spawnPoint()[1]*Tile.TILESIZE, false);
		} else {
			Platformer.inTransition = false;
			paused = false;
		}
	}
	
	// method for displaying the current score using sprites of numbers
	public void showScore(Graphics g) {
		if (score < 1000000) {
			int score2 = score;
			BufferedImage numbers = null;
			try {
				numbers = ImageIO.read(new File("images/numbers.png"));
			} catch (Exception e) {
				System.out.println("File \"numbers.png\" not found.");
			}
			for (int i = 0; i < 6; i++) {
				g.drawImage(new ImageIcon(numbers.getSubimage(score2 / (int)Math.pow(10,5-i) * 30, 0, 30, 30)).getImage(), (i+1)*30, 10, 30, 30, null);
				score2 %= (int)Math.pow(10,5-i);
			}
		}
	}
	
	// method for scrolling the game depending on how much past the threshold the player moves
	public void checkScroll() {
		if (player.getX() - gamex < SCROLL_THRESHOLD_LEFT && gamex > 0) {
			gamex += player.getX() - gamex - SCROLL_THRESHOLD_LEFT;
		} else if (player.getX()- gamex > SCROLL_THRESHOLD_RIGHT) {
			gamex += player.getX() - gamex - SCROLL_THRESHOLD_RIGHT;
		} 
		
		if (player.getY() - gamey < SCROLL_THRESHOLD_UP && gamey > 0) {
			gamey += player.getY() - gamey - SCROLL_THRESHOLD_UP;
		} else if (player.getY()- gamey > SCROLL_THRESHOLD_DOWN) {
			gamey += player.getY() - gamey - SCROLL_THRESHOLD_DOWN;
		}
	}
	
	// method that checks whether or not the player has ground beneath them
	public boolean playerOnGround(Player player) {
		for (int r = 0; r < map.tiles.length; r++) {
			for (int c = 0; c < map.tiles[r].length; c++) {
				if (map.tiles[r][c].getSolid() == 2 && onScreen(r,c)) {
					if (player.getRight() > c * Tile.TILESIZE && player.getLeft() < (c+1) * Tile.TILESIZE && player.getBottom() + 1 > r * Tile.TILESIZE && player.getTop() < (r+1) * Tile.TILESIZE) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	// method that checks whether a tile at (r,c) is visible on screen
	public static boolean onScreen(int r, int c) {
		return (c+1) * Tile.TILESIZE > gamex && c * Tile.TILESIZE < gamex + Platformer.SCREENWIDTH && (r+1) * Tile.TILESIZE > gamey && r * Tile.TILESIZE < gamey + Platformer.SCREENHEIGHT;
	}
	
	// method that checks for any performed actions
	public void actionPerformed(ActionEvent e) {
		
		// if the menu button is pressed, the program switches to the menu panel
		if (e.getSource() == menuButton) {
			Platformer.cards.next(Platformer.c);
			Platformer.menu.setFocusable(false);
			Platformer.game.setFocusable(true);
			Platformer.game.requestFocusInWindow();
		}
		
		// if the pause button is pressed, the program manually paused or unpaused
		else if (e.getSource() == pauseButton) {
			if (manualPaused) {
				manualPaused = false;
			} else {
				manualPaused = true;
			}
		} 
		// every time that the timer ticks, move every component on the screen
		else {
			
			// display the two buttons if the game is not in transition
			menuButton.setVisible(!Platformer.inTransition);
			pauseButton.setVisible(!Platformer.inTransition);
			
			// if the game isn't paused and the player is alive, 
			if (!paused && !manualPaused && player.getAlive()) {
				
				// checks whether keys are pressed and changes velocity accordingly
				
				// pressing A decreases dx
				if (pressedA) {
					if (player.getDX() > -5) {
						player.setDX(player.getDX() - 0.5);
					}
				} 
				
				// pressing D increases dx
				if (pressedD) {
					if (player.getDX() < 5) {
						player.setDX(player.getDX() + 0.5);
					}
				}
				
				// pressed W and having the player on the ground decreases dy significantly
				if (pressedW && playerOnGround(player)) {
					player.setDY(player.getDY() - 15);
				} 
				// if the player in not the ground, dy is still decreases slightly to help jump higher
				else if (pressedW && !playerOnGround(player) && player.getDY() < 0) {
					player.setDY(player.getDY() - 0.5);
				}
				
				// pressing E will activate a different power-up depending on what the player has
				
				// it can shoot a fireball
				if (pressedE && player.getPowerup().equals("fire")) {
					player.shootFireball();
				}
				
				// it can shoot a boomerang
				if (pressedE && player.getPowerup().equals("boomerang")) {
					player.shootBoomerang();
				}
				
				// it can set dy to a fixed value while falling (essentially gliding)
				if (pressedE && player.getPowerup().equals("wing") && !playerOnGround(player) && player.getDY() > 0) {
					player.setDY(1);
				}
				
				// if the player is not on the ground, then apply gravity to their dy
				if (player.getDY() < 20 && !playerOnGround(player)) {
					player.applyGravity();
				}
				
				// if the player is on the ground, then apply friction to their dx
				if (playerOnGround(player)) {
					if (player.getDX() > 0.1) {
						player.setDX(player.getDX() - 0.1);
					} else if (player.getDX() < -0.1) {
						player.setDX(player.getDX() + 0.1);
					} else {
						player.setDX(0);
					}
				}
				
				// creates player projectiles
				for (int i = 0; i < player.projectiles.size(); i++) {
					
					// apply gravity if needed for projectile
					if (player.projectiles.get(i).getApplyGravity()) {
						player.projectiles.get(i).applyGravity();
					}
					
					// if the projectile has type 'B', then return to player when set as "inactive" by collides with a tile or enemy
					if (player.projectiles.get(i).getType() == 'B') {
						if (player.projectiles.get(i).getState().equals("inactive")) {
							player.projectiles.get(i).returnTo(player);
							if (player.projectiles.get(i).collidesWith(player)) {
								player.projectiles.remove(i);
								i--;
							}
						} else if (player.projectiles.get(i).collidesWith(map.tiles)) {
							player.projectiles.get(i).setInactive();
						} else {
							for (int j = 0; j < map.enemies.size(); j++) {
								if (player.projectiles.get(i).collidesWith(map.enemies.get(j)) && map.enemies.get(j).getAlive()) {
									map.enemies.get(j).death();
									player.projectiles.get(i).setInactive();
									break;
								}
							}
						}
					}
					else if (player.projectiles.get(i).collidesWith(map.tiles)) {
						player.projectiles.remove(i);
						i--;
					} else {
						for (int j = 0; j < map.enemies.size(); j++) {
							if (player.projectiles.get(i).collidesWith(map.enemies.get(j)) && map.enemies.get(j).getAlive()) {
								map.enemies.get(j).death();
								player.projectiles.remove(i);
								i--;
								break;
							}
						}
					}
				}
				
				// handles cases with all enemies
				for (Enemy enemy : map.enemies) {
					if (enemy.getAlive()) {
						
						// if the enemy hasn't spawn yet, spawn it when it gets on screen
						if (!enemy.getSpawned()) {
							if (onScreen(enemy.getR(), enemy.getC()) && enemy.getType() != 'J') {
								enemy.setSpawned();
							} 
							
							// for the 'J' type enemy, spawn it after all of its spawnframes have been used up after getting close to the player
							else if (enemy.getType() == 'J' && enemy.getSpawnframes() != Enemy.JESTER_SPAWN_FRAMES) {
								if (enemy.getSpawnframes() > 0) {
									enemy.decreaseSpawnframes();
								} else {
									enemy.setSpawned();
								}
							} else if (enemy.getType() == 'J' && enemy.distanceTo(player) < 200) {
								enemy.decreaseSpawnframes();
							}
						}
						
						// for enemies that have spawned, apply gravity to the enemy
						if (enemy.getSpawned()) {
							enemy.applyGravity();
							
							// if the enemy is of 'S' type, they can shoot projectiles towards the player;
							if (enemy.getType() == 'S') {
								if (enemy.projectiles.size() == 0) {
									enemy.shoot(enemy.getAngle(player));
								}
								for (int i = 0; i < enemy.projectiles.size(); i++) {
									if (enemy.projectiles.get(i).collidesWith(map.tiles) || enemy.projectiles.get(i).getLifespan() < 0) {
										enemy.projectiles.remove(i);
										i--;
									}
									
									// if these projectiles collide with the player, set the player as dead
									else if (player.getAlive() && enemy.projectiles.get(i).collidesWith(player)) {
										player.death();
										enemy.projectiles.remove(i);
										i--;
									}
								}
							}
							
							// if the enemy is of 'I' type, change its dx accordingly to the player to follow them around
							if (enemy.getType() == 'I') {
								enemy.changeDX(player);
							}
						}
					}
				}
				
				
				// move the player, the enemies, and all the projectiles
				player.move(map);
				for (Projectile projectile : player.projectiles) {
					projectile.move();
				}
				for (Enemy enemy : map.enemies) {
					if (enemy.getAlive()) {
						enemy.move(map);
						for (Projectile projectile : enemy.projectiles) {
							projectile.move();
						}
					}
				}
			}
			
			// if an enemy is dead and has used up all their death frames, remove it from the list of enemies
			for (int i = 0; i < map.enemies.size(); i++) {
				Enemy enemy = map.enemies.get(i);
				if (!enemy.getAlive() && enemy.getDeathframes() <= 0) {
					map.enemies.remove(i);
				}
			}
			
			// if the player is dead and has used up all the transition frames, reinitialize the level
			if (!player.getAlive() && Platformer.transition_frames < -1) {
				Platformer.inTransition = false;
				initializeMap(level);
			}
			
			// if the player completed the level and used up all the transition frames, initialize the next level
			if (complete && Platformer.transition_frames < -1) {
				Platformer.inTransition = false;
				if (level + 1 <= Platformer.LEVELS) {
					initializeMap(level+1);
				} else {
					initializeMap(1);
				}
			}
			
			// does a movement and decreases the death frames that the enemy has 
			for (Enemy enemy : map.enemies) {
				if (!enemy.getAlive() && enemy.getDeathframes() > 0) {
					enemy.decreaseDeathframes();
					enemy.move(map);
					enemy.setDY(enemy.getDY() + 1);
				}
			}
			
			// does a movement and decreases the death frames that the player has
			if (!player.getAlive() && player.getDeathframes() > 0) {
				player.decreaseDeathframes();
				player.move(map);
				player.setDY(player.getDY() + 1);
			} 
			
			
			// scrolls the game
			checkScroll();
			
			repaint();
		}
	}
	
	// method for determining whether a key (A, D, W, E) is pressed and setting the corresponding boolean value to true
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == 65) { // A
			pressedA = true;
		}
		if (e.getKeyCode() == 68) { // D
			pressedD = true;
		}
		if (e.getKeyCode() == 87) { // W
			pressedW = true;
		}
		if (e.getKeyCode() == 69) { // E
			pressedE = true;
		}
		
	}
	
	// method for determining whether a key (A, D, W, E) is released and setting the corresponding boolean value to false
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == 65) { // A
			pressedA = false;
		}
		if (e.getKeyCode() == 68) { // D
			pressedD = false;
		}
		if (e.getKeyCode() == 87) { // W
			pressedW = false;
		}
		if (e.getKeyCode() == 69) { // E
			pressedE = false;
		}
		
	}
	
	public void keyTyped(KeyEvent e) {}
}