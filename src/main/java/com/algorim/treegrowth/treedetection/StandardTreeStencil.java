package com.algorim.treegrowth.treedetection;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.algorim.treegrowth.config.Constants;
import com.algorim.treegrowth.utilities.Common;
import com.algorim.treegrowth.utilities.Tree;

/**
 * A tree stencil for 1x1 standard trees.
 * 
 * TopStencil:
 * Checks, if the top stencil fits.
 * The first block should be wood.
 * The above blocks can be everything up to the MAX_GAP_TOP
 * The last block should be leaves.
 * 
 * TrunkStencil:
 * 1x1 Trunk
 * Checks if the minimum width is indeed 1
 * 
 * BottomStencil:
 * First block wood
 * A Gap of any material (MAX_GAP_BOT)
 * Last block dirt
 * 
 * @author xeedness
 * @see ITreeStencil
 */
public class StandardTreeStencil implements ITreeStencil {
	public static final int MAX_GAP_TOP = 1;
	public static final int MAX_GAP_BOT = 0;
	@Override
	public boolean topFits(Chunk chunk, int x, int y, int z) {
		boolean fits = false;
		if(Common.isWoodLog(chunk, x, y, z)) {
			int depth = 0;
			while(fits == false && depth <= MAX_GAP_BOT) {
				if(Common.isLeave(chunk, x, y+1+depth, z)) fits = true;
				if(!Common.isAir(chunk, x, y+1+depth, z)) break;
				depth++;
			}
		}
		return fits;
	}



	@Override
	public boolean trunkFits(Chunk chunk, Tree tree) {
		boolean fits = false;
		int x = tree.c1.x;
		int z = tree.c1.z;
		//Negative condition. If the trunk has nowhere the size 1, it is no standard trunk.
		//TODO Need positive condition?
		for(int y = tree.c1.y; y <= tree.c2.y-2; y++) {
			
			if(	!Common.isWoodLog(chunk, x+1, y, z) &&
				!Common.isWoodLog(chunk, x, y, z+1) && 
				!Common.isWoodLog(chunk, x-1, y, z) &&
				!Common.isWoodLog(chunk, x, y, z-1)) {
				
				fits = true;
				break;
			}
		
		}
		
		//Dimensions
		if((tree.c2.y - tree.c1.y)+1 < Constants.MIN_TREE_HEIGHT) fits = false;
		
		return fits;
	}

	
	@Override
	public boolean inflate(Chunk chunk, Tree tree) {
		if(!Common.isWoodLog(chunk, tree.c2.x, tree.c2.y, tree.c2.z)) return false;
		
		while(Common.isWoodLog(chunk, tree.c1.x, tree.c1.y, tree.c1.z)) {
			tree.c1.y--;
		}
		tree.c1.y++;
		
		return true;
	}

	@Override
	public boolean bottomFits(Chunk chunk, int x, int y, int z) {
		boolean fits = false;
		
		if(Common.isWoodLog(chunk, x, y, z)) {
			int depth = 0;
			while(fits == false && depth <= MAX_GAP_BOT) {
				if(Common.isDirt(chunk, x, y-1-depth, z)) fits = true;
				if(!Common.isAir(chunk, x, y-1-depth, z)) break;
				depth++;
			}
		}
		return fits;
	}

	

}
