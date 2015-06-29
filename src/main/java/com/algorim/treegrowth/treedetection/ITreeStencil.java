package com.algorim.treegrowth.treedetection;

import com.algorim.treegrowth.objects.Tree;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;


/**
 * This class is an interface for the treedetection.
 * Derived classes are stencils for different tree structures.
 * 
 * @author xeedness
 */
public interface ITreeStencil {
	
	/**
	 * Checks if the given Coordinates fit the top of the tree
	 * 
	 * @param chunk
	 * @param x Absolute X Coordinate
	 * @param y Absolute Y Coordinate
	 * @param z Absolute Z Coordinate
	 * @return Returns true if the stencil fits
	 */
	public boolean topFits(Chunk chunk, int x, int y, int z);
	
	/**
	 * Checks if the given Tree fits the trunk of the tree
	 * 
	 * @param chunk
	 * @param tree
	 * @return Returns true if the stencil fits
	 */
	public boolean trunkFits(Chunk chunk, Tree tree);
	
	/**
	 * Checks if the given Coordinates fit the bottom of the tree
	 * 
	 * @param chunk
	 * @param x Absolute X Coordinate
	 * @param y Absolute Y Coordinate
	 * @param z Absolute Z Coordinate
	 * @return Returns true if the stencil fits
	 */
	public boolean bottomFits(Chunk chunk, int x, int y, int z);

	/**
	 * Inflates the given tree to reach the maximum valid size.
	 * 
	 * @param chunk
	 * @param tree A Tree with both coordinates starting at the top fitting position.
	 * @return Returns true if the inflation succeeded.
	 */
	public boolean inflate(Chunk chunk, Tree tree);
}
