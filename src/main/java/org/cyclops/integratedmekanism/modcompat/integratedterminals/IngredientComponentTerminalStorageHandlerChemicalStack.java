package org.cyclops.integratedmekanism.modcompat.integratedterminals;

import com.google.common.collect.Lists;
import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.client.gui.GuiUtils;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.client.gui.GuiGraphicsExtended;
import org.cyclops.cyclopscore.helper.GuiHelpers;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.ingredient.storage.InconsistentIngredientInsertionException;
import org.cyclops.cyclopscore.ingredient.storage.IngredientStorageHelpers;
import org.cyclops.integratedmekanism.GeneralConfig;
import org.cyclops.integratedmekanism.core.CapabilityHelpers;
import org.cyclops.integratedmekanism.core.ChemicalHelpers;
import org.cyclops.integratedmekanism.modcompat.integratedterminals.sorter.ChemicalStackIdSorter;
import org.cyclops.integratedmekanism.modcompat.integratedterminals.sorter.ChemicalStackNameSorter;
import org.cyclops.integratedmekanism.modcompat.integratedterminals.sorter.ChemicalStackQuantitySorter;
import org.cyclops.integratedterminals.api.ingredient.IIngredientComponentTerminalStorageHandler;
import org.cyclops.integratedterminals.api.ingredient.IIngredientInstanceSorter;
import org.cyclops.integratedterminals.client.gui.container.ContainerScreenTerminalStorage;
import org.cyclops.integratedterminals.core.terminalstorage.query.SearchMode;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * @author rubensworks
 */
public class IngredientComponentTerminalStorageHandlerChemicalStack implements IIngredientComponentTerminalStorageHandler<ChemicalStack, Integer>  {

    private final IngredientComponent<ChemicalStack, Integer> ingredientComponent;

    public IngredientComponentTerminalStorageHandlerChemicalStack(IngredientComponent<ChemicalStack, Integer> ingredientComponent) {
        this.ingredientComponent = ingredientComponent;
    }

