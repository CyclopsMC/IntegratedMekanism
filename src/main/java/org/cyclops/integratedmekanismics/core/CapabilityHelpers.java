package org.cyclops.integratedmekanismics.core;

import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.cyclops.cyclopscore.helper.BlockEntityHelpers;
import org.cyclops.integrateddynamics.api.part.PartPos;

import java.util.List;

/**
 * @author rubensworks
 */
public class CapabilityHelpers {

    public static <T> LazyOptional<T> getFirstOf(PartPos pos, List<Capability<? extends T>> capabilities) {
        LazyOptional<T> lazyOptional = LazyOptional.empty();
        for (Capability<? extends T> capability : capabilities) {
            LazyOptional<T> lazyOptionalCurrent = BlockEntityHelpers.getCapability(pos.getPos(), pos.getSide(), capability).cast();
            if (lazyOptionalCurrent.isPresent()) {
                lazyOptional = lazyOptionalCurrent;
                break;
            }
        }
        return lazyOptional;
    }

    public static <T> LazyOptional<T> getFirstOf(ItemStack itemStack, List<Capability<? extends T>> capabilities) {
        LazyOptional<T> lazyOptional = LazyOptional.empty();
        for (Capability<? extends T> capability : capabilities) {
            LazyOptional<T> lazyOptionalCurrent = itemStack.getCapability(capability).cast();
            if (lazyOptionalCurrent.isPresent()) {
                lazyOptional = lazyOptionalCurrent;
                break;
            }
        }
        return lazyOptional;
    }

    public static LazyOptional<IChemicalHandler<?, ?>> getChemicalHandler(ItemStack itemStack) {
        return getFirstOf(itemStack, List.of(Capabilities.GAS_HANDLER, Capabilities.INFUSION_HANDLER, Capabilities.PIGMENT_HANDLER, Capabilities.SLURRY_HANDLER));
    }
}
