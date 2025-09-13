package org.cyclops.integratedmekanism.ingredient;

import com.google.common.collect.Lists;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponentCategoryType;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageSlotted;

/**
 * @author rubensworks
 */
public class MekanismIngredientComponents {

    public static IngredientComponent<ChemicalStack<?>, Integer> INGREDIENT_CHEMICALSTACK = new IngredientComponent<>(
            "mekanism:chemicalstack",
            new IngredientMatcherChemicalStack(),
            new IngredientSerializerChemicalStack(),
            Lists.newArrayList(
                    new IngredientComponentCategoryType<>(ResourceLocation.tryParse("mekanism:chemicalstack/type"),
                            Chemical.class, true, ChemicalStack::getType, ChemicalMatch.TYPE, false),
                    new IngredientComponentCategoryType<>(ResourceLocation.tryParse("mekanism:chemicalstack/amount"),
                            Long.class, false, ChemicalStack::getAmount, ChemicalMatch.AMOUNT, true)
            )
    ).setTranslationKey("recipecomponent.mekanism.chemicalstack");

    public static void registerStorageWrapperHandlers() {
        // TODO: This multi-typed hack should not be necessary anymore in Mekanisms's 1.21 API.
        // Add ingredient type capabilities
        INGREDIENT_CHEMICALSTACK.setStorageWrapperHandler(Capabilities.GAS_HANDLER, new IngredientComponentStorageWrapperHandlerChemicalStack<>(INGREDIENT_CHEMICALSTACK, Capabilities.GAS_HANDLER) {
            @Override
            public IGasHandler wrapStorage(IIngredientComponentStorage<ChemicalStack<?>, Integer> componentStorage) {
                if (componentStorage instanceof IIngredientComponentStorageSlotted<ChemicalStack<?>, Integer> componentStorageSlotted) {
                    return new StorageWrapperGasSlotted(componentStorageSlotted);
                }
                return new StorageWrapperGas(componentStorage);
            }
        });
        INGREDIENT_CHEMICALSTACK.setStorageWrapperHandler(Capabilities.INFUSION_HANDLER, new IngredientComponentStorageWrapperHandlerChemicalStack<>(INGREDIENT_CHEMICALSTACK, Capabilities.INFUSION_HANDLER) {
            @Override
            public IInfusionHandler wrapStorage(IIngredientComponentStorage<ChemicalStack<?>, Integer> componentStorage) {
                if (componentStorage instanceof IIngredientComponentStorageSlotted<ChemicalStack<?>, Integer> componentStorageSlotted) {
                    return new StorageWrapperInfusionSlotted(componentStorageSlotted);
                }
                return new StorageWrapperInfusion(componentStorage);
            }
        });
        INGREDIENT_CHEMICALSTACK.setStorageWrapperHandler(Capabilities.PIGMENT_HANDLER, new IngredientComponentStorageWrapperHandlerChemicalStack<>(INGREDIENT_CHEMICALSTACK, Capabilities.PIGMENT_HANDLER) {
            @Override
            public IPigmentHandler wrapStorage(IIngredientComponentStorage<ChemicalStack<?>, Integer> componentStorage) {
                if (componentStorage instanceof IIngredientComponentStorageSlotted<ChemicalStack<?>, Integer> componentStorageSlotted) {
                    return new StorageWrapperPigmentSlotted(componentStorageSlotted);
                }
                return new StorageWrapperPigment(componentStorage);
            }
        });
        INGREDIENT_CHEMICALSTACK.setStorageWrapperHandler(Capabilities.SLURRY_HANDLER, new IngredientComponentStorageWrapperHandlerChemicalStack<>(INGREDIENT_CHEMICALSTACK, Capabilities.SLURRY_HANDLER) {
            @Override
            public ISlurryHandler wrapStorage(IIngredientComponentStorage<ChemicalStack<?>, Integer> componentStorage) {
                if (componentStorage instanceof IIngredientComponentStorageSlotted<ChemicalStack<?>, Integer> componentStorageSlotted) {
                    return new StorageWrapperSlurrySlotted(componentStorageSlotted);
                }
                return new StorageWrapperSlurry(componentStorage);
            }
        });
    }

}
