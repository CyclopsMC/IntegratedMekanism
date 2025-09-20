package org.cyclops.integratedmekanism.modcompat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.integrateddynamics.inventory.container.ContainerLogicProgrammer;
import org.cyclops.integrateddynamics.inventory.container.ContainerLogicProgrammerPortable;
import org.cyclops.integratedmekanism.Reference;

/**
 * @author rubensworks
 */
@JeiPlugin
public class JEIIntegratedMekanismConfig implements IModPlugin {
    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registry) {
        registry.addUniversalRecipeTransferHandler(new LogicProgrammerTransferHandlerRecipeChemical<>(ContainerLogicProgrammer.class));
        registry.addUniversalRecipeTransferHandler(new LogicProgrammerTransferHandlerRecipeChemical<>(ContainerLogicProgrammerPortable.class));
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Reference.MOD_ID, "main");
    }
}
