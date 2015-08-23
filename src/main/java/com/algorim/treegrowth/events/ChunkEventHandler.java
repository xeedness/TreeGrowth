package com.algorim.treegrowth.events;

import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.algorim.treegrowth.manager.GrowthDataProvider;
import com.algorim.treegrowth.manager.GrowthProcessor;

public class ChunkEventHandler {
	GrowthDataProvider mGrowthDataProvider;
	GrowthProcessor mGrowthProcessor;

	public ChunkEventHandler(GrowthDataProvider growthDataProvider, GrowthProcessor growthProcessor) {
		this.mGrowthDataProvider = growthDataProvider;
		this.mGrowthProcessor = growthProcessor;
	}
	@SubscribeEvent
	public void chunkLoaded(ChunkEvent.Load chunkEvent)	{
		Chunk chunk = chunkEvent.getChunk();
		if(chunkEvent.world.isRemote) return;	
		mGrowthDataProvider.chunkGrowthDataLoaded(chunk.xPosition, chunk.zPosition, chunk);	
	}
	
	@SubscribeEvent
	public void chunkUnLoaded(ChunkEvent.Unload chunkEvent) {
		Chunk chunk = chunkEvent.getChunk();
		if(chunkEvent.world.isRemote) return;
		mGrowthDataProvider.chunkGrowthDataUnloaded(chunk.xPosition, chunk.zPosition);	
	}
}
