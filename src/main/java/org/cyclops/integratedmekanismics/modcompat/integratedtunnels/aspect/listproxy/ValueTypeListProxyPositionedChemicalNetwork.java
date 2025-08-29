package org.cyclops.integratedmekanismics.modcompat.integratedtunnels.aspect.listproxy;

import com.google.common.collect.Iterators;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.ingredient.collection.IIngredientCollectionLike;
import org.cyclops.cyclopscore.persist.nbt.INBTProvider;
import org.cyclops.cyclopscore.persist.nbt.NBTClassType;
import org.cyclops.integrateddynamics.api.ingredient.IIngredientPositionsIndex;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeListProxyPositioned;
import org.cyclops.integratedmekanismics.network.ChemicalNetworkConfig;
import org.cyclops.integratedmekanismics.value.MekanismValueTypes;
import org.cyclops.integratedmekanismics.value.ValueObjectTypeChemicalStack;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectReadBuilders;

import java.util.Iterator;
import java.util.Optional;

/**
 * A list proxy for the chemicals in a network at a certain position.
 */
public class ValueTypeListProxyPositionedChemicalNetwork extends ValueTypeListProxyPositioned<ValueObjectTypeChemicalStack, ValueObjectTypeChemicalStack.ValueChemicalStack> implements INBTProvider {

    private int channel;

    public ValueTypeListProxyPositionedChemicalNetwork(DimPos pos, Direction side, int channel) {
        super(MekanismTunnelsValueTypeListProxyFactories.POSITIONED_CHEMICAL_NETWORK.getName(), MekanismValueTypes.OBJECT_CHEMICALSTACK, pos, side);
        this.channel = channel;
    }

    public ValueTypeListProxyPositionedChemicalNetwork() {
        this(null, null, 0);
    }

    public void writeGeneratedFieldsToNBT(CompoundTag tag) {
        super.writeGeneratedFieldsToNBT(tag);
        NBTClassType.writeNbt(Integer.class, "channel", this.channel, tag);
    }

    public void readGeneratedFieldsFromNBT(CompoundTag tag) {
        super.readGeneratedFieldsFromNBT(tag);
        this.channel = NBTClassType.readNbt(Integer.class, "channel", tag);
    }

    protected Optional<IIngredientPositionsIndex<ChemicalStack<?>, Integer>> getChannelIndex() {
        return TunnelAspectReadBuilders.Network.getChannelIndex(ChemicalNetworkConfig.CAPABILITY, getPos(), getSide(), channel);
    }

    @Override
    public int getLength() {
        return getChannelIndex()
                .map(IIngredientCollectionLike::size)
                .orElse(0);
    }

    @Override
    public ValueObjectTypeChemicalStack.ValueChemicalStack get(int index) {
        return ValueObjectTypeChemicalStack.ValueChemicalStack.of(getChannelIndex()
                .map(store -> Iterators.get(store.iterator(), index, GasStack.EMPTY))
                .orElse((ChemicalStack) GasStack.EMPTY));
    }

    @Override
    public Iterator<ValueObjectTypeChemicalStack.ValueChemicalStack> iterator() {
        // We use a custom iterator that retrieves the network only once.
        // Because for large networks, the network would have to be retrieved for every single ingredient,
        // which could result in a major performance problem.
        return getChannelIndex()
                .map(store -> store.stream().map(ValueObjectTypeChemicalStack.ValueChemicalStack::of).iterator())
                .orElse(Iterators.forArray());
    }
}
