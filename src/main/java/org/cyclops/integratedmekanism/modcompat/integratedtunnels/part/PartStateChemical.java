package org.cyclops.integratedmekanism.modcompat.integratedtunnels.part;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.PositionedAddonsNetworkIngredientsFilter;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integratedmekanism.Capabilities;
import org.cyclops.integratedmekanism.GeneralConfig;
import org.cyclops.integratedmekanism.network.IChemicalNetwork;
import org.cyclops.integratedtunnels.core.TunnelHelpers;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;

import java.util.Optional;

/**
 * @author rubensworks
 */
public class PartStateChemical<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IChemicalNetwork, ChemicalStack> {

    public PartStateChemical(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    @Override
    public <T> Optional<T> getCapability(P partType, PartCapability<T> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
        if (capability == Capabilities.ChemicalHandler.PART) {
            return (Optional<T>) Optional.of(new Handler(this));
        }
        return super.getCapability(partType, capability, network, partNetwork, target);
    }
    public static class Handler implements IChemicalHandler {

        private final PartStateChemical partState;

        public Handler(PartStateChemical partState) {
            this.partState = partState;
        }

        protected IChemicalHandler getChemicalHandler() {
            return ((IChemicalNetwork) partState.getPositionedAddonsNetwork()).getChannelExternal(mekanism.common.capabilities.Capabilities.CHEMICAL.block(), TunnelHelpers.getPassiveInteractionChannel(partState));
        }

        @Override
        public int getChemicalTanks() {
            return partState.getPositionedAddonsNetwork() != null && partState.getStorageFilter() != null ? getChemicalHandler().getChemicalTanks() : 0;
        }

        @Override
        public ChemicalStack getChemicalInTank(int tank) {
            if (partState.getPositionedAddonsNetwork() != null && partState.getStorageFilter() != null) {
                ChemicalStack chemicalStack = getChemicalHandler().getChemicalInTank(tank);
                if (partState.getStorageFilter().testView(chemicalStack)) {
                    return chemicalStack;
                }
            }
            return getEmptyStack();
        }

        @Override
        public void setChemicalInTank(int tank, ChemicalStack chemicalStack) {
            if (partState.getPositionedAddonsNetwork() != null && partState.getStorageFilter() != null) {
                if (partState.getStorageFilter().testView(chemicalStack)) {
                    getChemicalHandler().setChemicalInTank(tank, chemicalStack);
                }
            }
        }

        @Override
        public long getChemicalTankCapacity(int tank) {
            return partState.getPositionedAddonsNetwork() != null && partState.getStorageFilter() != null ? getChemicalHandler().getChemicalTankCapacity(tank) : 0;
        }

        @Override
        public boolean isValid(int tank, ChemicalStack chemicalStack) {
            return partState.getPositionedAddonsNetwork() != null && partState.getStorageFilter() != null && partState.getStorageFilter().testInsertion(chemicalStack) && getChemicalHandler().isValid(tank, chemicalStack);
        }

        protected ChemicalStack rateLimitFluid(ChemicalStack chemicalStack) {
            if (chemicalStack != null && chemicalStack.getAmount() > GeneralConfig.chemicalRateLimit) {
                chemicalStack = (ChemicalStack) chemicalStack.copy();
                chemicalStack.setAmount(GeneralConfig.chemicalRateLimit);
                return chemicalStack;
            }
            return chemicalStack;
        }

        @Override
        public ChemicalStack insertChemical(int tank, ChemicalStack chemicalStack, Action action) {
            return partState.canReceive() && partState.getPositionedAddonsNetwork() != null && partState.getStorageFilter() != null && partState.getStorageFilter().testInsertion(chemicalStack) ? getChemicalHandler().insertChemical(rateLimitFluid(chemicalStack), action) : chemicalStack;
        }

        @Override
        public ChemicalStack extractChemical(int tank, long maxDrain, Action action) {
            if (partState.canExtract() && partState.getPositionedAddonsNetwork() != null && partState.getStorageFilter() != null) {
                PositionedAddonsNetworkIngredientsFilter<ChemicalStack> filter = partState.getStorageFilter();

                // If we do an effective extraction, first simulate to check if it matches the filter
                if (action.execute()) {
                    ChemicalStack drainedSimulated = getChemicalHandler().extractChemical(Math.min(maxDrain, GeneralConfig.chemicalRateLimit), Action.SIMULATE);
                    if (!filter.testExtraction(drainedSimulated)) {
                        return getEmptyStack();
                    }
                }

                ChemicalStack drained = getChemicalHandler().extractChemical(Math.min(maxDrain, GeneralConfig.chemicalRateLimit), action);

                // If simulating, just check the output
                if (action.simulate() && !filter.testExtraction(drained)) {
                    return getEmptyStack();
                }

                return drained;
            }
            return getEmptyStack();
        }

        public static ChemicalStack getEmptyStack() {
            return ChemicalStack.EMPTY;
        }
    }
}
