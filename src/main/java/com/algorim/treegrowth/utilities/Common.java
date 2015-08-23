package com.algorim.treegrowth.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.algorim.treegrowth.manager.GrowthDataProvider;

public class Common {
	
	
	
	private Common() {}
	
	
	
	public static boolean isAir(int blockID) {
		return blockID == 0;
	}
	public static boolean isAir(World world, int x, int y, int z) {
		return Block.getIdFromBlock(world.getChunkFromChunkCoords(x >> 4, z >> 4).getBlock(x & 15, y , z & 15)) == 0;
	}
	public static boolean isAir(Chunk chunk, int x, int y, int z) {
		return getBlockIDAbs(chunk, x, y, z) == 0;
	}
	public static boolean isClear(int blockID) {
		return 	blockID == 0 ||
				blockID == Block.blockRegistry.getIDForObject(Blocks.snow_layer);
	}
	public static boolean canBreath(int blockID) {
		return 	blockID == 0 ||
				blockID == Block.blockRegistry.getIDForObject(Blocks.snow_layer);
	}
	//TODO Check if this works
	public static boolean isFertile(int blockID) {
		return 	blockID == Block.blockRegistry.getIDForObject(Blocks.dirt) ||
				blockID == Block.blockRegistry.getIDForObject(Blocks.snow_layer) ||
				blockID == Block.blockRegistry.getIDForObject(Blocks.grass);
	}
	
	public static boolean canReplace(int blockID) {
		return blockID == Block.blockRegistry.getIDForObject("snow_layer") ||
				blockID == Block.blockRegistry.getIDForObject(Blocks.grass);
	}
	
	public static int getBlockID(World world, int x, int y, int z) {
		//TODO Check if this works
		return Block.getIdFromBlock(world.getChunkFromBlockCoords(new BlockPos(x, y, z)).getBlock(new BlockPos(x, y, z)));
	}
	
	public static int getBlockIDAbs(Chunk chunk, int x, int y, int z) {
		//TODO Check if this works
		return Block.getIdFromBlock(chunk.getBlock(new BlockPos(x, y, z)));
//		if((chunk.xPosition << 4) <= x && (chunk.xPosition << 4)+16 > x &&
//				(chunk.zPosition << 4) <= z && (chunk.zPosition << 4)+16 > z)
//			return chunk.getBlockID(x & 15, y, z & 15);
//		else
//			return chunk.worldObj.getBlockId(x, y, z);
	}
	//TODO Check if this works
	public static int getBlockMetadataAbs(Chunk chunk, int x, int y, int z) {
		return chunk.getBlockMetadata(new BlockPos(x & 15, y, z & 15));
	}
	
	
	public static boolean isWoodLog(Chunk chunk, int x, int y, int z) {
		//System.out.println("isWoodLog: "+x+" "+y+" "+z);
		return GrowthDataProvider.getInstance().isWood(getBlockIDAbs(chunk, x, y, z));
	}
	public static boolean isWoodLog(World world, int x, int y, int z) {
		//System.out.println("isWoodLog: "+x+" "+y+" "+z);
		return GrowthDataProvider.getInstance().isWood(getBlockID(world, x,y,z));
	}
	
	public static boolean isLeave(Chunk chunk, int x, int y, int z) {
		//System.out.println("isLeave: "+x+" "+y+" "+z);
		return GrowthDataProvider.getInstance().isLeave(getBlockIDAbs(chunk, x, y, z));
	}
	public static boolean isLeave(World world, int x, int y, int z) {
		//System.out.println("isWoodLog: "+x+" "+y+" "+z);
		return GrowthDataProvider.getInstance().isLeave(getBlockID(world, x,y,z));
	}
	public static boolean isDirt(Chunk chunk, int x, int y, int z) {
		//System.out.println("isDirt: "+x+" "+y+" "+z);
		
		
		return  Block.blockRegistry.getIDForObject(Blocks.dirt) == getBlockIDAbs(chunk, x, y, z) ||
				Block.blockRegistry.getIDForObject(Blocks.grass) == getBlockIDAbs(chunk, x, y, z);
	}
	
	public static boolean isDirt(World world, int x, int y, int z) {
		//System.out.println("isDirt: "+x+" "+y+" "+z);
		return  Block.blockRegistry.getIDForObject(Blocks.dirt) == getBlockID(world, x,y,z) ||
				Block.blockRegistry.getIDForObject(Blocks.grass) == getBlockID(world, x,y,z);
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
		int blockID = getBlockID(world, x,y,z);
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
		int blockID = getBlockID(world, x,y,z);
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
	
	public static void log(String area, String message, BlockPos... pos) {
		String posVec = "["+pos[0].getX()+","+pos[0].getY()+","+pos[0].getZ()+"]";
		
		for(int i=1; i<pos.length; i++) {
			posVec = posVec+" "+"["+pos[i].getX()+","+pos[i].getY()+","+pos[i].getZ()+"]";
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
		for(int xI=x; xI < x+size; xI++) {
			for(int zI=z; zI < z+size; zI++) {
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
		for(int xI=x; xI < x+size; xI++) {
			for(int zI=z; zI < z+size; zI++) {
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
		for(int xI=x; xI < x+size; xI++) {
			for(int zI=z; zI < z+size; zI++) {
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
		for(int xI=x; xI < x+size; xI++) {
			for(int zI=z; zI < z+size; zI++) {
				allAir &= isAir(chunk, xI, y, zI);
			}
		}
		return allAir;
	}
	
	
	
}
