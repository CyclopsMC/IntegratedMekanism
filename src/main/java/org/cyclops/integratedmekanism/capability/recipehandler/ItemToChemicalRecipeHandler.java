package org.cyclops.integratedmekanism.capability.recipehandler;

import com.google.common.collect.Lists;
import mekanism.api.recipes.ItemStackToChemicalRecipe;
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
public class ItemToChemicalRecipeHandler extends MekanismRecipeHandler<ItemStackToChemicalRecipe> {

    protected ItemToChemicalRecipeHandler(IMekanismRecipeTypeProvider<?, ? extends ItemStackToChemicalRecipe, ?> recipeType, Supplier<Level> levelSupplier) {
        super(recipeType, levelSupplier, Set.of(IngredientComponents.ITEMSTACK), Set.of(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK));
    }

    @Override
    public boolean isValidSizeInput(IngredientComponent<?, ?> ingredientComponent, int size) {
        return ingredientComponent == IngredientComponents.ITEMSTACK && size == 1;
    }

    @Override
    protected void recipeToInputs(ItemStackToChemicalRecipe recipe, Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs) {
        inputs.put(IngredientComponents.ITEMSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromItemIngredient(recipe.getInput()))
        ));
    }

    @Override
    protected void recipeToOutputs(ItemStackToChemicalRecipe recipe, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(
                recipe.getOutputDefinition().get(0)
        ));
    }

    @Override
    protected void recipeToOutputsSimulated(ItemStackToChemicalRecipe recipe, IMixedIngredients input, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(
                recipe.getOutput(input.getInstances(IngredientComponents.ITEMSTACK).get(0))
        ));
    }

    @Override
    protected boolean doesRecipeMatchInput(ItemStackToChemicalRecipe recipe, IMixedIngredients input) {
        return recipe.getInput().test(input.getInstances(IngredientComponents.ITEMSTACK).get(0));
    }
}
