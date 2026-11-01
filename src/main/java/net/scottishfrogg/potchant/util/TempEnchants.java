package net.scottishfrogg.potchant.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class TempEnchants {
    private record Entry(ItemStack stack, Enchantment enchantment, int previousLevel) {
    }
    private static final Map<UUID, List<Entry>> ACTIVE = new HashMap<>();
    private TempEnchants() {
    }
    public static boolean apply(Player player, ItemStack stack, Enchantment enchantment, int level) {
        if (stack.isEmpty()) {
            return false;
        }
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        int previous = enchantments.getOrDefault(enchantment, 0);
        if (previous >= level) {
            return false;
        }
        enchantments.put(enchantment, level);
        EnchantmentHelper.setEnchantments(enchantments, stack);
        ACTIVE.computeIfAbsent(player.getUUID(), id -> new ArrayList<>())
                .add(new Entry(stack, enchantment, previous));
        return true;
    }
    public static void cleanup(Player player) {
        List<Entry> entries = ACTIVE.remove(player.getUUID());
        if (entries == null) {
            return;
        }
        for (int i = entries.size() - 1; i >= 0; i--) {
            Entry entry = entries.get(i);
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(entry.stack());
            if (entry.previousLevel() > 0) {
                enchantments.put(entry.enchantment(), entry.previousLevel());
            } else {
                enchantments.remove(entry.enchantment());
            }
            EnchantmentHelper.setEnchantments(enchantments, entry.stack());
        }
    }
    public static void clearAll() {
        ACTIVE.clear();
    }
}
