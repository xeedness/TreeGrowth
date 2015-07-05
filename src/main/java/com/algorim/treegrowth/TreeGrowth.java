package com.algorim.treegrowth;

import java.util.ArrayList;

import com.algorim.treegrowth.events.TickHandler;
import com.algorim.treegrowth.items.GrowthItem;
import com.algorim.treegrowth.items.TreeGrowthConfigItem;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

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
    	TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);		
    }
}
