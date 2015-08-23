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

public class StencilItem extends Item {
	private final String name = "stencilItem";
    public StencilItem() {
    	setUnlocalizedName(name);
        maxStackSize = 1;
        GameRegistry.registerItem(this, name);
        setCreativeTab(CreativeTabs.tabMisc);
    }
	public String getName() {
     	return name;
    }
    /**
	 * @see com.algorim.treegrowth.treedetection.TreeDetector#applyStencils(World, int, int, int)
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
    //public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
    	
    	TreeGrowth.proxy.applyStencils(worldIn,
    			playerIn.getPosition().getX(),
    			playerIn.getPosition().getY(),
    			playerIn.getPosition().getZ());
    	return true;
    }
}