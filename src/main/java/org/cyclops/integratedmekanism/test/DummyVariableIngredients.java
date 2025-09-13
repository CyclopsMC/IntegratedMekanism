package org.cyclops.integratedmekanism.test;

import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeIngredients;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;

/**
 * TODO: use ID variant in 1.21.8+
 * Dummy ingredients variable.
 * @author rubensworks
 */
public class DummyVariableIngredients extends DummyVariable<ValueObjectTypeIngredients.ValueIngredients> {

    public DummyVariableIngredients(ValueObjectTypeIngredients.ValueIngredients value) {
        super(ValueTypes.OBJECT_INGREDIENTS, value);
    }

    public DummyVariableIngredients() {
        super(ValueTypes.OBJECT_INGREDIENTS);
    }

}
