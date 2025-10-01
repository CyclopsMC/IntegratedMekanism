package org.cyclops.integratedmekanism.modcompat.integratedtunnels.aspect;

import mekanism.api.chemical.ChemicalStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.cyclops.commoncapabilities.api.ingredient.capability.ICapabilityGetter;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageSlotted;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedmekanism.network.IChemicalNetwork;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.part.aspect.IChanneledTarget;
import org.cyclops.integratedtunnels.part.aspect.ITunnelConnection;
import org.cyclops.integratedtunnels.part.aspect.ITunnelTransfer;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public interface IChemicalTarget extends IChanneledTarget<IChemicalNetwork, ChemicalStack> {

    public IIngredientComponentStorage<ChemicalStack, Integer> getChemicalChannel();

    public IIngredientComponentStorageSlotted<ChemicalStack, Integer> getChemicalChannelSlotted();

    public IIngredientComponentStorage<ChemicalStack, Integer> getStorage();

    public IngredientPredicate<ChemicalStack, Integer> getChemicalStackMatcher();

    public PartTarget getPartTarget();

    public IAspectProperties getProperties();

    public ITunnelConnection getConnection();

    public static IChemicalTarget ofCapabilityProvider(ITunnelTransfer transfer, PartTarget partTarget, IAspectProperties properties,
                                                    IngredientPredicate<ChemicalStack, Integer> chemicalStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        BlockEntity tile = target.getPos().getLevel(true).getBlockEntity(target.getPos().getBlockPos());
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new ChemicalTargetCapabilityProvider(transfer, network, Block.class, tile == null ? ICapabilityGetter.forBlock(target.getPos().getLevel(true), target.getPos().getBlockPos(), null, null) : ICapabilityGetter.forBlockEntity(tile), tile, target.getSide(),
                chemicalStackMatcher, partTarget, properties, partState);
    }

    public static IChemicalTarget ofEntity(ITunnelTransfer transfer, PartTarget partTarget, @Nullable Entity entity,
                                                         IAspectProperties properties,
                                                         IngredientPredicate<ChemicalStack, Integer> chemicalStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new ChemicalTargetCapabilityProvider(transfer, network, Entity.class, entity == null ? null : ICapabilityGetter.forEntity(entity), entity, target.getSide(),
                chemicalStackMatcher, partTarget, properties, partState);
    }

    public static IChemicalTarget ofBlock(ITunnelTransfer transfer, PartTarget partTarget, IAspectProperties properties,
                                                        IngredientPredicate<ChemicalStack, Integer> chemicalStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new ChemicalTargetCapabilityProvider(transfer, network, Block.class, ICapabilityGetter.forBlock(target.getPos().getLevel(true), target.getPos().getBlockPos(), null, null), null, target.getSide(),
                chemicalStackMatcher, partTarget, properties, partState);
    }

}
