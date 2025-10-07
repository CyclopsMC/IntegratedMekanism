package org.cyclops.integratedmekanism.gametest;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mekanism.api.Action;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.ChemicalStack;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tile.TileEntityChemicalTank;
import mekanism.common.tile.TileEntityFluidTank;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.machine.TileEntityRotaryCondensentrator;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.commoncapabilities.IngredientComponents;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IPrototypedIngredientAlternatives;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import org.cyclops.commoncapabilities.api.capability.recipehandler.PrototypedIngredientAlternativesList;
import org.cyclops.commoncapabilities.api.capability.recipehandler.RecipeDefinition;
import org.cyclops.commoncapabilities.api.ingredient.IMixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.MixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.PrototypedIngredient;
import org.cyclops.integratedcrafting.part.PartTypeInterfaceCrafting;
import org.cyclops.integratedcrafting.part.aspect.CraftingAspectWriteBuilders;
import org.cyclops.integratedcrafting.part.aspect.CraftingAspects;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeRecipe;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.ingredient.ChemicalMatch;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.part.PartTypesMekanismTunnels;
import org.cyclops.integratedtunnels.part.PartTypes;

import java.util.List;
import java.util.Map;

import static org.cyclops.integratedcrafting.gametest.GameTestHelpersIntegratedCrafting.*;
import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.createVariableForValue;
import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.placeVariableInWriter;

