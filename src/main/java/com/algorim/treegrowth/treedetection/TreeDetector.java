package com.algorim.treegrowth.treedetection;

import java.util.ArrayList;

import com.algorim.treegrowth.config.Constants;
import com.algorim.treegrowth.manager.GrowthDataProvider;
import com.algorim.treegrowth.utilities.Common;
import com.algorim.treegrowth.utilities.Coord2i;
import com.algorim.treegrowth.utilities.Timer;
import com.algorim.treegrowth.utilities.Tree;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * This class is supposed to find trees in a chunk. 
 * For this it uses the {@link com.algorim.treegrowth.treedetection.ITreeStencil}.
 * 
 * @author xeedness
 *
 */
public class TreeDetector {
	StandardTreeStencil standardStencil;
	ArrayList<ITreeStencil> stencils;
	
	static TreeDetector instance;
	
	private TreeDetector() {
		standardStencil = new StandardTreeStencil();
		stencils = new ArrayList<ITreeStencil>();
		stencils.add(new LargeTreeStencil());
		stencils.add(new StandardTreeStencil());
	}
	
	static public TreeDetector getInstance() {
		if(instance == null) instance = new TreeDetector();
		return instance;
	}
	
	
	/**
	 * Finds position of trees in a chunk.
	 * 
	 * @param chunk
	 * @return list of trees
	 */
	public ArrayList<Tree> findTrees(Chunk chunk) {
		Timer.startTimer("FindTrees");
		ArrayList<Tree> trees = new ArrayList<Tree>();
		
		//Iterate through all coordinates of the chunk
		for(int xI=0; xI<16; xI++) {
			for(int zI=0; zI<16; zI++) {
				int x = (chunk.xPosition << 4)+xI;
				int z = (chunk.zPosition << 4)+zI;
				int y = chunk.getHeightValue(xI, zI);
				int depth = 0;
				boolean abort;
				
				outerloop:
				//Iterate through different heights (y)
				do {
					//Check if block is woodlog (tree top coordinate should be always wood)
					if(Common.isWoodLog(chunk, x, y-depth, z)) {
						for(ITreeStencil stencil : stencils) {
							if(stencil.topFits(chunk, x, y-depth, z)) {
								Tree tree = new Tree(chunk, stencil, x, y-depth, z);
								stencil.inflate(chunk, tree);
								if(stencil.bottomFits(chunk, tree.c1.x, tree.c1.y, tree.c1.z) && stencil.trunkFits(chunk, tree)) {
									//Check if the list already contains the tree. Add otherwise
									if(!trees.contains(tree)) trees.add(tree);
									
									//Continue with next coordinate
									break outerloop;
								}
							}
						}
					}
					depth++;
				} while(depth < Constants.MAX_SEARCH_DEPTH);
			}
		}
		
		Timer.stopTimer("FindTrees");
		return trees;
	}	
}
