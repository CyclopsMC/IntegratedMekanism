package org.cyclops.integratedmekanismics.network;

import mekanism.api.chemical.ChemicalStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.integrateddynamics.core.network.PositionedAddonsNetworkIngredients;
import org.cyclops.integratedmekanismics.GeneralConfig;

/**
 * @author rubensworks
 */
public class ChemicalNetwork extends PositionedAddonsNetworkIngredients<ChemicalStack<?>, Integer> implements IChemicalNetwork {
    public ChemicalNetwork(IngredientComponent<ChemicalStack<?>, Integer> component) {
        super(component);
    }

    @Override
    public long getRateLimit() {
        return GeneralConfig.chemicalRateLimit;
    }
}
