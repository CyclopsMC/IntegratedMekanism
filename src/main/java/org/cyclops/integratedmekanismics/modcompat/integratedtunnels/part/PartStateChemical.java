package org.cyclops.integratedmekanismics.modcompat.integratedtunnels.part;

import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.gas.GasStack;
import mekanism.common.capabilities.Capabilities;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.PositionedAddonsNetworkIngredientsFilter;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integratedmekanismics.GeneralConfig;
import org.cyclops.integratedmekanismics.network.IChemicalNetwork;
import org.cyclops.integratedtunnels.core.TunnelHelpers;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;
import org.jetbrains.annotations.NotNull;

/**
 * @author rubensworks
 */
public class PartStateChemical<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IChemicalNetwork, ChemicalStack<?>> {

    public PartStateChemical(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    @Override
    public <T2> LazyOptional<T2> getCapability(Capability<T2> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
        if (capability == Capabilities.GAS_HANDLER) {
            return LazyOptional.of(() -> new Handler<>(this, Capabilities.GAS_HANDLER)).cast();
        }
        if (capability == Capabilities.INFUSION_HANDLER) {
            return LazyOptional.of(() -> new Handler<>(this, Capabilities.INFUSION_HANDLER)).cast();
        }
        if (capability == Capabilities.PIGMENT_HANDLER) {
            return LazyOptional.of(() -> new Handler<>(this, Capabilities.PIGMENT_HANDLER)).cast();
        }
        if (capability == Capabilities.SLURRY_HANDLER) {
            return LazyOptional.of(() -> new Handler<>(this, Capabilities.SLURRY_HANDLER)).cast();
        }
        return super.getCapability(capability, network, partNetwork, target);
    }

    public static class Handler<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> implements IChemicalHandler<CHEMICAL, STACK> {

        private final PartStateChemical<?> partState;
        private final Capability<? extends IChemicalHandler<?, ? extends STACK>> handlerCapability;

        public Handler(PartStateChemical<?> partState, Capability<? extends IChemicalHandler<?, ? extends STACK>> handlerCapability) {
            this.partState = partState;
            this.handlerCapability = handlerCapability;
        }

        protected IChemicalHandler<CHEMICAL, STACK> getChemicalHandler() {
            return (IChemicalHandler<CHEMICAL, STACK>) partState.getPositionedAddonsNetwork().getChannelExternal(handlerCapability, TunnelHelpers.getPassiveInteractionChannel(partState));
        }

        @Override
        public int getTanks() {
            return partState.getPositionedAddonsNetwork() != null && partState.getStorageFilter() != null ? getChemicalHandler().getTanks() : 0;
        }

        @Override
        public STACK getChemicalInTank(int tank) {
            if (partState.getPositionedAddonsNetwork() != null && partState.getStorageFilter() != null) {
                STACK chemicalStack = getChemicalHandler().getChemicalInTank(tank);
                if (partState.getStorageFilter().testView(chemicalStack)) {
                    return chemicalStack;
                }
            }
            return getEmptyStack();
        }

        @Override
        public void setChemicalInTank(int tank, STACK chemicalStack) {
            if (partState.getPositionedAddonsNetwork() != null && partState.getStorageFilter() != null) {
                if (partState.getStorageFilter().testView(chemicalStack)) {
                    getChemicalHandler().setChemicalInTank(tank, chemicalStack);
                }
            }
        }

        @Override
        public long getTankCapacity(int tank) {
            return partState.getPositionedAddonsNetwork() != null && partState.getStorageFilter() != null ? getChemicalHandler().getTankCapacity(tank) : 0;
        }

        @Override
        public boolean isValid(int tank, STACK chemicalStack) {
            return partState.getPositionedAddonsNetwork() != null && partState.getStorageFilter() != null && partState.getStorageFilter().testInsertion(chemicalStack) && getChemicalHandler().isValid(tank, chemicalStack);
        }

        protected STACK rateLimitFluid(STACK chemicalStack) {
            if (chemicalStack != null && chemicalStack.getAmount() > GeneralConfig.chemicalRateLimit) {
                chemicalStack = (STACK) chemicalStack.copy();
                chemicalStack.setAmount(GeneralConfig.chemicalRateLimit);
                return chemicalStack;
            }
            return chemicalStack;
        }

        @Override
        public STACK insertChemical(int tank, STACK chemicalStack, Action action) {
            return partState.canReceive() && partState.getPositionedAddonsNetwork() != null && partState.getStorageFilter() != null && partState.getStorageFilter().testInsertion(chemicalStack) ? getChemicalHandler().insertChemical(rateLimitFluid(chemicalStack), action) : chemicalStack;
        }

        @Override
        public STACK extractChemical(int tank, long maxDrain, Action action) {
            if (partState.canExtract() && partState.getPositionedAddonsNetwork() != null && partState.getStorageFilter() != null) {
                PositionedAddonsNetworkIngredientsFilter<ChemicalStack<?>> filter = partState.getStorageFilter();

                // If we do an effective extraction, first simulate to check if it matches the filter
                if (action.execute()) {
                    STACK drainedSimulated = getChemicalHandler().extractChemical(Math.min(maxDrain, GeneralConfig.chemicalRateLimit), Action.SIMULATE);
                    if (!filter.testExtraction(drainedSimulated)) {
                        return getEmptyStack();
                    }
                }

                STACK drained = getChemicalHandler().extractChemical(Math.min(maxDrain, GeneralConfig.chemicalRateLimit), action);

                // If simulating, just check the output
                if (action.simulate() && !filter.testExtraction(drained)) {
                    return getEmptyStack();
                }

                return drained;
            }
            return getEmptyStack();
        }

        @Override
        public @NotNull STACK getEmptyStack() {
            return (STACK) GasStack.EMPTY;
        }
    }
}
