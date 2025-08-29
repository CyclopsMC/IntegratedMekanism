package org.cyclops.integratedmekanismics.part.aspect.listproxy;

import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.persist.nbt.INBTProvider;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeListProxyPositioned;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integratedmekanismics.core.CapabilityHelpers;

/**
 * A list proxy for a tank's capacities at a certain position.
 */
public class ValueTypeListProxyPositionedChemicalTankCapacities extends ValueTypeListProxyPositioned<ValueTypeLong, ValueTypeLong.ValueLong> implements INBTProvider {

    public ValueTypeListProxyPositionedChemicalTankCapacities(DimPos pos, Direction side) {
        super(MekanismValueTypeListProxyFactories.POSITIONED_CHEMICAL_TANK_CAPACITIES.getName(), ValueTypes.LONG, pos, side);
    }

    public ValueTypeListProxyPositionedChemicalTankCapacities() {
        this(null, null);
    }

    protected LazyOptional<IChemicalHandler<?, ?>> getTank() {
        return CapabilityHelpers.getFirstOf(PartPos.of(getPos(), getSide()), CapabilityHelpers.CHEMICAL_CAPABILITIES);
    }

    @Override
    public int getLength() {
        return getTank()
                .map(IChemicalHandler::getTanks)
                .orElse(0);
    }

    @Override
    public ValueTypeLong.ValueLong get(int index) {
        return ValueTypeLong.ValueLong.of(getTank()
                .map(chemicalHandler -> chemicalHandler.getTankCapacity(index))
                .orElse(0L));
    }
}
