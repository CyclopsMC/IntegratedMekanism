package org.cyclops.integratedmekanism.logicprogrammer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;
import org.cyclops.commoncapabilities.api.capability.fluidhandler.FluidMatch;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IPrototypedIngredientAlternatives;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import org.cyclops.commoncapabilities.api.capability.recipehandler.PrototypedIngredientAlternativesList;
import org.cyclops.commoncapabilities.api.capability.recipehandler.RecipeDefinition;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.MixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.PrototypedIngredient;
import org.cyclops.cyclopscore.helper.FluidHelpers;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.inventory.slot.SlotExtended;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.client.gui.subgui.IGuiInputElementValueType;
import org.cyclops.integrateddynamics.api.client.gui.subgui.ISubGuiBox;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.item.IValueTypeVariableFacade;
import org.cyclops.integrateddynamics.api.item.IVariableFacade;
import org.cyclops.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import org.cyclops.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import org.cyclops.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import org.cyclops.integrateddynamics.api.logicprogrammer.IValueTypeLogicProgrammerElement;
import org.cyclops.integrateddynamics.client.gui.container.ContainerScreenLogicProgrammerBase;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueHelpers;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeRecipe;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.helper.Helpers;
import org.cyclops.integrateddynamics.core.helper.L10NValues;
import org.cyclops.integrateddynamics.core.ingredient.ItemMatchProperties;
import org.cyclops.integrateddynamics.core.logicprogrammer.ValueTypeLPElementBase;
import org.cyclops.integrateddynamics.core.logicprogrammer.ValueTypeRecipeLPElement;
import org.cyclops.integrateddynamics.core.logicprogrammer.ValueTypeStringLPElementRenderPattern;
import org.cyclops.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import org.cyclops.integratedmekanism.IntegratedMekanism;
import org.cyclops.integratedmekanism.core.CapabilityHelpers;
import org.cyclops.integratedmekanism.ingredient.ChemicalMatch;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanism.network.packet.LogicProgrammerValueTypeRecipeChemicalSlotPropertiesChangedPacket;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Element for chemical recipes.
 * @author rubensworks
 */
public class ValueTypeRecipeChemicalLPElement implements IValueTypeLogicProgrammerElement<ISubGuiBox, ContainerScreenLogicProgrammerBase, ContainerLogicProgrammerBase> {

    public static final int SLOT_OFFSET = 4;
    public static final int SLOTS_PER_TYPE = 2;
    private static final IConfigRenderPattern CONFIG_RENDER_PATTERN = new IConfigRenderPattern.Base(154, 89, new Pair[]{
            // Items in
            Pair.of(20, 0), Pair.of(52, 0),
            // Fluids in
            Pair.of(20, 18),
            Pair.of(20, 36),
            // Chemicals in
            Pair.of(20, 54),
            Pair.of(20, 72),
            // Items out
            Pair.of(98, 0), Pair.of(130, 0),
            // Fluids out
            Pair.of(98, 18),
            Pair.of(98, 36),
            // Chemicals out
            Pair.of(98, 54),
            Pair.of(98, 72),
    }, null);

    @OnlyIn(Dist.CLIENT)
    public ValueTypeRecipeChemicalLPElementMasterSubGui lastGui;

    private List<ItemMatchProperties> inputStacks;
    private List<Pair<ItemStack, String>> inputFluids;
    private List<Pair<ItemStack, String>> inputChemicals;
    private List<ItemStack> outputStacks;
    private List<Pair<ItemStack, String>> outputFluids;
    private List<Pair<ItemStack, String>> outputChemicals;

    public ValueTypeRecipeChemicalLPElement() {
        this.activate(); // To construct lists.
    }

    public List<ItemMatchProperties> getInputStacks() {
        return inputStacks;
    }

    public List<Pair<ItemStack, String>> getInputFluids() {
        return inputFluids;
    }

    public List<Pair<ItemStack, String>> getInputChemicals() {
        return inputChemicals;
    }

    public List<ItemStack> getOutputStacks() {
        return outputStacks;
    }

    public List<Pair<ItemStack, String>> getOutputFluids() {
        return outputFluids;
    }

    public List<Pair<ItemStack, String>> getOutputChemicals() {
        return outputChemicals;
    }

    @Nullable
    @Override
    public <G2 extends Screen, C2 extends AbstractContainerMenu> IGuiInputElementValueType<?, G2, C2> createInnerGuiElement() {
        return null;
    }

    @Nullable
    public IGuiInputElementValueType<?, ContainerScreenLogicProgrammerBase, ContainerLogicProgrammerBase> getInnerGuiElement() {
        return null;
    }

