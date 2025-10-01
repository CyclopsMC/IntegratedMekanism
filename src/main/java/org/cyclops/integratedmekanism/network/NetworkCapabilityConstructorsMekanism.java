package org.cyclops.integratedmekanism.network;

import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.integrateddynamics.api.network.AttachCapabilitiesEventNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;

/**
 * @author rubensworks
 */
public class NetworkCapabilityConstructorsMekanism {

    public void onNetworkLoad(AttachCapabilitiesEventNetwork event) {
        ChemicalNetwork chemicalNetwork = new ChemicalNetwork(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK);
        IChemicalHandler channel = chemicalNetwork.getChannelExternal(Capabilities.CHEMICAL.block(), IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL);
        event.register(org.cyclops.integratedmekanism.Capabilities.ChemicalNetwork.NETWORK,
                new DefaultCapabilityProvider<>(chemicalNetwork));
        event.register(org.cyclops.integratedmekanism.Capabilities.ChemicalHandler.NETWORK,
                new DefaultCapabilityProvider<>(channel));
        event.addFullNetworkListener(chemicalNetwork);
    }

}
