package org.cyclops.integratedmekanism.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.common.registries.MekanismGases;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.cyclops.commoncapabilities.api.ingredient.IMixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.MixedIngredients;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IVariable;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeIngredients;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeList;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.test.IntegrationBefore;
import org.cyclops.integrateddynamics.core.test.IntegrationTest;
import org.cyclops.integrateddynamics.core.test.TestHelpers;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanism.operator.MekanismOperators;
import org.cyclops.integratedmekanism.value.MekanismValueTypes;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author rubensworks
 */
public class TestIngredientsOperators {

    private static final DummyValueType DUMMY_TYPE = DummyValueType.TYPE;
    private static final DummyVariable<DummyValueType.DummyValue> DUMMY_VARIABLE =
            new DummyVariable<DummyValueType.DummyValue>(DUMMY_TYPE, DummyValueType.DummyValue.of());

    private DummyVariable<ValueTypeInteger.ValueInteger> i0;
    private DummyVariable<ValueTypeInteger.ValueInteger> i1;
    private DummyVariable<ValueTypeInteger.ValueInteger> i2;
    private DummyVariable<ValueTypeInteger.ValueInteger> i3;

    private DummyVariableIngredients iChemicals;
    private DummyVariable<ValueTypeList.ValueList> lChemicals;
    private IMixedIngredients inputIngredients;
    private DummyVariableIngredients iMix;

    private DummyVariable<ValueObjectTypeChemicalStack.ValueChemicalStack> iChemical;

    @IntegrationBefore
    public void before() {
        i0 = new DummyVariable<>(ValueTypes.INTEGER, ValueTypeInteger.ValueInteger.of(0));
        i1 = new DummyVariable<>(ValueTypes.INTEGER, ValueTypeInteger.ValueInteger.of(1));
        i2 = new DummyVariable<>(ValueTypes.INTEGER, ValueTypeInteger.ValueInteger.of(2));
        i3 = new DummyVariable<>(ValueTypes.INTEGER, ValueTypeInteger.ValueInteger.of(3));

        iChemicals = new DummyVariableIngredients(ValueObjectTypeIngredients.ValueIngredients.of(
                MixedIngredients.ofInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(
                        new GasStack(MekanismGases.STEAM, 1000),
                        new GasStack(MekanismGases.HYDROGEN, 123))
                )));
        lChemicals = new DummyVariable<>(ValueTypes.LIST, ValueTypeList.ValueList.ofAll(
                ValueObjectTypeChemicalStack.ValueChemicalStack.of(new GasStack(MekanismGases.STEAM, 1000)),
                ValueObjectTypeChemicalStack.ValueChemicalStack.of(new GasStack(MekanismGases.HYDROGEN, 123))
        ));

