package com.algorim.treegrowth;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		
		ModelResourceLocation res = new ModelResourceLocation("treegrowth:growthItem", "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(CommonProxy.mGrowthItem, 0, res);
		
		ModelResourceLocation res2 = new ModelResourceLocation("treegrowth:stencilItem", "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(CommonProxy.mStencilItem, 0, res2);
		
		
		
	}
}
