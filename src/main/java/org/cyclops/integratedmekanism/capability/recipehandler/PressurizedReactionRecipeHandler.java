package org.cyclops.integratedmekanism.capability.recipehandler;

import com.google.common.collect.Lists;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.PressurizedReactionRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
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
public class PressurizedReactionRecipeHandler extends MekanismRecipeHandler<PressurizedReactionRecipe> {

    protected PressurizedReactionRecipeHandler(Supplier<Level> levelSupplier) {
        super(MekanismRecipeType.REACTION, levelSupplier, Set.of(IngredientComponents.ITEMSTACK, IngredientComponents.FLUIDSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Set.of(IngredientComponents.ITEMSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK));
    }

    @Override
    public boolean isValidSizeInput(IngredientComponent<?, ?> ingredientComponent, int size) {
        return (ingredientComponent == IngredientComponents.ITEMSTACK && size == 1)
                || (ingredientComponent == IngredientComponents.FLUIDSTACK && size == 1)
                || (ingredientComponent == MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK && size == 1);
    }

    @Override
    protected void recipeToInputs(PressurizedReactionRecipe recipe, Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs) {
        inputs.put(IngredientComponents.ITEMSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromItemIngredient(recipe.getInputSolid()))
        ));
        inputs.put(IngredientComponents.FLUIDSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromFluidIngredient(recipe.getInputFluid()))
        ));
        inputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, List.of(
                new PrototypedIngredientAlternativesList<>(getPrototypesFromChemicalIngredient(recipe.getInputChemical()))
        ));
    }

    @Override
    protected void recipeToOutputs(PressurizedReactionRecipe recipe, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        PressurizedReactionRecipe.PressurizedReactionRecipeOutput out = recipe.getOutputDefinition().get(0);
        if (!out.item().isEmpty()) {
            outputs.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                    out.item()
            ));
        }
        if (!out.chemical().isEmpty()) {
            outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(
                    out.chemical()
            ));
        }
    }

    @Override
    protected void recipeToOutputsSimulated(PressurizedReactionRecipe recipe, IMixedIngredients input, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        ChemicalStack chemicalStack = input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0);
        PressurizedReactionRecipe.PressurizedReactionRecipeOutput out = recipe.getOutput(
                input.getInstances(IngredientComponents.ITEMSTACK).get(0),
                input.getInstances(IngredientComponents.FLUIDSTACK).get(0),
                chemicalStack
        );
        outputs.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                out.item()
        ));
        outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(
                out.chemical()
        ));
    }

    @Override
    protected boolean doesRecipeMatchInput(PressurizedReactionRecipe recipe, IMixedIngredients input) {
        return recipe.getInputSolid().test(input.getInstances(IngredientComponents.ITEMSTACK).get(0))
                && recipe.getInputFluid().test(input.getInstances(IngredientComponents.FLUIDSTACK).get(0))
                && ((ChemicalStackIngredient) recipe.getInputChemical()).test(input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0));
    }
}
