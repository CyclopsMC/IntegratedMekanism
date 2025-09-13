package org.cyclops.integratedmekanism.modcompat.integratedtunnels.part;

import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integratedmekanism.GeneralConfig;
import org.cyclops.integratedmekanism.IntegratedMekanism;
import org.cyclops.integratedmekanism.core.CapabilityHelpers;
import org.cyclops.integratedmekanism.network.ChemicalNetworkConfig;
import org.cyclops.integratedmekanism.network.IChemicalNetwork;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;

import java.util.List;

/**
 * Interface for chemical handlers.
 * @author rubensworks
 */
public class PartTypeInterfaceChemical extends PartTypeInterfacePositionedAddon<IChemicalNetwork, IChemicalHandler<?, ?>, PartTypeInterfaceChemical, PartTypeInterfaceChemical.State> {

    public PartTypeInterfaceChemical(String name) {
        super(name);
    }

    @Override
    public ModBase getMod() {
        return IntegratedMekanism._instance;
    }

    @Override
    public Capability<IChemicalNetwork> getNetworkCapability() {
        return ChemicalNetworkConfig.CAPABILITY;
    }

    @Override
    public Capability<IChemicalHandler<?, ?>> getTargetCapability() {
        throw new UnsupportedOperationException(); // This should never be called. We override the method instead.
    }

    @Override
    public LazyOptional<IChemicalHandler<?, ?>> getTargetCapabilityInstance(PartPos pos) {
        return CapabilityHelpers.getFirstOf(pos, List.of(Capabilities.GAS_HANDLER, Capabilities.INFUSION_HANDLER, Capabilities.PIGMENT_HANDLER, Capabilities.SLURRY_HANDLER));
    }

    @Override
    protected PartTypeInterfaceChemical.State constructDefaultState() {
        return new PartTypeInterfaceChemical.State();
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceChemicalBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddon.State<IChemicalNetwork, IChemicalHandler<?, ?>, PartTypeInterfaceChemical, PartTypeInterfaceChemical.State> {

        @Override
        public Capability<IChemicalHandler<?, ?>> getTargetCapability() {
            throw new UnsupportedOperationException(); // This should never be called. We override the method instead.
        }

        @Override
        public IChemicalHandler<?, ?> getCapabilityInstance() {
            throw new UnsupportedOperationException(); // This should never be called. We override the method instead.
        }

        @Override
        public <T2> LazyOptional<T2> getCapability(Capability<T2> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
            if (isNetworkAndPositionValid()) {
                if (capability == Capabilities.GAS_HANDLER) {
                    return LazyOptional.of(() -> new ChemicalHandlerPartState<>(this, Capabilities.GAS_HANDLER)).cast();
                }
                if (capability == Capabilities.INFUSION_HANDLER) {
                    return LazyOptional.of(() -> new ChemicalHandlerPartState<>(this, Capabilities.INFUSION_HANDLER)).cast();
                }
                if (capability == Capabilities.PIGMENT_HANDLER) {
                    return LazyOptional.of(() -> new ChemicalHandlerPartState<>(this, Capabilities.PIGMENT_HANDLER)).cast();
                }
                if (capability == Capabilities.SLURRY_HANDLER) {
                    return LazyOptional.of(() -> new ChemicalHandlerPartState<>(this, Capabilities.SLURRY_HANDLER)).cast();
                }
            }
            return LazyOptional.empty();
        }
    }

}
