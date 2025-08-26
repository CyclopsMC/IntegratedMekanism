package org.cyclops.integratedmekanismics.modcompat.integratedrest;

import org.cyclops.cyclopscore.modcompat.ICompatInitializer;
import org.cyclops.cyclopscore.modcompat.IModCompat;
import org.cyclops.integratedmekanismics.Reference;

/**
 * @author rubensworks
 */
public class ModCompatIntegratedRest implements IModCompat {
    @Override
    public String getId() {
        return Reference.MOD_INTEGRATEDREST;
    }

    @Override
    public boolean isEnabledDefault() {
        return true;
    }

    @Override
    public String getComment() {
        return "Integrated REST support for Mekanism chemicals";
    }

    @Override
    public ICompatInitializer createInitializer() {
        return new ModCompatInitializerIntegratedRest();
    }
}
