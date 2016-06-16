package me.ferdz.placeableitems.block;

import java.util.Random;

import me.ferdz.placeableitems.block.state.EnumCarrotType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockCarrot extends BlockPlaceableItems {

	public static final PropertyEnum<EnumCarrotType> TYPE = PropertyEnum.create("type", EnumCarrotType.class);
	
	public BlockCarrot(Material material, String name) {
		super(Material.WOOD, name);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		switch (state.getValue(TYPE)) {
		case GOLDEN:
			return new ItemStack(Items.GOLDEN_CARROT, 1);
		case NORMAL:
			return new ItemStack(Items.CARROT, 1);

		default:
			return null;
		}
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		switch (state.getValue(TYPE)) {
		case GOLDEN:
			return Items.GOLDEN_CARROT;
		case NORMAL:
			return Items.CARROT;

		default:
			return null;
		}

	}
	
	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if(placer.getHeldItemMainhand().getItem().equals(Items.CARROT))
			return super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(TYPE, EnumCarrotType.NORMAL);
		else if(placer.getHeldItemMainhand().getItem().equals(Items.GOLDEN_CARROT))
			return super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(TYPE, EnumCarrotType.GOLDEN);
		return null;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState s = super.getStateFromMeta(meta % 8);
		s = s.withProperty(TYPE, EnumCarrotType.values()[(int)(meta / 8)]);
		return s;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int face = state.getValue(FACING).ordinal();
		int type = state.getValue(TYPE).getID();
		return face + (type * 8);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{ TYPE, FACING });
    }
}
