package ganymedes01.ganysend.core.proxy;

import ganymedes01.ganysend.blocks.ModBlocks;
import ganymedes01.ganysend.client.renderer.item.ItemPlayerInventoryRender;
import ganymedes01.ganysend.client.renderer.item.ItemTimeLordRender;
import ganymedes01.ganysend.client.renderer.tileentity.TileEntityBlockNewSkullRender;
import ganymedes01.ganysend.client.renderer.tileentity.TileEntityPlayerInventoryRender;
import ganymedes01.ganysend.client.renderer.tileentity.TileEntityTimeLordRender;
import ganymedes01.ganysend.tileentities.TileEntityBlockNewSkull;
import ganymedes01.ganysend.tileentities.TileEntityPlayerInventory;
import ganymedes01.ganysend.tileentities.TileEntityTimeManipulator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;

/**
 * Gany's End
 * 
 * @author ganymedes01
 * 
 */

public class ClientProxy extends CommonProxy {
	@Override
	public void registerTileEntities() {
		super.registerTileEntities();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBlockNewSkull.class, new TileEntityBlockNewSkullRender());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTimeManipulator.class, new TileEntityTimeLordRender());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlayerInventory.class, new TileEntityPlayerInventoryRender());
	}

	@Override
	public void registerRenderers() {
		MinecraftForgeClient.registerItemRenderer(ModBlocks.timeManipulator.blockID, new ItemTimeLordRender());
		MinecraftForgeClient.registerItemRenderer(ModBlocks.playerInventory.blockID, new ItemPlayerInventoryRender());
	}

	@Override
	public void handleTimeManipulatorPacket(int x, int y, int z, boolean revertTime, boolean advanceTime) {
		World world = FMLClientHandler.instance().getClient().theWorld;
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
			if (tileEntity instanceof TileEntityTimeManipulator) {
				((TileEntityTimeManipulator) tileEntity).revertTime = revertTime;
				((TileEntityTimeManipulator) tileEntity).advanceTime = advanceTime;
			}
	}

	@Override
	public void handlePlayerInventoryPacket(int x, int y, int z, String playerName) {
		World world = FMLClientHandler.instance().getClient().theWorld;
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
			if (tileEntity instanceof TileEntityPlayerInventory)
				((TileEntityPlayerInventory) tileEntity).setPlayerName(playerName);
	}
}