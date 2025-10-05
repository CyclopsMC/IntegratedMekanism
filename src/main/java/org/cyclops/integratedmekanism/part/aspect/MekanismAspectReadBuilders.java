package org.cyclops.integratedmekanism.part.aspect;

import com.google.common.collect.ImmutableList;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.lib.multiblock.IMultiblock;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import org.apache.commons.lang3.tuple.Pair;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.cyclopscore.helper.IModHelpersNeoForge;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectPropertyTypeInstance;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;
import org.cyclops.integrateddynamics.core.part.aspect.build.AspectBuilder;
import org.cyclops.integrateddynamics.core.part.aspect.build.IAspectValuePropagator;
import org.cyclops.integrateddynamics.core.part.aspect.property.AspectProperties;
import org.cyclops.integrateddynamics.core.part.aspect.property.AspectPropertyTypeInstance;
import org.cyclops.integrateddynamics.part.aspect.read.AspectReadBuilders;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.core.EmptyChemicalHandler;
import org.cyclops.integratedmekanism.part.aspect.listproxy.ValueTypeListProxyPositionedChemicalTankCapacities;
import org.cyclops.integratedmekanism.part.aspect.listproxy.ValueTypeListProxyPositionedChemicalTankChemicalStacks;
import org.cyclops.integratedmekanism.value.MekanismValueTypes;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;

import java.util.Optional;

/**
 * @author rubensworks
 */
public class MekanismAspectReadBuilders {

    // --------------- Value type builders ---------------
    public static final AspectBuilder<ValueObjectTypeChemicalStack.ValueChemicalStack, ValueObjectTypeChemicalStack, Pair<PartTarget, IAspectProperties>>
            BUILDER_OBJECT_CHEMICALSTACK = AspectBuilder.forReadType(MekanismValueTypes.OBJECT_CHEMICALSTACK).byMod(Reference.MOD_ID);
    public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Pair<PartTarget, IAspectProperties>>
            BUILDER_INTEGER = AspectBuilder.forReadType(ValueTypes.INTEGER).byMod(Reference.MOD_ID);
    public static final AspectBuilder<ValueTypeDouble.ValueDouble, ValueTypeDouble, Pair<PartTarget, IAspectProperties>>
            BUILDER_DOUBLE = AspectBuilder.forReadType(ValueTypes.DOUBLE).byMod(Reference.MOD_ID);
    public static final AspectBuilder<ValueTypeLong.ValueLong, ValueTypeLong, Pair<PartTarget, IAspectProperties>>
            BUILDER_LONG = AspectBuilder.forReadType(ValueTypes.LONG).byMod(Reference.MOD_ID);

    // --------------- Value type propagators ---------------
    public static final IAspectValuePropagator<ChemicalStack, ValueObjectTypeChemicalStack.ValueChemicalStack>
            PROP_GET_CHEMICALSTACK = ValueObjectTypeChemicalStack.ValueChemicalStack::of;

    // --------------- Value type builders ---------------

