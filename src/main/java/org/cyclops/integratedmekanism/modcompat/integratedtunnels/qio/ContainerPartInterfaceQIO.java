package org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio;

import com.google.common.collect.Lists;
import mekanism.api.security.SecurityMode;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.lib.frequency.Frequency;
import mekanism.common.lib.frequency.FrequencyManager;
import mekanism.common.lib.frequency.FrequencyManagerWrapper;
import mekanism.common.lib.frequency.FrequencyType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.cyclops.cyclopscore.helper.ValueNotifierHelpers;
import org.cyclops.cyclopscore.inventory.container.ContainerExtended;
import org.cyclops.integrateddynamics.api.part.IPartContainer;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integrateddynamics.core.network.PartNetworkElement;
import org.cyclops.integratedmekanism.RegistryEntries;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.part.PartTypeInterfaceQIO;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Container for selecting the QIO frequency of a QIO interface.
 * @author rubensworks
 */
public class ContainerPartInterfaceQIO extends ContainerExtended {

    public static final String BUTTON_UNLINK = "button_unlink";
    private static final int REFRESH_INTERVAL = 20;

    private final PartTarget target;
    private final Optional<IPartContainer> partContainer;
    private final PartTypeInterfaceQIO partType;
    private final Level world;

    private final int frequenciesValueId;
    private final int linkValueId;
    private final int statusValueId;
    private final int statusMessageValueId;
    private final int priorityValueId;
    private final int channelValueId;
    private final int selectValueId;

    private int ticks = 0;

    public ContainerPartInterfaceQIO(int id, Inventory playerInventory, RegistryFriendlyByteBuf packetBuffer) {
        this(id, playerInventory, PartHelpers.readPartTarget(packetBuffer), Optional.empty(), PartHelpers.readPart(packetBuffer));
    }

    public ContainerPartInterfaceQIO(int id, Inventory playerInventory, PartTarget target,
                                     Optional<IPartContainer> partContainer, PartTypeInterfaceQIO partType) {
        super(RegistryEntries.CONTAINER_PART_INTERFACE_QIO.get(), id, playerInventory);
        this.target = target;
        this.partContainer = partContainer;
        this.partType = partType;
        this.world = player.getCommandSenderWorld();

        this.frequenciesValueId = getNextValueId();
        this.linkValueId = getNextValueId();
        this.statusValueId = getNextValueId();
        this.statusMessageValueId = getNextValueId();
        this.priorityValueId = getNextValueId();
        this.channelValueId = getNextValueId();
        this.selectValueId = getNextValueId();

        putButtonAction(BUTTON_UNLINK, (s, containerExtended) -> {
            if (!world.isClientSide()) {
                partType.setLink(target, getPartState(), null);
                refreshLinkValues();
            }
        });
    }

    public PartTarget getTarget() {
        return target;
    }

    public PartTypeInterfaceQIO getPartType() {
        return partType;
    }

    public PartTypeInterfaceQIO.State getPartState() {
        return (PartTypeInterfaceQIO.State) partContainer.get().getPartState(target.getCenter().getSide());
    }

    public int getFrequenciesValueId() {
        return frequenciesValueId;
    }

    public int getLinkValueId() {
        return linkValueId;
    }

    public int getStatusValueId() {
        return statusValueId;
    }

    public int getStatusMessageValueId() {
        return statusMessageValueId;
    }

    public int getPriorityValueId() {
        return priorityValueId;
    }

    public int getChannelValueId() {
        return channelValueId;
    }

    public int getSelectValueId() {
        return selectValueId;
    }

    @Override
    protected int getSizeInventory() {
        return 0;
    }

    @Override
    public boolean stillValid(Player player) {
        return PartHelpers.canInteractWith(getTarget(), player, this.partContainer.get());
    }

    @Override
    protected void initializeValues() {
        refreshLinkValues();
        PartTypeInterfaceQIO.State state = getPartState();
        ValueNotifierHelpers.setValue(this, priorityValueId, state.getPriority());
        ValueNotifierHelpers.setValue(this, channelValueId, state.getChannelInterface());
    }

    protected void refreshLinkValues() {
        PartTypeInterfaceQIO.State state = getPartState();
        CompoundTag frequencies = new CompoundTag();
        frequencies.put("list", createFrequencyList(player.getUUID()));
        ValueNotifierHelpers.setValue(this, frequenciesValueId, frequencies);
        QIOFrequencyLink link = state.getLink();
        ValueNotifierHelpers.setValue(this, linkValueId, link == null ? new CompoundTag() : link.withAuthorizer(null).toNbt());
        ValueNotifierHelpers.setValue(this, statusValueId, state.getLinkStatus().ordinal());
        ValueNotifierHelpers.setValue(this, statusMessageValueId, state.getLinkStatus().getMessage(link));
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (!world.isClientSide() && ++ticks % REFRESH_INTERVAL == 0) {
            refreshLinkValues();
        }
    }

