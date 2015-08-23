package com.algorim.treegrowth;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.algorim.treegrowth.events.TickHandler;

@Mod(modid = TreeGrowth.MODID, version = TreeGrowth.VERSION)
public class TreeGrowth
{
	public static final String MODNAME = "Tree Growth";
    public static final String MODID = "treegrowth";
    public static final String VERSION = "0.0.0";
    
    
   

	@SidedProxy(clientSide="com.algorim.treegrowth.ClientProxy",
	            serverSide="com.algorim.treegrowth.ServerProxy")
	public static CommonProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
		proxy.preInit(event);
    }
        
    @EventHandler
    public void load(FMLInitializationEvent event)
    {

    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	
    }
    
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init(event);
    	//TODO Check if this works
    	FMLCommonHandler.instance().bus().register(new TickHandler());
    	//TickRegistry.registerTickHandler(, Side.SERVER);		
    }
}
