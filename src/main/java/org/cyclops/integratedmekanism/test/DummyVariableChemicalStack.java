package org.cyclops.integratedmekanism.test;


import org.cyclops.integratedmekanism.value.MekanismValueTypes;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;

/**
 * Dummy chemicalstack variable.
 * @author rubensworks
 */
public class DummyVariableChemicalStack extends DummyVariable<ValueObjectTypeChemicalStack.ValueChemicalStack> {

    public DummyVariableChemicalStack(ValueObjectTypeChemicalStack.ValueChemicalStack value) {
        super(MekanismValueTypes.OBJECT_CHEMICALSTACK, value);
    }

    public DummyVariableChemicalStack() {
        super(MekanismValueTypes.OBJECT_CHEMICALSTACK);
    }

}
