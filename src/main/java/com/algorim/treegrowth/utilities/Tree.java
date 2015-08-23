package com.algorim.treegrowth.utilities;

import com.algorim.treegrowth.config.Constants;
import com.algorim.treegrowth.treedetection.ITreeStencil;
import com.algorim.treegrowth.treedetection.TreeDetector;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.world.chunk.Chunk;

/**
 * This class is a representation of an actual tree in game. It contains the coordinates of the whole trunk.
 * 
 * @author xeedness
 *
 */
public class Tree {
	public BlockPos c1, c2;
	private Chunk chunk;
	private ITreeStencil stencil;
	
	public Tree(Chunk chunk, ITreeStencil stencil, int x1, int y1, int z1, int x2, int y2, int z2) {
		c1 = new BlockPos(x1,y1,z1);
		c2 = new BlockPos(x2,y2,z2);
		this.chunk = chunk;
	}
	
	public Tree(Chunk chunk, ITreeStencil stencil, int x, int y, int z) {
		this(chunk, stencil, x,y,z,x,y,z);
	}
	
	/**
	 * Optains the lower south west coordinate.
	 * @return 
	 */
	public BlockPos getPosition() {
		return getCoord1();
	}
	
	public Vec3i getDimension() {	
		return 	new Vec3i(Math.abs(c2.getX()-c1.getX()),
				Math.abs(c2.getY()-c1.getY()),
				Math.abs(c2.getZ()-c1.getZ()));
	}
	
	/**
	 * Optains the maximum dimension.
	 * @return
	 */
	public int getSize() {
		return Math.max(Math.abs(c2.getX()-c1.getX()), Math.abs(c2.getZ()-c1.getZ()))+1;
	}
	
	/**
	 * Checks if:
	 * c2.x-c1.x >= 0
	 * c2.x-c1.x == c2.z-c1.z
	 * c2.y-c1.y >= {@link com.algorim.treegrowth.Constants.MIN_TREE_HEIGHT}
	 * @return
	 */
	public boolean validate() {
		return 	c2.getX()-c1.getX() >= 0 &&
				c2.getX()-c1.getX() == c2.getZ()-c1.getZ() && 
				c2.getY()-c1.getY() >= Constants.MIN_TREE_HEIGHT;
	}
	
	public BlockPos getCoord1() {
		return c1;
	}
	public BlockPos getCoord2() {
		return c2;
	}
	public void setCoord1(BlockPos c) {
		c1 = new BlockPos(c);
	}
	public void setCoord2(BlockPos c) {
		c2 = new BlockPos(c);
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
		BlockPos ac1 = getCoord1();
		BlockPos ac2 = getCoord2();
		return ac1.getX()+":"+ac1.getY()+":"+ac1.getZ()+" "+ac2.getX()+":"+ac2.getY()+":"+ac2.getZ();
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
