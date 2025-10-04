package org.cyclops.integratedmekanism.gametest;

import mekanism.api.RelativeSide;
import mekanism.api.chemical.ChemicalStack;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.tile.TileEntityChemicalTank;
import mekanism.common.tile.component.config.DataType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.core.block.IgnoredBlockStatus;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeList;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.aspect.MekanismTunnelsAspects;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.part.PartTypesMekanismTunnels;
import org.cyclops.integratedmekanism.value.MekanismValueTypes;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectWriteBuilders;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.createVariableForValue;
import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.placeVariableInWriter;

@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsTunnelChemical {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final int TIMEOUT = 3000;
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 0, 2);

    public static TileEntityChemicalTank setTank(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, MekanismBlocks.BASIC_CHEMICAL_TANK.get());
        TileEntityChemicalTank chemicalTank = helper.getBlockEntity(pos);
        for (RelativeSide relativeSide : RelativeSide.values()) {
            chemicalTank.getConfig().getConfig(TransmissionType.CHEMICAL).setDataType(DataType.INPUT_OUTPUT, relativeSide);
        }
        return chemicalTank;
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testChemicalImporterToInterfaceBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place chemical importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypesMekanismTunnels.IMPORTER_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getItem()));

        // Place chemical interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));

        // Place tanks
        TileEntityChemicalTank tankIn = setTank(helper, POS.west());
        TileEntityChemicalTank tankOut = setTank(helper, POS.east().east());

        // Insert chemical in importer tank
        tankIn.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 1_000L));

        // Place empty variable in importer
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if chemical is moved
            helper.assertValueEqual(tankOut.getChemicalTank().getStack().getAmount(), 1_000L, "Tank out does not contain chemicals");
            helper.assertValueEqual(tankIn.getChemicalTank().getStack().getAmount(), 0L, "Tank in was not drained");

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT, "Active aspect is incorrect");
            helper.assertTrue(partStateWriter.getErrors(MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT).isEmpty(), "Active aspect has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testChemicalInterfaceToExporterBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place chemical interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));

        // Place chemical exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypesMekanismTunnels.EXPORTER_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.EXPORTER_CHEMICAL.getItem()));

        // Place tanks
        TileEntityChemicalTank tankIn = setTank(helper, POS.west());
        TileEntityChemicalTank tankOut = setTank(helper, POS.east().east());

        // Insert items in interface chest
        tankIn.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 1_000L));

        // Place empty variable in exporter
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), MekanismTunnelsAspects.Write.Chemical.BOOLEAN_EXPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if chemical is moved
            helper.assertValueEqual(tankOut.getChemicalTank().getStack().getAmount(), 1_000L, "Tank out does not contain chemicals");
            helper.assertValueEqual(tankIn.getChemicalTank().getStack().getAmount(), 0L, "Tank in was not drained");

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Exporter is deactivated");
            helper.assertValueEqual(
                    PartTypesMekanismTunnels.EXPORTER_CHEMICAL.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)), Direction.EAST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), MekanismTunnelsAspects.Write.Chemical.BOOLEAN_EXPORT, "Active aspect is incorrect");
            helper.assertTrue(partStateWriter.getErrors(MekanismTunnelsAspects.Write.Chemical.BOOLEAN_EXPORT).isEmpty(), "Active aspect has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testChemicalImporterToInterfaceToExporterBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place chemical importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypesMekanismTunnels.IMPORTER_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getItem()));

        // Place chemical interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));

        // Place chemical exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH, PartTypesMekanismTunnels.EXPORTER_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.EXPORTER_CHEMICAL.getItem()));

        // Place tanks
        TileEntityChemicalTank tankIn = setTank(helper, POS.west());
        TileEntityChemicalTank tankInterface = setTank(helper, POS.east().east());
        TileEntityChemicalTank tankOut = setTank(helper, POS.east().north());

        // Insert chemicals in interface tank
        tankIn.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 1_000L));

        // Place empty variable in importer
        ItemStack variableAspectImporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT, variableAspectImporter);

        // Place empty variable in exporter
        ItemStack variableAspectExporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH), MekanismTunnelsAspects.Write.Chemical.BOOLEAN_EXPORT, variableAspectExporter);

        helper.succeedWhen(() -> {
            // Check if chemical is moved
            helper.assertValueEqual(tankInterface.getChemicalTank().getStack().getAmount(), 0L, "Tank interface was not drained");
            helper.assertValueEqual(tankOut.getChemicalTank().getStack().getAmount(), 1_000L, "Tank out does not contain chemicals");
            helper.assertValueEqual(tankIn.getChemicalTank().getStack().getAmount(), 0L, "Tank in was not drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testChemicalsImporterToInterfaceChemicalCorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place chemical importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypesMekanismTunnels.IMPORTER_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getItem()));

        // Place chemical interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));

        // Place tanks
        TileEntityChemicalTank tankIn = setTank(helper, POS.west());
        TileEntityChemicalTank tankOut = setTank(helper, POS.east().east());

        // Insert chemicals in importer tank
        tankIn.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 1_000L));

        // Place empty variable in importer
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), MekanismValueTypes.OBJECT_CHEMICALSTACK, ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.HYDROGEN, 100)));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), MekanismTunnelsAspects.Write.Chemical.CHEMICALSTACK_IMPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if chemical is moved
            helper.assertValueEqual(tankOut.getChemicalTank().getStack().getAmount(), 1_000L, "Tank out does not contain chemicals");
            helper.assertValueEqual(tankIn.getChemicalTank().getStack().getAmount(), 0L, "Tank in was not drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testChemicalsImporterToInterfaceChemicalIncorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place chemical importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypesMekanismTunnels.IMPORTER_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getItem()));

        // Place chemical interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));

        // Place tanks
        TileEntityChemicalTank tankIn = setTank(helper, POS.west());
        TileEntityChemicalTank tankOut = setTank(helper, POS.east().east());

        // Insert chemicals in importer tank
        tankIn.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 1_000L));

        // Place empty variable in importer
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), MekanismValueTypes.OBJECT_CHEMICALSTACK, ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.GOLD, 100)));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), MekanismTunnelsAspects.Write.Chemical.CHEMICALSTACK_IMPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if chemical is not moved
            helper.assertValueEqual(tankOut.getChemicalTank().getStack().getAmount(), 0L, "Tank out was filled");
            helper.assertValueEqual(tankIn.getChemicalTank().getStack().getAmount(), 1_000L, "Tank in was drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testChemicalsImporterToFilteredInterfaceBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place chemical importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypesMekanismTunnels.IMPORTER_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getItem()));

        // Place chemical interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypesMekanismTunnels.INTERFACE_FILTERING_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_FILTERING_CHEMICAL.getItem()));

        // Place tanks
        TileEntityChemicalTank tankIn = setTank(helper, POS.west());
        TileEntityChemicalTank tankOut = setTank(helper, POS.east().east());

        // Insert chemicals in importer tank
        tankIn.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 1_000L));

        // Place empty variable in importer
        ItemStack variableAspectImporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT, variableAspectImporter);

        // Place empty variable in filtering interface
        ItemStack variableAspectInterface = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), MekanismTunnelsAspects.Write.ChemicalFilter.BOOLEAN_SET_FILTER, variableAspectInterface);

        helper.succeedWhen(() -> {
            // Check if chemical is moved
            helper.assertValueEqual(tankOut.getChemicalTank().getStack().getAmount(), 1_000L, "Tank out does not contain chemicals");
            helper.assertValueEqual(tankIn.getChemicalTank().getStack().getAmount(), 0L, "Tank in was not drained");

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status importer is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT, "Active aspect importer is incorrect");
            helper.assertTrue(partStateWriter.getErrors(MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT).isEmpty(), "Active aspect importer has errors");

            // Check filtering interface state
            IPartStateWriter partStateInterface = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)).getState();
            helper.assertFalse(partStateInterface.isDeactivated(), "Filtering interface is deactivated");
            helper.assertValueEqual(
                    PartTypesMekanismTunnels.INTERFACE_FILTERING_CHEMICAL.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)), Direction.EAST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status filtering interface is incorrect"
            );
            helper.assertValueEqual(partStateInterface.getActiveAspect(), MekanismTunnelsAspects.Write.ChemicalFilter.BOOLEAN_SET_FILTER, "Active aspect filtering interface is incorrect");
            helper.assertTrue(partStateInterface.getErrors(MekanismTunnelsAspects.Write.ChemicalFilter.BOOLEAN_SET_FILTER).isEmpty(), "Active aspect filtering interface has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testChemicalsImporterToFilteredInterfaceChemicalCorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place chemical importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypesMekanismTunnels.IMPORTER_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getItem()));

        // Place chemical interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypesMekanismTunnels.INTERFACE_FILTERING_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_FILTERING_CHEMICAL.getItem()));

        // Place tanks
        TileEntityChemicalTank tankIn = setTank(helper, POS.west());
        TileEntityChemicalTank tankOut = setTank(helper, POS.east().east());

        // Insert chemicals in importer tank
        tankIn.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 1_000L));

        // Place empty variable in importer
        ItemStack variableAspectImporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT, variableAspectImporter);

        // Place empty variable in filtering interface
        ItemStack variableAspectInterface = createVariableForValue(helper.getLevel(), MekanismValueTypes.OBJECT_CHEMICALSTACK, ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.HYDROGEN, 100)));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), MekanismTunnelsAspects.Write.ChemicalFilter.CHEMICALSTACK_SET_FILTER, variableAspectInterface);

        helper.succeedWhen(() -> {
            // Check if chemical is moved
            helper.assertValueEqual(tankOut.getChemicalTank().getStack().getAmount(), 1_000L, "Tank out does not contain chemicals");
            helper.assertValueEqual(tankIn.getChemicalTank().getStack().getAmount(), 0L, "Tank in was not drained");

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status importer is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT, "Active aspect importer is incorrect");
            helper.assertTrue(partStateWriter.getErrors(MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT).isEmpty(), "Active aspect importer has errors");

            // Check filtering interface state
            IPartStateWriter partStateInterface = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)).getState();
            helper.assertFalse(partStateInterface.isDeactivated(), "Filtering interface is deactivated");
            helper.assertValueEqual(
                    PartTypesMekanismTunnels.INTERFACE_FILTERING_CHEMICAL.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)), Direction.EAST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status filtering interface is incorrect"
            );
            helper.assertValueEqual(partStateInterface.getActiveAspect(), MekanismTunnelsAspects.Write.ChemicalFilter.CHEMICALSTACK_SET_FILTER, "Active aspect filtering interface is incorrect");
            helper.assertTrue(partStateInterface.getErrors(MekanismTunnelsAspects.Write.ChemicalFilter.CHEMICALSTACK_SET_FILTER).isEmpty(), "Active aspect filtering interface has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testChemicalsImporterToFilteredInterfaceChemicalIncorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place chemical importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypesMekanismTunnels.IMPORTER_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getItem()));

        // Place chemical interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypesMekanismTunnels.INTERFACE_FILTERING_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_FILTERING_CHEMICAL.getItem()));

        // Place tanks
        TileEntityChemicalTank tankIn = setTank(helper, POS.west());
        TileEntityChemicalTank tankOut = setTank(helper, POS.east().east());

        // Insert chemicals in importer tank
        tankIn.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 1_000L));

        // Place empty variable in importer
        ItemStack variableAspectImporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT, variableAspectImporter);

        // Place empty variable in filtering interface
        ItemStack variableAspectInterface = createVariableForValue(helper.getLevel(), MekanismValueTypes.OBJECT_CHEMICALSTACK, ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.GOLD, 100)));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), MekanismTunnelsAspects.Write.ChemicalFilter.CHEMICALSTACK_SET_FILTER, variableAspectInterface);

        helper.succeedWhen(() -> {
            // Check if chemical is moved
            helper.assertValueEqual(tankOut.getChemicalTank().getStack().getAmount(), 0L, "Tank out contains chemicals");
            helper.assertValueEqual(tankIn.getChemicalTank().getStack().getAmount(), 1_000L, "Tank in was drained");

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status importer is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT, "Active aspect importer is incorrect");
            helper.assertTrue(partStateWriter.getErrors(MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT).isEmpty(), "Active aspect importer has errors");

            // Check filtering interface state
            IPartStateWriter partStateInterface = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)).getState();
            helper.assertFalse(partStateInterface.isDeactivated(), "Filtering interface is deactivated");
            helper.assertValueEqual(
                    PartTypesMekanismTunnels.INTERFACE_FILTERING_CHEMICAL.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)), Direction.EAST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status filtering interface is incorrect"
            );
            helper.assertValueEqual(partStateInterface.getActiveAspect(), MekanismTunnelsAspects.Write.ChemicalFilter.CHEMICALSTACK_SET_FILTER, "Active aspect filtering interface is incorrect");
            helper.assertTrue(partStateInterface.getErrors(MekanismTunnelsAspects.Write.ChemicalFilter.CHEMICALSTACK_SET_FILTER).isEmpty(), "Active aspect filtering interface has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testChemicalsImporterToInterfaceChemicalListCorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place chemical importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypesMekanismTunnels.IMPORTER_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getItem()));

        // Place chemical interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));

        // Place tanks
        TileEntityChemicalTank tankIn = setTank(helper, POS.west());
        TileEntityChemicalTank tankOut = setTank(helper, POS.east().east());

        // Insert chemicals in importer tank
        tankIn.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 1_000L));

        // Place empty variable in importer
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.LIST, ValueTypeList.ValueList.ofAll(
                ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.GOLD, 100)),
                ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.HYDROGEN, 100))
        ));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), MekanismTunnelsAspects.Write.Chemical.LIST_IMPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if chemical is moved
            helper.assertValueEqual(tankOut.getChemicalTank().getStack().getAmount(), 1_000L, "Tank out does not contain chemicals");
            helper.assertValueEqual(tankIn.getChemicalTank().getStack().getAmount(), 0L, "Tank in was not drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testChemicalsImporterToInterfaceChemicalListIncorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place chemical importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypesMekanismTunnels.IMPORTER_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getItem()));

        // Place chemical interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));

        // Place tanks
        TileEntityChemicalTank tankIn = setTank(helper, POS.west());
        TileEntityChemicalTank tankOut = setTank(helper, POS.east().east());

        // Insert chemicals in importer tank
        tankIn.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 1_000L));

        // Place empty variable in importer
        ItemStack variableAspectImporter = createVariableForValue(helper.getLevel(), ValueTypes.LIST, ValueTypeList.ValueList.ofAll(
                ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.GOLD, 100)),
                ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.BIO, 100))
        ));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), MekanismTunnelsAspects.Write.Chemical.LIST_IMPORT, variableAspectImporter);

        helper.succeedWhen(() -> {
            // Check if chemical is moved
            helper.assertValueEqual(tankOut.getChemicalTank().getStack().getAmount(), 0L, "Tank out contains chemicals");
            helper.assertValueEqual(tankIn.getChemicalTank().getStack().getAmount(), 1_000L, "Tank in was drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testChemicalsImporterToInterfaceChemicalListBlacklistCorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place chemical importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypesMekanismTunnels.IMPORTER_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getItem()));

        // Place chemical interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));

        // Place tanks
        TileEntityChemicalTank tankIn = setTank(helper, POS.west());
        TileEntityChemicalTank tankOut = setTank(helper, POS.east().east());

        // Insert chemicals in importer tank
        tankIn.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 1_000L));

        // Place empty variable in importer
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.LIST, ValueTypeList.ValueList.ofAll(
                ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.GOLD, 100)),
                ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.BIO, 100))
        ));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), MekanismTunnelsAspects.Write.Chemical.LIST_IMPORT, variableAspect);

        // Enable blacklist
        PartPos posImporter = PartPos.of(DimPos.of(helper.getLevel(), helper.absolutePos(POS)), Direction.WEST);
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(posImporter);
        IAspectProperties properties = MekanismTunnelsAspects.Write.Chemical.LIST_IMPORT.getProperties(partStateHolder.getPart(), PartTarget.fromCenter(posImporter), partStateHolder.getState());
        properties.setValue(TunnelAspectWriteBuilders.PROP_BLACKLIST, ValueTypeBoolean.ValueBoolean.of(true));
        partStateHolder.getState().setAspectProperties(MekanismTunnelsAspects.Write.Chemical.LIST_IMPORT, properties);

        helper.succeedWhen(() -> {
            // Check if chemical is moved
            helper.assertValueEqual(tankOut.getChemicalTank().getStack().getAmount(), 1_000L, "Tank out does not contain chemicals");
            helper.assertValueEqual(tankIn.getChemicalTank().getStack().getAmount(), 0L, "Tank in was not drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testChemicalsImporterToInterfaceChemicalListBlacklistIncorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place chemical importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypesMekanismTunnels.IMPORTER_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getItem()));

        // Place chemical interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));

        // Place tanks
        TileEntityChemicalTank tankIn = setTank(helper, POS.west());
        TileEntityChemicalTank tankOut = setTank(helper, POS.east().east());

        // Insert chemicals in importer tank
        tankIn.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 1_000L));

        // Place empty variable in importer
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.LIST, ValueTypeList.ValueList.ofAll(
                ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.HYDROGEN, 100)),
                ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.GOLD, 100))
        ));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), MekanismTunnelsAspects.Write.Chemical.LIST_IMPORT, variableAspect);

        // Enable blacklist
        PartPos posImporter = PartPos.of(DimPos.of(helper.getLevel(), helper.absolutePos(POS)), Direction.WEST);
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(posImporter);
        IAspectProperties properties = MekanismTunnelsAspects.Write.Chemical.LIST_IMPORT.getProperties(partStateHolder.getPart(), PartTarget.fromCenter(posImporter), partStateHolder.getState());
        properties.setValue(TunnelAspectWriteBuilders.PROP_BLACKLIST, ValueTypeBoolean.ValueBoolean.of(true));
        partStateHolder.getState().setAspectProperties(MekanismTunnelsAspects.Write.Chemical.LIST_IMPORT, properties);

        helper.succeedWhen(() -> {
            // Check if chemical is moved
            helper.assertValueEqual(tankOut.getChemicalTank().getStack().getAmount(), 0L, "Tank out contains chemicals");
            helper.assertValueEqual(tankIn.getChemicalTank().getStack().getAmount(), 1_000L, "Tank in was drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testChemicalInterfaceToExporterChemicalFromSubnet(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place chemical interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));

        // Place chemical exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypesMekanismTunnels.EXPORTER_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.EXPORTER_CHEMICAL.getItem()));

        // Place cable for subnet
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_CABLE.value());

        // Place chemical interfaces in subnet
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.west()), Direction.WEST, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.west()), Direction.NORTH, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.west()), Direction.EAST, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));

        // Place tanks
        TileEntityChemicalTank tankIn1 = setTank(helper, POS.west().west());
        TileEntityChemicalTank tankIn2 = setTank(helper, POS.west().north());
        TileEntityChemicalTank tankOut = setTank(helper, POS.east().east());

        // Insert chemicals in subnet tanks
        tankIn1.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.HYDROGEN, 1_000L));
        tankIn2.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.GOLD, 1_000L));

        // Place empty variable in exporter
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), MekanismValueTypes.OBJECT_CHEMICALSTACK, ValueObjectTypeChemicalStack.ValueChemicalStack.of(new ChemicalStack(MekanismChemicals.GOLD, 100)));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), MekanismTunnelsAspects.Write.Chemical.CHEMICALSTACK_EXPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if chemical is moved
            helper.assertValueEqual(tankOut.getChemicalTank().getStack().getAmount(), 1_000L, "Tank out does not contain chemicals");
            helper.assertValueEqual(tankOut.getChemicalTank().getStack().getChemical(), MekanismChemicals.GOLD.value(), "Tank out does not contain the correct chemical type");
            helper.assertValueEqual(tankIn1.getChemicalTank().getStack().getAmount(), 1_000L, "Tank in 1 was incorrectly drained");
            helper.assertValueEqual(tankIn2.getChemicalTank().getStack().getAmount(), 0L, "Tank in 2 was not drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testChemicalWorldImporterToInterfaceBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place chemical importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypesMekanismTunnels.IMPORTER_WORLD_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getItem()));

        // Place chemical interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));

        // Place tanks
        ItemStack tankIn = helper.spawnItem(MekanismBlocks.BASIC_CHEMICAL_TANK.asItem(), POS.west()).getItem();
        TileEntityChemicalTank tankOut = setTank(helper, POS.east().east());

        // Insert chemical in importer tank
        tankIn.getCapability(Capabilities.CHEMICAL.item()).setChemicalInTank(0, new ChemicalStack(MekanismChemicals.HYDROGEN, 1_000L));

        // Place empty variable in importer
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), MekanismTunnelsAspects.Write.World.ENTITY_CHEMICAL_BOOLEAN_IMPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if chemical is moved
            helper.assertValueEqual(tankOut.getChemicalTank().getStack().getAmount(), 1_000L, "Tank out does not contain chemicals");
            helper.assertValueEqual(tankIn.getCapability(Capabilities.CHEMICAL.item()).getChemicalInTank(0).getAmount(), 0L, "Tank in was not drained");

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), MekanismTunnelsAspects.Write.World.ENTITY_CHEMICAL_BOOLEAN_IMPORT, "Active aspect is incorrect");
            helper.assertTrue(partStateWriter.getErrors(MekanismTunnelsAspects.Write.World.ENTITY_CHEMICAL_BOOLEAN_IMPORT).isEmpty(), "Active aspect has errors");
        });
    }

}