    public static final class Chemical {

        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROP_TANKID =
                new AspectPropertyTypeInstance<>(ValueTypes.INTEGER, "aspect.aspecttypes.integrateddynamics.integer.tankid", AspectReadBuilders.VALIDATOR_INTEGER_POSITIVE);
        public static final IAspectProperties PROPERTIES = new AspectProperties(ImmutableList.of(
                PROP_TANKID
        ));
        static {
            PROPERTIES.setValue(PROP_TANKID, ValueTypeInteger.ValueInteger.of(0)); // Not required in this case, but we do this here just as an example on how to set default values.
        }

        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IChemicalHandler> PROP_GET = input -> {
            PartPos target = input.getLeft().getTarget();
            return IModHelpersNeoForge.get().getCapabilityHelpers().getCapability(target.getPos(), target.getSide(), Capabilities.CHEMICAL.block())
                    .orElse(EmptyChemicalHandler.INSTANCE);
        };
        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, Pair<IChemicalHandler, Integer>> PROP_GET_ACTIVATABLE = input -> {
            PartPos target = input.getLeft().getTarget();
            IChemicalHandler chemicalHandler = IModHelpersNeoForge.get().getCapabilityHelpers().getCapability(target.getPos(), target.getSide(), Capabilities.CHEMICAL.block()).orElse(null);
            if(chemicalHandler != null) {
                int i = input.getRight().getValue(PROP_TANKID).getRawValue();
                if(i < chemicalHandler.getChemicalTanks()) {
                    return Pair.of(chemicalHandler, i);
                }
            }
            return null;
        };
        public static final IAspectValuePropagator<Pair<IChemicalHandler, Integer>, ChemicalStack>
                PROP_GET_CHEMICALSTACK = tankInfo -> tankInfo != null ? tankInfo.getLeft().getChemicalInTank(tankInfo.getRight()) : ChemicalStack.EMPTY;

        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ValueTypeList.ValueList>
                PROP_GET_LIST_CHEMICALSTACKS = input -> ValueTypeList.ValueList.ofFactory(new ValueTypeListProxyPositionedChemicalTankChemicalStacks(
                input.getLeft().getTarget().getPos(), input.getLeft().getTarget().getSide()
        ));
        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ValueTypeList.ValueList>
                PROP_GET_LIST_CAPACITIES = input -> ValueTypeList.ValueList.ofFactory(new ValueTypeListProxyPositionedChemicalTankCapacities(
                input.getLeft().getTarget().getPos(), input.getLeft().getTarget().getSide()
        ));

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, IChemicalHandler>
                BUILDER_BOOLEAN = AspectReadBuilders.BUILDER_BOOLEAN.byMod(Reference.MOD_ID).handle(PROP_GET, "chemical");
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, IChemicalHandler>
                BUILDER_INTEGER = AspectReadBuilders.BUILDER_INTEGER.byMod(Reference.MOD_ID).handle(PROP_GET, "chemical");
        public static final AspectBuilder<ValueTypeLong.ValueLong, ValueTypeLong, IChemicalHandler>
                BUILDER_LONG = AspectReadBuilders.BUILDER_LONG.byMod(Reference.MOD_ID).handle(PROP_GET, "chemical");
        public static final AspectBuilder<ValueTypeLong.ValueLong, ValueTypeLong, Pair<IChemicalHandler, Integer>>
                BUILDER_LONG_ACTIVATABLE = AspectReadBuilders.BUILDER_LONG.byMod(Reference.MOD_ID).handle(PROP_GET_ACTIVATABLE, "chemical").withProperties(PROPERTIES);
        public static final AspectBuilder<ValueTypeDouble.ValueDouble, ValueTypeDouble, Pair<IChemicalHandler, Integer>>
                BUILDER_DOUBLE_ACTIVATABLE = AspectReadBuilders.BUILDER_DOUBLE.byMod(Reference.MOD_ID).handle(PROP_GET_ACTIVATABLE, "chemical").withProperties(PROPERTIES);

    }

    public static final class Machine {

        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, Optional<FissionReactorMultiblockData>> PROP_GET_FISSIONREACTOR = input -> {
            DimPos dimPos = input.getLeft().getTarget().getPos();
            IMultiblock<?> multiBlock = IModHelpers.get().getBlockEntityHelpers().get(dimPos.getLevel(true), dimPos.getBlockPos(), IMultiblock.class).orElse(null);
            if (multiBlock != null && multiBlock.getMultiblock() instanceof FissionReactorMultiblockData data) {
                return Optional.of(data);
            }
            return Optional.empty();
        };

        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Optional<FissionReactorMultiblockData>>
                BUILDER_FISSIONREACTOR_INTEGER = MekanismAspectReadBuilders.BUILDER_INTEGER.appendKind("machine")
                .handle(PROP_GET_FISSIONREACTOR, "fissionreactor");
        public static final AspectBuilder<ValueTypeDouble.ValueDouble, ValueTypeDouble, Optional<FissionReactorMultiblockData>>
                BUILDER_FISSIONREACTOR_DOUBLE = MekanismAspectReadBuilders.BUILDER_DOUBLE.appendKind("machine")
                .handle(PROP_GET_FISSIONREACTOR, "fissionreactor");
        public static final AspectBuilder<ValueTypeLong.ValueLong, ValueTypeLong, Optional<FissionReactorMultiblockData>>
                BUILDER_FISSIONREACTOR_LONG = MekanismAspectReadBuilders.BUILDER_LONG.appendKind("machine")
                .handle(PROP_GET_FISSIONREACTOR, "fissionreactor");
    }

    public static final class World {
        public static final AspectBuilder<ValueTypeDouble.ValueDouble, ValueTypeDouble, DimPos>
                BUILDER_DOUBLE = MekanismAspectReadBuilders.BUILDER_DOUBLE.handle(AspectReadBuilders.World.PROP_GET, "world");
    }

}
