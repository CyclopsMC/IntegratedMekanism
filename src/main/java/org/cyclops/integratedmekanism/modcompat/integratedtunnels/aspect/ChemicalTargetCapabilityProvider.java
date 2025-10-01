package org.cyclops.integratedmekanism.modcompat.integratedtunnels.aspect;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.Direction;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.capability.ICapabilityGetter;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageSlotted;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedmekanism.Capabilities;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanism.network.IChemicalNetwork;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.part.aspect.*;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class ChemicalTargetCapabilityProvider extends ChanneledTargetCapabilityProvider<IChemicalHandler, IChemicalNetwork, ChemicalStack, Integer>
        implements IChemicalTarget {

    private final ITunnelConnection connection;
    private final PartTarget partTarget;
    private final IngredientPredicate<ChemicalStack, Integer> chemicalStackMatcher;
    private final IAspectProperties properties;

    public ChemicalTargetCapabilityProvider(ITunnelTransfer transfer, INetwork network,
                                            Class<?> capabilityType, @Nullable ICapabilityGetter<Direction> capabilityGetter, Object capabilityProvider,
                                            Direction side, IngredientPredicate<ChemicalStack, Integer> chemicalStackMatcher,
                                            PartTarget partTarget, IAspectProperties properties,
                                            @Nullable PartStateRoundRobin<?> partState) {
        super(network, capabilityType, capabilityGetter, side, network.getCapability(Capabilities.ChemicalNetwork.NETWORK).orElse(null), partState,
                properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_CRAFT).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO).getRawValue());
        this.connection = new TunnelConnectionPositionedNetworkCapabilityProvider(network, getChannel(), partTarget.getTarget(), transfer, capabilityProvider);
        this.chemicalStackMatcher = chemicalStackMatcher;
        this.partTarget = partTarget;
        this.properties = properties;
    }

    @Override
    public PartTarget getPartTarget() {
        return partTarget;
    }

    @Override
    public IIngredientComponentStorage<ChemicalStack, Integer> getChemicalChannel() {
        return getChanneledNetwork().getChannel(getChannel());
    }

    @Override
    public IIngredientComponentStorageSlotted<ChemicalStack, Integer> getChemicalChannelSlotted() {
        return getChanneledNetwork().getChannelSlotted(getChannel());
    }

    @Override
    public IngredientPredicate<ChemicalStack, Integer> getChemicalStackMatcher() {
        return chemicalStackMatcher;
    }

    @Override
    public IAspectProperties getProperties() {
        return properties;
    }

    @Override
    public ITunnelConnection getConnection() {
        return connection;
    }

    @Override
    protected IngredientComponent<ChemicalStack, Integer> getComponent() {
        return MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK;
    }
}
