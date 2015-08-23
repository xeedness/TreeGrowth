package com.algorim.treegrowth.manager;

import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;

import com.algorim.treegrowth.config.Constants;
import com.algorim.treegrowth.config.TreeConfiguration;
import com.algorim.treegrowth.utilities.Common;
import com.algorim.treegrowth.utilities.Tree;
import com.algorim.treegrowth.utilities.TreeData;

/**
 * This class holds the chunk infos. 
 * 
 * @author xeedness
 *
 */
public class GrowthDataProvider {
	
	private static GrowthDataProvider instance; 
	
	TreeMap<Integer, TreeMap<Integer, ChunkGrowthData>> mChunkGrowthDataMap;
	ChunkGrowthDataList mChunkGrowthDataList;
	//LinkedList<ChunkGrowthData> mChunkGrowthDataArray;
	//ListIterator<ChunkGrowthData> mCurrentItr;
//	ChunkGrowthData firstElem;
//	ChunkGrowthData lastElem;
//	ChunkGrowthData currentElem;
//	int chunkGrowthDataSize = 0;
	
	private TreeConfiguration mTreeConfig;
	private long tick = 0;
	
	private GrowthDataProvider() {
		mChunkGrowthDataMap = new TreeMap<Integer, TreeMap<Integer, ChunkGrowthData>>();
		mChunkGrowthDataList = new ChunkGrowthDataList();
		//mChunkGrowthDataArray = new LinkedList<ChunkGrowthData>();
		
		
	}
	

	static public GrowthDataProvider getInstance() {
		if(instance == null) instance = new GrowthDataProvider();
		return instance;
	}
	
	/**
	 * Initializes the tree config.
	 * 
	 * @param treeConfigPath
	 */
	public void init(String treeConfigPath) {
		mTreeConfig = new TreeConfiguration(treeConfigPath);
	}
	
	
	/**
	 * Checks if a given blockID is wood. This is due to the different block id's provided in the config.
	 * @param blockID
	 * @return
	 */
	public boolean isWood(int blockID) {
		return mTreeConfig.isWood(blockID);
	}
	/**
	 * Checks if a given blockID is leaves. This is due to the different block id's provided in the config.
	 * @param blockID
	 * @return
	 */
	public boolean isLeave(int blockID) {
		return mTreeConfig.isLeaves(blockID);
	}
	
	
	/**
	 * Get config TreeData object, that fit the parameters.
	 * @param chunk
	 * @param tree
	 * @param wood
	 * @param leaves
	 * @return
	 */
	private TreeData getTreeData(Chunk chunk, Tree tree, BlockPos woodPos, BlockPos leavesPos) {
		//return null;
		int woodID = Common.getBlockIDAbs(chunk, woodPos.getX(), woodPos.getY(), woodPos.getZ());
		int leafID = Common.getBlockIDAbs(chunk, leavesPos.getX(), leavesPos.getY(), leavesPos.getZ());
		int woodMeta = Common.getBlockMetadataAbs(chunk,woodPos.getX(), woodPos.getY(), woodPos.getZ());
		int leafMeta = Common.getBlockMetadataAbs(chunk,leavesPos.getX(), leavesPos.getY(), leavesPos.getZ());
		
		Block leafBlock = Block.getBlockById(leafID);
		Block woodBlock = Block.getBlockById(woodID);
		
		
		
		//TODO Why does this happen?
		if(leafBlock == null || woodBlock == null) {
			//Common.log("GrowthData","Could not retrieve leaf or wood blocks. leaves("+leafID+"), wood("+woodID+")", wood, leaves);
			return null;
		}
		
		//TODO This does not work
		BlockState bs = leafBlock.getBlockState();
		
//		leafBlock.getStateFromMeta((leafBlock.());
//		leafBlock.damageDropped(1);
//		leafBlock.damageDropped(leafBlock.getDefaultState());
//		leafBlock.damageDropped(leafBlock.getDefaultState());
		//leafMeta = leafBlock.damageDropped(leafMeta);
		//woodMeta = woodBlock.damageDropped(woodMeta);
		TreeData treeData = mTreeConfig.getTreeData(woodID, woodMeta, leafID, leafMeta, tree.getSize());
		
		return treeData;
	}
	
	/**
	 * Get config TreeData object, that fit the parameters. If the basic call does not get through. Other surrounding blocks are checked out.
	 * @param chunk
	 * @param tree
	 * @return
	 */
	
	public TreeData getTreeData(Chunk chunk, Tree tree) {
		TreeData treeData = null;
		treeData = getTreeData(chunk, tree, new BlockPos(tree.getCoord1()),
			new BlockPos(tree.getCoord2().getX(), tree.getCoord2().getY()+1, tree.getCoord2().getZ()));
		
		if(treeData == null) {
			outerloop:
			for(int x=tree.getCoord1().getX()-1; x<=tree.getCoord2().getX()+1; x++) {
				for(int z=tree.getCoord1().getZ()-1; z<=tree.getCoord2().getZ()+1; z++) {
					treeData = getTreeData(chunk, tree, new BlockPos(tree.getCoord1()),
							new BlockPos(x, tree.getCoord2().getY(), z));
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
	
	
	/**
	 * Increases the internal tick counter. 
	 * @return true, when the internal tick counter*TICK_TIME > GLOBAL_PROCESSING_TIME
	 */
	public boolean needsProcessing() {
		tick++;
		return tick*Constants.TICK_TIME > Constants.GLOBAL_PROCESSING_TIME;
	}
	
	/**
	 * Resets the tick counter.
	 */
	public void updateProcessing() {
		tick = 0;
	}
	/**
	 * This should be called, when a chunk gets loaded, to allow its processing.
	 * 
	 * @param x
	 * @param z
	 * @param chunk
	 */
	public synchronized void chunkGrowthDataLoaded(int x, int z, Chunk chunk) {
		ChunkGrowthData data = getChunkGrowthData(x,z);
		if(data == null) {
			data = new ChunkGrowthData();
			addChunkGrowthData(x, z, data);
		}
		data.chunk = chunk;
	}
	
	/**
	 * This should be called, when a chunk gets unloaded, to denie its processing.
	 * 
	 * @param x
	 * @param z
	 */
	public synchronized void chunkGrowthDataUnloaded(int x, int z) {
		ChunkGrowthData data = getChunkGrowthData(x,z);
		data.chunk = null;

	}

	
	/**
	 * Gets the next queued chunk to process.
	 * 
	 * @return
	 */
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
	/**
	 * This adds a chunk to the processing set.
	 * 
	 * @param x
	 * @param z
	 * @param data
	 */
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
	
	/**
	 * Removes a chunk from the processing set.
	 * @param x
	 * @param z
	 */
	public void removeChunkGrowthData(int x, int z) {
		ChunkGrowthData data = getChunkGrowthData(x,z);
		mChunkGrowthDataList.remove(data);
		//This should not fail
		mChunkGrowthDataMap.get(x).remove(z);
	}

	/**
	 * Gets the memory size of all chunks without overhead
	 * @return
	 */
	public int getSize() {
		int size = 0;
		for(TreeMap<Integer, ChunkGrowthData> value : mChunkGrowthDataMap.values()) {
			size+=value.size()*ChunkGrowthData.SIZE;
		}
		return size;
	}
	
	/**
	 * A simple double linked list.
	 * 
	 * @author xeedness
	 *
	 */
	public class ChunkGrowthDataList {
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
