package org.cyclops.integratedmekanismics.part.aspect;

import org.cyclops.integrateddynamics.api.part.aspect.IAspectWrite;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeList;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeOperator;
import org.cyclops.integratedmekanismics.value.ValueObjectTypeChemicalStack;

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

    }

}
