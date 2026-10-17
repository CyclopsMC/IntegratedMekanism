package org.cyclops.integratedmekanism.gametest;

import mekanism.api.Action;
import mekanism.api.security.SecurityMode;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.lib.frequency.Frequency;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tile.qio.TileEntityQIOComponent;
import mekanism.common.tile.qio.TileEntityQIODriveArray;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.evaluate.variable.ValueDeseralizationContext;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.block.IgnoredBlockStatus;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.part.PartTypeInterfaceQIO;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.part.PartTypesMekanismTunnels;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio.ContainerPartInterfaceQIO;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio.QIOFrequencyLink;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio.QIOLinkStatus;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.part.PartTypes;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

import java.util.UUID;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.placeVariableInWriter;

@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsQIOInterface {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final int TIMEOUT = 2000;
    public static final BlockPos POS = BlockPos.ZERO.offset(4, 0, 4);

    // ===== Helpers =====

    public static String uniqueFrequency() {
        return "integratedmekanism_test_" + UUID.randomUUID();
    }

    /**
     * Create a QIO frequency that is backed by a drive array at the given position.
     */
    public static QIOFrequency createFrequency(GameTestHelper helper, BlockPos pos, ServerPlayer owner, String name, SecurityMode securityMode) {
        helper.setBlock(pos, MekanismBlocks.QIO_DRIVE_ARRAY.get());
        TileEntityQIODriveArray driveArray = helper.getBlockEntity(pos);
        driveArray.getFrequencyComponent().setFrequencyFromData(FrequencyType.QIO, new Frequency.FrequencyIdentity(name, securityMode, owner.getUUID()), owner.getUUID());
        driveArray.getDriveSlots().get(0).setStack(new ItemStack(MekanismItems.BASE_QIO_DRIVE.get()));
        return driveArray.getQIOFrequency();
    }

    public static QIOFrequency createFrequency(GameTestHelper helper, BlockPos pos, ServerPlayer owner) {
        return createFrequency(helper, pos, owner, uniqueFrequency(), SecurityMode.PUBLIC);
    }

    public static PartPos placeInterface(GameTestHelper helper, BlockPos pos, Direction side) {
        if (!(helper.getLevel().getBlockState(helper.absolutePos(pos)).getBlock() == RegistryEntries.BLOCK_CABLE.value())) {
            helper.setBlock(pos, RegistryEntries.BLOCK_CABLE.value());
        }
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(pos), side, PartTypesMekanismTunnels.INTERFACE_QIO, new ItemStack(PartTypesMekanismTunnels.INTERFACE_QIO.getItem()));
        return PartPos.of(helper.getLevel(), helper.absolutePos(pos), side);
    }

    public static PartTypeInterfaceQIO.State getState(PartPos pos) {
        return (PartTypeInterfaceQIO.State) PartHelpers.getPart(pos).getState();
    }

    public static PartTarget getTarget(PartPos pos) {
        return PartTypesMekanismTunnels.INTERFACE_QIO.getTarget(pos, getState(pos));
    }

    public static void link(PartPos pos, QIOFrequency frequency, ServerPlayer authorizer) {
        PartTypesMekanismTunnels.INTERFACE_QIO.setLink(getTarget(pos), getState(pos), new QIOFrequencyLink(frequency.getIdentity(), authorizer.getUUID()));
    }

    public static IItemNetwork getItemNetwork(PartPos pos) {
        return NetworkHelpers.getNetworkChecked(pos).getCapability(org.cyclops.integratedtunnels.Capabilities.ItemNetwork.NETWORK).orElseThrow();
    }

    public static long countInChannel(PartPos pos, int channel, ItemStack type) {
        long count = 0;
        for (ItemStack stack : getItemNetwork(pos).getChannel(channel)) {
            if (type.isEmpty() || ItemStack.isSameItemSameComponents(stack, type)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static void assertStatus(GameTestHelper helper, PartPos pos, QIOLinkStatus status) {
        helper.assertValueEqual(getState(pos).getLinkStatus(), status, "Link status is incorrect");
        helper.assertValueEqual(
                PartTypesMekanismTunnels.INTERFACE_QIO.getBlockState(PartHelpers.getPartContainerChecked(pos), pos.getSide()).getValue(IgnoredBlockStatus.STATUS),
                status.getBlockStatus(),
                "Block status is incorrect"
        );
    }

    // ===== Linking =====

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOInterfaceLinkExposesItems(GameTestHelper helper) {
        ServerPlayer owner = helper.makeMockServerPlayerInLevel();
        QIOFrequency frequency = createFrequency(helper, POS.west(3), owner);
        frequency.massInsert(new ItemStack(Items.DIAMOND), 30, Action.EXECUTE);
        frequency.massInsert(new ItemStack(Items.COBBLESTONE), 200, Action.EXECUTE);

        // The interface faces up, away from the drive array, as it needs no physical connection
        PartPos pos = placeInterface(helper, POS, Direction.UP);
        assertStatus(helper, pos, QIOLinkStatus.UNLINKED);
        helper.assertValueEqual(countInChannel(pos, 0, ItemStack.EMPTY), 0L, "Unlinked interface exposes items");

        link(pos, frequency, owner);

        helper.succeedWhen(() -> {
            assertStatus(helper, pos, QIOLinkStatus.LINKED);
            helper.assertValueEqual(countInChannel(pos, 0, new ItemStack(Items.DIAMOND)), 30L, "Diamonds are not exposed");
            helper.assertValueEqual(countInChannel(pos, 0, new ItemStack(Items.COBBLESTONE)), 200L, "Cobblestone is not exposed");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOInterfaceUnlink(GameTestHelper helper) {
        ServerPlayer owner = helper.makeMockServerPlayerInLevel();
        QIOFrequency frequency = createFrequency(helper, POS.west(3), owner);
        frequency.massInsert(new ItemStack(Items.DIAMOND), 30, Action.EXECUTE);
        PartPos pos = placeInterface(helper, POS, Direction.UP);
        link(pos, frequency, owner);

        helper.startSequence()
                .thenWaitUntil(() -> helper.assertValueEqual(countInChannel(pos, 0, ItemStack.EMPTY), 30L, "Linked interface does not expose items"))
                .thenExecute(() -> {
                    PartTypesMekanismTunnels.INTERFACE_QIO.setLink(getTarget(pos), getState(pos), null);
                    assertStatus(helper, pos, QIOLinkStatus.UNLINKED);
                    BlockPos blockPos = pos.getPos().getBlockPos();
                    IIngredientComponentStorage<ItemStack, Integer> storage = IngredientComponent.ITEMSTACK.getBlockStorage(helper.getLevel(), blockPos,
                            helper.getLevel().getBlockState(blockPos), helper.getLevel().getBlockEntity(blockPos), pos.getSide());
                    helper.assertTrue(storage == null || (!storage.iterator().hasNext() && storage.getMaxQuantity() == 0),
                            "Unlinked interface still exposes a storage on its side");
                })
                .thenWaitUntil(() -> helper.assertValueEqual(countInChannel(pos, 0, ItemStack.EMPTY), 0L, "Unlinked interface still exposes items"))
                .thenSucceed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOInterfaceSeesMekanismChanges(GameTestHelper helper) {
        ServerPlayer owner = helper.makeMockServerPlayerInLevel();
        QIOFrequency frequency = createFrequency(helper, POS.west(3), owner);
        PartPos pos = placeInterface(helper, POS, Direction.UP);
        link(pos, frequency, owner);

        // Changes made on the Mekanism side are visible to the network
        frequency.massInsert(new ItemStack(Items.EMERALD), 12, Action.EXECUTE);

        helper.succeedWhen(() -> helper.assertValueEqual(countInChannel(pos, 0, new ItemStack(Items.EMERALD)), 12L, "Emeralds are not exposed"));
    }

    // ===== Transfers =====

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOInterfaceImporterIntoQIO(GameTestHelper helper) {
        ServerPlayer owner = helper.makeMockServerPlayerInLevel();
        QIOFrequency frequency = createFrequency(helper, POS.west(3), owner);
        PartPos pos = placeInterface(helper, POS, Direction.UP);
        link(pos, frequency, owner);

        // Importer pulls from a chest into the network
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.IMPORTER_ITEM, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()));
        helper.setBlock(POS.east().east(), Blocks.CHEST);
        ChestBlockEntity chest = helper.getBlockEntity(POS.east().east());
        chest.setItem(0, new ItemStack(Items.GOLD_INGOT, 20));
        chest.setItem(1, new ItemStack(Items.DIAMOND_SWORD));
        placeVariableInWriter(helper, PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.Item.BOOLEAN_IMPORT, new ItemStack(RegistryEntries.ITEM_VARIABLE));

        helper.succeedWhen(() -> {
            helper.assertContainerEmpty(POS.east().east());
            helper.assertValueEqual(frequency.getStored(new ItemStack(Items.GOLD_INGOT)), 20L, "Gold was not moved into QIO");
            helper.assertValueEqual(frequency.getStored(new ItemStack(Items.DIAMOND_SWORD)), 1L, "Sword was not moved into QIO");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOInterfaceExporterFromQIO(GameTestHelper helper) {
        ServerPlayer owner = helper.makeMockServerPlayerInLevel();
        QIOFrequency frequency = createFrequency(helper, POS.west(3), owner);
        frequency.massInsert(new ItemStack(Items.GOLD_INGOT), 20, Action.EXECUTE);
        PartPos pos = placeInterface(helper, POS, Direction.UP);
        link(pos, frequency, owner);

        // Exporter pushes from the network into a chest
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.EXPORTER_ITEM, new ItemStack(PartTypes.EXPORTER_ITEM.getItem()));
        helper.setBlock(POS.east().east(), Blocks.CHEST);
        placeVariableInWriter(helper, PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.Item.BOOLEAN_EXPORT, new ItemStack(RegistryEntries.ITEM_VARIABLE));

        helper.succeedWhen(() -> {
            helper.assertValueEqual(((ChestBlockEntity) helper.getBlockEntity(POS.east().east())).countItem(Items.GOLD_INGOT), 20, "Gold was not moved to the chest");
            helper.assertValueEqual(frequency.getTotalItemCount(), 0L, "QIO was not drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOInterfaceChannels(GameTestHelper helper) {
        ServerPlayer owner = helper.makeMockServerPlayerInLevel();
        QIOFrequency frequencyA = createFrequency(helper, POS.west(3), owner);
        QIOFrequency frequencyB = createFrequency(helper, POS.west(3).north(2), owner);
        frequencyA.massInsert(new ItemStack(Items.DIAMOND), 5, Action.EXECUTE);
        frequencyB.massInsert(new ItemStack(Items.EMERALD), 7, Action.EXECUTE);

        PartPos posA = placeInterface(helper, POS, Direction.UP);
        PartPos posB = placeInterface(helper, POS, Direction.DOWN);
        getState(posA).setChannelInterface(1);
        getState(posB).setChannelInterface(2);
        link(posA, frequencyA, owner);
        link(posB, frequencyB, owner);

        helper.succeedWhen(() -> {
            helper.assertValueEqual(countInChannel(posA, 1, ItemStack.EMPTY), 5L, "Channel 1 contents are incorrect");
            helper.assertValueEqual(countInChannel(posA, 2, ItemStack.EMPTY), 7L, "Channel 2 contents are incorrect");
            helper.assertValueEqual(countInChannel(posA, 0, ItemStack.EMPTY), 0L, "Channel 0 should be empty");
        });
    }

    // ===== Errors =====

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOInterfaceFrequencyNotFound(GameTestHelper helper) {
        ServerPlayer owner = helper.makeMockServerPlayerInLevel();
        PartPos pos = placeInterface(helper, POS, Direction.UP);
        PartTypesMekanismTunnels.INTERFACE_QIO.setLink(getTarget(pos), getState(pos),
                new QIOFrequencyLink(new Frequency.FrequencyIdentity(uniqueFrequency(), SecurityMode.PUBLIC, owner.getUUID()), owner.getUUID()));
        assertStatus(helper, pos, QIOLinkStatus.NOT_FOUND);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOInterfaceFrequencyDeleted(GameTestHelper helper) {
        ServerPlayer owner = helper.makeMockServerPlayerInLevel();
        QIOFrequency frequency = createFrequency(helper, POS.west(3), owner);
        frequency.massInsert(new ItemStack(Items.DIAMOND), 5, Action.EXECUTE);
        PartPos pos = placeInterface(helper, POS, Direction.UP);
        link(pos, frequency, owner);
        assertStatus(helper, pos, QIOLinkStatus.LINKED);

        FrequencyType.QIO.getManager(frequency.getIdentity(), owner.getUUID()).remove(frequency.getKey(), owner.getUUID());

        helper.succeedWhen(() -> {
            assertStatus(helper, pos, QIOLinkStatus.NOT_FOUND);
            helper.assertValueEqual(countInChannel(pos, 0, ItemStack.EMPTY), 0L, "Deleted frequency is still exposed");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOInterfacePrivateFrequency(GameTestHelper helper) {
        ServerPlayer owner = helper.makeMockServerPlayerInLevel();
        ServerPlayer other = helper.makeMockServerPlayerInLevel();
        helper.assertFalse(owner.getUUID().equals(other.getUUID()), "Mock players must differ");
        QIOFrequency frequency = createFrequency(helper, POS.west(3), owner, uniqueFrequency(), SecurityMode.PRIVATE);
        frequency.massInsert(new ItemStack(Items.DIAMOND), 5, Action.EXECUTE);
        PartPos pos = placeInterface(helper, POS, Direction.UP);

        // Another player can not link a private frequency
        link(pos, frequency, other);
        assertStatus(helper, pos, QIOLinkStatus.NO_ACCESS);

        helper.startSequence()
                .thenIdle(20)
                .thenExecute(() -> {
                    helper.assertValueEqual(countInChannel(pos, 0, ItemStack.EMPTY), 0L, "Private frequency is exposed without access");
                    // The owner can
                    link(pos, frequency, owner);
                    assertStatus(helper, pos, QIOLinkStatus.LINKED);
                })
                .thenWaitUntil(() -> helper.assertValueEqual(countInChannel(pos, 0, ItemStack.EMPTY), 5L, "Private frequency is not exposed to its owner"))
                .thenSucceed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOInterfaceDuplicate(GameTestHelper helper) {
        ServerPlayer owner = helper.makeMockServerPlayerInLevel();
        QIOFrequency frequency = createFrequency(helper, POS.west(3), owner);
        frequency.massInsert(new ItemStack(Items.DIAMOND), 5, Action.EXECUTE);
        PartPos posA = placeInterface(helper, POS, Direction.UP);
        PartPos posB = placeInterface(helper, POS, Direction.DOWN);
        getState(posB).setChannelInterface(3);
        link(posA, frequency, owner);
        link(posB, frequency, owner);

        // The second link is refused, even on another channel
        assertStatus(helper, posA, QIOLinkStatus.LINKED);
        assertStatus(helper, posB, QIOLinkStatus.DUPLICATE);

        helper.startSequence()
                .thenWaitUntil(() -> helper.assertValueEqual(countInChannel(posA, 0, ItemStack.EMPTY), 5L, "Items are not exposed once"))
                .thenExecute(() -> {
                    helper.assertValueEqual(countInChannel(posA, 3, ItemStack.EMPTY), 0L, "Duplicate link exposes items");
                    // Once the first interface is removed, the second one takes over
                    PartHelpers.removePart(helper.getLevel(), posA.getPos().getBlockPos(), posA.getSide(), null, false, false, false);
                })
                .thenWaitUntil(() -> {
                    assertStatus(helper, posB, QIOLinkStatus.LINKED);
                    helper.assertValueEqual(countInChannel(posB, 3, ItemStack.EMPTY), 5L, "Second interface did not take over");
                })
                .thenSucceed();
    }

    // ===== Items and configuration =====

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOInterfaceItemPersistence(GameTestHelper helper) {
        ServerPlayer owner = helper.makeMockServerPlayerInLevel();
        QIOFrequency publicFrequency = createFrequency(helper, POS.west(3), owner);
        QIOFrequency privateFrequency = createFrequency(helper, POS.west(3).north(2), owner, uniqueFrequency(), SecurityMode.PRIVATE);
        PartPos posPublic = placeInterface(helper, POS, Direction.UP);
        PartPos posPrivate = placeInterface(helper, POS.north(2), Direction.UP);
        link(posPublic, publicFrequency, owner);
        link(posPrivate, privateFrequency, owner);
        ValueDeseralizationContext context = ValueDeseralizationContext.of(helper.getLevel());

        // The frequency is stored on the item, but not who authorized it
        ItemStack itemPublic = PartTypesMekanismTunnels.INTERFACE_QIO.getItemStack(context, getState(posPublic), false);
        ItemStack itemPrivate = PartTypesMekanismTunnels.INTERFACE_QIO.getItemStack(context, getState(posPrivate), false);
        QIOFrequencyLink storedLink = itemPrivate.get(org.cyclops.integratedmekanism.RegistryEntries.DATACOMPONENT_QIO_FREQUENCY_LINK);
        helper.assertTrue(storedLink != null, "Frequency was not stored on the item");
        helper.assertValueEqual(storedLink.getName(), privateFrequency.getName(), "Stored frequency is incorrect");
        helper.assertTrue(storedLink.authorizer() == null, "Authorizer was stored on the item");

        // Placing again keeps public frequencies working, but private ones need to be authorized again
        PartHelpers.removePart(helper.getLevel(), posPublic.getPos().getBlockPos(), posPublic.getSide(), null, false, false, false);
        PartHelpers.removePart(helper.getLevel(), posPrivate.getPos().getBlockPos(), posPrivate.getSide(), null, false, false, false);
        PartHelpers.addPart(helper.getLevel(), posPublic.getPos().getBlockPos(), posPublic.getSide(), PartTypesMekanismTunnels.INTERFACE_QIO, itemPublic);
        PartHelpers.addPart(helper.getLevel(), posPrivate.getPos().getBlockPos(), posPrivate.getSide(), PartTypesMekanismTunnels.INTERFACE_QIO, itemPrivate);

        helper.succeedWhen(() -> {
            assertStatus(helper, posPublic, QIOLinkStatus.LINKED);
            assertStatus(helper, posPrivate, QIOLinkStatus.NO_ACCESS);
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOInterfaceConfigurationCard(GameTestHelper helper) {
        ServerPlayer owner = helper.makeMockServerPlayerInLevel();
        QIOFrequency frequency = createFrequency(helper, POS.west(3), owner);
        frequency.massInsert(new ItemStack(Items.DIAMOND), 5, Action.EXECUTE);
        PartPos pos = placeInterface(helper, POS, Direction.UP);

        // Copy the frequency from the drive array onto a configuration card, as Mekanism does
        TileEntityQIOComponent driveArray = helper.getBlockEntity(POS.west(3));
        CompoundTag data = driveArray.getConfigurationData(helper.getLevel().registryAccess(), owner);
        ItemStack card = new ItemStack(MekanismItems.CONFIGURATION_CARD.get());
        card.set(MekanismDataComponents.CONFIGURATION_DATA, data);

        BlockPos blockPos = pos.getPos().getBlockPos();
        InteractionResult result = PartTypesMekanismTunnels.INTERFACE_QIO.onPartActivated(getState(pos), blockPos, helper.getLevel(), owner,
                InteractionHand.MAIN_HAND, card, new BlockHitResult(Vec3.atCenterOf(blockPos), Direction.UP, blockPos, false));

        helper.assertTrue(result.consumesAction(), "Card was not used");
        helper.succeedWhen(() -> {
            assertStatus(helper, pos, QIOLinkStatus.LINKED);
            helper.assertValueEqual(getState(pos).getLink().getName(), frequency.getName(), "Linked frequency is incorrect");
            helper.assertValueEqual(countInChannel(pos, 0, ItemStack.EMPTY), 5L, "Items are not exposed");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testQIOInterfaceFrequencyList(GameTestHelper helper) {
        ServerPlayer owner = helper.makeMockServerPlayerInLevel();
        ServerPlayer other = helper.makeMockServerPlayerInLevel();
        String publicName = uniqueFrequency();
        String privateName = uniqueFrequency();
        createFrequency(helper, POS.west(3), owner, publicName, SecurityMode.PUBLIC);
        createFrequency(helper, POS.west(3).north(2), owner, privateName, SecurityMode.PRIVATE);

        helper.assertTrue(listContains(ContainerPartInterfaceQIO.createFrequencyList(owner.getUUID()), publicName), "Owner does not see public frequency");
        helper.assertTrue(listContains(ContainerPartInterfaceQIO.createFrequencyList(owner.getUUID()), privateName), "Owner does not see private frequency");
        helper.assertTrue(listContains(ContainerPartInterfaceQIO.createFrequencyList(other.getUUID()), publicName), "Other player does not see public frequency");
        helper.assertFalse(listContains(ContainerPartInterfaceQIO.createFrequencyList(other.getUUID()), privateName), "Other player sees private frequency");
        helper.succeed();
    }

    private static boolean listContains(ListTag list, String name) {
        for (Tag tag : list) {
            if (((CompoundTag) tag).getString("name").equals(name)) {
                return true;
            }
        }
        return false;
    }
}
