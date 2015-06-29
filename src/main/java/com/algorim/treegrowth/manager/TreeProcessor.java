package com.algorim.treegrowth.manager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.algorim.treegrowth.Common;
import com.algorim.treegrowth.objects.Coord2i;
import com.algorim.treegrowth.objects.Coord3i;
import com.algorim.treegrowth.objects.Tree;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

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
	public int getHeighestRating() {
		return heighestRating;
	}
	public float getFertility() {
		return (float)(cFertile)/getArea();
	}
	public float getAridity() {
		return (float)(cArid)/getArea();
	}
	public int getArea() {
		return (mGroundRating.length*mGroundRating.length);
	}
	public ArrayList<Coord3i> getCandidates() {
		return candidates;
	}
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
	

	//TODO Maybe put this in common
	private int getWorldX(int x) {
		return (mChunk.xPosition << 4)+x;
	}
	
	private int getWorldZ(int z) {
		return (mChunk.zPosition << 4)+z;
	}


	
	
	
}
