package me.ferdz.placeableitems.block.tool;

import me.ferdz.placeableitems.block.BlockPlaceableItems;
import me.ferdz.placeableitems.state.EnumPreciseFacing;
import me.ferdz.placeableitems.state.tool.EnumSword;
import me.ferdz.placeableitems.state.tool.EnumToolMaterial;
import me.ferdz.placeableitems.tileentity.tool.TESword;
import me.ferdz.placeableitems.utils.AABBUtils;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSword extends BlockTool {

	public static final PropertyEnum<EnumSword> MODEL = PropertyEnum.create("smodel", EnumSword.class);
	public static final PropertyEnum<EnumToolMaterial> MATERIAL = PropertyEnum.create("material", EnumToolMaterial.class);
	
	private static final AxisAlignedBB 
			BOX_TOP = new AxisAlignedBB(0, 0, 0, 1, 2 / 16F, 1),
			BOX_TOP_VERTICAL = new AxisAlignedBB(4 / 16F, 0, 4 / 16F, 12 / 16F, 11 / 16F, 12 / 16F),
			BOX_SIDE = new AxisAlignedBB(0, 0, 0, 2 / 16F, 1, 1),
			BOX_LEAN = new AxisAlignedBB(0, 0, 0, 4 / 16F, 1, 1);
	
	public BlockSword(String name) {
		super(name);
	}
	
	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		IBlockState state = super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer);
		state = state.withProperty(MATERIAL, EnumToolMaterial.WOOD);
	
		switch(facing) {
		case UP:
			return state.withProperty(MODEL, EnumSword.TOP);
		case EAST:
			return state.withProperty(FACING, EnumPreciseFacing.D0).withProperty(MODEL, EnumSword.SIDE0);
		case SOUTH:
			return state.withProperty(FACING, EnumPreciseFacing.D90).withProperty(MODEL, EnumSword.SIDE0);
		case WEST:
			return state.withProperty(FACING, EnumPreciseFacing.D180).withProperty(MODEL, EnumSword.SIDE0);
		case NORTH:
			return state.withProperty(FACING, EnumPreciseFacing.D270).withProperty(MODEL, EnumSword.SIDE0);
		default:
			return state;
		}
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return true;
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
		Block under = worldIn.getBlockState(pos.subtract(new Vec3i(0, 1, 0))).getBlock();
		if((side == EnumFacing.EAST || side == EnumFacing.NORTH || side == EnumFacing.SOUTH || side == EnumFacing.WEST) && under == Blocks.AIR) {
			if(!(under instanceof BlockPlaceableItems)) {
				return true;
			}
			return false;
		}
		return true;
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		
		if(worldIn.isRemote)
			return;
		
		TESword te = (TESword)worldIn.getTileEntity(pos);
		te.setModel(state.getValue(MODEL));
		te.setStack(stack.copy());
		worldIn.notifyBlockUpdate(pos, state, state, 2);
		te.markDirty();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.isRemote)
			return true;
		
		TESword te = (TESword)world.getTileEntity(pos);
		EnumSword modelState = te.getModel();

		if (playerIn.isSneaking()) {
			modelState = modelState.previous();
			if (world.getBlockState(pos.subtract(new Vec3i(0, 1, 0))).getBlock() == Blocks.AIR && modelState == EnumSword.SIDE_LEAN) {
				modelState = modelState.previous();
			}
		} else {
			modelState = modelState.next();
			if (world.getBlockState(pos.subtract(new Vec3i(0, 1, 0))).getBlock() == Blocks.AIR && modelState == EnumSword.SIDE_LEAN) {
				modelState = modelState.next();
			}
		}
		
		te.setModel(modelState);
		world.notifyBlockUpdate(pos, state, state, 2);
		te.markDirty();
		return true;
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);
		if(te != null && te instanceof TESword) {
			TESword sword = (TESword) te;
			Item i = sword.getStack().getItem();
			state = state.withProperty(MODEL, sword.getModel());
			if(i == Items.WOODEN_SWORD)
				return state.withProperty(MATERIAL, EnumToolMaterial.WOOD);
			else if (i == Items.STONE_SWORD)
				return state.withProperty(MATERIAL, EnumToolMaterial.STONE);
			else if (i == Items.IRON_SWORD)
				return state.withProperty(MATERIAL, EnumToolMaterial.IRON);
			else if (i == Items.GOLDEN_SWORD)
				return state.withProperty(MATERIAL, EnumToolMaterial.GOLD);
			else if (i == Items.DIAMOND_SWORD)
				return state.withProperty(MATERIAL, EnumToolMaterial.DIAMOND);
		}
		return state;
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		state = state.getActualState(source, pos);
		switch(state.getValue(MODEL)) {
		case SIDE0:
		case SIDE45:
		case SIDE90:
		case SIDE135:
		case SIDE180:
		case SIDE225:
		case SIDE270:
		case SIDE315:
			switch(state.getValue(FACING)) {
			case D0:
				return BOX_SIDE;
			case D90:
				return AABBUtils.rotate(BOX_SIDE, 135);
			case D180:
				return AABBUtils.rotate(BOX_SIDE, 270);
			case D270:
				return AABBUtils.rotate(BOX_SIDE, 45);
			}
		case SIDE_LEAN:
		case SIDE_IN0:
		case SIDE_IN45:
		case SIDE_IN90:
		case SIDE_IN135:
		case SIDE_IN180:
		case SIDE_IN225:
		case SIDE_IN270:
		case SIDE_IN315:
		case SIDE_OUT0:
		case SIDE_OUT45:
		case SIDE_OUT90:
		case SIDE_OUT135:
		case SIDE_OUT180:
		case SIDE_OUT225:
		case SIDE_OUT270:
		case SIDE_OUT315:
			switch(state.getValue(FACING)) {
			case D0:
				return BOX_LEAN;
			case D90:
				return AABBUtils.rotate(BOX_LEAN, 135);
			case D180:
				return AABBUtils.rotate(BOX_LEAN, 270);
			case D270:
				return AABBUtils.rotate(BOX_LEAN, 45);
			}
		case TOP:
			return BOX_TOP;
		case TOP_VERTICAL:
			return BOX_TOP_VERTICAL;
		}
		return super.getBoundingBox(state, source, pos);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{ MODEL, MATERIAL, FACING });
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TESword();
	}
}
