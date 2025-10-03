package org.cyclops.integratedmekanism.gametest;

import mekanism.api.RelativeSide;
import mekanism.api.chemical.ChemicalStack;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.tile.TileEntityChemicalTank;
import mekanism.common.tile.component.config.DataType;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.part.PartTypesMekanism;
import org.cyclops.integratedmekanism.part.aspect.MekanismAspects;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.testReadAspect;

@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsAspectsReadChemical {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 0, 2);

    public static TileEntityChemicalTank setTank(GameTestHelper helper) {
        helper.setBlock(POS.west(), MekanismBlocks.BASIC_CHEMICAL_TANK.get());
        TileEntityChemicalTank chemicalTank = helper.getBlockEntity(POS.west());
        chemicalTank.getConfig().getConfig(TransmissionType.CHEMICAL).setDataType(DataType.INPUT_OUTPUT, RelativeSide.LEFT);
        return chemicalTank;
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalFullTrue(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        chemicalTank.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 64_000));
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER, MekanismAspects.Read.Chemical.BOOLEAN_FULL, ValueTypeBoolean.ValueBoolean.of(true));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalFullFalse(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        chemicalTank.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 10));
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.BOOLEAN_FULL, ValueTypeBoolean.ValueBoolean.of(false));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalEmptyTrue(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.BOOLEAN_EMPTY, ValueTypeBoolean.ValueBoolean.of(true));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalEmptyFalse(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        chemicalTank.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 10));
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.BOOLEAN_EMPTY, ValueTypeBoolean.ValueBoolean.of(false));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalNonEmptyTrue(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        chemicalTank.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 10));
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.BOOLEAN_NONEMPTY, ValueTypeBoolean.ValueBoolean.of(true));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalNonEmptyFalse(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.BOOLEAN_NONEMPTY, ValueTypeBoolean.ValueBoolean.of(false));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalApplicableTrue(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.BOOLEAN_APPLICABLE, ValueTypeBoolean.ValueBoolean.of(true));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalApplicableFalse(GameTestHelper helper) {
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.BOOLEAN_APPLICABLE, ValueTypeBoolean.ValueBoolean.of(false));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalAmount(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        chemicalTank.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 10));
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.LONG_AMOUNT, ValueTypeLong.ValueLong.of(10));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalAmountTotal(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        chemicalTank.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 10));
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.LONG_AMOUNTTOTAL, ValueTypeLong.ValueLong.of(10));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalCapacityValid(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.LONG_CAPACITY, ValueTypeLong.ValueLong.of(64_000));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalCapacityInvalid(GameTestHelper helper) {
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.LONG_CAPACITY, ValueTypeLong.ValueLong.of(0));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalCapacityTotalValid(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.LONG_CAPACITYTOTAL, ValueTypeLong.ValueLong.of(64_000));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalCapacityTotalInvalid(GameTestHelper helper) {
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.LONG_CAPACITYTOTAL, ValueTypeLong.ValueLong.of(0));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalTanksValid(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.INTEGER_TANKS, ValueTypeInteger.ValueInteger.of(1));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalTanksInvalid(GameTestHelper helper) {
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.INTEGER_TANKS, ValueTypeInteger.ValueInteger.of(0));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalFillRatio(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        chemicalTank.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 500));
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.DOUBLE_FILLRATIO, ValueTypeDouble.ValueDouble.of(0.0078125D));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalTankChemicals(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        chemicalTank.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 500));
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.LIST_TANKCHEMICALS, ValueTypeList.ValueList.ofAll(
                ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.HYDROGEN, 500))
        ));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalTankCapacities(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        chemicalTank.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 500));
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.LIST_TANKCAPACITIES, ValueTypeList.ValueList.ofAll(
                ValueTypeLong.ValueLong.of(64_000)
        ));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadChemicalChemicalStack(GameTestHelper helper) {
        TileEntityChemicalTank chemicalTank = setTank(helper);
        chemicalTank.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 500));
        testReadAspect(POS, helper, PartTypesMekanism.CHEMICAL_READER,  MekanismAspects.Read.Chemical.CHEMICALSTACK, ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.HYDROGEN, 500)));
    }

}
