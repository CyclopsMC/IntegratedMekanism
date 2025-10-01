package org.cyclops.integratedmekanism.core;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;

/**
 * @author rubensworks
 */
public class EmptyChemicalHandler implements IChemicalHandler {

    public static final EmptyChemicalHandler INSTANCE = new EmptyChemicalHandler();

    @Override
    public int getChemicalTanks() {
        return 0;
    }

    @Override
    public ChemicalStack getChemicalInTank(int i) {
        return ChemicalStack.EMPTY;
    }

    @Override
    public void setChemicalInTank(int i, ChemicalStack stack) {

    }

    @Override
    public long getChemicalTankCapacity(int i) {
        return 0;
    }

    @Override
    public boolean isValid(int i, ChemicalStack stack) {
        return false;
    }

    @Override
    public ChemicalStack insertChemical(int i, ChemicalStack stack, Action action) {
        return stack;
    }

    @Override
    public ChemicalStack extractChemical(int i, long l, Action action) {
        return ChemicalStack.EMPTY;
    }
}
