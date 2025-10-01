package org.cyclops.integratedmekanism.modcompat.integratedterminals;

import mekanism.api.chemical.ChemicalStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherAdapter;
import org.cyclops.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherManager;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.modcompat.ICompatInitializer;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.integratedmekanism.ingredient.IngredientComponentCapabilitiesMekanism;
import org.cyclops.integratedterminals.Capabilities;
import org.cyclops.integratedterminals.api.ingredient.IIngredientComponentTerminalStorageHandler;

/**
 * @author rubensworks
 */
public class ModCompatInitializerIntegratedTerminals implements ICompatInitializer {
    @Override
    public void initialize() {

    }

    @Override
    public void initialize(IModBase mod) {
        ((ModBase<?>) mod).getModEventBus().addListener(this::onRegistriesCreate);
    }

    public void onRegistriesCreate(NewRegistryEvent event) {
        IngredientComponentCapabilityAttacherManager attacherManager = new IngredientComponentCapabilityAttacherManager();

        // Views
        attacherManager.addAttacher(new IngredientComponentCapabilityAttacherAdapter<ChemicalStack, Integer>(IngredientComponentCapabilitiesMekanism.INGREDIENT_CHEMICALSTACK_ID, Capabilities.IngredientComponentTerminalStorageHandler.INGREDIENT) {
            @Override
            public ICapabilityProvider<IngredientComponent<?, ?>, Void, IIngredientComponentTerminalStorageHandler<ChemicalStack, Integer>> createCapabilityProvider(IngredientComponent<ChemicalStack, Integer> ingredientComponent) {
                return new DefaultCapabilityProvider<>(new IngredientComponentTerminalStorageHandlerChemicalStack(ingredientComponent));
            }
        });
    }
}
