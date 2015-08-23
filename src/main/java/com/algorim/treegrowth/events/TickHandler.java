package com.algorim.treegrowth.events;

import java.util.Date;
import java.util.EnumSet;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.algorim.treegrowth.config.Constants;
import com.algorim.treegrowth.manager.ChunkGrowthData;
import com.algorim.treegrowth.manager.GrowthDataProvider;
import com.algorim.treegrowth.manager.GrowthProcessor;
import com.algorim.treegrowth.utilities.Timer;
//TODO Maybe side only?
public class TickHandler {
	long times = 0;
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		
		if (event.phase == Phase.END) {
			if(Constants.AUTO_PROCESSING_ENABLED) {
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
	}
}
