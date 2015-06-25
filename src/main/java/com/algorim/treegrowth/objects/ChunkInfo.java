package com.algorim.treegrowth.objects;

import net.minecraft.world.chunk.Chunk;

public class ChunkInfo {
	private Chunk mChunk;

	int[][] groundHeightMap;
	
	public ChunkInfo(Chunk chunk) {
		mChunk = chunk;
		groundHeightMap = new int[16][16];
	}
	
	public Chunk getChunk() {
		return mChunk;
	}
}
