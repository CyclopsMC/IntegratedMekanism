package org.cyclops.integratedmekanism.modcompat.integratedtunnels.part;

import mekanism.api.SerializationConstants;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.item.ItemConfigurationCard;
import mekanism.common.lib.frequency.Frequency;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.lib.frequency.IdentitySerializer;
import mekanism.common.registries.MekanismDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageHandler;
import org.cyclops.cyclopscore.config.extendedconfig.BlockConfig;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.integrateddynamics.api.evaluate.variable.ValueDeseralizationContext;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.NetworkCapability;
import org.cyclops.integrateddynamics.api.part.IPartContainer;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.block.IgnoredBlockStatus;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integrateddynamics.core.part.PartTypeBase;
import org.cyclops.integratedmekanism.Capabilities;
import org.cyclops.integratedmekanism.GeneralConfig;
import org.cyclops.integratedmekanism.IntegratedMekanism;
import org.cyclops.integratedmekanism.RegistryEntries;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio.ContainerPartInterfaceQIO;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio.IngredientComponentStorageHandlerQIO;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio.IngredientComponentStorageQIO;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio.QIOFrequencyLink;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio.QIOLinkStatus;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Interface that links an item network to a QIO frequency, without requiring a physical connection.
 * @author rubensworks
 */
public class PartTypeInterfaceQIO extends PartTypeInterfacePositionedAddon<IItemNetwork, IIngredientComponentStorageHandler, PartTypeInterfaceQIO, PartTypeInterfaceQIO.State> {

    public PartTypeInterfaceQIO(String name) {
        super(name);
    }

    @Override
    public ModBase getMod() {
        return IntegratedMekanism._instance;
    }

    @Override
    public NetworkCapability<IItemNetwork> getNetworkCapability() {
        return org.cyclops.integratedtunnels.Capabilities.ItemNetwork.NETWORK;
    }

    @Override
    public PartCapability<IIngredientComponentStorageHandler> getPartCapability() {
        return Capabilities.IngredientComponentStorageHandler.PART;
    }

    @Override
    public BlockCapability<IIngredientComponentStorageHandler, Direction> getBlockCapability() {
        return org.cyclops.commoncapabilities.api.capability.Capabilities.IngredientComponentStorageHandler.BLOCK;
    }

    @Override
    protected PartTypeInterfaceQIO.State constructDefaultState() {
        return new PartTypeInterfaceQIO.State();
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceQIOBaseConsumption;
    }

    @Override
    protected Block createBlock(BlockConfig blockConfig) {
        return new IgnoredBlockStatus();
    }

    @Override
    public BlockState getBlockState(IPartContainer partContainer, Direction side) {
        State state = partContainer != null ? (State) partContainer.getPartState(side) : null;
        IgnoredBlockStatus.Status status = state != null ? state.getLinkStatus().getBlockStatus() : IgnoredBlockStatus.Status.INACTIVE;
        return super.getBlockState(partContainer, side).setValue(IgnoredBlockStatus.STATUS, status);
    }

    // The storage is exposed at the position of this part itself, so there is no target block.

    @Override
    public PartPos getEffectiveTargetPos(PartTarget target, State state) {
        return target.getCenter();
    }

    @Override
    public boolean supportsOffsets() {
        return false;
    }

    @Override
    public Optional<IIngredientComponentStorageHandler> getTargetCapabilityInstance(PartPos pos) {
        PartHelpers.PartStateHolder<?, ?> holder = PartHelpers.getPart(pos);
        if (holder != null && holder.getState() instanceof State state && state.getActiveFrequency() != null) {
            return Optional.of(state.getCapabilityInstance());
        }
        return Optional.empty();
    }

    @Override
    public boolean canNeighbourChangeAffectTarget(State state, BlockGetter world, BlockPos neighbourBlockPos) {
        return false;
    }

    @Override
    public void registerTargetCapabilityListener(PartPos posTarget, State state) {
        // Our target is this part itself
    }

    @Override
    public boolean isUpdate(State state) {
        return true;
    }

    @Override
    public void update(INetwork network, IPartNetwork partNetwork, PartTarget target, State state) {
        super.update(network, partNetwork, target, state);
        updateLink(network, target, state);
    }

    @Override
    public void onNetworkAddition(INetwork network, IPartNetwork partNetwork, PartTarget target, State state) {
        state.setLinkResult(resolveLink(network, target, state));
        super.onNetworkAddition(network, partNetwork, target, state);
    }

    @Override
    public void afterNetworkReAlive(INetwork network, IPartNetwork partNetwork, PartTarget target, State state) {
        state.setLinkResult(resolveLink(network, target, state));
        super.afterNetworkReAlive(network, partNetwork, target, state);
    }

    /**
     * Change the frequency this interface links to.
     * @param target The part target.
     * @param state The part state.
     * @param link The new link, or null to unlink.
     */
    public void setLink(PartTarget target, State state, @Nullable QIOFrequencyLink link) {
        state.setLink(link);
        updateLink(NetworkHelpers.getNetwork(target.getCenter()).orElse(null), target, state);
    }

