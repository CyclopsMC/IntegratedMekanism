package org.cyclops.integratedmekanism.ingredient;

import mekanism.api.chemical.ChemicalStack;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherAdapter;
import org.cyclops.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherManager;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.integrateddynamics.Capabilities;
import org.cyclops.integrateddynamics.api.ingredient.capability.IIngredientComponentValueHandler;
import org.cyclops.integrateddynamics.api.ingredient.capability.IPositionedAddonsNetworkIngredientsHandler;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;

import java.util.Optional;

/**
 * @author rubensworks
 */
public class IngredientComponentCapabilitiesMekanism {

    public static ResourceLocation INGREDIENT_CHEMICALSTACK_ID = ResourceLocation.tryParse("mekanism:chemicalstack");

    public static void load() {
        IngredientComponentCapabilityAttacherManager attacherManager = new IngredientComponentCapabilityAttacherManager();

        // Value handler
        attacherManager.addAttacher(new IngredientComponentCapabilityAttacherAdapter<ChemicalStack, Integer>(INGREDIENT_CHEMICALSTACK_ID, Capabilities.IngredientComponentValueHandler.INGREDIENT) {
            @Override
            public ICapabilityProvider<IngredientComponent<?, ?>, Void, IIngredientComponentValueHandler<ValueObjectTypeChemicalStack, ValueObjectTypeChemicalStack.ValueChemicalStack, ChemicalStack, Integer>> createCapabilityProvider(IngredientComponent<ChemicalStack, Integer> ingredientComponent) {
                return new DefaultCapabilityProvider<>(new IngredientComponentValueHandlerChemicalStack(ingredientComponent));
            }
        });

        // Network handler
        attacherManager.addAttacher(new IngredientComponentCapabilityAttacherAdapter<ChemicalStack, Integer>(INGREDIENT_CHEMICALSTACK_ID, Capabilities.PositionedAddonsNetworkIngredientsHandler.INGREDIENT) {
            @Override
            public ICapabilityProvider<IngredientComponent<ChemicalStack, Integer>, Void, IPositionedAddonsNetworkIngredientsHandler<ChemicalStack, Integer>> createCapabilityProvider(IngredientComponent<ChemicalStack, Integer> ingredientComponent) {
                return new DefaultCapabilityProvider<>((network) -> (Optional) network.getCapability(org.cyclops.integratedmekanism.Capabilities.ChemicalNetwork.NETWORK));
            }
        });
    }
}
