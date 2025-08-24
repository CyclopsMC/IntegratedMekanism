package org.cyclops.integratedmekanismics.test;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismInfuseTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import org.cyclops.integrateddynamics.core.test.IntegrationBefore;
import org.cyclops.integrateddynamics.core.test.IntegrationTest;
import org.cyclops.integrateddynamics.core.test.TestHelpers;
import org.cyclops.integratedmekanismics.core.ChemicalHelpers;
import org.cyclops.integratedmekanismics.operator.MekanismOperators;
import org.cyclops.integratedmekanismics.value.ValueObjectTypeChemicalStack;

/**
 * @author rubensworks
 */
public class TestItemStackOperators {

    private static final DummyValueType DUMMY_TYPE = DummyValueType.TYPE;
    private static final DummyVariable<DummyValueType.DummyValue> DUMMY_VARIABLE = new DummyVariable<>(DUMMY_TYPE, DummyValueType.DummyValue.of());

    private DummyVariableItemStack iApple;
    private DummyVariableItemStack iHoe;
    private DummyVariableItemStack iTankEmpty;
    private DummyVariableItemStack iTankHydrogen;
    private DummyVariableItemStack iTankGold;

    @IntegrationBefore
    public void before() {
        iApple = new DummyVariableItemStack(ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.APPLE)));
        iHoe = new DummyVariableItemStack(ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.DIAMOND_HOE)));
        iTankEmpty = new DummyVariableItemStack(ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(MekanismBlocks.BASIC_CHEMICAL_TANK)));
        iTankHydrogen = new DummyVariableItemStack(ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(MekanismBlocks.BASIC_CHEMICAL_TANK)));
        iTankHydrogen.getValue().getRawValue().getCapability(Capabilities.GAS_HANDLER)
                .ifPresent(h -> h.insertChemical(new GasStack(MekanismGases.HYDROGEN, 1000), Action.EXECUTE));
        iTankGold = new DummyVariableItemStack(ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(MekanismBlocks.BASIC_CHEMICAL_TANK)));
        iTankGold.getValue().getRawValue().getCapability(Capabilities.INFUSION_HANDLER)
                .ifPresent(h -> h.insertChemical(new InfusionStack(MekanismInfuseTypes.GOLD, 100), Action.EXECUTE));
    }

    /**
     * ----------------------------------- ISCHEMICALSTACK -----------------------------------
     */

    @IntegrationTest
    public void testItemStackIsChemicalStack() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_ITEMSTACK_ISCHEMICALSTACK.evaluate(iHoe);
        Asserts.check(res1 instanceof ValueTypeBoolean.ValueBoolean, "result is a boolean");
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res1).getRawValue(), false, "ischemicalstack(hoe) = false");

        IValue res2 = MekanismOperators.OBJECT_ITEMSTACK_ISCHEMICALSTACK.evaluate(iTankHydrogen);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res2).getRawValue(), true, "ischemicalstack(tankhydrogen) = true");

        IValue res3 = MekanismOperators.OBJECT_ITEMSTACK_ISCHEMICALSTACK.evaluate(iTankEmpty);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res3).getRawValue(), true, "ischemicalstack(tankempty) = true");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputIsChemicalStackIsChemicalStackLarge() throws EvaluationException {
        MekanismOperators.OBJECT_ITEMSTACK_ISCHEMICALSTACK.evaluate(iApple, iApple);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputIsChemicalStackIsChemicalStackSmall() throws EvaluationException {
        MekanismOperators.OBJECT_ITEMSTACK_ISCHEMICALSTACK.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeIsChemicalStack() throws EvaluationException {
        MekanismOperators.OBJECT_ITEMSTACK_ISCHEMICALSTACK.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- CHEMICALSTACK -----------------------------------
     */

    @IntegrationTest
    public void testItemStackChemicalStack() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_ITEMSTACK_CHEMICALSTACK.evaluate(iHoe);
        Asserts.check(res1 instanceof ValueObjectTypeChemicalStack.ValueChemicalStack, "result is a chemicalstack");
        TestHelpers.assertEqual(((ValueObjectTypeChemicalStack.ValueChemicalStack) res1).getRawValue().isEmpty(), true, "chemicalstack(hoe) = null");

        IValue res2 = MekanismOperators.OBJECT_ITEMSTACK_CHEMICALSTACK.evaluate(iTankHydrogen);
        TestHelpers.assertEqual(((ValueObjectTypeChemicalStack.ValueChemicalStack) res2).getRawValue().isStackIdentical((ChemicalStack) new GasStack(MekanismGases.HYDROGEN, ChemicalHelpers.BUCKET_VOLUME)), true, "chemicalstack(tankhydrogen) = hydrogen:1000");
        TestHelpers.assertEqual(((ValueObjectTypeChemicalStack.ValueChemicalStack) res2).getRawValue().isStackIdentical((ChemicalStack) new InfusionStack(MekanismInfuseTypes.GOLD, ChemicalHelpers.BUCKET_VOLUME)), false, "chemicalstack(tankhydrogen) != gold:1000");

        IValue res3 = MekanismOperators.OBJECT_ITEMSTACK_CHEMICALSTACK.evaluate(iTankGold);
        TestHelpers.assertEqual(((ValueObjectTypeChemicalStack.ValueChemicalStack) res3).getRawValue().isStackIdentical((ChemicalStack) new InfusionStack(MekanismInfuseTypes.GOLD, 100)), true, "chemicalstack(tankgold) = gold:100");

        IValue res4 = MekanismOperators.OBJECT_ITEMSTACK_CHEMICALSTACK.evaluate(iTankEmpty);
        TestHelpers.assertEqual(((ValueObjectTypeChemicalStack.ValueChemicalStack) res4).getRawValue().isEmpty(), true, "chemicalstack(empty) = empty");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputChemicalStackChemicalStackLarge() throws EvaluationException {
        MekanismOperators.OBJECT_ITEMSTACK_CHEMICALSTACK.evaluate(iApple, iApple);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputChemicalStackChemicalStackSmall() throws EvaluationException {
        MekanismOperators.OBJECT_ITEMSTACK_CHEMICALSTACK.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeChemicalStack() throws EvaluationException {
        MekanismOperators.OBJECT_ITEMSTACK_CHEMICALSTACK.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- CHEMICALSTACK_CAPACITY -----------------------------------
     */

    @IntegrationTest
    public void testItemStackChemicalStackCapacity() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_ITEMSTACK_CHEMICALSTACKCAPACITY.evaluate(iHoe);
        Asserts.check(res1 instanceof ValueTypeLong.ValueLong, "result is a long");
        TestHelpers.assertEqual(((ValueTypeLong.ValueLong) res1).getRawValue(), 0L, "chemicalstackcapacity(hoe) = 0");

        IValue res2 = MekanismOperators.OBJECT_ITEMSTACK_CHEMICALSTACKCAPACITY.evaluate(iTankHydrogen);
        TestHelpers.assertEqual(((ValueTypeLong.ValueLong) res2).getRawValue(), 64000L, "chemicalstackcapacity(tankhydrogen) = 64000");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputChemicalStackCapacityChemicalStackCapacityLarge() throws EvaluationException {
        MekanismOperators.OBJECT_ITEMSTACK_CHEMICALSTACKCAPACITY.evaluate(iApple, iApple);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputChemicalStackCapacityChemicalStackCapacitySmall() throws EvaluationException {
        MekanismOperators.OBJECT_ITEMSTACK_CHEMICALSTACKCAPACITY.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeChemicalStackCapacity() throws EvaluationException {
        MekanismOperators.OBJECT_ITEMSTACK_CHEMICALSTACKCAPACITY.evaluate(DUMMY_VARIABLE);
    }

}
