package org.cyclops.integratedmekanismics.capability.recipehandler;

import com.google.common.collect.Lists;
import mekanism.api.recipes.FluidToFluidRecipe;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import net.minecraft.world.level.Level;
import org.cyclops.commoncapabilities.IngredientComponents;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IPrototypedIngredientAlternatives;
import org.cyclops.commoncapabilities.api.capability.recipehandler.PrototypedIngredientAlternativesList;
import org.cyclops.commoncapabilities.api.ingredient.IMixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author rubensworks
 */
public class FluidToFluidRecipeHandler extends MekanismRecipeHandler<FluidToFluidRecipe> {

    protected FluidToFluidRecipeHandler(IMekanismRecipeTypeProvider<? extends FluidToFluidRecipe, ?> recipeType, Supplier<Level> levelSupplier) {
        super(recipeType, levelSupplier, Set.of(IngredientComponents.FLUIDSTACK), Set.of(IngredientComponents.FLUIDSTACK));
    }

    @Override
    public boolean isValidSizeInput(IngredientComponent<?, ?> ingredientComponent, int size) {
        return ingredientComponent == IngredientComponents.FLUIDSTACK && size == 1;
    }

    @Override
    protected void recipeToInputs(FluidToFluidRecipe recipe, Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs) {
        inputs.put(IngredientComponents.FLUIDSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromFluidIngredient(recipe.getInput()))
        ));
    }

    @Override
    protected void recipeToOutputs(FluidToFluidRecipe recipe, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(IngredientComponents.FLUIDSTACK, Lists.newArrayList(
                recipe.getOutputDefinition().get(0)
        ));
    }

    @Override
    protected void recipeToOutputsSimulated(FluidToFluidRecipe recipe, IMixedIngredients input, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(IngredientComponents.FLUIDSTACK, Lists.newArrayList(recipe.getOutput(
                input.getInstances(IngredientComponents.FLUIDSTACK).get(0)
        )));
    }

    @Override
    protected boolean doesRecipeMatchInput(FluidToFluidRecipe recipe, IMixedIngredients input) {
        return recipe.getInput().test(input.getInstances(IngredientComponents.FLUIDSTACK).get(0));
    }
}
