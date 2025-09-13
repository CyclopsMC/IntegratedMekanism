package org.cyclops.integratedmekanism.capability.recipehandler;

import com.google.common.collect.Lists;
import mekanism.api.recipes.CombinerRecipe;
import mekanism.common.recipe.MekanismRecipeType;
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
public class CombinerRecipeHandler extends MekanismRecipeHandler<CombinerRecipe> {

    protected CombinerRecipeHandler(Supplier<Level> levelSupplier) {
        super(MekanismRecipeType.COMBINING, levelSupplier, Set.of(IngredientComponents.ITEMSTACK), Set.of(IngredientComponents.ITEMSTACK));
    }

    @Override
    public boolean isValidSizeInput(IngredientComponent<?, ?> ingredientComponent, int size) {
        return ingredientComponent == IngredientComponents.ITEMSTACK && size == 2;
    }

    @Override
    protected void recipeToInputs(CombinerRecipe recipe, Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs) {
        inputs.put(IngredientComponents.ITEMSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromItemIngredient(recipe.getMainInput())),
                new PrototypedIngredientAlternativesList<>(getPrototypesFromItemIngredient(recipe.getExtraInput()))
        ));
    }

    @Override
    protected void recipeToOutputs(CombinerRecipe recipe, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(recipe.getOutputDefinition().get(0)));
    }

    @Override
    protected void recipeToOutputsSimulated(CombinerRecipe recipe, IMixedIngredients input, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(recipe.getOutput(
                input.getInstances(IngredientComponents.ITEMSTACK).get(0),
                input.getInstances(IngredientComponents.ITEMSTACK).get(1)
        )));
    }

    @Override
    protected boolean doesRecipeMatchInput(CombinerRecipe recipe, IMixedIngredients input) {
        return recipe.getMainInput().test(input.getInstances(IngredientComponents.ITEMSTACK).get(0))
                && recipe.getExtraInput().test(input.getInstances(IngredientComponents.ITEMSTACK).get(1));
    }
}
