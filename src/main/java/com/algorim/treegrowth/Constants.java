package com.algorim.treegrowth;

public class Constants {
	
	
	/************************************/
	/************ Schedule **************/
	/************************************/

	//TESTING Values
	//If the current processing step takes less than this time, another one is scheduled
	public static int MAX_PROCESSING_TIME = 10;
	
	//The rate in ms at which processing steps are initiated
	public static int GLOBAL_PROCESSING_TIME = 1000;
	
	//The time in ms a world tick takes
	public static int TICK_TIME = 50;
	
	//The update schedule of a chunk in m*s*ms
	public static long CHUNK_UPDATE_TIME = 1*5*1000;
	
	
//	//Real Values
//	public static int MAX_PROCESSING_TIME = 10;
//	
//	//The rate in ms at which processing steps are initiated
//	public static int GLOBAL_PROCESSING_TIME = 5000;
//	
//	//The time in ms a world tick takes
//	public static int TICK_TIME = 50;
//	
//	//The update schedule of a chunk in m*s*ms
//	public static long CHUNK_UPDATE_TIME = 10*60*1000;
	
	/************************************/
	/******** Tree Processsing **********/
	/************************************/
	public static int MIN_SEARCH_DEPTH = 2;
	public static int MAX_SEARCH_DEPTH = 6;
	public static int MIN_TREE_HEIGHT = 3;
	public static int MAX_TREE_SIZE = 2;
	
	
	public static int RATING_THRESHOLD = 16;
	public static float ARIDITY_THRESHOLD = 0.5f;
	public static float FERTILITY_THRESHOLD = 0.3f;
}
