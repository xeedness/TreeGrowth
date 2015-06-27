package com.algorim.treegrowth;

import java.util.Date;
import java.util.EnumSet;

import com.algorim.treegrowth.manager.ChunkGrowthData;
import com.algorim.treegrowth.manager.GrowthDataProvider;
import com.algorim.treegrowth.manager.GrowthProcessor;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler {
	long times = 0;
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		//Do nothing.
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if(GrowthProcessor.getInstance().getAutoProcessingEnabled()) {
			if(GrowthDataProvider.getInstance().needsProcessing()) {	
				long sTime = new Date().getTime();
				long cTime = new Date().getTime();
				GrowthDataProvider.getInstance().updateProcessing();
				while(cTime - sTime < Constants.MAX_PROCESSING_TIME) {
					Timer.startTimer("ChunkLookUp");
					ChunkGrowthData data = GrowthDataProvider.getInstance().getNextProcessing();
					Timer.stopTimer("ChunkLookUp");
					if(data != null) {
						Timer.startTimer("ChunkProcess");
						GrowthProcessor.getInstance().processChunk(data.chunk);
						Timer.stopTimer("ChunkProcess");
						data.updateProcessing();
					} else {
						break;
					}
					cTime = new Date().getTime();
				}
				
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() {
		return null;
	}
	

}
