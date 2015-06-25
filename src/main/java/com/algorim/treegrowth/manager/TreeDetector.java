package com.algorim.treegrowth.manager;

import java.util.ArrayList;

import com.algorim.treegrowth.Common;
import com.algorim.treegrowth.Constants;
import com.algorim.treegrowth.objects.Tree;
import com.algorim.treegrowth.objects.Coord2i;

import net.minecraft.block.Block;
import net.minecraft.world.chunk.Chunk;

public class TreeDetector {
	
	
	
	static TreeDetector instance;
	
	private TreeDetector() {
		
	}
	
	static public TreeDetector getInstance() {
		if(instance == null) instance = new TreeDetector();
		return instance;
	}
	
	
	/**
	 * Finds position of trees in a chunk.
	 * An atleast 3 woodlogs tall trunk topped with atleast one leave is counted as a tree  
	 * 
	 * @param chunk
	 * @return list of tree coords
	 */
	public ArrayList<Tree> findTrees(Chunk chunk) {
		ArrayList<Tree> trees = new ArrayList<Tree>();
		for(int x=0; x<16; x++) {
			for(int z=0; z<16; z++) {
				int y = chunk.getHeightValue(x, z);
				int depth = 0;
				boolean abort;
				do {

					if(Common.isWoodLog(chunk, x,y-depth, z)) {
						Tree tree = inflateTree(chunk, x,y-depth, z);
						
						if(tree != null && !trees.contains(tree)) trees.add(tree);
						abort = true;
					} else {
						abort = (depth >= Constants.MAX_SEARCH_DEPTH);
					}
					depth++;
				} while(!abort);		
			}
		}
		
		
		return trees;
	}
		
	
	
