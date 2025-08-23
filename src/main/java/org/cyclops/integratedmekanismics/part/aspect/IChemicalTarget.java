package org.cyclops.integratedmekanismics.part.aspect;

import mekanism.api.chemical.ChemicalStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageSlotted;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedmekanismics.network.IChemicalNetwork;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.part.aspect.IChanneledTarget;
import org.cyclops.integratedtunnels.part.aspect.ITunnelConnection;
import org.cyclops.integratedtunnels.part.aspect.ITunnelTransfer;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public interface IChemicalTarget extends IChanneledTarget<IChemicalNetwork, ChemicalStack<?>> {

    public IIngredientComponentStorage<ChemicalStack<?>, Integer> getChemicalChannel();

    public IIngredientComponentStorageSlotted<ChemicalStack<?>, Integer> getChemicalChannelSlotted();

    public IIngredientComponentStorage<ChemicalStack<?>, Integer> getStorage();

    public IngredientPredicate<ChemicalStack<?>, Integer> getChemicalStackMatcher();

    public PartTarget getPartTarget();

    public IAspectProperties getProperties();

    public ITunnelConnection getConnection();

    public static IChemicalTarget ofCapabilityProvider(ITunnelTransfer transfer, PartTarget partTarget, IAspectProperties properties,
                                                    IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        BlockEntity tile = target.getPos().getLevel(true).getBlockEntity(target.getPos().getBlockPos());
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new ChemicalTargetCapabilityProvider(transfer, network, tile, target.getSide(),
                chemicalStackMatcher, partTarget, properties, partState);
    }

    public static IChemicalTarget ofEntity(ITunnelTransfer transfer, PartTarget partTarget, @Nullable Entity entity,
                                                         IAspectProperties properties,
                                                         IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new ChemicalTargetCapabilityProvider(transfer, network, entity, target.getSide(),
                chemicalStackMatcher, partTarget, properties, partState);
    }

    public static IChemicalTarget ofBlock(ITunnelTransfer transfer, PartTarget partTarget, IAspectProperties properties,
                                                        IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new ChemicalTargetCapabilityProvider(transfer, network, null, target.getSide(),
                chemicalStackMatcher, partTarget, properties, partState);
    }

    public static IChemicalTarget ofStorage(ITunnelTransfer transfer, INetwork network, PartTarget partTarget,
                                               IAspectProperties properties,
                                               IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackMatcher,
                                               IIngredientComponentStorage<ChemicalStack<?>, Integer> storage) {
        PartPos center = partTarget.getCenter();
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new ChemicalTargetStorage(transfer, network, storage,
                chemicalStackMatcher, partTarget, properties, partState);
    }

}
