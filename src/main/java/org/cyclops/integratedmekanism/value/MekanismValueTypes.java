package org.cyclops.integratedmekanism.value;

import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeRegistry;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeRegistry;

/**
 * @author rubensworks
 */
public class MekanismValueTypes {

    public static final IValueTypeRegistry REGISTRY = constructRegistry();

    public static ValueObjectTypeChemicalStack OBJECT_CHEMICALSTACK  = REGISTRY.register(new ValueObjectTypeChemicalStack());

    private static IValueTypeRegistry constructRegistry() {
        // This also allows this registry to be used outside of a minecraft environment.
        if(MinecraftHelpers.isModdedEnvironment()) {
            return IntegratedDynamics._instance.getRegistryManager().getRegistry(IValueTypeRegistry.class);
        } else {
            return ValueTypeRegistry.getInstance();
        }
    }

    public static void load() {}

}
