package com.algorim.treegrowth.treedetection;

import java.util.concurrent.Callable;

import net.minecraft.util.BlockPos;
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
	public static final int MAX_GAP_TOP = 2;
	public static final int MAX_GAP_BOT = 2;
	
	private int size;
	
	LargeTreeStencil(int size) {
		this.size = size;
	}
	@Override
	public boolean topFits(Chunk chunk, int x, int y, int z) {
		boolean fits = false;
		if(Common.isWoodLog(chunk, x, y, z)) {
			if(Common.isWoodLogLayer(chunk, x, y, z,size)) {
				int depth = 0;
				while(fits == false && depth <= MAX_GAP_TOP) {
					if(Common.isLeavesLayer(chunk, x, y+1+depth, z, size)) fits = true;
					//if(!Common.isAirLayer(chunk, x, y+1+depth, z, size)) break;
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
		for(int y=tree.c1.getY(); y <= tree.c2.getY(); y++) {
			if(!Common.isWoodLogLayer(chunk, tree.c1.getX(), y, tree.c1.getZ(), size))	{
				fits = false;
				break;
			}
		}
		//Dimensions
		if((tree.c2.getY() - tree.c1.getY())+1 < Constants.MIN_TREE_HEIGHT) fits = false;
		return fits;
	}

	@Override
	public boolean inflate(Chunk chunk, Tree tree) {
		boolean success = false;
		if(Common.isWoodLogLayer(chunk, tree.c1.getX(), tree.c1.getY(), tree.c1.getZ(), size)) {
			tree.c2 = new BlockPos(tree.c1.getX()+1, tree.c1.getY(), tree.c1.getZ()+1);
			do {
				tree.c1 = tree.c1.add(0,-1,0);
			} while(Common.isWoodLogLayer(chunk, tree.c1.getX(), tree.c1.getY(), tree.c1.getZ(), size));
			tree.c1 = tree.c1.add(0,1,0);
		}
		return success;
	}

	@Override
	public boolean bottomFits(Chunk chunk, int x, int y, int z) {
		boolean fits = false;
		if(Common.isWoodLogLayer(chunk, x, y, z, size)) {
			int depth = 0;
			while(fits == false && depth <= MAX_GAP_BOT) {
				for(int xI = x; xI < x + size; xI++) {
					for(int zI = z; zI < z + size; zI++) {
						fits |= Common.isDirt(chunk, xI, y-1-depth, zI); 
					}
				}
				//if(!Common.isAirLayer(chunk, x, y+1+depth, z, size)) break;
				depth++;
			}
		}
		return fits;
	}




}
