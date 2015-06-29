package com.algorim.treegrowth.objects;

import com.algorim.treegrowth.Constants;
import com.algorim.treegrowth.treedetection.TreeDetector;

import net.minecraft.world.chunk.Chunk;

public class Tree {
	public Coord3i c1, c2;
	//private int woodID, leaveID;
	private Chunk chunk;
	public Tree(Chunk chunk, int x1, int x2, int y1, int y2, int z1, int z2, int woodID, int leaveID) {
		c1 = new Coord3i(x1,y1,z1);
		c2 = new Coord3i(x2,y2,z2);
//		this.woodID = woodID;
//		this.leaveID = leaveID;
		this.chunk = chunk;
	}
	
	public Tree(Chunk chunk, int x1, int x2, int y1, int y2, int z1, int z2) {
		c1 = new Coord3i(x1,y1,z1);
		c2 = new Coord3i(x2,y2,z2);
		this.chunk = chunk;
	}
	
	public Tree(int x, int y, int z) {
		c1 = new Coord3i(x,y,z);
		c2 = new Coord3i(x,y,z);
	}
	
	
	
//	public int getLeaveID() {
//		return leaveID;
//	}
//
//	public void setLeaveID(int leaveID) {
//		this.leaveID = leaveID;
//	}
//	
//	public int getWoodID() {
//		return woodID;
//	}
//
//	public void setWoodID(int woodID) {
//		this.woodID = woodID;
//	}
	
	public Coord3i getPosition() {
		return getCoord1();
	}
	
	public Coord3i getDimension() {
		return new Coord3i(Math.abs(c2.x-c1.x),
				Math.abs(c2.y-c1.y),
				Math.abs(c2.z-c1.z));
	}
	public int getSize() {
		return Math.max(Math.abs(c2.x-c1.x), Math.abs(c2.z-c1.z))+1;
	}
	
	public boolean validate() {
		return 	c2.x-c1.x >= 0 &&
				c2.x-c1.x == c2.z-c1.z && 
				c2.y-c1.y >= Constants.MIN_TREE_HEIGHT;
	}
	
	public Coord3i getCoord1() {
		return c1;
	}
	public Coord3i getCoord2() {
		return c2;
	}
	public void setCoord1(Coord3i c) {
		c1 = new Coord3i(c);
	}
	public void setCoord2(Coord3i c) {
		c2 = new Coord3i(c);
	}
	@Override
	public boolean equals(Object object) {
		if(object == null) return false;
		if(getClass() != object.getClass()) return false;
		final Tree tree = (Tree)object;
//		System.out.println(c1+" "+c2);
//		System.out.println("IsEqual?: "+(c1.equals(tree.getCoord1()) &&
//				c2.equals(tree.getCoord2())));
		return c1.equals(tree.getCoord1()) &&
				c2.equals(tree.getCoord2());
	}
	
//	public Coord3i getAbsCoord1() {
//		return new Coord3i((chunk.xPosition << 4) + c1.x,
//				c1.y,
//				(chunk.zPosition << 4) + c1.z);
//	}
//	public Coord3i getAbsCoord2() {
//		return new Coord3i((chunk.xPosition << 4) + c2.x,
//				c2.y,
//				(chunk.zPosition << 4) + c2.z);
//	}
	public String toString() {
		//return c1.x+":"+c1.y+":"+c1.z+" "+c2.x+":"+c2.y+":"+c2.z+" w"+woodID+" l"+leaveID;
		Coord3i ac1 = getCoord1();
		Coord3i ac2 = getCoord2();
		return ac1.x+":"+ac1.y+":"+ac1.z+" "+ac2.x+":"+ac2.y+":"+ac2.z;
	}
	
	public int getSpaceRequirement() {
		return getSize()*7-2;
	}
	
	public int getGrowthDimension() {
		return getSpaceRequirement()+8;
	}
	
	
}
