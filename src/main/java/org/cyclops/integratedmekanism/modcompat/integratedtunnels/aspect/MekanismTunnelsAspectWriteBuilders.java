package org.cyclops.integratedmekanism.modcompat.integratedtunnels.aspect;

import com.google.common.collect.ImmutableList;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.operator.IOperator;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integrateddynamics.api.network.PositionedAddonsNetworkIngredientsFilter;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectPropertyTypeInstance;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;
import org.cyclops.integrateddynamics.core.helper.L10NValues;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.part.aspect.build.AspectBuilder;
import org.cyclops.integrateddynamics.core.part.aspect.build.IAspectValuePropagator;
import org.cyclops.integrateddynamics.core.part.aspect.build.IAspectWriteActivator;
import org.cyclops.integrateddynamics.core.part.aspect.build.IAspectWriteDeactivator;
import org.cyclops.integrateddynamics.core.part.aspect.property.AspectProperties;
import org.cyclops.integrateddynamics.core.part.aspect.property.AspectPropertyTypeInstance;
import org.cyclops.integrateddynamics.part.aspect.write.AspectWriteBuilders;
import org.cyclops.integratedmekanism.GeneralConfig;
import org.cyclops.integratedmekanism.IntegratedMekanism;
import org.cyclops.integratedmekanism.core.CapabilityHelpers;
import org.cyclops.integratedmekanism.core.ChemicalHelpers;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanism.network.ChemicalNetwork;
import org.cyclops.integratedmekanism.network.ChemicalNetworkConfig;
import org.cyclops.integratedmekanism.value.MekanismValueTypes;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;
import org.cyclops.integratedtunnels.core.TunnelHelpers;
import org.cyclops.integratedtunnels.core.part.IPartTypeInterfacePositionedAddon;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.part.aspect.ChanneledTargetInformation;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectWriteBuilders;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author rubensworks
 */
public class MekanismTunnelsAspectWriteBuilders {

    public static final AspectBuilder<ValueObjectTypeChemicalStack.ValueChemicalStack, ValueObjectTypeChemicalStack, Triple<PartTarget, IAspectProperties, ValueObjectTypeChemicalStack.ValueChemicalStack>>
            BUILDER_CHEMICALSTACK = AspectWriteBuilders.getValue(AspectBuilder.forWriteType(MekanismValueTypes.OBJECT_CHEMICALSTACK));
    public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueObjectTypeChemicalStack.ValueChemicalStack>, Triple<PartTarget, IAspectProperties, ChemicalStack<?>>>
            PROP_GET_CHEMICALSTACK = (input) -> Triple.of((PartTarget)input.getLeft(), (IAspectProperties)input.getMiddle(), input.getRight().getRawValue());;

    public static final class Chemical {

