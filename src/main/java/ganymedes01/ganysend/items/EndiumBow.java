package ganymedes01.ganysend.items;

import java.util.Arrays;
import java.util.List;

import ganymedes01.ganysend.GanysEnd;
import ganymedes01.ganysend.IConfigurable;
import ganymedes01.ganysend.ModItems.ISubItemsItem;
import ganymedes01.ganysend.api.IEndiumTool;
import ganymedes01.ganysend.core.utils.Utils;
import ganymedes01.ganysend.lib.ModMaterials;
import ganymedes01.ganysend.lib.Strings;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * Gany's End
 *
 * @author ganymedes01
 *
 */

public class EndiumBow extends ItemBow implements IEndiumTool, IConfigurable, ISubItemsItem {

	public EndiumBow() {
		setMaxDamage(ModMaterials.ENDIUM_TOOLS.getMaxUses());
		setCreativeTab(GanysEnd.enableEndiumTools ? GanysEnd.endTab : null);
		setUnlocalizedName(Utils.getUnlocalisedName(Strings.ENDIUM_BOW_NAME));
	}

	private IItemHandler getTaggedInventory(World world, ItemStack stack) {
		if (stack.hasTagCompound())
			if (stack.getTagCompound().getBoolean("Tagged")) {
				NBTTagCompound data = stack.getTagCompound();
				int[] pos = data.getIntArray("Position");
				int x = pos[0];
				int y = pos[1];
				int z = pos[2];
				int dim = data.getInteger("Dimension");

				if (world.provider.getDimensionId() == dim) {
					TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
					if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
						return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				}
			}
		return null;
	}

	private boolean consumeArrow(EntityPlayer player, ItemStack stack) {
		IItemHandler itemHandler = getTaggedInventory(player.worldObj, stack);
		if (itemHandler != null)
			for (int i = 0; i < itemHandler.getSlots(); i++) {
				ItemStack invtStack = itemHandler.getStackInSlot(i);
				if (invtStack != null && invtStack.getItem() == Items.arrow && invtStack.stackSize > 0) {
					ItemStack extracted = itemHandler.extractItem(i, 1, false);
					return extracted != null;
				}
			}

		return player.inventory.consumeInventoryItem(Items.arrow);
	}

	private boolean hasArrowsAvailable(EntityPlayer player, ItemStack stack) {
		IItemHandler itemHandler = getTaggedInventory(player.worldObj, stack);
		if (itemHandler != null)
			for (int i = 0; i < itemHandler.getSlots(); i++) {
				ItemStack invtStack = itemHandler.getStackInSlot(i);
				if (invtStack != null && invtStack.getItem() == Items.arrow && invtStack.stackSize > 0) {
					ItemStack extracted = itemHandler.extractItem(i, 1, true);
					return extracted != null;
				}
			}

		return player.inventory.hasItem(Items.arrow);
	}

	private boolean consumesArrow(EntityPlayer player, ItemStack stack) {
		return player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;
	}

	private boolean canFire(EntityPlayer player, ItemStack stack) {
		return consumesArrow(player, stack) || hasArrowsAvailable(player, stack);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		ArrowNockEvent event = new ArrowNockEvent(player, stack);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled())
			return event.result;

		if (canFire(player, stack))
			player.setItemInUse(stack, getMaxItemUseDuration(stack));

		return stack;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int itemUseCount) {
		if (world.isRemote)
			return;

		ArrowLooseEvent event = new ArrowLooseEvent(player, stack, getMaxItemUseDuration(stack) - itemUseCount);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled())
			return;

		if (canFire(player, stack)) {
			float f = event.charge / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;

			if (f < 0.1D)
				return;

			if (f > 1.0F)
				f = 1.0F;

			EntityArrow arrow = new EntityArrow(world, player, f * 2.0F);

			boolean consumedArrow = false;
			if (consumesArrow(player, stack)) {
				arrow.canBePickedUp = 2;
				consumedArrow = true;
			} else
				consumedArrow = consumeArrow(player, stack);

			if (!consumedArrow) {
				arrow.setDead();
				return;
			}

			if (f == 1.0F)
				arrow.setIsCritical(true);

			int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
			if (power > 0)
				arrow.setDamage(arrow.getDamage() + power * 0.5D + 0.5D);

			int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
			if (punch > 0)
				arrow.setKnockbackStrength(punch);

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0)
				arrow.setFire(100);

			stack.damageItem(1, player);
			world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

			world.spawnEntityInWorld(arrow);
		}
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setBoolean("Tagged", false);
	}

	@Override
	public boolean getIsRepairable(ItemStack item, ItemStack material) {
		return Utils.isStackOre(material, "ingotEndium");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		IInventory tile = Utils.getTileEntity(world, pos, IInventory.class);
		if (tile != null)
			if (stack.getItem() == this) {
				if (!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
				NBTTagCompound nbt = stack.getTagCompound();
				nbt.setIntArray("Position", new int[] { pos.getX(), pos.getY(), pos.getZ() });
				nbt.setInteger("Dimension", world.provider.getDimensionId());
				nbt.setBoolean("Tagged", true);
				player.swingItem();
				return false;
			}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("Position");
	}

	@Override
	public int getItemEnchantability() {
		return ModMaterials.ENDIUM_TOOLS.getEnchantability();
	}

	@Override
	public boolean isEnabled() {
		return GanysEnd.enableEndiumTools;
	}

	@Override
	public List<String> getModels() {
		return Arrays.asList("endium_bow", "endium_bow_pulling_0", "endium_bow_pulling_1", "endium_bow_pulling_2");
	}
}