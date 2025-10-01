package org.cyclops.integratedmekanism.modcompat.integratedtunnels.part;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanism.network.IChemicalNetwork;
import org.cyclops.integratedtunnels.core.part.IPartTypeInterfacePositionedAddon;

/**
 * @author rubensworks
 */
public class ChemicalHandlerPartState implements IChemicalHandler {

    private final IPartTypeInterfacePositionedAddon.IState<IChemicalNetwork, IChemicalHandler, ?, ?> state;

    public ChemicalHandlerPartState(IPartTypeInterfacePositionedAddon.IState<IChemicalNetwork, IChemicalHandler, ?, ?> state) {
        this.state = state;
    }

    protected IChemicalHandler getChemicalHandler() {
        return (IChemicalHandler) state.getPositionedAddonsNetwork().getChannelExternal(Capabilities.CHEMICAL.block(), state.getChannel());
    }

    @Override
    public int getChemicalTanks() {
        if (!state.isNetworkAndPositionValid()) {
            return 0;
        }
        state.disablePosition();
        int ret = getChemicalHandler().getChemicalTanks();
        state.enablePosition();
        return ret;
    }

    @Override
    public ChemicalStack getChemicalInTank(int tank) {
        if (!state.isNetworkAndPositionValid()) {
            return getEmptyStack();
        }
        state.disablePosition();
        ChemicalStack ret = getChemicalHandler().getChemicalInTank(tank);
        state.enablePosition();
        return ret;
    }

    @Override
    public long getChemicalTankCapacity(int tank) {
        if (!state.isNetworkAndPositionValid()) {
            return 0;
        }
        state.disablePosition();
        long ret = getChemicalHandler().getChemicalTankCapacity(tank);
        state.enablePosition();
        return ret;
    }

    @Override
    public ChemicalStack extractChemical(int tank, long l, Action action) {
        if (!state.isNetworkAndPositionValid()) {
            return getEmptyStack();
        }
        state.disablePosition();
        ChemicalStack ret = getChemicalHandler().extractChemical(tank, l, action);
        state.enablePosition();
        return ret;
    }

    @Override
    public ChemicalStack insertChemical(int tank, ChemicalStack stack, Action action) {
        if (!state.isNetworkAndPositionValid()) {
            return stack;
        }
        state.disablePosition();
        ChemicalStack ret = getChemicalHandler().insertChemical(tank, stack, action);
        state.enablePosition();
        return ret;
    }

    @Override
    public boolean isValid(int tank, ChemicalStack stack) {
        if (!state.isNetworkAndPositionValid()) {
            return false;
        }
        state.disablePosition();
        boolean ret = getChemicalHandler().isValid(tank, stack);
        state.enablePosition();
        return ret;
    }

    @Override
    public void setChemicalInTank(int tank, ChemicalStack stack) {
        if (state.isNetworkAndPositionValid()) {
            state.disablePosition();
            getChemicalHandler().setChemicalInTank(tank, stack);
            state.enablePosition();
        }
    }

    @Override
    public ChemicalStack insertChemical(ChemicalStack stack, Action action) {
        if (!state.isNetworkAndPositionValid()) {
            return stack;
        }
        state.disablePosition();
        ChemicalStack ret = getChemicalHandler().insertChemical(stack, action);
        state.enablePosition();
        return ret;
    }

    @Override
    public ChemicalStack extractChemical(long amount, Action action) {
        if (!state.isNetworkAndPositionValid()) {
            return getEmptyStack();
        }
        state.disablePosition();
        ChemicalStack ret = getChemicalHandler().extractChemical(amount, action);
        state.enablePosition();
        return ret;
    }

    @Override
    public ChemicalStack extractChemical(ChemicalStack stack, Action action) {
        if (!state.isNetworkAndPositionValid()) {
            return getEmptyStack();
        }
        state.disablePosition();
        ChemicalStack ret = getChemicalHandler().extractChemical(stack, action);
        state.enablePosition();
        return ret;
    }

    public static ChemicalStack getEmptyStack() {
        return MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK.getMatcher().getEmptyInstance();
    }
}
