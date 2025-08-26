package org.cyclops.integratedmekanismics.modcompat.integratedrest;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.cyclops.cyclopscore.modcompat.ICompatInitializer;
import org.cyclops.integrateddynamics.core.event.IntegratedDynamicsSetupEvent;
import org.cyclops.integratedmekanismics.modcompat.integratedrest.json.MekanismValueTypeJsonHandlers;

/**
 * @author rubensworks
 */
public class ModCompatInitializerIntegratedRest implements ICompatInitializer {
    @Override
    public void initialize() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::onSetup);
    }

    public void onSetup(IntegratedDynamicsSetupEvent event) {
        MekanismValueTypeJsonHandlers.load();
    }
}
