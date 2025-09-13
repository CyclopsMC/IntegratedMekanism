package org.cyclops.integratedmekanism.modcompat.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedmekanism.GeneralConfig;
import org.cyclops.integratedmekanism.IntegratedMekanism;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.aspect.MekanismTunnelsAspects;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspects;

/**
 * A part that can import chemicals.
 * @author rubensworks
 */
public class PartTypeImporterChemical extends PartTypeTunnelAspects<PartTypeImporterChemical, PartStateChemical<PartTypeImporterChemical>> {
    public PartTypeImporterChemical(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                MekanismTunnelsAspects.Write.Chemical.BOOLEAN_IMPORT,
                MekanismTunnelsAspects.Write.Chemical.LONG_IMPORT,
                MekanismTunnelsAspects.Write.Chemical.CHEMICALSTACK_IMPORT,
                MekanismTunnelsAspects.Write.Chemical.LIST_IMPORT,
                MekanismTunnelsAspects.Write.Chemical.PREDICATE_IMPORT
        ));
    }

    @Override
    public ModBase getMod() {
        return IntegratedMekanism._instance;
    }

    @Override
    protected PartStateChemical<PartTypeImporterChemical> constructDefaultState() {
        return new PartStateChemical<PartTypeImporterChemical>(Aspects.REGISTRY.getWriteAspects(this).size(), true, false);
    }

    @Override
    public int getConsumptionRate(PartStateChemical<PartTypeImporterChemical> state) {
        return GeneralConfig.importerChemicalBaseConsumption;
    }
}
