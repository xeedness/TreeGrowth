package com.algorim.treegrowth;

import java.io.File;





import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.algorim.treegrowth.config.Constants;
import com.algorim.treegrowth.config.IDConfiguration;
import com.algorim.treegrowth.events.ChunkEventHandler;
import com.algorim.treegrowth.items.GrowthItem;
import com.algorim.treegrowth.items.StencilItem;
import com.algorim.treegrowth.items.TreeGrowthConfigItem;
import com.algorim.treegrowth.manager.GrowthDataProvider;
import com.algorim.treegrowth.manager.GrowthProcessor;

public class CommonProxy {
	ChunkEventHandler mChunkEventHandler;
	GrowthDataProvider mGrowthDataProvider;
	GrowthProcessor mGrowthProcessor;
	
	public static GrowthItem mGrowthItem;
	public static TreeGrowthConfigItem mConfigItem;
	public static StencilItem mStencilItem;
	
	public void preInit(FMLPreInitializationEvent event) {
		mGrowthDataProvider = GrowthDataProvider.getInstance();
		mGrowthDataProvider.init(event.getModConfigurationDirectory()+"/treegrowth/trees.cfg");
		IDConfiguration.init(event.getModConfigurationDirectory()+"/treegrowth/ids.cfg");
		//mGrowthItem = new GrowthItem(IDConfiguration.growthItemID);
		//mConfigItem = new TreeGrowthConfigItem(IDConfiguration.treeGrowthConfigItemID);
		//mStencilItem = new StencilItem(IDConfiguration.stencilItemID);
		
		mGrowthItem = new GrowthItem();
		mConfigItem = new TreeGrowthConfigItem();
		mStencilItem = new StencilItem();

//		GameRegistry.registerItem(mGrowthItem, "growthItem");
//		GameRegistry.registerItem(mConfigItem, "configItem");
//		GameRegistry.registerItem(mStencilItem, "stencilItem");
         
		
		Configuration config = new Configuration(new File(event.getModConfigurationDirectory()+"/treegrowth/treegrowth.cfg"));
		config.load();
		
		Constants.AUTO_PROCESSING_ENABLED = config.get(Configuration.CATEGORY_GENERAL, "TreeGrowthEnabled", true).getBoolean(true);
		
		Property maxProcessingTimeProp = config.get(Configuration.CATEGORY_GENERAL, "MaxProcessingTime", Constants.MAX_PROCESSING_TIME);
		maxProcessingTimeProp.comment = "If the current processing step takes less than this time, another one is scheduled. Default: "+Constants.MAX_PROCESSING_TIME+" [ms]";
		Constants.MAX_PROCESSING_TIME = maxProcessingTimeProp.getInt(Constants.MAX_PROCESSING_TIME);
		
		Property globalProcessingTimeProp = config.get(Configuration.CATEGORY_GENERAL, "GlobalProcessingTime",  Constants.GLOBAL_PROCESSING_TIME);
		globalProcessingTimeProp.comment = "The rate at which processing steps are initiated. Default: "+Constants.GLOBAL_PROCESSING_TIME+" [ms]";
		Constants.GLOBAL_PROCESSING_TIME = globalProcessingTimeProp.getInt(Constants.GLOBAL_PROCESSING_TIME);
		
		Property chunkUpdateTimeTimeProp = config.get(Configuration.CATEGORY_GENERAL, "ChunkUpdateTime",  Constants.CHUNK_UPDATE_TIME);
		chunkUpdateTimeTimeProp.comment = "The update schedule of a chunk. Default "+Constants.CHUNK_UPDATE_TIME+"  [ms]";
		Constants.CHUNK_UPDATE_TIME = chunkUpdateTimeTimeProp.getInt(Constants.CHUNK_UPDATE_TIME);
		
		config.save();
	}
	public void init(FMLInitializationEvent event) {
    	mGrowthProcessor = GrowthProcessor.getInstance();
    	mChunkEventHandler = new ChunkEventHandler(mGrowthDataProvider, mGrowthProcessor);
        MinecraftForge.EVENT_BUS.register(mChunkEventHandler);
	}
	
	public void processChunkAtWorldCoords(World world, int posX, int posZ) {
		Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(posX,0, posZ));
		mGrowthProcessor.processChunk(chunk);
	}
	
	public void toggleAutoProcessing() {
		mGrowthProcessor.toggleAutoProcessing();
	}
	
	/**
	 * @see com.algorim.treegrowth.treedetection.TreeDetector#applyStencils(World, int, int, int)
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public void applyStencils(World world, int x, int y, int z) {
		mGrowthProcessor.applyStencils(world, x, y, z);
	}
}
