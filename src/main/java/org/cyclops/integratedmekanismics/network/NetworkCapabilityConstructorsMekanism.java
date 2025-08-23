package org.cyclops.integratedmekanismics.network;

import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.integrateddynamics.api.network.AttachCapabilitiesEventNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integratedmekanismics.Reference;
import org.cyclops.integratedmekanismics.ingredient.IngredientComponentsMekanism;

/**
 * @author rubensworks
 */
public class NetworkCapabilityConstructorsMekanism {

    public void onNetworkLoad(AttachCapabilitiesEventNetwork event) {
        ChemicalNetwork chemicalNetwork = new ChemicalNetwork(IngredientComponentsMekanism.INGREDIENT_CHEMICALSTACK);
        event.addCapability(new ResourceLocation(Reference.MOD_ID, "chemical_network"),
                new DefaultCapabilityProvider<>(() -> ChemicalNetworkConfig.CAPABILITY, chemicalNetwork));

        IGasHandler channelGas = chemicalNetwork.getChannelExternal(Capabilities.GAS_HANDLER, IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL);
        event.addCapability(new ResourceLocation(Reference.MOD_ID, "gas_storage_network"),
                new DefaultCapabilityProvider<>(() -> Capabilities.GAS_HANDLER, channelGas));
        IInfusionHandler channelInfusion = chemicalNetwork.getChannelExternal(Capabilities.INFUSION_HANDLER, IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL);
        event.addCapability(new ResourceLocation(Reference.MOD_ID, "infusion_storage_network"),
                new DefaultCapabilityProvider<>(() -> Capabilities.INFUSION_HANDLER, channelInfusion));
        IPigmentHandler channelPigment = chemicalNetwork.getChannelExternal(Capabilities.PIGMENT_HANDLER, IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL);
        event.addCapability(new ResourceLocation(Reference.MOD_ID, "pigment_storage_network"),
                new DefaultCapabilityProvider<>(() -> Capabilities.PIGMENT_HANDLER, channelPigment));
        ISlurryHandler channelSlurry = chemicalNetwork.getChannelExternal(Capabilities.SLURRY_HANDLER, IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL);
        event.addCapability(new ResourceLocation(Reference.MOD_ID, "slurry_storage_network"),
                new DefaultCapabilityProvider<>(() -> Capabilities.SLURRY_HANDLER, channelSlurry));

        event.addFullNetworkListener(chemicalNetwork);
    }

}
