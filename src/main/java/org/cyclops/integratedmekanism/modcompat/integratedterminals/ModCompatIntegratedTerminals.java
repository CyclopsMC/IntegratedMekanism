package org.cyclops.integratedmekanism.modcompat.integratedterminals;

import org.cyclops.cyclopscore.modcompat.ICompatInitializer;
import org.cyclops.cyclopscore.modcompat.IModCompat;
import org.cyclops.integratedmekanism.Reference;

/**
 * @author rubensworks
 */
public class ModCompatIntegratedTerminals implements IModCompat {
    @Override
    public String getId() {
        return Reference.MOD_INTEGRATEDTERMINALS;
    }

    @Override
    public boolean isEnabledDefault() {
        return true;
    }

    @Override
    public String getComment() {
        return "Integrated Terminals support for Mekanism chemicals";
    }

    @Override
    public ICompatInitializer createInitializer() {
        return new ModCompatInitializerIntegratedTerminals();
    }
}
