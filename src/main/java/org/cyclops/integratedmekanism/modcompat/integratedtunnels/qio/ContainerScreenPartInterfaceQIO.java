package org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio;

import com.google.common.collect.Lists;
import mekanism.api.security.SecurityMode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import org.cyclops.cyclopscore.client.gui.component.WidgetScrollBar;
import org.cyclops.cyclopscore.client.gui.component.button.ButtonText;
import org.cyclops.cyclopscore.client.gui.component.input.WidgetNumberField;
import org.cyclops.cyclopscore.client.gui.component.input.WidgetTextFieldExtended;
import org.cyclops.cyclopscore.client.gui.container.ContainerScreenExtended;
import org.cyclops.cyclopscore.helper.ValueNotifierHelpers;
import org.cyclops.integratedmekanism.Reference;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Gui for selecting the QIO frequency of a QIO interface.
 * @author rubensworks
 */
public class ContainerScreenPartInterfaceQIO extends ContainerScreenExtended<ContainerPartInterfaceQIO> {

    private static final int LIST_X = 8;
    private static final int LIST_Y = 48;
    private static final int LIST_WIDTH = 184;
    private static final int ROW_HEIGHT = 12;
    private static final int VISIBLE_ROWS = 9;
    private static final int DETAILS_Y = 162;
    private static final int FIELDS_Y = 186;
    private static final int COLOR_TEXT = 0x404040;
    private static final int COLOR_ROW_TEXT = 0xE0E0E0;
    private static final int COLOR_ROW_HOVER = 0x30FFFFFF;
    private static final int COLOR_ROW_LINKED = 0x6036D6BE;
    private static final int COLOR_ROW_LINKED_BORDER = 0xFF4FFFE1;
    private static final int COLOR_ROW_ERROR = 0x60C03D37;
    private static final int COLOR_ROW_ERROR_BORDER = 0xFFE05A50;
    private static final int STATUS_Y = 21;

    private final List<FrequencyEntry> entries = Lists.newArrayList();
    private final List<FrequencyEntry> filteredEntries = Lists.newArrayList();
    @Nullable
    private QIOFrequencyLink link = null;
    private MutableComponent statusMessage = Component.empty();
    private QIOLinkStatus status = QIOLinkStatus.UNLINKED;
    private int firstRow = 0;

    private WidgetTextFieldExtended fieldSearch;
    private WidgetScrollBar scrollBar;
    private WidgetNumberField fieldPriority;
    private WidgetNumberField fieldChannel;
    private ButtonText buttonUnlink;

    public ContainerScreenPartInterfaceQIO(ContainerPartInterfaceQIO container, Inventory inventory, Component title) {
        super(container, inventory, title);
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/part_interface_qio.png");
    }

    @Override
    protected int getBaseXSize() {
        return 216;
    }

    @Override
    protected int getBaseYSize() {
        return 208;
    }

    @Override
    public void init() {
        super.init();

        fieldSearch = new WidgetTextFieldExtended(font, leftPos + LIST_X + 1, topPos + 33, LIST_WIDTH + 14, 11,
                Component.translatable("gui.cyclopscore.search"), true);
        fieldSearch.setMaxLength(64);
        fieldSearch.setTextColor(0xFFFFFF);
        fieldSearch.setHint(Component.translatable("gui.integratedmekanism.interface_qio.search").withStyle(ChatFormatting.DARK_GRAY));
        fieldSearch.setResponder(value -> updateFilter());
        addRenderableWidget(fieldSearch);

        scrollBar = new WidgetScrollBar(leftPos + LIST_X + LIST_WIDTH + 2, topPos + LIST_Y, ROW_HEIGHT * VISIBLE_ROWS,
                Component.translatable("gui.cyclopscore.scrollbar"), firstRow -> this.firstRow = firstRow, VISIBLE_ROWS);
        addRenderableWidget(scrollBar);

        fieldPriority = createNumberField(leftPos + 54, Component.translatable("gui.integrateddynamics.partsettings.priority"));
        fieldChannel = createNumberField(leftPos + 152, Component.translatable("gui.integratedtunnels.partsettings.channel.interface"));

        MutableComponent unlink = Component.translatable("gui.integratedmekanism.interface_qio.unlink");
        int unlinkWidth = font.width(unlink) + 8;
        buttonUnlink = new ButtonText(leftPos + getBaseXSize() - 8 - unlinkWidth, topPos + 5, unlinkWidth, 14, unlink, unlink,
                createServerPressable(ContainerPartInterfaceQIO.BUTTON_UNLINK, null), true);
        addRenderableWidget(buttonUnlink);

        refreshValues();
        updateFilter();
    }

