package net.scottishfrogg.potchant.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.scottishfrogg.potchant.PotchantMod;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(Registries.POTION, PotchantMod.MOD_ID);
    public static final int DURATION = 3600;
    public record Family(String name, List<RegistryObject<Potion>> levels, Supplier<Item> ingredient) {
    }
    public static final List<Family> FAMILIES = new ArrayList<>();
    private static void add(String name, RegistryObject<MobEffect> effect, int maxLevel, Supplier<Item> ingredient) {
        List<RegistryObject<Potion>> levels = new ArrayList<>();
        for (int level = 1; level <= maxLevel; level++) {
            final int amplifier = level - 1;
            String id = maxLevel == 1 ? name : name + "_" + level;
            levels.add(POTIONS.register(id,
                    () -> new Potion(new MobEffectInstance(effect.get(), DURATION, amplifier))));
        }
        FAMILIES.add(new Family(name, levels, ingredient));
    }
    static {
        add("aqua_affinity", ModEffects.AQUA_AFFINITY, 1, () -> Items.PRISMARINE_CRYSTALS);
        add("bane_of_arthropods", ModEffects.BANE_OF_ARTHROPODS, 5, () -> Items.FERMENTED_SPIDER_EYE);
        add("blast_protection", ModEffects.BLAST_PROTECTION, 4, () -> Items.TNT);
        add("channeling", ModEffects.CHANNELING, 1, () -> Items.LIGHTNING_ROD);
        add("curse_of_binding", ModEffects.CURSE_OF_BINDING, 1, () -> Items.CHAIN);
        add("curse_of_vanishing", ModEffects.CURSE_OF_VANISHING, 1, () -> Items.ENDER_PEARL);
        add("depth_strider", ModEffects.DEPTH_STRIDER, 3, () -> Items.NAUTILUS_SHELL);
        add("efficiency", ModEffects.EFFICIENCY, 5, () -> Items.REDSTONE);
        add("feather_falling", ModEffects.FEATHER_FALLING, 4, () -> Items.FEATHER);
        add("fire_aspect", ModEffects.FIRE_ASPECT, 2, () -> Items.BLAZE_ROD);
        add("fire_protection", ModEffects.FIRE_PROTECTION, 4, () -> Items.MAGMA_BLOCK);
        add("flame", ModEffects.FLAME, 1, () -> Items.FIRE_CHARGE);
        add("fortune", ModEffects.FORTUNE, 3, () -> Items.EMERALD);
        add("frost_walker", ModEffects.FROST_WALKER, 2, () -> Items.BLUE_ICE);
        add("impaling", ModEffects.IMPALING, 5, () -> Items.PRISMARINE_SHARD);
        add("infinity", ModEffects.INFINITY, 1, () -> Items.ENDER_EYE);
        add("knockback", ModEffects.KNOCKBACK, 2, () -> Items.PISTON);
        add("looting", ModEffects.LOOTING, 3, () -> Items.GOLD_INGOT);
        add("loyalty", ModEffects.LOYALTY, 3, () -> Items.LEAD);
        add("luck_of_the_sea", ModEffects.LUCK_OF_THE_SEA, 3, () -> Items.SALMON);
        add("lure", ModEffects.LURE, 3, () -> Items.TROPICAL_FISH);
        add("mending", ModEffects.MENDING, 1, () -> Items.EXPERIENCE_BOTTLE);
        add("multishot", ModEffects.MULTISHOT, 1, () -> Items.ARROW);
        add("piercing", ModEffects.PIERCING, 4, () -> Items.SPECTRAL_ARROW);
        add("power", ModEffects.POWER, 5, () -> Items.FLINT);
        add("projectile_protection", ModEffects.PROJECTILE_PROTECTION, 4, () -> Items.SHIELD);
        add("protection", ModEffects.PROTECTION, 4, () -> Items.IRON_INGOT);
        add("punch", ModEffects.PUNCH, 2, () -> Items.SLIME_BALL);
        add("quick_charge", ModEffects.QUICK_CHARGE, 3, () -> Items.TRIPWIRE_HOOK);
        add("respiration", ModEffects.RESPIRATION, 3, () -> Items.KELP);
        add("riptide", ModEffects.RIPTIDE, 3, () -> Items.HEART_OF_THE_SEA);
        add("sharpness", ModEffects.SHARPNESS, 5, () -> Items.QUARTZ);
        add("silk_touch", ModEffects.SILK_TOUCH, 1, () -> Items.COBWEB);
        add("smite", ModEffects.SMITE, 5, () -> Items.ROTTEN_FLESH);
        add("soul_speed", ModEffects.SOUL_SPEED, 3, () -> Items.SOUL_SAND);
        add("sweeping_edge", ModEffects.SWEEPING_EDGE, 3, () -> Items.IRON_SWORD);
        add("swift_sneak", ModEffects.SWIFT_SNEAK, 3, () -> Items.SCULK);
        add("thorns", ModEffects.THORNS, 3, () -> Items.CACTUS);
        add("unbreaking", ModEffects.UNBREAKING, 3, () -> Items.OBSIDIAN);
    }
}
