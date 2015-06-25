package com.algorim.treegrowth.items;

//@NetworkMod(clientSideRequired=true, serverSideRequired=false) //Used in 1.6.4 and before
import com.algorim.treegrowth.TreeGrowth;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GrowthItem extends Item {

        public GrowthItem(int arg0) {
        	super(arg0);
            maxStackSize = 1;
            setCreativeTab(CreativeTabs.tabMisc);
            setUnlocalizedName("growthItem");
        }
        
        @Override
        public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
        	System.out.println("Item used: GrowthItem");
        	
        	TreeGrowth.proxy.processChunkAtWorldCoords(par3World,
        			par2EntityPlayer.getPlayerCoordinates().posX,
        			par2EntityPlayer.getPlayerCoordinates().posZ);
        	
        	return true;
        }
}

