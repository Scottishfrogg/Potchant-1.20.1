package net.scottishfrogg.potchant.registry;

import java.util.function.Predicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.scottishfrogg.potchant.PotchantMod;
import net.scottishfrogg.potchant.enchantment.EnchantTargets;
import net.scottishfrogg.potchant.enchantment.PotchantEnchantment;
import net.scottishfrogg.potchant.util.EnchantAlignment;


public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(Registries.ENCHANTMENT, PotchantMod.MOD_ID);
    private static final EquipmentSlot[] ARMOR =
            {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private static final EquipmentSlot[] HANDS = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};
    private static final Predicate<ItemStack> DIGGER = stack -> stack.getItem() instanceof DiggerItem;
    private static RegistryObject<Enchantment> reg(String name, Rarity rarity, int maxLevel, EnchantAlignment alignment,
                                                   Predicate<ItemStack> target, EquipmentSlot... slots) {
        return ENCHANTMENTS.register(name, () -> new PotchantEnchantment(rarity, maxLevel, alignment, target, slots));
    }
    public static final RegistryObject<Enchantment> AWKWARD =
            reg("awkward", Rarity.COMMON, 1, EnchantAlignment.NEUTRAL, EnchantTargets.ANY_TOOL_OR_ARMOR, ARMOR);
    public static final RegistryObject<Enchantment> MUNDANE =
            reg("mundane", Rarity.COMMON, 1, EnchantAlignment.NEUTRAL, EnchantTargets.ANY_TOOL_OR_ARMOR, ARMOR);
    public static final RegistryObject<Enchantment> THICK =
            reg("thick", Rarity.COMMON, 1, EnchantAlignment.NEUTRAL, EnchantTargets.ANY_TOOL_OR_ARMOR, ARMOR);
    public static final RegistryObject<Enchantment> REGENERATION =
            reg("regeneration", Rarity.UNCOMMON, 2, EnchantAlignment.GOOD, EnchantTargets.CHESTPLATE, EquipmentSlot.CHEST);
    public static final RegistryObject<Enchantment> FIRE_RESISTANCE =
            reg("fire_resistance", Rarity.RARE, 1, EnchantAlignment.GOOD, EnchantTargets.CHESTPLATE, EquipmentSlot.CHEST);
    public static final RegistryObject<Enchantment> HEALING =
            reg("healing", Rarity.RARE, 2, EnchantAlignment.GOOD, EnchantTargets.CHESTPLATE, EquipmentSlot.CHEST);
    public static final RegistryObject<Enchantment> INVISIBILITY =
            reg("invisibility", Rarity.RARE, 1, EnchantAlignment.GOOD, EnchantTargets.CHESTPLATE, EquipmentSlot.CHEST);
    public static final RegistryObject<Enchantment> TURTLE_MASTER =
            reg("turtle_master", Rarity.VERY_RARE, 2, EnchantAlignment.GOOD, EnchantTargets.CHESTPLATE, EquipmentSlot.CHEST);
    public static final RegistryObject<Enchantment> SWIFTNESS =
            reg("swiftness", Rarity.UNCOMMON, 2, EnchantAlignment.GOOD, EnchantTargets.BOOTS, EquipmentSlot.FEET);
    public static final RegistryObject<Enchantment> SLOW_FALLING =
            reg("slow_falling", Rarity.UNCOMMON, 1, EnchantAlignment.GOOD, EnchantTargets.BOOTS, EquipmentSlot.FEET);
    public static final RegistryObject<Enchantment> SLOWNESS =
            reg("slowness", Rarity.UNCOMMON, 4, EnchantAlignment.BAD, EnchantTargets.BOOTS, EquipmentSlot.FEET);
    public static final RegistryObject<Enchantment> NIGHT_VISION =
            reg("night_vision", Rarity.UNCOMMON, 1, EnchantAlignment.GOOD, EnchantTargets.HELMET, EquipmentSlot.HEAD);
    public static final RegistryObject<Enchantment> WATER_BREATHING =
            reg("water_breathing", Rarity.UNCOMMON, 1, EnchantAlignment.GOOD, EnchantTargets.HELMET, EquipmentSlot.HEAD);
    public static final RegistryObject<Enchantment> LEAPING =
            reg("leaping", Rarity.UNCOMMON, 2, EnchantAlignment.GOOD, EnchantTargets.LEGGINGS, EquipmentSlot.LEGS);
    public static final RegistryObject<Enchantment> STRENGTH =
            reg("strength", Rarity.RARE, 2, EnchantAlignment.GOOD, EnchantTargets.SWORD_OR_AXE, HANDS);
    public static final RegistryObject<Enchantment> POISON =
            reg("poison", Rarity.UNCOMMON, 2, EnchantAlignment.GOOD, EnchantTargets.SWORD_AXE_OR_BOW, HANDS);
    public static final RegistryObject<Enchantment> WEAKNESS =
            reg("weakness", Rarity.UNCOMMON, 1, EnchantAlignment.GOOD, EnchantTargets.SWORD_AXE_OR_BOW, HANDS);
    public static final RegistryObject<Enchantment> DECAY =
            reg("decay", Rarity.RARE, 1, EnchantAlignment.GOOD, EnchantTargets.SWORD_AXE_OR_BOW, HANDS);
    public static final RegistryObject<Enchantment> LUCK =
            reg("luck", Rarity.RARE, 1, EnchantAlignment.GOOD, EnchantTargets.ANY_ARMOR, ARMOR);
    public static final RegistryObject<Enchantment> HARMING =
            reg("harming", Rarity.UNCOMMON, 2, EnchantAlignment.BAD, EnchantTargets.ANY_ARMOR, ARMOR);
    public static final RegistryObject<Enchantment> HASTE =
            reg("haste", Rarity.UNCOMMON, 2, EnchantAlignment.GOOD, DIGGER, HANDS);
    public static final RegistryObject<Enchantment> MINING_FATIGUE =
            reg("mining_fatigue", Rarity.UNCOMMON, 2, EnchantAlignment.BAD, DIGGER, HANDS);
    public static final RegistryObject<Enchantment> RESISTANCE =
            reg("resistance", Rarity.RARE, 2, EnchantAlignment.GOOD, EnchantTargets.CHESTPLATE, EquipmentSlot.CHEST);
    public static final RegistryObject<Enchantment> ABSORPTION =
            reg("absorption", Rarity.RARE, 2, EnchantAlignment.GOOD, EnchantTargets.CHESTPLATE, EquipmentSlot.CHEST);
    public static final RegistryObject<Enchantment> HEALTH_BOOST =
            reg("health_boost", Rarity.RARE, 2, EnchantAlignment.GOOD, EnchantTargets.CHESTPLATE, EquipmentSlot.CHEST);
    public static final RegistryObject<Enchantment> SATURATION =
            reg("saturation", Rarity.RARE, 2, EnchantAlignment.GOOD, EnchantTargets.CHESTPLATE, EquipmentSlot.CHEST);
    public static final RegistryObject<Enchantment> BAD_LUCK =
            reg("bad_luck", Rarity.UNCOMMON, 2, EnchantAlignment.GOOD, EnchantTargets.SWORD_AXE_OR_BOW, HANDS);
    public static final RegistryObject<Enchantment> BLINDNESS =
            reg("blindness", Rarity.UNCOMMON, 1, EnchantAlignment.GOOD, EnchantTargets.SWORD_AXE_OR_BOW, HANDS);
    public static final RegistryObject<Enchantment> NAUSEA =
            reg("nausea", Rarity.UNCOMMON, 1, EnchantAlignment.GOOD, EnchantTargets.SWORD_AXE_OR_BOW, HANDS);
    public static final RegistryObject<Enchantment> HUNGER =
            reg("hunger", Rarity.UNCOMMON, 1, EnchantAlignment.GOOD, EnchantTargets.SWORD_AXE_OR_BOW, HANDS);
    public static final RegistryObject<Enchantment> WITHER =
            reg("wither", Rarity.RARE, 2, EnchantAlignment.GOOD, EnchantTargets.SWORD_AXE_OR_BOW, HANDS);
    public static final RegistryObject<Enchantment> LEVITATION =
            reg("levitation", Rarity.RARE, 1, EnchantAlignment.GOOD, EnchantTargets.SWORD_AXE_OR_BOW, HANDS);
    public static final RegistryObject<Enchantment> GLOWING =
            reg("glowing", Rarity.COMMON, 1, EnchantAlignment.GOOD, EnchantTargets.SWORD_AXE_OR_BOW, HANDS);
    public static final RegistryObject<Enchantment> DARKNESS =
            reg("darkness", Rarity.RARE, 1, EnchantAlignment.GOOD, EnchantTargets.SWORD_AXE_OR_BOW, HANDS);
    public static final RegistryObject<Enchantment> DOLPHINS_GRACE =
            reg("dolphins_grace", Rarity.UNCOMMON, 1, EnchantAlignment.GOOD, EnchantTargets.LEGGINGS, EquipmentSlot.LEGS);
    public static final RegistryObject<Enchantment> CONDUIT_POWER =
            reg("conduit_power", Rarity.RARE, 1, EnchantAlignment.GOOD, EnchantTargets.HELMET, EquipmentSlot.HEAD);
    public static final RegistryObject<Enchantment> HERO_OF_THE_VILLAGE =
            reg("hero_of_the_village", Rarity.VERY_RARE, 2, EnchantAlignment.GOOD, EnchantTargets.HELMET, EquipmentSlot.HEAD);
    public static final RegistryObject<Enchantment> BAD_OMEN =
            reg("bad_omen", Rarity.UNCOMMON, 2, EnchantAlignment.BAD, EnchantTargets.ANY_ARMOR, ARMOR);
}
