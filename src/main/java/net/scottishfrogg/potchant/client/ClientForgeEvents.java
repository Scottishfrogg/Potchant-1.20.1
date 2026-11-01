package net.scottishfrogg.potchant.client;

import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.scottishfrogg.potchant.PotchantMod;
import net.scottishfrogg.potchant.registry.ModEffects;
import net.scottishfrogg.potchant.util.EffectUtil;

@Mod.EventBusSubscriber(modid = PotchantMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        Player player = event.getEntity();
        Input input = event.getInput();
        int level = EffectUtil.level(player, ModEffects.SWIFT_SNEAK);
        if (level <= 0 || !input.shiftKeyDown) {
            return;
        }
        float factor = Math.min(1.0F, 0.3F + 0.15F * level) / 0.3F;
        input.leftImpulse *= factor;
        input.forwardImpulse *= factor;
    }
}
