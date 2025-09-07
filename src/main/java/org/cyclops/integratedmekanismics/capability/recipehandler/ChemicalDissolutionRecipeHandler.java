package org.cyclops.integratedmekanismics.capability.recipehandler;

import com.google.common.collect.Lists;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.ChemicalDissolutionRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import net.minecraft.world.level.Level;
import org.cyclops.commoncapabilities.IngredientComponents;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IPrototypedIngredientAlternatives;
import org.cyclops.commoncapabilities.api.capability.recipehandler.PrototypedIngredientAlternativesList;
import org.cyclops.commoncapabilities.api.ingredient.IMixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.integratedmekanismics.ingredient.MekanismIngredientComponents;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author rubensworks
 */
public class ChemicalDissolutionRecipeHandler extends MekanismRecipeHandler<ChemicalDissolutionRecipe> {

    protected ChemicalDissolutionRecipeHandler(Supplier<Level> levelSupplier) {
        super(MekanismRecipeType.DISSOLUTION, levelSupplier, Set.of(IngredientComponents.ITEMSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Set.of(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK));
    }

    @Override
    public boolean isValidSizeInput(IngredientComponent<?, ?> ingredientComponent, int size) {
        return (ingredientComponent == IngredientComponents.ITEMSTACK && size == 1)
                || (ingredientComponent == MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK && size == 1);
    }

    @Override
    protected void recipeToInputs(ChemicalDissolutionRecipe recipe, Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs) {
        inputs.put(IngredientComponents.ITEMSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromItemIngredient(recipe.getItemInput()))
        ));
        inputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromChemicalIngredient(recipe.getGasInput()))
        ));
    }

    @Override
    protected void recipeToOutputs(ChemicalDissolutionRecipe recipe, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(
                recipe.getOutputDefinition().get(0).getChemicalStack()
        ));
    }

    @Override
    protected void recipeToOutputsSimulated(ChemicalDissolutionRecipe recipe, IMixedIngredients input, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        ChemicalStack<?> chemicalStack = input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0);
        outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(
                recipe.getOutput(
                        input.getInstances(IngredientComponents.ITEMSTACK).get(0),
                        chemicalStack instanceof GasStack gasStack ? gasStack : GasStack.EMPTY
                )
        ));
    }

    @Override
    protected boolean doesRecipeMatchInput(ChemicalDissolutionRecipe recipe, IMixedIngredients input) {
        return recipe.getItemInput().test(input.getInstances(IngredientComponents.ITEMSTACK).get(0))
                && ((ChemicalStackIngredient) recipe.getGasInput()).test(input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0));
    }
}
