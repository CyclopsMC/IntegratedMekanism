package org.cyclops.integratedmekanismics.core;

import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;

/**
 * @author rubensworks
 */
public class EmptyChemicalHandler implements IGasHandler {

    public static final EmptyChemicalHandler INSTANCE = new EmptyChemicalHandler();

    @Override
    public int getTanks() {
        return 0;
    }

    @Override
    public GasStack getChemicalInTank(int i) {
        return getEmptyStack();
    }

    @Override
    public void setChemicalInTank(int i, GasStack stack) {

    }

    @Override
    public long getTankCapacity(int i) {
        return 0;
    }

    @Override
    public boolean isValid(int i, GasStack stack) {
        return false;
    }

    @Override
    public GasStack insertChemical(int i, GasStack stack, Action action) {
        return stack;
    }

    @Override
    public GasStack extractChemical(int i, long l, Action action) {
        return getEmptyStack();
    }
}
