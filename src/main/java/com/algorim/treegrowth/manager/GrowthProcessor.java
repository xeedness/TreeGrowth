package com.algorim.treegrowth.manager;

import java.util.ArrayList;
import java.util.Random;








import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLLog;

import com.algorim.treegrowth.config.Constants;
import com.algorim.treegrowth.treedetection.TreeDetector;
import com.algorim.treegrowth.utilities.Common;
import com.algorim.treegrowth.utilities.Tree;
import com.algorim.treegrowth.utilities.TreeData;

/**
 * Main processing class. It it used, when a chunk needs to be processed.
 * 
 * @author xeedness
 *
 */
public class GrowthProcessor {
	private static GrowthProcessor instance;
	private GrowthDataProvider mGrowthDataProvider;
	private GrowthProcessor() {
		mGrowthDataProvider = GrowthDataProvider.getInstance();
	}
	
	static public GrowthProcessor getInstance() {
		if(instance == null) instance = new GrowthProcessor();
		return instance;
	}
	
	/**
	 * Enables or disables the automatic processing
	 */
	public void toggleAutoProcessing() {
		Constants.AUTO_PROCESSING_ENABLED= !Constants.AUTO_PROCESSING_ENABLED;
		if(!Constants.AUTO_PROCESSING_ENABLED)
			FMLLog.log(Level.INFO, "Disabled autoProcessing");
		else
			FMLLog.log(Level.INFO, "Enabled autoProcessing");
	}
	
	/**
	 * Analyses a chunks tree and fertility structure and spawns new trees
	 * 
	 * @param chunk
	 * @return
	 */
	public boolean processChunk(Chunk chunk) {
		if(chunk.getWorld().isRemote) return true;
		//System.out.println("Processing Chunk: "+chunk.xPosition+" "+chunk.zPosition);
		ArrayList<Tree> trees = TreeDetector.getInstance().findTrees(chunk);
		for(Tree tree : trees) {
			//System.out.println("Found a tree: "+tree+" valid: "+tree.validate()+", size:"+tree.getSize());
			if(tree.validate()) {
//				int xDim=0, zDim=0;
//				int x = tree.c1.x-1;
//				int z = tree.c1.z;
//				int y = tree.c2.y-2;
//				while(Common.isLeave(chunk, x, y, z) || Common.isWoodLog(chunk, x, y, z) ) {
//					xDim++;
//					x--;
//				}
//				x = tree.c2.x+1;
//				z = tree.c2.z;
//				while(Common.isLeave(chunk, x, y, z) || Common.isWoodLog(chunk, x, y, z) ) {
//					xDim++;
//					x++;
//				}
//				
//				x = tree.c1.x;
//				z = tree.c1.z-1;
//				while(Common.isLeave(chunk, x, y, z) || Common.isWoodLog(chunk, x, y, z) ) {
//					zDim++;
//					z--;
//				}
//				
//				x = tree.c2.x;
//				z = tree.c2.z+1;
//				while(Common.isLeave(chunk, x, y, z) || Common.isWoodLog(chunk, x, y, z) ) {
//					zDim++;
//					z++;
//				}
//				xDim += tree.getSize();
//				zDim += tree.getSize();
//				
//				System.out.println("Tree dims: ("+xDim+","+zDim+") "+tree.getSpaceRequirement()+" -> "+Math.min(xDim, zDim));
//						
				
				TreeProcessor treeProcessor = new TreeProcessor(chunk, tree);
				treeProcessor.process();

				TreeData treeData = mGrowthDataProvider.getTreeData(chunk,tree);
				if(treeData != null && !chunk.getWorld().isRemote) {
					if(treeProcessor.getHeighestRating() < Constants.RATING_THRESHOLD) {
						//System.out.println("Skipped tree due to heighestRating: "+treeProcessor.getHeighestRating());
						continue;
					}
					if(treeProcessor.getAridity() > Constants.ARIDITY_THRESHOLD) {
						//System.out.println("Skipped tree due to aridity: "+treeProcessor.getAridity());
						continue;
					}
					
					int i = new Random().nextInt(treeProcessor.getCandidates().size());
					BlockPos c = treeProcessor.getCandidates().get(i);
					IBlockState sap = treeData.getSapling();
					float f = new Random().nextFloat();
					if(f < treeData.getFertility()) { 
						World world = chunk.getWorld();
						Block test;
						BlockPos pos = new BlockPos(c);
						
						chunk.getWorld().setBlockState(pos, sap);
						FMLLog.log(Level.INFO,"Spawned new sapling");
						//chunk.getWorld().setBlockState
						//chunk.getWorld().setBlock(c.x, c.y, c.z, sap.itemId, sap.getItemDamage(), 2);
						//((BlockSapling)Block.sapling).growTree(chunk.worldObj, c.x, c.y, c.z, chunk.worldObj.rand);
						//System.out.println("Spawned new Tree.");
					}
				}				
			} else {
				Common.log("GrowthProcessor","Tree not valid.", tree.getCoord1(), tree.getCoord2());
			}
		}
		
		return true;
	}
	
	/**
	 * @see com.algorim.treegrowth.treedetection.TreeDetector#applyStencils(World, int, int, int)
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public void applyStencils(World world, int x, int y, int z) {
		TreeDetector.getInstance().applyStencils(world, x, y, z);
	}
}