    /**
     * Re-check the linked frequency, and update this interface's presence in the network if it changed.
     * @param network The network, may be null.
     * @param target The part target.
     * @param state The part state.
     */
    public void updateLink(@Nullable INetwork network, PartTarget target, State state) {
        LinkResult result = resolveLink(network, target, state);
        if (result.status() != state.getLinkStatus() || result.frequency() != state.getActiveFrequency()) {
            if (network != null) {
                scheduleNetworkObservation(target, state);
                removeTargetFromNetwork(network, state);
            }
            state.setLinkResult(result);
            if (network != null) {
                addTargetToNetwork(network, target, state.getPriority(), state.getChannelInterface(), state);
                scheduleNetworkObservation(target, state);
            }
            state.sendUpdate();
        }
    }

    protected LinkResult resolveLink(@Nullable INetwork network, PartTarget target, State state) {
        QIOFrequencyLink link = state.getLink();
        if (link == null) {
            return new LinkResult(QIOLinkStatus.UNLINKED, null);
        }
        QIOFrequency frequency = link.getFrequency();
        if (frequency == null) {
            return new LinkResult(QIOLinkStatus.NOT_FOUND, null);
        }
        if (!link.isAuthorized()) {
            return new LinkResult(QIOLinkStatus.NO_ACCESS, null);
        }
        if (network != null && isLinkedElsewhere(network, target.getCenter(), frequency)) {
            return new LinkResult(QIOLinkStatus.DUPLICATE, null);
        }
        return new LinkResult(QIOLinkStatus.LINKED, frequency);
    }

