package org.cyclops.integratedmekanismics.modcompat.integratedtunnels.part;

import com.google.common.collect.Lists;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedmekanismics.GeneralConfig;
import org.cyclops.integratedmekanismics.IntegratedMekanismics;
import org.cyclops.integratedmekanismics.core.CapabilityHelpers;
import org.cyclops.integratedmekanismics.network.ChemicalNetworkConfig;
import org.cyclops.integratedmekanismics.network.IChemicalNetwork;
import org.cyclops.integratedmekanismics.modcompat.integratedtunnels.aspect.MekanismTunnelsAspects;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddonFiltering;

import java.util.List;

/**
 * Interface for filtering fluid handlers.
 * @author rubensworks
 */
public class PartTypeInterfaceFilteringChemical extends PartTypeInterfacePositionedAddonFiltering<IChemicalNetwork, IChemicalHandler<?, ?>, PartTypeInterfaceFilteringChemical, PartTypeInterfaceFilteringChemical.State> {
    public PartTypeInterfaceFilteringChemical(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                MekanismTunnelsAspects.Write.ChemicalFilter.BOOLEAN_SET_FILTER,
                MekanismTunnelsAspects.Write.ChemicalFilter.CHEMICALSTACK_SET_FILTER,
                MekanismTunnelsAspects.Write.ChemicalFilter.LIST_SET_FILTER,
                MekanismTunnelsAspects.Write.ChemicalFilter.PREDICATE_SET_FILTER
        ));
    }

    @Override
    public ModBase getMod() {
        return IntegratedMekanismics._instance;
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
    protected PartTypeInterfaceFilteringChemical.State constructDefaultState() {
        return new PartTypeInterfaceFilteringChemical.State(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceChemicalBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddonFiltering.State<IChemicalNetwork, IChemicalHandler<?, ?>, PartTypeInterfaceFilteringChemical, PartTypeInterfaceFilteringChemical.State> {

        public State(int inventorySize) {
            super(inventorySize);
        }

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
