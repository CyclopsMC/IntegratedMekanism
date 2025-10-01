package org.cyclops.integratedmekanism.capability.recipehandler;

import com.google.common.collect.Lists;
import mekanism.api.recipes.RotaryRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.cyclops.commoncapabilities.IngredientComponents;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IPrototypedIngredientAlternatives;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import org.cyclops.commoncapabilities.api.capability.recipehandler.PrototypedIngredientAlternativesList;
import org.cyclops.commoncapabilities.api.ingredient.IMixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author rubensworks
 */
public class RotaryRecipeHandler extends MekanismRecipeHandler<RotaryRecipe> {

    private boolean fluidToChemical;

    protected RotaryRecipeHandler(Supplier<Level> levelSupplier) {
        super(MekanismRecipeType.ROTARY, levelSupplier, Set.of(IngredientComponents.FLUIDSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Set.of(IngredientComponents.FLUIDSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK));
    }

    @Override
    public boolean isValidSizeInput(IngredientComponent<?, ?> ingredientComponent, int size) {
        return (ingredientComponent == IngredientComponents.FLUIDSTACK && size == 1)
                || (ingredientComponent == MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK && size == 1);
    }

    @Override
    public Collection<IRecipeDefinition> getRecipesUncached() {
        Collection<IRecipeDefinition> list = Lists.newArrayList();
        this.fluidToChemical = true;
        list.addAll(super.getRecipes());
        this.fluidToChemical = false;
        list.addAll(super.getRecipes());
        return list;
    }

    @Override
    protected boolean isValid(RecipeHolder<? extends RotaryRecipe> recipeHolder) {
        return super.isValid(recipeHolder) && (this.fluidToChemical ? recipeHolder.value().hasFluidToChemical() : recipeHolder.value().hasChemicalToFluid());
    }

    @Override
    protected void recipeToInputs(RotaryRecipe recipe, Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs) {
        if (recipe.hasFluidToChemical() && this.fluidToChemical) {
            inputs.put(IngredientComponents.FLUIDSTACK, List.of(
                    new PrototypedIngredientAlternativesList<>(getPrototypesFromFluidIngredient(recipe.getFluidInput()))
            ));
        }
        if (recipe.hasChemicalToFluid() && !this.fluidToChemical) {
            inputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, List.of(
                    new PrototypedIngredientAlternativesList<>(getPrototypesFromChemicalIngredient(recipe.getChemicalInput()))
            ));
        }
    }

    @Override
    protected void recipeToOutputs(RotaryRecipe recipe, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        if (recipe.hasFluidToChemical() && this.fluidToChemical) {
            outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(
                    recipe.getChemicalOutputDefinition().get(0)
            ));
        }
        if (recipe.hasChemicalToFluid() && !this.fluidToChemical) {
            outputs.put(IngredientComponents.FLUIDSTACK, Lists.newArrayList(
                    recipe.getFluidOutputDefinition().get(0)
            ));
        }
    }

    @Override
    protected void recipeToOutputsSimulated(RotaryRecipe recipe, IMixedIngredients input, Map<IngredientComponent<?, ?>, List<?>> outputs) {
        if (recipe.hasFluidToChemical() && this.fluidToChemical) {
            outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(
                    recipe.getChemicalOutput(input.getInstances(IngredientComponents.FLUIDSTACK).get(0))
            ));
        }
        if (recipe.hasChemicalToFluid() && !this.fluidToChemical) {
            outputs.put(IngredientComponents.FLUIDSTACK, Lists.newArrayList(
                    recipe.getFluidOutput(input.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0))
            ));
        }
    }

    @Override
    protected boolean doesRecipeMatchInput(RotaryRecipe recipe, IMixedIngredients input) {
        return recipe.getFluidInput().test(input.getFirstNonEmpty(IngredientComponents.FLUIDSTACK))
                || ((ChemicalStackIngredient) recipe.getChemicalInput()).test(input.getFirstNonEmpty(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK));
    }
}
