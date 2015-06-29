package com.algorim.treegrowth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.algorim.treegrowth.manager.GrowthDataProvider;
import com.algorim.treegrowth.objects.Coord3i;

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
	
//	public static int getBlockID(Chunk chunk, int x, int y, int z) {
//		//TODO Add y boundary top
//		if(x < 0 || x > 15 || y < 0 || z < 0 || z > 15) {
//			//System.out.println("getBlockID: Parameter out of bounds."+x+" "+y+" "+z);
//			return chunk.worldObj.getBlockId((chunk.xPosition << 4)+x, y, (chunk.zPosition << 4)+z);
//		} else {
//			return chunk.getBlockID(x,y,z);
//		}
//	}
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
	
//	public static int getBlockMetadata(Chunk chunk, int x, int y, int z) {
//		//TODO Add y boundary top
//		if(x < 0 || x > 15 || y < 0 || z < 0 || z > 15) {
//			//System.out.println("getBlockID: Parameter out of bounds."+x+" "+y+" "+z);
//			return chunk.worldObj.getBlockMetadata((chunk.xPosition << 4)+x, y, (chunk.zPosition << 4)+z);
//		} else {
//			return chunk.getBlockMetadata(x,y,z);
//		}
//	}
//	public static int getDirtLevel(Chunk chunk, int x, int z) {
//		int y = chunk.getHeightValue(x, z);
//		while(!isDirt(chunk, x, y, z)) {
//			if(y == 1) throw new IllegalArgumentException("No DirtLevel"); 
//			y--;
//		}
//		if(!canBreath(getBlockID(chunk, x, y+1, z))) throw new IllegalArgumentException("No Air Above DirtLevel");
//		return y;
//		
//	}
	
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
	//TODO Lianen?
	public static boolean isTreePart(World world, int x, int y, int z) {
		int blockID = world.getBlockId(x, y, z);
		return GrowthDataProvider.getInstance().isWood(blockID) ||
				GrowthDataProvider.getInstance().isLeave(blockID);
		
	}
	
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
}
