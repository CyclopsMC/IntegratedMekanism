package org.cyclops.integratedmekanismics.ingredient;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageSlotted;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageWrapperHandler;
import org.cyclops.cyclopscore.ingredient.collection.FilteredIngredientCollectionIterator;
import org.cyclops.integratedmekanismics.network.ChemicalNetwork;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Objects;

/**
 * Chemical storage wrapper handler for {@link IChemicalHandler}.
 * @author rubensworks
 */
public abstract class IngredientComponentStorageWrapperHandlerChemicalStack<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, C extends IChemicalHandler<CHEMICAL, STACK>>
        implements IIngredientComponentStorageWrapperHandler<ChemicalStack<?>, Integer, C> {

    private final IngredientComponent<ChemicalStack<?>, Integer> ingredientComponent;
    private final Capability<C> handlerCapability;

    public IngredientComponentStorageWrapperHandlerChemicalStack(IngredientComponent<ChemicalStack<?>, Integer> ingredientComponent,
                                                                 Capability<C> handlerCapability) {
        this.ingredientComponent = Objects.requireNonNull(ingredientComponent);
        this.handlerCapability = handlerCapability;
    }

    public static Action simulateToChemicalAction(boolean simulate) {
        return simulate ? Action.SIMULATE : Action.EXECUTE;
    }

    public static boolean chemicalActionToSimulate(Action chemicalAction) {
        return chemicalAction.simulate();
    }

    @Override
    public IIngredientComponentStorage<ChemicalStack<?>, Integer> wrapComponentStorage(C storage) {
        return new ComponentStorageWrapper(getComponent(), storage);
    }

    @Override
    public LazyOptional<C> getStorage(ICapabilityProvider capabilityProvider, @Nullable Direction facing) {
        return capabilityProvider.getCapability(this.handlerCapability, facing).cast();
    }

    @Override
    public IngredientComponent<ChemicalStack<?>, Integer> getComponent() {
        return this.ingredientComponent;
    }

    public static class ComponentStorageWrapper<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, C extends IChemicalHandler<CHEMICAL, STACK>> implements IIngredientComponentStorage<STACK, Integer> {

        private final IngredientComponent<STACK, Integer> ingredientComponent;
        private final C storage;

        public ComponentStorageWrapper(IngredientComponent<STACK, Integer> ingredientComponent, C storage) {
            this.ingredientComponent = ingredientComponent;
            this.storage = storage;
        }

        @Override
        public IngredientComponent<STACK, Integer> getComponent() {
            return this.ingredientComponent;
        }

        @Override
        public Iterator<STACK> iterator() {
            return new ChemicalHandlerChemicalStackIterator<>(storage);
        }

        @Override
        public Iterator<STACK> iterator(@Nonnull STACK prototype, Integer matchFlags) {
            if (getComponent().getMatcher().getAnyMatchCondition().equals(matchFlags)) {
                return iterator();
            }
            return new FilteredIngredientCollectionIterator<>(iterator(), getComponent().getMatcher(), prototype, matchFlags);
        }

        @Override
        public long getMaxQuantity() {
            long sum = 0;
            for (int i = 0; i < storage.getTanks(); i++) {
                sum = Math.addExact(sum, storage.getTankCapacity(i));
            }
            return sum;
        }

        @Override
        public STACK insert(@Nonnull STACK ingredient, boolean simulate) {
            // Don't continue if stack is empty
            if (ingredient.isEmpty()) {
                return getComponent().getMatcher().getEmptyInstance();
            }

            return storage.insertChemical(ingredient, simulateToChemicalAction(simulate));
        }

        @Override
        public STACK extract(@Nonnull STACK prototype, Integer matchFlags, boolean simulate) {
            // Don't continue if stack is empty
            if (prototype.isEmpty()) {
                return getComponent().getMatcher().getEmptyInstance();
            }

            // Optimize if ANY condition
            if (matchFlags == ChemicalMatch.ANY) {
                // Drain as much as possible
                return storage.extractChemical(prototype.getAmount(), simulateToChemicalAction(simulate));
            }

            // Optimize if AMOUNT condition
            if (matchFlags == ChemicalMatch.AMOUNT) {
                // Drain the exact given amount
                STACK drainedSimulated = storage.extractChemical(prototype.getAmount(), Action.SIMULATE);
                if (drainedSimulated.isEmpty() || drainedSimulated.getAmount() != prototype.getAmount()) {
                    return getComponent().getMatcher().getEmptyInstance();
                }
                return simulate ? drainedSimulated : storage.extractChemical(prototype.getAmount(), Action.EXECUTE);
            }

            // In all other cases, we have to iterate over the tank contents,
            // and drain based on their contents.
            for (int i = 0; i < storage.getTanks(); i++) {
                STACK contents = storage.getChemicalInTank(i);
                if (!contents.isEmpty()
                        && ChemicalMatch.areStacksEqual(contents, prototype, matchFlags & ~ChemicalMatch.AMOUNT)) {
                    STACK toDrain = (STACK) contents.copy();
                    toDrain.setAmount(prototype.getAmount());
                    STACK drained = storage.extractChemical(toDrain, simulateToChemicalAction(simulate));
                    if (ChemicalMatch.areStacksEqual(drained, prototype, matchFlags)) {
                        return drained;
                    }
                }
            }

            return getComponent().getMatcher().getEmptyInstance();
        }

        @Override
        public STACK extract(long maxQuantity, boolean simulate) {
            return storage.extractChemical(maxQuantity, simulateToChemicalAction(simulate));
        }
    }

    public static abstract class ChemicalStorageWrapper<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> implements IChemicalHandler<CHEMICAL, STACK> {

        private final IIngredientComponentStorage<ChemicalStack<?>, Integer> storage;
        private final Capability<? extends IChemicalHandler<CHEMICAL, STACK>> capability; // TODO: this hack can be removed in 1.21 when Mekanism puts all chemicals in a single registry

        public ChemicalStorageWrapper(IIngredientComponentStorage<ChemicalStack<?>, Integer> storage, Capability<? extends IChemicalHandler<CHEMICAL, STACK>> capability) {
            this.storage = storage;
            this.capability = capability;
        }

        @Override
        public int getTanks() {
            // +1 so that at least one slot appears empty, for when others want to insert
            return Iterators.size(storage.iterator()) + 1;
        }

        @Nonnull
        @Override
        public STACK getChemicalInTank(int tank) {
            return (STACK) Iterables.get(this.storage, tank, getEmptyStack());
        }

        @Override
        public long getTankCapacity(int tank) {
            return Long.MAX_VALUE;
        }

        @Override
        public void setChemicalInTank(int i, STACK stack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isValid(int tank, @Nonnull STACK stack) {
            return false;
        }

        protected void beforeOperation() { // TODO: this hack can be removed in 1.21 when Mekanism puts all chemicals in a single registry
            ChemicalNetwork.ACTIVE_CAPABILITY = capability;
        }

        protected void afterOperation() { // TODO: this hack can be removed in 1.21 when Mekanism puts all chemicals in a single registry
            ChemicalNetwork.ACTIVE_CAPABILITY = null;
        }

        @Override
        public STACK insertChemical(int i, STACK stack, Action action) {
            return stack;
        }

        @Override
        public STACK extractChemical(int i, long l, Action action) {
            return getEmptyStack();
        }

        @Override
        public STACK insertChemical(STACK resource, Action action) {
            beforeOperation();
            STACK ret = (STACK) storage.insert(resource, chemicalActionToSimulate(action));
            afterOperation();
            return ret;
        }

        @Override
        public STACK extractChemical(STACK resource, Action action) {
            beforeOperation();
            STACK ret = (STACK) storage.extract(resource, ChemicalMatch.EXACT, chemicalActionToSimulate(action));
            afterOperation();
            return ret;
        }

        @Override
        public STACK extractChemical(long maxDrain, Action action) {
            beforeOperation();
            STACK ret = (STACK) storage.extract(maxDrain, chemicalActionToSimulate(action));
            afterOperation();
            return ret;
        }

        @Nonnull
        @Override
        public STACK getEmptyStack() {
            return (STACK) storage.getComponent().getMatcher().getEmptyInstance();
        }
    }

    public static abstract class ChemicalStorageWrapperSlotted<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> extends ChemicalStorageWrapper<CHEMICAL, STACK> {

        private final IIngredientComponentStorageSlotted<ChemicalStack<?>, Integer> storage;

        public ChemicalStorageWrapperSlotted(IIngredientComponentStorageSlotted<ChemicalStack<?>, Integer> storage, Capability<? extends IChemicalHandler<CHEMICAL, STACK>> capability) {
            super(storage, capability);
            this.storage = storage;
        }

        @Override
        public int getTanks() {
            return this.storage.getSlots();
        }

        @Nonnull
        @Override
        public STACK getChemicalInTank(int tank) {
            int tanks = getTanks();
            if (tank < 0 || tank >= tanks) {
                throw new IndexOutOfBoundsException("Tank " + tank + " not in valid range - [0," + tanks + ")");
            }
            beforeOperation();
            STACK ret = (STACK) this.storage.getSlotContents(tank);
            afterOperation();
            return ret;
        }

        @Override
        public void setChemicalInTank(int i, STACK stack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isValid(int tank, @Nonnull STACK stack) {
            return true;
        }

        @Override
        public STACK insertChemical(int i, STACK stack, Action action) {
            beforeOperation();
            STACK ret = (STACK) this.storage.insert(i, stack, chemicalActionToSimulate(action));
            afterOperation();
            return ret;
        }

        @Override
        public STACK extractChemical(int i, long l, Action action) {
            beforeOperation();
            STACK ret = (STACK) this.storage.extract(i, l, chemicalActionToSimulate(action));
            afterOperation();
            return ret;
        }
    }

    // --- Dummy storage wrapper extensions to make sure that the proper handler interfaces are implemented, because we can't do this via generics. ---


    public static final class StorageWrapperGasSlotted extends IngredientComponentStorageWrapperHandlerChemicalStack.ChemicalStorageWrapperSlotted<Gas, GasStack> implements IGasHandler {
        public StorageWrapperGasSlotted(IIngredientComponentStorageSlotted<ChemicalStack<?>, Integer> storage) {
            super(storage, Capabilities.GAS_HANDLER);
        }
    }
    public static final class StorageWrapperGas extends IngredientComponentStorageWrapperHandlerChemicalStack.ChemicalStorageWrapper<Gas, GasStack> implements IGasHandler {
        public StorageWrapperGas(IIngredientComponentStorage<ChemicalStack<?>, Integer> storage) {
            super(storage, Capabilities.GAS_HANDLER);
        }
    }
    public static final class StorageWrapperInfusionSlotted extends IngredientComponentStorageWrapperHandlerChemicalStack.ChemicalStorageWrapperSlotted<InfuseType, InfusionStack> implements IInfusionHandler {
        public StorageWrapperInfusionSlotted(IIngredientComponentStorageSlotted<ChemicalStack<?>, Integer> storage) {
            super(storage, Capabilities.INFUSION_HANDLER);
        }
    }
    public static final class StorageWrapperInfusion extends IngredientComponentStorageWrapperHandlerChemicalStack.ChemicalStorageWrapper<InfuseType, InfusionStack> implements IInfusionHandler {
        public StorageWrapperInfusion(IIngredientComponentStorage<ChemicalStack<?>, Integer> storage) {
            super(storage, Capabilities.INFUSION_HANDLER);
        }
    }
    public static final class StorageWrapperPigmentSlotted extends IngredientComponentStorageWrapperHandlerChemicalStack.ChemicalStorageWrapperSlotted<Pigment, PigmentStack> implements IPigmentHandler {
        public StorageWrapperPigmentSlotted(IIngredientComponentStorageSlotted<ChemicalStack<?>, Integer> storage) {
            super(storage, Capabilities.PIGMENT_HANDLER);
        }
    }
    public static final class StorageWrapperPigment extends IngredientComponentStorageWrapperHandlerChemicalStack.ChemicalStorageWrapper<Pigment, PigmentStack> implements IPigmentHandler {
        public StorageWrapperPigment(IIngredientComponentStorage<ChemicalStack<?>, Integer> storage) {
            super(storage, Capabilities.PIGMENT_HANDLER);
        }
    }
    public static final class StorageWrapperSlurrySlotted extends IngredientComponentStorageWrapperHandlerChemicalStack.ChemicalStorageWrapperSlotted<Slurry, SlurryStack> implements ISlurryHandler {
        public StorageWrapperSlurrySlotted(IIngredientComponentStorageSlotted<ChemicalStack<?>, Integer> storage) {
            super(storage, Capabilities.SLURRY_HANDLER);
        }
    }
    public static final class StorageWrapperSlurry extends IngredientComponentStorageWrapperHandlerChemicalStack.ChemicalStorageWrapper<Slurry, SlurryStack> implements ISlurryHandler {
        public StorageWrapperSlurry(IIngredientComponentStorage<ChemicalStack<?>, Integer> storage) {
            super(storage, Capabilities.SLURRY_HANDLER);
        }
    }

}
