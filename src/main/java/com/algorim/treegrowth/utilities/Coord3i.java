package com.algorim.treegrowth.utilities;

public class Coord3i {
	public int x,y,z;
	public Coord3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Coord3i(Coord3i c) {
		x = c.x;
		y = c.y;
		z = c.z;
	}
	
	public boolean equals(Coord3i c) {
		return x == c.x && y == c.y && z == c.z;
	}
	
	public String toString() {
		return x+","+y+","+z;
	}
}
