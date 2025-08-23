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
 * A part that can import chemicals.
 * @author rubensworks
 */
public class PartTypeImporterChemical extends PartTypeTunnelAspects<PartTypeImporterChemical, PartStateChemical<PartTypeImporterChemical>> {
    public PartTypeImporterChemical(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                MekanismAspects.Write.Chemical.BOOLEAN_IMPORT,
                MekanismAspects.Write.Chemical.LONG_IMPORT,
                MekanismAspects.Write.Chemical.CHEMICALSTACK_IMPORT,
                MekanismAspects.Write.Chemical.LIST_IMPORT,
                MekanismAspects.Write.Chemical.PREDICATE_IMPORT
        ));
    }

    @Override
    public ModBase getMod() {
        return IntegratedMekanismics._instance;
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
