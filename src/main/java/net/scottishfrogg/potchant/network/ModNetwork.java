package net.scottishfrogg.potchant.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.scottishfrogg.potchant.PotchantMod;

public final class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(PotchantMod.MOD_ID, "main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
    private ModNetwork() {
    }
    public static void register() {
        CHANNEL.registerMessage(0, HealPacket.class, HealPacket::encode, HealPacket::decode, HealPacket::handle);
    }
}
