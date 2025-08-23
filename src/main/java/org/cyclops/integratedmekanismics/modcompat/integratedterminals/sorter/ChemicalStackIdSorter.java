package org.cyclops.integratedmekanismics.modcompat.integratedterminals.sorter;

import mekanism.api.chemical.ChemicalStack;
import org.cyclops.integratedmekanismics.core.ChemicalHelpers;
import org.cyclops.integratedterminals.capability.ingredient.sorter.IngredientInstanceSorterAdapter;
import org.cyclops.integratedterminals.client.gui.image.Images;

/**
 * Sorts chemicals by internal ID.
 * @author rubensworks
 */
public class ChemicalStackIdSorter extends IngredientInstanceSorterAdapter<ChemicalStack<?>> {

    public ChemicalStackIdSorter() {
        super(Images.BUTTON_MIDDLE_ID, "chemicalstack", "id");
    }

    protected String getChemicalStackId(ChemicalStack<?> chemicalStack) {
        return ChemicalHelpers.getStackRegistry(chemicalStack).getKey(chemicalStack.getType()).toString();
    }

    @Override
    public int compare(ChemicalStack<?> o1, ChemicalStack<?> o2) {
        return getChemicalStackId(o1).compareTo(getChemicalStackId(o2));
    }
}
