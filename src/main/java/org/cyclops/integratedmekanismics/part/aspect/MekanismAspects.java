package org.cyclops.integratedmekanismics.part.aspect;

import org.cyclops.integrateddynamics.api.part.aspect.IAspectWrite;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeList;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeOperator;
import org.cyclops.integratedmekanismics.value.ValueObjectTypeChemicalStack;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectWriteBuilders;

/**
 * Collection of all mekanism aspects.
 * @author rubensworks
 */
public class MekanismAspects {

    public static final class Write {

        public static final class Chemical {

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_EXPORT =
                    MekanismAspectWriteBuilders.Chemical.BUILDER_BOOLEAN
                            .withProperties(MekanismAspectWriteBuilders.Chemical.PROPERTIES_RATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_BOOLEAN_GETRATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeLong.ValueLong, ValueTypeLong> LONG_EXPORT =
                    MekanismAspectWriteBuilders.Chemical.BUILDER_LONG
                            .withProperties(MekanismAspectWriteBuilders.Chemical.PROPERTIES)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueObjectTypeChemicalStack.ValueChemicalStack, ValueObjectTypeChemicalStack> CHEMICALSTACK_EXPORT =
                    MekanismAspectWriteBuilders.Chemical.BUILDER_CHEMICALSTACK
                            .withProperties(MekanismAspectWriteBuilders.Chemical.PROPERTIES_RATECHECKSCRAFT)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALSTACK_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_EXPORT =
                    MekanismAspectWriteBuilders.Chemical.BUILDER_LIST
                            .withProperties(MekanismAspectWriteBuilders.Chemical.PROPERTIES_RATECHECKSLIST)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKLIST_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> PREDICATE_EXPORT =
                    MekanismAspectWriteBuilders.Chemical.BUILDER_OPERATOR
                            .withProperties(MekanismAspectWriteBuilders.Chemical.PROPERTIES_RATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKPREDICATE_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("export").buildWrite();

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_IMPORT =
                    MekanismAspectWriteBuilders.Chemical.BUILDER_BOOLEAN
                            .withProperties(MekanismAspectWriteBuilders.Chemical.PROPERTIES_RATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_BOOLEAN_GETRATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeLong.ValueLong, ValueTypeLong> LONG_IMPORT =
                    MekanismAspectWriteBuilders.Chemical.BUILDER_LONG
                            .withProperties(MekanismAspectWriteBuilders.Chemical.PROPERTIES)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueObjectTypeChemicalStack.ValueChemicalStack, ValueObjectTypeChemicalStack> CHEMICALSTACK_IMPORT =
                    MekanismAspectWriteBuilders.Chemical.BUILDER_CHEMICALSTACK
                            .withProperties(MekanismAspectWriteBuilders.Chemical.PROPERTIES_RATECHECKS)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALSTACK_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_IMPORT =
                    MekanismAspectWriteBuilders.Chemical.BUILDER_LIST
                            .withProperties(MekanismAspectWriteBuilders.Chemical.PROPERTIES_RATECHECKSLIST)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKLIST_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> PREDICATE_IMPORT =
                    MekanismAspectWriteBuilders.Chemical.BUILDER_OPERATOR
                            .withProperties(MekanismAspectWriteBuilders.Chemical.PROPERTIES_RATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKPREDICATE_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("import").buildWrite();

        }

        public static final class World {

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> ENTITY_CHEMICAL_BOOLEAN_IMPORT =
                    MekanismAspectWriteBuilders.World.BUILDER_BOOLEAN
                            .withProperties(MekanismAspectWriteBuilders.World.Chemical.PROPERTIES_RATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_BOOLEAN_GETRATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeLong.ValueLong, ValueTypeLong> ENTITY_CHEMICAL_INTEGER_IMPORT =
                    MekanismAspectWriteBuilders.World.BUILDER_LONG
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("import").buildWrite();
            public static final IAspectWrite<ValueObjectTypeChemicalStack.ValueChemicalStack, ValueObjectTypeChemicalStack> ENTITY_CHEMICAL_CHEMICALSTACK_IMPORT =
                    MekanismAspectWriteBuilders.World.BUILDER_CHEMICALSTACK
                            .withProperties(MekanismAspectWriteBuilders.World.Chemical.PROPERTIES_RATECHECKS)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALSTACK_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> ENTITY_CHEMICAL_LISTCHEMICALSTACK_IMPORT =
                    MekanismAspectWriteBuilders.World.BUILDER_LIST
                            .withProperties(MekanismAspectWriteBuilders.World.Chemical.PROPERTIES_RATECHECKSLIST)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKLIST_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> ENTITY_CHEMICAL_PREDICATECHEMICALSTACK_IMPORT =
                    MekanismAspectWriteBuilders.World.BUILDER_OPERATOR
                            .withProperties(MekanismAspectWriteBuilders.World.Chemical.PROPERTIES_RATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKPREDICATE_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_IMPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("import").buildWrite();

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> ENTITY_CHEMICAL_BOOLEAN_EXPORT =
                    MekanismAspectWriteBuilders.World.BUILDER_BOOLEAN
                            .withProperties(MekanismAspectWriteBuilders.World.Chemical.PROPERTIES_RATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_BOOLEAN_GETRATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeLong.ValueLong, ValueTypeLong> ENTITY_CHEMICAL_INTEGER_EXPORT =
                    MekanismAspectWriteBuilders.World.BUILDER_LONG
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_LONG_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("export").buildWrite();
            public static final IAspectWrite<ValueObjectTypeChemicalStack.ValueChemicalStack, ValueObjectTypeChemicalStack> ENTITY_CHEMICAL_CHEMICALSTACK_EXPORT =
                    MekanismAspectWriteBuilders.World.BUILDER_CHEMICALSTACK
                            .withProperties(MekanismAspectWriteBuilders.World.Chemical.PROPERTIES_RATECHECKSCRAFT)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALSTACK_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> ENTITY_CHEMICAL_LISTCHEMICALSTACK_EXPORT =
                    MekanismAspectWriteBuilders.World.BUILDER_LIST
                            .withProperties(MekanismAspectWriteBuilders.World.Chemical.PROPERTIES_RATECHECKSLIST)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKLIST_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> ENTITY_CHEMICAL_PREDICATECHEMICALSTACK_EXPORT =
                    MekanismAspectWriteBuilders.World.BUILDER_OPERATOR
                            .withProperties(MekanismAspectWriteBuilders.World.Chemical.PROPERTIES_RATE)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKPREDICATE_CHEMICALPREDICATE)
                            .handle(MekanismAspectWriteBuilders.World.Chemical.PROP_ENTITY_CHEMICALTARGET)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_EXPORT)
                            .appendKind("entity").appendKind("chemical").appendKind("export").buildWrite();

        }

        public static final class ChemicalFilter {

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_SET_FILTER =
                    MekanismAspectWriteBuilders.Chemical.BUILDER_BOOLEAN
                            .withProperties(MekanismAspectWriteBuilders.Chemical.PROPERTIES_FILTER)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_BOOLEAN_PREDICATE)
                            .handle(TunnelAspectWriteBuilders.propSetFilter())
                            .appendActivator(TunnelAspectWriteBuilders.PREPARE_FILTER)
                            .appendDeactivator(TunnelAspectWriteBuilders.RESET_FILTER)
                            .appendKind("filter").buildWrite();
            public static final IAspectWrite<ValueObjectTypeChemicalStack.ValueChemicalStack, ValueObjectTypeChemicalStack> CHEMICALSTACK_SET_FILTER =
                    MekanismAspectWriteBuilders.Chemical.BUILDER_CHEMICALSTACK
                            .withProperties(MekanismAspectWriteBuilders.Chemical.PROPERTIES_FILTER_CHECKS)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALSTACK_CHEMICALPREDICATE)
                            .handle(TunnelAspectWriteBuilders.propSetFilter())
                            .appendActivator(TunnelAspectWriteBuilders.PREPARE_FILTER)
                            .appendDeactivator(TunnelAspectWriteBuilders.RESET_FILTER)
                            .appendKind("filter").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_SET_FILTER =
                    MekanismAspectWriteBuilders.Chemical.BUILDER_LIST
                            .withProperties(MekanismAspectWriteBuilders.Chemical.PROPERTIES_FILTER_CHECKS)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKLIST_CHEMICALPREDICATE)
                            .handle(TunnelAspectWriteBuilders.propSetFilter())
                            .appendActivator(TunnelAspectWriteBuilders.PREPARE_FILTER)
                            .appendDeactivator(TunnelAspectWriteBuilders.RESET_FILTER)
                            .appendKind("filter").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> PREDICATE_SET_FILTER =
                    MekanismAspectWriteBuilders.Chemical.BUILDER_OPERATOR
                            .withProperties(MekanismAspectWriteBuilders.Chemical.PROPERTIES_FILTER)
                            .handle(MekanismAspectWriteBuilders.Chemical.PROP_CHEMICALSTACKPREDICATE_CHEMICALPREDICATE)
                            .handle(TunnelAspectWriteBuilders.propSetFilter())
                            .appendActivator(TunnelAspectWriteBuilders.PREPARE_FILTER)
                            .appendDeactivator(TunnelAspectWriteBuilders.RESET_FILTER)
                            .appendKind("filter").buildWrite();

        }

    }

}
