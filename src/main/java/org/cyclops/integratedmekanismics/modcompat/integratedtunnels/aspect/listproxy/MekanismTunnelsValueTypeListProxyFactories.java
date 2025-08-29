package org.cyclops.integratedmekanismics.modcompat.integratedtunnels.aspect.listproxy;

import net.minecraft.resources.ResourceLocation;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeListProxyFactories;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeListProxyNBTFactory;
import org.cyclops.integratedmekanismics.Reference;
import org.cyclops.integratedmekanismics.value.ValueObjectTypeChemicalStack;

/**
 * @author rubensworks
 */
public class MekanismTunnelsValueTypeListProxyFactories {

    public static ValueTypeListProxyNBTFactory<ValueObjectTypeChemicalStack, ValueObjectTypeChemicalStack.ValueChemicalStack, ValueTypeListProxyPositionedChemicalNetwork> POSITIONED_CHEMICAL_NETWORK;

    public static void load() {
        if (POSITIONED_CHEMICAL_NETWORK == null) {
            POSITIONED_CHEMICAL_NETWORK = ValueTypeListProxyFactories.REGISTRY.register(new ValueTypeListProxyNBTFactory<>(
                    new ResourceLocation(Reference.MOD_ID, "positioned_chemical_network"),
                    ValueTypeListProxyPositionedChemicalNetwork.class));
        }
    }

}
