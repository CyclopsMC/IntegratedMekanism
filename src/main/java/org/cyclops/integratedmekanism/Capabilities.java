package org.cyclops.integratedmekanism;

import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.integrateddynamics.api.network.NetworkCapability;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integrateddynamics.core.part.event.RegisterPartCapabilitiesEvent;
import org.cyclops.integratedmekanism.ingredient.IngredientComponentStorageWrapperHandlerChemicalStack;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanism.network.IChemicalNetwork;

/**
 * @author rubensworks
 */
public class Capabilities {
    public static final class ChemicalNetwork {
        public static final NetworkCapability<IChemicalNetwork> NETWORK = NetworkCapability.create(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "chemical_network"), IChemicalNetwork.class);
    }
    public static final class ChemicalHandler {
        public static final NetworkCapability<IChemicalHandler> NETWORK = NetworkCapability.create(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "chemical_handler"), IChemicalHandler.class);
        public static final PartCapability<IChemicalHandler> PART = PartCapability.create(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "chemical_handler"), IChemicalHandler.class);
    }

    public static void registerPartCapabilities(RegisterPartCapabilitiesEvent event) {
        event.register(mekanism.common.capabilities.Capabilities.CHEMICAL.block(), Capabilities.ChemicalHandler.PART);

        MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK.setStorageWrapperHandler(Capabilities.ChemicalHandler.PART, new IngredientComponentStorageWrapperHandlerChemicalStack<>(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, Capabilities.ChemicalHandler.PART));
    }
}
