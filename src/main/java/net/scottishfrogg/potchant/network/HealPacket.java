package net.scottishfrogg.potchant.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.scottishfrogg.potchant.event.EnchantEvents;

public class HealPacket {
    public static void encode(HealPacket packet, FriendlyByteBuf buffer) {
    }
    public static HealPacket decode(FriendlyByteBuf buffer) {
        return new HealPacket();
    }
    public static void handle(HealPacket packet, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                EnchantEvents.tryHeal(player);
            }
        });
        ctx.setPacketHandled(true);
    }
}
