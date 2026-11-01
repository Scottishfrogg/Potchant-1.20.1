package net.scottishfrogg.potchant.util;

import net.minecraft.world.item.enchantment.Enchantment;
import net.scottishfrogg.potchant.enchantment.PotchantEnchantment;

public enum EnchantAlignment {
    GOOD, NEUTRAL, BAD;
    public static EnchantAlignment of(Enchantment enchantment) {
        if (enchantment instanceof PotchantEnchantment potchant) {
            return potchant.alignment();
        }
        return enchantment.isCurse() ? BAD : GOOD;
    }
}
