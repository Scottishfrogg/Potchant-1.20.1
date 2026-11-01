package net.scottishfrogg.potchant.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.scottishfrogg.potchant.PotchantMod;
import net.scottishfrogg.potchant.menu.BoundSlot;
import net.scottishfrogg.potchant.registry.ModEffects;

@Mod.EventBusSubscriber(modid = PotchantMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BindingEvents {
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BoundSlot.wrapInventorySlots(player.inventoryMenu);
        }
    }
    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BoundSlot.wrapInventorySlots(player.inventoryMenu);
        }
    }
    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BoundSlot.wrapInventorySlots(player.inventoryMenu);
        }
    }
    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getEntity() instanceof ServerPlayer) {
            BoundSlot.wrapInventorySlots(event.getContainer());
        }
    }
    @SubscribeEvent
    public static void onToss(ItemTossEvent event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide || player.isCreative()
                || !player.hasEffect(ModEffects.CURSE_OF_BINDING.get())) {
            return;
        }
        ItemStack dropped = event.getEntity().getItem().copy();
        event.setCanceled(true);
        Inventory inventory = player.getInventory();
        ItemStack selected = inventory.getItem(inventory.selected);
        if (selected.isEmpty()) {
            inventory.setItem(inventory.selected, dropped);
        } else if (ItemStack.isSameItemSameTags(selected, dropped)
                && selected.getCount() + dropped.getCount() <= selected.getMaxStackSize()) {
            selected.grow(dropped.getCount());
        } else {
            inventory.add(dropped);
        }
    }
    @SubscribeEvent
    public static void onSwapHands(LivingSwapItemsEvent.Hands event) {
        if (event.getEntity() instanceof Player player && !player.isCreative()
                && player.hasEffect(ModEffects.CURSE_OF_BINDING.get())) {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && player.hasEffect(ModEffects.CURSE_OF_VANISHING.get())) {
            player.getInventory().clearContent();
        }
    }
}
