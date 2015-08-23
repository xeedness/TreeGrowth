package com.algorim.treegrowth.manager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.algorim.treegrowth.utilities.Common;
import com.algorim.treegrowth.utilities.Coord2i;
import com.algorim.treegrowth.utilities.Tree;

/**
 * This class processes an already detected tree. 
 * It calculates the position candidates for new saplings.
 * Also it calculates information about the overall fertility of the surrounding area of the tree.
 * 
 * 
 * @author xeedness
 *
 */
public class TreeProcessor {
	public static final int TYPE_FERTILE = 0;
	public static final int TYPE_OCCUPIED = 1;
	public static final int TYPE_ARID = 2;
	public static final int RATING_RADIUS = 2;
	
	private Chunk mChunk;
	private World mWorld;
	private Tree mTree;
	
	private int[][] mGroundHeight;
	private int[][] mGroundType;
	private int[][] mGroundRating;
	private int[][] mGroundBlockID;
	private boolean[][] mValidPlace;
	private int startX;
	private int startZ;
	private int endX;
	private int endZ;
	private ArrayList<BlockPos> candidates; 
	
	private int cFertile;
	private int cArid;
	private int heighestRating;
	TreeProcessor(Chunk chunk, Tree tree) {
		mChunk = chunk;
		mTree = tree;
		mWorld = chunk.getWorld();
		
		startX = tree.c1.getX()-(tree.getGrowthDimension()-tree.getSize())/2;
		startZ = tree.c1.getZ()-(tree.getGrowthDimension()-tree.getSize())/2;
		endX = tree.c2.getX()+(tree.getGrowthDimension()-tree.getSize())/2;
		endZ = tree.c2.getZ()+(tree.getGrowthDimension()-tree.getSize())/2;
		
		mGroundHeight = new int[tree.getGrowthDimension()+RATING_RADIUS*2][tree.getGrowthDimension()+RATING_RADIUS*2];
		mGroundType = new int[tree.getGrowthDimension()+RATING_RADIUS*2][tree.getGrowthDimension()+RATING_RADIUS*2];
		mGroundBlockID = new int[tree.getGrowthDimension()+RATING_RADIUS*2][tree.getGrowthDimension()+RATING_RADIUS*2];
		mValidPlace = new boolean[tree.getGrowthDimension()+RATING_RADIUS*2][tree.getGrowthDimension()+RATING_RADIUS*2];
		mGroundRating = new int[tree.getGrowthDimension()][tree.getGrowthDimension()];
		
		candidates = new ArrayList<BlockPos>();
//		System.out.println("TreeProcessorInit:");
//		System.out.println("start: "+start.x+" "+start.y);
//		System.out.println("end: "+end.x+" "+end.y);
//		System.out.println("treesize: "+tree.getSize());
//		System.out.println("treeGrowthDimension: "+tree.getGrowthDimension());
	}
	
	/**
	 * This is the main processing routine. It calculates the fertility, aridity, heighestRating and candidate positions.
	 */
	public void process() {
		generateArrays();
		
		
		cFertile = 0;
		cArid = 0;
		heighestRating = 0;
		for(int x=0; x<mGroundRating.length; x++) {
			for(int z=0; z<mGroundRating[x].length; z++) {
				if(mGroundType[x+RATING_RADIUS][z+RATING_RADIUS] == TYPE_FERTILE) cFertile++;
				if(mGroundType[x+RATING_RADIUS][z+RATING_RADIUS] == TYPE_ARID) cArid++;
				mGroundRating[x][z] = getRating(x,z);
				heighestRating = Math.max(heighestRating, mGroundRating[x][z]);
			}
		}
		
		for(int x=0; x<mGroundRating.length; x++) {
			for(int z=0; z<mGroundRating[x].length; z++) {
				
				if(mGroundRating[x][z] == heighestRating) candidates.add(new BlockPos(startX+x, mGroundHeight[RATING_RADIUS+x][RATING_RADIUS+z]+1, startZ+z));
			}
		}
		
//		writeIntArray(mGroundHeight, "zheightmap"+start.x+"-"+start.y+".txt");
//		writeIntArray(mGroundType, "ztypemap"+start.x+"-"+start.y+".txt");
//		writeIntArray(mGroundBlockID, "zidmap"+start.x+"-"+start.y+".txt");
//		writeBooleanArray(mValidPlace, "zvalidmap"+start.x+"-"+start.y+".txt");
//		writeIntArray(mGroundRating, "zratingmap"+start.x+"-"+start.y+".txt");
//	
		//System.out.println("FertilityRate: "+cFertile+"/"+(mGroundType.length*mGroundType.length));
		//System.out.println("AridityRate: "+cArid+"/"+(mGroundType.length*mGroundType.length));
		
		
	}
	
