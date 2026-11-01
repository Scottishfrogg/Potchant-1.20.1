package net.scottishfrogg.potchant.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.scottishfrogg.potchant.registry.ModEffects;

public class BoundSlot extends Slot {
    private final Slot original;
    public BoundSlot(Slot original) {
        super(original.container, original.getContainerSlot(), original.x, original.y);
        this.original = original;
        this.index = original.index;
    }
    private boolean bound() {
        return this.container instanceof Inventory inventory
                && !inventory.player.isCreative()
                && inventory.player.hasEffect(ModEffects.CURSE_OF_BINDING.get());
    }
    @Override
    public boolean mayPlace(ItemStack stack) {
        return !bound() && original.mayPlace(stack);
    }
    @Override
    public boolean mayPickup(Player player) {
        return !bound() && original.mayPickup(player);
    }
    @Override
    public int getMaxStackSize() {
        return original.getMaxStackSize();
    }
    @Override
    public int getMaxStackSize(ItemStack stack) {
        return original.getMaxStackSize(stack);
    }
    public static void wrapInventorySlots(AbstractContainerMenu menu) {
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            if (!(slot instanceof BoundSlot) && slot.container instanceof Inventory) {
                menu.slots.set(i, new BoundSlot(slot));
            }
        }
    }
}
