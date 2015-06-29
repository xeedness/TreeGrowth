package com.algorim.treegrowth.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;
import java.util.TreeMap;

import com.algorim.treegrowth.Common;
import com.algorim.treegrowth.Constants;
import com.algorim.treegrowth.objects.Coord3i;
import com.algorim.treegrowth.objects.Tree;
import com.algorim.treegrowth.objects.TreeData;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class GrowthDataProvider {
	
	private static GrowthDataProvider instance; 
	
	TreeMap<Integer, TreeMap<Integer, ChunkGrowthData>> mChunkGrowthDataMap;
	ChunGrowthDataList mChunkGrowthDataList;
	//LinkedList<ChunkGrowthData> mChunkGrowthDataArray;
	//ListIterator<ChunkGrowthData> mCurrentItr;
//	ChunkGrowthData firstElem;
//	ChunkGrowthData lastElem;
//	ChunkGrowthData currentElem;
//	int chunkGrowthDataSize = 0;
	
	private TreeConfiguration treeConfig;
	private long tick = 0;
	
	private GrowthDataProvider() {
		mChunkGrowthDataMap = new TreeMap<Integer, TreeMap<Integer, ChunkGrowthData>>();
		mChunkGrowthDataList = new ChunGrowthDataList();
		//mChunkGrowthDataArray = new LinkedList<ChunkGrowthData>();
		treeConfig = new TreeConfiguration("config/treegrowth.cfg");
		
	}
	
	static public GrowthDataProvider getInstance() {
		if(instance == null) instance = new GrowthDataProvider();
		return instance;
	}
	public boolean isWood(int blockID) {
		return treeConfig.isWood(blockID);
	}
	public boolean isLeave(int blockID) {
		return treeConfig.isLeaves(blockID);
	}
	public TreeData getTreeData(Chunk chunk, Tree tree, Coord3i wood, Coord3i leaves) {
		int woodID = Common.getBlockIDAbs(chunk, wood.x, wood.y, wood.z);
		int leafID = Common.getBlockIDAbs(chunk, leaves.x, leaves.y, leaves.z);
		int woodMeta = Common.getBlockMetadataAbs(chunk,wood.x, wood.y, wood.z);
		int leafMeta = Common.getBlockMetadataAbs(chunk,leaves.x, leaves.y, leaves.z);
		Block leafBlock = Block.blocksList[leafID];
		Block woodBlock = Block.blocksList[woodID];
		
		//TODO Why does this happen?
		if(leafBlock == null || woodBlock == null) {
			//Common.log("GrowthData","Could not retrieve leaf or wood blocks. leaves("+leafID+"), wood("+woodID+")", wood, leaves);
			return null;
		}
		leafMeta = leafBlock.damageDropped(leafMeta);
		woodMeta = woodBlock.damageDropped(woodMeta);
		TreeData treeData = treeConfig.getTreeData(woodID, woodMeta, leafID, leafMeta, tree.getSize());
		
		return treeData;
	}
	public TreeData getTreeData(Chunk chunk, Tree tree) {
		TreeData treeData = null;
		treeData = getTreeData(chunk, tree, new Coord3i(tree.getCoord1().x,tree.getCoord1().y,tree.getCoord1().z),
			new Coord3i(tree.getCoord2().x, tree.getCoord2().y+1, tree.getCoord2().z));
		
		if(treeData == null) {
			
			//TODO This doesnt work if the tree has a weird size
			outerloop:
			for(int x=tree.getCoord2().x-1; x<=tree.getCoord2().x+1; x++) {
				for(int z=tree.getCoord2().z-1; z<=tree.getCoord2().z+1; z++) {
					treeData = getTreeData(chunk, tree, new Coord3i(tree.getCoord1().x,tree.getCoord1().y,tree.getCoord1().z),
							new Coord3i(x, tree.getCoord2().y+1, z));
					if(treeData != null) break outerloop;
				}
			}
		}
		if(treeData == null)
			Common.log("GrowthData","Could not identify tree.",  tree.getCoord1(), tree.getCoord2());
		return treeData;
	}

	public ChunkGrowthData getChunkGrowthData(int x, int z)  {
		TreeMap<Integer, ChunkGrowthData> xdim = mChunkGrowthDataMap.get(x);
		if(xdim == null) return null;
		ChunkGrowthData data = xdim.get(z);
		return data;
	}
	public boolean needsProcessing() {
		tick++;
		return tick*Constants.TICK_TIME > Constants.GLOBAL_PROCESSING_TIME;
	}
	
	public void updateProcessing() {
		tick = 0;
	}
	public synchronized void chunkGrowthDataLoaded(int x, int z, Chunk chunk) {
		ChunkGrowthData data = getChunkGrowthData(x,z);
		if(data == null) {
			data = new ChunkGrowthData();
			addChunkGrowthData(x, z, data);
		}
		data.chunk = chunk;
	}
	
	public synchronized void chunkGrowthDataUnloaded(int x, int z) {
		ChunkGrowthData data = getChunkGrowthData(x,z);
//		if(data == null) {
//			data = new ChunkGrowthData();
//			addChunkGrowthData(x, z, data);
//		}
		data.chunk = null;

	}
//	public ChunkGrowthData getChunkGrowthData(int id) {
//		
//		return mChunkGrowthDataArray.get(id);
//	}
	
	public synchronized ChunkGrowthData getNextProcessing() {
		if(mChunkGrowthDataList.size == 0) return null;
		if(mChunkGrowthDataList.currentElem == null) mChunkGrowthDataList.currentElem = mChunkGrowthDataList.firstElem;
		else if (mChunkGrowthDataList.currentElem.next != null) mChunkGrowthDataList.currentElem = mChunkGrowthDataList.currentElem.next;
		while(mChunkGrowthDataList.currentElem != null && !mChunkGrowthDataList.currentElem.needsProcessing()) {
			mChunkGrowthDataList.currentElem = mChunkGrowthDataList.currentElem.next;
		}
		//while(currentElem.next != null && !(data = currentElem.next).needsProcessing(world)) {}
		//if(data.needsProcessing(world)) return data;
		//else return null;
		
		return mChunkGrowthDataList.currentElem;
	}
	public void addChunkGrowthData(int x, int z, ChunkGrowthData data) {
		TreeMap<Integer, ChunkGrowthData> xdim = mChunkGrowthDataMap.get(x);
		if(xdim == null) {
			xdim = new TreeMap<Integer, ChunkGrowthData>();
			xdim.put(z, data);
			mChunkGrowthDataMap.put(x, xdim);
		} else {
			xdim.put(z, data);
		}
		mChunkGrowthDataList.add(data);
	}
	
	public void removeChunkGrowthData(int x, int z) {
		//TODO Get and remove?
		ChunkGrowthData data = getChunkGrowthData(x,z);
		mChunkGrowthDataList.remove(data);
		//This should not fail
		mChunkGrowthDataMap.get(x).remove(z);
	}
	
	public ChunkGrowthData createChunkGrowthData(int x, int z) {
		ChunkGrowthData data = new ChunkGrowthData();
		//setChunkGrowthData(x, z, data);
		return data;
	}
	public int getSize() {
		int size = 0;
		for(TreeMap<Integer, ChunkGrowthData> value : mChunkGrowthDataMap.values()) {
			size+=value.size()*ChunkGrowthData.SIZE;
		}
		return size;
	}
	
	public class ChunGrowthDataList {
		public ChunkGrowthData firstElem;
		public ChunkGrowthData lastElem;
		public ChunkGrowthData currentElem;
		public int size = 0;
		
		public void add(ChunkGrowthData elem) {
			if(lastElem != null) 
				lastElem.next = elem;
			elem.prev = lastElem;
			lastElem = elem;
			size++;
			if(firstElem == null)
				firstElem = lastElem;
		}
		public void remove(ChunkGrowthData elem) {
			if(elem.equals(currentElem)) {
				if(elem.next != null) {
					currentElem = elem.next;
				} else if(size == 1) {
					currentElem = null;
				} else {
					currentElem = firstElem;
				}
			}
			
			if(elem.prev != null) {
				elem.prev.next = elem.next;
				
			}
			if(elem.next != null) {
				elem.next.prev = elem.prev;
			}
		}
		
	}
	
	
}
