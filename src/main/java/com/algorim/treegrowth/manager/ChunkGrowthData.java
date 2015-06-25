package com.algorim.treegrowth.manager;

import java.util.ListIterator;
import java.util.TreeMap;

import com.algorim.treegrowth.Constants;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ChunkGrowthData {
	
	public static final int SIZE = 8;
	
	
	public ChunkGrowthData next, prev;
	public long last_processed = -1;
	public int processed = 0;
	public int id;
	public Chunk chunk;
	
	public boolean needsProcessing() {
		//if(true) return false;
		if(chunk == null) return false;
		long total = chunk.worldObj.getTotalWorldTime();
//		System.out.println("NeedsProcessing called: "
//				+ total
//				+ " - "+last_processed
//				+ " = "+(total-last_processed));
		if(last_processed == -1) {
			//System.out.println("Setting last_processed to: "+total);
			last_processed = total;
			return false;
		} else {
			return (total-last_processed)*Constants.TICK_TIME > Constants.CHUNK_UPDATE_TIME;
		}
	}
	public void updateProcessing() {
		processed++;
		last_processed = chunk.worldObj.getTotalWorldTime();
	}
	
	public void remove() {
		prev.next = next;
		next.prev = prev;
	}
	
	
}
