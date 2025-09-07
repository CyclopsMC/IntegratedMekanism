package org.cyclops.integratedmekanismics.capability.recipehandler;

import com.google.common.collect.Lists;
import mekanism.api.recipes.ElectrolysisRecipe;
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
public class SeparatingRecipeHandler extends MekanismRecipeHandler<ElectrolysisRecipe> {

    protected SeparatingRecipeHandler(Supplier<Level> levelSupplier) {
        super(MekanismRecipeType.SEPARATING, levelSupplier, Set.of(IngredientComponents.FLUIDSTACK), Set.of(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK));
    }

    @Override
    public boolean isValidSizeInput(IngredientComponent<?, ?> ingredientComponent, int size) {
        return ingredientComponent == IngredientComponents.FLUIDSTACK && size == 1;
    }

    @Override
    protected void recipeToInputs(ElectrolysisRecipe recipe, Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs) {
        inputs.put(IngredientComponents.FLUIDSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromFluidIngredient(recipe.getInput()))
        ));
    }

    @Override
    protected void recipeToOutputs(ElectrolysisRecipe recipe, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        ElectrolysisRecipe.ElectrolysisRecipeOutput output = recipe.getOutputDefinition().get(0);
        outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(
                output.left(),
                output.right()
        ));
    }

    @Override
    protected void recipeToOutputsSimulated(ElectrolysisRecipe recipe, IMixedIngredients input, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        ElectrolysisRecipe.ElectrolysisRecipeOutput output = recipe.getOutput(
                input.getInstances(IngredientComponents.FLUIDSTACK).get(0)
        );
        outputs.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                output.left(),
                output.right()
        ));
    }

    @Override
    protected boolean doesRecipeMatchInput(ElectrolysisRecipe recipe, IMixedIngredients input) {
        return recipe.getInput().test(input.getInstances(IngredientComponents.FLUIDSTACK).get(0));
    }
}
