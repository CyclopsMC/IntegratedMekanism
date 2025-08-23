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
 * A part that can import fluids from the world.
 * @author rubensworks
 */
public class PartTypeImporterWorldChemical extends PartTypeTunnelAspectsWorld<PartTypeImporterWorldChemical, PartStateWorld<PartTypeImporterWorldChemical>> {
    public PartTypeImporterWorldChemical(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                // TODO
//                TunnelAspects.Write.World.ENTITY_FLUID_BOOLEAN_IMPORT,
//                TunnelAspects.Write.World.ENTITY_FLUID_INTEGER_IMPORT,
//                TunnelAspects.Write.World.ENTITY_FLUID_FLUIDSTACK_IMPORT,
//                TunnelAspects.Write.World.ENTITY_FLUID_LISTFLUIDSTACK_IMPORT,
//                TunnelAspects.Write.World.ENTITY_FLUID_PREDICATEFLUIDSTACK_IMPORT,
//                TunnelAspects.Write.World.ENTITY_FLUID_NBT_IMPORT
        ));
    }

    @Override
    public ModBase getMod() {
        return IntegratedMekanismics._instance;
    }

    @Override
    protected PartStateWorld<PartTypeImporterWorldChemical> constructDefaultState() {
        return new PartStateWorld<PartTypeImporterWorldChemical>(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(PartStateWorld<PartTypeImporterWorldChemical> state) {
        return state.hasVariable() ? GeneralConfig.importerWorldChemicalBaseConsumptionEnabled : GeneralConfig.importerWorldChemicalBaseConsumptionDisabled;
    }
}