    protected WidgetNumberField createNumberField(int x, Component name) {
        WidgetNumberField field = new WidgetNumberField(font, x, topPos + FIELDS_Y, 46, 14, true, name, true);
        field.setPositiveOnly(false);
        field.setMaxLength(9);
        field.setTextColor(0xFFFFFF);
        field.setCanLoseFocus(true);
        addRenderableWidget(field);
        return field;
    }

    @Override
    public void onUpdate(int valueId, CompoundTag value) {
        super.onUpdate(valueId, value);
        ContainerPartInterfaceQIO menu = getMenu();
        if (valueId == menu.getFrequenciesValueId()) {
            entries.clear();
            Tag listTag = ValueNotifierHelpers.getValueNbt(menu, valueId);
            if (listTag instanceof CompoundTag compoundTag) {
                for (Tag entryTag : compoundTag.getList("list", Tag.TAG_COMPOUND)) {
                    entries.add(FrequencyEntry.fromNbt((CompoundTag) entryTag));
                }
            }
            if (fieldSearch != null) {
                updateFilter();
            }
        } else if (valueId == menu.getLinkValueId()) {
            link = QIOFrequencyLink.fromNbt(ValueNotifierHelpers.getValueNbt(menu, valueId));
        } else if (valueId == menu.getStatusValueId()) {
            status = QIOLinkStatus.fromOrdinal(ValueNotifierHelpers.getValueInt(menu, valueId));
        } else if (valueId == menu.getStatusMessageValueId()) {
            statusMessage = ValueNotifierHelpers.getValueTextComponent(menu, valueId);
        } else if (valueId == menu.getPriorityValueId() && fieldPriority != null && !fieldPriority.isFocused()) {
            fieldPriority.setValue(Integer.toString(ValueNotifierHelpers.getValueInt(menu, valueId)));
        } else if (valueId == menu.getChannelValueId() && fieldChannel != null && !fieldChannel.isFocused()) {
            fieldChannel.setValue(Integer.toString(ValueNotifierHelpers.getValueInt(menu, valueId)));
        }
        if (buttonUnlink != null) {
            buttonUnlink.active = link != null;
        }
    }

    protected void updateFilter() {
        String query = fieldSearch.getValue().toLowerCase(Locale.ROOT);
        filteredEntries.clear();
        for (FrequencyEntry entry : entries) {
            if (query.isEmpty() || entry.name().toLowerCase(Locale.ROOT).contains(query)
                    || entry.ownerName().toLowerCase(Locale.ROOT).contains(query)) {
                filteredEntries.add(entry);
            }
        }
        scrollBar.setTotalRows(filteredEntries.size());
        scrollBar.scrollTo(0);
    }

    protected boolean isLinked(FrequencyEntry entry) {
        return link != null && entry.name().equals(link.getName())
                && entry.security() == link.identity().securityMode()
                && Objects.equals(entry.owner(), link.identity().ownerUUID());
    }

    @Nullable
    protected FrequencyEntry getEntryAt(double mouseX, double mouseY) {
        int x = (int) mouseX - leftPos - LIST_X;
        int y = (int) mouseY - topPos - LIST_Y;
        if (x < 0 || x >= LIST_WIDTH || y < 0 || y >= ROW_HEIGHT * VISIBLE_ROWS) {
            return null;
        }
        int index = firstRow + y / ROW_HEIGHT;
        return index < filteredEntries.size() ? filteredEntries.get(index) : null;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);

