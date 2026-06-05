/*
Name: Allen He // Date: 01/17/24 // Projectile.java
Course: ICS3U7-02 with Ms. Strelkovska
Description: Projectile class for creating and handling the projectiles used by the player and the enemies in the game.
*/

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.event.*;

public class Projectile {
	
	// instance variable declaration
	private double x, y, dx, dy;
	private int w, h, lifespan, direction, count;
	private char type;
	private boolean applyGravity;
	private String state;
	
	private static BufferedImage fireball = null, boomerang = null;
	private ImageIcon img;
	
	
	// constructor for creating projectiles with parameters of x, y, dx, dy, and type
	public Projectile(double x, double y, double dx, double dy, char type){
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.lifespan = 300;
		this.state = "active";
		this.count = 0;
		
		// depending on the type of the projectile, they will have different properties
		this.type = type;
		
		// the 'B' projectile is larger than the others
		if (type == 'B') {
			this.w = 50;
			this.h = 50;
		} else {
			this.w = 20;
			this.h = 20;
		}
		
		// the 'F' projectile has gravity applied to it
		if (type == 'F') {
			applyGravity = true;
		} else {
			applyGravity = false;
		}
		
		if (dx > 0) {
			this.direction = 1;
		} else {
			this.direction = -1;
		}
		
		// if sprite images have not been loaded, load them
		if (type == 'E') {
			this.img = new ImageIcon("images/projectile.png");
		} else if (type == 'F') {
			if (fireball == null) {
				try {
					fireball = ImageIO.read(new File("images/fireball.png"));
				} catch (Exception e) {
					System.out.println("File \"fireball.png\" not found.");
				}
			}
		} else if (type == 'B') {
			if (boomerang == null) {
				try {
					boomerang = ImageIO.read(new File("images/boomerang.png"));
				} catch (Exception e) {
					System.out.println("File \"boomerang.png\" not found.");
				}
			}
		}
	}
	
	
	// accessor and modifier methods for revelant instance variables
	public double getX()  {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getDX() {
		return dx;
	}
	
	public double getDY() {
		return dy;
	}
	
	public char getType() {
		return type;
	}
	
	public int getLifespan() {
		return lifespan;
	}
	
	public boolean getApplyGravity() {
		return applyGravity;
	}
	
	public String getState() {
		return state;
	}
	
	public void setInactive() {
		this.state = "inactive";
		this.dx = 0;
	}
	
	// method for determining if the projectile collides with a tile
	public boolean collidesWith(Tile[][] tiles) {
		
		// check for collisions if "active"
		if (state.equals("active")) {
			for (int r = 0; r < tiles.length; r++) {
				for (int c = 0; c < tiles[r].length; c++) {
					
					// if the projectile collides with a solid tile, return true
					if (tiles[r][c].getSolid() == 2 && x+w > tiles[r][c].getTX() && x < tiles[r][c].getTX() + tiles[r][c].getTW() && y+h > tiles[r][c].getTY() && y < tiles[r][c].getTY() + tiles[r][c].getTH()) {
						
						// there is an exception is the projectile is of type 'F' and on top of the tile, in which false is returned
						if (type == 'F' && y + h - dy + 1 < tiles[r][c].getTY()) {
							dy = -10;
							return false;
						}
						
						// if the projectile is of type 'B', set it to "inactive" before returning true
						else if (type == 'B') {
							state = "inactive";
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	
	// method for checking if the projectile collides with a specified player (or enemy)
	public boolean collidesWith(Player player) {
		return x+w > player.getX() && x < player.getX() + player.getW() && y+h > player.getY() && y < player.getY() + player.getH();
	}
	
	// method that calculates the dy for a certain dx for a projectile to return to a specified player
	public void returnTo(Player player) {
		dy = dx * (y - player.getY()) / (x - player.getX());
	}
	
	// method for applying gravity
	public void applyGravity() {
		dy += 1;
	}
	
	// method for moving the projectile
	public void move() {
		
		// change the dx of the boomerang according to its direction and set it to "inactive" during its return
		if (type == 'B') {
			dx -= direction * 0.5;
			if (direction == 1 && dx <= 0 || direction == -1 && dx >= 0) {
				state = "inactive";
			}
		}
		
		// move by dx and dy and decrease lifespan
		x += dx;
		y += dy;
		lifespan -= 1;
	}
	
	// method for drawing the projectile on the screen
	public void draw(Graphics g) {
		
		// depending on the value of count, projectiles can have a different sprite
		count += 1;
		if (type == 'B') {
			img = new ImageIcon(boomerang.getSubimage((count / 2 % 8) * 50,0,50,50));
		} else if (type == 'F') {
			img = new ImageIcon(fireball.getSubimage((count % 2) * 20,0,20,20));
		}
		g.drawImage(img.getImage(), (int)x - GamePanel.gamex, (int)y - GamePanel.gamey, w, h, null);
	}
}