    @Override
    public void loadTooltip(List<Component> lines) {
        getValueType().loadTooltip(lines, true, null);
    }

    @Override
    public ILogicProgrammerElementType getType() {
        return MekanismLogicProgrammerElementTypes.VALUETYPE_RECIPE_CHEMICAL;
    }

    @Override
    public String getMatchString() {
        return getName().getString().toLowerCase();
    }

    @Override
    public boolean matchesInput(IValueType<?> valueType) {
        return false;
    }

    @Override
    public boolean matchesOutput(IValueType<?> valueType) {
        return ValueHelpers.correspondsTo(valueType, valueType);
    }

    @Override
    public Component getName() {
        return Component.translatable("gui.integratedmekanism.logicprogrammer.element.recipechemical.name");
    }

    @Override
    public IConfigRenderPattern getRenderPattern() {
        return CONFIG_RENDER_PATTERN;
    }

    @Override
    public void onInputSlotUpdated(Player player, int slotId, ItemStack itemStack) {
        if (inputStacks == null) {
            return;
        }

        boolean refreshAmountBoxes = false;

        // Inputs
        if (slotId >= 0 && slotId < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE) {
            ItemStack itemStackOld = inputStacks.get(slotId).getItemStack();
            if (itemStackOld.getItem() != itemStack.getItem()) {
                inputStacks.set(slotId, new ItemMatchProperties(itemStack.copy()));
                if (MinecraftHelpers.isClientSideThread()) {
                    refreshPropertiesGui(slotId);
                }
            }
        }
        if (slotId >= ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE && slotId < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE * 2) {
            int index = slotId - ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE;
            String amountString = inputFluids.get(index).getRight();
            if (amountString.equalsIgnoreCase("0")) {
                int amount = FluidHelpers.getAmount(Helpers.getFluidStack(inputFluids.get(index).getLeft()));
                amountString = Integer.toString(amount);
            }
            inputFluids.set(index, Pair.of(itemStack.copy(), amountString));
            refreshAmountBoxes = true;
        }
        if (slotId >= ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE * 2 && slotId < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE * 3) {
            int index = slotId - ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE * 2;
            String amountString = inputChemicals.get(index).getRight();
            if (amountString.equalsIgnoreCase("0")) {
                int amount = getChemicalAmount(inputChemicals.get(index).getLeft());
                amountString = Integer.toString(amount);
            }
            inputChemicals.set(index, Pair.of(itemStack.copy(), amountString));
            refreshAmountBoxes = true;
        }

        // Outputs
        if (slotId >= ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE * 3 && slotId < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE * 4) {
            outputStacks.set(slotId - ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE * 3, itemStack.copy());
        }
        if (slotId >= ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE * 4 && slotId < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE * 5) {
            int index = slotId - ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE * 4;
            String amountString = outputFluids.get(index).getRight();
            if (amountString.equalsIgnoreCase("0")) {
                int amount = FluidHelpers.getAmount(Helpers.getFluidStack(outputFluids.get(index).getLeft()));
                amountString = Integer.toString(amount);
            }
            outputFluids.set(index, Pair.of(itemStack.copy(), amountString));
            refreshAmountBoxes = true;
        }
        if (slotId >= ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE * 5 && slotId < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE * 6) {
            int index = slotId - ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE * 5;
            String amountString = outputChemicals.get(index).getRight();
            if (amountString.equalsIgnoreCase("0")) {
                int amount = getChemicalAmount(outputChemicals.get(index).getLeft());
                amountString = Integer.toString(amount);
            }
            outputChemicals.set(index, Pair.of(itemStack.copy(), amountString));
            refreshAmountBoxes = true;
        }

        if (refreshAmountBoxes && MinecraftHelpers.isClientSideThread() && lastGui != null) {
            refreshAmountBoxes();
        }
    }

    protected static ChemicalStack getChemical(ItemStack itemStack) {
        return CapabilityHelpers.getChemicalHandler(itemStack)
                .map(handler -> {
                    int tanks = handler.getChemicalTanks();
                    for (int i = 0; i < tanks; i++) {
                        if (!handler.getChemicalInTank(i).isEmpty()) {
                            return handler.getChemicalInTank(i);
                        }
                    }
                    return ChemicalStack.EMPTY;
                })
                .orElse(ChemicalStack.EMPTY);
    }

    protected static int getChemicalAmount(ItemStack itemStack) {
        return (int) getChemical(itemStack).getAmount();
    }

