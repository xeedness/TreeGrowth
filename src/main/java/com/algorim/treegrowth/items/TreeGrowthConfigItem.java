package com.algorim.treegrowth.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.algorim.treegrowth.TreeGrowth;

/**
 * Item that enabled or disables automatic chunk processing
 * 
 * @author xeedness
 *
 */
public class TreeGrowthConfigItem extends Item {

    public TreeGrowthConfigItem(int arg0) {
    	super(arg0);
        maxStackSize = 1;
        setCreativeTab(CreativeTabs.tabMisc);
        setUnlocalizedName("TreeGrowthConfigItem");
    }
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
    	if(!par2World.isRemote)
    		TreeGrowth.proxy.toggleAutoProcessing();
	    return par1ItemStack;
    }
}

