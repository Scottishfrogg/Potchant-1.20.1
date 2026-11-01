package net.scottishfrogg.potchant.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.scottishfrogg.potchant.PotchantMod;
import net.scottishfrogg.potchant.util.EnchantAlignment;

@Mod.EventBusSubscriber(modid = PotchantMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnchantCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("enchantall")
                .requires(source -> source.hasPermission(2))
                .executes(context -> enchant(context.getSource(), null, "every")));
        dispatcher.register(Commands.literal("enchantallgood")
                .requires(source -> source.hasPermission(2))
                .executes(context -> enchant(context.getSource(), EnchantAlignment.GOOD, "every positive")));
        dispatcher.register(Commands.literal("enchantallbad")
                .requires(source -> source.hasPermission(2))
                .executes(context -> enchant(context.getSource(), EnchantAlignment.BAD, "every negative")));
    }
    private static int enchant(CommandSourceStack source, EnchantAlignment only, String what)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            source.sendFailure(Component.literal("Hold the item you want to enchant."));
            return 0;
        }
        Map<Enchantment, Integer> enchantments = new LinkedHashMap<>(EnchantmentHelper.getEnchantments(stack));
        int added = 0;
        for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS) {
            if (only == null || EnchantAlignment.of(enchantment) == only) {
                enchantments.put(enchantment, enchantment.getMaxLevel());
                added++;
            }
        }
        EnchantmentHelper.setEnchantments(enchantments, stack);
        final int count = added;
        source.sendSuccess(() -> Component.literal(
                "Put " + what + " enchantment (" + count + ") on " + stack.getHoverName().getString()), true);
        return count;
    }
}
