package org.cyclops.integratedmekanism.core;

import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * @author rubensworks
 */
public class CapabilityHelpers {

    public static boolean hasChemical(IChemicalHandler handler) {
        int tanks = handler.getChemicalTanks();
        for (int i = 0; i < tanks; i++) {
            if (!handler.getChemicalInTank(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static Optional<IChemicalHandler> getChemicalHandler(ItemStack itemStack) {
        return Optional.ofNullable(itemStack.getCapability(Capabilities.CHEMICAL.item()));
    }
}
