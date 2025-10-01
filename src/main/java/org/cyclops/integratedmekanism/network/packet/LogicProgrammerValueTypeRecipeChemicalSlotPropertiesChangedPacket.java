package org.cyclops.integratedmekanism.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.cyclops.cyclopscore.network.CodecField;
import org.cyclops.cyclopscore.network.PacketCodec;
import org.cyclops.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import org.cyclops.integrateddynamics.core.ingredient.ItemMatchProperties;
import org.cyclops.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.logicprogrammer.ValueTypeRecipeChemicalLPElement;

/**
 * Packet for sending to the server if recipe slot properties have changed.
 * @author rubensworks
 *
 */
public class LogicProgrammerValueTypeRecipeChemicalSlotPropertiesChangedPacket extends PacketCodec<LogicProgrammerValueTypeRecipeChemicalSlotPropertiesChangedPacket> {

    public static final Type<LogicProgrammerValueTypeRecipeChemicalSlotPropertiesChangedPacket> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "lp_valuetype_recipe_chemical_slot_properties_changed"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LogicProgrammerValueTypeRecipeChemicalSlotPropertiesChangedPacket> CODEC = getCodec(LogicProgrammerValueTypeRecipeChemicalSlotPropertiesChangedPacket::new);

    @CodecField
    private int slot;
    @CodecField
    private boolean nbt;
    @CodecField
    private String tag;
    @CodecField
    private int tagQuantity;
    @CodecField
    private boolean reusable;

    public LogicProgrammerValueTypeRecipeChemicalSlotPropertiesChangedPacket() {
        super(ID);
    }

    public LogicProgrammerValueTypeRecipeChemicalSlotPropertiesChangedPacket(int slot, boolean nbt, String tag, int tagQuantity, boolean reusable) {
        super(ID);
        this.slot = slot;
        this.nbt = nbt;
        this.tag = tag;
        this.tagQuantity = tagQuantity;
        this.reusable = reusable;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void actionClient(Level world, Player player) {

    }

    @Override
    public void actionServer(Level world, ServerPlayer player) {
        if(player.containerMenu instanceof ContainerLogicProgrammerBase) {
            ILogicProgrammerElement element = ((ContainerLogicProgrammerBase) player.containerMenu).getActiveElement();
            if(element instanceof ValueTypeRecipeChemicalLPElement) {
                ItemMatchProperties props = ((ValueTypeRecipeChemicalLPElement) element).getInputStacks().get(slot);
                props.setNbt(nbt);
                props.setItemTag(tag.isEmpty() ? null : tag);
                props.setTagQuantity(this.tagQuantity);
                props.setReusable(reusable);
                ((ContainerLogicProgrammerBase) player.containerMenu).onDirty();
            }
        }
    }

}
