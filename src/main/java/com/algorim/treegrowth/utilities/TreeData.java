package com.algorim.treegrowth.utilities;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;

/**
 * This class represents a tree type. For example a birch. 
 * It contains its identification and growth characteristics.
 * 
 * @author xeedness
 *
 */
public class TreeData {
	public transient static final int TYPE_BASIC = 0;
	public transient static final int TYPE_ROUND = 1;
	
	private String name;
	private int type;
	private int dim;
	private int woodID;
	private int woodMeta;
	private int leafID;
	private int leafMeta;
	private int saplingID;
	private int saplingMeta;
	private float fertility;
	
	public TreeData() {};
	public TreeData(String name, int type, int dim, int woodID, int woodMeta, int leafID, int leafMeta, int saplingID, int saplingMeta, float fertility) {
		this.name = name;
		this.type = type;
		this.dim = dim;
		this.woodID = woodID;
		this.woodMeta = woodMeta;
		this.leafID = leafID;
		this.leafMeta = leafMeta;
		this.saplingID = saplingID;
		this.saplingMeta = saplingMeta;
		this.fertility = fertility;
	}
	public boolean equals(int woodID, int woodMeta, int leafID, int leafMeta, int dim) {
		return this.woodID == woodID &&
				this.woodMeta == woodMeta &&
				this.leafID == leafID &&
				this.leafMeta == leafMeta &&
				this.dim == dim;
	}
	public String getName() {
		return name;
	}
	public int getType() {
		return type;
	}

	public int getDim() {
		return dim;
	}

	public int getWoodID() {
		return woodID;
	}

	public int getWoodMeta() {
		return woodMeta;
	}

	public int getLeafID() {
		return leafID;
	}

	public int getLeafMeta() {
		return leafMeta;
	}


	public int getSaplingID() {
		return saplingID;
	}


	public int getSaplingMeta() {
		return saplingMeta;
	}


	public float getFertility() {
		return fertility;
	}
	
	public ItemStack getSapling() {
		return new ItemStack(saplingID, 1, saplingMeta);
	}
	
	@Override
	public String toString() {
		return name+
		", type: "+type+", "+
		"wood: ("+woodID+":"+woodMeta+"), "+
		"leaves: ("+leafID+":"+leafMeta+"), "+
		"sapling: ("+saplingID+":"+saplingMeta+"), "+
		"fertility: "+fertility;
		
	}
	

}
