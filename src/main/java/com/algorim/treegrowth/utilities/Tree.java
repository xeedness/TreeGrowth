package com.algorim.treegrowth.utilities;

import com.algorim.treegrowth.config.Constants;
import com.algorim.treegrowth.treedetection.ITreeStencil;
import com.algorim.treegrowth.treedetection.TreeDetector;

import net.minecraft.world.chunk.Chunk;

/**
 * This class is a representation of an actual tree in game. It contains the coordinates of the whole trunk.
 * 
 * @author xeedness
 *
 */
public class Tree {
	public Coord3i c1, c2;
	private Chunk chunk;
	private ITreeStencil stencil;
	
	public Tree(Chunk chunk, ITreeStencil stencil, int x1, int x2, int y1, int y2, int z1, int z2) {
		c1 = new Coord3i(x1,y1,z1);
		c2 = new Coord3i(x2,y2,z2);
		this.chunk = chunk;
	}
	
	public Tree(Chunk chunk, ITreeStencil stencil, int x, int y, int z) {
		this(chunk, stencil, x,y,z,x,y,z);
	}
	
	/**
	 * Optains the lower south west coordinate.
	 * @return 
	 */
	public Coord3i getPosition() {
		return getCoord1();
	}
	
	public Coord3i getDimension() {
		return new Coord3i(Math.abs(c2.x-c1.x),
				Math.abs(c2.y-c1.y),
				Math.abs(c2.z-c1.z));
	}
	
	/**
	 * Optains the maximum dimension.
	 * @return
	 */
	public int getSize() {
		return Math.max(Math.abs(c2.x-c1.x), Math.abs(c2.z-c1.z))+1;
	}
	
	/**
	 * Checks if:
	 * c2.x-c1.x >= 0
	 * c2.x-c1.x == c2.z-c1.z
	 * c2.y-c1.y >= {@link com.algorim.treegrowth.Constants.MIN_TREE_HEIGHT}
	 * @return
	 */
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

		return c1.equals(tree.getCoord1()) &&
				c2.equals(tree.getCoord2());
	}
	
	public String toString() {
		Coord3i ac1 = getCoord1();
		Coord3i ac2 = getCoord2();
		return ac1.x+":"+ac1.y+":"+ac1.z+" "+ac2.x+":"+ac2.y+":"+ac2.z;
	}
	
	/**
	 * Returns the Space required by the tree
	 * @return
	 */
	public int getSpaceRequirement() {
		//TODO This is not right. Consider measurement by actual leaves blocks
		return getSize()*7-2;
	}
	
	/**
	 * Returns the dimension of the space where new saplings can spawn.
	 * @return
	 */
	public int getGrowthDimension() {
		return getSpaceRequirement()+8;
	}
	
	
}
