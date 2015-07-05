package com.algorim.treegrowth.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.logging.Level;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;







import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;






import com.algorim.treegrowth.utilities.TreeData;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.FMLLog;

/**
 * This class holds the different tree structures
 * 
 * @author xeedness
 *
 */
public class TreeConfiguration {
	ArrayList<TreeData> treeData;
	ArrayList<Integer> woodIDs;
	ArrayList<Integer> leafIDs;
	
	public TreeConfiguration(String filename) {
		woodIDs = new ArrayList<Integer>();
		leafIDs = new ArrayList<Integer>();
		treeData = new ArrayList<TreeData>();
		if(new File(filename).exists()) 
			read(filename);
		else
			init(filename);
	}
	
	
	/**
	 * Initializes tree configuration file.
	 * 
	 * @param filename
	 */
	private void init(String filename) {
		initVanilla();
		
		Gson gson = new Gson();
		try {
			String out = gson.toJson(treeData);
			out = out.replace("{","{\r\n");
			out = out.replace("}","}\r\n");
			out = out.replace("[","[\r\n");
			out = out.replace("]","]\r\n");
			out = out.replace(",",",\r\n");
			FileUtils.write(new File(filename), out);
		} catch (IOException e) {
			FMLLog.log(Level.SEVERE, "TreeGrowth could not initialize tree config due to "+e.getMessage());
		}
	}
	
	/**
	 * Adds vanilla tree data.
	 */
	private void initVanilla() {
		treeData.add(new TreeData("Oak",TreeData.TYPE_BASIC,1, 17, 0, 18, 0, 6, 0, 0.5f));
		treeData.add(new TreeData("Spruce", TreeData.TYPE_BASIC,1, 17, 1, 18, 1, 6, 1, 0.5f));
		treeData.add(new TreeData("Birch", TreeData.TYPE_BASIC,1, 17, 2, 18, 2, 6, 2, 0.5f));
		treeData.add(new TreeData("Jungle1", TreeData.TYPE_BASIC,1, 17, 3, 18, 3, 6, 3, 0.5f));
		treeData.add(new TreeData("Jungle2",TreeData.TYPE_BASIC,2, 17, 3, 18, 3, 6, 3, 0.5f));
		
		for(TreeData d : treeData) {
			if(!woodIDs.contains(d.getWoodID()))
				woodIDs.add(d.getWoodID());
			if(!leafIDs.contains(d.getLeafID()))
				leafIDs.add(d.getLeafID());
		}
		
	}
	
	/**
	 * Reads in a configuration file
	 * 
	 * @param filename
	 */
	public void read(String filename) {
		Gson gson = new Gson();
		
		Type collectionType = new TypeToken<Collection<TreeData>>(){}.getType();
		try {
			treeData = gson.fromJson(FileUtils.readFileToString(new File(filename)), collectionType);
		} catch (JsonSyntaxException e) {
			FMLLog.log(Level.SEVERE, "TreeGrowth could not load tree config due to "+e.getMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			FMLLog.log(Level.SEVERE, "TreeGrowth could not load tree config due to "+e.getMessage());
			//e.printStackTrace();
		}
		
		for(TreeData d : treeData) {
			if(!woodIDs.contains(d.getWoodID()))
				woodIDs.add(d.getWoodID());
			if(!leafIDs.contains(d.getLeafID()))
				leafIDs.add(d.getLeafID());
		}
	}
	
	/**
	 * Looks up a fitting tree structure. 
	 * 
	 * @param woodID
	 * @param woodMeta
	 * @param leafID
	 * @param leafMeta
	 * @param dim
	 * @return null, if no fitting structure could be found.
	 */
	public TreeData getTreeData(int woodID, int woodMeta, int leafID, int leafMeta, int dim) {
		for(TreeData d : treeData) {
			//System.out.println("Looking up TreeData: "+woodID+" "+woodMeta+" "+leafID+" "+leafMeta+" "+dim);
			if(d.equals(woodID, woodMeta, leafID, leafMeta, dim)) return d;			
		}
		FMLLog.log(Level.INFO, "TreeGrowth TreeConfiguration does not contain detected tree.");
		return null;
	}
	
	/**
	 * Checks if the configured tree data contains wood id
	 * @param id
	 * @return
	 */
	public boolean isWood(int id) {
		return woodIDs.contains(id);
	}
	
	/**
	 * @param id
	 * @return
	 */
	public boolean isLeaves(int id) {
		return leafIDs.contains(id);
	}
}
