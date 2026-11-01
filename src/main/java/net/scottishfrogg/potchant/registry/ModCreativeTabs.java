package net.scottishfrogg.potchant.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.scottishfrogg.potchant.PotchantMod;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PotchantMod.MOD_ID);
    public static final RegistryObject<CreativeModeTab> POTIONS_TAB = TABS.register("potions",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + PotchantMod.MOD_ID + ".potions"))
                    .icon(() -> potionStack(ModPotions.FAMILIES.get(0).levels().get(0).get()))
                    .displayItems((params, output) -> {
                        Item[] bottles = {ModItems.POTCHANT_POTION.get(), Items.SPLASH_POTION, Items.LINGERING_POTION};
                        for (Item bottle : bottles) {
                            for (RegistryObject<Potion> potion : ModPotions.POTIONS.getEntries()) {
                                output.accept(PotionUtils.setPotion(new ItemStack(bottle), potion.get()));
                            }
                        }
                    })
                    .build());
    public static final RegistryObject<CreativeModeTab> ENCHANTS_TAB = TABS.register("enchants",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + PotchantMod.MOD_ID + ".enchants"))
                    .icon(() -> new ItemStack(Items.ENCHANTED_BOOK))
                    .withTabsBefore(POTIONS_TAB.getId())
                    .displayItems((params, output) -> {
                        for (RegistryObject<Enchantment> entry : ModEnchantments.ENCHANTMENTS.getEntries()) {
                            Enchantment enchantment = entry.get();
                            for (int level = enchantment.getMinLevel(); level <= enchantment.getMaxLevel(); level++) {
                                output.accept(EnchantedBookItem.createForEnchantment(
                                        new EnchantmentInstance(enchantment, level)));
                            }
                        }
                    })
                    .build());
    private static ItemStack potionStack(Potion potion) {
        return PotionUtils.setPotion(new ItemStack(ModItems.POTCHANT_POTION.get()), potion);
    }
}