    @Override
    public IngredientComponent<ChemicalStack, Integer> getComponent() {
        return ingredientComponent;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(MekanismBlocks.BASIC_CHEMICAL_TANK);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInstance(GuiGraphics guiGraphics, ChemicalStack instance, long maxQuantity, @javax.annotation.Nullable String label, AbstractContainerScreen gui,
                             ContainerScreenTerminalStorage.DrawLayer layer, float partialTick, int x, int y,
                             int mouseX, int mouseY, @javax.annotation.Nullable List<Component> additionalTooltipLines) {
        if (instance != null) {
            if (layer == ContainerScreenTerminalStorage.DrawLayer.BACKGROUND) {
                // Draw chemical
                Chemical chemical = instance.getChemical();
                MekanismRenderer.color(guiGraphics, instance);
                GuiUtils.drawTiledSprite(guiGraphics, x, y, GuiHelpers.SLOT_SIZE_INNER, GuiHelpers.SLOT_SIZE_INNER, GuiHelpers.SLOT_SIZE_INNER, MekanismRenderer.getSprite(chemical.getIcon()), 16, 16, 100, GuiUtils.TilingDirection.UP_RIGHT);
                MekanismRenderer.resetColor(guiGraphics);

                // Draw amount
                GuiGraphicsExtended renderItem = new GuiGraphicsExtended(guiGraphics);
                renderItem.drawSlotText(Minecraft.getInstance().font, label != null ? label : GuiHelpers.quantityToScaledString(instance.getAmount()), x, y);
            } else {
                GuiHelpers.renderTooltip(gui, guiGraphics.pose(), x, y, GuiHelpers.SLOT_SIZE_INNER, GuiHelpers.SLOT_SIZE_INNER, mouseX, mouseY, () -> {
                    List<Component> lines = Lists.newArrayList();
                    lines.add(((MutableComponent) instance.getTextComponent())
                            .withStyle(Style.EMPTY.withColor(instance.getChemicalColorRepresentation())));
                    instance.appendHoverText(Item.TooltipContext.EMPTY, lines, TooltipFlag.NORMAL);
                    addQuantityTooltip(lines, instance);
                    if (additionalTooltipLines != null) {
                        lines.addAll(additionalTooltipLines);
                    }
                    return lines;
                });
            }
        }
    }

    @Override
    public String formatQuantity(ChemicalStack instance) {
        // Same as fluids
        return L10NHelpers.localize("gui.integratedterminals.terminal_storage.tooltip.fluid.amount",
                String.format(Locale.ROOT, "%,d", instance.getAmount()));
    }

    @Override
    public boolean isInstance(ItemStack itemStack) {
        return itemStack.getCapability(Capabilities.CHEMICAL.item()) != null;
    }

    @Override
    public ChemicalStack getInstance(ItemStack itemStack) {
        return CapabilityHelpers.getChemicalHandler(itemStack)
                .map(handler -> handler.getChemicalTanks() > 0 ? handler.getChemicalInTank(0) : ChemicalStack.EMPTY)
                .orElse(ChemicalStack.EMPTY);
    }

    @Override
    public long getMaxQuantity(ItemStack itemStack) {
        return CapabilityHelpers.getChemicalHandler(itemStack)
                .map(handler -> handler.getChemicalTanks() > 0 ? handler.getChemicalTankCapacity(0) : 0)
                .orElse(0L);
    }

    @Override
    public int getInitialInstanceMovementQuantity() {
        return GeneralConfig.guiStorageChemicalInitialQuantity;
    }

    @Override
    public int getIncrementalInstanceMovementQuantity() {
        return GeneralConfig.guiStorageChemicalIncrementalQuantity;
    }

    @Override
    public int throwIntoWorld(IIngredientComponentStorage<ChemicalStack, Integer> storage, ChemicalStack chemicalStack, Player player) {
        return 0; // Dropping chemicals in the world is not supported
    }

    protected IIngredientComponentStorage<ChemicalStack, Integer> getChemicalStorage(IngredientComponent<ChemicalStack, Integer> component,
                                                                                        IChemicalHandler chemicalHandler) {
        return component
                .getStorageWrapperHandler(Capabilities.CHEMICAL.item())
                .wrapComponentStorage(chemicalHandler);
    }

    @Override
    public ChemicalStack insertIntoContainer(IIngredientComponentStorage<ChemicalStack, Integer> storage, AbstractContainerMenu container, int containerSlot, ChemicalStack maxInstance, @Nullable Player player, boolean transferFullSelection) {
        ItemStack stack = container.getSlot(containerSlot).getItem();
        return CapabilityHelpers.getChemicalHandler(stack)
                .<ChemicalStack>map(chemicalHandler -> {
                    IIngredientComponentStorage<ChemicalStack, Integer> itemStorage = getChemicalStorage(storage.getComponent(), chemicalHandler);
                    ChemicalStack moved = ChemicalStack.EMPTY;
                    try {
                        moved = IngredientStorageHelpers.moveIngredientsIterative(storage, itemStorage, maxInstance,
                                ingredientComponent.getMatcher().getExactMatchNoQuantityCondition(), false);
                    } catch (InconsistentIngredientInsertionException e) {
                        // Ignore
                    }
                    container.broadcastChanges();
                    return moved;
                })
                .orElse(ChemicalStack.EMPTY);
    }

    @Override
    public void extractActiveStackFromPlayerInventory(IIngredientComponentStorage<ChemicalStack, Integer> storage, AbstractContainerMenu container, Inventory playerInventory, long moveQuantityPlayerSlot) {
        ItemStack playerStack = container.getCarried();
        CapabilityHelpers.getChemicalHandler(playerStack)
                .ifPresent(chemicalHandler -> {
                    IIngredientComponentStorage<ChemicalStack, Integer> itemStorage = getChemicalStorage(storage.getComponent(), chemicalHandler);
                    try {
                        IngredientStorageHelpers.moveIngredientsIterative(itemStorage, storage, moveQuantityPlayerSlot, false);
                    } catch (InconsistentIngredientInsertionException e) {
                        // Ignore
                    }
                });
    }

    @Override
    public void extractMaxFromContainerSlot(IIngredientComponentStorage<ChemicalStack, Integer> storage, AbstractContainerMenu container, int containerSlot, Inventory playerInventory, int limit) {
        Slot slot = container.getSlot(containerSlot);
        if (slot.mayPickup(playerInventory.player)) {
            ItemStack toMoveStack = slot.getItem();
            CapabilityHelpers.getChemicalHandler(toMoveStack)
                    .ifPresent(chemicalHandler -> {
                        IIngredientComponentStorage<ChemicalStack, Integer> itemStorage = getChemicalStorage(storage.getComponent(), chemicalHandler);
                        try {
                            IngredientStorageHelpers.moveIngredientsIterative(itemStorage, storage, limit == -1 ? Long.MAX_VALUE : limit, false);
                        } catch (InconsistentIngredientInsertionException e) {
                            // Ignore
                        }
                    });
        }
    }

    @Override
    public long getActivePlayerStackQuantity(Inventory playerInventory, AbstractContainerMenu container) {
        ItemStack toMoveStack = container.getCarried();
        return CapabilityHelpers.getChemicalHandler(toMoveStack)
                .map(chemicalHandler -> chemicalHandler.getChemicalTanks() > 0 ? chemicalHandler.getChemicalInTank(0).getAmount() : 0L)
                .orElse(0L);
    }

    @Override
    public void drainActivePlayerStackQuantity(Inventory playerInventory, AbstractContainerMenu container, long quantityIn) {
        ItemStack toMoveStack = container.getCarried();
        CapabilityHelpers.getChemicalHandler(toMoveStack)
                .ifPresent(chemicalHandler -> {
                    long quantity = quantityIn;
                    while (quantity > 0) {
                        long drained = chemicalHandler.extractChemical(quantity, Action.EXECUTE).getAmount();
                        if (drained <= 0) {
                            break;
                        }
                        quantity -= drained;
                    }
                });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Predicate<ChemicalStack> getInstanceFilterPredicate(SearchMode searchMode, String query) {
        return switch (searchMode) {
            case MOD -> i -> ChemicalHelpers.getStackRegistry().getKey(i.getChemical()).getNamespace()
                    .toLowerCase(Locale.ENGLISH).matches(".*" + query + ".*");
            case TOOLTIP -> i -> false; // Chemicals have no tooltip
            case TAG -> i -> i.getChemical().getAsHolder().tags()
                    .filter(tag -> tag.location().toString().toLowerCase(Locale.ENGLISH).matches(".*" + query + ".*"))
                    .anyMatch(tag -> !MekanismAPI.CHEMICAL_REGISTRY.getTag(tag).isEmpty());
            case DEFAULT -> i -> i != null && i.getTextComponent().getString().toLowerCase(Locale.ENGLISH).matches(".*" + query + ".*");
        };
    }

    @Override
    public Collection<IIngredientInstanceSorter<ChemicalStack>> getInstanceSorters() {
        return Lists.newArrayList(
                new ChemicalStackNameSorter(),
                new ChemicalStackIdSorter(),
                new ChemicalStackQuantitySorter()
        );
    }
}
