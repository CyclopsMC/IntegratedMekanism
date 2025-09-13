package org.cyclops.integratedmekanism.modcompat.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedmekanism.GeneralConfig;
import org.cyclops.integratedmekanism.IntegratedMekanism;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.aspect.MekanismTunnelsAspects;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspectsWorld;
import org.cyclops.integratedtunnels.part.PartStateWorld;

/**
 * A part that can export chemicals to the world.
 * @author rubensworks
 */
public class PartTypeExporterWorldChemical extends PartTypeTunnelAspectsWorld<PartTypeExporterWorldChemical, PartStateWorld<PartTypeExporterWorldChemical>> {
    public PartTypeExporterWorldChemical(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                MekanismTunnelsAspects.Write.World.ENTITY_CHEMICAL_BOOLEAN_EXPORT,
                MekanismTunnelsAspects.Write.World.ENTITY_CHEMICAL_INTEGER_EXPORT,
                MekanismTunnelsAspects.Write.World.ENTITY_CHEMICAL_CHEMICALSTACK_EXPORT,
                MekanismTunnelsAspects.Write.World.ENTITY_CHEMICAL_LISTCHEMICALSTACK_EXPORT,
                MekanismTunnelsAspects.Write.World.ENTITY_CHEMICAL_PREDICATECHEMICALSTACK_EXPORT
        ));
    }

    @Override
    public ModBase getMod() {
        return IntegratedMekanism._instance;
    }

    @Override
    protected PartStateWorld<PartTypeExporterWorldChemical> constructDefaultState() {
        return new PartStateWorld<PartTypeExporterWorldChemical>(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(PartStateWorld<PartTypeExporterWorldChemical> state) {
        return state.hasVariable() ? GeneralConfig.exporterWorldChemicalBaseConsumptionEnabled : GeneralConfig.exporterWorldChemicalBaseConsumptionDisabled;
    }
}
