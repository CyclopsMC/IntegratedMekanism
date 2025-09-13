package org.cyclops.integratedmekanism.modcompat.integratedtunnels;

import org.cyclops.cyclopscore.modcompat.ICompatInitializer;
import org.cyclops.cyclopscore.modcompat.IModCompat;
import org.cyclops.integratedmekanism.Reference;

/**
 * @author rubensworks
 */
public class ModCompatIntegratedTunnels implements IModCompat {
    @Override
    public String getId() {
        return Reference.MOD_INTEGRATEDTUNNELS;
    }

    @Override
    public boolean isEnabledDefault() {
        return true;
    }

    @Override
    public String getComment() {
        return "Interfaces, importers and exporters for Mekanism chemicals";
    }

    @Override
    public ICompatInitializer createInitializer() {
        return new ModCompatInitializerIntegratedTunnels();
    }
}
