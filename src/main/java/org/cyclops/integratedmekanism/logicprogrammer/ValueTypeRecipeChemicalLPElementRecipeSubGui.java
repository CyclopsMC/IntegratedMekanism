package org.cyclops.integratedmekanism.logicprogrammer;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.cyclops.cyclopscore.client.gui.component.input.WidgetTextFieldExtended;
import org.cyclops.integrateddynamics.client.gui.container.ContainerScreenLogicProgrammerBase;
import org.cyclops.integrateddynamics.core.client.gui.subgui.SubGuiBox;
import org.cyclops.integrateddynamics.core.logicprogrammer.IRenderPatternValueTypeTooltip;
import org.cyclops.integrateddynamics.core.logicprogrammer.RenderPattern;
import org.cyclops.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import org.cyclops.integratedmekanism.IntegratedMekanism;
import org.cyclops.integratedmekanism.network.packet.LogicProgrammerValueTypeRecipeChemicalValueChangedPacket;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @author rubensworks
 */
@OnlyIn(Dist.CLIENT)
class ValueTypeRecipeChemicalLPElementRecipeSubGui extends RenderPattern<ValueTypeRecipeChemicalLPElement, ContainerScreenLogicProgrammerBase, ContainerLogicProgrammerBase>
        implements IRenderPatternValueTypeTooltip {

    private boolean renderTooltip = true;
    private List<WidgetTextFieldExtended> inputFluidAmounts = null;
    private List<WidgetTextFieldExtended> inputChemicalAmounts = null;
    private List<WidgetTextFieldExtended> outputFluidAmounts = null;
    private List<WidgetTextFieldExtended> outputChemicalAmounts = null;

    public ValueTypeRecipeChemicalLPElementRecipeSubGui(ValueTypeRecipeChemicalLPElement element, int baseX, int baseY, int maxWidth, int maxHeight,
                                                        ContainerScreenLogicProgrammerBase gui, ContainerLogicProgrammerBase container) {
        super(element, baseX, baseY, maxWidth, maxHeight, gui, container);
    }

    public List<WidgetTextFieldExtended> getInputFluidAmounts() {
        return inputFluidAmounts;
    }

    public List<WidgetTextFieldExtended> getInputChemicalAmounts() {
        return inputChemicalAmounts;
    }

    public List<WidgetTextFieldExtended> getOutputFluidAmounts() {
        return outputFluidAmounts;
    }

    public List<WidgetTextFieldExtended> getOutputChemicalAmounts() {
        return outputChemicalAmounts;
    }

    @Override
    public void setRenderTooltip(boolean renderTooltip) {
        this.renderTooltip = renderTooltip;
    }

    @Override
    public boolean isRenderTooltip() {
        return renderTooltip;
    }

    protected static WidgetTextFieldExtended makeTextBox(int componentId, int x, int y, String text) {
        Font fontRenderer = Minecraft.getInstance().font;
        int searchWidth = 32;

        WidgetTextFieldExtended box = new WidgetTextFieldExtended(fontRenderer, x, y,
                searchWidth, fontRenderer.lineHeight + 3, Component.translatable("gui.cyclopscore.search"), true);
        box.setMaxLength(10);
        box.setBordered(false);
        box.setVisible(true);
        box.setTextColor(16777215);
        box.setCanLoseFocus(true);
        box.setValue(text);
        box.setWidth(searchWidth);
        return box;
    }

    @Override
    public void init(int guiLeft, int guiTop) {
        super.init(guiLeft, guiTop);

        int slotSpacing = 20;
        this.inputFluidAmounts = IntStream
                .range(0, ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE)
                .mapToObj(i -> makeTextBox(i, guiLeft + getX() + 39, guiTop + getY() + 19 + slotSpacing * i, element.getInputFluids().get(i).getRight()))
                .toList();
        this.inputChemicalAmounts = IntStream
                .range(0, ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE)
                .mapToObj(i -> makeTextBox(ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE + i, guiLeft + getX() + 39, guiTop + getY() + 55 + slotSpacing * i, element.getInputChemicals().get(i).getRight()))
                .toList();
        this.outputFluidAmounts = IntStream
                .range(0, ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE)
                .mapToObj(i -> makeTextBox(ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE * 2 + i, guiLeft + getX() + 117, guiTop + getY() + 19 + slotSpacing * i, element.getOutputFluids().get(i).getRight()))
                .toList();
        this.outputChemicalAmounts = IntStream
                .range(0, ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE)
                .mapToObj(i -> makeTextBox(ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE * 3 + i, guiLeft + getX() + 117, guiTop + getY() + 55 + slotSpacing * i, element.getOutputChemicals().get(i).getRight()))
                .toList();
    }

    @Override
    public void drawGuiContainerForegroundLayer(GuiGraphics guiGraphics, int guiLeft, int guiTop, TextureManager textureManager, Font fontRenderer, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(guiGraphics, guiLeft, guiTop, textureManager, fontRenderer, mouseX, mouseY);

        // Draw type tooltips
        if (gui.isHovering(getX() + 0, getY() + 0, 22, 18, mouseX, mouseY)) {
            gui.drawTooltip(Lists.newArrayList(Component.translatable("gui.integratedmekanism.logicprogrammer.element.recipechemical.tooltip.item")), guiGraphics.pose(), mouseX - guiLeft, mouseY - guiTop);
        }
        if (gui.isHovering(getX() + 0, getY() +18, 22, 36, mouseX, mouseY)) {
            gui.drawTooltip(Lists.newArrayList(Component.translatable("gui.integratedmekanism.logicprogrammer.element.recipechemical.tooltip.fluid")), guiGraphics.pose(), mouseX - guiLeft, mouseY - guiTop);
        }
        if (gui.isHovering(getX() + 0, getY() + 54, 22, 36, mouseX, mouseY)) {
            gui.drawTooltip(Lists.newArrayList(Component.translatable("gui.integratedmekanism.logicprogrammer.element.recipechemical.tooltip.chemical")), guiGraphics.pose(), mouseX - guiLeft, mouseY - guiTop);
        }

        // Output type tooltip
        this.drawTooltipForeground(gui, guiGraphics, container, guiLeft, guiTop, mouseX, mouseY, element.getValueType());

        // Render the info tooltip when hovering the input item slots
        for (int slotId = 0; slotId < this.container.slots.size(); ++slotId) {
            Slot slot = this.container.slots.get(slotId);
            if (slotId >= ValueTypeRecipeChemicalLPElement.SLOT_OFFSET && slotId < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE + ValueTypeRecipeChemicalLPElement.SLOT_OFFSET) {
                int slotX = slot.x;
                int slotY = slot.y;

                // Draw tooltips
                if (gui.isHovering(slotX, slotY, 16, 16, mouseX, mouseY)) {
                    gui.drawTooltip(Lists.newArrayList(
                            Component.translatable("valuetype.integrateddynamics.ingredients.slot.info")
                                    .withStyle(ChatFormatting.ITALIC)
                    ), guiGraphics.pose(), mouseX - guiLeft, mouseY - guiTop - (slot.getItem().isEmpty() ? 0 : 15));
                }
            }
        }
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, int guiLeft, int guiTop, TextureManager textureManager, Font fontRenderer, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, guiLeft, guiTop, textureManager, fontRenderer, partialTicks, mouseX, mouseY);

        // Draw slot types
        guiGraphics.renderItem(new ItemStack(Items.CHEST), guiLeft + getX() + 2, guiTop + getY());
        guiGraphics.renderItem(new ItemStack(Items.BUCKET), guiLeft + getX() + 2, guiTop + getY() + 18);
        guiGraphics.renderItem(new ItemStack(Items.BUCKET), guiLeft + getX() + 2, guiTop + getY() + 36);
        guiGraphics.renderItem(new ItemStack(MekanismBlocks.BASIC_CHEMICAL_TANK), guiLeft + getX() + 2, guiTop + getY() + 54);
        guiGraphics.renderItem(new ItemStack(MekanismBlocks.BASIC_CHEMICAL_TANK), guiLeft + getX() + 2, guiTop + getY() + 72);

        // Draw crafting arrow
        guiGraphics.blit(SubGuiBox.TEXTURE, guiLeft + getX() + 74, guiTop + getY() + 38, 0, 38, 22, 15);

        this.inputFluidAmounts.forEach(widget -> widget.render(guiGraphics, mouseX, mouseY, partialTicks));
        this.inputChemicalAmounts.forEach(widget -> widget.render(guiGraphics, mouseX, mouseY, partialTicks));
        this.outputFluidAmounts.forEach(widget -> widget.render(guiGraphics, mouseX, mouseY, partialTicks));
        this.outputChemicalAmounts.forEach(widget -> widget.render(guiGraphics, mouseX, mouseY, partialTicks));
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        for (int i = 0; i < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE; i++) {
            if (this.inputFluidAmounts.get(i).charTyped(typedChar, keyCode)) {
                String amount = this.inputFluidAmounts.get(i).getValue();
                element.getInputFluids().set(i, Pair.of(element.getInputFluids().get(i).getLeft(), amount));
                container.onDirty();
                IntegratedMekanism._instance.getPacketHandler().sendToServer(
                        new LogicProgrammerValueTypeRecipeChemicalValueChangedPacket(amount,
                                LogicProgrammerValueTypeRecipeChemicalValueChangedPacket.Type.INPUT_FLUID,
                                i));
                return true;
            }
            if (this.inputChemicalAmounts.get(i).charTyped(typedChar, keyCode)) {
                String amount = this.inputChemicalAmounts.get(i).getValue();
                element.getInputChemicals().set(i, Pair.of(element.getInputChemicals().get(i).getLeft(), amount));
                container.onDirty();
                IntegratedMekanism._instance.getPacketHandler().sendToServer(
                        new LogicProgrammerValueTypeRecipeChemicalValueChangedPacket(amount,
                                LogicProgrammerValueTypeRecipeChemicalValueChangedPacket.Type.INPUT_CHEMICAL,
                                i));
                return true;
            }
            if (this.outputFluidAmounts.get(i).charTyped(typedChar, keyCode)) {
                String amount = this.outputFluidAmounts.get(i).getValue();
                element.getInputFluids().set(i, Pair.of(element.getInputFluids().get(i).getLeft(), amount));
                container.onDirty();
                IntegratedMekanism._instance.getPacketHandler().sendToServer(
                        new LogicProgrammerValueTypeRecipeChemicalValueChangedPacket(amount,
                                LogicProgrammerValueTypeRecipeChemicalValueChangedPacket.Type.OUTPUT_FLUID,
                                i));
                return true;
            }
            if (this.outputChemicalAmounts.get(i).charTyped(typedChar, keyCode)) {
                String amount = this.outputChemicalAmounts.get(i).getValue();
                element.getInputChemicals().set(i, Pair.of(element.getInputChemicals().get(i).getLeft(), amount));
                container.onDirty();
                IntegratedMekanism._instance.getPacketHandler().sendToServer(
                        new LogicProgrammerValueTypeRecipeChemicalValueChangedPacket(amount,
                                LogicProgrammerValueTypeRecipeChemicalValueChangedPacket.Type.OUTPUT_CHEMICAL,
                                i));
                return true;
            }
        }
        return super.charTyped(typedChar, keyCode);
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int modifiers) {
        for (int i = 0; i < ValueTypeRecipeChemicalLPElement.SLOTS_PER_TYPE; i++) {
            if (this.inputFluidAmounts.get(i).keyPressed(typedChar, keyCode, modifiers)) {
                String amount = this.inputFluidAmounts.get(i).getValue();
                element.getInputFluids().set(i, Pair.of(element.getInputFluids().get(i).getLeft(), amount));
                container.onDirty();
                IntegratedMekanism._instance.getPacketHandler().sendToServer(
                        new LogicProgrammerValueTypeRecipeChemicalValueChangedPacket(amount,
                                LogicProgrammerValueTypeRecipeChemicalValueChangedPacket.Type.INPUT_FLUID,
                                i));
                return true;
            }
            if (this.inputChemicalAmounts.get(i).keyPressed(typedChar, keyCode, modifiers)) {
                String amount = this.inputChemicalAmounts.get(i).getValue();
                element.getInputChemicals().set(i, Pair.of(element.getInputChemicals().get(i).getLeft(), amount));
                container.onDirty();
                IntegratedMekanism._instance.getPacketHandler().sendToServer(
                        new LogicProgrammerValueTypeRecipeChemicalValueChangedPacket(amount,
                                LogicProgrammerValueTypeRecipeChemicalValueChangedPacket.Type.INPUT_CHEMICAL,
                                i));
                return true;
            }
            if (this.outputFluidAmounts.get(i).keyPressed(typedChar, keyCode, modifiers)) {
                String amount = this.outputFluidAmounts.get(i).getValue();
                element.getInputFluids().set(i, Pair.of(element.getInputFluids().get(i).getLeft(), amount));
                container.onDirty();
                IntegratedMekanism._instance.getPacketHandler().sendToServer(
                        new LogicProgrammerValueTypeRecipeChemicalValueChangedPacket(amount,
                                LogicProgrammerValueTypeRecipeChemicalValueChangedPacket.Type.OUTPUT_FLUID,
                                i));
                return true;
            }
            if (this.outputChemicalAmounts.get(i).keyPressed(typedChar, keyCode, modifiers)) {
                String amount = this.outputChemicalAmounts.get(i).getValue();
                element.getInputChemicals().set(i, Pair.of(element.getInputChemicals().get(i).getLeft(), amount));
                container.onDirty();
                IntegratedMekanism._instance.getPacketHandler().sendToServer(
                        new LogicProgrammerValueTypeRecipeChemicalValueChangedPacket(amount,
                                LogicProgrammerValueTypeRecipeChemicalValueChangedPacket.Type.OUTPUT_CHEMICAL,
                                i));
                return true;
            }
        }
        return super.keyPressed(typedChar, keyCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return Streams.concat(
                        this.inputFluidAmounts.stream(),
                        this.inputChemicalAmounts.stream(),
                        this.outputFluidAmounts.stream(),
                        this.outputChemicalAmounts.stream()
                )
                .anyMatch(widget -> widget.mouseClicked(mouseX, mouseY, mouseButton))
                || super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
