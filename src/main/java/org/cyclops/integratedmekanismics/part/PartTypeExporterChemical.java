package org.cyclops.integratedmekanismics.part;

import com.google.common.collect.Lists;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedmekanismics.GeneralConfig;
import org.cyclops.integratedmekanismics.IntegratedMekanismics;
import org.cyclops.integratedmekanismics.part.aspect.MekanismAspects;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspects;

/**
 * A part that can export chemicals.
 * @author rubensworks
 */
public class PartTypeExporterChemical extends PartTypeTunnelAspects<PartTypeExporterChemical, PartStateChemical<PartTypeExporterChemical>> {
    public PartTypeExporterChemical(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                MekanismAspects.Write.Chemical.BOOLEAN_EXPORT,
                MekanismAspects.Write.Chemical.LONG_EXPORT,
                MekanismAspects.Write.Chemical.CHEMICALSTACK_EXPORT,
                MekanismAspects.Write.Chemical.LIST_EXPORT,
                MekanismAspects.Write.Chemical.PREDICATE_EXPORT
        ));
    }

    @Override
    public ModBase getMod() {
        return IntegratedMekanismics._instance;
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
