package org.cyclops.integratedmekanismics.modcompat.integratedscripting;

import org.cyclops.cyclopscore.modcompat.ICompatInitializer;
import org.cyclops.cyclopscore.modcompat.IModCompat;
import org.cyclops.integratedmekanismics.Reference;

/**
 * @author rubensworks
 */
public class ModCompatIntegratedScripting implements IModCompat {
    @Override
    public String getId() {
        return Reference.MOD_INTEGRATEDSCRIPTING;
    }

    @Override
    public boolean isEnabledDefault() {
        return true;
    }

    @Override
    public String getComment() {
        return "Integrated Scripting support for Mekanism chemicals";
    }

    @Override
    public ICompatInitializer createInitializer() {
        return new ModCompatInitializerIntegratedScripting();
    }
}
