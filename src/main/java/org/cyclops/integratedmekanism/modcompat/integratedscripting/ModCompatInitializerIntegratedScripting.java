package org.cyclops.integratedmekanism.modcompat.integratedscripting;

import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.modcompat.ICompatInitializer;
import org.cyclops.integrateddynamics.command.CommandTest;
import org.cyclops.integratedmekanism.modcompat.integratedscripting.translation.MekanismValueTranslators;
import org.cyclops.integratedmekanism.gametest.integration.TestValueTranslators;
import org.cyclops.integratedscripting.api.evaluate.translation.ValueTranslatorRegisterEvent;

import java.util.List;

/**
 * @author rubensworks
 */
public class ModCompatInitializerIntegratedScripting implements ICompatInitializer {
    @Override
    public void initialize() {

    }

    @Override
    public void initialize(IModBase mod) {
        ((ModBase<?>) mod).getModEventBus().addListener(this::onValueTranslatorRegister);
    }

    public void onValueTranslatorRegister(ValueTranslatorRegisterEvent event) {
        MekanismValueTranslators.load();

        // Add test classes
        CommandTest.CLASSES.addAll(List.of(
                TestValueTranslators.class.getName()
        ));
    }
}
