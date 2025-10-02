package org.cyclops.integratedmekanism.test;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.text.EnumColor;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.resource.PrimaryResource;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;
import org.cyclops.integrateddynamics.core.test.IntegrationBefore;
import org.cyclops.integrateddynamics.core.test.IntegrationTest;
import org.cyclops.integrateddynamics.core.test.TestHelpers;
import org.cyclops.integratedmekanism.core.ChemicalHelpers;
import org.cyclops.integratedmekanism.operator.MekanismOperators;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;

/**
 * @author rubensworks
 */
public class TestChemicalStackOperators {

    private static final DummyValueType DUMMY_TYPE = DummyValueType.TYPE;
    private static final DummyVariable<DummyValueType.DummyValue> DUMMY_VARIABLE = new DummyVariable<>(DUMMY_TYPE, DummyValueType.DummyValue.of());

    private DummyVariableChemicalStack eHydrogen;
    private DummyVariableChemicalStack eHydrogen100;
    private DummyVariableChemicalStack eSteam;
    private DummyVariableChemicalStack eGold;
    private DummyVariableChemicalStack ePlutonium;
    private DummyVariableChemicalStack eSodium;
    private DummyVariableChemicalStack eSodiumSuperheated;
    private DummyVariableChemicalStack eRed;
    private DummyVariableChemicalStack eIron;
    private DummyVariable<ValueTypeLong.ValueLong> l100;
    private DummyVariable<ValueTypeString.ValueString> sWaterVapor;

