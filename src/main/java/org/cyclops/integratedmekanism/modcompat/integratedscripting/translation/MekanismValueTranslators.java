package org.cyclops.integratedmekanism.modcompat.integratedscripting.translation;

import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.integratedmekanism.value.MekanismValueTypes;
import org.cyclops.integratedscripting.IntegratedScripting;
import org.cyclops.integratedscripting.api.evaluate.translation.IValueTranslatorRegistry;
import org.cyclops.integratedscripting.evaluate.translation.ValueTranslatorRegistry;
import org.cyclops.integratedscripting.evaluate.translation.translator.ValueTranslatorObjectAdapter;

/**
 * @author rubensworks
 */
public class MekanismValueTranslators {

    public static final IValueTranslatorRegistry REGISTRY = constructRegistry();

    private static IValueTranslatorRegistry constructRegistry() {
        // This also allows this registry to be used outside of a minecraft environment.
        if(MinecraftHelpers.isModdedEnvironment()) {
            return IntegratedScripting._instance.getRegistryManager().getRegistry(IValueTranslatorRegistry.class);
        } else {
            return ValueTranslatorRegistry.getInstance();
        }
    }

    public static void load() {
        // Object types
        REGISTRY.register(new ValueTranslatorObjectAdapter<>("id_chemical", MekanismValueTypes.OBJECT_CHEMICALSTACK));
    }

}
