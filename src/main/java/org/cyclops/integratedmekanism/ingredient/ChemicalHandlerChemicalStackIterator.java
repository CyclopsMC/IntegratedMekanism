package org.cyclops.integratedmekanism.ingredient;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator over all slots in a chemical handler.
 * @author rubensworks
 */
public class ChemicalHandlerChemicalStackIterator implements Iterator<ChemicalStack> {

    private final IChemicalHandler chemicalHandler;
    private int slot;

    public ChemicalHandlerChemicalStackIterator(IChemicalHandler chemicalHandler, int offset) {
        this.chemicalHandler = chemicalHandler;
        this.slot = offset;
    }

    public ChemicalHandlerChemicalStackIterator(IChemicalHandler chemicalHandler) {
        this(chemicalHandler, 0);
    }

    @Override
    public boolean hasNext() {
        return slot < chemicalHandler.getChemicalTanks();
    }

    @Override
    public ChemicalStack next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Slot out of bounds");
        }
        return chemicalHandler.getChemicalInTank(slot++);
    }
}