        // Title and status
        guiGraphics.drawString(font, title, leftPos + 8, topPos + 7, COLOR_TEXT, false);
        int statusWidth = getBaseXSize() - 16;
        if (font.width(statusMessage) > statusWidth) {
            String truncated = font.plainSubstrByWidth(statusMessage.getString(), statusWidth - font.width("...")) + "...";
            guiGraphics.drawString(font, Component.literal(truncated).withStyle(statusMessage.getStyle()), leftPos + 8, topPos + STATUS_Y, 0xFFFFFF, false);
        } else {
            guiGraphics.drawString(font, statusMessage, leftPos + 8, topPos + STATUS_Y, 0xFFFFFF, false);
        }

        // Frequency list
        FrequencyEntry hovered = getEntryAt(mouseX, mouseY);
        int listX = leftPos + LIST_X;
        for (int row = 0; row < VISIBLE_ROWS; row++) {
            int index = firstRow + row;
            if (index >= filteredEntries.size()) {
                break;
            }
            FrequencyEntry entry = filteredEntries.get(index);
            int y = topPos + LIST_Y + row * ROW_HEIGHT;
            if (isLinked(entry)) {
                boolean active = status == QIOLinkStatus.LINKED;
                guiGraphics.fill(listX, y, listX + LIST_WIDTH, y + ROW_HEIGHT, active ? COLOR_ROW_LINKED : COLOR_ROW_ERROR);
                guiGraphics.fill(listX, y, listX + 1, y + ROW_HEIGHT, active ? COLOR_ROW_LINKED_BORDER : COLOR_ROW_ERROR_BORDER);
            } else if (entry == hovered) {
                guiGraphics.fill(listX, y, listX + LIST_WIDTH, y + ROW_HEIGHT, COLOR_ROW_HOVER);
            }
            // Frequency color swatch
            guiGraphics.fill(listX + 3, y + 2, listX + 9, y + ROW_HEIGHT - 2, 0xFF000000);
            guiGraphics.fill(listX + 4, y + 3, listX + 8, y + ROW_HEIGHT - 3, 0xFF000000 | entry.color());

            MutableComponent security = getSecurityComponent(entry.security());
            int securityWidth = font.width(security);
            String name = font.plainSubstrByWidth(entry.name(), LIST_WIDTH - 18 - securityWidth);
            guiGraphics.drawString(font, name, listX + 12, y + 2, COLOR_ROW_TEXT, false);
            guiGraphics.drawString(font, security, listX + LIST_WIDTH - 3 - securityWidth, y + 2, 0xFFFFFF, false);
        }
        if (filteredEntries.isEmpty()) {
            Component empty = Component.translatable(entries.isEmpty()
                    ? "gui.integratedmekanism.interface_qio.none" : "gui.integratedmekanism.interface_qio.no_matches");
            guiGraphics.drawCenteredString(font, empty, listX + LIST_WIDTH / 2, topPos + LIST_Y + 4, 0xA0A0A0);
        }

        // Details of the hovered or linked frequency
        FrequencyEntry details = hovered != null ? hovered : filteredEntries.stream().filter(this::isLinked).findFirst()
                .orElseGet(() -> entries.stream().filter(this::isLinked).findFirst().orElse(null));
        if (details != null) {
            guiGraphics.drawString(font, Component.translatable("gui.integratedmekanism.interface_qio.owner", details.ownerName()),
                    leftPos + 8, topPos + DETAILS_Y, COLOR_TEXT, false);
            guiGraphics.drawString(font, Component.translatable("gui.integratedmekanism.interface_qio.contents",
                            String.format(Locale.ROOT, "%,d", details.items()), String.format(Locale.ROOT, "%,d", details.itemsMax()),
                            String.format(Locale.ROOT, "%,d", details.types()), String.format(Locale.ROOT, "%,d", details.typesMax())),
                    leftPos + 8, topPos + DETAILS_Y + 10, COLOR_TEXT, false);
        }

