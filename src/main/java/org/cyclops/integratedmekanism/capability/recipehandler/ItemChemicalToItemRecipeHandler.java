package org.cyclops.integratedmekanism.capability.recipehandler;

import com.google.common.collect.Lists;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.chemical.ItemStackChemicalToItemStackRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
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
public class ItemChemicalToItemRecipeHandler<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, INGREDIENT extends ChemicalStackIngredient<CHEMICAL, STACK>> extends MekanismRecipeHandler<ItemStackChemicalToItemStackRecipe<CHEMICAL, STACK, INGREDIENT>> {

    protected ItemChemicalToItemRecipeHandler(IMekanismRecipeTypeProvider<? extends ItemStackChemicalToItemStackRecipe<CHEMICAL, STACK, INGREDIENT>, ?> recipeType, Supplier<Level> levelSupplier) {
        super(recipeType, levelSupplier, Set.of(IngredientComponents.ITEMSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Set.of(IngredientComponents.ITEMSTACK));
    }

    @Override
    public boolean isValidSizeInput(IngredientComponent<?, ?> ingredientComponent, int size) {
        return (ingredientComponent == IngredientComponents.ITEMSTACK && size == 1)
                || (ingredientComponent == MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK && size == 1);
    }

    @Override
    protected void recipeToInputs(ItemStackChemicalToItemStackRecipe<CHEMICAL, STACK, INGREDIENT> recipe, Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs) {
        inputs.put(IngredientComponents.ITEMSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromItemIngredient(recipe.getItemInput()))
        ));
        inputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromChemicalIngredient(recipe.getChemicalInput()))
        ));
    }

    @Override
    protected void recipeToOutputs(ItemStackChemicalToItemStackRecipe<CHEMICAL, STACK, INGREDIENT> recipe, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                recipe.getOutputDefinition().get(0)
        ));
    }

    @Override
    protected void recipeToOutputsSimulated(ItemStackChemicalToItemStackRecipe<CHEMICAL, STACK, INGREDIENT> recipe, IMixedIngredients input, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                recipe.getOutput(
                        input.getInstances(IngredientComponents.ITEMSTACK).get(0),
                        (STACK) input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0)
                )
        ));
    }

    @Override
    protected boolean doesRecipeMatchInput(ItemStackChemicalToItemStackRecipe<CHEMICAL, STACK, INGREDIENT> recipe, IMixedIngredients input) {
        return recipe.getItemInput().test(input.getInstances(IngredientComponents.ITEMSTACK).get(0))
                && recipe.getChemicalInput().test((STACK) input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0));
    }
}
