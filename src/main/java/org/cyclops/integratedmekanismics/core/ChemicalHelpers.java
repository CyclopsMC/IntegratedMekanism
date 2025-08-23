package org.cyclops.integratedmekanismics.core;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraftforge.registries.ForgeRegistry;

/**
 * @author rubensworks
 */
public class ChemicalHelpers {

    public static ForgeRegistry<Chemical> getStackRegistry(ChemicalStack<?> instance) {
        if (instance instanceof GasStack) {
            return (ForgeRegistry) MekanismAPI.gasRegistry();
        } else if (instance instanceof InfusionStack) {
            return (ForgeRegistry) MekanismAPI.infuseTypeRegistry();
        } else if (instance instanceof PigmentStack) {
            return (ForgeRegistry) MekanismAPI.pigmentRegistry();
        } else if (instance instanceof SlurryStack) {
            return (ForgeRegistry) MekanismAPI.slurryRegistry();
        }
        throw new IllegalArgumentException("Unknown chemical stack: " + instance);
    }

}