    @IntegrationBefore
    public void before() {
        eHydrogen = new DummyVariableChemicalStack(ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.HYDROGEN, 1000)));
        eHydrogen100 = new DummyVariableChemicalStack(ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.HYDROGEN, 100)));
        eSteam = new DummyVariableChemicalStack(ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.STEAM, 1000)));
        eGold = new DummyVariableChemicalStack(ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.GOLD, 1000)));
        ePlutonium = new DummyVariableChemicalStack(ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.PLUTONIUM, 1000)));
        eSodium = new DummyVariableChemicalStack(ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.SODIUM, 1000)));
        eSodiumSuperheated = new DummyVariableChemicalStack(ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.SUPERHEATED_SODIUM, 1000)));
        eRed = new DummyVariableChemicalStack(ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 1000)));
        eIron = new DummyVariableChemicalStack(ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.PROCESSED_RESOURCES.get(PrimaryResource.IRON).getCleanSlurry(), 1000)));
        l100 = new DummyVariable<>(ValueTypes.LONG, ValueTypeLong.ValueLong.of(100));
        sWaterVapor = new DummyVariable<>(ValueTypes.STRING, ValueTypeString.ValueString.of("mekanism:water_vapor"));
    }

    /**
     * ----------------------------------- AMOUNT -----------------------------------
     */

    @IntegrationTest
    public void testAmount() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_AMOUNT.evaluate(eHydrogen);
        Asserts.check(res1 instanceof ValueTypeLong.ValueLong, "result is a long");
        TestHelpers.assertEqual(((ValueTypeLong.ValueLong) res1).getRawValue(), ChemicalHelpers.BUCKET_VOLUME, "amount(hydrogen:1000) = 1000");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_AMOUNT.evaluate(eGold);
        TestHelpers.assertEqual(((ValueTypeLong.ValueLong) res2).getRawValue(), ChemicalHelpers.BUCKET_VOLUME, "amount(gold:1000) = 1000");

        IValue res3 = MekanismOperators.OBJECT_CHEMICALSTACK_AMOUNT.evaluate(eHydrogen100);
        TestHelpers.assertEqual(((ValueTypeLong.ValueLong) res3).getRawValue(), 100L, "amount(hydrogen:100) = 100");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeAmountLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_AMOUNT.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeAmountSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_AMOUNT.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeAmount() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_AMOUNT.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- TINT -----------------------------------
     */

    @IntegrationTest
    public void testTint() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_TINT.evaluate(eHydrogen);
        Asserts.check(res1 instanceof ValueTypeInteger.ValueInteger, "result is an integer");
        TestHelpers.assertEqual(((ValueTypeInteger.ValueInteger) res1).getRawValue(), -1, "tint(hydrogen) = -1");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_TINT.evaluate(eGold);
        TestHelpers.assertEqual(((ValueTypeInteger.ValueInteger) res2).getRawValue(), 15912295, "tint(gold) = 15912295");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeTintLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_TINT.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeTintSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_TINT.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeTint() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_TINT.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- ISRADIOACTIVE -----------------------------------
     */

    @IntegrationTest
    public void testIsRadioactive() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_ISRADIOACTIVE.evaluate(eHydrogen);
        Asserts.check(res1 instanceof ValueTypeBoolean.ValueBoolean, "result is a boolean");
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res1).getRawValue(), false, "isradioactive(hydrogen) = false");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_ISRADIOACTIVE.evaluate(eGold);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res2).getRawValue(), false, "isradioactive(gold) = false");

        IValue res3 = MekanismOperators.OBJECT_CHEMICALSTACK_ISRADIOACTIVE.evaluate(ePlutonium);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res3).getRawValue(), true, "isradioactive(plutonium) = true");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeIsRadioactiveLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISRADIOACTIVE.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeIsRadioactiveSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISRADIOACTIVE.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeIsRadioactive() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISRADIOACTIVE.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- RADIOACTIVITY -----------------------------------
     */

    @IntegrationTest
    public void testRadioactivity() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_RADIOACTIVITY.evaluate(eHydrogen);
        Asserts.check(res1 instanceof ValueTypeDouble.ValueDouble, "result is a double");
        TestHelpers.assertEqual(((ValueTypeDouble.ValueDouble) res1).getRawValue(), 0D, "radioactivity(hydrogen) = 0");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_RADIOACTIVITY.evaluate(eGold);
        TestHelpers.assertEqual(((ValueTypeDouble.ValueDouble) res2).getRawValue(), 0D, "radioactivity(gold) = 0");

        IValue res3 = MekanismOperators.OBJECT_CHEMICALSTACK_RADIOACTIVITY.evaluate(ePlutonium);
        TestHelpers.assertEqual(((ValueTypeDouble.ValueDouble) res3).getRawValue(), 0.02D, "radioactivity(plutonium) = 0.02");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeRadioactivityLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_RADIOACTIVITY.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeRadioactivitySmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_RADIOACTIVITY.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeRadioactivity() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_RADIOACTIVITY.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- ISCOOLANT -----------------------------------
     */

    @IntegrationTest
    public void testIsCoolant() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_ISCOOLANT.evaluate(eHydrogen);
        Asserts.check(res1 instanceof ValueTypeBoolean.ValueBoolean, "result is a boolean");
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res1).getRawValue(), false, "iscoolant(hydrogen) = false");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_ISCOOLANT.evaluate(eGold);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res2).getRawValue(), false, "iscoolant(gold) = false");

        IValue res3 = MekanismOperators.OBJECT_CHEMICALSTACK_ISCOOLANT.evaluate(eSodium);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res3).getRawValue(), true, "iscoolant(sodium) = true");

        IValue res4 = MekanismOperators.OBJECT_CHEMICALSTACK_ISCOOLANT.evaluate(eSodiumSuperheated);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res4).getRawValue(), true, "iscoolant(sodiumSuperheated) = true");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeIsCoolantLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISCOOLANT.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeIsCoolantSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISCOOLANT.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeIsCoolant() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISCOOLANT.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- THERMALENTHALPY -----------------------------------
     */

    @IntegrationTest
    public void testThermalEnthalpy() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_THERMALENTHALPY.evaluate(eHydrogen);
        Asserts.check(res1 instanceof ValueTypeDouble.ValueDouble, "result is a double");
        TestHelpers.assertEqual(((ValueTypeDouble.ValueDouble) res1).getRawValue(), 0D, "thermalenthalpy(hydrogen) = 0");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_THERMALENTHALPY.evaluate(eGold);
        TestHelpers.assertEqual(((ValueTypeDouble.ValueDouble) res2).getRawValue(), 0D, "thermalenthalpy(gold) = 0");

        IValue res3 = MekanismOperators.OBJECT_CHEMICALSTACK_THERMALENTHALPY.evaluate(eSodium);
        TestHelpers.assertEqual(((ValueTypeDouble.ValueDouble) res3).getRawValue(), 5.0D, "thermalenthalpy(sodium) = 5.0");

        IValue res4 = MekanismOperators.OBJECT_CHEMICALSTACK_THERMALENTHALPY.evaluate(eSodiumSuperheated);
        TestHelpers.assertEqual(((ValueTypeDouble.ValueDouble) res4).getRawValue(), 5.0D, "thermalenthalpy(sodiumSuperheated) = 5.0");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeThermalEnthalpyLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_THERMALENTHALPY.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeThermalEnthalpySmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_THERMALENTHALPY.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeThermalEnthalpy() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_THERMALENTHALPY.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- CONDUCTIVITY -----------------------------------
     */

    @IntegrationTest
    public void testConductivity() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_CONDUCTIVITY.evaluate(eHydrogen);
        Asserts.check(res1 instanceof ValueTypeDouble.ValueDouble, "result is a double");
        TestHelpers.assertEqual(((ValueTypeDouble.ValueDouble) res1).getRawValue(), 0D, "conductivity(hydrogen) = 0");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_CONDUCTIVITY.evaluate(eGold);
        TestHelpers.assertEqual(((ValueTypeDouble.ValueDouble) res2).getRawValue(), 0D, "conductivity(gold) = 0");

        IValue res3 = MekanismOperators.OBJECT_CHEMICALSTACK_CONDUCTIVITY.evaluate(eSodium);
        TestHelpers.assertEqual(((ValueTypeDouble.ValueDouble) res3).getRawValue(), 5.0D, "conductivity(sodium) = 1.0");

        IValue res4 = MekanismOperators.OBJECT_CHEMICALSTACK_CONDUCTIVITY.evaluate(eSodiumSuperheated);
        TestHelpers.assertEqual(((ValueTypeDouble.ValueDouble) res4).getRawValue(), 5.0D, "conductivity(sodiumSuperheated) = 1.0");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeConductivityLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_CONDUCTIVITY.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeConductivitySmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_CONDUCTIVITY.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeConductivity() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_CONDUCTIVITY.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- ISCOOLEDCOOLANT -----------------------------------
     */

    @IntegrationTest
    public void testIsCooledCoolant() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_ISCOOLEDCOOLANT.evaluate(eHydrogen);
        Asserts.check(res1 instanceof ValueTypeBoolean.ValueBoolean, "result is a boolean");
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res1).getRawValue(), false, "iscooledcoolant(hydrogen) = false");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_ISCOOLEDCOOLANT.evaluate(eGold);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res2).getRawValue(), false, "iscooledcoolant(gold) = false");

        IValue res3 = MekanismOperators.OBJECT_CHEMICALSTACK_ISCOOLEDCOOLANT.evaluate(eSodium);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res3).getRawValue(), true, "iscooledcoolant(sodium) = true");

        IValue res4 = MekanismOperators.OBJECT_CHEMICALSTACK_ISCOOLEDCOOLANT.evaluate(eSodiumSuperheated);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res4).getRawValue(), false, "iscooledcoolant(sodiumSuperheated) = false");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeIsCooledCoolantLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISCOOLEDCOOLANT.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeIsCooledCoolantSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISCOOLEDCOOLANT.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeIsCooledCoolant() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISCOOLEDCOOLANT.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- COOLEDCOOLANTOF -----------------------------------
     */

    @IntegrationTest
    public void testCooledCoolantOf() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_COOLEDCOOLANTOF.evaluate(eHydrogen);
        Asserts.check(res1 instanceof ValueObjectTypeChemicalStack.ValueChemicalStack, "result is a chemical");
        TestHelpers.assertEqual(((ValueObjectTypeChemicalStack.ValueChemicalStack) res1).getRawValue().isEmpty(), true, "cooledcoolantof(hydrogen) = empty");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_COOLEDCOOLANTOF.evaluate(eGold);
        TestHelpers.assertEqual(((ValueObjectTypeChemicalStack.ValueChemicalStack) res2).getRawValue().isEmpty(), true, "cooledcoolantof(gold) = empty");

        IValue res3 = MekanismOperators.OBJECT_CHEMICALSTACK_COOLEDCOOLANTOF.evaluate(eSodium);
        TestHelpers.assertEqual(((ValueObjectTypeChemicalStack.ValueChemicalStack) res3).getRawValue().equals(new ChemicalStack(MekanismChemicals.SUPERHEATED_SODIUM, 1000)), true, "cooledcoolantof(sodiumSuperheated) = true");

        IValue res4 = MekanismOperators.OBJECT_CHEMICALSTACK_COOLEDCOOLANTOF.evaluate(eSodiumSuperheated);
        TestHelpers.assertEqual(((ValueObjectTypeChemicalStack.ValueChemicalStack) res4).getRawValue().isEmpty(), true, "cooledcoolantof(sodiumSuperheated) = empty");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeCooledCoolantOfLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_COOLEDCOOLANTOF.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeCooledCoolantOfSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_COOLEDCOOLANTOF.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeCooledCoolantOf() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_COOLEDCOOLANTOF.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- ISHEATEDCOOLANT -----------------------------------
     */

    @IntegrationTest
    public void testIsHeatedCoolant() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_ISHEATEDCOOLANT.evaluate(eHydrogen);
        Asserts.check(res1 instanceof ValueTypeBoolean.ValueBoolean, "result is a boolean");
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res1).getRawValue(), false, "isheatedcoolant(hydrogen) = false");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_ISHEATEDCOOLANT.evaluate(eGold);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res2).getRawValue(), false, "isheatedcoolant(gold) = false");

        IValue res3 = MekanismOperators.OBJECT_CHEMICALSTACK_ISHEATEDCOOLANT.evaluate(eSodium);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res3).getRawValue(), false, "isheatedcoolant(sodium) = false");

        IValue res4 = MekanismOperators.OBJECT_CHEMICALSTACK_ISHEATEDCOOLANT.evaluate(eSodiumSuperheated);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res4).getRawValue(), true, "isheatedcoolant(sodiumSuperheated) = true");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeIsHeatedCoolantLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISHEATEDCOOLANT.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeIsHeatedCoolantSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISHEATEDCOOLANT.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeIsHeatedCoolant() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISHEATEDCOOLANT.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- HEATEDCOOLANTOF -----------------------------------
     */

    @IntegrationTest
    public void testHeatedCoolantOf() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_HEATEDCOOLANTOF.evaluate(eHydrogen);
        Asserts.check(res1 instanceof ValueObjectTypeChemicalStack.ValueChemicalStack, "result is a chemical");
        TestHelpers.assertEqual(((ValueObjectTypeChemicalStack.ValueChemicalStack) res1).getRawValue().isEmpty(), true, "heatedcoolantof(hydrogen) = empty");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_HEATEDCOOLANTOF.evaluate(eGold);
        TestHelpers.assertEqual(((ValueObjectTypeChemicalStack.ValueChemicalStack) res2).getRawValue().isEmpty(), true, "heatedcoolantof(gold) = empty");

        IValue res3 = MekanismOperators.OBJECT_CHEMICALSTACK_HEATEDCOOLANTOF.evaluate(eSodium);
        TestHelpers.assertEqual(((ValueObjectTypeChemicalStack.ValueChemicalStack) res3).getRawValue().isEmpty(), true, "heatedcoolantof(sodium) = empty");

        IValue res4 = MekanismOperators.OBJECT_CHEMICALSTACK_HEATEDCOOLANTOF.evaluate(eSodiumSuperheated);
        TestHelpers.assertEqual(((ValueObjectTypeChemicalStack.ValueChemicalStack) res4).getRawValue().equals(new ChemicalStack(MekanismChemicals.SODIUM, 1000)), true, "heatedcoolantof(sodiumSuperheated) = sodium");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeHeatedCoolantOfLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_HEATEDCOOLANTOF.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeHeatedCoolantOfSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_HEATEDCOOLANTOF.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeHeatedCoolantOf() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_HEATEDCOOLANTOF.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- ISFUEL -----------------------------------
     */

    @IntegrationTest
    public void testIsFuel() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_ISFUEL.evaluate(eHydrogen);
        Asserts.check(res1 instanceof ValueTypeBoolean.ValueBoolean, "result is a boolean");
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res1).getRawValue(), true, "isfuel(hydrogen) = true");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_ISFUEL.evaluate(eGold);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res2).getRawValue(), false, "isfuel(gold) = false");

        IValue res3 = MekanismOperators.OBJECT_CHEMICALSTACK_ISFUEL.evaluate(eSodium);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res3).getRawValue(), false, "isfuel(sodium) = false");

        IValue res4 = MekanismOperators.OBJECT_CHEMICALSTACK_ISFUEL.evaluate(eSodiumSuperheated);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res4).getRawValue(), false, "isfuel(sodiumSuperheated) = false");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeIsFuelLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISFUEL.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeIsFuelSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISFUEL.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeIsFuel() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISFUEL.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- BURN_TICKS -----------------------------------
     */

    @IntegrationTest
    public void testBurnTicks() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_BURN_TICKS.evaluate(eHydrogen);
        Asserts.check(res1 instanceof ValueTypeInteger.ValueInteger, "result is an integer");
        TestHelpers.assertEqual(((ValueTypeInteger.ValueInteger) res1).getRawValue(), 1, "burnticks(hydrogen) = 1");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_BURN_TICKS.evaluate(eGold);
        TestHelpers.assertEqual(((ValueTypeInteger.ValueInteger) res2).getRawValue(), 0, "burnticks(gold) = 0");

        IValue res3 = MekanismOperators.OBJECT_CHEMICALSTACK_BURN_TICKS.evaluate(eSodium);
        TestHelpers.assertEqual(((ValueTypeInteger.ValueInteger) res3).getRawValue(), 0, "burnticks(sodium) = 0");

        IValue res4 = MekanismOperators.OBJECT_CHEMICALSTACK_BURN_TICKS.evaluate(eSodiumSuperheated);
        TestHelpers.assertEqual(((ValueTypeInteger.ValueInteger) res4).getRawValue(), 0, "burnticks(sodiumSuperheated) = 0");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeBurnTicksLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_BURN_TICKS.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeBurnTicksSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_BURN_TICKS.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeBurnTicks() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_BURN_TICKS.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- ENERGY_PER_TICK -----------------------------------
     */

    @IntegrationTest
    public void testEnergyPerTick() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_ENERGY_PER_TICK.evaluate(eHydrogen);
        Asserts.check(res1 instanceof ValueTypeLong.ValueLong, "result is a long");
        TestHelpers.assertEqual(((ValueTypeLong.ValueLong) res1).getRawValue(), 200L, "energypertick(hydrogen) = 200");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_ENERGY_PER_TICK.evaluate(eGold);
        TestHelpers.assertEqual(((ValueTypeLong.ValueLong) res2).getRawValue(), 0L, "energypertick(gold) = 0");

        IValue res3 = MekanismOperators.OBJECT_CHEMICALSTACK_ENERGY_PER_TICK.evaluate(eSodium);
        TestHelpers.assertEqual(((ValueTypeLong.ValueLong) res3).getRawValue(), 0L, "energypertick(sodium) = 0");

        IValue res4 = MekanismOperators.OBJECT_CHEMICALSTACK_ENERGY_PER_TICK.evaluate(eSodiumSuperheated);
        TestHelpers.assertEqual(((ValueTypeLong.ValueLong) res4).getRawValue(), 0L, "energypertick(sodiumSuperheated) = 0");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeEnergyPerTickLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ENERGY_PER_TICK.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeEnergyPerTickSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ENERGY_PER_TICK.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeEnergyPerTick() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ENERGY_PER_TICK.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- ISRAWCHEMICALEQUAL -----------------------------------
     */

    @IntegrationTest
    public void testIsRawChemicalEqual() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_ISRAWCHEMICALEQUAL.evaluate(eHydrogen, eHydrogen100);
        Asserts.check(res1 instanceof ValueTypeBoolean.ValueBoolean, "result is a boolean");
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res1).getRawValue(), true, "israwchemicalequal(hydrogen:1000, hydrogen:100) = true");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_ISRAWCHEMICALEQUAL.evaluate(eGold, eHydrogen);
        TestHelpers.assertEqual(((ValueTypeBoolean.ValueBoolean) res2).getRawValue(), false, "israwchemicalequal(gold:1000, hydrogen:1000) = false");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeIsRawChemicalEqualLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISRAWCHEMICALEQUAL.evaluate(eHydrogen, eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeIsRawChemicalEqualSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISRAWCHEMICALEQUAL.evaluate(eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeIsRawChemicalEqual() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_ISRAWCHEMICALEQUAL.evaluate(DUMMY_VARIABLE, DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- MODNAME -----------------------------------
     */

    @IntegrationTest
    public void testModname() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_MODNAME.evaluate(eHydrogen);
        Asserts.check(res1 instanceof ValueTypeString.ValueString, "result is a string");
        TestHelpers.assertEqual(((ValueTypeString.ValueString) res1).getRawValue(), "Mekanism", "modname(hydrogen) = Mekanism");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_MODNAME.evaluate(eGold);
        TestHelpers.assertEqual(((ValueTypeString.ValueString) res2).getRawValue(), "Mekanism", "modname(gold) = Mekanism");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeModnameLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_MODNAME.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeModnameSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_MODNAME.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeModname() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_MODNAME.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- WITH_AMOUNT -----------------------------------
     */

    @IntegrationTest
    public void testWithAmount() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_WITH_AMOUNT.evaluate(eHydrogen, l100);
        Asserts.check(res1 instanceof ValueObjectTypeChemicalStack.ValueChemicalStack, "result is a chemical");
        TestHelpers.assertEqual(((ValueObjectTypeChemicalStack.ValueChemicalStack) res1).getRawValue().equals(new ChemicalStack(MekanismChemicals.HYDROGEN, 100)), true, "withamount(hydrogen:1000, 100) = hydrogen:100");
        TestHelpers.assertEqual(((ValueObjectTypeChemicalStack.ValueChemicalStack) res1).getRawValue().equals(new ChemicalStack(MekanismChemicals.HYDROGEN, 1000)), false, "withamount(hydrogen:1000, 100) != hydrogen:1000");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeWithAmountLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_WITH_AMOUNT.evaluate(eHydrogen, l100, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeWithAmountSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_WITH_AMOUNT.evaluate(eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeWithAmount() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_WITH_AMOUNT.evaluate(DUMMY_VARIABLE, DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- TAG -----------------------------------
     */

    @IntegrationTest
    public void testTag() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_TAG.evaluate(eSteam);
        Asserts.check(res1 instanceof ValueTypeList.ValueList<?,?>, "result is a list");
        IValueTypeListProxy<ValueTypeString, ValueTypeString.ValueString> list = ((ValueTypeList.ValueList<ValueTypeString, ValueTypeString.ValueString>) res1).getRawValue();
        TestHelpers.assertEqual(list.getLength(), 2, "tag(steam).length = 2");
        TestHelpers.assertEqual(list.get(0).getRawValue(), "mekanism:water_vapor", "tag(steam)[0] = mekanism:water_vapor");
        TestHelpers.assertEqual(list.get(1).getRawValue(), "mekanism:gaseous", "tag(steam)[1] = mekanism:gaseous");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_TAG.evaluate(eHydrogen);
        TestHelpers.assertEqual(((ValueTypeList.ValueList<?, ?>) res2).getRawValue().getLength(), 1, "tag(hydrogen).length = 1");
        TestHelpers.assertEqual(((ValueTypeList.ValueList<ValueTypeString, ValueTypeString.ValueString>) res2).getRawValue().get(0).getRawValue(), "mekanism:gaseous", "tag(hydrogen)[1] = mekanism:gaseous");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeTagLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_TAG.evaluate(eHydrogen, eHydrogen);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeTagSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_TAG.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeTag() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_TAG.evaluate(DUMMY_VARIABLE);
    }

    /**
     * ----------------------------------- TAG_STACKS -----------------------------------
     */

    @IntegrationTest
    public void testTagStacks() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_CHEMICALSTACK_TAG_STACKS.evaluate(sWaterVapor);
        Asserts.check(res1 instanceof ValueTypeList.ValueList<?,?>, "result is a list");
        IValueTypeListProxy<ValueObjectTypeChemicalStack, ValueObjectTypeChemicalStack.ValueChemicalStack> list = ((ValueTypeList.ValueList<ValueObjectTypeChemicalStack, ValueObjectTypeChemicalStack.ValueChemicalStack>) res1).getRawValue();
        TestHelpers.assertEqual(list.getLength(), 2, "tagstacks(mekanism:water_vapor).length = 2");
        TestHelpers.assertEqual(list.get(0).getRawValue().equals(new ChemicalStack(MekanismChemicals.WATER_VAPOR, 1000)), true, "tagstacks(mekanism:water_vapor)[0] = water_vapor");
        TestHelpers.assertEqual(list.get(1).getRawValue().equals(new ChemicalStack(MekanismChemicals.STEAM, 1000)), true, "tagstacks(mekanism:water_vapor)[1] = steam");

        IValue res2 = MekanismOperators.OBJECT_CHEMICALSTACK_TAG_STACKS.evaluate(new DummyVariable<>(ValueTypes.STRING, ValueTypeString.ValueString.of("none")));
        TestHelpers.assertEqual(((ValueTypeList.ValueList<?, ?>) res2).getRawValue().getLength(), 0, "tagstacks(none) = empty");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeTagStacksLarge() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_TAG_STACKS.evaluate(sWaterVapor, sWaterVapor);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputSizeTagStacksSmall() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_TAG_STACKS.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeTagStacks() throws EvaluationException {
        MekanismOperators.OBJECT_CHEMICALSTACK_TAG_STACKS.evaluate(DUMMY_VARIABLE);
    }

}
