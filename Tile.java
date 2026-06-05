/*
Name: Allen He // Date: 01/17/24 // Tile.java
Course: ICS3U7-02 with Ms. Strelkovska
Description: Tile class for creating and handling the individual tiles that make up the map in the game.
*/

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.event.*;

public class Tile {
	
	// variable declaration
	public static final int TILESIZE = 50;
	private int r, c, tx, ty, tw, th, solid;
	private char type;
	
	private static BufferedImage ground = null;
	private ImageIcon img;
	
	// constructor for creating a tile with parameters of r, c, tx, ty, type, and map
	public Tile(int r, int c, int tx, int ty, char type, Map map) {
		
		// initialize variables
		this.tx = tx;
		this.ty = ty;
		this.tw = TILESIZE;
		this.th = TILESIZE;
		this.r = r;
		this.c = c;
		
		// depending on the type of the tile, it will have different properties
		this.type = type;
		
		// any tile with type '.' or of a type for a player or enemy will remain blank
		if (type == '.' || type == 'P' || type == 'E' || type == 'S' || type == 'I' || type == 'H' || type == 'J') { // air
			img = new ImageIcon("images/blank.png");
			solid = 0;
		} 
		
		// any tile with type '#' will be ground that adjusts to its surroundings
		else if (type == '#') { // ground
			if (ground == null) {
				try {
					ground = ImageIO.read(new File("images/ground.png"));
				} catch (Exception e) {
					System.out.println("File \"ground.png\" not found.");
				}
			}
			int surroundings = checkSurroundings(r,c,map.map);
			img = new ImageIcon(ground.getSubimage(surroundings/10*TILESIZE,surroundings%10*TILESIZE, TILESIZE, TILESIZE));
			solid = 2;
		} 
		
		// any tile with type 'C' will be a coin
		else if (type == 'C') {
			img = new ImageIcon("images/coin.png");
			solid = 0;
		} 
		
		// any tile with type 'F', 'B', or 'W' will be a lucky block
		else if (type == 'F' || type == 'B' || type == 'W') {
			img = new ImageIcon("images/luckyblock.png");
			solid = 2;
		} 
		
		// any tile with type '*' will be a brick
		else if (type == '*') {
			img = new ImageIcon("images/brick.png");
			solid = 2;
		} 
		
		// any tile with type 'f' will be a fire power-up
		else if (type == 'f') {
			img = new ImageIcon("images/firepowerup.png");
			solid = 0;
		} 
		
		// any tile with type 'b' will be a boomerang power-up
		else if (type == 'b') {
			img = new ImageIcon("images/boomerangpowerup.png");
			solid = 0;
		} 
		
		// any tile with type 'w' will be a wing power-up
		else if (type == 'w') {
			img = new ImageIcon("images/wingpowerup.png");
			solid = 0;
		} 
		
		// any tile with type 'K' will be a spiked brick
		else if (type == 'K') {
			img = new ImageIcon("images/spikedbrick.png");
			solid = 2;
		} 
		
		// any tile with type 'X' will be the end brick;
		else if (type == 'X') {
			img = new ImageIcon("images/endbrick.png");
			solid = 2;
		}
	}
	
	// accessor and modifier methods for all relevant instance variables
	public int getR() {
		return r;
	}
	
	public int getC() {
		return c;
	}
	
	public int getTX() {
		return tx;
	}
	
	public int getTY() {
		return ty;
	}
	
	public int getTW() {
		return tw;
	}
	
	public int getTH() {
		return th;
	}
	
	public char getType() {
		return type;
	}
	
