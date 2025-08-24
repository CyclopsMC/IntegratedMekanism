package org.cyclops.integratedmekanismics.network;

import mekanism.api.chemical.ChemicalStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.network.PositionedAddonsNetworkIngredients;
import org.cyclops.integratedmekanismics.GeneralConfig;
import org.jetbrains.annotations.Nullable;

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

    @Override
    public @Nullable IIngredientComponentStorage<ChemicalStack<?>, Integer> getPositionedStorageUnsafe(PartPos pos) {
        // TODO: work with a global statically set Capability to determine the cap that will be retrieved. If none is set, use a composite over all caps. See MekanismAspectWriteBuilders PROP_IMPORT and PROP_EXPORT
        return super.getPositionedStorageUnsafe(pos); // TODO: override?
    }

    @Override
    public IIngredientComponentStorage<ChemicalStack<?>, Integer> getPositionedStorage(PartPos pos) {
        // TODO
        return super.getPositionedStorage(pos);
    }
}
