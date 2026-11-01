package net.scottishfrogg.potchant;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.scottishfrogg.potchant.network.ModNetwork;
import net.scottishfrogg.potchant.registry.ModBrewing;
import net.scottishfrogg.potchant.registry.ModCreativeTabs;
import net.scottishfrogg.potchant.registry.ModEffects;
import net.scottishfrogg.potchant.registry.ModEnchantments;
import net.scottishfrogg.potchant.registry.ModItems;
import net.scottishfrogg.potchant.registry.ModPotions;
import org.slf4j.Logger;

@Mod(PotchantMod.MOD_ID)
public class PotchantMod {
    public static final String MOD_ID = "potchantmod";
    public static final Logger LOGGER = LogUtils.getLogger();
    public PotchantMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        ModEffects.EFFECTS.register(modEventBus);
        ModPotions.POTIONS.register(modEventBus);
        ModEnchantments.ENCHANTMENTS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModBrewing::register);
        ModNetwork.register();
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
