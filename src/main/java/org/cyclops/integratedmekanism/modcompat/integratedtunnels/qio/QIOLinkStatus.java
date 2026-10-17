package org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.cyclops.integrateddynamics.core.block.IgnoredBlockStatus;

import javax.annotation.Nullable;

/**
 * The status of the link between a QIO interface and its QIO frequency.
 * @author rubensworks
 */
public enum QIOLinkStatus {

    UNLINKED(IgnoredBlockStatus.Status.INACTIVE, "unlinked", ChatFormatting.DARK_GRAY),
    LINKED(IgnoredBlockStatus.Status.ACTIVE, "linked", ChatFormatting.DARK_GREEN),
    NOT_FOUND(IgnoredBlockStatus.Status.ERROR, "not_found", ChatFormatting.DARK_RED),
    NO_ACCESS(IgnoredBlockStatus.Status.ERROR, "no_access", ChatFormatting.DARK_RED),
    DUPLICATE(IgnoredBlockStatus.Status.ERROR, "duplicate", ChatFormatting.DARK_RED);

    private final IgnoredBlockStatus.Status blockStatus;
    private final String key;
    private final ChatFormatting color;

    QIOLinkStatus(IgnoredBlockStatus.Status blockStatus, String key, ChatFormatting color) {
        this.blockStatus = blockStatus;
        this.key = key;
        this.color = color;
    }

    public IgnoredBlockStatus.Status getBlockStatus() {
        return blockStatus;
    }

    public MutableComponent getMessage(@Nullable QIOFrequencyLink link) {
        return Component.translatable("gui.integratedmekanism.interface_qio.status." + key,
                link == null ? "" : link.getName()).withStyle(color);
    }

    public static QIOLinkStatus fromOrdinal(int ordinal) {
        QIOLinkStatus[] values = values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : UNLINKED;
    }
}
