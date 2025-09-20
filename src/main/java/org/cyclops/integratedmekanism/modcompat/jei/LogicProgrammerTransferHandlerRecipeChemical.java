package org.cyclops.integratedmekanism.modcompat.jei;

import com.google.common.collect.Lists;
import mekanism.api.chemical.ChemicalStack;
import mekanism.client.jei.MekanismJEI;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.cyclops.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import org.cyclops.integrateddynamics.core.ingredient.ItemMatchProperties;
import org.cyclops.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import org.cyclops.integrateddynamicscompat.modcompat.jei.logicprogrammer.LogicProgrammerTransferHandler;
import org.cyclops.integratedmekanism.IntegratedMekanism;
import org.cyclops.integratedmekanism.logicprogrammer.ValueTypeRecipeChemicalLPElement;
import org.cyclops.integratedmekanism.network.packet.CPacketValueTypeRecipeChemicalLPElementSetRecipe;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Allows recipe transferring to Logic Programmer elements with slots.
 * @author rubensworks
 */
public class LogicProgrammerTransferHandlerRecipeChemical<T extends ContainerLogicProgrammerBase> extends LogicProgrammerTransferHandler<T> { // TODO: in next major, plug into LogicProgrammerTransferHandler as component instead of extending.

    public LogicProgrammerTransferHandlerRecipeChemical(Class<T> clazz) {
        super(clazz);
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(T container, Object recipe, IRecipeSlotsView recipeLayout,
                                               Player player, boolean maxTransfer, boolean doTransfer) {
        ILogicProgrammerElement element = container.getActiveElement();

        if (element instanceof ValueTypeRecipeChemicalLPElement) {
            return handleRecipeChemicalElement((ValueTypeRecipeChemicalLPElement) element, container, recipeLayout, doTransfer);
        }

        return super.transferRecipe(container, recipe, recipeLayout, player, maxTransfer, doTransfer);
    }

    protected IRecipeTransferError handleRecipeChemicalElement(ValueTypeRecipeChemicalLPElement element, T container, IRecipeSlotsView recipeLayout, boolean doTransfer) {
        List<ItemMatchProperties> itemInputs = Lists.newArrayList();
        List<FluidStack> fluidInputs = Lists.newArrayList();
        List<ChemicalStack<?>> chemicalInputs = Lists.newArrayList();
        List<ItemStack> itemOutputs = Lists.newArrayList();
        List<FluidStack> fluidOutputs = Lists.newArrayList();
        List<ChemicalStack<?>> chemicalOutputs = Lists.newArrayList();

        for (IRecipeSlotView slotView : recipeLayout.getSlotViews()) {
            if (slotView.isEmpty()) {
                // We assume only item slots can be empty
                itemInputs.add(new ItemMatchProperties(ItemStack.EMPTY));
            } else {
                ITypedIngredient<?> typedIngredient = slotView.getAllIngredients().findFirst().get();
                if (typedIngredient.getType() == VanillaTypes.ITEM_STACK) {
                    // Collect items
                    if (slotView.getRole() == RecipeIngredientRole.INPUT) {
                        ResourceLocation heuristicTag = getHeuristicItemsTag(slotView);
                        if (heuristicTag != null) {
                            itemInputs.add(new ItemMatchProperties(ItemStack.EMPTY, false, heuristicTag.toString(), ((ItemStack) typedIngredient.getIngredient()).getCount()));
                        } else {
                            itemInputs.add(new ItemMatchProperties(((ItemStack) typedIngredient.getIngredient()).copy()));
                        }
                    } else if (slotView.getRole() == RecipeIngredientRole.OUTPUT) {
                        itemOutputs.add(((ItemStack) typedIngredient.getIngredient()).copy());
                    }
                } else if (typedIngredient.getType() == ForgeTypes.FLUID_STACK) {
                    // Collect fluids
                    if (slotView.getRole() == RecipeIngredientRole.INPUT) {
                        fluidInputs.add(((FluidStack) typedIngredient.getIngredient()).copy());
                    } else if (slotView.getRole() == RecipeIngredientRole.OUTPUT) {
                        fluidOutputs.add(((FluidStack) typedIngredient.getIngredient()).copy());
                    }
                } else if (typedIngredient.getType() == MekanismJEI.TYPE_GAS || typedIngredient.getType() == MekanismJEI.TYPE_INFUSION || typedIngredient.getType() == MekanismJEI.TYPE_PIGMENT || typedIngredient.getType() == MekanismJEI.TYPE_SLURRY) {
                    // Collect fluids
                    if (slotView.getRole() == RecipeIngredientRole.INPUT) {
                        chemicalInputs.add(((ChemicalStack<?>) typedIngredient.getIngredient()).copy());
                    } else if (slotView.getRole() == RecipeIngredientRole.OUTPUT) {
                        chemicalOutputs.add(((ChemicalStack<?>) typedIngredient.getIngredient()).copy());
                    }
                }
            }
        }

        if (!element.isValidForRecipeGrid(itemInputs, fluidInputs, chemicalInputs, itemOutputs, fluidOutputs, chemicalOutputs)) {
            return new IRecipeTransferError() {
                @Override
                public Type getType() {
                    return Type.USER_FACING;
                }

                @Override
                public void showError(GuiGraphics guiGraphics, int mouseX, int mouseY, IRecipeSlotsView recipeLayout, int recipeX, int recipeY) {
                    guiGraphics.renderComponentTooltip(
                            Minecraft.getInstance().font,
                            Collections.singletonList(Component.translatable("error.jei.integrateddynamics.recipetransfer.recipe.toobig.desc")),
                            mouseX, mouseY);
                }
            };
        }

        if (doTransfer) {
            element.setRecipeGrid(container, itemInputs, fluidInputs, chemicalInputs, itemOutputs, fluidOutputs, chemicalOutputs);
            IntegratedMekanism._instance.getPacketHandler().sendToServer(
                    new CPacketValueTypeRecipeChemicalLPElementSetRecipe(container.containerId, itemInputs, fluidInputs, chemicalInputs, itemOutputs, fluidOutputs, chemicalOutputs));
        }

        return null;
    }

}
