package com.algorim.treegrowth.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.algorim.treegrowth.TreeGrowth;

/**
 * Item that processes a chunk, when used.
 * 
 * @author xeedness
 *
 */
public class GrowthItem extends Item {
		private final String name = "growthItem";
        public GrowthItem() {
        	setUnlocalizedName(name);
        	GameRegistry.registerItem(this, name);
            maxStackSize = 1;
            setCreativeTab(CreativeTabs.tabMisc);
        }

        
        public String getName() {
        	return name;
        }
        @Override
        public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {       	
        	//TODO Check if this works
        	TreeGrowth.proxy.processChunkAtWorldCoords(worldIn,
        			playerIn.getPosition().getX(),
        			playerIn.getPosition().getZ());
        	
        	return true;
        }
}

