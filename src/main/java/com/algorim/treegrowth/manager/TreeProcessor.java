package com.algorim.treegrowth.manager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.algorim.treegrowth.utilities.Common;
import com.algorim.treegrowth.utilities.Coord2i;
import com.algorim.treegrowth.utilities.Coord3i;
import com.algorim.treegrowth.utilities.Tree;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

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
	private Coord2i start;
	private Coord2i end;
	private ArrayList<Coord3i> candidates; 
	
	private int cFertile;
	private int cArid;
	private int heighestRating;
	TreeProcessor(Chunk chunk, Tree tree) {
		mChunk = chunk;
		mTree = tree;
		mWorld = chunk.worldObj;
		
		start = new Coord2i(tree.c1.x-(tree.getGrowthDimension()-tree.getSize())/2,
				tree.c1.z-(tree.getGrowthDimension()-tree.getSize())/2);
		end = new Coord2i(tree.c2.x+(tree.getGrowthDimension()-tree.getSize())/2,
				tree.c2.z+(tree.getGrowthDimension()-tree.getSize())/2);
		
		mGroundHeight = new int[tree.getGrowthDimension()+RATING_RADIUS*2][tree.getGrowthDimension()+RATING_RADIUS*2];
		mGroundType = new int[tree.getGrowthDimension()+RATING_RADIUS*2][tree.getGrowthDimension()+RATING_RADIUS*2];
		mGroundBlockID = new int[tree.getGrowthDimension()+RATING_RADIUS*2][tree.getGrowthDimension()+RATING_RADIUS*2];
		mValidPlace = new boolean[tree.getGrowthDimension()+RATING_RADIUS*2][tree.getGrowthDimension()+RATING_RADIUS*2];
		mGroundRating = new int[tree.getGrowthDimension()][tree.getGrowthDimension()];
		
		candidates = new ArrayList<Coord3i>();
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
			for(int y=0; y<mGroundRating[x].length; y++) {
				if(mGroundType[x+RATING_RADIUS][y+RATING_RADIUS] == TYPE_FERTILE) cFertile++;
				if(mGroundType[x+RATING_RADIUS][y+RATING_RADIUS] == TYPE_ARID) cArid++;
				mGroundRating[x][y] = getRating(x,y);
				heighestRating = Math.max(heighestRating, mGroundRating[x][y]);
			}
		}
		
		for(int x=0; x<mGroundRating.length; x++) {
			for(int y=0; y<mGroundRating[x].length; y++) {
				
				if(mGroundRating[x][y] == heighestRating) candidates.add(new Coord3i(start.x+x, mGroundHeight[RATING_RADIUS+x][RATING_RADIUS+y]+1, start.y+y));
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
	public ArrayList<Coord3i> getCandidates() {
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
		for(int x = start.x-RATING_RADIUS; x<= end.x+RATING_RADIUS;x++) {
			for(int z = start.y-RATING_RADIUS; z<= end.y+RATING_RADIUS;z++) {
				int height = mWorld.getHeightValue(x,z)-1;
				mGroundHeight[x-(start.x-RATING_RADIUS)][z-(start.y-RATING_RADIUS)] = height;
				mGroundBlockID[x-(start.x-RATING_RADIUS)][z-(start.y-RATING_RADIUS)] = mWorld.getBlockId(x, height, z);
				mValidPlace[x-(start.x-RATING_RADIUS)][z-(start.y-RATING_RADIUS)] = Common.isClear(mWorld.getBlockId(x, height+1, z));
				if(Common.isTreePart(mWorld, x, height, z)) {
					mGroundType[x-(start.x-RATING_RADIUS)][z-(start.y-RATING_RADIUS)] = TYPE_OCCUPIED;
				} else if(Common.isFertile(mWorld, x, height, z)) {
					mGroundType[x-(start.x-RATING_RADIUS)][z-(start.y-RATING_RADIUS)] = TYPE_FERTILE;
				} else {
					mGroundType[x-(start.x-RATING_RADIUS)][z-(start.y-RATING_RADIUS)] = TYPE_ARID;
				}
				mValidPlace[x-(start.x-RATING_RADIUS)][z-(start.y-RATING_RADIUS)] = Common.isClear(mWorld.getBlockId(x, height+1, z)) && mGroundType[x-(start.x-RATING_RADIUS)][z-(start.y-RATING_RADIUS)] == TYPE_FERTILE;
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
