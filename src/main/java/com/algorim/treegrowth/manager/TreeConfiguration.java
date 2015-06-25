package com.algorim.treegrowth.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;







import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;




import com.algorim.treegrowth.objects.TreeData;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class TreeConfiguration {
	ArrayList<TreeData> treeData;
	ArrayList<Integer> woodIDs;
	ArrayList<Integer> leafIDs;
	
	TreeConfiguration(String filename) {
		woodIDs = new ArrayList<Integer>();
		leafIDs = new ArrayList<Integer>();
		treeData = new ArrayList<TreeData>();
		read(filename);
	}
	
	public void read(String filename) {
		Gson gson = new Gson();
		
		Type collectionType = new TypeToken<Collection<TreeData>>(){}.getType();
		try {
			treeData = gson.fromJson(FileUtils.readFileToString(new File(filename)), collectionType);
		} catch (JsonSyntaxException e) {
			System.out.println("Could not load TreeGrowth config due to "+e.getMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not load TreeGrowth config due to "+e.getMessage());
			//e.printStackTrace();
		}
		
		for(TreeData d : treeData) {
			if(!woodIDs.contains(d.getWoodID()))
				woodIDs.add(d.getWoodID());
			if(!leafIDs.contains(d.getLeafID()))
				leafIDs.add(d.getLeafID());
		}
	}
	
	public TreeData getTreeData(int woodID, int woodMeta, int leafID, int leafMeta, int dim) {
		for(TreeData d : treeData) {
			//System.out.println("Looking up TreeData: "+woodID+" "+woodMeta+" "+leafID+" "+leafMeta+" "+dim);
			if(d.equals(woodID, woodMeta, leafID, leafMeta, dim)) return d;			
		}
		System.out.println("TreeConfiguration does not contain detected tree.");
		return null;
	}
	
	public boolean isWood(int id) {
		return woodIDs.contains(id);
	}
	
	public boolean isLeaves(int id) {
		return leafIDs.contains(id);
	}
}
