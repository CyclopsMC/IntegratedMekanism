package org.cyclops.integratedmekanism.modcompat.integratedtunnels.aspect;

import com.google.common.collect.Iterators;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectRead;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectWrite;
import org.cyclops.integrateddynamics.core.evaluate.operator.Operators;
import org.cyclops.integrateddynamics.core.evaluate.operator.PositionedOperator;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;
import org.cyclops.integrateddynamics.part.aspect.read.AspectReadBuilders;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.ingredient.ChemicalMatch;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.aspect.operator.PositionedOperatorIngredientIndexChemical;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectWriteBuilders;

/**
 * Collection of all mekanism aspects.
 * @author rubensworks
 */
public class MekanismTunnelsAspects {

    public static final class Read {

        public static final class Chemical {
            public static final IAspectRead<ValueTypeLong.ValueLong, ValueTypeLong>
                    LONG_COUNT = MekanismTunnelsAspectReadBuilders.Network.Chemical.BUILDER_LONG
                    .handle(MekanismTunnelsAspectReadBuilders.Network.Chemical.PROP_GET_CHANNELINDEX)
                    .handle(channel -> channel.stream().mapToLong(ChemicalStack::getAmount).sum())
                    .handle(AspectReadBuilders.PROP_GET_LONG, "count")
                    .buildRead();
            public static final IAspectRead<ValueTypeLong.ValueLong, ValueTypeLong>
                    LONG_COUNTMAX = MekanismTunnelsAspectReadBuilders.Network.Chemical.BUILDER_LONG
                    .handle(MekanismTunnelsAspectReadBuilders.Network.Chemical.PROP_GET_CHANNEL)
                    .handle(IIngredientComponentStorage::getMaxQuantity)
                    .handle(AspectReadBuilders.PROP_GET_LONG, "countmax")
                    .buildRead();
            public static final IAspectRead<ValueTypeList.ValueList, ValueTypeList>
                    LIST_CHEMICALSTACKS = MekanismTunnelsAspectReadBuilders.Network.Chemical.BUILDER_LIST
                    .handle(MekanismTunnelsAspectReadBuilders.Network.Chemical.PROP_GET_LIST, "chemicalstacks")
                    .buildRead();
            public static final IAspectRead<ValueTypeOperator.ValueOperator, ValueTypeOperator>
                    OPERATOR_GETCHEMICALCOUNT = MekanismTunnelsAspectReadBuilders.Network.Chemical.BUILDER_OPERATOR
                    .handle(input -> ValueTypeOperator.ValueOperator.of(new PositionedOperatorIngredientIndexChemical(
                            input.getLeft().getTarget().getPos(),
                            input.getLeft().getTarget().getSide(),
                            input.getRight().getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL).getRawValue()
                    )))
                    .appendKind("countbychemical")
                    .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger>
                    INTEGER_INTERFACES = MekanismTunnelsAspectReadBuilders.Network.Chemical.BUILDER_INTEGER
                    .handle(MekanismTunnelsAspectReadBuilders.Network.Chemical.PROP_GET_CHANNELINDEX)
                    .handle(channel -> Iterators.size(channel.getPositions(ChemicalStack.EMPTY, ChemicalMatch.ANY)))
                    .handle(AspectReadBuilders.PROP_GET_INTEGER, "interfaces")
                    .buildRead();
            static {
                Operators.REGISTRY.registerSerializer(new PositionedOperator.Serializer(
                        PositionedOperatorIngredientIndexChemical.class, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "positioned_ingredient_index_chemical")));
            }
        }

    }

    public static final class Write {

        public static final class Chemical {

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_EXPORT =
                    MekanismTunnelsAspectWriteBuilders.Chemical.BUILDER_BOOLEAN
                            .withProperties(MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_RATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_BOOLEAN_GETRATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeLong.ValueLong, ValueTypeLong> LONG_EXPORT =
                    MekanismTunnelsAspectWriteBuilders.Chemical.BUILDER_LONG
                            .withProperties(MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueObjectTypeChemicalStack.ValueChemicalStack, ValueObjectTypeChemicalStack> CHEMICALSTACK_EXPORT =
                    MekanismTunnelsAspectWriteBuilders.Chemical.BUILDER_CHEMICALSTACK
                            .withProperties(MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_RATECHECKSCRAFT)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALSTACK_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_EXPORT =
                    MekanismTunnelsAspectWriteBuilders.Chemical.BUILDER_LIST
                            .withProperties(MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_RATECHECKSLIST)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKLIST_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> PREDICATE_EXPORT =
                    MekanismTunnelsAspectWriteBuilders.Chemical.BUILDER_OPERATOR
                            .withProperties(MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_RATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKPREDICATE_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("export").buildWrite();

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_IMPORT =
                    MekanismTunnelsAspectWriteBuilders.Chemical.BUILDER_BOOLEAN
                            .withProperties(MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_RATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_BOOLEAN_GETRATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeLong.ValueLong, ValueTypeLong> LONG_IMPORT =
                    MekanismTunnelsAspectWriteBuilders.Chemical.BUILDER_LONG
                            .withProperties(MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueObjectTypeChemicalStack.ValueChemicalStack, ValueObjectTypeChemicalStack> CHEMICALSTACK_IMPORT =
                    MekanismTunnelsAspectWriteBuilders.Chemical.BUILDER_CHEMICALSTACK
                            .withProperties(MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_RATECHECKS)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALSTACK_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_IMPORT =
                    MekanismTunnelsAspectWriteBuilders.Chemical.BUILDER_LIST
                            .withProperties(MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_RATECHECKSLIST)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKLIST_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> PREDICATE_IMPORT =
                    MekanismTunnelsAspectWriteBuilders.Chemical.BUILDER_OPERATOR
                            .withProperties(MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_RATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKPREDICATE_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("import").buildWrite();

        }

        public static final class World {

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> ENTITY_CHEMICAL_BOOLEAN_IMPORT =
                    MekanismTunnelsAspectWriteBuilders.World.BUILDER_BOOLEAN
                            .withProperties(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROPERTIES_RATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_BOOLEAN_GETRATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeLong.ValueLong, ValueTypeLong> ENTITY_CHEMICAL_INTEGER_IMPORT =
                    MekanismTunnelsAspectWriteBuilders.World.BUILDER_LONG
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("import").buildWrite();
            public static final IAspectWrite<ValueObjectTypeChemicalStack.ValueChemicalStack, ValueObjectTypeChemicalStack> ENTITY_CHEMICAL_CHEMICALSTACK_IMPORT =
                    MekanismTunnelsAspectWriteBuilders.World.BUILDER_CHEMICALSTACK
                            .withProperties(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROPERTIES_RATECHECKS)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALSTACK_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> ENTITY_CHEMICAL_LISTCHEMICALSTACK_IMPORT =
                    MekanismTunnelsAspectWriteBuilders.World.BUILDER_LIST
                            .withProperties(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROPERTIES_RATECHECKSLIST)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKLIST_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> ENTITY_CHEMICAL_PREDICATECHEMICALSTACK_IMPORT =
                    MekanismTunnelsAspectWriteBuilders.World.BUILDER_OPERATOR
                            .withProperties(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROPERTIES_RATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKPREDICATE_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("import").buildWrite();

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> ENTITY_CHEMICAL_BOOLEAN_EXPORT =
                    MekanismTunnelsAspectWriteBuilders.World.BUILDER_BOOLEAN
                            .withProperties(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROPERTIES_RATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_BOOLEAN_GETRATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeLong.ValueLong, ValueTypeLong> ENTITY_CHEMICAL_INTEGER_EXPORT =
                    MekanismTunnelsAspectWriteBuilders.World.BUILDER_LONG
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("export").buildWrite();
            public static final IAspectWrite<ValueObjectTypeChemicalStack.ValueChemicalStack, ValueObjectTypeChemicalStack> ENTITY_CHEMICAL_CHEMICALSTACK_EXPORT =
                    MekanismTunnelsAspectWriteBuilders.World.BUILDER_CHEMICALSTACK
                            .withProperties(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROPERTIES_RATECHECKSCRAFT)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALSTACK_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> ENTITY_CHEMICAL_LISTCHEMICALSTACK_EXPORT =
                    MekanismTunnelsAspectWriteBuilders.World.BUILDER_LIST
                            .withProperties(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROPERTIES_RATECHECKSLIST)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKLIST_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> ENTITY_CHEMICAL_PREDICATECHEMICALSTACK_EXPORT =
                    MekanismTunnelsAspectWriteBuilders.World.BUILDER_OPERATOR
                            .withProperties(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROPERTIES_RATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKPREDICATE_CHEMICALPREDICATE)
                            .handle(MekanismTunnelsAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("export").buildWrite();

        }

        public static final class ChemicalFilter {

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_SET_FILTER =
                    MekanismTunnelsAspectWriteBuilders.Chemical.BUILDER_BOOLEAN
                            .withProperties(MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_FILTER)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_BOOLEAN_PREDICATE)
                            .handle(TunnelAspectWriteBuilders.propSetFilter())
                            .appendActivator(TunnelAspectWriteBuilders.PREPARE_FILTER)
                            .appendDeactivator(TunnelAspectWriteBuilders.RESET_FILTER)
                            .appendKind("filter").buildWrite();
            public static final IAspectWrite<ValueObjectTypeChemicalStack.ValueChemicalStack, ValueObjectTypeChemicalStack> CHEMICALSTACK_SET_FILTER =
                    MekanismTunnelsAspectWriteBuilders.Chemical.BUILDER_CHEMICALSTACK
                            .withProperties(MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_FILTER_CHECKS)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALSTACK_CHEMICALPREDICATE)
                            .handle(TunnelAspectWriteBuilders.propSetFilter())
                            .appendActivator(TunnelAspectWriteBuilders.PREPARE_FILTER)
                            .appendDeactivator(TunnelAspectWriteBuilders.RESET_FILTER)
                            .appendKind("filter").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_SET_FILTER =
                    MekanismTunnelsAspectWriteBuilders.Chemical.BUILDER_LIST
                            .withProperties(MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_FILTER_CHECKS)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKLIST_CHEMICALPREDICATE)
                            .handle(TunnelAspectWriteBuilders.propSetFilter())
                            .appendActivator(TunnelAspectWriteBuilders.PREPARE_FILTER)
                            .appendDeactivator(TunnelAspectWriteBuilders.RESET_FILTER)
                            .appendKind("filter").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> PREDICATE_SET_FILTER =
                    MekanismTunnelsAspectWriteBuilders.Chemical.BUILDER_OPERATOR
                            .withProperties(MekanismTunnelsAspectWriteBuilders.Chemical.PROPERTIES_FILTER)
                            .handle(MekanismTunnelsAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKPREDICATE_CHEMICALPREDICATE)
                            .handle(TunnelAspectWriteBuilders.propSetFilter())
                            .appendActivator(TunnelAspectWriteBuilders.PREPARE_FILTER)
                            .appendDeactivator(TunnelAspectWriteBuilders.RESET_FILTER)
                            .appendKind("filter").buildWrite();

        }

    }

}
