package org.cyclops.integratedmekanism.part.aspect.listproxy;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.Direction;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.helper.IModHelpersNeoForge;
import org.cyclops.cyclopscore.persist.nbt.INBTProvider;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeListProxyPositioned;
import org.cyclops.integratedmekanism.value.MekanismValueTypes;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;

import java.util.Optional;

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
    public ValueObjectTypeChemicalStack.ValueChemicalStack get(int index) {
        return ValueObjectTypeChemicalStack.ValueChemicalStack.of(getTank()
                .<ChemicalStack>map(chemicalHandler -> chemicalHandler.getChemicalInTank(index))
                .orElse(ChemicalStack.EMPTY));
    }
}
