package org.cyclops.integratedmekanismics.capability.recipehandler;

import com.google.common.collect.Maps;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.ingredient.creator.ItemStackIngredientCreator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.cyclops.commoncapabilities.IngredientComponents;
import org.cyclops.commoncapabilities.api.capability.fluidhandler.FluidMatch;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ItemMatch;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IPrototypedIngredientAlternatives;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import org.cyclops.commoncapabilities.api.capability.recipehandler.RecipeDefinition;
import org.cyclops.commoncapabilities.api.ingredient.*;
import org.cyclops.commoncapabilities.modcompat.vanilla.capability.recipehandler.VanillaRecipeTypeRecipeHandler;
import org.cyclops.integratedmekanismics.ingredient.MekanismIngredientComponents;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author rubensworks
 */
public abstract class MekanismRecipeHandler<R extends MekanismRecipe> implements IRecipeHandler {

    private final IMekanismRecipeTypeProvider<? extends R, ?> recipeType;
    private final Supplier<Level> levelSupplier;
    private final Set<IngredientComponent<?, ?>> inputComponents;
    private final Set<IngredientComponent<?, ?>> outputComponents;

    public static Map<Class<?>, Collection<IRecipeDefinition>> CACHED_RECIPES = Maps.newHashMap();

    protected MekanismRecipeHandler(IMekanismRecipeTypeProvider<? extends R, ?> recipeType, Supplier<Level> levelSupplier, Set<IngredientComponent<?, ?>> inputComponents, Set<IngredientComponent<?, ?>> outputComponents) {
        this.recipeType = recipeType;
        this.levelSupplier = levelSupplier;
        this.inputComponents = inputComponents;
        this.outputComponents = outputComponents;
    }

    public IMekanismRecipeTypeProvider<? extends R,?> getRecipeType() {
        return recipeType;
    }

    public Level getLevel() {
        return levelSupplier.get();
    }

    @Override
    public Set<IngredientComponent<?, ?>> getRecipeInputComponents() {
        return inputComponents;
    }

    @Override
    public Set<IngredientComponent<?, ?>> getRecipeOutputComponents() {
        return outputComponents;
    }

    @Override
    public final Collection<IRecipeDefinition> getRecipes() {
        Collection<IRecipeDefinition> cached = CACHED_RECIPES.get(getClass());
        if (cached == null) {
            cached = getRecipesUncached();
            CACHED_RECIPES.put(getClass(), cached);
        }
        return cached;
    }

    public Collection<IRecipeDefinition> getRecipesUncached() {
        return recipeType.getRecipes(getLevel()).stream()
                .filter(recipe -> isValid(recipe))
                .<IRecipeDefinition>map(recipe -> {
                    Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs = Maps.newIdentityHashMap();
                    recipeToInputs(recipe, inputs);
                    Map<IngredientComponent<?, ?>, List<?>> outputs = Maps.newIdentityHashMap();
                    recipeToOutputs(recipe, outputs);
                    return new RecipeDefinition(inputs, new MixedIngredients(outputs));
                })
                .toList();
    }

    protected boolean isValid(R recipe) {
        return true;
    }

    @Override
    public @Nullable IMixedIngredients simulate(IMixedIngredients input) {
        for (IngredientComponent<?, ?> recipeInputComponent : getRecipeInputComponents()) {
            if (!isValidSizeInput(recipeInputComponent, input.getInstances(recipeInputComponent).size())) {
                return null;
            }
        }

        @Nullable R recipe = recipeType.findFirst(getLevel(), (pRecipe) -> doesRecipeMatchInput(pRecipe, input));

        if (recipe != null) {
            Map<IngredientComponent<?, ?>, List<?>> outputs = Maps.newIdentityHashMap();
            recipeToOutputsSimulated(recipe, input, outputs);
            return new MixedIngredients(outputs);
        }

        return null;
    }

    protected abstract void recipeToInputs(R recipe, Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs);

    protected abstract void recipeToOutputs(R recipe, Map<IngredientComponent<?, ?>, List<?>> outputs);

    protected abstract void recipeToOutputsSimulated(R recipe, IMixedIngredients input, Map<IngredientComponent<?, ?>, List<?>> outputs);

    protected abstract boolean doesRecipeMatchInput(R recipe, IMixedIngredients input);

    public static List<IPrototypedIngredient<ChemicalStack<?>, Integer>> getPrototypesFromChemicalIngredient(ChemicalStackIngredient<?, ?> ingredient) {
        return ((List<ChemicalStack<?>>) ingredient.getRepresentations()).stream()
                .map(stack -> new PrototypedIngredient<>(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, stack, ItemMatch.ITEM))
                .collect(Collectors.toList());
    }

    public static List<IPrototypedIngredient<ItemStack, Integer>> getPrototypesFromItemIngredient(InputIngredient<ItemStack> ingredient) {
        if (ingredient instanceof ItemStackIngredientCreator.SingleItemStackIngredient ingredientCast) {
            return VanillaRecipeTypeRecipeHandler.getPrototypesFromIngredient(ingredientCast.getInputRaw());
        }
        return ingredient.getRepresentations().stream()
                .map(stack -> new PrototypedIngredient<>(IngredientComponents.ITEMSTACK, stack, ItemMatch.ITEM))
                .collect(Collectors.toList());
    }

    public static List<IPrototypedIngredient<FluidStack, Integer>> getPrototypesFromFluidIngredient(InputIngredient<FluidStack> ingredient) {
        return ingredient.getRepresentations().stream()
                .map(stack -> new PrototypedIngredient<>(IngredientComponents.FLUIDSTACK, stack, FluidMatch.FLUID))
                .collect(Collectors.toList());
    }
}
