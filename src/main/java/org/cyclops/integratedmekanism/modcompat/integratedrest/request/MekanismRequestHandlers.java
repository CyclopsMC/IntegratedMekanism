package org.cyclops.integratedmekanism.modcompat.integratedrest.request;

import org.cyclops.integratedrest.IntegratedRest;
import org.cyclops.integratedrest.api.http.request.IRequestHandlerRegistry;

/**
 * @author rubensworks
 */
public class MekanismRequestHandlers {

    public static IRequestHandlerRegistry REGISTRY = IntegratedRest._instance.getRegistryManager().getRegistry(IRequestHandlerRegistry.class);

    public static void load() {
        REGISTRY.registerHandler("registry/chemical", new RegistryChemicalRequestHandler());
    }

}
