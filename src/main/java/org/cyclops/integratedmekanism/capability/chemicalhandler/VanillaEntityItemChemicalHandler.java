package org.cyclops.integratedmekanism.capability.chemicalhandler;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.cyclops.commoncapabilities.modcompat.vanilla.capability.VanillaEntityItemCapabilityDelegator;

import javax.annotation.Nonnull;

/**
 * A chemical handler for entity items that have a chemical handler.
 * @author rubensworks
 */
public class VanillaEntityItemChemicalHandler extends VanillaEntityItemCapabilityDelegator<IChemicalHandler> implements IChemicalHandler {

    public VanillaEntityItemChemicalHandler(ItemEntity entity) {
        super(entity);
    }

    @Override
    protected ItemCapability<IChemicalHandler, Void> getCapabilityType() {
        return mekanism.common.capabilities.Capabilities.CHEMICAL.item();
    }

    @Override
    public int getChemicalTanks() {
        return getCapability()
                .map(IChemicalHandler::getChemicalTanks)
                .orElse(0);
    }

    @Nonnull
    @Override
    public ChemicalStack getChemicalInTank(int tank) {
        return getCapability()
                .map(chemicalHandler -> chemicalHandler.getChemicalInTank(tank))
                .orElse(ChemicalStack.EMPTY);
    }

    @Override
    public void setChemicalInTank(int i, ChemicalStack chemicalStack) {
        getCapability()
                .ifPresent(chemicalHandler -> chemicalHandler.setChemicalInTank(i, chemicalStack));
    }

    @Override
    public long getChemicalTankCapacity(int tank) {
        return getCapability()
                .map(chemicalHandler -> chemicalHandler.getChemicalTankCapacity(tank))
                .orElse(0L);
    }

    @Override
    public boolean isValid(int tank, @Nonnull ChemicalStack stack) {
        return getCapability()
                .map(chemicalHandler -> chemicalHandler.isValid(tank, stack))
                .orElse(false);
    }

    @Override
    public ChemicalStack insertChemical(int tank, ChemicalStack resource, Action action) {
        return getCapability()
                .map(chemicalHandler -> chemicalHandler.insertChemical(tank, resource, action))
                .orElse(resource);
    }

    @Override
    public ChemicalStack extractChemical(int tank, long maxDrain, Action action) {
        return getCapability()
                .map(chemicalHandler -> chemicalHandler.extractChemical(tank, maxDrain, action))
                .orElse(ChemicalStack.EMPTY);
    }
}
