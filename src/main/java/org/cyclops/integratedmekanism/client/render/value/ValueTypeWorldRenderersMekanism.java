package org.cyclops.integratedmekanism.client.render.value;

import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.client.render.valuetype.IValueTypeWorldRendererRegistry;
import org.cyclops.integratedmekanism.value.MekanismValueTypes;

/**
 * A collection of all value type world renderers.
 * @author rubensworks
 */
public class ValueTypeWorldRenderersMekanism {

    public static final IValueTypeWorldRendererRegistry REGISTRY = IntegratedDynamics._instance.getRegistryManager().getRegistry(IValueTypeWorldRendererRegistry.class);

    public static void load() {
        REGISTRY.register(MekanismValueTypes.OBJECT_CHEMICALSTACK, new ChemicalValueTypeWorldRenderer());
    }

}
