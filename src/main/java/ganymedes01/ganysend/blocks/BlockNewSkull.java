package ganymedes01.ganysend.blocks;

import ganymedes01.ganysend.ModItems;
import ganymedes01.ganysend.core.utils.InventoryUtils;
import ganymedes01.ganysend.core.utils.Utils;
import ganymedes01.ganysend.lib.SkullTypes;
import ganymedes01.ganysend.lib.Strings;
import ganymedes01.ganysend.tileentities.TileEntityBlockNewSkull;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Gany's End
 *
 * @author ganymedes01
 *
 */

public class BlockNewSkull extends BlockContainer {

	public BlockNewSkull() {
		super(Material.circuits);
		setHardness(1.0F);
		setBlockTextureName("skull");
		setStepSound(soundTypeStone);
		setBlockName(Utils.getUnlocalizedName(Strings.BLOCK_NEW_SKULL_NAME));
	}

	@Override
	public int getRenderType() {
		return RenderingRegistry.getNextAvailableRenderId();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
		int meta = access.getBlockMetadata(x, y, z) & 7;
		switch (meta) {
			case 1:
			default:
				setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
				break;
			case 2:
				setBlockBounds(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
				break;
			case 3:
				setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
				break;
			case 4:
				setBlockBounds(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
				break;
			case 5:
				setBlockBounds(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		int l = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 2.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, l, 2);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityBlockNewSkull();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return ModItems.itemNewSkull;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		Item item = getItem(world, x, y, z);

		if (item == null)
			return null;

		ItemStack stack = new ItemStack(item, 1, getDamageValue(world, x, y, z));
		TileEntityBlockNewSkull tile = Utils.getTileEntity(world, x, y, z, TileEntityBlockNewSkull.class);
		if (tile != null)
			if (tile.func_145904_a() == SkullTypes.player.ordinal() && tile.func_152108_a() != null) {
				stack.setTagCompound(new NBTTagCompound());
				NBTTagCompound profileData = new NBTTagCompound();
				NBTUtil.func_152460_a(profileData, tile.func_152108_a());
				stack.getTagCompound().setTag("SkullOwner", profileData);
			}

		return stack;
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		TileEntity tileentity = world.getTileEntity(x, y, z);
		return tileentity != null && tileentity instanceof TileEntityBlockNewSkull ? ((TileEntityBlockNewSkull) tileentity).func_145904_a() : super.getDamageValue(world, x, y, z);
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode) {
			ArrayList<ItemStack> drops = getDrops(world, x, y, z, meta, 0);
			if (ForgeEventFactory.fireBlockHarvesting(drops, world, this, x, y, z, meta, 0, 1.0F, false, player) > 0.0F)
				for (ItemStack stack : drops)
					InventoryUtils.dropStack(world, x, y, z, stack);
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

		TileEntityBlockNewSkull tile = Utils.getTileEntity(world, x, y, z, TileEntityBlockNewSkull.class);
		if (tile == null)
			return drops;
		ItemStack stack = new ItemStack(ModItems.itemNewSkull, 1, tile.func_145904_a());

		if (tile.func_145904_a() == SkullTypes.player.ordinal() && tile.func_152108_a() != null) {
			stack.setTagCompound(new NBTTagCompound());
			NBTTagCompound profileData = new NBTTagCompound();
			NBTUtil.func_152460_a(profileData, tile.func_152108_a());
			stack.getTagCompound().setTag("SkullOwner", profileData);
		}
		drops.add(stack);

		return drops;
	}

	@Override
	public int quantityDropped(Random rand) {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return Blocks.soul_sand.getBlockTextureFromSide(side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemIconName() {
		return getTextureName() + "_" + ItemSkull.field_94587_a[0];
	}
}