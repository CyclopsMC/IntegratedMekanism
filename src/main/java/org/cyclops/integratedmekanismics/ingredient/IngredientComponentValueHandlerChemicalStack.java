package org.cyclops.integratedmekanismics.ingredient;

import mekanism.api.chemical.ChemicalStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.integrateddynamics.api.ingredient.capability.IIngredientComponentValueHandler;
import org.cyclops.integratedmekanismics.value.ValueObjectTypeChemicalStack;
import org.cyclops.integratedmekanismics.value.MekanismValueTypes;
import org.jetbrains.annotations.Nullable;

/**
 * @author rubensworks
 */
public class IngredientComponentValueHandlerChemicalStack implements IIngredientComponentValueHandler<ValueObjectTypeChemicalStack,
        ValueObjectTypeChemicalStack.ValueChemicalStack, ChemicalStack<?>, Integer> {

    private final IngredientComponent<ChemicalStack<?>, Integer> ingredientComponent;

    public IngredientComponentValueHandlerChemicalStack(IngredientComponent<ChemicalStack<?>, Integer> ingredientComponent) {
        this.ingredientComponent = ingredientComponent;
    }

    @Override
    public ValueObjectTypeChemicalStack getValueType() {
        return MekanismValueTypes.OBJECT_CHEMICALSTACK;
    }

    @Override
    public IngredientComponent<ChemicalStack<?>, Integer> getComponent() {
        return this.ingredientComponent;
    }

    @Override
    public ValueObjectTypeChemicalStack.ValueChemicalStack toValue(@Nullable ChemicalStack<?> chemicalStack) {
        return ValueObjectTypeChemicalStack.ValueChemicalStack.of(chemicalStack);
    }

    @Override
    public @Nullable ChemicalStack<?> toInstance(ValueObjectTypeChemicalStack.ValueChemicalStack valueChemicalStack) {
        return valueChemicalStack.getRawValue();
    }
}
