package org.cyclops.integratedmekanism.modcompat.integratedtunnels.part;

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
import org.cyclops.integratedmekanism.GeneralConfig;
import org.cyclops.integratedmekanism.IntegratedMekanism;
import org.cyclops.integratedmekanism.network.IChemicalNetwork;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;

import java.util.Optional;

/**
 * Interface for chemical handlers.
 * @author rubensworks
 */
public class PartTypeInterfaceChemical extends PartTypeInterfacePositionedAddon<IChemicalNetwork, IChemicalHandler, PartTypeInterfaceChemical, PartTypeInterfaceChemical.State> {

    public PartTypeInterfaceChemical(String name) {
        super(name);
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
    protected PartTypeInterfaceChemical.State constructDefaultState() {
        return new PartTypeInterfaceChemical.State();
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceChemicalBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddon.State<IChemicalNetwork, IChemicalHandler, PartTypeInterfaceChemical, PartTypeInterfaceChemical.State> {

        @Override
        public PartCapability<IChemicalHandler> getTargetCapability() {
            return org.cyclops.integratedmekanism.Capabilities.ChemicalHandler.PART;
        }

        @Override
        public IChemicalHandler getCapabilityInstance() {
            return new ChemicalHandlerPartState(this);
        }

        @Override
        public <T> Optional<T> getCapability(PartTypeInterfaceChemical partType, PartCapability<T> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
            return this.isNetworkAndPositionValid() && capability == getTargetCapability() ? Optional.of((T) this.getCapabilityInstance()) : super.getCapability(partType, capability, network, partNetwork, target);
        }
    }

}
