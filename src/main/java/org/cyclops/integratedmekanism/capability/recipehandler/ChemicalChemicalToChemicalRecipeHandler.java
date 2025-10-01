package org.cyclops.integratedmekanism.capability.recipehandler;

import com.google.common.collect.Lists;
import mekanism.api.recipes.ChemicalChemicalToChemicalRecipe;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import net.minecraft.world.level.Level;
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
public class ChemicalChemicalToChemicalRecipeHandler extends MekanismRecipeHandler<ChemicalChemicalToChemicalRecipe> {

    protected ChemicalChemicalToChemicalRecipeHandler(IMekanismRecipeTypeProvider<?, ? extends ChemicalChemicalToChemicalRecipe, ?> recipeType, Supplier<Level> levelSupplier) {
        super(recipeType, levelSupplier, Set.of(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Set.of(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK));
    }

    @Override
    public boolean isValidSizeInput(IngredientComponent<?, ?> ingredientComponent, int size) {
        return ingredientComponent == MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK && size == 2;
    }

    @Override
    protected void recipeToInputs(ChemicalChemicalToChemicalRecipe recipe, Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs) {
        inputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromChemicalIngredient(recipe.getLeftInput())),
                new PrototypedIngredientAlternativesList<>(getPrototypesFromChemicalIngredient(recipe.getRightInput()))
        ));
    }

    @Override
    protected void recipeToOutputs(ChemicalChemicalToChemicalRecipe recipe, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(
                recipe.getOutputDefinition().get(0)
        ));
    }

    @Override
    protected void recipeToOutputsSimulated(ChemicalChemicalToChemicalRecipe recipe, IMixedIngredients input, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(recipe.getOutput(
                input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0),
                input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(1)
        )));
    }

    @Override
    protected boolean doesRecipeMatchInput(ChemicalChemicalToChemicalRecipe recipe, IMixedIngredients input) {
        return recipe.getLeftInput().test(input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0))
                && recipe.getRightInput().test(input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(1));
    }
}
