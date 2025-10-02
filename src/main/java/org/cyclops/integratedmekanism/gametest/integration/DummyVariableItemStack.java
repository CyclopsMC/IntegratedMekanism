package org.cyclops.integratedmekanism.gametest.integration;

import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;

/**
 * TODO: use ID variant in 1.21.8+
 * Dummy itemstack variable.
 * @author rubensworks
 */
public class DummyVariableItemStack extends DummyVariable<ValueObjectTypeItemStack.ValueItemStack> {

    public DummyVariableItemStack(ValueObjectTypeItemStack.ValueItemStack value) {
        super(ValueTypes.OBJECT_ITEMSTACK, value);
    }

    public DummyVariableItemStack() {
        super(ValueTypes.OBJECT_ITEMSTACK);
    }

}
