package org.cyclops.integratedmekanism.network;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.datamaps.chemical.attribute.ChemicalRadioactivity;
import mekanism.api.radiation.IRadiationManager;
import mekanism.common.registries.MekanismDataMapTypes;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.integrateddynamics.api.network.PositionedAddonsNetworkIngredientsFilter;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.network.PositionedAddonsNetworkIngredients;
import org.cyclops.integratedmekanism.GeneralConfig;
import org.jetbrains.annotations.Nullable;

/**
 * @author rubensworks
 */
public class ChemicalNetwork extends PositionedAddonsNetworkIngredients<ChemicalStack, Integer> implements IChemicalNetwork {

    public ChemicalNetwork(IngredientComponent<ChemicalStack, Integer> component) {
        super(component);
    }

    @Override
    public long getRateLimit() {
        return GeneralConfig.chemicalRateLimit;
    }

    @Override
    public @Nullable PositionedAddonsNetworkIngredientsFilter<ChemicalStack> getPositionedStorageFilter(PartPos pos) {
        // Do not allow radioactive chemicals to be passed if config option is enabled.
        PositionedAddonsNetworkIngredientsFilter<ChemicalStack> superFilter = super.getPositionedStorageFilter(pos);
        if (GeneralConfig.transferRadioactiveChemicals || !IRadiationManager.INSTANCE.isRadiationEnabled()) {
            return superFilter;
        } else {
            return new PositionedAddonsNetworkIngredientsFilter<>(
                    (chemicalStack) -> {
                        @Nullable ChemicalRadioactivity attribute = chemicalStack.getData(MekanismDataMapTypes.INSTANCE.chemicalRadioactivity());
                        return (attribute == null || attribute.radioactivity() == 0)
                                && (superFilter == null || superFilter.getFilter().test(chemicalStack));
                    },
                    true, true, superFilter == null || superFilter.isAllowAllIfFilterNotApplied());
        }
    }
}
