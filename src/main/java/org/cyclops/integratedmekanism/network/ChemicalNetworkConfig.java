package org.cyclops.integratedmekanism.network;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.cyclops.cyclopscore.config.extendedconfig.CapabilityConfig;
import org.cyclops.integratedmekanism.IntegratedMekanism;

/**
 * @author rubensworks
 */
public class ChemicalNetworkConfig extends CapabilityConfig<IChemicalNetwork> {

    public static Capability<IChemicalNetwork> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public ChemicalNetworkConfig() {
        super(
                IntegratedMekanism._instance,
                "chemicalNetwork",
                IChemicalNetwork.class
        );
    }
}
