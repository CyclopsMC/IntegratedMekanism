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
 * A part that can import fluids from the world.
 * @author rubensworks
 */
public class PartTypeImporterWorldChemical extends PartTypeTunnelAspectsWorld<PartTypeImporterWorldChemical, PartStateWorld<PartTypeImporterWorldChemical>> {
    public PartTypeImporterWorldChemical(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                MekanismTunnelsAspects.Write.World.ENTITY_CHEMICAL_BOOLEAN_IMPORT,
                MekanismTunnelsAspects.Write.World.ENTITY_CHEMICAL_INTEGER_IMPORT,
                MekanismTunnelsAspects.Write.World.ENTITY_CHEMICAL_CHEMICALSTACK_IMPORT,
                MekanismTunnelsAspects.Write.World.ENTITY_CHEMICAL_LISTCHEMICALSTACK_IMPORT,
                MekanismTunnelsAspects.Write.World.ENTITY_CHEMICAL_PREDICATECHEMICALSTACK_IMPORT
        ));
    }

    @Override
    public ModBase getMod() {
        return IntegratedMekanism._instance;
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
