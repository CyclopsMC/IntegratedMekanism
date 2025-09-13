package org.cyclops.integratedmekanism.ingredient;

import mekanism.api.chemical.ChemicalStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherAdapter;
import org.cyclops.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherManager;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.integrateddynamics.Reference;
import org.cyclops.integrateddynamics.capability.ingredient.IngredientComponentValueHandlerConfig;
import org.cyclops.integrateddynamics.capability.network.PositionedAddonsNetworkIngredientsHandlerConfig;
import org.cyclops.integratedmekanism.network.ChemicalNetworkConfig;

/**
 * @author rubensworks
 */
public class IngredientComponentCapabilitiesMekanism {

    public static ResourceLocation INGREDIENT_CHEMICALSTACK_ID = ResourceLocation.tryParse("mekanism:chemicalstack");

    public static void load() {
        IngredientComponentCapabilityAttacherManager attacherManager = new IngredientComponentCapabilityAttacherManager();

        // Value handler
        ResourceLocation capabilityIngredientComponentValueHandler = new ResourceLocation(Reference.MOD_ID, "ingredient_component_value_handler");
        attacherManager.addAttacher(new IngredientComponentCapabilityAttacherAdapter<ChemicalStack<?>, Integer>(INGREDIENT_CHEMICALSTACK_ID, capabilityIngredientComponentValueHandler) {
            @Override
            public ICapabilityProvider createCapabilityProvider(IngredientComponent<ChemicalStack<?>, Integer> ingredientComponent) {
                return new DefaultCapabilityProvider<>(() -> IngredientComponentValueHandlerConfig.CAPABILITY,
                        new IngredientComponentValueHandlerChemicalStack(ingredientComponent));
            }
        });

        // Network handler
        ResourceLocation networkHandler = new ResourceLocation(Reference.MOD_ID, "network_handler");
        attacherManager.addAttacher(new IngredientComponentCapabilityAttacherAdapter<ChemicalStack<?>, Integer>(INGREDIENT_CHEMICALSTACK_ID, networkHandler) {
            @Override
            public ICapabilityProvider createCapabilityProvider(IngredientComponent<ChemicalStack<?>, Integer> ingredientComponent) {
                return new DefaultCapabilityProvider<>(() -> PositionedAddonsNetworkIngredientsHandlerConfig.CAPABILITY,
                        (network) -> network.getCapability(ChemicalNetworkConfig.CAPABILITY));
            }
        });
    }
}
