package org.cyclops.integratedmekanismics.modcompat.integratedscripting;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.cyclops.cyclopscore.modcompat.ICompatInitializer;
import org.cyclops.integrateddynamics.command.CommandTest;
import org.cyclops.integratedmekanismics.modcompat.integratedscripting.translation.MekanismValueTranslators;
import org.cyclops.integratedmekanismics.test.TestValueTranslators;
import org.cyclops.integratedscripting.api.evaluate.translation.ValueTranslatorRegisterEvent;

import java.util.List;

/**
 * @author rubensworks
 */
public class ModCompatInitializerIntegratedScripting implements ICompatInitializer {
    @Override
    public void initialize() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onValueTranslatorRegister);
    }

    public void onValueTranslatorRegister(ValueTranslatorRegisterEvent event) {
        MekanismValueTranslators.load();

        // Add test classes
        CommandTest.CLASSES.addAll(List.of(
                TestValueTranslators.class.getName()
        ));
    }
}
