package ganymedes01.ganysend.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.ganysend.core.utils.Utils;
import ganymedes01.ganysend.lib.ModMaterials;
import ganymedes01.ganysend.lib.Strings;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

/**
 * Gany's End
 * 
 * @author ganymedes01
 * 
 */

public class ReinforcedEndiumPickaxe extends EndiumPickaxe {

	public ReinforcedEndiumPickaxe() {
		super(ModMaterials.REIN_ENDIUM_TOOLS);
		setHarvestLevel("pickaxe", 3);
		setTextureName(Utils.getItemTexture(Strings.REINFORCED_ENDIUM_PICKAXE_NAME));
		setUnlocalizedName(Utils.getUnlocalisedName(Strings.REINFORCED_ENDIUM_PICKAXE_NAME));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.rare;
	}
}