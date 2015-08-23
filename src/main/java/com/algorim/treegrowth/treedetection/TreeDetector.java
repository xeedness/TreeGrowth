package com.algorim.treegrowth.treedetection;

import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLLog;

import com.algorim.treegrowth.config.Constants;
import com.algorim.treegrowth.manager.GrowthDataProvider;
import com.algorim.treegrowth.utilities.Common;
import com.algorim.treegrowth.utilities.Timer;
import com.algorim.treegrowth.utilities.Tree;
import com.algorim.treegrowth.utilities.TreeData;

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
		stencils.add(new LargeTreeStencil(2));
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
				int y = chunk.getHeight(xI, zI);
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
								if(stencil.bottomFits(chunk, tree.c1.getX(), tree.c1.getY(), tree.c1.getZ()) && stencil.trunkFits(chunk, tree)) {
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
	
	/**
	 * Checks if a stencil fits the tree at the given coordinates
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public void applyStencils(World world, int x, int y, int z) {
		if(!world.isRemote) { 
			Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x,0, z));
			int depth = 0;
			
			outerloop:
			//Iterate through different heights (y)
			do {
				//Check if block is woodlog (tree top coordinate should be always wood)
				if(Common.isWoodLog(chunk, x, y-depth, z)) {
					for(ITreeStencil stencil : stencils) {
						if(stencil.topFits(chunk, x, y-depth, z)) {
							Tree tree = new Tree(chunk, stencil, x, y-depth, z);
							stencil.inflate(chunk, tree);
							if(stencil.bottomFits(chunk, tree.c1.getX(), tree.c1.getY(), tree.c1.getZ()) && stencil.trunkFits(chunk, tree)) {
								FMLLog.log(Level.INFO, "TreeGrowth tree structure: "+tree);
								TreeData treeData = GrowthDataProvider.getInstance().getTreeData(chunk,tree);
								if(treeData == null) {
									FMLLog.log(Level.INFO, "TreeGrowth found tree structure. "+
								"wood: ("+Common.getBlockIDAbs(chunk, tree.c1.getX(), tree.c1.getY(), tree.c1.getZ())+":"+Common.getBlockMetadataAbs(chunk, tree.c1.getX(), tree.c1.getY(), tree.c1.getZ())+"),"+
								"leaves: ("+Common.getBlockIDAbs(chunk, tree.c2.getX(), tree.c2.getY()+1, tree.c2.getZ())+":"+Common.getBlockMetadataAbs(chunk, tree.c2.getX(), tree.c2.getY()+1, tree.c2.getZ())+")");
								} else {
									FMLLog.log(Level.INFO, "TreeGrowth identified tree. "+treeData);
								}
								
								return;
							}
						}
					}
				}
				depth++;
			} while(depth < Constants.MAX_SEARCH_DEPTH);
			String blocks = "";
			for(int i=0;i<10; i++) {
				blocks +=
						"("+Common.getBlockIDAbs(chunk, x, y-i, z)+":"+Common.getBlockMetadataAbs(chunk, x, y-i, z)+"), ";
		
			}
			FMLLog.log(Level.INFO, "TreeGrowth could not find tree. Blocks: "+blocks);
		}
	}
}