    @Override
    public void onUpdate(int valueId, CompoundTag value) {
        super.onUpdate(valueId, value);
        if (!world.isClientSide()) {
            if (valueId == selectValueId) {
                onSelect(value);
            } else if (valueId == priorityValueId || valueId == channelValueId) {
                onPriorityOrChannelChanged();
            }
        }
    }

    protected void onSelect(CompoundTag value) {
        Tag tag = value.get(ValueNotifierHelpers.KEY);
        if (!(tag instanceof CompoundTag selection)) {
            return;
        }
        QIOFrequency frequency = getFrequency(selection);
        // Never trust the client, so validate the selection
        if (frequency != null && QIOFrequencyLink.canAccess(frequency, player.getUUID())) {
            partType.setLink(target, getPartState(), new QIOFrequencyLink(frequency.getIdentity(), player.getUUID()));
        }
        refreshLinkValues();
    }

    protected void onPriorityOrChannelChanged() {
        PartTypeInterfaceQIO.State state = getPartState();
        int priority = ValueNotifierHelpers.getValueInt(this, priorityValueId);
        int channel = ValueNotifierHelpers.getValueInt(this, channelValueId);
        if (priority == state.getPriority() && channel == state.getChannelInterface()) {
            return;
        }
        state.setChannelInterface(channel);
        NetworkHelpers.getNetwork(target.getCenter()).ifPresentOrElse(
                network -> network.setPriorityAndChannel(new PartNetworkElement<>(partType, target.getCenter()), priority, state.getChannel()),
                () -> state.setPriority(priority)
        );
    }

    @Nullable
    protected static QIOFrequency getFrequency(CompoundTag selection) {
        if (!selection.hasUUID("owner")) {
            return null;
        }
        String name = selection.getString("name");
        SecurityMode securityMode = SecurityMode.BY_ID.apply(selection.getInt("security"));
        UUID owner = selection.getUUID("owner");
        FrequencyManager<QIOFrequency> manager = FrequencyType.QIO.getManager(new Frequency.FrequencyIdentity(name, securityMode, owner), owner);
        return manager == null ? null : manager.getFrequency(name);
    }

    /**
     * @param player A player.
     * @return All QIO frequencies the given player can access.
     */
    public static ListTag createFrequencyList(UUID player) {
        FrequencyManagerWrapper<QIOFrequency> managers = FrequencyType.QIO.getManagerWrapper();
        Set<QIOFrequency> frequencies = new LinkedHashSet<>();
        frequencies.addAll(managers.getPrivateManager(player).getFrequencies());
        // Make sure the player's own trusted frequencies are loaded
        managers.getTrustedManager(player);
        for (FrequencyManager<QIOFrequency> manager : managers.getTrustedManagers()) {
            for (QIOFrequency frequency : manager.getFrequencies()) {
                if (QIOFrequencyLink.canAccess(frequency, player)) {
                    frequencies.add(frequency);
                }
            }
        }
        frequencies.addAll(managers.getPublicManager().getFrequencies());

        List<QIOFrequency> sorted = Lists.newArrayList(frequencies);
        sorted.sort(Comparator.comparing((QIOFrequency frequency) -> !Objects.equals(frequency.getOwner(), player))
                .thenComparing(QIOFrequency::getName, String.CASE_INSENSITIVE_ORDER));

        ListTag list = new ListTag();
        for (QIOFrequency frequency : sorted) {
            if (frequency.getOwner() == null) {
                continue;
            }
            CompoundTag tag = new CompoundTag();
            tag.putString("name", frequency.getName());
            tag.putInt("security", frequency.getSecurity().ordinal());
            tag.putUUID("owner", frequency.getOwner());
            tag.putString("ownerName", frequency.getOwnerName());
            int[] rgb = frequency.getColor().getRgbCode();
            tag.putInt("color", (rgb[0] << 16) | (rgb[1] << 8) | rgb[2]);
            tag.putLong("items", frequency.getTotalItemCount());
            tag.putLong("itemsMax", frequency.getTotalItemCountCapacity());
            tag.putInt("types", frequency.getTotalItemTypes(false));
            tag.putInt("typesMax", frequency.getTotalItemTypeCapacity());
            list.add(tag);
        }
        return list;
    }
}
