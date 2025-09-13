package org.cyclops.integratedmekanism.capability.recipehandler;

import com.google.common.collect.Lists;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import mekanism.api.recipes.ChemicalCrystallizerRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import net.minecraft.world.level.Level;
import org.cyclops.commoncapabilities.IngredientComponents;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IPrototypedIngredientAlternatives;
import org.cyclops.commoncapabilities.api.capability.recipehandler.PrototypedIngredientAlternativesList;
import org.cyclops.commoncapabilities.api.ingredient.IMixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author rubensworks
 */
public class ChemicalCrystallizerRecipeHandler extends MekanismRecipeHandler<ChemicalCrystallizerRecipe> {

    protected ChemicalCrystallizerRecipeHandler(Supplier<Level> levelSupplier) {
        super(MekanismRecipeType.CRYSTALLIZING, levelSupplier, Set.of(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Set.of(IngredientComponents.ITEMSTACK));
    }

    @Override
    public boolean isValidSizeInput(IngredientComponent<?, ?> ingredientComponent, int size) {
        return ingredientComponent == MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK && size == 1;
    }

    @Override
    protected void recipeToInputs(ChemicalCrystallizerRecipe recipe, Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs) {
        inputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromChemicalIngredient(recipe.getInput()))
        ));
    }

    @Override
    protected void recipeToOutputs(ChemicalCrystallizerRecipe recipe, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                recipe.getOutputDefinition().get(0)
        ));
    }

    @Override
    protected void recipeToOutputsSimulated(ChemicalCrystallizerRecipe recipe, IMixedIngredients input, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                recipe.getOutput(BoxedChemicalStack.box(input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0)))
        ));
    }

    @Override
    protected boolean doesRecipeMatchInput(ChemicalCrystallizerRecipe recipe, IMixedIngredients input) {
        return ((ChemicalStackIngredient) recipe.getInput()).test(input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0));
    }
}
