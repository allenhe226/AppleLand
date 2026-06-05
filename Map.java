/*
Name: Allen He // Date: 01/17/24 // Map.java
Course: ICS3U7-02 with Ms. Strelkovska
Description: Map class for creating and handling the map that the player traverses in the game.
*/

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

// map class to create and handle levels and store arrays of tiles
public class Map {
	
	// variable declaration
	private Scanner filesc;
	public char[][] map;
	public Tile[][] tiles;
	public ArrayList<Enemy> enemies;
	
	// constructor for creating a map with a text file as a parameter
	public Map(String file) {
		
		try {
			// figures out the number of rows and columns in the text file
			int rows = 0, columns = 0;
			filesc = new Scanner(new File(file));
			while (filesc.hasNextLine()) {
				rows++;
				String line = filesc.nextLine();
				columns = line.length();
			}
			filesc.close();
			
			// creates 2D arrays according to the results
			map = new char[rows][columns];
			tiles = new Tile[rows][columns];
			filesc = new Scanner(new File(file));
			
			// populates the map array with characters from the text file
			int row = 0;
			while (filesc.hasNextLine()) {
				String line = filesc.nextLine();
				for (int i = 0; i < line.length(); i++) {
					map[row][i] = line.charAt(i);
				}
				row++;
			}
		} catch (Exception e) {
			System.out.print("Scanner not found.");
		}
		
		// populates the tiles array and enemies arraylist by iterating through every character on the map
		enemies = new ArrayList<Enemy>();
		for (int r = 0; r < map.length; r++) {
			for (int c = 0; c < map[r].length; c++) {
				tiles[r][c] = new Tile(r, c, c * Tile.TILESIZE, r * Tile.TILESIZE, map[r][c], this);
				if (map[r][c] == 'E' || map[r][c] == 'S' || map[r][c] == 'I' || map[r][c] == 'H' || map[r][c] == 'J') {
					enemies.add(new Enemy(r, c, c*Tile.TILESIZE, r*Tile.TILESIZE, map[r][c]));
				}
			}
		}
	}
	
	// method for determining the spawn point of the player
	public int[] spawnPoint() {
		for (int r = 0; r < map.length; r++) {
			for (int c = 0; c < map[r].length; c++) {
				if (map[r][c] == 'P')  {
					return new int[]{c, r};
				}
			}
		}
		return new int[]{0,0};
	} 
}