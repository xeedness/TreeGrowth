package com.algorim.treegrowth.manager;

import java.util.ArrayList;
import java.util.Random;

import com.algorim.treegrowth.config.Constants;
import com.algorim.treegrowth.treedetection.TreeDetector;
import com.algorim.treegrowth.utilities.Common;
import com.algorim.treegrowth.utilities.Coord3i;
import com.algorim.treegrowth.utilities.Tree;
import com.algorim.treegrowth.utilities.TreeData;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.BonemealEvent;

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
			System.out.println("Disabled autoProcessing");
		else
			System.out.println("Enabled autoProcessing");
	}
	
	/**
	 * Analyses a chunks tree and fertility structure and spawns new trees
	 * 
	 * @param chunk
	 * @return
	 */
	public boolean processChunk(Chunk chunk) {
		
		System.out.println("Processing Chunk: "+chunk.xPosition+" "+chunk.zPosition);
		ArrayList<Tree> trees = TreeDetector.getInstance().findTrees(chunk);
		for(Tree tree : trees) {
			//System.out.println("Found a tree: "+tree+" valid: "+tree.validate()+", size:"+tree.getSize());
			if(tree.validate()) {
				
				TreeProcessor treeProcessor = new TreeProcessor(chunk, tree);
				treeProcessor.process();

				TreeData treeData = mGrowthDataProvider.getTreeData(chunk,tree);
				if(treeData != null && !chunk.worldObj.isRemote) {
					if(treeProcessor.getHeighestRating() < Constants.RATING_THRESHOLD) {
						System.out.println("Skipped tree due to heighestRating: "+treeProcessor.getHeighestRating());
						continue;
					}
					if(treeProcessor.getAridity() > Constants.ARIDITY_THRESHOLD) {
						System.out.println("Skipped tree due to aridity: "+treeProcessor.getAridity());
						continue;
					}
					if(treeProcessor.getFertility() < Constants.FERTILITY_THRESHOLD) {
						System.out.println("Skipped tree due to fertility: "+treeProcessor.getFertility());
						continue;
					}
					
					int i = new Random().nextInt(treeProcessor.getCandidates().size());
					Coord3i c = treeProcessor.getCandidates().get(i);
					ItemStack sap = treeData.getSapling();
					float f = new Random().nextFloat();
					if(f < treeData.getFertility()) { 
						chunk.worldObj.setBlock(c.x, c.y, c.z, sap.itemID, sap.getItemDamage(), 2);
						((BlockSapling)Block.sapling).growTree(chunk.worldObj, c.x, c.y, c.z, chunk.worldObj.rand);
						System.out.println("Spawned new Tree.");
					}
				}				
			} else {
				Common.log("GrowthProcessor","Tree not valid.", tree.getCoord1(), tree.getCoord2());
			}
		}
		
		return true;
	}
}