    @OnlyIn(Dist.CLIENT)
    protected void refreshPropertiesGui(int slot) {
        if (this.lastGui != null && this.lastGui.isPropertySubGuiActive(slot)) {
            this.lastGui.propertiesSubGuis.get(slot).loadStateToGui();
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void refreshAmountBoxes() {
        if (this.lastGui != null && !this.lastGui.subGuiRecipe.getInputFluidAmounts() .isEmpty()) {
            for (int i = 0; i < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE; i++) {
                this.lastGui.subGuiRecipe.getInputFluidAmounts().get(i).setValue(inputFluids.get(i).getRight());
                this.lastGui.subGuiRecipe.getInputChemicalAmounts().get(i).setValue(inputChemicals.get(i).getRight());
                this.lastGui.subGuiRecipe.getOutputFluidAmounts().get(i).setValue(outputFluids.get(i).getRight());
                this.lastGui.subGuiRecipe.getOutputChemicalAmounts().get(i).setValue(outputChemicals.get(i).getRight());
            }
        }
    }

    public void sendSlotPropertiesToServer(int slotId, ItemMatchProperties props) {
        IntegratedMekanism._instance.getPacketHandler().sendToServer(
                new LogicProgrammerValueTypeRecipeChemicalSlotPropertiesChangedPacket(
                        slotId, props.isNbt(), props.getItemTag() == null ? "" : props.getItemTag(), props.getTagQuantity(), props.isReusable()));
    }

    // For JEI recipe transfer handler
    public boolean isValidForRecipeGrid(List<ItemMatchProperties> itemInputs, List<FluidStack> fluidInputs, List<ChemicalStack> chemicalInputs,
                                        List<ItemStack> itemOutputs, List<FluidStack> fluidOutputs, List<ChemicalStack> chemicalOutputs) {
        return itemInputs.size() <= SLOTS_PER_TYPE
                && fluidInputs.size() <= SLOTS_PER_TYPE
                && chemicalInputs.size() <= SLOTS_PER_TYPE
                && itemOutputs.size() <= SLOTS_PER_TYPE
                && fluidOutputs.size() <= SLOTS_PER_TYPE
                && chemicalOutputs.size() <= SLOTS_PER_TYPE;
    }

    protected void putItemPropertiesInContainer(ContainerLogicProgrammerBase container, int slot, ItemMatchProperties props) {
        putStackInContainer(container, slot, props.getItemStack());
        getInputStacks().set(slot, props);
    }

    protected void putStackInContainer(ContainerLogicProgrammerBase container, int slot, ItemStack itemStack) {
        // Offset: Player inventory, recipe grid slots
        container.setItem(container.getItems().size() - (36 + SLOTS_PER_TYPE * 6) + slot, 0, itemStack);
    }

    // For JEI recipe transfer handler
    public void setRecipeGrid(ContainerLogicProgrammerBase container,
                              List<ItemMatchProperties> itemInputs, List<FluidStack> fluidInputs, List<ChemicalStack> chemicalInputs,
                              List<ItemStack> itemOutputs, List<FluidStack> fluidOutputs, List<ChemicalStack> chemicalOutputs) {
        int slot = 0;

        // Fill input item slots
        for (ItemMatchProperties itemInput : itemInputs) {
            putItemPropertiesInContainer(container, slot, itemInput);
            slot++;
        }
        while (slot < SLOTS_PER_TYPE) {
            putItemPropertiesInContainer(container, slot, new ItemMatchProperties(ItemStack.EMPTY));
            slot++;
        }

        // Fill input fluid slots
        for (int i = 0; i < SLOTS_PER_TYPE; i++) {
            FluidStack input = FluidStack.EMPTY;
            if (fluidInputs.size() > i) {
                input = fluidInputs.get(i);
            }
            ItemStack stack = input.isEmpty() ? ItemStack.EMPTY : ValueTypeRecipeLPElement.getFluidBucket(input);
            String amount = String.valueOf(FluidHelpers.getAmount(input));
            putStackInContainer(container, slot, stack);
            inputFluids.set(i, Pair.of(stack, amount));
            slot++;
        }

        // Fill input chemical slots
        for (int i = 0; i < SLOTS_PER_TYPE; i++) {
            ChemicalStack input = ChemicalStack.EMPTY;
            if (i < chemicalInputs.size()) {
                input = chemicalInputs.get(i);
            }
            ItemStack stack = input.isEmpty() ? ItemStack.EMPTY : ValueObjectTypeChemicalStack.valueToItemStack(input);
            String amount = String.valueOf(input.getAmount());
            putStackInContainer(container, slot, stack);
            inputChemicals.set(i, Pair.of(stack, amount));
            slot++;
        }

        // Fill input output slots
        for (ItemStack itemOutput : itemOutputs) {
            putStackInContainer(container, slot, itemOutput);
            slot++;
        }
        while (slot < SLOTS_PER_TYPE * 4) {
            putStackInContainer(container, slot, ItemStack.EMPTY);
            slot++;
        }

        // Fill output fluid slot
        for (int i = 0; i < SLOTS_PER_TYPE; i++) {
            FluidStack output = FluidStack.EMPTY;
            if (i < fluidOutputs.size()) {
                output = fluidOutputs.get(i);
            }
            ItemStack stack = output.isEmpty() ? ItemStack.EMPTY : ValueTypeRecipeLPElement.getFluidBucket(output);
            String amount = String.valueOf(FluidHelpers.getAmount(output));
            putStackInContainer(container, slot, stack);
            outputFluids.set(i, Pair.of(stack, amount));
            slot++;
        }

        // Fill output chemical slot
        for (int i = 0; i < SLOTS_PER_TYPE; i++) {
            ChemicalStack output = ChemicalStack.EMPTY;
            if (i < chemicalOutputs.size()) {
                output = chemicalOutputs.get(i);
            }
            ItemStack stack = output.isEmpty() ? ItemStack.EMPTY : ValueObjectTypeChemicalStack.valueToItemStack(output);
            String amount = String.valueOf(output.getAmount());
            putStackInContainer(container, slot, stack);
            outputChemicals.set(i, Pair.of(stack, amount));
            slot++;
        }

        if (MinecraftHelpers.isClientSideThread()) {
            refreshAmountBoxes();
        }
    }

    protected boolean isInputValid() {
        return inputStacks.stream().anyMatch(ItemMatchProperties::isValid)
                || inputFluids.stream().anyMatch(pair -> !pair.getLeft().isEmpty() || !pair.getRight().equalsIgnoreCase("0"))
                || inputChemicals.stream().anyMatch(pair -> !pair.getLeft().isEmpty() || !pair.getRight().equalsIgnoreCase("0"));
    }

    protected boolean isOutputValid() {
        return outputStacks.stream().anyMatch(ItemStack::isEmpty)
                || outputFluids.stream().anyMatch(pair -> !pair.getLeft().isEmpty() || !pair.getRight().equalsIgnoreCase("0"))
                || outputChemicals.stream().anyMatch(pair -> !pair.getLeft().isEmpty() || !pair.getRight().equalsIgnoreCase("0"));
    }

    @Override
    public boolean isFor(IVariableFacade variableFacade) {
        if (variableFacade instanceof IValueTypeVariableFacade) {
            IValueTypeVariableFacade valueTypeFacade = (IValueTypeVariableFacade) variableFacade;
            if (valueTypeFacade.isValid()) {
                if (getValueType() == valueTypeFacade.getValueType()) {
                    ValueObjectTypeRecipe.ValueRecipe recipe = (ValueObjectTypeRecipe.ValueRecipe) ((IValueTypeVariableFacade<?>) variableFacade).getValue();
                    return recipe.getRawValue().map(r -> r.getInputComponents().contains(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK) || r.getOutput().getComponents().contains(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK)).orElse(false);
                }
            }
        }
        return false;
    }

    @Override
    public boolean canWriteElementPre() {
        return isInputValid() == isOutputValid(); // Not &&, because we also allow fully blank recipes
    }

    @Override
    public ItemStack writeElement(Player player, ItemStack itemStack) {
        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager().getRegistry(IVariableFacadeHandlerRegistry.class);
        return registry.writeVariableFacadeItem(!player.level().isClientSide(), itemStack, ValueTypes.REGISTRY,
                new ValueTypeLPElementBase.ValueTypeVariableFacadeFactory(getValueType(), getValue()), player.level(), player, RegistryEntries.BLOCK_LOGIC_PROGRAMMER.value().defaultBlockState());
    }

    @Override
    public void loadElement(IVariableFacade variableFacade) {
        if (variableFacade instanceof IValueTypeVariableFacade valueTypeVariableFacade) {
            setValue(valueTypeVariableFacade.getValue());
        }
    }

    @Override
    public boolean canCurrentlyReadFromOtherItem() {
        return true;
    }

    @Override
    public void activate() {
        inputStacks = Lists.newArrayList();
        for (int i = 0; i < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE; i++) {
            inputStacks.add(new ItemMatchProperties(ItemStack.EMPTY));
        }
        inputFluids = Lists.newArrayList();
        for (int i = 0; i < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE; i++) {
            inputFluids.add(Pair.of(ItemStack.EMPTY, "0"));
        }
        inputChemicals = Lists.newArrayList();
        for (int i = 0; i < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE; i++) {
            inputChemicals.add(Pair.of(ItemStack.EMPTY, "0"));
        }
        outputStacks = NonNullList.withSize(ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE, ItemStack.EMPTY);
        outputFluids = Lists.newArrayList();
        for (int i = 0; i < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE; i++) {
            outputFluids.add(i, Pair.of(ItemStack.EMPTY, "0"));
        }
        outputChemicals = Lists.newArrayList();
        for (int i = 0; i < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE; i++) {
            outputChemicals.add(Pair.of(ItemStack.EMPTY, "0"));
        }
    }

    @Override
    public void deactivate() {

    }

    @Override
    public Component validate() {
        for (Pair<ItemStack, String> input : inputFluids) {
            if (!input.getLeft().isEmpty() && Helpers.getFluidStack(input.getLeft()).isEmpty()) {
                return Component.translatable(L10NValues.VALUETYPE_OBJECT_FLUID_ERROR_NOFLUID);
            }
            try {
                Integer.parseInt(input.getRight());
            } catch (NumberFormatException e) {
                return Component.translatable(L10NValues.VALUETYPE_ERROR_INVALIDINPUT, input.getRight());
            }
        }
        for (Pair<ItemStack, String> input : outputFluids) {
            if (!input.getLeft().isEmpty() && Helpers.getFluidStack(input.getLeft()).isEmpty()) {
                return Component.translatable(L10NValues.VALUETYPE_OBJECT_FLUID_ERROR_NOFLUID);
            }
            try {
                Integer.parseInt(input.getRight());
            } catch (NumberFormatException e) {
                return Component.translatable(L10NValues.VALUETYPE_ERROR_INVALIDINPUT, input.getRight());
            }
        }

        for (Pair<ItemStack, String> input : inputChemicals) {
            if (!input.getLeft().isEmpty() && !CapabilityHelpers.getChemicalHandler(input.getLeft()).isPresent()) {
                return Component.translatable("valuetype.integratedmekanism.error.chemical.no_chemical");
            }
            try {
                Integer.parseInt(input.getRight());
            } catch (NumberFormatException e) {
                return Component.translatable(L10NValues.VALUETYPE_ERROR_INVALIDINPUT, input.getRight());
            }
        }
        for (Pair<ItemStack, String> input : outputChemicals) {
            if (!input.getLeft().isEmpty() && !CapabilityHelpers.getChemicalHandler(input.getLeft()).isPresent()) {
                return Component.translatable("valuetype.integratedmekanism.error.chemical.no_chemical");
            }
            try {
                Integer.parseInt(input.getRight());
            } catch (NumberFormatException e) {
                return Component.translatable(L10NValues.VALUETYPE_ERROR_INVALIDINPUT, input.getRight());
            }
        }

        // Validate input item tag strings if they are defined
        for (ItemMatchProperties inputStack : inputStacks) {
            if (inputStack.getItemTag() != null) {
                try {
                    ResourceLocation.parse(inputStack.getItemTag());
                } catch (ResourceLocationException e) {
                    return Component.translatable(L10NValues.VALUETYPE_ERROR_INVALIDINPUT, inputStack.getItemTag());
                }
            }
        }
        return null;
    }

    @Override
    public int getColor() {
        return ValueTypes.OBJECT_RECIPE.getDisplayColor();
    }

    @Override
    public String getSymbol() {
        return L10NHelpers.localize("gui.integratedmekanism.logicprogrammer.element.recipechemical.name");
    }

    @Override
    public boolean isItemValidForSlot(int slotId, ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean slotClick(int slotId, Slot slot, int mouseButton, ClickType clickType, Player player) {
        return ValueTypeRecipeLPElement.slotClickCommon(slotId, slot, mouseButton, clickType, player, getInputStacks(), SLOTS_PER_TYPE, (i) -> {
            if (MinecraftHelpers.isClientSideThread()) {
                lastGui.setPropertySubGui(i);
            }
        }, (i) -> {
            if (MinecraftHelpers.isClientSideThread()) {
                this.refreshPropertiesGui(i);
            }
        });
    }

    @Override
    public Slot createSlot(Container temporaryInputSlots, int slotId, int x, int y) {
        SlotExtended slot = new SlotExtended(temporaryInputSlots, slotId, x, y) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return ValueTypeRecipeChemicalLPElement.this.isItemValidForSlot(slotId, itemStack);
            }

            @Override
            public ItemStack getItem() {
                if (MinecraftHelpers.isClientSideThread() && slotId < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE) {
                    return ValueTypeRecipeLPElement.getRotatingItemFromTag(getInputStacks().get(slotId))
                            .orElseGet(super::getItem);
                }
                return super.getItem();
            }
        };
        slot.setPhantom(true);
        return slot;
    }

    @Override
    public int getItemStackSizeLimit() {
        return 64;
    }

    protected static <T> List<T> spliceTrailingEmpty(List<T> list, Predicate<T> isEmpty) {
        int lastNonEmpty = 0;
        for (int i = 0; i < list.size(); i++) {
            if (!isEmpty.test(list.get(i))) {
                lastNonEmpty = i + 1;
            }
        }
        return list.subList(0, lastNonEmpty);
    }

    protected List<FluidStack> convertFluidStacks(List<Pair<ItemStack, String>> fluidPairs) {
        return IntStream.range(0, ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE)
                .mapToObj(i -> {
                    if (i >= fluidPairs.size()) {
                        return FluidStack.EMPTY;
                    }
                    FluidStack fluidStack = Helpers.getFluidStack(fluidPairs.get(i).getLeft());
                    if (!fluidStack.isEmpty()) {
                        fluidStack.setAmount(Integer.parseInt(fluidPairs.get(i).getRight()));
                    }
                    return fluidStack;
                })
                .filter(i -> !i.isEmpty())
                .toList();
    }

    protected List<ChemicalStack> convertChemicalStacks(List<Pair<ItemStack, String>> chemicalPairs) {
        return IntStream.range(0, ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE)
                .<ChemicalStack>mapToObj(i -> {
                    if (i >= chemicalPairs.size()) {
                        return ChemicalStack.EMPTY;
                    }
                    ChemicalStack chemicalStack = getChemical(chemicalPairs.get(i).getLeft());
                    if (!chemicalStack.isEmpty()) {
                        chemicalStack.setAmount(Integer.parseInt(chemicalPairs.get(i).getRight()));
                    }
                    return chemicalStack;
                })
                .filter(i -> !i.isEmpty())
                .toList();
    }

    protected Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> getInputs(List<ItemMatchProperties> itemStacks,
                                                                                                      List<Pair<ItemStack, String>> fluidPairsIn,
                                                                                                      List<Pair<ItemStack, String>> chemicalPairsIn) {
        // Cut of list until last non-empty stack
        itemStacks = spliceTrailingEmpty(itemStacks, props -> !props.isValid());
        List<Pair<ItemStack, String>> fluidPairs = spliceTrailingEmpty(fluidPairsIn, pair -> pair.getLeft().isEmpty());
        List<Pair<ItemStack, String>> chemicalPairs = spliceTrailingEmpty(chemicalPairsIn, pair -> pair.getLeft().isEmpty());

        // Define actual stacks
        List<FluidStack> fluidStacks = convertFluidStacks(fluidPairs);
        List<ChemicalStack> chemicalStacks = convertChemicalStacks(chemicalPairs);

        Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs = Maps.newIdentityHashMap();
        List<IPrototypedIngredientAlternatives<ItemStack, Integer>> items = itemStacks.stream()
                .map(ItemMatchProperties::createPrototypedIngredient)
                .collect(Collectors.toList());
        List<PrototypedIngredientAlternativesList<FluidStack, Integer>> fluids = fluidStacks.stream()
                .map(fluidStack -> new PrototypedIngredientAlternativesList<>(Collections.singletonList(new PrototypedIngredient<>(IngredientComponent.FLUIDSTACK, fluidStack, FluidMatch.FLUID | FluidMatch.DATA))))
                .toList();
        List<PrototypedIngredientAlternativesList<ChemicalStack, Integer>> chemicals = chemicalStacks.stream()
                .map(chemicalStack -> new PrototypedIngredientAlternativesList<>(Collections.singletonList(new PrototypedIngredient<>(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, chemicalStack, ChemicalMatch.TYPE))))
                .toList();
        if (!items.isEmpty()) {
            inputs.put(IngredientComponent.ITEMSTACK, (List) items);
        }
        if (!fluids.isEmpty()) {
            inputs.put(IngredientComponent.FLUIDSTACK, (List) fluids);
        }
        if (!chemicals.isEmpty()) {
            inputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, (List) chemicals);
        }

        return inputs;
    }

