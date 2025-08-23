package org.cyclops.integratedmekanismics.part;

import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.part.IPartTypeRegistry;

/**
 * @author rubensworks
 */
public class PartTypesMekanism {

    public static final IPartTypeRegistry REGISTRY = IntegratedDynamics._instance.getRegistryManager().getRegistry(IPartTypeRegistry.class);

    public static void load() {}

    public static final PartTypeInterfaceChemical INTERFACE_CHEMICAL = REGISTRY.register(new PartTypeInterfaceChemical("interface_chemical"));

}
