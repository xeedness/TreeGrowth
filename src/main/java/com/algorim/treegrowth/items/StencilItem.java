package com.algorim.treegrowth.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.algorim.treegrowth.TreeGrowth;

public class StencilItem extends Item {

    public StencilItem(int arg0) {
    	super(arg0);
        maxStackSize = 1;
        setCreativeTab(CreativeTabs.tabMisc);
        setUnlocalizedName("stencilItem");
        setTextureName("treegrowth:stencilitem");
    }
    
    /**
	 * @see com.algorim.treegrowth.treedetection.TreeDetector#applyStencils(World, int, int, int)
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
    	
    	TreeGrowth.proxy.applyStencils(par3World,
    			par2EntityPlayer.getPlayerCoordinates().posX,
    			par2EntityPlayer.getPlayerCoordinates().posY,
    			par2EntityPlayer.getPlayerCoordinates().posZ);
    	return true;
    }
}