    /**
     * Linking the same frequency multiple times would count its items multiple times,
     * also across channels, so only the first link is kept active.
     */
    protected boolean isLinkedElsewhere(INetwork network, PartPos pos, QIOFrequency frequency) {
        return network.getCapability(getNetworkCapability())
                .map(itemNetwork -> {
                    for (PartPos otherPos : itemNetwork.getPositions()) {
                        if (!otherPos.equals(pos) && otherPos.getPos().isLoaded()) {
                            PartHelpers.PartStateHolder<?, ?> holder = PartHelpers.getPart(otherPos);
                            if (holder != null && holder.getState() instanceof State otherState
                                    && otherState.getActiveFrequency() == frequency) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .orElse(false);
    }

    @Override
    public Optional<MenuProvider> getContainerProvider(PartPos pos) {
        return Optional.of(new MenuProvider() {

            @Override
            public Component getDisplayName() {
                return Component.translatable(getTranslationKey());
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity) {
                Triple<IPartContainer, PartTypeBase, PartTarget> data = PartHelpers.getContainerPartConstructionData(pos);
                return new ContainerPartInterfaceQIO(id, playerInventory, data.getRight(), Optional.of(data.getLeft()), (PartTypeInterfaceQIO) data.getMiddle());
            }

            @Override
            public boolean shouldTriggerClientSideContainerClosingOnOpen() {
                return false;
            }
        });
    }

    @Override
    public Optional<MenuProvider> getContainerProviderSettings(PartPos pos) {
        // Priority and channel are configured in the main gui
        return Optional.empty();
    }

    @Override
    public InteractionResult onPartActivated(State partState, BlockPos pos, Level world, Player player, InteractionHand hand,
                                             ItemStack heldItem, BlockHitResult hit) {
        if (!player.isSecondaryUseActive() && heldItem.getItem() instanceof ItemConfigurationCard) {
            if (!world.isClientSide()) {
                Frequency.FrequencyIdentity identity = getConfigurationCardFrequency(heldItem, world.registryAccess());
                if (identity == null) {
                    player.displayClientMessage(Component.translatable("gui.integratedmekanism.interface_qio.card.empty"), true);
                } else {
                    QIOFrequencyLink link = new QIOFrequencyLink(identity, player.getUUID());
                    if (link.isAuthorized()) {
                        PartTarget target = getTarget(PartPos.of(world, pos, hit.getDirection()), partState);
                        setLink(target, partState, link);
                        player.displayClientMessage(partState.getLinkStatus().getMessage(link), true);
                    } else {
                        player.displayClientMessage(QIOLinkStatus.NO_ACCESS.getMessage(link), true);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.onPartActivated(partState, pos, world, player, hand, heldItem, hit);
    }

    /**
     * @param stack A Mekanism configuration card.
     * @param provider The registry lookup.
     * @return The QIO frequency stored in the card, or null.
     */
    @Nullable
    public static Frequency.FrequencyIdentity getConfigurationCardFrequency(ItemStack stack, HolderLookup.Provider provider) {
        CompoundTag data = stack.get(MekanismDataComponents.CONFIGURATION_DATA);
        if (data == null || !data.contains(SerializationConstants.COMPONENT_FREQUENCY, Tag.TAG_COMPOUND)) {
            return null;
        }
        CompoundTag frequencies = data.getCompound(SerializationConstants.COMPONENT_FREQUENCY);
        if (!frequencies.contains(FrequencyType.QIO.getName(), Tag.TAG_COMPOUND)) {
            return null;
        }
        return IdentitySerializer.NAME.codec()
                .parse(provider.createSerializationContext(NbtOps.INSTANCE), frequencies.get(FrequencyType.QIO.getName()))
                .result()
                .filter(identity -> identity.ownerUUID() != null)
                .orElse(null);
    }

    @Override
    public ItemStack getItemStack(ValueDeseralizationContext valueDeseralizationContext, State state, boolean saveState) {
        ItemStack itemStack = super.getItemStack(valueDeseralizationContext, state, saveState);
        if (state.getLink() != null) {
            // Don't store who authorized the link, as the item may end up with another player
            itemStack.set(RegistryEntries.DATACOMPONENT_QIO_FREQUENCY_LINK, state.getLink().withAuthorizer(null));
        }
        return itemStack;
    }

    @Override
    public State getState(ValueDeseralizationContext valueDeseralizationContext, ItemStack itemStack) {
        State state = super.getState(valueDeseralizationContext, itemStack);
        QIOFrequencyLink link = itemStack.has(RegistryEntries.DATACOMPONENT_QIO_FREQUENCY_LINK)
                ? itemStack.get(RegistryEntries.DATACOMPONENT_QIO_FREQUENCY_LINK) : state.getLink();
        // A placed interface must be re-authorized for non-public frequencies
        state.setLink(link == null ? null : link.withAuthorizer(null));
        return state;
    }

    @Override
    public void loadTooltip(State state, List<Component> lines) {
        super.loadTooltip(state, lines);
        lines.add(state.getLinkStatus().getMessage(state.getLink()));
    }

    @Override
    public void loadTooltip(ItemStack itemStack, List<Component> lines) {
        super.loadTooltip(itemStack, lines);
        QIOFrequencyLink link = itemStack.get(RegistryEntries.DATACOMPONENT_QIO_FREQUENCY_LINK);
        if (link != null) {
            lines.add(Component.translatable("gui.integratedmekanism.interface_qio.frequency", link.getName()));
        }
    }

    public record LinkResult(QIOLinkStatus status, @Nullable QIOFrequency frequency) {
    }

    public static class State extends PartTypeInterfacePositionedAddon.State<IItemNetwork, IIngredientComponentStorageHandler, PartTypeInterfaceQIO, PartTypeInterfaceQIO.State> {

        @Nullable
        private QIOFrequencyLink link = null;
        private QIOLinkStatus linkStatus = QIOLinkStatus.UNLINKED;
        @Nullable
        private QIOFrequency activeFrequency = null;
        private final IngredientComponentStorageHandlerQIO storageHandler =
                new IngredientComponentStorageHandlerQIO(new IngredientComponentStorageQIO(this::getActiveFrequency));

        @Nullable
        public QIOFrequencyLink getLink() {
            return link;
        }

        public void setLink(@Nullable QIOFrequencyLink link) {
            this.link = link;
            sendUpdate();
        }

        public QIOLinkStatus getLinkStatus() {
            return linkStatus;
        }

        /**
         * @return The frequency this interface currently exposes to the network, or null.
         */
        @Nullable
        public QIOFrequency getActiveFrequency() {
            return activeFrequency != null && !activeFrequency.isRemoved() ? activeFrequency : null;
        }

        public void setLinkResult(LinkResult result) {
            this.linkStatus = result.status();
            this.activeFrequency = result.frequency();
        }

        @Override
        protected int getDefaultUpdateInterval() {
            return 10;
        }

        @Override
        public PartCapability<IIngredientComponentStorageHandler> getTargetCapability() {
            return Capabilities.IngredientComponentStorageHandler.PART;
        }

        @Override
        public IIngredientComponentStorageHandler getCapabilityInstance() {
            return storageHandler;
        }

        @Override
        public <T> Optional<T> getCapability(PartTypeInterfaceQIO partType, PartCapability<T> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
            if (capability == getTargetCapability()) {
                return getActiveFrequency() != null ? Optional.of((T) storageHandler) : Optional.empty();
            }
            return super.getCapability(partType, capability, network, partNetwork, target);
        }

        @Override
        public void writeToNBT(ValueDeseralizationContext valueDeseralizationContext, CompoundTag tag) {
            super.writeToNBT(valueDeseralizationContext, tag);
            if (link != null) {
                tag.put("qioLink", link.toNbt());
            }
            tag.putInt("qioLinkStatus", linkStatus.ordinal());
        }

        @Override
        public void readFromNBT(ValueDeseralizationContext valueDeseralizationContext, CompoundTag tag) {
            super.readFromNBT(valueDeseralizationContext, tag);
            this.link = QIOFrequencyLink.fromNbt(tag.get("qioLink"));
            this.linkStatus = QIOLinkStatus.fromOrdinal(tag.getInt("qioLinkStatus"));
        }
    }

}
