package ganymedes01.ganysend.integration;

import ganymedes01.ganysend.blocks.ModBlocks;
import ganymedes01.ganysend.lib.Reference;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

/**
 * Gany's End
 * 
 * @author ganymedes01
 * 
 */

public class NEIGanysEndConfig implements IConfigureNEI {

	@Override
	public void loadConfig() {
		API.hideItem(ModBlocks.enderToggler_air.blockID);
		API.hideItem(ModBlocks.blockNewSkull.blockID);
	}

	@Override
	public String getName() {
		return Reference.MOD_NAME;
	}

	@Override
	public String getVersion() {
		return Reference.VERSION_NUMBER;
	}
}