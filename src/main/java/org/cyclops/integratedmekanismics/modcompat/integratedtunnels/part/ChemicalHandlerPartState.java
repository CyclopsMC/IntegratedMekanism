package org.cyclops.integratedmekanismics.modcompat.integratedtunnels.part;

import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraftforge.common.capabilities.Capability;
import org.cyclops.integratedmekanismics.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanismics.network.IChemicalNetwork;
import org.cyclops.integratedtunnels.core.part.IPartTypeInterfacePositionedAddon;
import org.jetbrains.annotations.NotNull;

/**
 * @author rubensworks
 */
public class ChemicalHandlerPartState<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> implements IChemicalHandler<CHEMICAL, STACK> {

    private final IPartTypeInterfacePositionedAddon.IState<IChemicalNetwork, IChemicalHandler<?, ?>, ?, ?> state;
    private final Capability<? extends IChemicalHandler<?, ? extends STACK>> handlerCapability;

    public ChemicalHandlerPartState(IPartTypeInterfacePositionedAddon.IState<IChemicalNetwork, IChemicalHandler<?, ?>, ?, ?> state, Capability<? extends IChemicalHandler<?, ? extends STACK>> handlerCapability) {
        this.state = state;
        this.handlerCapability = handlerCapability;
    }

    protected IChemicalHandler<CHEMICAL, STACK> getChemicalHandler() {
        return (IChemicalHandler<CHEMICAL, STACK>) state.getPositionedAddonsNetwork().getChannelExternal(this.handlerCapability, state.getChannel());
    }

    @Override
    public int getTanks() {
        if (!state.isNetworkAndPositionValid()) {
            return 0;
        }
        state.disablePosition();
        int ret = getChemicalHandler().getTanks();
        state.enablePosition();
        return ret;
    }

    @Override
    public STACK getChemicalInTank(int tank) {
        if (!state.isNetworkAndPositionValid()) {
            return getEmptyStack();
        }
        state.disablePosition();
        STACK ret = getChemicalHandler().getChemicalInTank(tank);
        state.enablePosition();
        return ret;
    }

    @Override
    public long getTankCapacity(int tank) {
        if (!state.isNetworkAndPositionValid()) {
            return 0;
        }
        state.disablePosition();
        long ret = getChemicalHandler().getTankCapacity(tank);
        state.enablePosition();
        return ret;
    }

    @Override
    public STACK extractChemical(int tank, long l, Action action) {
        if (!state.isNetworkAndPositionValid()) {
            return getEmptyStack();
        }
        state.disablePosition();
        STACK ret = getChemicalHandler().extractChemical(tank, l, action);
        state.enablePosition();
        return ret;
    }

    @Override
    public STACK insertChemical(int tank, STACK stack, Action action) {
        if (!state.isNetworkAndPositionValid()) {
            return stack;
        }
        state.disablePosition();
        STACK ret = getChemicalHandler().insertChemical(tank, stack, action);
        state.enablePosition();
        return ret;
    }

    @Override
    public boolean isValid(int tank, STACK stack) {
        if (!state.isNetworkAndPositionValid()) {
            return false;
        }
        state.disablePosition();
        boolean ret = getChemicalHandler().isValid(tank, stack);
        state.enablePosition();
        return ret;
    }

    @Override
    public void setChemicalInTank(int tank, STACK stack) {
        if (state.isNetworkAndPositionValid()) {
            state.disablePosition();
            getChemicalHandler().setChemicalInTank(tank, stack);
            state.enablePosition();
        }
    }

    @Override
    public STACK insertChemical(STACK stack, Action action) {
        if (!state.isNetworkAndPositionValid()) {
            return stack;
        }
        state.disablePosition();
        STACK ret = getChemicalHandler().insertChemical(stack, action);
        state.enablePosition();
        return ret;
    }

    @Override
    public STACK extractChemical(long amount, Action action) {
        if (!state.isNetworkAndPositionValid()) {
            return getEmptyStack();
        }
        state.disablePosition();
        STACK ret = getChemicalHandler().extractChemical(amount, action);
        state.enablePosition();
        return ret;
    }

    @Override
    public STACK extractChemical(STACK stack, Action action) {
        if (!state.isNetworkAndPositionValid()) {
            return getEmptyStack();
        }
        state.disablePosition();
        STACK ret = getChemicalHandler().extractChemical(stack, action);
        state.enablePosition();
        return ret;
    }

    @Override
    public @NotNull STACK getEmptyStack() {
        return (STACK) MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK.getMatcher().getEmptyInstance();
    }
}
