package org.cyclops.integratedmekanism.modcompat.integratedrest;

import net.neoforged.bus.api.EventPriority;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.modcompat.ICompatInitializer;
import org.cyclops.integrateddynamics.core.event.IntegratedDynamicsSetupEvent;
import org.cyclops.integratedmekanism.modcompat.integratedrest.json.MekanismValueTypeJsonHandlers;
import org.cyclops.integratedmekanism.modcompat.integratedrest.request.MekanismRequestHandlers;

/**
 * @author rubensworks
 */
public class ModCompatInitializerIntegratedRest implements ICompatInitializer {
    @Override
    public void initialize() {

    }

    @Override
    public void initialize(IModBase mod) {
        ((ModBase<?>) mod).getModEventBus().addListener(EventPriority.LOW, this::onSetup);
    }

    public void onSetup(IntegratedDynamicsSetupEvent event) {
        MekanismRequestHandlers.load();
        MekanismValueTypeJsonHandlers.load();
    }
}
