package com.algorim.treegrowth.utilities;

import java.util.Date;
import java.util.TreeMap;

public class Timer {
	static TreeMap<String, Long> timers = new TreeMap<String, Long>();
	static TreeMap<String, Float> accTime = new TreeMap<String, Float>();
	static TreeMap<String, Integer> accTimeN = new TreeMap<String, Integer>();
 	public static void startTimer(String name) {
		timers.put(name, new Date().getTime());
	}
	
	public static void stopTimer(String name) {
		if(timers.containsKey(name)) {
			long time = (new Date().getTime()-timers.get(name));
			float acc = accTime(name, time);
			System.out.println("Timer "+name+" took "+time+"("+acc+") ms.");
		} else {
			System.out.println("Timer does not exist.");
		}
	}
	
	private static float accTime(String name, long time) {
		if(accTime.containsKey(name)) {
			int n = accTimeN.get(name);
			float t = (accTime.get(name) * n) + time;
			n += 1;
			t /= n;
			accTimeN.put(name, n);
			accTime.put(name, t);
			return t;
		} else {
			accTime.put(name, (float)time);
			accTimeN.put(name, 1);
			return time;
		}
	}
}
