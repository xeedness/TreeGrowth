package com.algorim.treegrowth.config;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class IDConfiguration {
	
	public static Configuration config;
	public static int growthItemID;
	public static int treeGrowthConfigItemID;
	
	
	public static void init(String configFile)
	{
		config = new Configuration(new File(configFile));
		growthItemID = config.getBlock("GrowthItem ID", 3000, null).getInt();
		treeGrowthConfigItemID = config.getBlock("TreeGrowthConfigItem ID", 3001, null).getInt();
	}	
}
