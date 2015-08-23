package com.algorim.treegrowth.config;

import java.io.File;



import org.apache.logging.log4j.Level;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;

public class IDConfiguration {
	
	public static Configuration config;
	public static int growthItemID;
	public static int treeGrowthConfigItemID;
	public static int stencilItemID;
	
	
	public static void init(String configFile)
	{
		try {
			config = new Configuration(new File(configFile));
			config.load();
			//growthItemID = config.getBlock("GrowthItem ID", 3000, null).getInt();
			//treeGrowthConfigItemID = config.getBlock("TreeGrowthConfigItem ID", 3001, null).getInt();
			//stencilItemID = config.getBlock("StencilItem ID", 3002, null).getInt();
		} catch (Exception e) {
			FMLLog.log(Level.WARN, e, "TreeGrowth has had a problem loading its configuration");
		} finally {
			if (config.hasChanged()) {
				config.save();
			}
		}
	}	
}
