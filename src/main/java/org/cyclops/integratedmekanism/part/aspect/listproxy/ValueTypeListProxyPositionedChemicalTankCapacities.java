package org.cyclops.integratedmekanism.part.aspect.listproxy;

import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.Direction;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.helper.IModHelpersNeoForge;
import org.cyclops.cyclopscore.persist.nbt.INBTProvider;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeListProxyPositioned;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;

import java.util.Optional;

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

    protected Optional<IChemicalHandler> getTank() {
        return IModHelpersNeoForge.get().getCapabilityHelpers().getCapability(this.getPos(), this.getSide(), Capabilities.CHEMICAL.block());
    }

    @Override
    public int getLength() {
        return getTank()
                .map(IChemicalHandler::getChemicalTanks)
                .orElse(0);
    }

    @Override
    public ValueTypeLong.ValueLong get(int index) {
        return ValueTypeLong.ValueLong.of(getTank()
                .map(chemicalHandler -> chemicalHandler.getChemicalTankCapacity(index))
                .orElse(0L));
    }
}
