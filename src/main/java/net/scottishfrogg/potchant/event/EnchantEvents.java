package net.scottishfrogg.potchant.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import net.scottishfrogg.potchant.PotchantMod;
import net.scottishfrogg.potchant.registry.ModEnchantments;

@Mod.EventBusSubscriber(modid = PotchantMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnchantEvents {
    private static final int FOREVER = -1;
    private static final int POISON_I_TICKS = 900;
    private static final int POISON_II_TICKS = 450;
    private static final int WEAKNESS_TICKS = 600;
    private static final int DECAY_TICKS = 800;
    private static final int BAD_LUCK_TICKS = 200;
    private static final int BLINDNESS_TICKS = 60;
    private static final int NAUSEA_TICKS = 60;
    private static final int HUNGER_TICKS = 100;
    private static final int WITHER_TICKS = 60;
    private static final int LEVITATION_TICKS = 40;
    private static final int GLOWING_TICKS = 100;
    private static final int DARKNESS_TICKS = 60;
    private static final int BAD_OMEN_TICKS = 120000;
    private static final int SATURATION_I_TICKS = 200;
    private static final int SATURATION_II_TICKS = 100;
    private static final float MINING_SPEED_PER_LEVEL = 0.2F;
    private static final double DOLPHINS_GRACE_BONUS = 0.5D;
    private static final UUID DOLPHINS_GRACE_UUID = UUID.fromString("9a3c1f4e-7b2d-4e8a-a1c5-3f6e8d2b7c41");
    private static final float HARMING_DAMAGE_PER_LEVEL = 6.0F;
    private static final float HEALING_PER_LEVEL = 4.0F;
    private static final int HEALING_DEBOUNCE_TICKS = 10;
    private static final List<MobEffect> MANAGED = List.of(
            MobEffects.REGENERATION, MobEffects.MOVEMENT_SPEED, MobEffects.FIRE_RESISTANCE, MobEffects.NIGHT_VISION,
            MobEffects.DAMAGE_BOOST, MobEffects.JUMP, MobEffects.WATER_BREATHING, MobEffects.INVISIBILITY,
            MobEffects.SLOW_FALLING, MobEffects.LUCK, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DAMAGE_RESISTANCE,
            MobEffects.ABSORPTION, MobEffects.HEALTH_BOOST, MobEffects.CONDUIT_POWER, MobEffects.HERO_OF_THE_VILLAGE);
    private static final String TAG_POISON = "PotchantPoison";
    private static final String TAG_WEAKNESS = "PotchantWeakness";
    private static final String TAG_DECAY = "PotchantDecay";
    private static final String TAG_BAD_LUCK = "PotchantBadLuck";
    private static final String TAG_BLINDNESS = "PotchantBlindness";
    private static final String TAG_NAUSEA = "PotchantNausea";
    private static final String TAG_HUNGER = "PotchantHunger";
    private static final String TAG_WITHER = "PotchantWither";
    private static final String TAG_LEVITATION = "PotchantLevitation";
    private static final String TAG_GLOWING = "PotchantGlowing";
    private static final String TAG_DARKNESS = "PotchantDarkness";
    private static final Map<UUID, Long> LAST_HEAL = new HashMap<>();
    private static int level(RegistryObject<Enchantment> enchantment, ItemStack stack) {
        return stack.isEmpty() ? 0 : EnchantmentHelper.getTagEnchantmentLevel(enchantment.get(), stack);
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)
                || player.tickCount % 4 != 0) {
            return;
        }
        Map<MobEffect, Integer> wanted = wantedEffects(player);
        for (MobEffect effect : MANAGED) {
            Integer amplifier = wanted.get(effect);
            MobEffectInstance current = player.getEffect(effect);
            boolean ours = current != null && current.isAmbient() && current.getDuration() == FOREVER;
            if (amplifier == null) {
                if (ours) {
                    player.removeEffect(effect);
                }
            } else if (current == null || (ours && current.getAmplifier() != amplifier)) {
                if (current != null) {
                    player.removeEffect(effect);
                }
                player.addEffect(new MobEffectInstance(effect, FOREVER, amplifier, true, false, true));
            }
        }
        int saturation = level(ModEnchantments.SATURATION, player.getItemBySlot(EquipmentSlot.CHEST));
        if (saturation > 0 && player.tickCount % (saturation >= 2 ? SATURATION_II_TICKS : SATURATION_I_TICKS) == 0) {
            player.getFoodData().eat(1, 0.0F);
        }
    }
    private static Map<MobEffect, Integer> wantedEffects(Player player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        ItemStack mainHand = player.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack offHand = player.getItemBySlot(EquipmentSlot.OFFHAND);
        Map<MobEffect, Integer> wanted = new HashMap<>();
        int level;
        if ((level = level(ModEnchantments.REGENERATION, chest)) > 0) {
            want(wanted, MobEffects.REGENERATION, level - 1);
        }
        if ((level = level(ModEnchantments.SWIFTNESS, feet)) > 0) {
            want(wanted, MobEffects.MOVEMENT_SPEED, level - 1);
        }
        if (level(ModEnchantments.FIRE_RESISTANCE, chest) > 0) {
            want(wanted, MobEffects.FIRE_RESISTANCE, 0);
        }
        if (level(ModEnchantments.NIGHT_VISION, head) > 0) {
            want(wanted, MobEffects.NIGHT_VISION, 0);
        }
        level = Math.max(heldStrength(mainHand), heldStrength(offHand));
        if (level > 0) {
            want(wanted, MobEffects.DAMAGE_BOOST, level - 1);
        }
        if ((level = level(ModEnchantments.LEAPING, legs)) > 0) {
            want(wanted, MobEffects.JUMP, level - 1);
        }
        if (level(ModEnchantments.WATER_BREATHING, head) > 0) {
            want(wanted, MobEffects.WATER_BREATHING, 0);
        }
        if (level(ModEnchantments.INVISIBILITY, chest) > 0) {
            want(wanted, MobEffects.INVISIBILITY, 0);
        }
        if (level(ModEnchantments.SLOW_FALLING, feet) > 0) {
            want(wanted, MobEffects.SLOW_FALLING, 0);
        }
        int luckPieces = level(ModEnchantments.LUCK, head) + level(ModEnchantments.LUCK, chest)
                + level(ModEnchantments.LUCK, legs) + level(ModEnchantments.LUCK, feet);
        if (luckPieces > 0) {
            want(wanted, MobEffects.LUCK, luckPieces - 1);
        }
        if ((level = level(ModEnchantments.SLOWNESS, feet)) > 0) {
            want(wanted, MobEffects.MOVEMENT_SLOWDOWN, level - 1);
        }
        if ((level = level(ModEnchantments.TURTLE_MASTER, chest)) > 0) {
            want(wanted, MobEffects.MOVEMENT_SLOWDOWN, level == 1 ? 3 : 5);
            want(wanted, MobEffects.DAMAGE_RESISTANCE, level == 1 ? 2 : 3);
        }
        if ((level = level(ModEnchantments.RESISTANCE, chest)) > 0) {
            want(wanted, MobEffects.DAMAGE_RESISTANCE, level - 1);
        }
        if ((level = level(ModEnchantments.ABSORPTION, chest)) > 0) {
            want(wanted, MobEffects.ABSORPTION, level - 1);
        }
        if ((level = level(ModEnchantments.HEALTH_BOOST, chest)) > 0) {
            want(wanted, MobEffects.HEALTH_BOOST, level - 1);
        }
        if (level(ModEnchantments.CONDUIT_POWER, head) > 0) {
            want(wanted, MobEffects.CONDUIT_POWER, 0);
        }
        if ((level = level(ModEnchantments.HERO_OF_THE_VILLAGE, head)) > 0) {
            want(wanted, MobEffects.HERO_OF_THE_VILLAGE, level - 1);
        }
        return wanted;
    }
    private static int heldStrength(ItemStack stack) {
        boolean swordOrAxe = stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem;
        return swordOrAxe ? level(ModEnchantments.STRENGTH, stack) : 0;
    }
    private static void want(Map<MobEffect, Integer> wanted, MobEffect effect, int amplifier) {
        wanted.merge(effect, amplifier, Math::max);
    }
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack tool = event.getEntity().getMainHandItem();
        int haste = level(ModEnchantments.HASTE, tool);
        int fatigue = level(ModEnchantments.MINING_FATIGUE, tool);
        if (haste > 0 || fatigue > 0) {
            event.setNewSpeed(event.getNewSpeed() * (1.0F + MINING_SPEED_PER_LEVEL * haste)
                    * (1.0F - MINING_SPEED_PER_LEVEL * fatigue));
        }
    }
    @SubscribeEvent
    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        if (event.getSlotType() == EquipmentSlot.LEGS && level(ModEnchantments.DOLPHINS_GRACE, event.getItemStack()) > 0) {
            event.addModifier(ForgeMod.SWIM_SPEED.get(), new AttributeModifier(DOLPHINS_GRACE_UUID,
                    "Potchant dolphin's grace", DOLPHINS_GRACE_BONUS, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }
    @SubscribeEvent
    public static void onArrowShot(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || event.loadedFromDisk()
                || !(event.getEntity() instanceof AbstractArrow arrow) || arrow instanceof ThrownTrident
                || !(arrow.getOwner() instanceof LivingEntity shooter)) {
            return;
        }
        ItemStack bow = shooter.getMainHandItem().getItem() instanceof BowItem ? shooter.getMainHandItem()
                : shooter.getOffhandItem().getItem() instanceof BowItem ? shooter.getOffhandItem() : ItemStack.EMPTY;
        if (bow.isEmpty()) {
            return;
        }
        CompoundTag data = arrow.getPersistentData();
        data.putInt(TAG_POISON, level(ModEnchantments.POISON, bow));
        data.putInt(TAG_WEAKNESS, level(ModEnchantments.WEAKNESS, bow));
        data.putInt(TAG_DECAY, level(ModEnchantments.DECAY, bow));
        data.putInt(TAG_BAD_LUCK, level(ModEnchantments.BAD_LUCK, bow));
        data.putInt(TAG_BLINDNESS, level(ModEnchantments.BLINDNESS, bow));
        data.putInt(TAG_NAUSEA, level(ModEnchantments.NAUSEA, bow));
        data.putInt(TAG_HUNGER, level(ModEnchantments.HUNGER, bow));
        data.putInt(TAG_WITHER, level(ModEnchantments.WITHER, bow));
        data.putInt(TAG_LEVITATION, level(ModEnchantments.LEVITATION, bow));
        data.putInt(TAG_GLOWING, level(ModEnchantments.GLOWING, bow));
        data.putInt(TAG_DARKNESS, level(ModEnchantments.DARKNESS, bow));
    }
    @SubscribeEvent
    public static void onHit(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) {
            return;
        }
        DamageSource source = event.getSource();
        int poison = 0;
        int weakness = 0;
        int decay = 0;
        int badLuck = 0;
        int blindness = 0;
        int nausea = 0;
        int hunger = 0;
        int wither = 0;
        int levitation = 0;
        int glowing = 0;
        int darkness = 0;
        if (source.getDirectEntity() instanceof AbstractArrow arrow && !(arrow instanceof ThrownTrident)) {
            CompoundTag data = arrow.getPersistentData();
            poison = data.getInt(TAG_POISON);
            weakness = data.getInt(TAG_WEAKNESS);
            decay = data.getInt(TAG_DECAY);
            badLuck = data.getInt(TAG_BAD_LUCK);
            blindness = data.getInt(TAG_BLINDNESS);
            nausea = data.getInt(TAG_NAUSEA);
            hunger = data.getInt(TAG_HUNGER);
            wither = data.getInt(TAG_WITHER);
            levitation = data.getInt(TAG_LEVITATION);
            glowing = data.getInt(TAG_GLOWING);
            darkness = data.getInt(TAG_DARKNESS);
        } else if (source.getEntity() instanceof LivingEntity attacker && source.getDirectEntity() == attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            if (weapon.getItem() instanceof SwordItem || weapon.getItem() instanceof AxeItem) {
                poison = level(ModEnchantments.POISON, weapon);
                weakness = level(ModEnchantments.WEAKNESS, weapon);
                decay = level(ModEnchantments.DECAY, weapon);
                badLuck = level(ModEnchantments.BAD_LUCK, weapon);
                blindness = level(ModEnchantments.BLINDNESS, weapon);
                nausea = level(ModEnchantments.NAUSEA, weapon);
                hunger = level(ModEnchantments.HUNGER, weapon);
                wither = level(ModEnchantments.WITHER, weapon);
                levitation = level(ModEnchantments.LEVITATION, weapon);
                glowing = level(ModEnchantments.GLOWING, weapon);
                darkness = level(ModEnchantments.DARKNESS, weapon);
            }
        }
        if (poison > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, poison >= 2 ? POISON_II_TICKS : POISON_I_TICKS,
                    poison >= 2 ? 1 : 0));
        }
        if (weakness > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, WEAKNESS_TICKS, 0));
        }
        if (decay > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, DECAY_TICKS, 1));
        }
        if (badLuck > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.UNLUCK, BAD_LUCK_TICKS, badLuck - 1));
        }
        if (blindness > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, BLINDNESS_TICKS, 0));
        }
        if (nausea > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, NAUSEA_TICKS, 0));
        }
        if (hunger > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.HUNGER, HUNGER_TICKS, 0));
        }
        if (wither > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, WITHER_TICKS, wither - 1));
        }
        if (levitation > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, LEVITATION_TICKS, 0));
        }
        if (glowing > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOWING_TICKS, 0));
        }
        if (darkness > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, DARKNESS_TICKS, 0));
        }
    }
    @SubscribeEvent
    public static void onEquip(LivingEquipmentChangeEvent event) {
        LivingEntity wearer = event.getEntity();
        if (wearer.level().isClientSide || event.getSlot().getType() != EquipmentSlot.Type.ARMOR
                || wearer.tickCount < 5 || ItemStack.isSameItem(event.getFrom(), event.getTo())) {
            return;
        }
        int level = level(ModEnchantments.HARMING, event.getTo());
        if (level > 0) {
            wearer.hurt(wearer.damageSources().magic(), HARMING_DAMAGE_PER_LEVEL * level);
        }
        int omen = level(ModEnchantments.BAD_OMEN, event.getTo());
        if (omen > 0) {
            wearer.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, BAD_OMEN_TICKS, omen - 1));
        }
    }
    public static void tryHeal(ServerPlayer player) {
        int level = level(ModEnchantments.HEALING, player.getItemBySlot(EquipmentSlot.CHEST));
        if (level <= 0 || !player.isAlive() || player.getHealth() >= player.getMaxHealth()) {
            return;
        }
        long now = player.level().getGameTime();
        Long last = LAST_HEAL.get(player.getUUID());
        if (last != null && now - last < HEALING_DEBOUNCE_TICKS) {
            return;
        }
        LAST_HEAL.put(player.getUUID(), now);
        player.heal(HEALING_PER_LEVEL * level);
        ((ServerLevel) player.level()).sendParticles(ParticleTypes.HEART,
                player.getX(), player.getY() + 1.0D, player.getZ(), 4, 0.3D, 0.4D, 0.3D, 0.0D);
    }
    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        LAST_HEAL.remove(event.getEntity().getUUID());
    }
}
