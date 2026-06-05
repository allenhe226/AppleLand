/*
Name: Allen He // Date: 01/17/24 // Player.java
Course: ICS3U7-02 with Ms. Strelkovska
Description: Player class for creating and handling the player that we control in the game.
*/

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Player {
	
	// variable declaration
	public static final int PLAYERSIZE = 50;
	private double x, y, dx, dy;
	private int w, h, cooldown, direction_x, deathframes;
	
	private boolean alive, collidable;
	
	private String powerup;
	public ArrayList<Projectile> projectiles;
	
	private BufferedImage apple = null, fireapple = null, boomerangapple = null, wingapple = null;
	private ImageIcon img;
	
	// constructor that creates a player object with parameters of x and y
	public Player(double x, double y){
		
		// initializes all instance variables
		this.x = x;
		this.y = y;
		this.w = PLAYERSIZE;
		this.h = PLAYERSIZE;
		this.dx = 0.0;
		this.dy = 0.0;
		this.powerup = "nothing";
		this.alive = true;
		this.collidable = true;
		this.deathframes = 50;
		this.projectiles = new ArrayList<Projectile>();
		this.cooldown = 10;
		this.direction_x = 1;
		
		// if sprite images have not been loaded, load them
		if (apple == null) {
			try {
				apple = ImageIO.read(new File("images/apple.png"));
			} catch (Exception e) {
				System.out.println("File \"apple.png\" not found.");
			}
		}
		
		if (fireapple == null) {
			try {
				fireapple = ImageIO.read(new File("images/fireapple.png"));
			} catch (Exception e) {
				System.out.println("File \"fireapple.png\" not found.");
			}
		}
		
		if (boomerangapple == null) {
			try {
				boomerangapple = ImageIO.read(new File("images/boomerangapple.png"));
			} catch (Exception e) {
				System.out.println("File \"boomerangapple.png\" not found.");
			}
		}
		
		if (wingapple == null) {
			try {
				wingapple = ImageIO.read(new File("images/wingapple.png"));
			} catch (Exception e) {
				System.out.println("File \"wingapple.png\" not found.");
			}
		}
	}
	
	// accessor and modifer methods for all relevant instance variables
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public int getW() {
		return w;
	}
	
	public int getH() {
		return h;
	}
	
	public double getDX() {
		return dx;
	}
	
	public double getDY() {
		return dy;
	}
	
	public void setDX(double dx) {
		this.dx = dx;
	}
	
	public void setDY(double dy) {
		this.dy = dy;
	}
	
	public double getLeft() {
		return x + 5;
	}
	
	public double getRight() {
		return x + PLAYERSIZE - 5;
	}
	
	public double getTop() {
		return y + 5;
	}
	
	public double getBottom() {
		return y + PLAYERSIZE;
	}
	
	public String getPowerup() {
		return powerup;
	}
	
	public boolean getAlive() {
		return alive;
	}
	
	public void setAlive() {
		alive = true;
	}
	
	public boolean getCollidable() {
		return collidable;
	}
	
	public int getCooldown() {
		return cooldown;
	}
	
	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}
	
	public int getDeathframes() {
		return deathframes;
	}
	
	public void decreaseDeathframes() {
		deathframes--;
	}
	
	// method for applying gravity
	public void applyGravity() {
		dy += 1;
	}
	
	// move function that moves the player and checks for collisions with tiles and enemies
	public void move(Map map){
		
		// declare variables
		double left, right, top, bottom;
		boolean canMoveX, canMoveY;
		left = x + 5;
		right = x + PLAYERSIZE - 5;
		top = y + 5;
		bottom = y + PLAYERSIZE;
		canMoveX = true;
		canMoveY = true;
		
		// checks for collisions if "collidable"
		if (collidable) {
			for (int r = 0; r < map.tiles.length; r++) {
				for (int c = 0; c < map.tiles[r].length; c++) {
					Tile tile = map.tiles[r][c];
					
					// if a tile is solid, then it will block the player's movement
					if (map.tiles[r][c].getSolid() == 2 && GamePanel.onScreen(r,c)) {
						if (checkCollision(left + dx, right + dx, top, bottom, tile)) {
							canMoveX = false;
						}
						if (checkCollision(left, right, top + dy, bottom + dy, tile)) {
							canMoveY = false;
							
							// additionally, if the player hits a tile from beneath, it can trigger certain events
							
							// if the tile has type 'F', spawns a fire power-up tile above
							if (map.tiles[r][c].getType() == 'F' && top > map.tiles[r][c].getTY() + map.tiles[r][c].getTH()) {
								map.tiles[r][c] = new Tile(r, c, c * Tile.TILESIZE, r * Tile.TILESIZE, '*', map);
								map.tiles[r-1][c] = new Tile(r-1, c, c * Tile.TILESIZE, (r-1) * Tile.TILESIZE, 'f', map);
							}
							
							// if the tile has type 'B', spawns a boomerang power-up tile above
							if (map.tiles[r][c].getType() == 'B' && top > map.tiles[r][c].getTY() + map.tiles[r][c].getTH()) {
								map.tiles[r][c] = new Tile(r, c, c * Tile.TILESIZE, r * Tile.TILESIZE, '*', map);
								map.tiles[r-1][c] = new Tile(r-1, c, c * Tile.TILESIZE, (r-1) * Tile.TILESIZE, 'b', map);
							}
							
							// if the tile has type 'W', spawns a wing power-up tile above
							if (map.tiles[r][c].getType() == 'W' && top > map.tiles[r][c].getTY() + map.tiles[r][c].getTH()) {
								map.tiles[r][c] = new Tile(r, c, c * Tile.TILESIZE, r * Tile.TILESIZE, '*', map);
								map.tiles[r-1][c] = new Tile(r-1, c, c * Tile.TILESIZE, (r-1) * Tile.TILESIZE, 'w', map);
							}
							
							// if the tile has type 'X', the level is won and the next level is triggered
							if (map.tiles[r][c].getType() == 'X' && top > map.tiles[r][c].getTY() + map.tiles[r][c].getTH()) {
								map.tiles[r][c] = new Tile(r, c, c * Tile.TILESIZE, r * Tile.TILESIZE, '*', map);
								Platformer.game.complete();
							}
						}
						
						// if the player touches a tile with type 'K', the player dies
						if (tile.getType() == 'K' && checkCollision(left + dx, right + dx, top + dy, bottom + dy, tile)) {
							death();
						}
						
						// used to make sure that movement isn't blocked even in scenarios when x and y movements individually are not blocked
						if (canMoveX && canMoveY && checkCollision(left + dx, right + dx, top + dy, bottom + dy, tile)) {
							canMoveX = false;
						}
					}
					
					// if the player touches a tile with type 'C', they gain 1 point for their score
					if (map.tiles[r][c].getType() == 'C' && GamePanel.onScreen(r,c)) {
						if (checkCollision(left, right, top, bottom, tile)) {
							GamePanel.score++;
							map.tiles[r][c] = new Tile(r, c, c * Tile.TILESIZE, r * Tile.TILESIZE, '.', map);
						}
					}
					
					// if the player touches a tile with type 'f', they gain a fire power-up
					if (map.tiles[r][c].getType() == 'f' && GamePanel.onScreen(r,c)) {
						if (checkCollision(left, right, top, bottom, tile)) {
							powerup = "fire";
							map.tiles[r][c] = new Tile(r, c, c * Tile.TILESIZE, r * Tile.TILESIZE, '.', map);
						}
					}
					
					// if the player touches a tile with type 'b', they gain a boomerang power-up
					if (map.tiles[r][c].getType() == 'b' && GamePanel.onScreen(r,c)) {
						if (checkCollision(left, right, top, bottom, tile)) {
							powerup = "boomerang";
							map.tiles[r][c] = new Tile(r, c, c * Tile.TILESIZE, r * Tile.TILESIZE, '.', map);
						}
					} 
					
					// if the player touches a tile with type 'w', they gain a wing power-up
					if (map.tiles[r][c].getType() == 'w' && GamePanel.onScreen(r,c)) {
						if (checkCollision(left, right, top, bottom, tile)) {
							powerup = "wing";
							map.tiles[r][c] = new Tile(r, c, c * Tile.TILESIZE, r * Tile.TILESIZE, '.', map);
						}
					}
				}
			}
		
			for (int i = 0; i < map.enemies.size(); i++) {
				checkCollision(left, right, top, bottom, map.enemies.get(i), map);
			}
		}
		
		// if x movement is not blocked, move x by dx
		if (canMoveX) {
			x += dx;
		} else {
			dx = 0;
		}
		
		// if y movement is not blocked, move y by dy
		if (canMoveY) {
			y += dy;
		} else {
			dy = 0;
		}
		
		// switches direction depending on whether your dx is positive or negative
		if (dx > 0) {
			direction_x = 1;
		} else if (dx < 0) {
			direction_x = -1;
		}
		
		// decreases the cooldown for power-ups
		if (cooldown > 0) {
			cooldown--;
		}
	}
	
	// method for checking collisions between the player and a tile, given the tile and the player's four sides
	public boolean checkCollision(double left, double right, double top, double bottom, Tile tile) {
		return right > tile.getTX() && left < tile.getTX() + tile.getTW() && bottom > tile.getTY() && top < tile.getTY() + tile.getTH();
	}
	
	// method for checking collisions between the player and an enemy, given the tile and the player's four sides
	public void checkCollision(double left, double right, double top, double bottom, Enemy enemy, Map map) {
		if (enemy.getSpawned() && right + dx > enemy.getX() && left + dx < enemy.getX() + enemy.getW() && bottom + dy > enemy.getY() && top + dy < enemy.getY() + enemy.getH()) {
			
			// if the player is above the enemy when they collide, the enemy dies
			if (enemy.getAlive() && bottom < enemy.getY() && enemy.getType() != 'H') {
				for (int i = 0; i < map.enemies.size(); i++) {
					if (map.enemies.get(i).equals(enemy)) {
						map.enemies.get(i).death();
					}
				}
				dy = -15;
				GamePanel.score += 10;
			} 
			
			// if not, the player dies
			else if (enemy.getAlive()) {
				death();
			}
		}
	}
	
	// method for creating a fireball projectile for the player with a cooldown of 20 frames
	public void shootFireball() {
		if (cooldown == 0) {
			projectiles.add(new Projectile(x, y, direction_x * 10, 0, 'F'));
			cooldown = 20;
		}
	}
	
	// method for creating a boomerang projectile for the player with a limit of one on the map
	public void shootBoomerang() {
		if (projectiles.size() == 0) {
			projectiles.add(new Projectile(x, y, direction_x * 20, 0, 'B'));
		}
	}
	
	// method for toggling variables when the player dies
	public void death() {
		dx = 0;
		dy = -10;
		alive = false;
		collidable = false;
	}
	
	
	// method for drawing the player on the screen
	public void draw(Graphics g) {
		
		// depending on both your x and y direction and the power-up you currently have, the sprite drawn for the player will be different
		int direction_y;
		if (dy > 0) {
			direction_y = 1;
		} else if (dy < 0) {
			direction_y = -1;
		} else {
			direction_y = 0;
		}
		
		if (powerup.equals("nothing")) {
			img = new ImageIcon(apple.getSubimage(25+direction_x*PLAYERSIZE/2,50+direction_y*PLAYERSIZE,50,50));
		} else if (powerup.equals("fire")) {
			img = new ImageIcon(fireapple.getSubimage(25+direction_x*PLAYERSIZE/2,50+direction_y*PLAYERSIZE,50,50));
		} else if (powerup.equals("boomerang")) {
			img = new ImageIcon(boomerangapple.getSubimage(25+direction_x*PLAYERSIZE/2,50+direction_y*PLAYERSIZE,50,50));
		} else if (powerup.equals("wing")) {
			img = new ImageIcon(wingapple.getSubimage(25+direction_x*PLAYERSIZE/2,50+direction_y*PLAYERSIZE,50,50));
		}
		
		g.drawImage(img.getImage(), (int)x - GamePanel.gamex, (int)y - GamePanel.gamey, w, h, null);
	}
}