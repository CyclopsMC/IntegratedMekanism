package org.cyclops.integratedmekanism.ingredient;

import mekanism.api.chemical.ChemicalStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.integrateddynamics.api.ingredient.IIngredientComponentHandler;
import org.cyclops.integratedmekanism.value.MekanismValueTypes;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;
import org.jetbrains.annotations.Nullable;

/**
 * @author rubensworks
 */
public class IngredientComponentHandlerChemical implements IIngredientComponentHandler<ValueObjectTypeChemicalStack, ValueObjectTypeChemicalStack.ValueChemicalStack, ChemicalStack, Integer> {

    @Override
    public ValueObjectTypeChemicalStack getValueType() {
        return MekanismValueTypes.OBJECT_CHEMICALSTACK;
    }

    @Override
    public IngredientComponent<ChemicalStack, Integer> getComponent() {
        return MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK;
    }

    @Override
    public ValueObjectTypeChemicalStack.ValueChemicalStack toValue(@Nullable ChemicalStack chemicalStack) {
        return ValueObjectTypeChemicalStack.ValueChemicalStack.of(chemicalStack);
    }

    @Override
    public @Nullable ChemicalStack toInstance(ValueObjectTypeChemicalStack.ValueChemicalStack valueChemicalStack) {
        return valueChemicalStack.getRawValue();
    }
}