	private Tree inflateTree(Chunk chunk, int sx, int sy, int sz) {
		Tree tree = new Tree(chunk, sx,sx, sy,sy, sz,sz);
		//int x1=sx, x2=sx, y1=sy, y2=sy, z1=sz, z2=sz;
		Coord2i c = inflateHeight(chunk, tree.c1.x, tree.c1.y, tree.c1.z);
		tree.c1.y = c.x;
		tree.c2.y = c.y;
		if(tree.c1.y == -1) return null;
		int y;
		do {
			tree.c1.x--;
			c = inflateHeight(chunk, tree.c1.x, tree.c2.y, tree.c1.z);
			tree.c1.y = Math.max(tree.c1.y, c.x);
			tree.c2.y = Math.min(tree.c2.y, c.y);
		} while(c.x != -1 && tree.c1.x > -Constants.MAX_TREE_SIZE);
		tree.c1.x++;
		
		do {
			tree.c2.x++;
			c = inflateHeight(chunk, tree.c2.x, tree.c2.y, tree.c1.z);
			tree.c1.y = Math.max(tree.c1.y, c.x);
			tree.c2.y = Math.min(tree.c2.y, c.y);
		} while(c.x != -1 && tree.c2.x < 15+Constants.MAX_TREE_SIZE);
		tree.c2.x--;
		
		do {
			tree.c1.z--;
			for(int x=tree.c1.x; x<=tree.c2.x;x++) {
				c = inflateHeight(chunk, x, tree.c2.y, tree.c1.z);
				tree.c1.y = Math.max(tree.c1.y, c.x);
				tree.c2.y = Math.min(tree.c2.y, c.y);
				if(c.x == -1) break;
			}
		} while(c.x != -1 && tree.c1.z > -Constants.MAX_TREE_SIZE);
		tree.c1.z++;
		
		do {
			tree.c2.z++;
			for(int x=tree.c1.x; x<=tree.c2.x;x++) {	
				c = inflateHeight(chunk, x, tree.c2.y, tree.c2.z);
				tree.c1.y = Math.max(tree.c1.y, c.x);
				tree.c2.y = Math.min(tree.c2.y, c.y);
				if(c.x == -1) break;
			}
		} while(c.x != -1 && tree.c1.z < 15+Constants.MAX_TREE_SIZE);
		tree.c2.z--;
		//TODO leaveID
		//Check if leave topping is present
		int leaveBlockID = 0;
		for(int x=tree.c1.x;x<=tree.c2.x;x++) {
			for(int z=tree.c1.z;z<=tree.c2.z;z++) {
				//leaveBlockID = getBlockID(chunk, x,tree.c2.y+1,z);
				if(!findTreeTopping(chunk,x,tree.c2.y,z)) return null;
			}
		}
		
		//Check if height is right
		if(tree.c2.y-tree.c1.y < Constants.MIN_TREE_HEIGHT-1) return null;
		
		tree.setLeaveID(leaveBlockID);
		tree.setWoodID(Common.getBlockID(chunk, tree.c1.x, tree.c1.y, tree.c1.z));
		return tree;
		
		
		
	}
	private void skipTreeTopping(Chunk chunk, int x, int z, Coord2i c) {
		while(isTreeTopping(chunk, x,c.y,z)) {
			c.y--;
			//System.out.println("Skip "+c.y+": "+chunk.getBlockID(x,c.y,z)+getBlockName(chunk.getBlockID(x,c.y,z)));
		}
		//The end of the tree is sometimes wood - leave/air - dirt. so look for dirt
		
		if(Common.isDirt(chunk, x, c.y, z)) {
			c.y += 1;
		}
	}
	private Coord2i inflateHeight(Chunk chunk, int x, int y, int z) {
		int blockID;
		Coord2i c = new Coord2i(y,y);
		int depth = 0;
		//System.out.println("Start:"+x+" "+z);
		//System.out.println("Start "+c.y+":"+chunk.getBlockID(x,c.y,z)+" "+getBlockName(chunk.getBlockID(x,c.y,z)));
//		while(isTreeTopping(chunk.getBlockID(x,c.y,z))) {
//			c.y--;
//			System.out.println(chunk.getBlockID(x,c.y,z)+getBlockName(chunk.getBlockID(x,c.y,z)));
//		}
		
		
//		while(isWoodLog(chunk, x,c.x,z) || isTreeTopping(chunk, x,c.x,z)) {
//			if(isTreeTopping(chunk, x,c.x,z)) {
//				c.y = c.x;
//				skipTreeTopping(chunk, x, z, c);
//				c.x = c.y;
//			}
//			//if(isLeave(chunk.getBlockID(x,c.x,z))) c.y = c.x;
//			c.x--;
//			//System.out.println("Log "+c.x+": "+chunk.getBlockID(x,c.x,z)+getBlockName(chunk.getBlockID(x,c.x,z)));
//		}
		//Skip treetopping
		while(isTreeTopping(chunk, x,c.y,z)) {
			c.y--;
		}
		c.x = c.y;
		//Skip logs
		while(Common.isWoodLog(chunk, x,c.x,z)) {
			c.x--;
		}
		//Check if below is something else than wood air and leaves
		int isBranch = 0;
		for(int xI=c.x; xI >= c.x-2; xI--) {
			if(Common.isAir(Common.getBlockID(chunk, x,c.x,z)) && Common.isLeave(chunk,x,c.x,z) && Common.isWoodLog(chunk, x,c.x,z)) isBranch++;
		}
		if(isBranch == 3) {
			c.x = -1;
			c.y = Integer.MAX_VALUE;
			return c;
		}
//		if((isAir(getBlockID(chunk, x,c.x,z)) || isLeave(chunk,x,c.x,z)) && (isAir(getBlockID(chunk, x,c.x-1,z)) || isLeave(chunk,x,c.x-1,z))) {
//			c.x = -1;
//			c.y = Integer.MAX_VALUE;
//			return c;
//		}
		
		c.x++;
		if(c.y-c.x < Constants.MIN_TREE_HEIGHT) {
			c.x = -1;
			c.y = Integer.MAX_VALUE;
			return c;
		}
		//System.out.println("End:"+x+" "+z);
		return c;
	}
	
	static boolean isTreeTopping(Chunk chunk, int x, int y, int z) {
		//System.out.println("isTreeTopping: "+x+" "+y+" "+z);
		int blockID = Common.getBlockID(chunk, x, y, z);
		//TODO Check if it does need to contain air
		return GrowthDataProvider.getInstance().isLeave(blockID)
				|| blockID == Block.vine.blockID;
	}
	static boolean findTreeTopping(Chunk chunk, int x, int y, int z) {
		while(Common.isWoodLog(chunk,x,y,z)) {
			y++;
		}
		return isTreeTopping(chunk, x, y, z);
	}
	
}
