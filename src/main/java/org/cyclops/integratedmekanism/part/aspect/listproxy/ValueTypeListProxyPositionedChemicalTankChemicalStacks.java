package org.cyclops.integratedmekanism.part.aspect.listproxy;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.persist.nbt.INBTProvider;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeListProxyPositioned;
import org.cyclops.integratedmekanism.core.CapabilityHelpers;
import org.cyclops.integratedmekanism.value.MekanismValueTypes;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;

/**
 * A list proxy for a tank's chemicalstacks at a certain position.
 */
public class ValueTypeListProxyPositionedChemicalTankChemicalStacks extends ValueTypeListProxyPositioned<ValueObjectTypeChemicalStack, ValueObjectTypeChemicalStack.ValueChemicalStack> implements INBTProvider {

    public ValueTypeListProxyPositionedChemicalTankChemicalStacks(DimPos pos, Direction side) {
        super(MekanismValueTypeListProxyFactories.POSITIONED_CHEMICAL_TANK_CHEMICAL_STACKS.getName(), MekanismValueTypes.OBJECT_CHEMICALSTACK, pos, side);
    }

    public ValueTypeListProxyPositionedChemicalTankChemicalStacks() {
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
    public ValueObjectTypeChemicalStack.ValueChemicalStack get(int index) {
        return ValueObjectTypeChemicalStack.ValueChemicalStack.of(getTank()
                .<ChemicalStack<?>>map(chemicalHandler -> chemicalHandler.getChemicalInTank(index))
                .orElse(GasStack.EMPTY));
    }
}
