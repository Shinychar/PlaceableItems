package com.stuntmania.placeableitems.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.stuntmania.placeableitems.PlaceableItems;
import com.stuntmania.placeableitems.tileentity.TESnowball;

public class BlockSnowball extends BlockPlaceableItems {
	
	public BlockSnowball() {
		super(Material.sponge);
		this.setBlockBounds(0.25F, 0, 0.25F, 0.75F, 0.5F, 0.75F);
		this.setBlockTextureName(PlaceableItems.MODID + ":snowball");
	}
	
	@Override
	public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
		if (!(entity instanceof EntityItem)) {
			world.removeTileEntity(x, y, z);
			world.setBlock(x, y, z, Blocks.snow_layer);
			world.setBlockMetadataWithNotify(x, y, z, 0, 2);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TESnowball();
	}
	
	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return Items.snowball;
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return new ItemStack(Items.snowball);
	}
}