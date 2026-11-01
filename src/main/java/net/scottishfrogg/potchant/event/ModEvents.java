package net.scottishfrogg.potchant.event;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.scottishfrogg.potchant.PotchantMod;
import net.scottishfrogg.potchant.registry.ModEffects;

@Mod.EventBusSubscriber(modid = PotchantMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {
    public static final float VANILLA_UNDERWATER_PENALTY = 5.0F;
    public static final float BANE_DAMAGE_PER_LEVEL = 2.5F;
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player.hasEffect(ModEffects.AQUA_AFFINITY.get())
                && player.isEyeInFluid(FluidTags.WATER)
                && !EnchantmentHelper.hasAquaAffinity(player)) {
            event.setNewSpeed(event.getNewSpeed() * VANILLA_UNDERWATER_PENALTY);
        }
        MobEffectInstance efficiency = player.getEffect(ModEffects.EFFICIENCY.get());
        if (efficiency != null) {
            float toolSpeed = player.getMainHandItem().getDestroySpeed(event.getState());
            if (toolSpeed > 1.0F) {
                int level = efficiency.getAmplifier() + 1;
                event.setNewSpeed(event.getNewSpeed() * (toolSpeed + level * level + 1) / toolSpeed);
            }
        }
    }
    private static final float[] DEPTH_STRIDER_SPEED_MULTIPLIER = {1.03F, 1.05F, 1.07F};
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        MobEffectInstance depth = player.getEffect(ModEffects.DEPTH_STRIDER.get());
        if (depth == null || !player.isInWater() || (player.xxa == 0.0F && player.zza == 0.0F)) {
            return;
        }
        int index = Math.min(depth.getAmplifier(), DEPTH_STRIDER_SPEED_MULTIPLIER.length - 1);
        float multiplier = DEPTH_STRIDER_SPEED_MULTIPLIER[index];
        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x * multiplier, motion.y, motion.z * multiplier);
    }
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)
                || event.getSource().getDirectEntity() != attacker) {
            return;
        }
        MobEffectInstance bane = attacker.getEffect(ModEffects.BANE_OF_ARTHROPODS.get());
        LivingEntity target = event.getEntity();
        if (bane == null || target.getMobType() != MobType.ARTHROPOD) {
            return;
        }
        int level = bane.getAmplifier() + 1;
        event.setAmount(event.getAmount() + BANE_DAMAGE_PER_LEVEL * level);
        int duration = 20 + target.getRandom().nextInt(10 * level);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 3));
    }
    public static final float BLAST_DAMAGE_REDUCTION_PER_LEVEL = 0.08F;
    public static final float BLAST_KNOCKBACK_REDUCTION_PER_LEVEL = 0.15F;
    private static final Map<LivingEntity, Vec3> PRE_BLAST_MOTION = new HashMap<>();
    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (event.getLevel().isClientSide) {
            return;
        }
        for (Entity entity : event.getAffectedEntities()) {
            if (entity instanceof LivingEntity living && living.hasEffect(ModEffects.BLAST_PROTECTION.get())) {
                PRE_BLAST_MOTION.put(living, living.getDeltaMovement());
            }
        }
    }
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || PRE_BLAST_MOTION.isEmpty()) {
            return;
        }
        PRE_BLAST_MOTION.forEach((living, before) -> {
            MobEffectInstance blast = living.getEffect(ModEffects.BLAST_PROTECTION.get());
            if (blast == null || !living.isAlive()) {
                return;
            }
            double keep = 1.0 - Math.min(1.0, BLAST_KNOCKBACK_REDUCTION_PER_LEVEL * (blast.getAmplifier() + 1));
            Vec3 push = living.getDeltaMovement().subtract(before);
            living.setDeltaMovement(before.add(push.scale(keep)));
            if (living instanceof ServerPlayer player) {
                player.connection.send(new ClientboundSetEntityMotionPacket(player));
            }
        });
        PRE_BLAST_MOTION.clear();
    }
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        Level level = projectile.level();
        if (level.isClientSide || !level.isThundering()) {
            return;
        }
        if (!(event.getRayTraceResult() instanceof EntityHitResult hit)) {
            return;
        }
        if (!(projectile.getOwner() instanceof LivingEntity owner)
                || !owner.hasEffect(ModEffects.CHANNELING.get())) {
            return;
        }
        Entity target = hit.getEntity();
        if (!(target instanceof LivingEntity) || target == owner) {
            return;
        }
        BlockPos pos = target.blockPosition();
        if (!level.canSeeSky(pos)) {
            return;
        }
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt == null) {
            return;
        }
        bolt.moveTo(Vec3.atBottomCenterOf(pos));
        if (owner instanceof ServerPlayer player) {
            bolt.setCause(player);
        }
        level.addFreshEntity(bolt);
    }
}
