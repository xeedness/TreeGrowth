package com.algorim.treegrowth.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.algorim.treegrowth.TreeGrowth;

/**
 * Item that enabled or disables automatic chunk processing
 * 
 * @author xeedness
 *
 */
public class TreeGrowthConfigItem extends Item {
	private final String name = "TreeGrowthConfigItem";
    public TreeGrowthConfigItem() {
    	setUnlocalizedName(name);
        maxStackSize = 1;
        GameRegistry.registerItem(this, name);
        setCreativeTab(CreativeTabs.tabMisc);
    }
    
    public String getName() {
     	return name;
    }
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
    	if(!par2World.isRemote)
    		TreeGrowth.proxy.toggleAutoProcessing();
	    return par1ItemStack;
    }
}

