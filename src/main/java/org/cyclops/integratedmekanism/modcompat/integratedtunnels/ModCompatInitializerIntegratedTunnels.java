package org.cyclops.integratedmekanism.modcompat.integratedtunnels;

import com.google.common.collect.Lists;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.modcompat.ICompatInitializer;
import org.cyclops.integrateddynamics.core.event.IntegratedDynamicsSetupEvent;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.aspect.MekanismTunnelsAspects;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.aspect.listproxy.MekanismTunnelsValueTypeListProxyFactories;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.part.PartTypesMekanismTunnels;

/**
 * @author rubensworks
 */
public class ModCompatInitializerIntegratedTunnels implements ICompatInitializer {
    @Override
    public void initialize() {

    }

    @Override
    public void initialize(IModBase mod) {
        ((ModBase<?>) mod).getModEventBus().addListener(this::onRegistriesCreate);
        ((ModBase<?>) mod).getModEventBus().addListener(this::onSetup);
    }

    public void onRegistriesCreate(NewRegistryEvent event) {
        PartTypesMekanismTunnels.load();
    }

    protected void onSetup(IntegratedDynamicsSetupEvent event) {
        MekanismTunnelsValueTypeListProxyFactories.load();

        // Inject aspects into ID parts
        AspectRegistry.getInstance().register(org.cyclops.integrateddynamics.core.part.PartTypes.NETWORK_READER, Lists.newArrayList(
                MekanismTunnelsAspects.Read.Chemical.LONG_COUNT,
                MekanismTunnelsAspects.Read.Chemical.LONG_COUNTMAX,
                MekanismTunnelsAspects.Read.Chemical.LIST_CHEMICALSTACKS,
                MekanismTunnelsAspects.Read.Chemical.OPERATOR_GETCHEMICALCOUNT,
                MekanismTunnelsAspects.Read.Chemical.INTEGER_INTERFACES
        ));
    }
}
