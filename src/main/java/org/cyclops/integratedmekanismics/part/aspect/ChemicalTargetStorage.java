package org.cyclops.integratedmekanismics.part.aspect;

import mekanism.api.chemical.ChemicalStack;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageSlotted;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedmekanismics.network.ChemicalNetworkConfig;
import org.cyclops.integratedmekanismics.network.IChemicalNetwork;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.part.aspect.*;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class ChemicalTargetStorage extends ChanneledTarget<IChemicalNetwork, ChemicalStack<?>> implements IChemicalTarget {

    private final ITunnelConnection connection;
    private final IIngredientComponentStorage<ChemicalStack<?>, Integer> storage;
    private final IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackMatcher;
    private final PartTarget partTarget;
    private final IAspectProperties properties;

    public ChemicalTargetStorage(ITunnelTransfer transfer, INetwork network,
                                 IIngredientComponentStorage<ChemicalStack<?>, Integer> storage,
                                 IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackMatcher, PartTarget partTarget,
                                 IAspectProperties properties, @Nullable PartStateRoundRobin<?> partState) {
        super(network, network.getCapability(ChemicalNetworkConfig.CAPABILITY).orElse(null), partState,
                properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_CRAFT).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO).getRawValue());
        this.connection = new TunnelConnectionPositionedNetwork(network, getChannel(), partTarget.getTarget(), transfer);
        this.storage = storage;
        this.chemicalStackMatcher = chemicalStackMatcher;
        this.partTarget = partTarget;
        this.properties = properties;
    }

    @Override
    public IIngredientComponentStorage<ChemicalStack<?>, Integer> getChemicalChannel() {
        return getChanneledNetwork().getChannel(getChannel());
    }

    @Override
    public IIngredientComponentStorageSlotted<ChemicalStack<?>, Integer> getChemicalChannelSlotted() {
        return getChanneledNetwork().getChannelSlotted(getChannel());
    }

    @Override
    public boolean hasValidTarget() {
        return storage != null;
    }

    @Override
    public IIngredientComponentStorage<ChemicalStack<?>, Integer> getStorage() {
        return storage;
    }

    @Override
    public IngredientPredicate<ChemicalStack<?>, Integer> getChemicalStackMatcher() {
        return chemicalStackMatcher;
    }

    @Override
    public PartTarget getPartTarget() {
        return partTarget;
    }

    @Override
    public IAspectProperties getProperties() {
        return properties;
    }

    @Override
    public ITunnelConnection getConnection() {
        return connection;
    }
}
