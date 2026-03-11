package org.cyclops.integratedmekanism.gametest;

import com.mojang.authlib.GameProfile;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.expression.ILazyExpressionValueCache;
import org.cyclops.integrateddynamics.api.evaluate.operator.IOperator;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.evaluate.variable.IVariable;
import org.cyclops.integrateddynamics.api.evaluate.variable.IVariableInvalidateListener;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectRead;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectVariable;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectWrite;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integrateddynamics.core.evaluate.expression.LazyExpression;
import org.cyclops.integrateddynamics.core.evaluate.operator.Operators;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integrateddynamics.core.part.PartTypes;
import org.cyclops.integrateddynamics.core.part.event.PartVariableDrivenVariableContentsUpdatedEvent;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.aspect.MekanismTunnelsAspects;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.part.PartTypesMekanismTunnels;
import org.cyclops.integratedmekanism.operator.MekanismOperators;
import org.cyclops.integratedmekanism.part.aspect.MekanismAspects;
import org.cyclops.integratedmekanism.part.PartTypesMekanism;
import org.cyclops.integratedmekanism.value.MekanismValueTypes;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.createVariableForValue;

/**
 * Game tests for all advancements in Integrated Mekanism.
 */
@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsAdvancements {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final int TIMEOUT = 200;
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 0, 2);

    // ===== Helpers =====

    /**
     * Creates a minimal mock ServerPlayer without going through placeNewPlayer(),
     * which avoids issues with custom network payloads in the test environment.
     * awardRecipes() is overridden to be a no-op, preventing a NPE when advancements
     * with recipe rewards (like root) are granted and try to sync via the null connection.
     */
    private static ServerPlayer createMockPlayer(GameTestHelper helper) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "test-advancement-player");
        return new ServerPlayer(
                helper.getLevel().getServer(), helper.getLevel(), profile, ClientInformation.createDefault()
        ) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return true;
            }

            @Override
            public void awardRecipes(Collection<RecipeHolder<?>> recipes) {
                // no-op: skip ClientboundUpdateRecipesPacket since connection is null in tests
            }
        };
    }

    /**
     * Creates a simple cache for lazy expressions.
     */
    private static ILazyExpressionValueCache simpleCache() {
        return new ILazyExpressionValueCache() {
            private final Map<Integer, IValue> values = new HashMap<>();
            @Override public void setValue(int id, IValue value) { values.put(id, value); }
            @Override public boolean hasValue(int id) { return values.containsKey(id); }
            @Override public IValue getValue(int id) { return values.get(id); }
            @Override public void removeValue(int id) { values.remove(id); }
        };
    }

    /**
     * Creates a mock aspect variable for the given aspect read.
     */
    private static <V extends IValue> IAspectVariable<V> makeAspectVar(IAspectRead<V, ?> aspect) {
        return new IAspectVariable<V>() {
            @Override public PartTarget getTarget() { return null; }
            @Override public IAspectRead<V, ?> getAspect() { return aspect; }
            @Override public IValueType<V> getType() { return aspect.getValueType(); }
            @Override public V getValue() throws EvaluationException { return aspect.getValueType().getDefault(); }
            @Override public void invalidate() {}
            @Override public void addInvalidationListener(IVariableInvalidateListener l) {}
        };
    }

    /**
     * Creates a mock operator variable (LazyExpression) for the given operator.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <V extends IValue> LazyExpression<V> makeOpVar(
            IOperator operator,
            IValueType<V> type,
            IVariable<?>... inputs) {
        ILazyExpressionValueCache cache = simpleCache();
        return new LazyExpression<V>(0, operator, inputs, cache) {
            @Override public IValueType<V> getType() { return type; }
            @Override public V getValue() throws EvaluationException { return type.getDefault(); }
        };
    }

    /**
     * Fires the part_variable_driven event with the given variable and a display panel.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void fireVariableDrivenEvent(ServerPlayer player, IVariable<?> variable) {
        NeoForge.EVENT_BUS.post(new PartVariableDrivenVariableContentsUpdatedEvent(
                null, null, null, PartTypes.DISPLAY_PANEL, null, player, variable, null));
    }

    /**
     * Places a variable item in the writer part's inventory slot for the given aspect,
     * then calls updateActivation with the given player so the part_writer_aspect event fires.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void placeVariableWithPlayer(Level level, PartPos partPos,
            IAspectWrite<?, ?> aspect, ItemStack variable, ServerPlayer player) {
        PartHelpers.PartStateHolder<?, ?> holder = PartHelpers.getPart(partPos);
        IPartTypeWriter partType = (IPartTypeWriter) holder.getPart();
        IPartStateWriter partState = (IPartStateWriter) holder.getState();

        List<IAspectWrite> aspects = partType.getWriteAspects();
        int slot = -1;
        for (int i = 0; i < aspects.size(); i++) {
            if (aspects.get(i) == aspect) {
                slot = i;
                break;
            }
        }
        if (slot < 0) {
            throw new GameTestAssertException("Aspect not found in part: " + aspect);
        }

        partState.getInventory().setItem(slot, variable);
        partType.updateActivation(PartTarget.fromCenter(partPos), partState, player);
    }

    /**
     * Asserts that a given advancement (by namespace:path) has been completed by the player.
     */
    private static void assertAdvancement(GameTestHelper helper, ServerPlayer player,
            String namespace, String path) {
        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(namespace, path);
        AdvancementHolder advancement = helper.getLevel().getServer().getAdvancements().get(advancementId);
        helper.assertTrue(advancement != null, "Advancement not found: " + advancementId);
        helper.assertTrue(
                player.getAdvancements().getOrStartProgress(advancement).isDone(),
                "Advancement not granted: " + advancementId
        );
    }

    /**
     * Asserts that a given advancement (by namespace:path) has NOT been completed by the player.
     */
    private static void assertAdvancementNotDone(GameTestHelper helper, ServerPlayer player,
            String namespace, String path) {
        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(namespace, path);
        AdvancementHolder advancement = helper.getLevel().getServer().getAdvancements().get(advancementId);
        if (advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone()) {
            throw new GameTestAssertException("Advancement should NOT have been obtained: " + advancementId);
        }
    }

    // ===== Root advancement (minecraft:inventory_changed) =====

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementRoot(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);

        ItemStack variable = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        player.getInventory().setItem(0, variable);
        CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), variable);

        assertAdvancement(helper, player, Reference.MOD_ID, "root");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementRootNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);

        // Use a dirt block instead of a variable - should NOT trigger the advancement
        ItemStack dirt = new ItemStack(net.minecraft.world.item.Items.DIRT);
        player.getInventory().setItem(0, dirt);
        CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), dirt);

        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "root");
        helper.succeed();
    }

    // ===== cyclopscore:item_crafted advancements =====

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementReader(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        ItemStack crafted = new ItemStack(PartTypesMekanism.CHEMICAL_READER.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancement(helper, player, Reference.MOD_ID, "reading/reader");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementReaderNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        // Craft interface_chemical instead of chemical_reader - should NOT trigger the advancement
        ItemStack crafted = new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "reading/reader");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementInterfaceChemical(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        ItemStack crafted = new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancement(helper, player, Reference.MOD_ID, "tunnels/interface_chemical");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementInterfaceChemicalNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        // Craft chemical_reader instead of interface_chemical - should NOT trigger the advancement
        ItemStack crafted = new ItemStack(PartTypesMekanism.CHEMICAL_READER.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "tunnels/interface_chemical");
        helper.succeed();
    }

    // ===== integrateddynamics:part_variable_driven advancements =====

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementChemicalTankChemical(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        IAspectVariable<ValueObjectTypeChemicalStack.ValueChemicalStack> aspectVar =
                makeAspectVar(MekanismAspects.Read.Chemical.CHEMICALSTACK);
        fireVariableDrivenEvent(player, aspectVar);
        assertAdvancement(helper, player, Reference.MOD_ID, "reading/chemical_tank_chemical");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementChemicalTankChemicalNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        // Use a different aspect (BOOLEAN_EMPTY) - should NOT trigger the advancement
        IAspectVariable<ValueTypeBoolean.ValueBoolean> aspectVar =
                makeAspectVar(MekanismAspects.Read.Chemical.BOOLEAN_EMPTY);
        fireVariableDrivenEvent(player, aspectVar);
        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "reading/chemical_tank_chemical");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementChemicalTankEmpty(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        IAspectVariable<ValueTypeBoolean.ValueBoolean> aspectVar =
                makeAspectVar(MekanismAspects.Read.Chemical.BOOLEAN_EMPTY);
        fireVariableDrivenEvent(player, aspectVar);
        assertAdvancement(helper, player, Reference.MOD_ID, "reading/chemical_tank_empty");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementChemicalTankEmptyNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        // Use CHEMICALSTACK aspect instead of BOOLEAN_EMPTY - should NOT trigger the advancement
        IAspectVariable<ValueObjectTypeChemicalStack.ValueChemicalStack> aspectVar =
                makeAspectVar(MekanismAspects.Read.Chemical.CHEMICALSTACK);
        fireVariableDrivenEvent(player, aspectVar);
        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "reading/chemical_tank_empty");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementChemicalRadioactive(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        LazyExpression<ValueTypeBoolean.ValueBoolean> opVar =
                makeOpVar(MekanismOperators.OBJECT_CHEMICALSTACK_ISRADIOACTIVE, ValueTypes.BOOLEAN);
        fireVariableDrivenEvent(player, opVar);
        assertAdvancement(helper, player, Reference.MOD_ID, "reading/chemical_radioactive");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementChemicalRadioactiveNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        // Use a different operator (RELATIONAL_LT) - should NOT trigger the advancement
        LazyExpression<ValueTypeBoolean.ValueBoolean> opVar =
                makeOpVar(Operators.RELATIONAL_LT, ValueTypes.BOOLEAN);
        fireVariableDrivenEvent(player, opVar);
        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "reading/chemical_radioactive");
        helper.succeed();
    }

    // ===== integrateddynamics:part_writer_aspect advancements =====

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementImportAllChemicals(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                PartTypesMekanismTunnels.IMPORTER_CHEMICAL,
                new ItemStack(PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        ItemStack variable = createVariableForValue(level, ValueTypes.BOOLEAN,
                ValueTypeBoolean.ValueBoolean.of(true));
        placeVariableWithPlayer(level, partPos, MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT, variable, player);

        assertAdvancement(helper, player, Reference.MOD_ID, "tunnels/import_all_chemicals");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementImportAllChemicalsNegative(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                PartTypesMekanismTunnels.IMPORTER_CHEMICAL,
                new ItemStack(PartTypesMekanismTunnels.IMPORTER_CHEMICAL.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Use boolean FALSE instead of true - should NOT trigger the advancement
        ItemStack variable = createVariableForValue(level, ValueTypes.BOOLEAN,
                ValueTypeBoolean.ValueBoolean.of(false));
        placeVariableWithPlayer(level, partPos, MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT, variable, player);

        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "tunnels/import_all_chemicals");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementExportChemical(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                PartTypesMekanismTunnels.EXPORTER_CHEMICAL,
                new ItemStack(PartTypesMekanismTunnels.EXPORTER_CHEMICAL.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        ItemStack variable = createVariableForValue(level, MekanismValueTypes.OBJECT_CHEMICALSTACK,
                ValueObjectTypeChemicalStack.ValueChemicalStack.of(ChemicalStack.EMPTY));
        placeVariableWithPlayer(level, partPos, MekanismTunnelsAspects.Write.Chemical.CHEMICALSTACK_EXPORT, variable, player);

        assertAdvancement(helper, player, Reference.MOD_ID, "tunnels/export_chemical");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementExportChemicalNegative(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                PartTypesMekanismTunnels.EXPORTER_CHEMICAL,
                new ItemStack(PartTypesMekanismTunnels.EXPORTER_CHEMICAL.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Use a boolean variable instead of chemicalstack - should NOT trigger the advancement
        ItemStack variable = createVariableForValue(level, ValueTypes.BOOLEAN,
                ValueTypeBoolean.ValueBoolean.of(true));
        placeVariableWithPlayer(level, partPos, MekanismTunnelsAspects.Write.Chemical.CHEMICALSTACK_EXPORT, variable, player);

        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "tunnels/export_chemical");
        helper.succeed();
    }

}
