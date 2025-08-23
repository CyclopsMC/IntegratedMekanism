package org.cyclops.integratedmekanismics.ingredient;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator over all slots in a chemical handler.
 * @author rubensworks
 */
public class ChemicalHandlerChemicalStackIterator<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> implements Iterator<STACK> {

    private final IChemicalHandler<CHEMICAL, STACK> chemicalHandler;
    private int slot;

    public ChemicalHandlerChemicalStackIterator(IChemicalHandler<CHEMICAL, STACK> chemicalHandler, int offset) {
        this.chemicalHandler = chemicalHandler;
        this.slot = offset;
    }

    public ChemicalHandlerChemicalStackIterator(IChemicalHandler<CHEMICAL, STACK> chemicalHandler) {
        this(chemicalHandler, 0);
    }

    @Override
    public boolean hasNext() {
        return slot < chemicalHandler.getTanks();
    }

    @Override
    public STACK next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Slot out of bounds");
        }
        return chemicalHandler.getChemicalInTank(slot++);
    }
}
