package net.scottishfrogg.potchant.event;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.scottishfrogg.potchant.PotchantMod;
import net.scottishfrogg.potchant.registry.ModEffects;
import net.scottishfrogg.potchant.util.EffectUtil;

@Mod.EventBusSubscriber(modid = PotchantMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProjectileEvents {
    private static final String MULTISHOT_COPY_TAG = "PotchantMultishotCopy";
    private static final float MULTISHOT_ANGLE = 10.0F * ((float) Math.PI / 180.0F);
    private static final Map<AbstractArrow, Integer> RETURNING = new HashMap<>();
    private static final Map<UUID, ItemStack[]> SNAPSHOTS = new HashMap<>();
    @SubscribeEvent
    public static void onProjectileJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || event.loadedFromDisk()) {
            return;
        }
        if (!(event.getEntity() instanceof Projectile projectile)) {
            return;
        }
        if (projectile.getPersistentData().getBoolean(MULTISHOT_COPY_TAG)) {
            return;
        }
        if (!(projectile.getOwner() instanceof LivingEntity owner)) {
            return;
        }
        if (projectile instanceof AbstractArrow arrow) {
            if (!(arrow instanceof ThrownTrident)) {
                int power = EffectUtil.level(owner, ModEffects.POWER);
                if (power > 0) {
                    arrow.setBaseDamage(arrow.getBaseDamage() + power * 0.5D + 0.5D);
                }
                int punch = EffectUtil.level(owner, ModEffects.PUNCH);
                if (punch > 0) {
                    arrow.setKnockback(punch);
                }
                int piercing = EffectUtil.level(owner, ModEffects.PIERCING);
                if (piercing > 0) {
                    arrow.setPierceLevel((byte) Math.max(arrow.getPierceLevel(), piercing));
                }
            }
            if (EffectUtil.level(owner, ModEffects.FLAME) > 0) {
                arrow.setSecondsOnFire(100);
            }
            if (EffectUtil.level(owner, ModEffects.INFINITY) > 0) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
        }
        if (EffectUtil.level(owner, ModEffects.MULTISHOT) > 0
                && (projectile instanceof AbstractArrow || projectile instanceof ThrowableProjectile)) {
            spawnMultishotCopy(projectile, MULTISHOT_ANGLE);
            spawnMultishotCopy(projectile, -MULTISHOT_ANGLE);
        }
    }
    private static void spawnMultishotCopy(Projectile original, float angle) {
        Entity copy = original.getType().create(original.level());
        if (copy == null) {
            return;
        }
        CompoundTag data = original.saveWithoutId(new CompoundTag());
        copy.load(data);
        copy.setUUID(UUID.randomUUID());
        copy.getPersistentData().putBoolean(MULTISHOT_COPY_TAG, true);
        copy.setDeltaMovement(original.getDeltaMovement().yRot(angle));
        if (copy instanceof AbstractArrow arrowCopy) {
            arrowCopy.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }
        original.level().addFreshEntity(copy);
    }
    @SubscribeEvent
    public static void onImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (projectile.level().isClientSide || !(projectile.getOwner() instanceof LivingEntity owner)) {
            return;
        }
        HitResult hit = event.getRayTraceResult();
        if (hit instanceof EntityHitResult entityHit
                && entityHit.getEntity() instanceof LivingEntity target
                && target != owner
                && EffectUtil.level(owner, ModEffects.FLAME) > 0) {
            target.setSecondsOnFire(5);
        }
        int loyalty = EffectUtil.level(owner, ModEffects.LOYALTY);
        if (loyalty > 0 && owner instanceof ServerPlayer player && projectile instanceof AbstractArrow arrow) {
            if (hit instanceof EntityHitResult && !(arrow instanceof ThrownTrident)) {
                arrow.setNoPhysics(true);
                arrow.playerTouch(player);
            } else {
                RETURNING.put(arrow, loyalty);
            }
        }
    }
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || RETURNING.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<AbstractArrow, Integer>> iterator = RETURNING.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<AbstractArrow, Integer> entry = iterator.next();
            AbstractArrow arrow = entry.getKey();
            int level = entry.getValue();
            if (arrow.isRemoved()) {
                iterator.remove();
                continue;
            }
            if (!(arrow.getOwner() instanceof ServerPlayer owner) || !owner.isAlive() || owner.level() != arrow.level()) {
                arrow.setNoPhysics(false);
                iterator.remove();
                continue;
            }
            arrow.setNoPhysics(true);
            Vec3 toOwner = owner.getEyePosition().subtract(arrow.position());
            arrow.setPosRaw(arrow.getX(), arrow.getY() + toOwner.y * 0.015D * level, arrow.getZ());
            arrow.setDeltaMovement(arrow.getDeltaMovement().scale(0.95D).add(toOwner.normalize().scale(0.05D * level)));
            if (arrow.distanceToSqr(owner) < 2.5D) {
                arrow.playerTouch(owner);
                if (arrow.isRemoved()) {
                    iterator.remove();
                } else if (arrow.pickup != AbstractArrow.Pickup.ALLOWED) {
                    arrow.discard();
                    iterator.remove();
                }
            }
        }
    }
    @SubscribeEvent
    public static void onUseStart(LivingEntityUseItemEvent.Start event) {
        if (!(event.getItem().getItem() instanceof CrossbowItem)) {
            return;
        }
        int level = EffectUtil.level(event.getEntity(), ModEffects.QUICK_CHARGE);
        if (level > 0) {
            event.setDuration(event.getDuration() - 5 * level);
        }
    }
    @SubscribeEvent
    public static void onBowRelease(ArrowLooseEvent event) {
        int level = EffectUtil.level(event.getEntity(), ModEffects.QUICK_CHARGE);
        if (level > 0) {
            event.setCharge((int) (event.getCharge() * (25.0F / (25.0F - 5.0F * level))));
        }
    }
    @SubscribeEvent
    public static void onUseStop(LivingEntityUseItemEvent.Stop event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        ItemStack stack = event.getItem();
        int riptide = EffectUtil.level(player, ModEffects.RIPTIDE);
        if (riptide > 0 && stack.getItem() instanceof TridentItem) {
            riptide(player, stack, riptide, event);
            return;
        }
        if (!player.level().isClientSide && EffectUtil.level(player, ModEffects.INFINITY) > 0
                && (stack.getItem() instanceof ProjectileWeaponItem || stack.getItem() instanceof TridentItem)) {
            snapshot(player);
        }
    }
    private static void riptide(Player player, ItemStack stack, int level, LivingEntityUseItemEvent.Stop event) {
        int used = stack.getUseDuration() - event.getDuration();
        if (used < 10) {
            return;
        }
        event.setCanceled(true);
        if (!player.isInWaterOrRain()) {
            return;
        }
        Level world = player.level();
        if (!world.isClientSide) {
            InteractionHand hand = player.getUsedItemHand();
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        }
        float yaw = player.getYRot() * ((float) Math.PI / 180.0F);
        float pitch = player.getXRot() * ((float) Math.PI / 180.0F);
        float x = -Mth.sin(yaw) * Mth.cos(pitch);
        float y = -Mth.sin(pitch);
        float z = Mth.cos(yaw) * Mth.cos(pitch);
        float length = Mth.sqrt(x * x + y * y + z * z);
        float power = 3.0F * ((1.0F + level) / 4.0F);
        player.push(x * power / length, y * power / length, z * power / length);
        player.startAutoSpinAttack(20);
        if (player.onGround()) {
            player.move(MoverType.SELF, new Vec3(0.0D, 1.1999999F, 0.0D));
        }
        if (!world.isClientSide) {
            SoundEvent sound = level >= 3 ? SoundEvents.TRIDENT_RIPTIDE_3
                    : level == 2 ? SoundEvents.TRIDENT_RIPTIDE_2 : SoundEvents.TRIDENT_RIPTIDE_1;
            world.playSound(null, player, sound, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || EffectUtil.level(player, ModEffects.INFINITY) <= 0) {
            return;
        }
        if (isProjectileItem(event.getItemStack().getItem())) {
            snapshot(player);
        }
    }
    private static boolean isProjectileItem(Item item) {
        return item instanceof ArrowItem || item instanceof SnowballItem || item instanceof EggItem
                || item instanceof EnderpearlItem || item instanceof ThrowablePotionItem
                || item instanceof ExperienceBottleItem || item instanceof TridentItem;
    }
    private static void snapshot(Player player) {
        Inventory inventory = player.getInventory();
        ItemStack[] copy = new ItemStack[inventory.items.size() + 1];
        for (int i = 0; i < inventory.items.size(); i++) {
            copy[i] = inventory.items.get(i).copy();
        }
        copy[inventory.items.size()] = inventory.offhand.get(0).copy();
        SNAPSHOTS.putIfAbsent(player.getUUID(), copy);
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        ItemStack[] snapshot = SNAPSHOTS.remove(event.player.getUUID());
        if (snapshot == null) {
            return;
        }
        Inventory inventory = event.player.getInventory();
        int mainSize = inventory.items.size();
        for (int i = 0; i < snapshot.length; i++) {
            ItemStack before = snapshot[i];
            if (before.isEmpty() || !isProjectileItem(before.getItem())) {
                continue;
            }
            ItemStack now = i < mainSize ? inventory.items.get(i) : inventory.offhand.get(0);
            if (now.isEmpty()) {
                if (i < mainSize) {
                    inventory.items.set(i, before.copy());
                } else {
                    inventory.offhand.set(0, before.copy());
                }
            } else if (ItemStack.isSameItemSameTags(now, before) && now.getCount() < before.getCount()) {
                now.setCount(before.getCount());
            }
        }
    }
    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        SNAPSHOTS.remove(event.getEntity().getUUID());
    }
    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        RETURNING.clear();
        SNAPSHOTS.clear();
    }
}
