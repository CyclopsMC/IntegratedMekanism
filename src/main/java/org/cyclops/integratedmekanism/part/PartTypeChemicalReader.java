package org.cyclops.integratedmekanism.part;

import com.google.common.collect.Lists;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.core.part.read.PartStateReaderBase;
import org.cyclops.integrateddynamics.core.part.read.PartTypeReadBase;
import org.cyclops.integratedmekanism.GeneralConfig;
import org.cyclops.integratedmekanism.IntegratedMekanism;
import org.cyclops.integratedmekanism.part.aspect.MekanismAspects;

/**
 * A chemical reader part.
 * @author rubensworks
 */
public class PartTypeChemicalReader extends PartTypeReadBase<PartTypeChemicalReader, PartStateReaderBase<PartTypeChemicalReader>> {

    public PartTypeChemicalReader(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.newArrayList(
                MekanismAspects.Read.Chemical.BOOLEAN_FULL,
                MekanismAspects.Read.Chemical.BOOLEAN_EMPTY,
                MekanismAspects.Read.Chemical.BOOLEAN_NONEMPTY,
                MekanismAspects.Read.Chemical.BOOLEAN_APPLICABLE,
                MekanismAspects.Read.Chemical.LONG_AMOUNT,
                MekanismAspects.Read.Chemical.LONG_AMOUNTTOTAL,
                MekanismAspects.Read.Chemical.LONG_CAPACITY,
                MekanismAspects.Read.Chemical.LONG_CAPACITYTOTAL,
                MekanismAspects.Read.Chemical.INTEGER_TANKS,
                MekanismAspects.Read.Chemical.DOUBLE_FILLRATIO,
                MekanismAspects.Read.Chemical.LIST_TANKCHEMICALS,
                MekanismAspects.Read.Chemical.LIST_TANKCAPACITIES,
                MekanismAspects.Read.Chemical.CHEMICALSTACK
        ));
    }

    @Override
    protected ModBase getMod() {
        return IntegratedMekanism._instance;
    }

    @Override
    public PartStateReaderBase<PartTypeChemicalReader> constructDefaultState() {
        return new PartStateReaderBase<>();
    }

    @Override
    public int getConsumptionRate(PartStateReaderBase<PartTypeChemicalReader> state) {
        return GeneralConfig.chemicalReaderBaseConsumption;
    }

}