    protected Map<IngredientComponent<?, ?>, List<?>> getOutputs(List<ItemStack> itemStacksIn,
                                                                 List<Pair<ItemStack, String>> fluidPairsIn,
                                                                 List<Pair<ItemStack, String>> chemicalPairsIn) {
        // Cut of list until last non-empty stack
        List<ItemStack> itemStacks = spliceTrailingEmpty(itemStacksIn, ItemStack::isEmpty);
        List<Pair<ItemStack, String>> fluidPairs = spliceTrailingEmpty(fluidPairsIn, pair -> pair.getLeft().isEmpty());
        List<Pair<ItemStack, String>> chemicalPairs = spliceTrailingEmpty(chemicalPairsIn, pair -> pair.getLeft().isEmpty());

        // Define actual stacks
        List<FluidStack> fluidStacks = convertFluidStacks(fluidPairs);
        List<ChemicalStack> chemicalStacks = convertChemicalStacks(chemicalPairs);

        Map<IngredientComponent<?, ?>, List<?>> outputs = Maps.newIdentityHashMap();
        if (!itemStacks.isEmpty()) {
            outputs.put(IngredientComponent.ITEMSTACK, itemStacks);
        }
        if (!fluidStacks.isEmpty()) {
            outputs.put(IngredientComponent.FLUIDSTACK, fluidStacks);
        }
        if (!chemicalStacks.isEmpty()) {
            outputs.put(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, chemicalStacks);
        }

        return outputs;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFocused(ISubGuiBox subGui) {
        if (subGui instanceof ValueTypeStringLPElementRenderPattern) {
            return ((ValueTypeStringLPElementRenderPattern) subGui).getTextField().isFocused();
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setFocused(ISubGuiBox subGui, boolean focused) {
        if (subGui instanceof ValueTypeStringLPElementRenderPattern) {
            ((ValueTypeStringLPElementRenderPattern) subGui).getTextField().setFocused(focused);
        }
    }

    @Override
    public IValueType<?> getValueType() {
        return ValueTypes.OBJECT_RECIPE;
    }

    @Override
    public IValue getValue() {
        if (!isInputValid() && !isOutputValid()) {
            return ValueObjectTypeRecipe.ValueRecipe.of(null);
        }
        return ValueObjectTypeRecipe.ValueRecipe.of(
                new RecipeDefinition(getInputs(this.inputStacks, this.inputFluids, this.inputChemicals),
                        ValueTypeRecipeLPElement.getInputsReusable(this.inputStacks),
                        new MixedIngredients(getOutputs(this.outputStacks, this.outputFluids, this.outputChemicals))));
    }

    @Override
    public void setValue(IValue value) {
        ValueObjectTypeRecipe.ValueRecipe valueRecipe = (ValueObjectTypeRecipe.ValueRecipe) value;
        valueRecipe.getRawValue().ifPresent(recipe -> {
            loadInputItems(recipe);
            loadInputFluids(recipe);
            loadInputChemicals(recipe);

            loadOutputItems(recipe);
            loadOutputFluids(recipe);
            loadOutputChemicals(recipe);
        });
    }

    private void loadInputItems(IRecipeDefinition recipe) {
        List<IPrototypedIngredientAlternatives<ItemStack, Integer>> listAlternatives = recipe.getInputs(IngredientComponent.ITEMSTACK);
        for (int i = 0; i < listAlternatives.size(); i++) {
            IPrototypedIngredientAlternatives<ItemStack, Integer> prototypes = listAlternatives.get(i);
            boolean reusable = recipe.isInputReusable(IngredientComponent.ITEMSTACK, i);
            ItemMatchProperties itemMatchProperties = ItemMatchProperties.fromPrototypedIngredient(prototypes, reusable);
            this.inputStacks.set(i, itemMatchProperties);
        }
    }

    protected static <T> List<T> ensureLength(List<T> list, int minLength, T filler) {
        if (list.size() > minLength) {
            return list.subList(0, minLength);
        } else if (list.size() < minLength) {
            return Stream.concat(list.stream(), IntStream.range(0, minLength - list.size()).mapToObj(i -> filler))
                    .collect(Collectors.toList());
        }
        return list;
    }

    private <T, M> List<T> loadFirstInputs(IRecipeDefinition recipe, IngredientComponent<T, M> ingredientComponent) {
        List<IPrototypedIngredientAlternatives<T, M>> listAlternatives = recipe.getInputs(ingredientComponent);
        return ensureLength(listAlternatives.stream()
                .map(prototypes -> {
                    if (!prototypes.getAlternatives().isEmpty()) {
                        return prototypes.getAlternatives().stream().findFirst().get().getPrototype();
                    }
                    return ingredientComponent.getMatcher().getEmptyInstance();
                })
                .toList(), SLOTS_PER_TYPE, ingredientComponent.getMatcher().getEmptyInstance());
    }

    private void loadInputFluids(IRecipeDefinition recipe) {
        this.inputFluids = loadFirstInputs(recipe, IngredientComponent.FLUIDSTACK).stream()
                .map(fluidStack -> Pair.of(fluidStack.isEmpty() ? ItemStack.EMPTY : ValueTypeRecipeLPElement.getFluidBucket(fluidStack), Integer.toString(fluidStack.getAmount())))
                .collect(Collectors.toList());
    }

    private void loadInputChemicals(IRecipeDefinition recipe) {
        this.inputChemicals = loadFirstInputs(recipe, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).stream()
                .map(chemicalStack -> Pair.of(chemicalStack.isEmpty() ? ItemStack.EMPTY : ValueObjectTypeChemicalStack.valueToItemStack(chemicalStack), Integer.toString((int) chemicalStack.getAmount())))
                .collect(Collectors.toList());
    }

    private void loadOutputItems(IRecipeDefinition recipe) {
        List<ItemStack> instances = recipe.getOutput().getInstances(IngredientComponent.ITEMSTACK);
        if (instances.size() > 0) {
            outputStacks.set(0, instances.get(0));
        }
        if (instances.size() > 1) {
            outputStacks.set(1, instances.get(1));
        }
    }

    private void loadOutputFluids(IRecipeDefinition recipe) {
        this.outputFluids = ensureLength(recipe.getOutput().getInstances(IngredientComponent.FLUIDSTACK).stream()
                .map(fluidStack -> Pair.of(ValueTypeRecipeLPElement.getFluidBucket(fluidStack), Integer.toString(fluidStack.getAmount())))
                .toList(), SLOTS_PER_TYPE, Pair.of(ItemStack.EMPTY, "0"));
    }

    private void loadOutputChemicals(IRecipeDefinition recipe) {
        this.outputChemicals = ensureLength(recipe.getOutput().getInstances(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK).stream()
                .map(chemicalStack -> Pair.of(ValueObjectTypeChemicalStack.valueToItemStack(chemicalStack), Integer.toString((int) chemicalStack.getAmount())))
                .toList(), SLOTS_PER_TYPE, Pair.of(ItemStack.EMPTY, "0"));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setValueInGui(ISubGuiBox subGui) {
        ValueTypeRecipeChemicalLPElementRecipeSubGui gui = ((ValueTypeRecipeChemicalLPElementMasterSubGui) subGui).getSubGuiRecipe();
        setValueInContainer(gui.getContainer());
        if (gui.getInputFluidAmounts() != null) {
            for (int i = 0; i < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE; i++) {
                gui.getInputFluidAmounts().get(i).setValue(inputFluids.get(i).getRight());
            }
            for (int i = 0; i < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE; i++) {
                gui.getInputChemicalAmounts().get(i).setValue(inputChemicals.get(i).getRight());
            }
            for (int i = 0; i < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE; i++) {
                gui.getOutputFluidAmounts().get(i).setValue(outputFluids.get(i).getRight());
            }
            for (int i = 0; i < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE; i++) {
                gui.getOutputChemicalAmounts().get(i).setValue(outputChemicals.get(i).getRight());
            }
        }
    }

    @Override
    public void setValueInContainer(ContainerLogicProgrammerBase container) {
        Container slots = container.getTemporaryInputSlots();

        // Input slots
        int slot = 0;
        for (int i = 0; i < this.inputStacks.size(); i++) {
            slots.setItem(slot++, this.inputStacks.get(i).getItemStack());
        }
        for (int i = 0; i < this.inputFluids.size(); i++) {
            slots.setItem(slot++, this.inputFluids.get(i).getLeft());
        }
        for (int i = 0; i < this.inputChemicals.size(); i++) {
            slots.setItem(slot++, this.inputChemicals.get(i).getLeft());
        }

        // Output slots
        for (int i = 0; i < this.outputStacks.size(); i++) {
            slots.setItem(slot++, this.outputStacks.get(i));
            // No need to set slot type, as this can't be changed for output stacks
        }
        for (int i = 0; i < this.outputFluids.size(); i++) {
            slots.setItem(slot++, this.outputFluids.get(i).getLeft());
        }
        for (int i = 0; i < this.outputChemicals.size(); i++) {
            slots.setItem(slot++, this.outputChemicals.get(i).getLeft());
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ISubGuiBox createSubGui(int baseX, int baseY, int maxWidth, int maxHeight,
                                   ContainerScreenLogicProgrammerBase gui, ContainerLogicProgrammerBase container) {
        return lastGui = new ValueTypeRecipeChemicalLPElementMasterSubGui(this, baseX, baseY, maxWidth, maxHeight, gui, container);
    }

}
