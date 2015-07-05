package com.algorim.treegrowth;

import java.io.File;

import com.algorim.treegrowth.config.Constants;
import com.algorim.treegrowth.config.IDConfiguration;
import com.algorim.treegrowth.events.ChunkEventHandler;
import com.algorim.treegrowth.items.GrowthItem;
import com.algorim.treegrowth.items.TreeGrowthConfigItem;
import com.algorim.treegrowth.manager.GrowthDataProvider;
import com.algorim.treegrowth.manager.GrowthProcessor;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {
	ChunkEventHandler mChunkEventHandler;
	GrowthDataProvider mGrowthDataProvider;
	GrowthProcessor mGrowthProcessor;
	
	private GrowthItem mGrowthItem;
	private TreeGrowthConfigItem mConfigItem;
	
	public void preInit(FMLPreInitializationEvent event) {
		mGrowthDataProvider = GrowthDataProvider.getInstance();
		mGrowthDataProvider.init(event.getModConfigurationDirectory()+"/treegrowth/trees.cfg");
		IDConfiguration.init(event.getModConfigurationDirectory()+"/treegrowth/ids.cfg");
		mGrowthItem = new GrowthItem(IDConfiguration.growthItemID);
		mConfigItem = new TreeGrowthConfigItem(IDConfiguration.treeGrowthConfigItemID);

		GameRegistry.registerItem(mGrowthItem, "growthItem");
		GameRegistry.registerItem(mConfigItem, "configItem");
         
		
		Configuration config = new Configuration(new File(event.getModConfigurationDirectory()+"/treegrowth/treegrowth.cfg"));
		config.load();
		
		Constants.AUTO_PROCESSING_ENABLED = config.get(Configuration.CATEGORY_GENERAL, "TreeGrowthEnabled", true).getBoolean(true);
		
		Property maxProcessingTimeProp = config.get(Configuration.CATEGORY_GENERAL, "MaxProcessingTime", Constants.MAX_PROCESSING_TIME);
		maxProcessingTimeProp.comment = "If the current processing step takes less than this time, another one is scheduled. Default: "+Constants.MAX_PROCESSING_TIME+" [ms]";
		Constants.MAX_PROCESSING_TIME = maxProcessingTimeProp.getInt();
		
		Property globalProcessingTimeProp = config.get(Configuration.CATEGORY_GENERAL, "GlobalProcessingTime",  Constants.GLOBAL_PROCESSING_TIME);
		globalProcessingTimeProp.comment = "The rate at which processing steps are initiated. Default: "+Constants.GLOBAL_PROCESSING_TIME+" [ms]";
		Constants.GLOBAL_PROCESSING_TIME = globalProcessingTimeProp.getInt();
		
		Property chunkUpdateTimeTimeProp = config.get(Configuration.CATEGORY_GENERAL, "ChunkUpdateTime",  Constants.CHUNK_UPDATE_TIME);
		chunkUpdateTimeTimeProp.comment = "The update schedule of a chunk. Default "+Constants.CHUNK_UPDATE_TIME+"  [ms]";
		Constants.CHUNK_UPDATE_TIME = chunkUpdateTimeTimeProp.getInt();
		
		config.save();
	}
	public void init(FMLInitializationEvent event) {
    	mGrowthProcessor = GrowthProcessor.getInstance();
    	System.out.println("Creating and registering chunkEventHandler");
    	mChunkEventHandler = new ChunkEventHandler(mGrowthDataProvider, mGrowthProcessor);
        MinecraftForge.EVENT_BUS.register(mChunkEventHandler);
	}
	
	public void processChunkAtWorldCoords(World world, int posX, int posZ) {
		Chunk chunk = world.getChunkFromBlockCoords(posX, posZ);
		mGrowthProcessor.processChunk(chunk);
	}
	
	public void toggleAutoProcessing() {
		mGrowthProcessor.toggleAutoProcessing();
	}
}
