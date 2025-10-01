package org.cyclops.integratedmekanism.proxy;

import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.network.PacketHandler;
import org.cyclops.cyclopscore.proxy.CommonProxyComponent;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integratedmekanism.IntegratedMekanism;
import org.cyclops.integratedmekanism.network.packet.CPacketValueTypeRecipeChemicalLPElementSetRecipe;
import org.cyclops.integratedmekanism.network.packet.LogicProgrammerValueTypeRecipeChemicalSlotPropertiesChangedPacket;
import org.cyclops.integratedmekanism.network.packet.LogicProgrammerValueTypeRecipeChemicalValueChangedPacket;

/**
 * Proxy for server and client side.
 * @author rubensworks
 *
 */
public class CommonProxy extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return IntegratedMekanism._instance;
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        super.registerPacketHandlers(packetHandler);

        // Register packets.
        packetHandler.register(LogicProgrammerValueTypeRecipeChemicalValueChangedPacket.class, LogicProgrammerValueTypeRecipeChemicalValueChangedPacket.ID, LogicProgrammerValueTypeRecipeChemicalValueChangedPacket.CODEC);
        packetHandler.register(LogicProgrammerValueTypeRecipeChemicalSlotPropertiesChangedPacket.class, LogicProgrammerValueTypeRecipeChemicalSlotPropertiesChangedPacket.ID, LogicProgrammerValueTypeRecipeChemicalSlotPropertiesChangedPacket.CODEC);
        packetHandler.register(CPacketValueTypeRecipeChemicalLPElementSetRecipe.class, CPacketValueTypeRecipeChemicalLPElementSetRecipe.ID, CPacketValueTypeRecipeChemicalLPElementSetRecipe.CODEC);

        IntegratedDynamics.clog("Registered packet handler.");
    }

}
