package net.scottishfrogg.potchant.event;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.scottishfrogg.potchant.PotchantMod;
import net.scottishfrogg.potchant.registry.ModEffects;
import net.scottishfrogg.potchant.util.EffectUtil;

@Mod.EventBusSubscriber(modid = PotchantMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProtectionEvents {
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || source.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
            return;
        }
        int points = EffectUtil.level(entity, ModEffects.PROTECTION);
        if (source.is(DamageTypeTags.IS_FIRE)) {
            points += 2 * EffectUtil.level(entity, ModEffects.FIRE_PROTECTION);
        }
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            points += 2 * EffectUtil.level(entity, ModEffects.BLAST_PROTECTION);
        }
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            points += 2 * EffectUtil.level(entity, ModEffects.PROJECTILE_PROTECTION);
        }
        if (source.is(DamageTypeTags.IS_FALL)) {
            points += 3 * EffectUtil.level(entity, ModEffects.FEATHER_FALLING);
        }
        if (points <= 0) {
            return;
        }
        points = Math.min(points, 20);
        event.setAmount(event.getAmount() * (1.0F - points * 0.04F));
    }
    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || entity.getRemainingFireTicks() <= 0) {
            return;
        }
        int level = EffectUtil.level(entity, ModEffects.FIRE_PROTECTION);
        if (level <= 0) {
            return;
        }
        double shorter = Math.min(0.6D, 0.15D * level);
        double extraTicksPerTick = shorter / (1.0D - shorter);
        int extra = (int) extraTicksPerTick;
        if (entity.getRandom().nextDouble() < extraTicksPerTick - extra) {
            extra++;
        }
        if (extra > 0) {
            entity.setRemainingFireTicks(Math.max(0, entity.getRemainingFireTicks() - extra));
        }
    }
}
