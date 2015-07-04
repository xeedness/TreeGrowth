package com.algorim.treegrowth.treedetection;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.algorim.treegrowth.config.Constants;
import com.algorim.treegrowth.utilities.Common;
import com.algorim.treegrowth.utilities.Tree;

/**
 * A tree stencil for 2x2 trees.
 * 
 * TopStencil:
 * Checks, if the top stencil fits.
 * The first layer should be all wood.
 * The above layers can be everything up to the MAX_GAP_TOP
 * The last layer should be all Leaves.
 * 
 * TrunkStencil:
 * 2x2 Trunk
 * 
 * BottomStencil:
 * First layer all wood
 * A Gap of any material (MAX_GAP_BOT)
 * Last layer all dirt
 * 
 * @author xeedness
 * @see ITreeStencil
 */
public class LargeTreeStencil implements ITreeStencil {
	//TODO This is super random
	public static final int MAX_GAP_TOP = 2;
	public static final int MAX_GAP_BOT = 2;
	
	/**
	 * Checks, if the whole layer of the given coordinate is wood
	 * 
	 * @param chunk
	 * @param x Absolute X Coordinate
	 * @param y Absolute Y Coordinate
	 * @param z Absolute Z Coordinate
	 * @return
	 */
	private boolean isWoodLogLayer(Chunk chunk,int x,int y,int z) {
		return Common.isWoodLog(chunk, x, y, z) && 
				Common.isWoodLog(chunk, x+1, y, z) && 
				Common.isWoodLog(chunk, x+1, y, z+1) && 
				Common.isWoodLog(chunk, x, y, z+1);
	}
	/**
	 * Checks, if the whole layer of the given coordinate are leaves
	 * 
	 * @param chunk
	 * @param x Absolute X Coordinate
	 * @param y Absolute Y Coordinate
	 * @param z Absolute Z Coordinate
	 * @return
	 */
	private boolean isLeavesLayer(Chunk chunk,int x,int y,int z) {
		return Common.isLeave(chunk, x, y, z) && 
				Common.isLeave(chunk, x+1, y, z) && 
				Common.isLeave(chunk, x+1, y, z+1) && 
				Common.isLeave(chunk, x, y, z+1);
	}
	/**
	 * Checks, if the whole layer of the given coordinate is dirt
	 * 
	 * @param chunk
	 * @param x Absolute X Coordinate
	 * @param y Absolute Y Coordinate
	 * @param z Absolute Z Coordinate
	 * @return
	 */
	private boolean isDirtLayer(Chunk chunk,int x,int y,int z) {
		return Common.isDirt(chunk, x, y, z) && 
				Common.isDirt(chunk, x+1, y, z) && 
				Common.isDirt(chunk, x+1, y, z+1) && 
				Common.isDirt(chunk, x, y, z+1);
	}
	


	@Override
	public boolean topFits(Chunk chunk, int x, int y, int z) {
		boolean fits = false;
		if(Common.isWoodLog(chunk, x, y, z)) {
			if(isWoodLogLayer(chunk, x, y, z)) {
				int depth = 0;
				while(fits == false && depth <= MAX_GAP_TOP) {
					if(isLeavesLayer(chunk, x, y+1+depth, z)) fits = true;
					//TODO Maybe some restriction what may be in the gap
					depth++;
				}
			}
		}
		return fits;
	}


	@Override
	public boolean trunkFits(Chunk chunk, Tree tree) {
		boolean fits = true;
		
		//Maybe add a negative block condition later (for example if 3x3 is introduced)
		for(int y=tree.c1.y; y <= tree.c2.y; y++) {
			if(!isWoodLogLayer(chunk, tree.c1.x, y, tree.c1.z))	{
				fits = false;
				break;
			}
		}
		//Dimensions
		if((tree.c2.y - tree.c1.y)+1 < Constants.MIN_TREE_HEIGHT) fits = false;
		return fits;
	}

	@Override
	public boolean inflate(Chunk chunk, Tree tree) {
		boolean success = false;
		if(isWoodLogLayer(chunk, tree.c1.x, tree.c1.y, tree.c1.z)) {
			tree.c2.x = tree.c1.x+1;
			tree.c2.z = tree.c1.z+1;
			do {
				tree.c1.y--;
			} while(isWoodLogLayer(chunk, tree.c1.x, tree.c1.y, tree.c1.z));
			tree.c1.y++;
		}
		return success;
	}

	@Override
	public boolean bottomFits(Chunk chunk, int x, int y, int z) {
		boolean fits = false;
		if(isWoodLogLayer(chunk, x, y, z)) {
			int depth = 0;
			while(fits == false && depth <= MAX_GAP_BOT) {
				if(isDirtLayer(chunk, x, y-1-depth, z)) fits = true;
				//TODO Maybe some restriction what may be in the gap
				depth++;
			}
		}
		return fits;
	}




}
