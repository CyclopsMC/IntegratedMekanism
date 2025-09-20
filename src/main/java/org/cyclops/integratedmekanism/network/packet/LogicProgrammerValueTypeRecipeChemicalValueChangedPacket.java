package org.cyclops.integratedmekanism.network.packet;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.cyclops.cyclopscore.network.CodecField;
import org.cyclops.cyclopscore.network.PacketCodec;
import org.cyclops.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import org.cyclops.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import org.cyclops.integratedmekanism.logicprogrammer.ValueTypeRecipeChemicalLPElement;

/**
 * Packet for sending to the server if a recipe string value has changed.
 * @author rubensworks
 *
 */
public class LogicProgrammerValueTypeRecipeChemicalValueChangedPacket extends PacketCodec {

    @CodecField
    private String value;
    @CodecField
    private int type;
    @CodecField
    private int slot;

    public LogicProgrammerValueTypeRecipeChemicalValueChangedPacket() {

    }

    public LogicProgrammerValueTypeRecipeChemicalValueChangedPacket(String value, Type type, int slot) {
        this.value = value;
        this.type = type.ordinal();
        this.slot = slot;
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
            if(element instanceof ValueTypeRecipeChemicalLPElement elementCast) {
                Type type = Type.values()[this.type];
                switch (type) {
                    case INPUT_FLUID:
                        elementCast.getInputFluids().set(slot, Pair.of(elementCast.getInputFluids().get(slot).getLeft(), value));
                        break;
                    case INPUT_CHEMICAL:
                        elementCast.getInputChemicals().set(slot, Pair.of(elementCast.getInputChemicals().get(slot).getLeft(), value));
                        break;
                    case OUTPUT_FLUID:
                        elementCast.getOutputFluids().set(slot, Pair.of(elementCast.getOutputFluids().get(slot).getLeft(), value));
                        break;
                    case OUTPUT_CHEMICAL:
                        elementCast.getOutputChemicals().set(slot, Pair.of(elementCast.getOutputChemicals().get(slot).getLeft(), value));
                        break;
                }
                ((ContainerLogicProgrammerBase) player.containerMenu).onDirty();
            }
        }
    }

    public static enum Type {
        INPUT_FLUID,
        INPUT_CHEMICAL,
        OUTPUT_FLUID,
        OUTPUT_CHEMICAL,
    }

}
