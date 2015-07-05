package com.algorim.treegrowth.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.algorim.treegrowth.manager.GrowthDataProvider;

import net.minecraft.block.Block;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class Common {
	
	
	
	private Common() {}
	
	
	
	public static boolean isAir(int blockID) {
		return blockID == 0;
	}
	public static boolean isAir(World world, int x, int y, int z) {
		return world.getBlockId(x,y,z) == 0;
	}
	public static boolean isAir(Chunk chunk, int x, int y, int z) {
		return getBlockIDAbs(chunk, x, y, z) == 0;
	}
	public static boolean isClear(int blockID) {
		return 	blockID == 0 ||
				blockID == Block.snow.blockID;
	}
	public static boolean canBreath(int blockID) {
		return 	blockID == 0 ||
				blockID == Block.snow.blockID;
	}
	public static boolean isFertile(int blockID) {
		return 	blockID == Block.dirt.blockID ||
				blockID == Block.snow.blockID ||
				blockID == Block.grass.blockID;
	}
	
	public static boolean canReplace(int blockID) {
		return blockID == Block.snow.blockID ||
				blockID == Block.grass.blockID;
	}
	
	public static int getBlockIDAbs(Chunk chunk, int x, int y, int z) {
		if((chunk.xPosition << 4) <= x && (chunk.xPosition << 4)+16 > x &&
				(chunk.zPosition << 4) <= z && (chunk.zPosition << 4)+16 > z)
			return chunk.getBlockID(x & 15, y, z & 15);
		else
			return chunk.worldObj.getBlockId(x, y, z);
	}
	public static int getBlockMetadataAbs(Chunk chunk, int x, int y, int z) {
		if((chunk.xPosition << 4) <= x && (chunk.xPosition << 4)+16 > x &&
				(chunk.zPosition << 4) <= z && (chunk.zPosition << 4)+16 > z)
			return chunk.getBlockMetadata(x & 15, y, z & 15);
		else
			return chunk.worldObj.getBlockMetadata(x,y,z);
	}
	
	
	public static boolean isWoodLog(Chunk chunk, int x, int y, int z) {
		//System.out.println("isWoodLog: "+x+" "+y+" "+z);
		return GrowthDataProvider.getInstance().isWood(getBlockIDAbs(chunk, x, y, z));
	}
	public static boolean isWoodLog(World world, int x, int y, int z) {
		//System.out.println("isWoodLog: "+x+" "+y+" "+z);
		return GrowthDataProvider.getInstance().isWood(world.getBlockId(x, y, z));
	}
	
	public static boolean isLeave(Chunk chunk, int x, int y, int z) {
		//System.out.println("isLeave: "+x+" "+y+" "+z);
		return GrowthDataProvider.getInstance().isLeave(getBlockIDAbs(chunk, x, y, z));
	}
	public static boolean isLeave(World world, int x, int y, int z) {
		//System.out.println("isWoodLog: "+x+" "+y+" "+z);
		return GrowthDataProvider.getInstance().isLeave(world.getBlockId(x, y, z));
	}
	public static boolean isDirt(Chunk chunk, int x, int y, int z) {
		//System.out.println("isDirt: "+x+" "+y+" "+z);
		return  Block.dirt.blockID == getBlockIDAbs(chunk, x, y, z) ||
				Block.grass.blockID == getBlockIDAbs(chunk, x, y, z);
	}
	
	public static boolean isDirt(World world, int x, int y, int z) {
		//System.out.println("isDirt: "+x+" "+y+" "+z);
		return  Block.dirt.blockID == world.getBlockId(x, y, z) ||
				Block.grass.blockID == world.getBlockId(x, y, z);
	}
	/**
	 * Checks if block is wood or leaves
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static boolean isTreePart(World world, int x, int y, int z) {
		int blockID = world.getBlockId(x, y, z);
		return GrowthDataProvider.getInstance().isWood(blockID) ||
				GrowthDataProvider.getInstance().isLeave(blockID);
		
	}
	
	/**
	 * @see #isFertile
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static boolean isFertile(World world, int x, int y, int z) {
		int blockID = world.getBlockId(x, y, z);
		return isFertile(blockID);
		
	}
	
	public static void log(String area, String message) {
		File file = new File("treegrowth.log");
		try {
			if(!file.exists()) file.createNewFile();
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));	
			pw.println(DateFormat.getDateTimeInstance().format(new Date())+"\t["+area+"] "+message);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void log(String area, String message, Coord3i... pos) {
		String posVec = "["+pos[0].x+","+pos[0].y+","+pos[0].z+"]";
		
		for(int i=1; i<pos.length; i++) {
			posVec = posVec+" "+"["+pos[i].x+","+pos[i].y+","+pos[i].z+"]";
		}
		
		log(area, posVec+" "+message);
	}
	
	/**
	 * Checks, if the whole layer of the given coordinate is wood
	 * 
	 * @param chunk
	 * @param x Absolute X Coordinate
	 * @param y Absolute Y Coordinate
	 * @param z Absolute Z Coordinate
	 * @return
	 */
	public static boolean isWoodLogLayer(Chunk chunk,int x,int y,int z, int size) {
		boolean allWood = true;
		for(int xI=x; xI <= x+size; xI++) {
			for(int zI=z; zI <= z+size; zI++) {
				allWood &= isWoodLog(chunk, xI, y, zI);
			}
		}
		return allWood;
	}
	/**
	 * Checks, if the whole layer of the given coordinate are leaves
	 * 
	 * @param chunk
	 * @param x Absolute X Coordinate
	 * @param y Absolute Y Coordinate
	 * @param z Absolute Z Coordinate
	 * @return
	 */
	public static boolean isLeavesLayer(Chunk chunk,int x,int y,int z, int size) {
		boolean allLeaves = true;
		for(int xI=x; xI <= x+size; xI++) {
			for(int zI=z; zI <= z+size; zI++) {
				allLeaves &= isLeave(chunk, xI, y, zI);
			}
		}
		return allLeaves;
	}
	/**
	 * Checks, if the whole layer of the given coordinate is dirt
	 * 
	 * @param chunk
	 * @param x Absolute X Coordinate
	 * @param y Absolute Y Coordinate
	 * @param z Absolute Z Coordinate
	 * @return
	 */
	public static boolean isDirtLayer(Chunk chunk,int x,int y,int z, int size) {
		boolean allDirt = true;
		for(int xI=x; xI <= x+size; xI++) {
			for(int zI=z; zI <= z+size; zI++) {
				allDirt &= isDirt(chunk, xI, y, zI);
			}
		}
		return allDirt;
	}
	
	/**
	 * Checks, if the whole layer of the given coordinate is air
	 * 
	 * @param chunk
	 * @param x Absolute X Coordinate
	 * @param y Absolute Y Coordinate
	 * @param z Absolute Z Coordinate
	 * @return
	 */
	public static boolean isAirLayer(Chunk chunk,int x,int y,int z, int size) {
		boolean allAir = true;
		for(int xI=x; xI <= x+size; xI++) {
			for(int zI=z; zI <= z+size; zI++) {
				allAir &= isAir(chunk, xI, y, zI);
			}
		}
		return allAir;
	}
	
	
	
}