@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsCrafting {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final int TIMEOUT = 2000;
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 0, 2);

    public static TileEntityChemicalTank setChemicalTank(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, MekanismBlocks.BASIC_CHEMICAL_TANK.get());
        TileEntityChemicalTank chemicalTank = helper.getBlockEntity(pos);
        enableIo(chemicalTank);
        return chemicalTank;
    }

    public static TileEntityFluidTank setFluidTank(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, MekanismBlocks.BASIC_FLUID_TANK.get());
        TileEntityFluidTank fluidTank = helper.getBlockEntity(pos);
        return fluidTank;
    }

    public static void enableIo(TileEntityConfigurableMachine blockEntity) {
        for (RelativeSide relativeSide : RelativeSide.values()) {
            blockEntity.getConfig().getConfig(TransmissionType.CHEMICAL).setDataType(DataType.INPUT_OUTPUT, relativeSide);
            blockEntity.getConfig().getConfig(TransmissionType.ITEM).setDataType(DataType.INPUT_OUTPUT, relativeSide);
        }
        blockEntity.getConfig().getConfig(TransmissionType.ITEM).setCanEject(true);
    }

    public static TileEntityConfigurableMachine getMachine(GameTestHelper helper, INetworkPositions<PartTypeInterfaceCrafting.State> positions) {
        PartPos iface = positions.interfaces().get(0);
        return (TileEntityConfigurableMachine) helper.getLevel().getBlockEntity(iface.getPos().getBlockPos().relative(iface.getSide()));
    }

    public static void enableChemicalRecipeInWriter(GameTestHelper helper, PartPos writerPos, IRecipeDefinition recipe) {
        placeVariableInWriter(helper.getLevel(), writerPos, CraftingAspects.Write.RECIPE_CRAFT, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_RECIPE, ValueObjectTypeRecipe.ValueRecipe.of(recipe)));
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testCraftingCrystalizeBrineToSalt(GameTestHelper helper) {
        INetworkPositions<PartTypeInterfaceCrafting.State> positions = createBasicNetwork(helper, POS, MekanismBlocks.CHEMICAL_CRYSTALLIZER.get());
        TileEntityConfigurableMachine machine = getMachine(helper, positions);
        machine.insertEnergy(100_000, Action.EXECUTE);
        machine.getConfig().getConfig(TransmissionType.CHEMICAL).setDataType(DataType.INPUT, RelativeSide.TOP);
        machine.getConfig().getConfig(TransmissionType.ITEM).setDataType(DataType.OUTPUT, RelativeSide.TOP);
        machine.getConfig().getConfig(TransmissionType.ITEM).setEjecting(true);

        // Get interface chest
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.east());

        // Add chemical tank and interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.SOUTH, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));
        TileEntityChemicalTank tank = setChemicalTank(helper, POS.south());
        tank.getChemicalTank().setStack(new ChemicalStack(MekanismChemicals.BRINE, 15));

        // Add recipe to crafting interface
        // mekanism:crystallizing/salt
        Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs = Maps.newIdentityHashMap();
        inputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, List.of(new PrototypedIngredientAlternativesList<>(Lists.newArrayList(
                new PrototypedIngredient<>(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, new ChemicalStack(MekanismChemicals.BRINE, 15L), ChemicalMatch.EXACT)
        ))));
        IMixedIngredients output = MixedIngredients.ofInstance(IngredientComponent.ITEMSTACK, new ItemStack(MekanismItems.SALT.asItem()));
        ItemStack variableRecipe = createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_RECIPE, ValueObjectTypeRecipe.ValueRecipe.of(new RecipeDefinition(inputs, output)));
        positions.interfaceStates().get(0).getInventoryVariables().setItem(0, variableRecipe);

        // Enable crafting aspect in crafting writer
        enableRecipeInWriter(helper, positions.writer(), new ItemStack(MekanismItems.SALT.asItem()));

        helper.succeedWhen(() -> {
            // Check crafting interface state
            helper.assertTrue(positions.interfaceStates().get(0).isRecipeSlotValid(0), "Recipe in crafting interface is not valid");

            // Check if items have been crafted
            helper.assertValueEqual(chestIn.getItem(0).getItem(), MekanismItems.SALT.asItem(), "Slot 0 item is incorrect");
            helper.assertValueEqual(chestIn.getItem(0).getCount(), 1, "Slot 0 amount is incorrect");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testCraftingRotaryWaterVapor(GameTestHelper helper) {
        INetworkPositions<PartTypeInterfaceCrafting.State> positions = createBasicNetwork(helper, POS, MekanismBlocks.ROTARY_CONDENSENTRATOR.get());
        TileEntityConfigurableMachine machine = getMachine(helper, positions);
        machine.insertEnergy(100_000, Action.EXECUTE);
        machine.getConfig().getConfig(TransmissionType.FLUID).setDataType(DataType.INPUT, RelativeSide.TOP);
        machine.getConfig().getConfig(TransmissionType.CHEMICAL).setDataType(DataType.OUTPUT, RelativeSide.TOP);
        machine.getConfig().getConfig(TransmissionType.CHEMICAL).setEjecting(true);
        ((TileEntityRotaryCondensentrator) machine).nextMode();

        // Place fluid tank and interface
        PartHelpers.removePart(helper.getLevel(), helper.absolutePos(POS), Direction.EAST, null, true, false, false);
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.EAST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));
        TileEntityFluidTank fluidTank = setFluidTank(helper, POS.east());
        fluidTank.setFluidInTank(0, new FluidStack(Fluids.WATER, 10));

        // Add chemical tank and interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.SOUTH, PartTypesMekanismTunnels.INTERFACE_CHEMICAL, new ItemStack(PartTypesMekanismTunnels.INTERFACE_CHEMICAL.getItem()));
        TileEntityChemicalTank tank = setChemicalTank(helper, POS.south());

        // Add recipe to crafting interface
        // mekanism:rotary/water_vapor
        Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs = Maps.newIdentityHashMap();
        inputs.put(IngredientComponents.FLUIDSTACK, List.of(new PrototypedIngredientAlternativesList<>(Lists.newArrayList(
                new PrototypedIngredient<>(IngredientComponents.FLUIDSTACK, new FluidStack(Fluids.WATER, 1), ChemicalMatch.EXACT)
        ))));
        IMixedIngredients output = MixedIngredients.ofInstance(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, new ChemicalStack(MekanismChemicals.WATER_VAPOR, 1L));
        RecipeDefinition recipe = new RecipeDefinition(inputs, output);
        ItemStack variableRecipe = createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_RECIPE, ValueObjectTypeRecipe.ValueRecipe.of(recipe));
        positions.interfaceStates().get(0).getInventoryVariables().setItem(0, variableRecipe);

        // Enable crafting aspect in crafting writer
        enableChemicalRecipeInWriter(helper, positions.writer(), recipe);

        // Increase crafting amount
        PartPos posCraftingWriter = positions.writer();
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(posCraftingWriter);
        IAspectProperties properties = CraftingAspects.Write.RECIPE_CRAFT.getProperties(partStateHolder.getPart(), PartTarget.fromCenter(posCraftingWriter), partStateHolder.getState());
        properties.setValue(CraftingAspectWriteBuilders.PROP_CRAFT_AMOUNT, ValueTypeInteger.ValueInteger.of(10));
        partStateHolder.getState().setAspectProperties(CraftingAspects.Write.RECIPE_CRAFT, properties);

        helper.succeedWhen(() -> {
            // Check crafting interface state
            helper.assertTrue(positions.interfaceStates().get(0).isRecipeSlotValid(0), "Recipe in crafting interface is not valid");

            // Check if chemical has been crafted
            ChemicalStack stack = tank.getChemicalTank().getStack();
            helper.assertValueEqual(stack.getChemical(), MekanismChemicals.WATER_VAPOR.get(), "Slot 0 item is incorrect");
            helper.assertValueEqual(stack.getAmount(), 10L, "Slot 0 amount is incorrect");
        });
    }

}
