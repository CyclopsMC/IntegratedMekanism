package org.cyclops.integratedmekanismics.part.aspect;

import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Pair;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.api.ingredient.IIngredientPositionsIndex;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeList;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeOperator;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.part.aspect.build.AspectBuilder;
import org.cyclops.integrateddynamics.core.part.aspect.build.IAspectValuePropagator;
import org.cyclops.integrateddynamics.part.aspect.read.AspectReadBuilders;
import org.cyclops.integratedmekanismics.IntegratedMekanismics;
import org.cyclops.integratedmekanismics.network.ChemicalNetworkConfig;
import org.cyclops.integratedmekanismics.part.aspect.listproxy.ValueTypeListProxyPositionedChemicalNetwork;

import java.util.Optional;

/**
 * @author rubensworks
 */
public class MekanismAspectReadBuilders {

    public static final class Network {

        public static <T, M> Optional<IIngredientComponentStorage<T, M>> getChannel(Capability<? extends IPositionedAddonsNetworkIngredients<T, M>> networkCapability,
                                                                                    DimPos dimPos, Direction side, int channel) {
            INetwork network = NetworkHelpers.getNetwork(dimPos.getLevel(true), dimPos.getBlockPos(), side).orElse(null);
            return Optional.ofNullable(network != null ? network.getCapability(networkCapability)
                    .map(itemNetwork -> {
                        itemNetwork.scheduleObservation();
                        return itemNetwork.getChannel(channel);
                    })
                    .orElse(null) : null);
        }

        public static <T, M> Optional<IIngredientPositionsIndex<T, M>> getChannelIndex(Capability<? extends IPositionedAddonsNetworkIngredients<T, M>> networkCapability,
                                                                                       DimPos dimPos, Direction side, int channel) {
            INetwork network = NetworkHelpers.getNetwork(dimPos.getLevel(true), dimPos.getBlockPos(), side).orElse(null);
            return Optional.ofNullable(network != null ? network.getCapability(networkCapability)
                    .map(itemNetwork -> {
                        itemNetwork.scheduleObservation();
                        return itemNetwork.getChannelIndex(channel);
                    })
                    .orElse(null) : null);
        }

        public static final class Chemical {
            public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, Pair<PartTarget, IAspectProperties>>
                    BUILDER_LIST = AspectReadBuilders.BUILDER_LIST.byMod(IntegratedMekanismics._instance)
                    .withProperties(AspectReadBuilders.Network.PROPERTIES)
                    .appendKind("chemicalnetwork");
            public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Pair<PartTarget, IAspectProperties>>
                    BUILDER_INTEGER = AspectReadBuilders.BUILDER_INTEGER.byMod(IntegratedMekanismics._instance)
                    .withProperties(AspectReadBuilders.Network.PROPERTIES)
                    .appendKind("chemicalnetwork");
            public static final AspectBuilder<ValueTypeLong.ValueLong, ValueTypeLong, Pair<PartTarget, IAspectProperties>>
                    BUILDER_LONG = AspectReadBuilders.BUILDER_LONG.byMod(IntegratedMekanismics._instance)
                    .withProperties(AspectReadBuilders.Network.PROPERTIES)
                    .appendKind("chemicalnetwork");
            public static final AspectBuilder<ValueTypeOperator.ValueOperator, ValueTypeOperator, Pair<PartTarget, IAspectProperties>>
                    BUILDER_OPERATOR = AspectReadBuilders.BUILDER_OPERATOR.byMod(IntegratedMekanismics._instance)
                    .withProperties(AspectReadBuilders.Network.PROPERTIES)
                    .appendKind("chemicalnetwork");

            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IIngredientComponentStorage<ChemicalStack<?>, Integer>> PROP_GET_CHANNEL = input -> {
                int channel = input.getRight().getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL).getRawValue();
                return getChannel(ChemicalNetworkConfig.CAPABILITY, input.getLeft().getTarget().getPos(), input.getLeft().getTarget().getSide(), channel).orElse(null);
            };
            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IIngredientPositionsIndex<ChemicalStack<?>, Integer>> PROP_GET_CHANNELINDEX = input -> {
                int channel = input.getRight().getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL).getRawValue();
                return getChannelIndex(ChemicalNetworkConfig.CAPABILITY, input.getLeft().getTarget().getPos(), input.getLeft().getTarget().getSide(), channel).orElse(null);
            };

            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ValueTypeList.ValueList>
                    PROP_GET_LIST = input -> ValueTypeList.ValueList.ofFactory(new ValueTypeListProxyPositionedChemicalNetwork(
                    input.getLeft().getTarget().getPos(),
                    input.getLeft().getTarget().getSide(),
                    input.getRight().getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL).getRawValue()));
        }
    }

}
