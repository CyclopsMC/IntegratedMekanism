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

    public static final List<Capability<? extends IChemicalHandler<?, ?>>> CHEMICAL_CAPABILITIES = List.of(Capabilities.GAS_HANDLER, Capabilities.INFUSION_HANDLER, Capabilities.PIGMENT_HANDLER, Capabilities.SLURRY_HANDLER);

    public static <T extends IChemicalHandler<?, ?>> LazyOptional<T> getFirstOf(PartPos pos, List<Capability<? extends T>> capabilities) {
        LazyOptional<T> lazyOptional = LazyOptional.empty();
        for (Capability<? extends T> capability : capabilities) {
            LazyOptional<T> lazyOptionalCurrent = BlockEntityHelpers.getCapability(pos.getPos(), pos.getSide(), capability).cast();
            if (!lazyOptional.isPresent()) {
                lazyOptional = lazyOptionalCurrent;
            } else if (lazyOptionalCurrent.isPresent() && hasChemical(lazyOptionalCurrent.resolve().get())) {
                lazyOptional = lazyOptionalCurrent;
                break;
            }
        }
        return lazyOptional;
    }

    public static boolean hasChemical(IChemicalHandler<?, ?> handler) {
        int tanks = handler.getTanks();
        for (int i = 0; i < tanks; i++) {
            if (!handler.getChemicalInTank(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the first chemical handler that contains something.
     * If all are empty, it returns the first one.
     * @param itemStack An item.
     * @param capabilities An ordered collection of capabilities.
     * @return An optional chemical handler.
     * @param <T> The chemical handler type.
     */
    public static <T extends IChemicalHandler<?, ?>> LazyOptional<T> getFirstOf(ItemStack itemStack, List<Capability<? extends T>> capabilities) {
        LazyOptional<T> lazyOptionalFirst = LazyOptional.empty();
        for (Capability<? extends T> capability : capabilities) {
            LazyOptional<T> lazyOptionalCurrent = itemStack.getCapability(capability).cast();
            if (lazyOptionalCurrent.isPresent()) {
                T handler = lazyOptionalCurrent.resolve().get();
                if (handler.getTanks() > 0 && !handler.getChemicalInTank(0).isEmpty()) {
                    return lazyOptionalCurrent;
                }
                if (!lazyOptionalFirst.isPresent()) {
                    lazyOptionalFirst = lazyOptionalCurrent;
                }
            }
        }
        return lazyOptionalFirst;
    }

    public static LazyOptional<IChemicalHandler<?, ?>> getChemicalHandler(ItemStack itemStack) {
        return getFirstOf(itemStack, CHEMICAL_CAPABILITIES);
    }
}
