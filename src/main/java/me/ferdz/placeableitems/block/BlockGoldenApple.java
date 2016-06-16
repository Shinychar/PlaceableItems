package me.ferdz.placeableitems.block;

import me.ferdz.placeableitems.block.state.EnumApplePosition;
import me.ferdz.placeableitems.block.state.EnumCarrotType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGoldenApple extends BlockPlaceableItems {

	public static final PropertyEnum<EnumApplePosition> POSITION = PropertyEnum.create("position", EnumApplePosition.class);
	
	public BlockGoldenApple(String name) {
		super(Material.WOOD, name);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(POSITION)) {
		case DOWN:
			return new AxisAlignedBB(0.1875, 0, 0.1875, 0.8125, 0.75, 0.8125);
		case UP:
			return new AxisAlignedBB(0.1875, 0.375, 0.1875, 0.8125, 1, 0.8125);

		default:
			return null;
		}
	}

	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if(facing == EnumFacing.DOWN)
			return super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(POSITION, EnumApplePosition.UP);
		return super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(POSITION, EnumApplePosition.DOWN);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState s = super.getStateFromMeta(meta % 8);
		s = s.withProperty(POSITION, EnumApplePosition.values()[(int)(meta / 8)]);
		return s;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int face = state.getValue(FACING).ordinal();
		int position = state.getValue(POSITION).getID();
		return face + (position * 8);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{ POSITION, FACING });
    }
}