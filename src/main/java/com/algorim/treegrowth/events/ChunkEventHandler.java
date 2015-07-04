package com.algorim.treegrowth.events;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import com.algorim.treegrowth.manager.ChunkGrowthData;
import com.algorim.treegrowth.manager.GrowthDataProvider;
import com.algorim.treegrowth.manager.GrowthProcessor;

import cpw.mods.fml.common.SidedProxy;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;

public class ChunkEventHandler {
	GrowthDataProvider mGrowthDataProvider;
	GrowthProcessor mGrowthProcessor;

	public ChunkEventHandler(GrowthDataProvider growthDataProvider, GrowthProcessor growthProcessor) {
		this.mGrowthDataProvider = growthDataProvider;
		this.mGrowthProcessor = growthProcessor;
	}
	@ForgeSubscribe
	public void chunkLoaded(ChunkEvent.Load chunkEvent)	{
		Chunk chunk = chunkEvent.getChunk();
		if(chunkEvent.world.isRemote) return;	
		mGrowthDataProvider.chunkGrowthDataLoaded(chunk.xPosition, chunk.zPosition, chunk);	
	}
	
	@ForgeSubscribe
	public void chunkUnLoaded(ChunkEvent.Unload chunkEvent) {
		Chunk chunk = chunkEvent.getChunk();
		if(chunkEvent.world.isRemote) return;
		mGrowthDataProvider.chunkGrowthDataUnloaded(chunk.xPosition, chunk.zPosition);	
	}
}
