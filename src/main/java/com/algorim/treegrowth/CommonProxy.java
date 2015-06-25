package com.algorim.treegrowth;

import com.algorim.treegrowth.manager.GrowthDataProvider;
import com.algorim.treegrowth.manager.GrowthProcessor;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.event.FMLInitializationEvent;

public class CommonProxy {
	ChunkEventHandler mChunkEventHandler;
	GrowthDataProvider mGrowthDataProvider;
	GrowthProcessor mGrowthProcessor;
	public void init(FMLInitializationEvent event) {
    	
    	mGrowthDataProvider = GrowthDataProvider.getInstance();
    	mGrowthProcessor = GrowthProcessor.getInstance();
    	System.out.println("Creating and registering chunkEventHandler");
    	mChunkEventHandler = new ChunkEventHandler(mGrowthDataProvider, mGrowthProcessor);
        MinecraftForge.EVENT_BUS.register(mChunkEventHandler);
	}
	
	public void processChunkAtWorldCoords(World world, int posX, int posZ) {
		Chunk chunk = world.getChunkFromBlockCoords(posX, posZ);
		mGrowthProcessor.processChunk(chunk);
	}
}
