package org.cyclops.integratedmekanism.capability.recipehandler;

import com.google.common.collect.Lists;
import mekanism.api.recipes.FluidChemicalToChemicalRecipe;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
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
public class FluidChemicalToChemicalRecipeHandler extends MekanismRecipeHandler<FluidChemicalToChemicalRecipe> {

    protected FluidChemicalToChemicalRecipeHandler(IMekanismRecipeTypeProvider<?, ? extends FluidChemicalToChemicalRecipe, ?> recipeType, Supplier<Level> levelSupplier) {
        super(recipeType, levelSupplier, Set.of(IngredientComponents.FLUIDSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Set.of(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK));
    }

    @Override
    public boolean isValidSizeInput(IngredientComponent<?, ?> ingredientComponent, int size) {
        return (ingredientComponent == IngredientComponents.FLUIDSTACK && size == 1)
                || ingredientComponent == MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK && size == 1;
    }

    @Override
    protected void recipeToInputs(FluidChemicalToChemicalRecipe recipe, Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs) {
        inputs.put(IngredientComponents.FLUIDSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromFluidIngredient(recipe.getFluidInput()))
        ));
        inputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromChemicalIngredient(recipe.getChemicalInput()))
        ));
    }

    @Override
    protected void recipeToOutputs(FluidChemicalToChemicalRecipe recipe, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(
                recipe.getOutputDefinition().get(0)
        ));
    }

    @Override
    protected void recipeToOutputsSimulated(FluidChemicalToChemicalRecipe recipe, IMixedIngredients input, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(recipe.getOutput(
                input.getInstances(IngredientComponents.FLUIDSTACK).get(0),
                input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0)
        )));
    }

    @Override
    protected boolean doesRecipeMatchInput(FluidChemicalToChemicalRecipe recipe, IMixedIngredients input) {
        return recipe.getFluidInput().test(input.getInstances(IngredientComponents.FLUIDSTACK).get(0))
                && recipe.getChemicalInput().test(input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0));
    }
}
