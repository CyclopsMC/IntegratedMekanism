package org.cyclops.integratedmekanism.gametest;

import mekanism.api.Action;
import mekanism.api.security.SecurityMode;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.lib.frequency.Frequency;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tile.qio.TileEntityQIOComponent;
import mekanism.common.tile.qio.TileEntityQIODriveArray;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ItemMatch;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.part.PartTypes;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

import java.util.UUID;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.placeVariableInWriter;

@GameTestHolder(org.cyclops.integratedmekanism.Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsQIO {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final int TIMEOUT = 3000;
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 0, 2);

    /**
     * Set a QIO component to a new public frequency.
     */
    public static QIOFrequency setFrequency(GameTestHelper helper, TileEntityQIOComponent tile, String name) {
        // A logged-in owner is needed, as Mekanism looks up its username
        UUID owner = helper.makeMockServerPlayerInLevel().getUUID();
        tile.getFrequencyComponent().setFrequencyFromData(FrequencyType.QIO, new Frequency.FrequencyIdentity(name, SecurityMode.PUBLIC, owner), owner);
        return tile.getQIOFrequency();
    }

    public static TileEntityQIODriveArray setDriveArray(GameTestHelper helper, BlockPos pos, String frequencyName) {
        helper.setBlock(pos, MekanismBlocks.QIO_DRIVE_ARRAY.get());
        TileEntityQIODriveArray driveArray = helper.getBlockEntity(pos);
        setFrequency(helper, driveArray, frequencyName);
        driveArray.getDriveSlots().get(0).setStack(new ItemStack(MekanismItems.BASE_QIO_DRIVE.get()));
        return driveArray;
    }

    public static String uniqueFrequency() {
        return "integratedmekanism_test_" + UUID.randomUUID();
    }

    public static IIngredientComponentStorage<ItemStack, Integer> getStorage(GameTestHelper helper, BlockPos pos) {
        BlockPos absPos = helper.absolutePos(pos);
        return IngredientComponent.ITEMSTACK.getBlockStorage(helper.getLevel(), absPos, helper.getLevel().getBlockState(absPos), helper.getLevel().getBlockEntity(absPos), Direction.NORTH);
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOStorageInsertExtract(GameTestHelper helper) {
        TileEntityQIODriveArray driveArray = setDriveArray(helper, POS, uniqueFrequency());
        QIOFrequency frequency = driveArray.getQIOFrequency();
        IIngredientComponentStorage<ItemStack, Integer> storage = getStorage(helper, POS);
        helper.assertTrue(storage != null, "Storage is null");
        helper.assertTrue(storage.getMaxQuantity() > 0, "Storage has no capacity");

        // Insert
        helper.assertTrue(storage.insert(new ItemStack(Items.COBBLESTONE, 100), true).isEmpty(), "Simulated insert failed");
        helper.assertValueEqual(frequency.getStored(new ItemStack(Items.COBBLESTONE)), 0L, "Simulated insert changed contents");
        helper.assertTrue(storage.insert(new ItemStack(Items.COBBLESTONE, 100), false).isEmpty(), "Insert failed");
        storage.insert(new ItemStack(Items.DIRT, 10), false);
        helper.assertValueEqual(frequency.getStored(new ItemStack(Items.COBBLESTONE)), 100L, "Insert did not change contents");

        // Iterate
        long total = 0;
        int types = 0;
        for (ItemStack stack : storage) {
            total += stack.getCount();
            types++;
        }
        helper.assertValueEqual(types, 2, "Incorrect type count");
        helper.assertValueEqual(total, 110L, "Incorrect total count");
        helper.assertTrue(storage.iterator(new ItemStack(Items.COBBLESTONE), ItemMatch.ITEM | ItemMatch.DATA).hasNext(), "Exact iterator is empty");
        helper.assertValueEqual(storage.iterator(new ItemStack(Items.COBBLESTONE), ItemMatch.ITEM | ItemMatch.DATA).next().getCount(), 100, "Exact iterator count is incorrect");
        helper.assertTrue(storage.iterator(new ItemStack(Items.DIRT), ItemMatch.ITEM).hasNext(), "Fuzzy iterator is empty");
        helper.assertFalse(storage.iterator(new ItemStack(Items.STONE), ItemMatch.ITEM).hasNext(), "Iterator for absent item is not empty");

        // Extract exact
        helper.assertValueEqual(storage.extract(new ItemStack(Items.COBBLESTONE, 30), ItemMatch.EXACT, false).getCount(), 30, "Exact extract failed");
        helper.assertValueEqual(frequency.getStored(new ItemStack(Items.COBBLESTONE)), 70L, "Exact extract did not change contents");
        helper.assertTrue(storage.extract(new ItemStack(Items.COBBLESTONE, 71), ItemMatch.EXACT, false).isEmpty(), "Exact extract of too many items did not fail");

        // Extract without stacksize: the count is the maximum
        helper.assertValueEqual(storage.extract(new ItemStack(Items.COBBLESTONE, 1000), ItemMatch.ITEM | ItemMatch.DATA, false).getCount(), 70, "Max extract failed");

        // Extract fuzzy
        ItemStack namedDirt = new ItemStack(Items.DIRT, 5);
        namedDirt.set(DataComponents.CUSTOM_NAME, Component.literal("abc"));
        helper.assertTrue(storage.extract(namedDirt, ItemMatch.EXACT, false).isEmpty(), "Exact extract of data-mismatching item did not fail");
        ItemStack extractedFuzzy = storage.extract(namedDirt, ItemMatch.ITEM | ItemMatch.STACKSIZE, false);
        helper.assertValueEqual(extractedFuzzy.getItem(), Items.DIRT, "Fuzzy extract item is incorrect");
        helper.assertValueEqual(extractedFuzzy.getCount(), 5, "Fuzzy extract count is incorrect");
        helper.assertFalse(extractedFuzzy.has(DataComponents.CUSTOM_NAME), "Fuzzy extract should return the stored type");

        // Extract any
        helper.assertValueEqual(storage.extract(64, false).getCount(), 5, "Extract any failed");
        helper.assertValueEqual(frequency.getTotalItemCount(), 0L, "Storage is not empty");
        helper.assertTrue(storage.extract(64, false).isEmpty(), "Extract from empty storage did not fail");

        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOStorageInsertFull(GameTestHelper helper) {
        TileEntityQIODriveArray driveArray = setDriveArray(helper, POS, uniqueFrequency());
        IIngredientComponentStorage<ItemStack, Integer> storage = getStorage(helper, POS);
        long capacity = storage.getMaxQuantity();
        helper.assertValueEqual(capacity, driveArray.getQIOFrequency().getTotalItemCountCapacity(), "Capacity is incorrect");

        ItemStack remaining = storage.insert(new ItemStack(Items.COBBLESTONE, (int) capacity + 10), false);
        helper.assertValueEqual(remaining.getCount(), 10, "Remaining count is incorrect");
        helper.assertValueEqual(remaining.getItem(), Items.COBBLESTONE, "Remaining item is incorrect");

        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOStorageNoFrequency(GameTestHelper helper) {
        helper.setBlock(POS, MekanismBlocks.QIO_DRIVE_ARRAY.get());
        TileEntityQIODriveArray driveArray = helper.getBlockEntity(POS);
        driveArray.getDriveSlots().get(0).setStack(new ItemStack(MekanismItems.BASE_QIO_DRIVE.get()));

        // Must not fall back to exposing the drive slots
        IIngredientComponentStorage<ItemStack, Integer> storage = getStorage(helper, POS);
        helper.assertTrue(storage != null, "Storage is null");
        helper.assertFalse(storage.iterator().hasNext(), "Storage without frequency is not empty");
        helper.assertValueEqual(storage.getMaxQuantity(), 0L, "Storage without frequency has capacity");
        helper.assertValueEqual(storage.insert(new ItemStack(Items.COBBLESTONE, 10), false).getCount(), 10, "Storage without frequency accepted items");
        helper.assertTrue(storage.extract(64, false).isEmpty(), "Storage without frequency returned items");

        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOStorageSharedFrequency(GameTestHelper helper) {
        String frequencyName = uniqueFrequency();
        TileEntityQIODriveArray driveArray = setDriveArray(helper, POS, frequencyName);
        helper.setBlock(POS.east(), MekanismBlocks.QIO_DASHBOARD.get());
        setFrequency(helper, helper.getBlockEntity(POS.east()), frequencyName);

        getStorage(helper, POS.east()).insert(new ItemStack(Items.COBBLESTONE, 20), false);
        helper.assertValueEqual(driveArray.getQIOFrequency().getStored(new ItemStack(Items.COBBLESTONE)), 20L, "Dashboard did not insert into frequency");
        helper.assertValueEqual(getStorage(helper, POS).extract(new ItemStack(Items.COBBLESTONE, 20), ItemMatch.EXACT, false).getCount(), 20, "Drive array did not extract from frequency");

        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOImporterToInterface(GameTestHelper helper) {
        TileEntityQIODriveArray driveArray = setDriveArray(helper, POS.east().east(), uniqueFrequency());

        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place item importer and interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ITEM, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()));
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chest
        helper.setBlock(POS.west(), Blocks.CHEST);
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.west());
        chestIn.setItem(0, new ItemStack(Items.WHITE_WOOL, 10));
        chestIn.setItem(1, new ItemStack(Items.DIAMOND_PICKAXE));

        // Place empty variable in importer
        placeVariableInWriter(helper, PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Item.BOOLEAN_IMPORT, new ItemStack(RegistryEntries.ITEM_VARIABLE));

        helper.succeedWhen(() -> {
            helper.assertContainerEmpty(POS.west());
            helper.assertValueEqual(driveArray.getQIOFrequency().getStored(new ItemStack(Items.WHITE_WOOL)), 10L, "Wool was not moved to QIO");
            helper.assertValueEqual(driveArray.getQIOFrequency().getStored(new ItemStack(Items.DIAMOND_PICKAXE)), 1L, "Pickaxe was not moved to QIO");
            helper.assertValueEqual(driveArray.getDriveSlots().get(0).getStack().getItem(), MekanismItems.BASE_QIO_DRIVE.get(), "Drive was removed");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOInterfaceToExporter(GameTestHelper helper) {
        TileEntityQIODriveArray driveArray = setDriveArray(helper, POS.west(), uniqueFrequency());
        driveArray.getQIOFrequency().massInsert(new ItemStack(Items.WHITE_WOOL), 10, Action.EXECUTE);

        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place item interface and exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.EXPORTER_ITEM, new ItemStack(PartTypes.EXPORTER_ITEM.getItem()));

        // Place chest
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place empty variable in exporter
        placeVariableInWriter(helper, PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.Item.BOOLEAN_EXPORT, new ItemStack(RegistryEntries.ITEM_VARIABLE));

        helper.succeedWhen(() -> {
            helper.assertValueEqual(((ChestBlockEntity) helper.getBlockEntity(POS.east().east())).countItem(Items.WHITE_WOOL), 10, "Wool was not moved to chest");
            helper.assertValueEqual(driveArray.getQIOFrequency().getTotalItemCount(), 0L, "QIO was not drained");
            helper.assertValueEqual(driveArray.getDriveSlots().get(0).getStack().getItem(), MekanismItems.BASE_QIO_DRIVE.get(), "Drive was exported");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOExporterDirect(GameTestHelper helper) {
        // Exporters target the drive array directly, without interface
        TileEntityQIODriveArray driveArray = setDriveArray(helper, POS.west(), uniqueFrequency());

        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.EXPORTER_ITEM, new ItemStack(PartTypes.EXPORTER_ITEM.getItem()));
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        helper.setBlock(POS.east().east(), Blocks.CHEST);
        ChestBlockEntity chest = helper.getBlockEntity(POS.east().east());
        chest.setItem(0, new ItemStack(Items.WHITE_WOOL, 10));

        placeVariableInWriter(helper, PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Item.BOOLEAN_EXPORT, new ItemStack(RegistryEntries.ITEM_VARIABLE));

        helper.succeedWhen(() -> {
            helper.assertContainerEmpty(POS.east().east());
            helper.assertValueEqual(driveArray.getQIOFrequency().getStored(new ItemStack(Items.WHITE_WOOL)), 10L, "Wool was not moved to QIO");
        });
    }
}
