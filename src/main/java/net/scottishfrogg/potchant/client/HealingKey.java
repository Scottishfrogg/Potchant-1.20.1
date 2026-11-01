package net.scottishfrogg.potchant.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.scottishfrogg.potchant.PotchantMod;
import net.scottishfrogg.potchant.network.HealPacket;
import net.scottishfrogg.potchant.network.ModNetwork;
import org.lwjgl.glfw.GLFW;

public final class HealingKey {
    public static final KeyMapping HEAL = new KeyMapping(
            "key.potchantmod.healing", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.potchantmod");
    private HealingKey() {
    }
    @Mod.EventBusSubscriber(modid = PotchantMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
            event.register(HEAL);
        }
    }
    @Mod.EventBusSubscriber(modid = PotchantMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBus {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            while (HEAL.consumeClick()) {
                if (Minecraft.getInstance().player != null) {
                    ModNetwork.CHANNEL.sendToServer(new HealPacket());
                }
            }
        }
    }
}
