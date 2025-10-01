package org.cyclops.integratedmekanism.modcompat.integratedtunnels.part;

import com.google.common.collect.Lists;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.NetworkCapability;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedmekanism.GeneralConfig;
import org.cyclops.integratedmekanism.IntegratedMekanism;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.aspect.MekanismTunnelsAspects;
import org.cyclops.integratedmekanism.network.IChemicalNetwork;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddonFiltering;

import java.util.Optional;

/**
 * Interface for filtering fluid handlers.
 * @author rubensworks
 */
public class PartTypeInterfaceFilteringChemical extends PartTypeInterfacePositionedAddonFiltering<IChemicalNetwork, IChemicalHandler, PartTypeInterfaceFilteringChemical, PartTypeInterfaceFilteringChemical.State> {
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
        return IntegratedMekanism._instance;
    }

    @Override
    public NetworkCapability<IChemicalNetwork> getNetworkCapability() {
        return org.cyclops.integratedmekanism.Capabilities.ChemicalNetwork.NETWORK;
    }

    @Override
    public PartCapability<IChemicalHandler> getPartCapability() {
        return org.cyclops.integratedmekanism.Capabilities.ChemicalHandler.PART;
    }

    @Override
    public BlockCapability<IChemicalHandler, Direction> getBlockCapability() {
        return Capabilities.CHEMICAL.block();
    }

    @Override
    protected PartTypeInterfaceFilteringChemical.State constructDefaultState() {
        return new PartTypeInterfaceFilteringChemical.State(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceChemicalBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddonFiltering.State<IChemicalNetwork, IChemicalHandler, PartTypeInterfaceFilteringChemical, PartTypeInterfaceFilteringChemical.State> {

        public State(int inventorySize) {
            super(inventorySize);
        }

        @Override
        public PartCapability<IChemicalHandler> getTargetCapability() {
            return org.cyclops.integratedmekanism.Capabilities.ChemicalHandler.PART;
        }

        @Override
        public IChemicalHandler getCapabilityInstance() {
            return new ChemicalHandlerPartState(this);
        }

        @Override
        public <T> Optional<T> getCapability(PartTypeInterfaceFilteringChemical partType, PartCapability<T> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
            return this.isNetworkAndPositionValid() && capability == getTargetCapability() ? Optional.of((T) this.getCapabilityInstance()) : super.getCapability(partType, capability, network, partNetwork, target);
        }
    }
}
