package net.scottishfrogg.potchant.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.RegistryObject;

public final class EffectUtil {
    private EffectUtil() {
    }
    public static int level(LivingEntity entity, RegistryObject<MobEffect> effect) {
        MobEffectInstance instance = entity.getEffect(effect.get());
        return instance == null ? 0 : instance.getAmplifier() + 1;
    }
}
