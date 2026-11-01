package net.scottishfrogg.potchant.enchantment;

import java.util.function.Predicate;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.scottishfrogg.potchant.util.EnchantAlignment;

public class PotchantEnchantment extends Enchantment {
    private final int maxLevel;
    private final Predicate<ItemStack> target;
    private final EnchantAlignment alignment;

    public PotchantEnchantment(Rarity rarity, int maxLevel, EnchantAlignment alignment,
                               Predicate<ItemStack> target, EquipmentSlot... slots) {
        super(rarity, EnchantmentCategory.BREAKABLE, slots);
        this.maxLevel = maxLevel;
        this.alignment = alignment;
        this.target = target;
    }
    public EnchantAlignment alignment() {
        return alignment;
    }
    @Override
    public int getMaxLevel() {
        return maxLevel;
    }
    @Override
    public boolean canEnchant(ItemStack stack) {
        return target.test(stack);
    }
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return target.test(stack);
    }
    @Override
    public boolean isCurse() {
        return alignment == EnchantAlignment.BAD;
    }
    @Override
    public boolean isDiscoverable() {
        return true;
    }
    @Override
    public boolean isTradeable() {
        return true;
    }
    @Override
    public boolean isAllowedOnBooks() {
        return true;
    }
}
