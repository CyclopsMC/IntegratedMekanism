package org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import mekanism.api.security.SecurityMode;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.lib.frequency.Frequency;
import mekanism.common.lib.frequency.FrequencyManager;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.lib.frequency.IdentitySerializer;
import mekanism.common.lib.security.SecurityUtils;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * A link to a QIO frequency, authorized by a player.
 * @param identity The frequency identity.
 * @param authorizer The player that configured the link, used for checking private and trusted frequencies.
 * @author rubensworks
 */
public record QIOFrequencyLink(Frequency.FrequencyIdentity identity, @Nullable UUID authorizer) {

    public static final Codec<QIOFrequencyLink> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IdentitySerializer.NAME.codec().fieldOf("identity").forGetter(QIOFrequencyLink::identity),
            UUIDUtil.CODEC.optionalFieldOf("authorizer").forGetter(link -> Optional.ofNullable(link.authorizer()))
    ).apply(instance, (identity, authorizer) -> new QIOFrequencyLink(identity, authorizer.orElse(null))));
    public static final StreamCodec<ByteBuf, QIOFrequencyLink> STREAM_CODEC = StreamCodec.composite(
            IdentitySerializer.NAME.streamCodec(), QIOFrequencyLink::identity,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), link -> Optional.ofNullable(link.authorizer()),
            (identity, authorizer) -> new QIOFrequencyLink(identity, authorizer.orElse(null))
    );

    public String getName() {
        return identity().key().toString();
    }

    public QIOFrequencyLink withAuthorizer(@Nullable UUID authorizer) {
        return new QIOFrequencyLink(identity(), authorizer);
    }

    /**
     * @return The frequency this link refers to, or null if it does not exist (anymore).
     */
    @Nullable
    public QIOFrequency getFrequency() {
        FrequencyManager<QIOFrequency> manager = FrequencyType.QIO.getManager(identity(), identity().ownerUUID());
        return manager == null ? null : manager.getFrequency(identity().key());
    }

    /**
     * @return If the authorizer of this link may access the frequency.
     */
    public boolean isAuthorized() {
        return canAccess(identity().securityMode(), identity().ownerUUID(), authorizer());
    }

    public static boolean canAccess(SecurityMode securityMode, @Nullable UUID owner, @Nullable UUID player) {
        return switch (securityMode) {
            case PUBLIC -> true;
            case PRIVATE -> player != null && Objects.equals(owner, player);
            case TRUSTED -> player != null && (Objects.equals(owner, player)
                    || SecurityUtils.get().isTrusted(SecurityMode.TRUSTED, owner, player));
        };
    }

    public static boolean canAccess(QIOFrequency frequency, UUID player) {
        return canAccess(frequency.getSecurity(), frequency.getOwner(), player);
    }

    public Tag toNbt() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
    }

    @Nullable
    public static QIOFrequencyLink fromNbt(@Nullable Tag tag) {
        if (!(tag instanceof CompoundTag compoundTag) || compoundTag.isEmpty()) {
            return null;
        }
        return CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null);
    }

}
