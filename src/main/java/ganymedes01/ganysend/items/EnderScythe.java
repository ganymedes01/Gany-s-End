package ganymedes01.ganysend.items;

import ganymedes01.ganysend.GanysEnd;
import ganymedes01.ganysend.core.utils.BeheadingDamage;
import ganymedes01.ganysend.core.utils.Utils;
import ganymedes01.ganysend.lib.ModMaterials;
import ganymedes01.ganysend.lib.Strings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Gany's End
 * 
 * @author ganymedes01
 * 
 */

public class EnderScythe extends ItemSword {

	public EnderScythe() {
		super(ModMaterials.ENDIUM_TOOLS);
		setMaxStackSize(1);
		setCreativeTab(GanysEnd.endTab);
		setTextureName(Utils.getItemTexture(Strings.ENDER_SCYTHE_NAME));
		setUnlocalizedName(Utils.getUnlocalizedName(Strings.ENDER_SCYTHE_NAME));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity target) {
		if (!player.worldObj.isRemote && stack != null) {
			boolean damageTool = false;
			float dmg = 4.0F + ModMaterials.ENDIUM_TOOLS.getDamageVsEntity();

			if (target instanceof EntityLivingBase) {
				if (shouldDamage(target) && target.canAttackWithItem() && !target.hitByEntity(player))
					if (target.attackEntityFrom(BeheadingDamage.create(player), dmg)) {
						player.setSprinting(false);
						player.setLastAttacker(target);
						damageTool = true;
					}
			} else if (target instanceof EntityDragonPart) {
				((EntityDragonPart) target).entityDragonObj.attackEntityFromPart((EntityDragonPart) target, BeheadingDamage.create(player), dmg);
				damageTool = true;
			}
			if (damageTool) {
				stack.damageItem(1, player);
				player.addStat(StatList.damageDealtStat, Math.round(dmg * 10.0F));
				player.addExhaustion(0.3F);
				if (stack.stackSize <= 0)
					player.destroyCurrentEquippedItem();
			}
		}
		return true;
	}

	private boolean shouldDamage(Entity target) {
		return target instanceof EntityPlayer ? MinecraftServer.getServer().isPVPEnabled() : true;
	}

	@Override
	public boolean getIsRepairable(ItemStack item, ItemStack material) {
		return material.getItem() == ModItems.endiumIngot;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack item) {
		return true;
	}
}