package org.cyclops.integratedmekanismics.capability.recipehandler;

import com.google.common.collect.Lists;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.chemical.ChemicalChemicalToChemicalRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import net.minecraft.world.level.Level;
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
public class ChemicalChemicalToChemicalRecipeHandler<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, INGREDIENT extends ChemicalStackIngredient<CHEMICAL, STACK>> extends MekanismRecipeHandler<ChemicalChemicalToChemicalRecipe<CHEMICAL, STACK, INGREDIENT>> {

    protected ChemicalChemicalToChemicalRecipeHandler(IMekanismRecipeTypeProvider<? extends ChemicalChemicalToChemicalRecipe<CHEMICAL, STACK, INGREDIENT>, ?> recipeType, Supplier<Level> levelSupplier) {
        super(recipeType, levelSupplier, Set.of(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Set.of(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK));
    }

    @Override
    public boolean isValidSizeInput(IngredientComponent<?, ?> ingredientComponent, int size) {
        return ingredientComponent == MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK && size == 2;
    }

    @Override
    protected void recipeToInputs(ChemicalChemicalToChemicalRecipe<CHEMICAL, STACK, INGREDIENT> recipe, Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs) {
        inputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromChemicalIngredient(recipe.getLeftInput())),
                new PrototypedIngredientAlternativesList<>(getPrototypesFromChemicalIngredient(recipe.getRightInput()))
        ));
    }

    @Override
    protected void recipeToOutputs(ChemicalChemicalToChemicalRecipe<CHEMICAL, STACK, INGREDIENT> recipe, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(
                recipe.getOutputDefinition().get(0)
        ));
    }

    @Override
    protected void recipeToOutputsSimulated(ChemicalChemicalToChemicalRecipe<CHEMICAL, STACK, INGREDIENT> recipe, IMixedIngredients input, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(recipe.getOutput(
                (STACK) input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0),
                (STACK) input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(1)
        )));
    }

    @Override
    protected boolean doesRecipeMatchInput(ChemicalChemicalToChemicalRecipe<CHEMICAL, STACK, INGREDIENT> recipe, IMixedIngredients input) {
        return recipe.getLeftInput().test((STACK) input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0))
                && recipe.getRightInput().test((STACK) input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(1));
    }
}
