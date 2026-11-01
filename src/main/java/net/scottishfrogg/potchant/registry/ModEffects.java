package net.scottishfrogg.potchant.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.scottishfrogg.potchant.PotchantMod;
import net.scottishfrogg.potchant.effect.PotchantEffect;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, PotchantMod.MOD_ID);
    private static RegistryObject<MobEffect> good(String name, int color) {
        return EFFECTS.register(name, () -> new PotchantEffect(MobEffectCategory.BENEFICIAL, color));
    }
    private static RegistryObject<MobEffect> bad(String name, int color) {
        return EFFECTS.register(name, () -> new PotchantEffect(MobEffectCategory.HARMFUL, color));
    }
    public static final RegistryObject<MobEffect> AQUA_AFFINITY = good("aqua_affinity", 0x2E6FD8);
    public static final RegistryObject<MobEffect> BANE_OF_ARTHROPODS = good("bane_of_arthropods", 0x5E7A2A);
    public static final RegistryObject<MobEffect> BLAST_PROTECTION = good("blast_protection", 0xB04A2A);
    public static final RegistryObject<MobEffect> CHANNELING = good("channeling", 0xE8D44D);
    public static final RegistryObject<MobEffect> CURSE_OF_BINDING = bad("curse_of_binding", 0x3B2A4A);
    public static final RegistryObject<MobEffect> CURSE_OF_VANISHING = bad("curse_of_vanishing", 0x7A6BA8);
    public static final RegistryObject<MobEffect> DEPTH_STRIDER = good("depth_strider", 0x3FA7C8);
    public static final RegistryObject<MobEffect> EFFICIENCY = good("efficiency", 0xD98B2B);
    public static final RegistryObject<MobEffect> FEATHER_FALLING = good("feather_falling", 0xE8E8F0);
    public static final RegistryObject<MobEffect> FIRE_ASPECT = good("fire_aspect", 0xE8591A);
    public static final RegistryObject<MobEffect> FIRE_PROTECTION = good("fire_protection", 0xC93A12);
    public static final RegistryObject<MobEffect> FLAME = good("flame", 0xFF8C00);
    public static final RegistryObject<MobEffect> FORTUNE = good("fortune", 0x2ECC71);
    public static final RegistryObject<MobEffect> FROST_WALKER = good("frost_walker", 0x9FE0F5);
    public static final RegistryObject<MobEffect> IMPALING = good("impaling", 0x1B9AAA);
    public static final RegistryObject<MobEffect> INFINITY = good("infinity", 0x9B59B6);
    public static final RegistryObject<MobEffect> KNOCKBACK = good("knockback", 0x8D6E63);
    public static final RegistryObject<MobEffect> LOOTING = good("looting", 0xF1C40F);
    public static final RegistryObject<MobEffect> LOYALTY = good("loyalty", 0x4A6FA5);
    public static final RegistryObject<MobEffect> LUCK_OF_THE_SEA = good("luck_of_the_sea", 0x1ABC9C);
    public static final RegistryObject<MobEffect> LURE = good("lure", 0x3498DB);
    public static final RegistryObject<MobEffect> MENDING = good("mending", 0x7CFC00);
    public static final RegistryObject<MobEffect> MULTISHOT = good("multishot", 0xE67E22);
    public static final RegistryObject<MobEffect> PIERCING = good("piercing", 0xB0B0B0);
    public static final RegistryObject<MobEffect> POWER = good("power", 0xD35400);
    public static final RegistryObject<MobEffect> PROJECTILE_PROTECTION = good("projectile_protection", 0x5D6D7E);
    public static final RegistryObject<MobEffect> PROTECTION = good("protection", 0x7F8C8D);
    public static final RegistryObject<MobEffect> PUNCH = good("punch", 0xA04000);
    public static final RegistryObject<MobEffect> QUICK_CHARGE = good("quick_charge", 0xF39C12);
    public static final RegistryObject<MobEffect> RESPIRATION = good("respiration", 0x16A085);
    public static final RegistryObject<MobEffect> RIPTIDE = good("riptide", 0x2980B9);
    public static final RegistryObject<MobEffect> SHARPNESS = good("sharpness", 0xBDC3C7);
    public static final RegistryObject<MobEffect> SILK_TOUCH = good("silk_touch", 0xECF0F1);
    public static final RegistryObject<MobEffect> SMITE = good("smite", 0xF5E6A8);
    public static final RegistryObject<MobEffect> SOUL_SPEED = good("soul_speed", 0x5B4636);
    public static final RegistryObject<MobEffect> SWEEPING_EDGE = good("sweeping_edge", 0xD0D3D4);
    public static final RegistryObject<MobEffect> SWIFT_SNEAK = good("swift_sneak", 0x0E4D4D);
    public static final RegistryObject<MobEffect> THORNS = good("thorns", 0x27632A);
    public static final RegistryObject<MobEffect> UNBREAKING = good("unbreaking", 0x34495E);
}
