package net.scottishfrogg.potchant.registry;

import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.registries.RegistryObject;

public class ModBrewing {
    private static final List<Item> BOTTLES =
            List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
    public static void register() {
        for (ModPotions.Family family : ModPotions.FAMILIES) {
            List<RegistryObject<Potion>> levels = family.levels();
            BrewingRecipeRegistry.addRecipe(new BrewingRecipe(
                    StrictNBTIngredient.of(bottle(Items.POTION, Potions.AWKWARD)),
                    Ingredient.of(family.ingredient().get()),
                    bottle(Items.POTION, levels.get(0).get())));
            for (int i = 1; i < levels.size(); i++) {
                Potion previous = levels.get(i - 1).get();
                Potion next = levels.get(i).get();
                for (Item bottle : BOTTLES) {
                    BrewingRecipeRegistry.addRecipe(new BrewingRecipe(
                            StrictNBTIngredient.of(bottle(bottle, previous)),
                            Ingredient.of(Items.GLOWSTONE_DUST),
                            bottle(bottle, next)));
                }
            }
        }
    }
    private static ItemStack bottle(Item item, Potion potion) {
        return PotionUtils.setPotion(new ItemStack(item), potion);
    }
}