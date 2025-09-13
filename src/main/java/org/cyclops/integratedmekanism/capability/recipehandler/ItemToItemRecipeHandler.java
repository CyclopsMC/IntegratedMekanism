package org.cyclops.integratedmekanism.capability.recipehandler;

import com.google.common.collect.Lists;
import mekanism.api.recipes.ItemStackToItemStackRecipe;
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
public class ItemToItemRecipeHandler extends MekanismRecipeHandler<ItemStackToItemStackRecipe> {

    protected ItemToItemRecipeHandler(IMekanismRecipeTypeProvider<ItemStackToItemStackRecipe, ?> recipeType, Supplier<Level> levelSupplier) {
        super(recipeType, levelSupplier, Set.of(IngredientComponents.ITEMSTACK), Set.of(IngredientComponents.ITEMSTACK));
    }

    @Override
    public boolean isValidSizeInput(IngredientComponent<?, ?> ingredientComponent, int size) {
        return ingredientComponent == IngredientComponents.ITEMSTACK && size == 1;
    }

    @Override
    protected void recipeToInputs(ItemStackToItemStackRecipe recipe, Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs) {
        inputs.put(IngredientComponents.ITEMSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromItemIngredient(recipe.getInput()))
        ));
    }

    @Override
    protected void recipeToOutputs(ItemStackToItemStackRecipe recipe, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                recipe.getOutputDefinition().get(0)
        ));
    }

    @Override
    protected void recipeToOutputsSimulated(ItemStackToItemStackRecipe recipe, IMixedIngredients input, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                recipe.getOutput(input.getInstances(IngredientComponents.ITEMSTACK).get(0))
        ));
    }

    @Override
    protected boolean doesRecipeMatchInput(ItemStackToItemStackRecipe recipe, IMixedIngredients input) {
        return recipe.getInput().test(input.getInstances(IngredientComponents.ITEMSTACK).get(0));
    }
}
