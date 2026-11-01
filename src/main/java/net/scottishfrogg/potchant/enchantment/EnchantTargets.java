package net.scottishfrogg.potchant.enchantment;

import java.util.function.Predicate;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;

public final class EnchantTargets {
    private EnchantTargets() {
    }
    public static final Predicate<ItemStack> ANY_TOOL_OR_ARMOR = stack -> {
        var item = stack.getItem();
        return item instanceof ArmorItem || item instanceof TieredItem || item instanceof ShearsItem
                || item instanceof FlintAndSteelItem || item instanceof FishingRodItem;
    };
    public static final Predicate<ItemStack> ANY_ARMOR = stack -> stack.getItem() instanceof ArmorItem;
    public static final Predicate<ItemStack> SWORD_OR_AXE =
            stack -> stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem;
    public static final Predicate<ItemStack> SWORD_AXE_OR_BOW =
            stack -> SWORD_OR_AXE.test(stack) || stack.getItem() instanceof BowItem;
    public static Predicate<ItemStack> armorIn(EquipmentSlot slot) {
        return stack -> stack.getItem() instanceof ArmorItem armor && armor.getEquipmentSlot() == slot;
    }
    public static final Predicate<ItemStack> HELMET = armorIn(EquipmentSlot.HEAD);
    public static final Predicate<ItemStack> CHESTPLATE = armorIn(EquipmentSlot.CHEST);
    public static final Predicate<ItemStack> LEGGINGS = armorIn(EquipmentSlot.LEGS);
    public static final Predicate<ItemStack> BOOTS = armorIn(EquipmentSlot.FEET);
}