	public int getSolid() {
		return solid;
	}
	
	
	// method for checking the surroundings of ground tiles and assigning them the appropriate sprite
	private int checkSurroundings(int r, int c, char[][] tiles) {
		
		// using a hashmap, the program checks all 8 adjacent tiles to see if they are ground tiles as well
		// the program will then decide which sprite to give the tile to fit amongst the surrounding ground tiles
		HashMap<String,Boolean> d = new HashMap<String,Boolean>();
		d.put("n",isGround(r-1,c, tiles));
		d.put("e",isGround(r,c+1, tiles));
		d.put("s",isGround(r+1,c, tiles));
		d.put("w",isGround(r,c-1, tiles));
		d.put("ne",isGround(r-1,c+1, tiles));
		d.put("se",isGround(r+1,c+1, tiles));	
		d.put("sw",isGround(r+1,c-1, tiles));
		d.put("nw",isGround(r-1,c-1, tiles));
		
		// center cases
		if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w") && d.get("ne") && d.get("nw") && d.get("se") && d.get("sw")) {
			return 11;
		} else if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w") && d.get("ne") && d.get("se") && d.get("sw")) {
			return 45;
		} else if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w") && d.get("nw") && d.get("se") && d.get("sw")) {
			return 35;
		} else if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w") && d.get("ne") && d.get("nw") && d.get("se")) {
			return 44;
		} else if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w") && d.get("ne") && d.get("nw") && d.get("sw")) {
			return 34;
		} else if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w") && d.get("se") && d.get("sw")) {
			return 15;
		} else if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w") && d.get("ne") && d.get("nw")) {
			return 14;
		} else if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w") && d.get("ne") && d.get("se")) {
			return 64;
		} else if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w") && d.get("nw") && d.get("sw")) {
			return 54;
		} else if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w") && d.get("nw") && d.get("se")) {
			return 06;
		} else if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w") && d.get("ne") && d.get("sw")) {
			return 16;
		} else if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w") && d.get("ne")) {
			return 26;
		} else if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w") && d.get("nw")) {
			return 36;
		} else if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w") && d.get("se")) {
			return 46;
		} else if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w") && d.get("sw")) {
			return 56;
		} else if (d.get("n") &&  d.get("e") && d.get("s") && d.get("w")) {
			return 51;
		}
		
		// edge cases
		else if (d.get("n") &&  d.get("e") && d.get("w") && d.get("nw") && d.get("ne")) {
			return 12;
		} else if (d.get("s") &&  d.get("e") && d.get("w") && d.get("sw") && d.get("se")) {
			return 10;
		} else if (d.get("n") &&  d.get("s") && d.get("e") && d.get("ne") && d.get("se")) {
			return 01;
		} else if (d.get("n") &&  d.get("s") && d.get("w") && d.get("nw") && d.get("sw")) {
			return 21;
		} else if (d.get("n") &&  d.get("e") && d.get("w") && d.get("nw")) {
			return 55;
		} else if (d.get("n") &&  d.get("e") && d.get("w") && d.get("ne")) {
			return 65;
		} else if (d.get("s") &&  d.get("e") && d.get("w") && d.get("sw")) {
			return 53;
		} else if (d.get("s") &&  d.get("e") && d.get("w") && d.get("se")) {
			return 63;
		} else if (d.get("n") && d.get("s") && d.get("w") && d.get("sw")) {
			return 25;
		} else if (d.get("n") && d.get("s") && d.get("w") && d.get("nw")) {
			return 24;
		} else if (d.get("n") && d.get("s") && d.get("e") && d.get("se")) {
			return 05;
		} else if (d.get("n") && d.get("s") && d.get("e") && d.get("ne")) {
			return 04;
		} else if (d.get("n") &&  d.get("e") && d.get("w")) {
			return 52;
		} else if (d.get("s") &&  d.get("e") && d.get("w")) {
			return 50;
		} else if (d.get("n") &&  d.get("s") && d.get("w")) {
			return 61;
		} else if (d.get("n") &&  d.get("s") && d.get("e")) {
			return 41;
		} else if (d.get("n") && d.get("s")) {
			return 31;
		} else if (d.get("e") && d.get("w")) {
			return 13;
		}
		
		// corner cases
		else if (d.get("n") &&  d.get("e") && d.get("ne")) {
			return 02;
		} else if (d.get("s") &&  d.get("e") && d.get("se")) {
			return 00;
		} else if (d.get("s") &&  d.get("w") && d.get("sw")) {
			return 20;
		} else if (d.get("n") &&  d.get("w") && d.get("nw")) {
			return 22;
		} else if (d.get("n") &&  d.get("e")) {
			return 42;
		} else if (d.get("s") &&  d.get("e")) {
			return 40;
		} else if (d.get("s") &&  d.get("w")) {
			return 60;
		} else if (d.get("n") &&  d.get("w")) {
			return 62;
		} else if (d.get("e")) {
			return 03;
		} else if (d.get("w")) {
			return 23;
		} else if (d.get("s")) {
			return 30;
		} else if (d.get("n")) {
			return 32;
		} else {
			return 33;
		}
	}
	
	// method for checking whether a certain tile is ground
	private boolean isGround(int r, int c, char[][] tiles) {
		return r<0 || r>=tiles.length || c<0 || c>=tiles[r].length || tiles[r][c] == '#';
	}
	
	// method for drawing the tile on the screen
	public void draw(Graphics g) {
		g.drawImage(img.getImage(), tx-GamePanel.gamex, ty-GamePanel.gamey, tw, th, null);
	}
}