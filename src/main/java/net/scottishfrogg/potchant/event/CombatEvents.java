package net.scottishfrogg.potchant.event;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.scottishfrogg.potchant.PotchantMod;
import net.scottishfrogg.potchant.registry.ModEffects;
import net.scottishfrogg.potchant.util.EffectUtil;
import net.scottishfrogg.potchant.util.TempEnchants;

@Mod.EventBusSubscriber(modid = PotchantMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CombatEvents {
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        LivingEntity target = event.getEntity();
        if (source.getDirectEntity() instanceof ThrownTrident trident
                && trident.getOwner() instanceof LivingEntity thrower) {
            int impaling = EffectUtil.level(thrower, ModEffects.IMPALING);
            if (impaling > 0 && target.getMobType() == MobType.WATER) {
                event.setAmount(event.getAmount() + 2.5F * impaling);
            }
            return;
        }
        if (!(source.getEntity() instanceof LivingEntity attacker) || source.getDirectEntity() != attacker) {
            return;
        }
        float bonus = 0.0F;
        int sharpness = EffectUtil.level(attacker, ModEffects.SHARPNESS);
        if (sharpness > 0) {
            bonus += 1.0F + (sharpness - 1) * 0.5F;
        }
        int smite = EffectUtil.level(attacker, ModEffects.SMITE);
        if (smite > 0 && target.getMobType() == MobType.UNDEAD) {
            bonus += 2.5F * smite;
        }
        int impaling = EffectUtil.level(attacker, ModEffects.IMPALING);
        if (impaling > 0 && target.getMobType() == MobType.WATER) {
            bonus += 2.5F * impaling;
        }
        if (bonus > 0.0F) {
            event.setAmount(event.getAmount() + bonus);
        }
        int fireAspect = EffectUtil.level(attacker, ModEffects.FIRE_ASPECT);
        if (fireAspect > 0) {
            target.setSecondsOnFire(fireAspect * 4);
        }
        int knockback = EffectUtil.level(attacker, ModEffects.KNOCKBACK);
        if (knockback > 0) {
            float yaw = attacker.getYRot() * ((float) Math.PI / 180.0F);
            target.knockback(knockback * 0.5F, Mth.sin(yaw), -Mth.cos(yaw));
        }
    }
    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }
        int level = EffectUtil.level(player, ModEffects.SWEEPING_EDGE);
        ItemStack weapon = player.getMainHandItem();
        if (level > 0 && weapon.getItem() instanceof SwordItem) {
            TempEnchants.apply(player, weapon, Enchantments.SWEEPING_EDGE, level);
        }
    }
    @SubscribeEvent
    public static void onThorns(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        DamageSource source = event.getSource();
        if (victim.level().isClientSide || source.is(DamageTypes.THORNS)
                || !(source.getEntity() instanceof LivingEntity attacker) || attacker == victim) {
            return;
        }
        int level = EffectUtil.level(victim, ModEffects.THORNS);
        if (level <= 0) {
            return;
        }
        RandomSource random = victim.getRandom();
        if (random.nextFloat() >= 0.15F * level) {
            return;
        }
        attacker.hurt(victim.level().damageSources().thorns(victim), 1 + random.nextInt(4));
        damageRandomArmor(victim, 2);
    }
    private static void damageRandomArmor(LivingEntity wearer, int amount) {
        EquipmentSlot[] armorSlots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        List<EquipmentSlot> worn = new ArrayList<>();
        for (EquipmentSlot slot : armorSlots) {
            if (wearer.getItemBySlot(slot).isDamageableItem()) {
                worn.add(slot);
            }
        }
        if (worn.isEmpty()) {
            return;
        }
        EquipmentSlot chosen = worn.get(wearer.getRandom().nextInt(worn.size()));
        wearer.getItemBySlot(chosen).hurtAndBreak(amount, wearer, e -> e.broadcastBreakEvent(chosen));
    }
    @SubscribeEvent
    public static void onLooting(LootingLevelEvent event) {
        DamageSource source = event.getDamageSource();
        if (source != null && source.getEntity() instanceof LivingEntity killer) {
            int level = EffectUtil.level(killer, ModEffects.LOOTING);
            if (level > 0) {
                event.setLootingLevel(event.getLootingLevel() + level);
            }
        }
    }
}
