package org.cyclops.integratedmekanismics.modcompat.integratedterminals.sorter;

import mekanism.api.chemical.ChemicalStack;
import org.cyclops.integratedterminals.capability.ingredient.sorter.IngredientInstanceSorterAdapter;
import org.cyclops.integratedterminals.client.gui.image.Images;

/**
 * Sorts chemicals by display name.
 * @author rubensworks
 */
public class ChemicalStackNameSorter extends IngredientInstanceSorterAdapter<ChemicalStack<?>> {

    public ChemicalStackNameSorter() {
        super(Images.BUTTON_MIDDLE_NAME, "chemicalstack", "name");
    }

    @Override
    public int compare(ChemicalStack<?> o1, ChemicalStack<?> o2) {
        return o1.getTextComponent().getString().compareTo(o2.getTextComponent().getString());
    }
}
