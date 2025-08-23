package org.cyclops.integratedmekanismics.modcompat.integratedtunnels;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import org.cyclops.cyclopscore.modcompat.ICompatInitializer;
import org.cyclops.integratedmekanismics.part.PartTypesMekanism;

/**
 * @author rubensworks
 */
public class ModCompatInitializerIntegratedTunnels implements ICompatInitializer {
    @Override
    public void initialize() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegistriesCreate);
    }

    public void onRegistriesCreate(NewRegistryEvent event) {
        PartTypesMekanism.load();
    }
}
