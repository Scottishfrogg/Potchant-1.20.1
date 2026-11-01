package net.scottishfrogg.potchant.registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.scottishfrogg.potchant.PotchantMod;
import net.scottishfrogg.potchant.item.PotchantPotionItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PotchantMod.MOD_ID);
    public static final RegistryObject<Item> POTCHANT_POTION =
            ITEMS.register("potchant_potion", () -> new PotchantPotionItem(new Item.Properties().stacksTo(1)));
}