        // Settings
        guiGraphics.drawString(font, Component.translatable("gui.integratedmekanism.interface_qio.priority"),
                leftPos + 8, topPos + FIELDS_Y + 3, COLOR_TEXT, false);
        guiGraphics.drawString(font, Component.translatable("gui.integratedmekanism.interface_qio.channel"),
                leftPos + 108, topPos + FIELDS_Y + 3, COLOR_TEXT, false);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Labels are drawn in the background, so skip the default title rendering
        if (isHovering(8, STATUS_Y - 1, getBaseXSize() - 16, 10, mouseX, mouseY) && font.width(statusMessage) > getBaseXSize() - 16) {
            guiGraphics.renderTooltip(font, font.split(statusMessage, 200), mouseX - leftPos, mouseY - topPos);
        }
        FrequencyEntry hovered = getEntryAt(mouseX, mouseY);
        if (hovered != null) {
            List<Component> lines = Lists.newArrayList();
            lines.add(Component.literal(hovered.name()).withStyle(ChatFormatting.WHITE));
            lines.add(Component.translatable("gui.integratedmekanism.interface_qio.owner", hovered.ownerName()).withStyle(ChatFormatting.GRAY));
            lines.add(getSecurityComponent(hovered.security()));
            lines.add(Component.translatable(isLinked(hovered)
                    ? "gui.integratedmekanism.interface_qio.relink" : "gui.integratedmekanism.interface_qio.link")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            guiGraphics.renderComponentTooltip(font, lines, mouseX - leftPos, mouseY - topPos);
        }
    }

    protected MutableComponent getSecurityComponent(SecurityMode securityMode) {
        return securityMode.getTextComponent().copy().withStyle(switch (securityMode) {
            case PUBLIC -> ChatFormatting.GREEN;
            case PRIVATE -> ChatFormatting.RED;
            case TRUSTED -> ChatFormatting.AQUA;
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        FrequencyEntry entry = getEntryAt(mouseX, mouseY);
        if (entry != null && mouseButton == 0) {
            CompoundTag selection = new CompoundTag();
            selection.putString("name", entry.name());
            selection.putInt("security", entry.security().ordinal());
            selection.putUUID("owner", entry.owner());
            ValueNotifierHelpers.setValue(getMenu(), getMenu().getSelectValueId(), selection);
            playClickSound();
            return true;
        }
        boolean wasPriorityFocused = fieldPriority.isFocused();
        boolean wasChannelFocused = fieldChannel.isFocused();
        boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);
        if ((wasPriorityFocused && !fieldPriority.isFocused()) || (wasChannelFocused && !fieldChannel.isFocused())) {
            saveSettings();
        }
        return result;
    }

    protected void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int x = (int) mouseX - leftPos - LIST_X;
        int y = (int) mouseY - topPos - LIST_Y;
        if (x >= 0 && x < LIST_WIDTH + 14 && y >= 0 && y < ROW_HEIGHT * VISIBLE_ROWS) {
            return scrollBar.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        EditBox focusedField = getFocusedField();
        if (focusedField != null && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                saveSettings();
                focusedField.setFocused(false);
                setFocused(null);
                return true;
            }
            // Don't let typing in a field trigger key bindings, such as closing the gui
            focusedField.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Nullable
    protected EditBox getFocusedField() {
        for (EditBox field : new EditBox[]{fieldSearch, fieldPriority, fieldChannel}) {
            if (field.isFocused()) {
                return field;
            }
        }
        return null;
    }

    protected void saveSettings() {
        try {
            ValueNotifierHelpers.setValue(getMenu(), getMenu().getPriorityValueId(), fieldPriority.getInt());
        } catch (NumberFormatException e) {
        }
        try {
            ValueNotifierHelpers.setValue(getMenu(), getMenu().getChannelValueId(), fieldChannel.getInt());
        } catch (NumberFormatException e) {
        }
    }

    @Override
    public void onClose() {
        saveSettings();
        super.onClose();
    }

    public static record FrequencyEntry(String name, SecurityMode security, UUID owner, String ownerName, int color,
                                        long items, long itemsMax, int types, int typesMax) {
        public static FrequencyEntry fromNbt(CompoundTag tag) {
            return new FrequencyEntry(
                    tag.getString("name"),
                    SecurityMode.BY_ID.apply(tag.getInt("security")),
                    tag.getUUID("owner"),
                    tag.getString("ownerName"),
                    tag.getInt("color"),
                    tag.getLong("items"),
                    tag.getLong("itemsMax"),
                    tag.getInt("types"),
                    tag.getInt("typesMax")
            );
        }
    }
}
