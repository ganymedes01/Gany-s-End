package ganymedes01.ganysend.inventory;

import ganymedes01.ganysend.tileentities.TileEntityFilteringHopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Gany's End
 * 
 * @author ganymedes01
 * 
 */

public class ContainerFilteringHopper extends Container {

	TileEntityFilteringHopper hopper;

	public ContainerFilteringHopper(InventoryPlayer inventory, TileEntityFilteringHopper tile) {
		hopper = tile;

		byte b0 = 44;
		byte b1 = 26;
		if (tile.isFilter())
			b0 = 26 + 18;
		else
			b1 = 20;

		for (int i = 0; i < tile.getSizeInventory(); i++)
			addSlotToContainer(new Slot(tile, i, b0 + i * 18, b1));
		if (tile.isFilter())
			addSlotToContainer(new Slot(tile, TileEntityFilteringHopper.FILER_SLOT, 80, 53));

		byte b2 = 84;
		if (!tile.isFilter())
			b2 = 51;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, i * 18 + b2));
		for (int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 58 + b2));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotIndex < hopper.getSizeInventory()) {
				if (!mergeItemStack(itemstack1, hopper.getSizeInventory(), inventorySlots.size(), true))
					return null;
			} else if (slotIndex < hopper.getSizeInventory() + 1) {
				if (!mergeItemStack(itemstack1, hopper.getSizeInventory() + 1, inventorySlots.size(), true))
					return null;
			} else if (!mergeItemStack(itemstack1, 0, hopper.getSizeInventory(), false))
				return null;

			if (itemstack1.stackSize == 0)
				slot.putStack((ItemStack) null);
			else
				slot.onSlotChanged();
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
}
