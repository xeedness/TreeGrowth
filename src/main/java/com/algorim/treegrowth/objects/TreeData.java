package com.algorim.treegrowth.objects;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;

public class TreeData {
	public transient static final int TYPE_BASIC = 0;
	public transient static final int TYPE_ROUND = 1;
	
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
	public TreeData(int type, int dim, int woodID, int woodMeta, int leafID, int leafMeta, int saplingID, int saplingMeta, float fertility) {
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
	

}
