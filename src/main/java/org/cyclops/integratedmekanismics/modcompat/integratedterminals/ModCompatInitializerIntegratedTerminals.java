package org.cyclops.integratedmekanismics.modcompat.integratedterminals;

import mekanism.api.chemical.ChemicalStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherAdapter;
import org.cyclops.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherManager;
import org.cyclops.cyclopscore.modcompat.ICompatInitializer;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.integratedmekanismics.Reference;
import org.cyclops.integratedmekanismics.ingredient.IngredientComponentCapabilitiesMekanism;
import org.cyclops.integratedterminals.capability.ingredient.IngredientComponentTerminalStorageHandlerConfig;

/**
 * @author rubensworks
 */
public class ModCompatInitializerIntegratedTerminals implements ICompatInitializer {
    @Override
    public void initialize() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegistriesCreate);
    }

    public void onRegistriesCreate(NewRegistryEvent event) {
        IngredientComponentCapabilityAttacherManager attacherManager = new IngredientComponentCapabilityAttacherManager();

        // Views
        ResourceLocation capabilityIngredientComponentViewHandler = new ResourceLocation(Reference.MOD_ID, "view_handler");
        attacherManager.addAttacher(new IngredientComponentCapabilityAttacherAdapter<ChemicalStack<?>, Integer>(IngredientComponentCapabilitiesMekanism.INGREDIENT_CHEMICALSTACK_ID, capabilityIngredientComponentViewHandler) {
            @Override
            public ICapabilityProvider createCapabilityProvider(IngredientComponent<ChemicalStack<?>, Integer> ingredientComponent) {
                return new DefaultCapabilityProvider<>(() -> IngredientComponentTerminalStorageHandlerConfig.CAPABILITY,
                        new IngredientComponentTerminalStorageHandlerChemicalStack(ingredientComponent));
            }
        });
    }
}
