package com.algorim.treegrowth.objects;

public class Coord2i {
	public int x,y;
	public Coord2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Coord2i(Coord2i c) {
		x = c.x;
		y = c.y;
	}
	
	public boolean equals(Coord2i c) {
		return x == c.x && y == c.y;
	}
	
	public String toString() {
		return x+","+y;
	}
}
