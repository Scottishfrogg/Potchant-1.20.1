package net.scottishfrogg.potchant.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.scottishfrogg.potchant.PotchantMod;
import net.scottishfrogg.potchant.registry.ModEffects;
import net.scottishfrogg.potchant.util.EffectUtil;
import net.scottishfrogg.potchant.util.TempEnchants;

@Mod.EventBusSubscriber(modid = PotchantMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class UtilityEvents {
    private static final double SOUL_SAND_SPEED_FACTOR = 0.4D;
    private static final double[] SOUL_SPEED_BOOST = {1.05D, 1.10D, 1.14D};
    private static final Map<UUID, Integer> LAST_AIR = new HashMap<>();
    private static final Map<UUID, Map<ItemStack, Integer>> LAST_DAMAGE = new HashMap<>();
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        soulSpeed(player);
        if (player.level().isClientSide) {
            return;
        }
        TempEnchants.cleanup(player);
        respiration(player);
        unbreaking(player);
    }
    private static void soulSpeed(Player player) {
        MobEffectInstance soulSpeed = player.getEffect(ModEffects.SOUL_SPEED.get());
        if (soulSpeed == null || !player.onGround()) {
            return;
        }
        BlockState below = player.getBlockStateOn();
        if (!below.is(BlockTags.SOUL_SPEED_BLOCKS)) {
            return;
        }
        double boost = SOUL_SPEED_BOOST[Math.min(soulSpeed.getAmplifier(), SOUL_SPEED_BOOST.length - 1)];
        double scale = below.is(Blocks.SOUL_SAND) ? boost / SOUL_SAND_SPEED_FACTOR : boost;
        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x * scale, motion.y, motion.z * scale);
    }
    private static void respiration(Player player) {
        UUID id = player.getUUID();
        int level = EffectUtil.level(player, ModEffects.RESPIRATION);
        if (level <= 0) {
            LAST_AIR.remove(id);
            return;
        }
        int air = player.getAirSupply();
        Integer last = LAST_AIR.get(id);
        if (last != null && air == last - 1 && player.isEyeInFluid(FluidTags.WATER)
                && player.getRandom().nextInt(level + 1) > 0) {
            player.setAirSupply(last);
            air = last;
        }
        LAST_AIR.put(id, air);
    }
    private static void unbreaking(Player player) {
        UUID id = player.getUUID();
        int level = EffectUtil.level(player, ModEffects.UNBREAKING);
        if (level <= 0) {
            LAST_DAMAGE.remove(id);
            return;
        }
        Map<ItemStack, Integer> previous = LAST_DAMAGE.get(id);
        Map<ItemStack, Integer> current = new IdentityHashMap<>();
        Inventory inventory = player.getInventory();
        for (List<ItemStack> stacks : List.of(inventory.items, inventory.armor, inventory.offhand)) {
            for (ItemStack stack : stacks) {
                if (!stack.isDamageableItem()) {
                    continue;
                }
                int damage = stack.getDamageValue();
                Integer before = previous == null ? null : previous.get(stack);
                if (before != null && damage > before) {
                    double ignoreChance = level / (double) (level + 1);
                    if (stack.getItem() instanceof ArmorItem) {
                        ignoreChance *= 0.4D;
                    }
                    int refund = 0;
                    for (int i = 0; i < damage - before; i++) {
                        if (player.getRandom().nextDouble() < ignoreChance) {
                            refund++;
                        }
                    }
                    if (refund > 0) {
                        damage -= refund;
                        stack.setDamageValue(damage);
                    }
                }
                current.put(stack, damage);
            }
        }
        LAST_DAMAGE.put(id, current);
    }
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        int level = EffectUtil.level(entity, ModEffects.FROST_WALKER);
        if (level > 0) {
            FrostWalkerEnchantment.onEntityMoved(entity, entity.level(), entity.blockPosition(), level);
        }
    }
    @SubscribeEvent
    public static void onAttacked(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.hasEffect(ModEffects.FROST_WALKER.get())) {
            return;
        }
        DamageSource source = event.getSource();
        if (source.is(DamageTypes.HOT_FLOOR)) {
            event.setCanceled(true);
        } else if (source.is(DamageTypes.IN_FIRE)
                && entity.level().getBlockState(entity.blockPosition()).getBlock() instanceof CampfireBlock) {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onXpPickup(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || !player.hasEffect(ModEffects.MENDING.get())) {
            return;
        }
        List<ItemStack> damaged = new ArrayList<>();
        Inventory inventory = player.getInventory();
        for (List<ItemStack> stacks : List.of(inventory.items, inventory.armor, inventory.offhand)) {
            for (ItemStack stack : stacks) {
                if (stack.isDamaged()) {
                    damaged.add(stack);
                }
            }
        }
        if (damaged.isEmpty()) {
            return;
        }
        Collections.shuffle(damaged);
        ExperienceOrb orb = event.getOrb();
        int xp = orb.getValue();
        for (ItemStack stack : damaged) {
            if (xp <= 0) {
                break;
            }
            int repair = Math.min(xp * 2, stack.getDamageValue());
            stack.setDamageValue(stack.getDamageValue() - repair);
            xp -= (repair + 1) / 2;
        }
        event.setCanceled(true);
        player.takeXpDelay = 2;
        player.take(orb, 1);
        if (xp > 0) {
            player.giveExperiencePoints(xp);
        }
        orb.discard();
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();
        if (tool.isEmpty()) {
            return;
        }
        if (EffectUtil.level(player, ModEffects.SILK_TOUCH) > 0) {
            if (TempEnchants.apply(player, tool, Enchantments.SILK_TOUCH, 1)) {
                event.setExpToDrop(0);
            }
            return;
        }
        int fortune = EffectUtil.level(player, ModEffects.FORTUNE);
        if (fortune > 0) {
            TempEnchants.apply(player, tool, Enchantments.BLOCK_FORTUNE, fortune);
        }
    }
    @SubscribeEvent
    public static void onRodUse(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (player.level().isClientSide || !(stack.getItem() instanceof FishingRodItem)) {
            return;
        }
        int lure = EffectUtil.level(player, ModEffects.LURE);
        if (lure > 0) {
            TempEnchants.apply(player, stack, Enchantments.FISHING_SPEED, lure);
        }
        int luck = EffectUtil.level(player, ModEffects.LUCK_OF_THE_SEA);
        if (luck > 0) {
            TempEnchants.apply(player, stack, Enchantments.FISHING_LUCK, luck);
        }
    }
    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        TempEnchants.cleanup(player);
        LAST_AIR.remove(player.getUUID());
        LAST_DAMAGE.remove(player.getUUID());
    }
    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        TempEnchants.clearAll();
        LAST_AIR.clear();
        LAST_DAMAGE.clear();
    }
}
