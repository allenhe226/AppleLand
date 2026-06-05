/*
Name: Allen He // Date: 01/17/24 // Enemy.java
Course: ICS3U7-02 with Ms. Strelkovska
Description: Enemy class that inherits from the Player class for creating and handling the various types of enemies in the game.
*/

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.event.*;

public class Enemy extends Player {
	
	// instance variable declaration
	private int r, c, spawnframes;
	private char type;
	private boolean spawned;
	public static int JESTER_SPAWN_FRAMES = 10;
	
	private static BufferedImage enemy = null, sniper = null, angry = null, helmet = null, jester = null;
	private ImageIcon img;
	
	// constructor for creating enemies with parameters of r, c, x, y, and type
	public Enemy(int r, int c, double x, double y, char type) {
		
		// runs the player constructor
		super(x,y);
		
		// depending on the type assigned to the enemy, it will have different properties
		this.type = type;
		this.r = r;
		this.c = c;
		this.spawned = false;
		
		// all enemies move and cannot shoot except those of 'S' type
		if (type == 'S') {
			this.setDX(0);
		} else {
			this.setDX(-2);
		}
		
		// all enemies do not get spawning frames except those of 'J' type
		if (type == 'J') {
			spawnframes = JESTER_SPAWN_FRAMES;
			this.setY(this.getY() + Tile.TILESIZE);
		} else {
			spawnframes = 0;
		}
 
		// if sprite images have not been loaded, load them
		if (enemy == null) {
			try {
				enemy = ImageIO.read(new File("images/enemy.png"));
			} catch (Exception e) {
				System.out.println("File \"enemy.png\" not found.");
			}
		}
		if (angry == null) {
			try {
				angry = ImageIO.read(new File("images/angry.png"));
			} catch (Exception e) {
				System.out.println("File \"angry.png\" not found.");
			}
		}
		if (sniper == null) {
			try {
				sniper = ImageIO.read(new File("images/sniper.png"));
			} catch (Exception e) {
				System.out.println("File \"sniper.png\" not found.");
			}
		}
		if (helmet == null) {
			try {
				helmet = ImageIO.read(new File("images/helmet.png"));
			} catch (Exception e) {
				System.out.println("File \"helmet.png\" not found.");
			}
		}
		if (jester == null) {
			try {
				jester = ImageIO.read(new File("images/jester.png"));
			} catch (Exception e) {
				System.out.println("File \"jester.png\" not found.");
			}
		}
	}
	
	// accessor and modifier methods for all relevant instance variables
	public int getR() {
		return r;
	}
	
	public int getC() {
		return c;
	}
	
	public char getType() {
		return type;
	}
	
	public boolean getSpawned() {
		return spawned;
	}
	
	public void setSpawned() {
		this.spawned = true;
	}
	
	public int getSpawnframes() {
		return spawnframes;
	}
	
	// decreases the spawn frames for enemies of the 'J' type
	public void decreaseSpawnframes() {
		setDY(getDY()-1.5);
		setY(getY() + getDY());
		spawnframes--;
	}
	
	// move function that moves the enemy and checks for collisions with tiles
	public void move(Map map){
		if (spawned) {
			
			// variable declaration
			double left, right, top, bottom;
			boolean canMoveX, canMoveY;
			left = getX() + 5;
			right = getX() + PLAYERSIZE - 5;
			top = getY() + 5;
			bottom = getY() + PLAYERSIZE;
			canMoveX = true;
			canMoveY = true;
			
			// checks for collisions if "collidable"
			if (getCollidable()) {
				for (int r = 0; r < map.tiles.length; r++) {
					for (int c = 0; c < map.tiles[r].length; c++) {
						Tile tile = map.tiles[r][c];
						
						// if a tile is solid, then it will block the enemy's movement
						if (map.tiles[r][c].getSolid() == 2) {
							if (checkCollision(left + getDX(), right + getDX(), top, bottom, tile)) {
								canMoveX = false;
							}
							if (checkCollision(left, right, top + getDY(), bottom + getDY(), tile)) {
								canMoveY = false;
							}
						}
					}
				}
			}
			
			// if nothing blocks x movement, move x by dx, otherwise, switch directions
			if (canMoveX) {
				setX(getX() + getDX());
			} else if (type == 'E' || type == 'H' || type == 'J') {
				setDX(getDX() * -1);
			}
			
			// if nothing blocks y movement, move y by dy
			if (canMoveY) {
				setY(getY() + getDY());
			} else {
				setDY(0);
			}
			
			// decreases the cooldown for using projectiles
			if (getCooldown() > 0) {
				setCooldown(getCooldown()-1);
			}
		}

	}
	
	// for the 'S' type enemy
	
	// method for determining the angle between the enemy and a specified player
	public double getAngle(Player player) {
		return Math.atan2(player.getY() - getY(), player.getX() - getX());
	}
	
	// method for creating a projectile at a specified angle with a 30 frame cooldown
	public void shoot(double angle) {
		int speed = 10;
		if (getCooldown() == 0) {
			projectiles.add(new Projectile(getX(), getY(), speed * Math.cos(angle), speed * Math.sin(angle), 'E'));
			setCooldown(30);
		}
	}
	
	// for the 'I' type enemy
	
	// method for determing what dx should be to match a specified player's x position
	public void changeDX(Player player) {
		if (getX() - player.getX() < -2) {
			setDX(2);
		} else if (getX() - player.getX() > 2){
			setDX(-2);
		} else {
			setDX(0);
		}
	}
	
	// for the 'J' enemy
	
	// method for calculating the distance between the enemy and a specified player
	public double distanceTo(Player player) {
		return Math.sqrt(Math.pow(player.getX() - getX(), 2) + Math.pow(player.getY() - getY(), 2));
	}
	
	// method for drawing the enemy on the screen
	public void draw(Graphics g) {
		
		// only draws enemies to the screen if they have spawned or if it is of 'J' type and their spawnframes have decreased from their total
		if (spawned || (type == 'J' && spawnframes < JESTER_SPAWN_FRAMES)) {
			// depending on the x direction and the type of the enemy, the sprite drawn will differ accordingly
			int direction_x;
			if (getDX() > 0) {
				direction_x = 1;
			} else {
				direction_x = 0;
			}
			
			if (type == 'E') {
				img = new ImageIcon(enemy.getSubimage(direction_x*PLAYERSIZE,0,50,50));
			} else if (type == 'S') {
				img = new ImageIcon(sniper.getSubimage(direction_x*PLAYERSIZE,0,50,50));
			} else if (type == 'I') {
				img = new ImageIcon(angry.getSubimage(direction_x*PLAYERSIZE,0,50,50));
			} else if (type == 'H') {
				img = new ImageIcon(helmet.getSubimage(direction_x*PLAYERSIZE,0,50,50));
			} else if (type == 'J') {
				img = new ImageIcon(jester.getSubimage(direction_x*PLAYERSIZE,0,50,50));
			}
			
			
			g.drawImage(img.getImage(), (int)getX() - GamePanel.gamex, (int)getY() - GamePanel.gamey, getW(), getH(), null);
		}
	}
}