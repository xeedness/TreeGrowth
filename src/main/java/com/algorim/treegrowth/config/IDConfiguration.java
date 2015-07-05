package com.algorim.treegrowth.config;

import java.io.File;
import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.Configuration;

public class IDConfiguration {
	
	public static Configuration config;
	public static int growthItemID;
	public static int treeGrowthConfigItemID;
	
	
	public static void init(String configFile)
	{
		try {
			config = new Configuration(new File(configFile));
			config.load();
			growthItemID = config.getBlock("GrowthItem ID", 3000, null).getInt();
			treeGrowthConfigItemID = config.getBlock("TreeGrowthConfigItem ID", 3001, null).getInt();
		} catch (Exception e) {
			FMLLog.log(Level.SEVERE, e, "TreeGrowth has had a problem loading its configuration");
		} finally {
			if (config.hasChanged()) {
				config.save();
			}
		}
	}	
}
