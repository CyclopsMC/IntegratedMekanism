package org.cyclops.integratedmekanismics.modcompat.integratedtunnels;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import org.cyclops.cyclopscore.modcompat.ICompatInitializer;
import org.cyclops.integrateddynamics.core.event.IntegratedDynamicsSetupEvent;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integratedmekanismics.part.PartTypesMekanism;
import org.cyclops.integratedmekanismics.part.aspect.MekanismAspects;
import org.cyclops.integratedmekanismics.part.aspect.listproxy.MekanismValueTypeListProxyFactories;

/**
 * @author rubensworks
 */
public class ModCompatInitializerIntegratedTunnels implements ICompatInitializer {
    @Override
    public void initialize() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegistriesCreate);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSetup);
    }

    public void onRegistriesCreate(NewRegistryEvent event) {
        PartTypesMekanism.load();
    }

    protected void onSetup(IntegratedDynamicsSetupEvent event) {
        MekanismValueTypeListProxyFactories.load();

        // Inject aspects into ID parts
        AspectRegistry.getInstance().register(org.cyclops.integrateddynamics.core.part.PartTypes.NETWORK_READER, Lists.newArrayList(
                MekanismAspects.Read.Chemical.LONG_COUNT,
                MekanismAspects.Read.Chemical.LONG_COUNTMAX,
                MekanismAspects.Read.Chemical.LIST_CHEMICALSTACKS,
                MekanismAspects.Read.Chemical.OPERATOR_GETCHEMICALCOUNT,
                MekanismAspects.Read.Chemical.INTEGER_INTERFACES
        ));
    }
}
