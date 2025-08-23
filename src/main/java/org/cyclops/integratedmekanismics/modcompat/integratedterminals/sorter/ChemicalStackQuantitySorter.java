package org.cyclops.integratedmekanismics.modcompat.integratedterminals.sorter;

import mekanism.api.chemical.ChemicalStack;
import org.cyclops.integratedterminals.capability.ingredient.sorter.IngredientInstanceSorterAdapter;
import org.cyclops.integratedterminals.client.gui.image.Images;

/**
 * Sorts chemicals by amount.
 * @author rubensworks
 */
public class ChemicalStackQuantitySorter extends IngredientInstanceSorterAdapter<ChemicalStack<?>> {

    public ChemicalStackQuantitySorter() {
        super(Images.BUTTON_MIDDLE_QUANTITY, "chemicalstack", "quantity");
    }

    @Override
    public int compare(ChemicalStack<?> o1, ChemicalStack<?> o2) {
        return -Long.compare(o2.getAmount(), o1.getAmount());
    }
}