        Map<IngredientComponent<?, ?>, List<?>> ingredients = Maps.newIdentityHashMap();
        ingredients.put(IngredientComponent.ENERGY, Lists.newArrayList(777L));
        ingredients.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Lists.newArrayList(new GasStack(MekanismGases.HYDROGEN, 125)));
        ingredients.put(IngredientComponent.ITEMSTACK, Lists.newArrayList(new ItemStack(Items.OAK_BOAT), new ItemStack(Item.byBlock(Blocks.STONE))));
        inputIngredients = new MixedIngredients(ingredients);
        iMix = new DummyVariableIngredients(ValueObjectTypeIngredients.ValueIngredients.of(inputIngredients));

        iChemical = new DummyVariable<>(MekanismValueTypes.OBJECT_CHEMICALSTACK, ValueObjectTypeChemicalStack.ValueChemicalStack.of(new GasStack(MekanismGases.HYDROGEN, 123)));
    }

    /**
     * ----------------------------------- CHEMICALS -----------------------------------
     */

    @IntegrationTest
    public void testChemicals() throws EvaluationException {
        IValue res1 = MekanismOperators.INGREDIENTS_CHEMICALS.evaluate(iChemicals);
        Asserts.check(res1 instanceof ValueTypeList.ValueList, "result is a list");
        TestHelpers.assertEqual(((ValueTypeList.ValueList) res1).getRawValue().getLength(), 2, "chemicals(chemicals, 0).size = 2");
        TestHelpers.assertEqual(res1,
                ValueTypeList.ValueList.ofList(MekanismValueTypes.OBJECT_CHEMICALSTACK, Lists.newArrayList(
                        ValueObjectTypeChemicalStack.ValueChemicalStack.of(new GasStack(MekanismGases.STEAM, 1000)),
                        ValueObjectTypeChemicalStack.ValueChemicalStack.of(new GasStack(MekanismGases.HYDROGEN, 123))
                )), "chemicals(chemicals) = lava, water");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testChemicalsSizeLarge() throws EvaluationException {
        MekanismOperators.INGREDIENTS_CHEMICALS.evaluate(iMix, i0);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testChemicalsSizeSmall() throws EvaluationException {
        MekanismOperators.INGREDIENTS_CHEMICALS.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testChemicalsSize() throws EvaluationException {
        MekanismOperators.INGREDIENTS_CHEMICALS.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- WITH_CHEMICAL -----------------------------------
     */

    @IntegrationTest
    public void testWithChemical() throws EvaluationException {
        IValue res1 = MekanismOperators.INGREDIENTS_WITH_CHEMICAL.evaluate(iMix, i0, iChemical);
        Asserts.check(res1 instanceof ValueObjectTypeIngredients.ValueIngredients, "result is an ingredient");
        IMixedIngredients outputIngredients1 = ((ValueObjectTypeIngredients.ValueIngredients) res1).getRawValue().get();
        List<ChemicalStack<?>> outputList1 = outputIngredients1.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK);
        TestHelpers.assertEqual(outputList1.size(), 1, "with_chemicals(mix, 0, chemicals)[0]size = 1");
        TestHelpers.assertEqual(outputList1.get(0), new GasStack(MekanismGases.HYDROGEN, 123),
                "with_chemicals(mix, 0, chemicals)[0] = chemicals[0]");

        TestHelpers.assertEqual(outputIngredients1.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).size(), inputIngredients.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).size(), "Chemicals size remains the same");
        TestHelpers.assertNonEqual(ValueObjectTypeChemicalStack.ValueChemicalStack.of(outputIngredients1.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0)), ValueObjectTypeChemicalStack.ValueChemicalStack.of(inputIngredients.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(0)), "Chemicals 0 changes");
        TestHelpers.assertEqual(outputIngredients1.getInstances(IngredientComponent.ITEMSTACK), inputIngredients.getInstances(IngredientComponent.ITEMSTACK), "Items remains the same");
        TestHelpers.assertEqual(outputIngredients1.getInstances(IngredientComponent.ENERGY), inputIngredients.getInstances(IngredientComponent.ENERGY), "Energy remains the same");


        IValue res2 = MekanismOperators.INGREDIENTS_WITH_CHEMICAL.evaluate(new IVariable[]{iMix, i2, iChemical});
        IMixedIngredients outputIngredients2 = ((ValueObjectTypeIngredients.ValueIngredients) res2).getRawValue().get();
        List<ChemicalStack<?>> outputList2 = outputIngredients2.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK);
        TestHelpers.assertEqual(outputList2.size(), 3, "with_chemicals(mix, 3, chemicals)[0]size = 2");
        TestHelpers.assertEqual(outputList2.get(0), new GasStack(MekanismGases.HYDROGEN, 125),
                "with_chemicals(mix, 2, chemicals)[0] = chemicals[0]");
        TestHelpers.assertEqual(outputList2.get(1), GasStack.EMPTY,
                "with_chemicals(mix, 2, chemicals)[1] = chemicals[1]");
        TestHelpers.assertEqual(outputList2.get(2), new GasStack(MekanismGases.HYDROGEN, 123),
                "with_chemicals(mix, 2, chemicals)[2] = chemicals[2]");

        TestHelpers.assertNonEqual(outputIngredients2.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).size(), inputIngredients.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).size(), "Chemicals size changes");
        TestHelpers.assertNonEqual(outputIngredients2.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).get(2), Collections.emptyList(), "Chemicals 3 is not empty");
        TestHelpers.assertEqual(outputIngredients2.getInstances(IngredientComponent.ITEMSTACK), inputIngredients.getInstances(IngredientComponent.ITEMSTACK), "Items remains the same");
        TestHelpers.assertEqual(outputIngredients2.getInstances(IngredientComponent.ENERGY), inputIngredients.getInstances(IngredientComponent.ENERGY), "Energy remains the same");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testWithChemicalSizeLarge() throws EvaluationException {
        MekanismOperators.INGREDIENTS_WITH_CHEMICAL.evaluate(iMix, i0, iChemical);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testWithChemicalSizeSmall() throws EvaluationException {
        MekanismOperators.INGREDIENTS_WITH_CHEMICAL.evaluate(iMix, i0);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testWithChemicalSize() throws EvaluationException {
        MekanismOperators.INGREDIENTS_WITH_CHEMICAL.evaluate(DUMMY_VARIABLE, DUMMY_VARIABLE, DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- WITH_CHEMICALS -----------------------------------
     */

    @IntegrationTest
    public void testWithChemicals() throws EvaluationException {
        IValue res1 = MekanismOperators.INGREDIENTS_WITH_CHEMICALS.evaluate(new IVariable[]{iMix, lChemicals});
        Asserts.check(res1 instanceof ValueObjectTypeIngredients.ValueIngredients, "result is an ingredient");
        IMixedIngredients outputIngredients1 = ((ValueObjectTypeIngredients.ValueIngredients) res1).getRawValue().get();
        List<ChemicalStack<?>> outputList1 = outputIngredients1.getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK);
        TestHelpers.assertEqual(outputList1.size(), 2, "with_chemicals(mix, chemicals)[0]size = 2");
        TestHelpers.assertEqual(outputList1.get(0), new GasStack(MekanismGases.STEAM, 1000),
                "with_chemicals(mix, chemicals)[0] = chemicals[0]");
        TestHelpers.assertEqual(outputList1.get(1), new GasStack(MekanismGases.HYDROGEN, 123),
                "with_chemicals(mix, chemicals)[1] = chemicals[1]");

        TestHelpers.assertEqual(outputIngredients1.getInstances(IngredientComponent.ITEMSTACK), inputIngredients.getInstances(IngredientComponent.ITEMSTACK), "Item remains the same");
        TestHelpers.assertEqual(outputIngredients1.getInstances(IngredientComponent.ENERGY), inputIngredients.getInstances(IngredientComponent.ENERGY), "Energy remains the same");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testWithChemicalsSizeLarge() throws EvaluationException {
        MekanismOperators.INGREDIENTS_WITH_CHEMICALS.evaluate(iMix, lChemicals);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testWithChemicalsSizeSmall() throws EvaluationException {
        MekanismOperators.INGREDIENTS_WITH_CHEMICALS.evaluate(iMix);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testWithChemicalsSize() throws EvaluationException {
        MekanismOperators.INGREDIENTS_WITH_CHEMICALS.evaluate(DUMMY_VARIABLE, DUMMY_VARIABLE);
    }

}
