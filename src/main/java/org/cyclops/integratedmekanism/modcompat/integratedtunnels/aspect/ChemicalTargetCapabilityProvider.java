package org.cyclops.integratedmekanism.modcompat.integratedtunnels.aspect;

import com.google.common.collect.Maps;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageSlotted;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageWrapperHandler;
import org.cyclops.commoncapabilities.api.ingredient.storage.IngredientComponentStorageEmpty;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanism.network.ChemicalNetworkConfig;
import org.cyclops.integratedmekanism.network.IChemicalNetwork;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.part.aspect.*;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

/**
 * @author rubensworks
 */
public class ChemicalTargetCapabilityProvider extends ChanneledTargetCapabilityProvider<IChemicalNetwork, ChemicalStack<?>, Integer>
        implements IChemicalTarget {

    private final ITunnelConnection connection;
    private final PartTarget partTarget;
    private final IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackMatcher;
    private final IAspectProperties properties;

    // TODO: this hack can be removed in 1.21 when Mekanism puts all chemicals in a single registry
    private final ICapabilityProvider capabilityProvider;
    private final Direction side;
    private final Map<Capability<?>, IIngredientComponentStorage<ChemicalStack<?>, Integer>> storages = Maps.newIdentityHashMap();
    // TODO: this hack can be removed in 1.21 when Mekanism puts all chemicals in a single registry
    private final Map<Capability<?>, ITunnelConnection> capabilityConnections = Maps.newIdentityHashMap();

    public ChemicalTargetCapabilityProvider(ITunnelTransfer transfer, INetwork network, @Nullable ICapabilityProvider capabilityProvider,
                                            Direction side, IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackMatcher,
                                            PartTarget partTarget, IAspectProperties properties,
                                            @Nullable PartStateRoundRobin<?> partState) {
        super(network, capabilityProvider, side, network.getCapability(ChemicalNetworkConfig.CAPABILITY).orElse(null), partState,
                properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_CRAFT).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO).getRawValue());
        this.connection = new TunnelConnectionPositionedNetworkCapabilityProvider(network, getChannel(), partTarget.getTarget(), transfer, capabilityProvider);
        this.chemicalStackMatcher = chemicalStackMatcher;
        this.partTarget = partTarget;
        this.properties = properties;

        this.capabilityProvider = capabilityProvider;
        this.side = side;
    }

    @Override
    public PartTarget getPartTarget() {
        return partTarget;
    }

    @Override
    public IIngredientComponentStorage<ChemicalStack<?>, Integer> getChemicalChannel() {
        return getChanneledNetwork().getChannel(getChannel());
    }

    @Override
    public IIngredientComponentStorageSlotted<ChemicalStack<?>, Integer> getChemicalChannelSlotted() {
        return getChanneledNetwork().getChannelSlotted(getChannel());
    }

    // TODO: this hack can be removed in 1.21 when Mekanism puts all chemicals in a single registry
    @Override
    public <H extends IChemicalHandler<?, ?>> IIngredientComponentStorage<ChemicalStack<?>, Integer> getStorage(Capability<H> chemicalCapability) {
        IIngredientComponentStorage<ChemicalStack<?>, Integer> storage = this.storages.get(chemicalCapability);
        if (storage == null) {
            IIngredientComponentStorageWrapperHandler<ChemicalStack<?>, Integer, H> wrapperHandler = getComponent().getStorageWrapperHandler(chemicalCapability);
            storage = this.capabilityProvider.getCapability(chemicalCapability, side)
                    .map(wrapperHandler::wrapComponentStorage)
                    .orElseGet(() -> new IngredientComponentStorageEmpty<>(this.getComponent()));;
            this.storages.put(chemicalCapability, storage);
        }

        return storage;
    }

    @Override
    public IngredientPredicate<ChemicalStack<?>, Integer> getChemicalStackMatcher() {
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

    // TODO: this hack can be removed in 1.21 when Mekanism puts all chemicals in a single registry
    @Override
    public ITunnelConnection getConnection(Capability<?> chemicalCapability) {
        return capabilityConnections.computeIfAbsent(chemicalCapability,
                cap -> new CapabilityTunnelConnection(this.connection, cap));
    }

    @Override
    protected IngredientComponent<ChemicalStack<?>, Integer> getComponent() {
        return MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK;
    }

    /**
     * A tunnel connection wrapper that includes a capability in its identity to prevent the sleep-cache
     * from one capability iteration (e.g. GAS) from blocking subsequent iterations (e.g. SLURRY).
     * TODO: this hack can be removed in 1.21 when Mekanism puts all chemicals in a single registry
     */
    private static final class CapabilityTunnelConnection implements ITunnelConnection {

        private final ITunnelConnection base;
        private final Capability<?> capability;

        private CapabilityTunnelConnection(ITunnelConnection base, Capability<?> capability) {
            this.base = base;
            this.capability = capability;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof CapabilityTunnelConnection)) {
                return false;
            }
            CapabilityTunnelConnection that = (CapabilityTunnelConnection) obj;
            return this.capability == that.capability && Objects.equals(this.base, that.base);
        }

        @Override
        public int hashCode() {
            return Objects.hash(System.identityHashCode(capability), base);
        }
    }
}