        public static final IAspectWriteActivator ACTIVATOR = createPositionedNetworkAddonActivator(
                () -> ChemicalNetworkConfig.CAPABILITY,
                List.of(Capabilities.GAS_HANDLER, Capabilities.INFUSION_HANDLER, Capabilities.PIGMENT_HANDLER, Capabilities.SLURRY_HANDLER));
        public static final IAspectWriteDeactivator DEACTIVATOR = createPositionedNetworkAddonDeactivator(
                () -> ChemicalNetworkConfig.CAPABILITY,
                List.of(Capabilities.GAS_HANDLER, Capabilities.INFUSION_HANDLER, Capabilities.PIGMENT_HANDLER, Capabilities.SLURRY_HANDLER));

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, Triple<PartTarget, IAspectProperties, Boolean>>
                BUILDER_BOOLEAN = AspectWriteBuilders.BUILDER_BOOLEAN.byMod(IntegratedMekanism._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("chemical").handle(AspectWriteBuilders.PROP_GET_BOOLEAN).withProperties(TunnelAspectWriteBuilders.PROPERTIES_CHANNEL);
        public static final AspectBuilder<ValueTypeLong.ValueLong, ValueTypeLong, Triple<PartTarget, IAspectProperties, Long>>
                BUILDER_LONG = TunnelAspectWriteBuilders.BUILDER_LONG.byMod(IntegratedMekanism._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("chemical").handle(AspectWriteBuilders.PROP_GET_LONG).withProperties(TunnelAspectWriteBuilders.PROPERTIES_CHANNEL);
        public static final AspectBuilder<ValueObjectTypeChemicalStack.ValueChemicalStack, ValueObjectTypeChemicalStack, Triple<PartTarget, IAspectProperties, ChemicalStack<?>>>
                BUILDER_CHEMICALSTACK = MekanismTunnelsAspectWriteBuilders.BUILDER_CHEMICALSTACK.byMod(IntegratedMekanism._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("chemical").handle(MekanismTunnelsAspectWriteBuilders.PROP_GET_CHEMICALSTACK).withProperties(TunnelAspectWriteBuilders.PROPERTIES_CHANNEL);
        public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>>
                BUILDER_LIST = AspectWriteBuilders.BUILDER_LIST.byMod(IntegratedMekanism._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("chemical").withProperties(TunnelAspectWriteBuilders.PROPERTIES_CHANNEL);
        public static final AspectBuilder<ValueTypeOperator.ValueOperator, ValueTypeOperator, Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>>
                BUILDER_OPERATOR = AspectWriteBuilders.BUILDER_OPERATOR.byMod(IntegratedMekanism._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("chemical").withProperties(TunnelAspectWriteBuilders.PROPERTIES_CHANNEL);

        public static final Predicate<ValueTypeLong.ValueLong> VALIDATOR_LONG_MAXRATE =
                input -> input.getRawValue() <= GeneralConfig.chemicalRateLimit;
        public static final IAspectPropertyTypeInstance<ValueTypeLong, ValueTypeLong.ValueLong> PROP_RATE =
                new AspectPropertyTypeInstance<>(ValueTypes.LONG, "aspect.aspecttypes.integratedmekanism.long.chemical.rate",
                        TunnelAspectWriteBuilders.Energy.VALIDATOR_LONG_POSITIVE.and(VALIDATOR_LONG_MAXRATE));
        public static final IAspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean> PROP_CHECK_AMOUNT =
                new AspectPropertyTypeInstance<>(ValueTypes.BOOLEAN, "aspect.aspecttypes.integratedmekanism.boolean.chemical.checkamount");

        public static final IAspectProperties PROPERTIES = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                TunnelAspectWriteBuilders.PROP_CHANNEL,
                //PROP_EXACTAMOUNT
                TunnelAspectWriteBuilders.PROP_PASSIVE_IO,
                PROP_CHECK_AMOUNT
        ));
        public static final IAspectProperties PROPERTIES_RATE = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                TunnelAspectWriteBuilders.PROP_CHANNEL,
                TunnelAspectWriteBuilders.PROP_ROUNDROBIN,
                PROP_RATE,
                //PROP_EXACTAMOUNT
                TunnelAspectWriteBuilders.PROP_PASSIVE_IO,
                PROP_CHECK_AMOUNT
        ));
        public static final IAspectProperties PROPERTIES_RATECHECKS = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                TunnelAspectWriteBuilders.PROP_CHANNEL,
                TunnelAspectWriteBuilders.PROP_ROUNDROBIN,
                TunnelAspectWriteBuilders.PROP_BLACKLIST,
                TunnelAspectWriteBuilders.PROP_EMPTYISANY,
                PROP_RATE,
                TunnelAspectWriteBuilders.PROP_PASSIVE_IO,
                //PROP_EXACTAMOUNT,
                PROP_CHECK_AMOUNT
        ));
        public static final IAspectProperties PROPERTIES_RATECHECKSCRAFT = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                TunnelAspectWriteBuilders.PROP_CHANNEL,
                TunnelAspectWriteBuilders.PROP_ROUNDROBIN,
                TunnelAspectWriteBuilders.PROP_BLACKLIST,
                TunnelAspectWriteBuilders.PROP_EMPTYISANY,
                PROP_RATE,
                TunnelAspectWriteBuilders.PROP_PASSIVE_IO,
                //PROP_EXACTAMOUNT,
                PROP_CHECK_AMOUNT,
                TunnelAspectWriteBuilders.PROP_CRAFT
        ));
        public static final IAspectProperties PROPERTIES_RATECHECKSLIST = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                TunnelAspectWriteBuilders.PROP_CHANNEL,
                TunnelAspectWriteBuilders.PROP_ROUNDROBIN,
                TunnelAspectWriteBuilders.PROP_BLACKLIST,
                PROP_RATE,
                TunnelAspectWriteBuilders.PROP_PASSIVE_IO,
                //PROP_EXACTAMOUNT,
                PROP_CHECK_AMOUNT
        ));
        public static final IAspectProperties PROPERTIES_FILTER = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                TunnelAspectWriteBuilders.PROP_FILTER_APPLY_TO_INSERTIONS,
                TunnelAspectWriteBuilders.PROP_FILTER_APPLY_TO_EXTRACTIONS,
                TunnelAspectWriteBuilders.PROP_FILTER_ALLOW_ALL_IF_NOT_APPLIED
        ));
        public static final IAspectProperties PROPERTIES_FILTER_CHECKS = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                TunnelAspectWriteBuilders.PROP_FILTER_APPLY_TO_INSERTIONS,
                TunnelAspectWriteBuilders.PROP_FILTER_APPLY_TO_EXTRACTIONS,
                TunnelAspectWriteBuilders.PROP_FILTER_ALLOW_ALL_IF_NOT_APPLIED,
                TunnelAspectWriteBuilders.PROP_BLACKLIST,
                PROP_RATE,
                PROP_CHECK_AMOUNT
        ));

        static {
            PROPERTIES.setValue(TunnelAspectWriteBuilders.PROP_CHANNEL, ValueTypeInteger.ValueInteger.of(IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL));
            //PROPERTIES.setValue(PROP_EXACTAMOUNT, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES.setValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES.setValue(PROP_CHECK_AMOUNT, ValueTypeBoolean.ValueBoolean.of(false));

            PROPERTIES_RATE.setValue(TunnelAspectWriteBuilders.PROP_CHANNEL, ValueTypeInteger.ValueInteger.of(IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL));
            PROPERTIES_RATE.setValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATE.setValue(PROP_RATE, ValueTypeLong.ValueLong.of(1000));
            //PROPERTIES_RATE.setValue(PROP_EXACTAMOUNT, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATE.setValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_RATE.setValue(PROP_CHECK_AMOUNT, ValueTypeBoolean.ValueBoolean.of(false));

            PROPERTIES_RATECHECKS.setValue(TunnelAspectWriteBuilders.PROP_CHANNEL, ValueTypeInteger.ValueInteger.of(IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL));
            PROPERTIES_RATECHECKS.setValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATECHECKS.setValue(TunnelAspectWriteBuilders.PROP_BLACKLIST, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATECHECKS.setValue(TunnelAspectWriteBuilders.PROP_EMPTYISANY, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATECHECKS.setValue(PROP_RATE, ValueTypeLong.ValueLong.of(1000));
            PROPERTIES_RATECHECKS.setValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO, ValueTypeBoolean.ValueBoolean.of(true));
            //PROPERTIES_RATECHECKS.setValue(PROP_EXACTAMOUNT, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATECHECKS.setValue(PROP_CHECK_AMOUNT, ValueTypeBoolean.ValueBoolean.of(false));

            PROPERTIES_RATECHECKSCRAFT.setValue(TunnelAspectWriteBuilders.PROP_CHANNEL, ValueTypeInteger.ValueInteger.of(IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL));
            PROPERTIES_RATECHECKSCRAFT.setValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATECHECKSCRAFT.setValue(TunnelAspectWriteBuilders.PROP_BLACKLIST, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATECHECKSCRAFT.setValue(TunnelAspectWriteBuilders.PROP_EMPTYISANY, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATECHECKSCRAFT.setValue(PROP_RATE, ValueTypeLong.ValueLong.of(1000));
            PROPERTIES_RATECHECKSCRAFT.setValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO, ValueTypeBoolean.ValueBoolean.of(true));
            //PROPERTIES_RATECHECKSCRAFT.setValue(PROP_EXACTAMOUNT, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATECHECKSCRAFT.setValue(PROP_CHECK_AMOUNT, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATECHECKSCRAFT.setValue(TunnelAspectWriteBuilders.PROP_CRAFT, ValueTypeBoolean.ValueBoolean.of(false));

            PROPERTIES_RATECHECKSLIST.setValue(TunnelAspectWriteBuilders.PROP_CHANNEL, ValueTypeInteger.ValueInteger.of(IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL));
            PROPERTIES_RATECHECKSLIST.setValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATECHECKSLIST.setValue(TunnelAspectWriteBuilders.PROP_BLACKLIST, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATECHECKSLIST.setValue(PROP_RATE, ValueTypeLong.ValueLong.of(1000));
            //PROPERTIES_RATECHECKSLIST.setValue(PROP_EXACTAMOUNT, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATECHECKSLIST.setValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_RATECHECKSLIST.setValue(PROP_CHECK_AMOUNT, ValueTypeBoolean.ValueBoolean.of(false));

            PROPERTIES_FILTER.setValue(TunnelAspectWriteBuilders.PROP_FILTER_APPLY_TO_INSERTIONS, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_FILTER.setValue(TunnelAspectWriteBuilders.PROP_FILTER_APPLY_TO_EXTRACTIONS, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_FILTER.setValue(TunnelAspectWriteBuilders.PROP_FILTER_ALLOW_ALL_IF_NOT_APPLIED, ValueTypeBoolean.ValueBoolean.of(false));

            PROPERTIES_FILTER_CHECKS.setValue(TunnelAspectWriteBuilders.PROP_FILTER_APPLY_TO_INSERTIONS, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_FILTER_CHECKS.setValue(TunnelAspectWriteBuilders.PROP_FILTER_APPLY_TO_EXTRACTIONS, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_FILTER_CHECKS.setValue(TunnelAspectWriteBuilders.PROP_FILTER_ALLOW_ALL_IF_NOT_APPLIED, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_FILTER_CHECKS.setValue(TunnelAspectWriteBuilders.PROP_BLACKLIST, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_FILTER_CHECKS.setValue(PROP_RATE, ValueTypeLong.ValueLong.of(1000));
            PROPERTIES_FILTER_CHECKS.setValue(PROP_CHECK_AMOUNT, ValueTypeBoolean.ValueBoolean.of(false));
        }

        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Triple<PartTarget, IAspectProperties, Long>>
                PROP_BOOLEAN_GETRATE = input -> Triple.of(input.getLeft(), input.getMiddle(), input.getRight() ? input.getMiddle().getValue(PROP_RATE).getRawValue() : 0);
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Triple<PartTarget, IAspectProperties, ChanneledTargetInformation<ChemicalStack<?>, Integer>>>
                PROP_BOOLEAN_PREDICATE = input -> {
            IAspectProperties properties = input.getMiddle();
            // TODO: restore exact amount
            IngredientPredicate<ChemicalStack<?>, Integer> chemicalMatcher = new IngredientPredicate<ChemicalStack<?>, Integer>(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, false, false, 0, properties.getValue(PROP_CHECK_AMOUNT).getRawValue()) {
                @Override
                public boolean test(ChemicalStack<?> integer) {
                    return input.getRight();
                }
            };
            return Triple.of(input.getLeft(), input.getMiddle(), ChanneledTargetInformation.of(chemicalMatcher, chemicalMatcher, -1));
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Long>, Triple<PartTarget, IAspectProperties, ChanneledTargetInformation<ChemicalStack<?>, Integer>>>
                PROP_LONG_CHEMICALPREDICATE = input -> {
            // TODO: restore exact amount
            IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackMatcher = ChemicalHelpers.matchAll(input.getRight(), input.getMiddle().getValue(PROP_CHECK_AMOUNT).getRawValue() || input.getMiddle().getValue(TunnelAspectWriteBuilders.PROP_EXACTAMOUNT).getRawValue());
            return Triple.of(input.getLeft(), input.getMiddle(), ChanneledTargetInformation.of(chemicalStackMatcher, chemicalStackMatcher, -1));
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ChemicalStack<?>>, Triple<PartTarget, IAspectProperties, ChanneledTargetInformation<ChemicalStack<?>, Integer>>>
                PROP_CHEMICALSTACK_CHEMICALPREDICATE = input -> {
            IAspectProperties properties = input.getMiddle();
            long rate = properties.getValue(PROP_RATE).getRawValue();
            boolean checkAmount = properties.getValue(PROP_CHECK_AMOUNT).getRawValue();
            boolean exactAmount = properties.getValue(TunnelAspectWriteBuilders.PROP_EXACTAMOUNT).getRawValue();
            boolean blacklist = properties.getValue(TunnelAspectWriteBuilders.PROP_BLACKLIST).getRawValue();
            boolean checkChemical = true;
            ChemicalStack prototype = ChemicalHelpers.prototypeWithCount(input.getRight(), rate);

            // If the (original) prototype is empty, adjust match flags based on the empty behaviour
            if (input.getRight() == null) {
                IngredientPredicate.EmptyBehaviour emptyBehaviour = IngredientPredicate.EmptyBehaviour.fromBoolean(properties.getValue(TunnelAspectWriteBuilders.PROP_EMPTYISANY).getRawValue());
                if (emptyBehaviour == IngredientPredicate.EmptyBehaviour.ANY) {
                    checkAmount = false;
                    checkChemical = false;
                } else {
                    prototype = null;
                }
            }

            IngredientPredicate<ChemicalStack<?>, Integer> ingredientPredicate = ChemicalHelpers.matchChemicalStack(prototype, checkChemical, checkAmount, blacklist, exactAmount);
            return Triple.of(input.getLeft(), input.getMiddle(),
                    ChanneledTargetInformation.of(ingredientPredicate, ingredientPredicate, -1));
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>, Triple<PartTarget, IAspectProperties, ChanneledTargetInformation<ChemicalStack<?>, Integer>>>
                PROP_CHEMICALSTACKLIST_CHEMICALPREDICATE = input -> {
            ValueTypeList.ValueList<ValueObjectTypeChemicalStack, ValueObjectTypeChemicalStack.ValueChemicalStack> list = input.getRight();
            TunnelAspectWriteBuilders.validateListValues(list, MekanismValueTypes.OBJECT_CHEMICALSTACK);

            IAspectProperties properties = input.getMiddle();
            long rate = properties.getValue(PROP_RATE).getRawValue();
            boolean checkAmount = properties.getValue(PROP_CHECK_AMOUNT).getRawValue();
            boolean exactAmount = properties.getValue(TunnelAspectWriteBuilders.PROP_EXACTAMOUNT).getRawValue()
                    || checkAmount; // TODO: restore exact amount
            boolean blacklist = properties.getValue(TunnelAspectWriteBuilders.PROP_BLACKLIST).getRawValue();
            IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackMatcher = ChemicalHelpers.matchChemicalStacks(list.getRawValue(), true, checkAmount, blacklist, rate, exactAmount);
            return Triple.of(input.getLeft(), input.getMiddle(),
                    ChanneledTargetInformation.of(chemicalStackMatcher, chemicalStackMatcher, -1));
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>, Triple<PartTarget, IAspectProperties, ChanneledTargetInformation<ChemicalStack<?>, Integer>>>
                PROP_CHEMICALSTACKPREDICATE_CHEMICALPREDICATE = input -> {
            IOperator predicate = input.getRight().getRawValue();
            if (predicate.getInputTypes().length == 1
                    && ValueHelpers.correspondsTo(predicate.getInputTypes()[0], MekanismValueTypes.OBJECT_CHEMICALSTACK)
                    && ValueHelpers.correspondsTo(predicate.getOutputType(), ValueTypes.BOOLEAN)) {
                IAspectProperties properties = input.getMiddle();
                long rate = properties.getValue(PROP_RATE).getRawValue();
                boolean checkAmount = properties.getValue(PROP_CHECK_AMOUNT).getRawValue();
                boolean exactAmount = properties.getValue(TunnelAspectWriteBuilders.PROP_EXACTAMOUNT).getRawValue()
                        || checkAmount; // TODO: restore exact amount
                IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackMatcher = ChemicalHelpers.matchPredicate(input.getLeft(), predicate, rate, exactAmount);
                return Triple.of(input.getLeft(), input.getMiddle(),
                        ChanneledTargetInformation.of(chemicalStackMatcher, chemicalStackMatcher, -1));
            } else {
                Component current = ValueTypeOperator.getSignature(predicate);
                Component expected = ValueTypeOperator.getSignature(new IValueType[]{MekanismValueTypes.OBJECT_CHEMICALSTACK}, ValueTypes.BOOLEAN);
                throw new EvaluationException(Component.translatable(L10NValues.ASPECT_ERROR_INVALIDTYPE,
                        expected, current));
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ChanneledTargetInformation<ChemicalStack<?>, Integer>>, IChemicalTarget>
                PROP_CHEMICALTARGET = input -> IChemicalTarget.ofCapabilityProvider(input.getRight().getTransfer(),
                input.getLeft(), input.getMiddle(), input.getRight().getIngredientPredicate());

        public static final IAspectValuePropagator<IChemicalTarget, Void>
                PROP_EXPORT = input -> {
            // Save this filter into the part state to handle passive exports
            if (input.isPassiveIO()) {
                input.getPartStatePositionedAddon().setStorageFilter(new PositionedAddonsNetworkIngredientsFilter<>(
                        input.getChemicalStackMatcher(),
                        false,
                        true,
                        false
                ));
            }

            if (input.hasValidTarget()) {
                input.preTransfer();
                // For predicate-based matchers, make sure we can iterate over the contents in a slotted manner,
                // as the predicate must apply to each slotted ingredient.
                // Only do this for exporting, not for importing, as this would otherwise break round-robin imports.
                IIngredientComponentStorage<ChemicalStack<?>, Integer> source = input.getChemicalStackMatcher().hasMatchFlags() ? input.getChemicalChannel() : input.getChemicalChannelSlotted();
                for (Capability<? extends IChemicalHandler<?, ?>> chemicalCapability : CapabilityHelpers.CHEMICAL_CAPABILITIES) { // TODO: this hack can be removed in 1.21 when Mekanism puts all chemicals in a single registry
                    ChemicalNetwork.ACTIVE_CAPABILITY = chemicalCapability;
                    ChemicalStack<?> moved = TunnelHelpers.moveSingleStateOptimized(
                            input.getNetwork(),
                            input.getChanneledNetwork(),
                            input.getChannel(),
                            input.getConnection(chemicalCapability),
                            source,
                            -1,
                            input.getStorage(chemicalCapability),
                            -1,
                            input.getChemicalStackMatcher(),
                            input.getPartTarget().getCenter(),
                            input.isCraftIfFailed()
                    );
                    ChemicalNetwork.ACTIVE_CAPABILITY = null;
                    if (!moved.isEmpty()) { // TODO: this hack can be removed in 1.21 when Mekanism puts all chemicals in a single registry
                        break;
                    }
                }
                input.postTransfer();
            }
            return null;
        };
        public static final IAspectValuePropagator<IChemicalTarget, Void>
                PROP_IMPORT = input -> {
            // Save this filter into the part state to handle passive imports
            if (input.isPassiveIO()) {
                input.getPartStatePositionedAddon().setStorageFilter(new PositionedAddonsNetworkIngredientsFilter<>(
                        input.getChemicalStackMatcher(),
                        true,
                        false,
                        false
                ));
            }

            if (input.hasValidTarget()) {
                input.preTransfer();
                for (Capability<? extends IChemicalHandler<?, ?>> chemicalCapability : CapabilityHelpers.CHEMICAL_CAPABILITIES) { // TODO: this hack can be removed in 1.21 when Mekanism puts all chemicals in a single registry
                    ChemicalNetwork.ACTIVE_CAPABILITY = chemicalCapability;
                    ChemicalStack<?> moved = TunnelHelpers.moveSingleStateOptimized(
                            input.getNetwork(),
                            input.getChanneledNetwork(),
                            input.getChannel(),
                            input.getConnection(chemicalCapability),
                            input.getStorage(chemicalCapability),
                            -1,
                            input.getChemicalChannel(),
                            -1,
                            input.getChemicalStackMatcher(),
                            input.getPartTarget().getCenter(),
                            false
                    );
                    ChemicalNetwork.ACTIVE_CAPABILITY = null;
                    if (!moved.isEmpty()) { // TODO: this hack can be removed in 1.21 when Mekanism puts all chemicals in a single registry
                        break;
                    }
                }
                input.postTransfer();
            }
            return null;
        };

    }

    public static final class World {

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, Triple<PartTarget, IAspectProperties, Boolean>>
                BUILDER_BOOLEAN = AspectWriteBuilders.BUILDER_BOOLEAN.byMod(IntegratedMekanism._instance)
                .appendKind("world").handle(AspectWriteBuilders.PROP_GET_BOOLEAN).withProperties(TunnelAspectWriteBuilders.PROPERTIES_CHANNEL);
        public static final AspectBuilder<ValueTypeLong.ValueLong, ValueTypeLong, Triple<PartTarget, IAspectProperties, Long>>
                BUILDER_LONG = TunnelAspectWriteBuilders.BUILDER_LONG.byMod(IntegratedMekanism._instance)
                .appendKind("world").handle(AspectWriteBuilders.PROP_GET_LONG).withProperties(TunnelAspectWriteBuilders.PROPERTIES_CHANNEL);
        public static final AspectBuilder<ValueObjectTypeChemicalStack.ValueChemicalStack, ValueObjectTypeChemicalStack, Triple<PartTarget, IAspectProperties, ChemicalStack<?>>>
                BUILDER_CHEMICALSTACK = MekanismTunnelsAspectWriteBuilders.BUILDER_CHEMICALSTACK.byMod(IntegratedMekanism._instance)
                .appendKind("world").handle(MekanismTunnelsAspectWriteBuilders.PROP_GET_CHEMICALSTACK).withProperties(TunnelAspectWriteBuilders.PROPERTIES_CHANNEL);
        public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>>
                BUILDER_LIST = AspectWriteBuilders.BUILDER_LIST.byMod(IntegratedMekanism._instance)
                .appendKind("world").withProperties(TunnelAspectWriteBuilders.PROPERTIES_CHANNEL);
        public static final AspectBuilder<ValueTypeOperator.ValueOperator, ValueTypeOperator, Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>>
                BUILDER_OPERATOR = AspectWriteBuilders.BUILDER_OPERATOR.byMod(IntegratedMekanism._instance)
                .appendKind("world").withProperties(TunnelAspectWriteBuilders.PROPERTIES_CHANNEL);

        public static final class Chemical {

            public static final IAspectProperties PROPERTIES_CHEMICAL = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                    TunnelAspectWriteBuilders.PROP_CHANNEL,
                    TunnelAspectWriteBuilders.PROP_ROUNDROBIN,
                    TunnelAspectWriteBuilders.PROP_EMPTYISANY
            ));
            public static final IAspectProperties PROPERTIES_CHEMICALLIST = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                    TunnelAspectWriteBuilders.PROP_CHANNEL,
                    TunnelAspectWriteBuilders.PROP_ROUNDROBIN,
                    TunnelAspectWriteBuilders.PROP_BLACKLIST
            ));
            public static final IAspectProperties PROPERTIES_RATE = MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_RATE.clone();
            public static final IAspectProperties PROPERTIES_RATECHECKS = MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_RATECHECKS.clone();
            public static final IAspectProperties PROPERTIES_RATECHECKSCRAFT = MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_RATECHECKSCRAFT.clone();
            public static final IAspectProperties PROPERTIES_RATECHECKSLIST = MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_RATECHECKSLIST.clone();

            static {
                PROPERTIES_CHEMICAL.setValue(TunnelAspectWriteBuilders.PROP_CHANNEL, ValueTypeInteger.ValueInteger.of(IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL));
                PROPERTIES_CHEMICAL.setValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN, ValueTypeBoolean.ValueBoolean.of(false));
                PROPERTIES_CHEMICAL.setValue(TunnelAspectWriteBuilders.PROP_BLACKLIST, ValueTypeBoolean.ValueBoolean.of(false));
                PROPERTIES_CHEMICAL.setValue(TunnelAspectWriteBuilders.PROP_EMPTYISANY, ValueTypeBoolean.ValueBoolean.of(false));
                PROPERTIES_CHEMICAL.removeValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO);

                PROPERTIES_CHEMICALLIST.setValue(TunnelAspectWriteBuilders.PROP_CHANNEL, ValueTypeInteger.ValueInteger.of(IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL));
                PROPERTIES_CHEMICALLIST.setValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN, ValueTypeBoolean.ValueBoolean.of(false));
                PROPERTIES_CHEMICALLIST.setValue(TunnelAspectWriteBuilders.PROP_BLACKLIST, ValueTypeBoolean.ValueBoolean.of(false));
                PROPERTIES_CHEMICALLIST.removeValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO);

                PROPERTIES_RATE.setValue(TunnelAspectWriteBuilders.World.PROPERTY_ENTITYINDEX, ValueTypeInteger.ValueInteger.of(0));
                PROPERTIES_RATE.removeValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO);

                PROPERTIES_RATECHECKS.setValue(TunnelAspectWriteBuilders.World.PROPERTY_ENTITYINDEX, ValueTypeInteger.ValueInteger.of(0));
                PROPERTIES_RATECHECKS.setValue(TunnelAspectWriteBuilders.PROP_BLACKLIST, ValueTypeBoolean.ValueBoolean.of(false));
                PROPERTIES_RATECHECKS.removeValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO);

                PROPERTIES_RATECHECKSCRAFT.setValue(TunnelAspectWriteBuilders.World.PROPERTY_ENTITYINDEX, ValueTypeInteger.ValueInteger.of(0));
                PROPERTIES_RATECHECKSCRAFT.setValue(TunnelAspectWriteBuilders.PROP_BLACKLIST, ValueTypeBoolean.ValueBoolean.of(false));
                PROPERTIES_RATECHECKSCRAFT.removeValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO);

                PROPERTIES_RATECHECKSLIST.setValue(TunnelAspectWriteBuilders.World.PROPERTY_ENTITYINDEX, ValueTypeInteger.ValueInteger.of(0));
                PROPERTIES_RATECHECKSLIST.removeValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO);
            }

            public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, IChemicalTarget>
                    PROP_BOOLEAN_CHEMICALTARGET = input -> {
                IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackPredicate = input.getRight() ? ChemicalHelpers
                        .matchAll(ChemicalHelpers.BUCKET_VOLUME, false)
                        : ChemicalHelpers.MATCH_NONE;
                return IChemicalTarget.ofCapabilityProvider(
                        chemicalStackPredicate,
                        input.getLeft(),
                        input.getMiddle(),
                        chemicalStackPredicate);
            };
            public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ChemicalStack>, IChemicalTarget>
                    PROP_CHEMICALSTACK_CHEMICALTARGET = input -> {
                IAspectProperties properties = input.getMiddle();
                boolean blacklist = properties.getValue(TunnelAspectWriteBuilders.PROP_BLACKLIST).getRawValue();
                long amount = ChemicalHelpers.BUCKET_VOLUME;
                ChemicalStack prototype = ChemicalHelpers.prototypeWithCount(input.getRight(), amount);
                boolean checkChemical = true;

                // If the (original) prototype is empty, adjust match flags based on the empty behaviour
                if (input.getRight() == null) {
                    IngredientPredicate.EmptyBehaviour emptyBehaviour = IngredientPredicate.EmptyBehaviour.fromBoolean(properties.getValue(TunnelAspectWriteBuilders.PROP_EMPTYISANY).getRawValue());
                    if (emptyBehaviour == IngredientPredicate.EmptyBehaviour.ANY) {
                        checkChemical = false;
                    } else {
                        prototype = null;
                    }
                }

                IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackPredicate = ChemicalHelpers.matchChemicalStack(prototype, checkChemical, false, blacklist, true);
                return IChemicalTarget.ofBlock(chemicalStackPredicate, input.getLeft(), input.getMiddle(), chemicalStackPredicate);
            };
            public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>, IChemicalTarget>
                    PROP_CHEMICALSTACKLIST_CHEMICALTARGET = input -> {
                ValueTypeList.ValueList list = input.getRight();
                TunnelAspectWriteBuilders.validateListValues(list, MekanismValueTypes.OBJECT_CHEMICALSTACK);

                IAspectProperties properties = input.getMiddle();
                boolean blacklist = properties.getValue(TunnelAspectWriteBuilders.PROP_BLACKLIST).getRawValue();
                IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackPredicate = ChemicalHelpers.matchChemicalStacks(list.getRawValue(), true, false, blacklist, ChemicalHelpers.BUCKET_VOLUME, true);
                return IChemicalTarget.ofBlock(chemicalStackPredicate, input.getLeft(), input.getMiddle(), chemicalStackPredicate);
            };
            public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>, IChemicalTarget>
                    PROP_CHEMICALSTACKPREDICATE_CHEMICALTARGET = input -> {
                IOperator predicate = input.getRight().getRawValue();
                if (predicate.getInputTypes().length == 1
                        && ValueHelpers.correspondsTo(predicate.getInputTypes()[0], MekanismValueTypes.OBJECT_CHEMICALSTACK)
                        && ValueHelpers.correspondsTo(predicate.getOutputType(), ValueTypes.BOOLEAN)) {
                    IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackPredicate = ChemicalHelpers.matchPredicate(input.getLeft(), predicate,
                            ChemicalHelpers.BUCKET_VOLUME, true);
                    return IChemicalTarget.ofBlock(chemicalStackPredicate, input.getLeft(), input.getMiddle(), chemicalStackPredicate);
                } else {
                    Component current = ValueTypeOperator.getSignature(predicate);
                    Component expected = ValueTypeOperator.getSignature(new IValueType[]{MekanismValueTypes.OBJECT_CHEMICALSTACK}, ValueTypes.BOOLEAN);
                    throw new EvaluationException(Component.translatable(L10NValues.ASPECT_ERROR_INVALIDTYPE,
                            expected, current));
                }
            };

            public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ChanneledTargetInformation<ChemicalStack<?>, Integer>>, IChemicalTarget>
                    PROP_ENTITY_CHEMICALTARGET = input -> {
                PartTarget partTarget = input.getLeft();
                IAspectProperties properties = input.getMiddle();
                IngredientPredicate<ChemicalStack<?>, Integer> chemicalStackPredicate = input.getRight().getIngredientPredicate();
                int entityIndex = properties.getValue(TunnelAspectWriteBuilders.World.PROPERTY_ENTITYINDEX).getRawValue();

                Entity entity = TunnelAspectWriteBuilders.getEntity(partTarget.getTarget(), entityIndex);
                return IChemicalTarget.ofEntity(chemicalStackPredicate, partTarget, entity, properties, chemicalStackPredicate);
            };

        }

    }

    // TODO: Derived from version in TunnelAspectWriteBuilders. Can probably be removed in 1.21
    public static <N extends IPositionedAddonsNetwork> IAspectWriteActivator createPositionedNetworkAddonActivator(final Supplier<Capability<N>> networkCapability, final List<Capability<?>> targetCapabilities) {
        return new IAspectWriteActivator() {
            public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void onActivate(P partType, PartTarget target, S state) {
                for (Capability<?> targetCapability : targetCapabilities) {
                    state.addVolatileCapability(targetCapability, LazyOptional.of(() -> state).cast());
                }
                DimPos pos = target.getCenter().getPos();
                NetworkHelpers.getNetwork(pos.getLevel(true), pos.getBlockPos(), target.getCenter().getSide())
                        .ifPresent(network -> network.getCapability(networkCapability.get())
                                .ifPresent(positionedAddonsNetwork -> {
                                    if (state instanceof IPartTypeInterfacePositionedAddon.IState) {
                                        ((IPartTypeInterfacePositionedAddon.IState<N, ?, ?, ?>) state).setPositionedAddonsNetwork(positionedAddonsNetwork);
                                    }
                                    if (state instanceof PartStatePositionedAddon) {
                                        ((PartStatePositionedAddon<?, N, ?>) state).setPositionedAddonsNetwork(positionedAddonsNetwork);
                                        ((PartStatePositionedAddon<?, ?, ?>) state).setStorageFilter(null);
                                    }

                                    // Notify target neighbour
                                    DimPos originPos = target.getCenter().getPos();
                                    DimPos targetPos = target.getTarget().getPos();
                                    targetPos.getLevel(true).neighborChanged(targetPos.getBlockPos(),
                                            targetPos.getLevel(true).getBlockState(targetPos.getBlockPos()).getBlock(), originPos.getBlockPos());
                                }));
            }
        };
    }

    // TODO: Derived from version in TunnelAspectWriteBuilders. Can probably be removed in 1.21
    public static <N extends IPositionedAddonsNetwork> IAspectWriteDeactivator createPositionedNetworkAddonDeactivator(final Supplier<Capability<N>> networkCapability, final List<Capability<?>> targetCapabilities) {
        return new IAspectWriteDeactivator() {
            public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void onDeactivate(P partType, PartTarget target, S state) {
                for (Capability<?> targetCapability : targetCapabilities) {
                    state.removeVolatileCapability(targetCapability);
                }
                DimPos pos = target.getCenter().getPos();
                NetworkHelpers.getNetwork(pos.getLevel(true), pos.getBlockPos(), target.getCenter().getSide())
                        .ifPresent(network -> network.getCapability(networkCapability.get())
                                .ifPresent(positionedAddonsNetwork -> {
                                    if (state instanceof IPartTypeInterfacePositionedAddon.IState) {
                                        ((IPartTypeInterfacePositionedAddon.IState<N, ?, ?, ?>) state).setPositionedAddonsNetwork(positionedAddonsNetwork);
                                    }
                                    if (state instanceof PartStatePositionedAddon) {
                                        ((PartStatePositionedAddon<?, N, ?>) state).setPositionedAddonsNetwork(positionedAddonsNetwork);
                                        ((PartStatePositionedAddon<?, ?, ?>) state).setStorageFilter(null);
                                    }

                                    // Notify target neighbour
                                    DimPos originPos = target.getCenter().getPos();
                                    DimPos targetPos = target.getTarget().getPos();
                                    targetPos.getLevel(true).neighborChanged(targetPos.getBlockPos(),
                                            targetPos.getLevel(true).getBlockState(targetPos.getBlockPos()).getBlock(), originPos.getBlockPos());
                                }));
            }
        };
    }

}
