package org.cyclops.integratedmekanism.modcompat.integratedtunnels;

import org.cyclops.cyclopscore.config.ConfigHandler;
import org.cyclops.cyclopscore.modcompat.ICompatInitializer;
import org.cyclops.cyclopscore.modcompat.IModCompat;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio.ContainerPartInterfaceQIOConfig;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio.DataComponentQIOFrequencyLinkConfig;

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

    /**
     * Register the configurables that are needed for Integrated Tunnels compatibility.
     * This must happen before the mod compats are loaded.
     * @param configHandler The config handler.
     */
    public static void registerConfigs(ConfigHandler configHandler) {
        configHandler.addConfigurable(new DataComponentQIOFrequencyLinkConfig());
        configHandler.addConfigurable(new ContainerPartInterfaceQIOConfig());
    }
}
