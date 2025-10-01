package org.cyclops.integratedmekanism.ingredient;

import com.google.common.collect.Lists;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponentCategoryType;

/**
 * @author rubensworks
 */
public class MekanismIngredientComponents {

    public static IngredientComponent<ChemicalStack, Integer> INGREDIENT_CHEMICALSTACK = new IngredientComponent<>(
            "mekanism:chemicalstack",
            new IngredientMatcherChemicalStack(),
            new IngredientSerializerChemicalStack(),
            Lists.newArrayList(
                    new IngredientComponentCategoryType<>(ResourceLocation.tryParse("mekanism:chemicalstack/chemical"),
                            Chemical.class, true, ChemicalStack::getChemical, ChemicalMatch.TYPE, false),
                    new IngredientComponentCategoryType<>(ResourceLocation.tryParse("mekanism:chemicalstack/amount"),
                            Long.class, false, ChemicalStack::getAmount, ChemicalMatch.AMOUNT, true)
            )
    ).setTranslationKey("recipecomponent.mekanism.chemicalstack");

    public static void registerStorageWrapperHandlers() {
        INGREDIENT_CHEMICALSTACK.setStorageWrapperHandler(mekanism.common.capabilities.Capabilities.CHEMICAL.block(), new IngredientComponentStorageWrapperHandlerChemicalStack<>(INGREDIENT_CHEMICALSTACK, mekanism.common.capabilities.Capabilities.CHEMICAL.block()));
        INGREDIENT_CHEMICALSTACK.setStorageWrapperHandler(mekanism.common.capabilities.Capabilities.CHEMICAL.item(), new IngredientComponentStorageWrapperHandlerChemicalStack<>(INGREDIENT_CHEMICALSTACK, mekanism.common.capabilities.Capabilities.CHEMICAL.item()));
        INGREDIENT_CHEMICALSTACK.setStorageWrapperHandler(mekanism.common.capabilities.Capabilities.CHEMICAL.entity(), new IngredientComponentStorageWrapperHandlerChemicalStack<>(INGREDIENT_CHEMICALSTACK, mekanism.common.capabilities.Capabilities.CHEMICAL.entity()));
    }

}
