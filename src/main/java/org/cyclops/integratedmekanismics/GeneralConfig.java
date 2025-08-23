package org.cyclops.integratedmekanismics;

import net.minecraftforge.fml.config.ModConfig;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.cyclopscore.config.extendedconfig.DummyConfig;

/**
 * A config with general options for this mod.
 * @author rubensworks
 *
 */
public class GeneralConfig extends DummyConfig {

    @ConfigurableProperty(category = "core", comment = "The maximum network chemical transfer rate.", isCommandable = true, minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int chemicalRateLimit = Integer.MAX_VALUE;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the chemical interface.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int interfaceChemicalBaseConsumption = 0;
    @ConfigurableProperty(category = "machine", comment = "The number that should be selected when clicking on a chemical in the storage terminal.", isCommandable = true)
    public static int guiStorageChemicalInitialQuantity = 100000;
    @ConfigurableProperty(category = "machine", comment = "The number that should be removed when right-clicking when a chemical is selected in the storage terminal.", isCommandable = true)
    public static int guiStorageChemicalIncrementalQuantity = 1000;

    public GeneralConfig() {
        super(IntegratedMekanismics._instance, "general");
    }

}
