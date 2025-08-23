package org.cyclops.integratedmekanismics.part;

import com.google.common.collect.Lists;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedmekanismics.GeneralConfig;
import org.cyclops.integratedmekanismics.IntegratedMekanismics;
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
                // TODO
//                TunnelAspects.Write.World.ENTITY_FLUID_BOOLEAN_EXPORT,
//                TunnelAspects.Write.World.ENTITY_FLUID_INTEGER_EXPORT,
//                TunnelAspects.Write.World.ENTITY_FLUID_FLUIDSTACK_EXPORT,
//                TunnelAspects.Write.World.ENTITY_FLUID_LISTFLUIDSTACK_EXPORT,
//                TunnelAspects.Write.World.ENTITY_FLUID_PREDICATEFLUIDSTACK_EXPORT,
//                TunnelAspects.Write.World.ENTITY_FLUID_NBT_EXPORT
        ));
    }

    @Override
    public ModBase getMod() {
        return IntegratedMekanismics._instance;
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