	/**
	 * Get the heighest rating achieved by a position. 
	 * @return values range from 0 to {@value TreeProcessor#RATING_RADIUS}
	 * @see TreeProcessor#getRating(int, int) 
	 */
	public int getHeighestRating() {
		return heighestRating;
	}
	
	/**
	 * Gets the fertility of the surrounding area of the tree
	 * @return values range from 0 to 1 
	 */
	public float getFertility() {
		return (float)(cFertile)/getArea();
	}
	/**
	 * Gets the aridity of the surrounding area of the tree
	 * @return values range from 0 to 1 
	 */
	public float getAridity() {
		return (float)(cArid)/getArea();
	}
	/**
	 * Area of the surrounding area
	 * @return
	 */
	public int getArea() {
		return (mGroundRating.length*mGroundRating.length);
	}
	/**
	 * Gets the sapling candidates
	 * @return
	 */
	public ArrayList<BlockPos> getCandidates() {
		return candidates;
	}
	/**
	 * Calculates the rating of the given position.
	 * Every tile in a {@value TreeProcessor#RATING_RADIUS} radius, which is fertile increases the rating.
	 * If the position is no valid placement 0 is returned
	 * 
	 * @param x
	 * @param y
	 * @return values range from 0 to {@value TreeProcessor#RATING_RADIUS}
	 */
	private int getRating(int x, int y) {
		int rating = 0;
		if(!mValidPlace[x+RATING_RADIUS][y+RATING_RADIUS]) return rating;
		for(int xI=x-RATING_RADIUS;xI<=x+RATING_RADIUS;xI++) {
			for(int yI=y-RATING_RADIUS;yI<=y+RATING_RADIUS;yI++) {
				if(xI >= -RATING_RADIUS && xI < mGroundType.length && yI >= -RATING_RADIUS && yI < mGroundType[xI+RATING_RADIUS].length) {
					if(mGroundType[xI+RATING_RADIUS][yI+RATING_RADIUS] == TYPE_FERTILE) rating++;
				}
			}
			
		}
		
		return rating;
	}
	/**
	 * Fills the arrays with informations.
	 */
	private void generateArrays() {
		for(int x = startX-RATING_RADIUS; x<= endX+RATING_RADIUS;x++) {
			for(int z = startZ-RATING_RADIUS; z<= endZ+RATING_RADIUS;z++) {
				//int height = mWorld.getHeight()-1;
				int height = mWorld.getChunkFromChunkCoords(x >> 4, z >> 4).getHeight(x & 15, z & 15)-1;
				
				mGroundHeight[x-(startX-RATING_RADIUS)][z-(startZ-RATING_RADIUS)] = height;
				mGroundBlockID[x-(startX-RATING_RADIUS)][z-(startZ-RATING_RADIUS)] = Common.getBlockID(mWorld, x, height, z);
				mValidPlace[x-(startX-RATING_RADIUS)][z-(startZ-RATING_RADIUS)] = Common.isClear(Common.getBlockID(mWorld, x, height+1, z));
				if(Common.isTreePart(mWorld, x, height, z)) {
					mGroundType[x-(startX-RATING_RADIUS)][z-(startZ-RATING_RADIUS)] = TYPE_OCCUPIED;
				} else if(Common.isFertile(mWorld, x, height, z)) {
					mGroundType[x-(startX-RATING_RADIUS)][z-(startZ-RATING_RADIUS)] = TYPE_FERTILE;
				} else {
					mGroundType[x-(startX-RATING_RADIUS)][z-(startZ-RATING_RADIUS)] = TYPE_ARID;
				}
				mValidPlace[x-(startX-RATING_RADIUS)][z-(startZ-RATING_RADIUS)] = Common.isClear(Common.getBlockID(mWorld,x, height+1, z)) && mGroundType[x-(startX-RATING_RADIUS)][z-(startZ-RATING_RADIUS)] == TYPE_FERTILE;
			}
		}
	}
	
	/**
	 * Write int[][] array to disc
	 * 
	 * @param array
	 * @param filename
	 */
	private void writeIntArray(int[][] array, String filename) {
		File f = new File(filename);
		try {
			f.createNewFile();
			System.out.println("Writing array to "+f.getAbsolutePath());
			PrintWriter pw = new PrintWriter(f);
			for(int y=0; y<array[0].length; y++) {
				for(int x=0; x<array.length; x++) {
					pw.print(array[x][y]+"\t");
				}
				pw.print("\n");
			}
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Write boolean[][] array to disc
	 * 
	 * @param array
	 * @param filename
	 */
	private void writeBooleanArray(boolean[][] array, String filename) {
		File f = new File(filename);
		try {
			f.createNewFile();
			System.out.println("Writing array to "+f.getAbsolutePath());
			PrintWriter pw = new PrintWriter(f);
			for(int y=0; y<array[0].length; y++) {
				for(int x=0; x<array.length; x++) {
					pw.print((array[x][y] ? "1" : 0)+"\t");
				}
				pw.print("\n");
			}
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
