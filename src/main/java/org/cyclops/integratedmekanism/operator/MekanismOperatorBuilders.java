package org.cyclops.integratedmekanism.operator;

import mekanism.api.chemical.ChemicalStack;
import net.minecraft.world.item.ItemStack;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import org.cyclops.integrateddynamics.core.evaluate.IOperatorValuePropagator;
import org.cyclops.integrateddynamics.core.evaluate.OperatorBuilders;
import org.cyclops.integrateddynamics.core.evaluate.build.OperatorBuilder;
import org.cyclops.integrateddynamics.core.evaluate.operator.IterativeFunction;
import org.cyclops.integrateddynamics.core.evaluate.operator.OperatorBase;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.value.MekanismValueTypes;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;

/**
 * @author rubensworks
 */
public class MekanismOperatorBuilders {

    // --------------- Value propagators ---------------
    public static final IOperatorValuePropagator<ChemicalStack<?>, IValue> PROPAGATOR_CHEMICALSTACK_VALUE = ValueObjectTypeChemicalStack.ValueChemicalStack::of;

    // --------------- ChemicalStack builders ---------------
    public static final OperatorBuilder<OperatorBase.SafeVariablesGetter> CHEMICALSTACK = OperatorBuilder.forType(MekanismValueTypes.OBJECT_CHEMICALSTACK).modId(Reference.MOD_ID).appendKind("chemicalstack");
    public static final OperatorBuilder<OperatorBase.SafeVariablesGetter> CHEMICALSTACK_1_SUFFIX_LONG = CHEMICALSTACK.inputTypes(1, MekanismValueTypes.OBJECT_CHEMICALSTACK).modId(Reference.MOD_ID).renderPattern(IConfigRenderPattern.SUFFIX_1_LONG);
    public static final OperatorBuilder<OperatorBase.SafeVariablesGetter> CHEMICALSTACK_2 = CHEMICALSTACK.inputTypes(2, MekanismValueTypes.OBJECT_CHEMICALSTACK).modId(Reference.MOD_ID).renderPattern(IConfigRenderPattern.INFIX);
    public static final OperatorBuilder<OperatorBase.SafeVariablesGetter> CHEMICALSTACK_2_LONG = CHEMICALSTACK.inputTypes(2, MekanismValueTypes.OBJECT_CHEMICALSTACK).modId(Reference.MOD_ID).renderPattern(IConfigRenderPattern.INFIX_LONG);
    public static final IterativeFunction.PrePostBuilder<ChemicalStack<?>, IValue> FUNCTION_CHEMICALSTACK = IterativeFunction.PrePostBuilder.begin()
            .appendPre(input -> {
                ValueObjectTypeChemicalStack.ValueChemicalStack a = input.getValue(0, MekanismValueTypes.OBJECT_CHEMICALSTACK);
                return a.getRawValue();
            });
    public static final IterativeFunction.PrePostBuilder<ChemicalStack<?>, Integer> FUNCTION_CHEMICALSTACK_TO_INT =
            FUNCTION_CHEMICALSTACK.appendPost(OperatorBuilders.PROPAGATOR_INTEGER_VALUE);
    public static final IterativeFunction.PrePostBuilder<ChemicalStack<?>, Long> FUNCTION_CHEMICALSTACK_TO_LONG =
            FUNCTION_CHEMICALSTACK.appendPost(OperatorBuilders.PROPAGATOR_LONG_VALUE);
    public static final IterativeFunction.PrePostBuilder<ItemStack, Long> FUNCTION_ITEMSTACK_TO_LONG =
            OperatorBuilders.FUNCTION_ITEMSTACK.appendPost(OperatorBuilders.PROPAGATOR_LONG_VALUE);
    public static final IterativeFunction.PrePostBuilder<ChemicalStack<?>, Double> FUNCTION_CHEMICALSTACK_TO_DOUBLE =
            FUNCTION_CHEMICALSTACK.appendPost(OperatorBuilders.PROPAGATOR_DOUBLE_VALUE);
    public static final IterativeFunction.PrePostBuilder<ChemicalStack<?>, Boolean> FUNCTION_CHEMICALSTACK_TO_BOOLEAN =
            FUNCTION_CHEMICALSTACK.appendPost(OperatorBuilders.PROPAGATOR_BOOLEAN_VALUE);
    public static final IterativeFunction.PrePostBuilder<ChemicalStack<?>, ChemicalStack<?>> FUNCTION_CHEMICALSTACK_TO_CHEMICALSTACK =
            FUNCTION_CHEMICALSTACK.appendPost(PROPAGATOR_CHEMICALSTACK_VALUE);

    // --------------- Ingredients builders ---------------
    public static final OperatorBuilder<OperatorBase.SafeVariablesGetter> INGREDIENTS_3_CHEMICALSTACK = OperatorBuilders.INGREDIENTS
            .modId(Reference.MOD_ID)
            .inputTypes(ValueTypes.OBJECT_INGREDIENTS, ValueTypes.INTEGER, MekanismValueTypes.OBJECT_CHEMICALSTACK)
            .renderPattern(IConfigRenderPattern.INFIX_2_LONG).output(ValueTypes.OBJECT_INGREDIENTS);

}
