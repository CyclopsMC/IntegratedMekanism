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
 * A part that can export chemicals.
 * @author rubensworks
 */
public class PartTypeExporterChemical extends PartTypeTunnelAspects<PartTypeExporterChemical, PartStateChemical<PartTypeExporterChemical>> {
    public PartTypeExporterChemical(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                MekanismTunnelsAspects.Write.Chemical.BOOLEAN_EXPORT,
                MekanismTunnelsAspects.Write.Chemical.LONG_EXPORT,
                MekanismTunnelsAspects.Write.Chemical.CHEMICALSTACK_EXPORT,
                MekanismTunnelsAspects.Write.Chemical.LIST_EXPORT,
                MekanismTunnelsAspects.Write.Chemical.PREDICATE_EXPORT
        ));
    }

    @Override
    public ModBase getMod() {
        return IntegratedMekanism._instance;
    }

    @Override
    protected PartStateChemical<PartTypeExporterChemical> constructDefaultState() {
        return new PartStateChemical<PartTypeExporterChemical>(Aspects.REGISTRY.getWriteAspects(this).size(), false, true);
    }

    @Override
    public int getConsumptionRate(PartStateChemical<PartTypeExporterChemical> state) {
        return GeneralConfig.exporterChemicalBaseConsumption;
    }
}
