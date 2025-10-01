package org.cyclops.integratedmekanism.ingredient;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.neoforged.neoforge.capabilities.BaseCapability;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.capability.ICapabilityGetter;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageSlotted;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageWrapperHandler;
import org.cyclops.cyclopscore.ingredient.collection.FilteredIngredientCollectionIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

/**
 * Chemical storage wrapper handler for {@link IChemicalHandler}.
 * @author rubensworks
 */
public class IngredientComponentStorageWrapperHandlerChemicalStack<C>
        implements IIngredientComponentStorageWrapperHandler<ChemicalStack, Integer, IChemicalHandler, C> {

    private final IngredientComponent<ChemicalStack, Integer> ingredientComponent;
    private final BaseCapability<? extends IChemicalHandler, C> handlerCapability;

    public IngredientComponentStorageWrapperHandlerChemicalStack(IngredientComponent<ChemicalStack, Integer> ingredientComponent,
                                                                 BaseCapability<? extends IChemicalHandler, C> handlerCapability) {
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
    public IIngredientComponentStorage<ChemicalStack, Integer> wrapComponentStorage(IChemicalHandler storage) {
        return new ComponentStorageWrapper(getComponent(), storage);
    }

    @Override
    public IChemicalHandler wrapStorage(IIngredientComponentStorage<ChemicalStack, Integer> componentStorage) {
        if (componentStorage instanceof IIngredientComponentStorageSlotted<ChemicalStack, Integer> componentStorageSlotted) {
            return new ChemicalStorageWrapperSlotted(componentStorageSlotted);
        }
        return new ChemicalStorageWrapper(componentStorage);
    }

    @Override
    public Optional<IChemicalHandler> getStorage(ICapabilityGetter<C> capabilityProvider, @Nullable C context) {
        return Optional.ofNullable(capabilityProvider.getCapability(this.handlerCapability, context));
    }

    @Override
    public IngredientComponent<ChemicalStack, Integer> getComponent() {
        return this.ingredientComponent;
    }

    public static class ComponentStorageWrapper implements IIngredientComponentStorage<ChemicalStack, Integer> {

        private final IngredientComponent<ChemicalStack, Integer> ingredientComponent;
        private final IChemicalHandler storage;

        public ComponentStorageWrapper(IngredientComponent<ChemicalStack, Integer> ingredientComponent, IChemicalHandler storage) {
            this.ingredientComponent = ingredientComponent;
            this.storage = storage;
        }

        @Override
        public IngredientComponent<ChemicalStack, Integer> getComponent() {
            return this.ingredientComponent;
        }

        @Override
        public Iterator<ChemicalStack> iterator() {
            return new ChemicalHandlerChemicalStackIterator(storage);
        }

        @Override
        public Iterator<ChemicalStack> iterator(@Nonnull ChemicalStack prototype, Integer matchFlags) {
            if (getComponent().getMatcher().getAnyMatchCondition().equals(matchFlags)) {
                return iterator();
            }
            return new FilteredIngredientCollectionIterator<>(iterator(), getComponent().getMatcher(), prototype, matchFlags);
        }

        @Override
        public long getMaxQuantity() {
            long sum = 0;
            for (int i = 0; i < storage.getChemicalTanks(); i++) {
                sum = Math.addExact(sum, storage.getChemicalTankCapacity(i));
            }
            return sum;
        }

        @Override
        public ChemicalStack insert(@Nonnull ChemicalStack ingredient, boolean simulate) {
            // Don't continue if stack is empty
            if (ingredient.isEmpty()) {
                return getComponent().getMatcher().getEmptyInstance();
            }

            return storage.insertChemical(ingredient, simulateToChemicalAction(simulate));
        }

        @Override
        public ChemicalStack extract(@Nonnull ChemicalStack prototype, Integer matchFlags, boolean simulate) {
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
                ChemicalStack drainedSimulated = storage.extractChemical(prototype.getAmount(), Action.SIMULATE);
                if (drainedSimulated.isEmpty() || drainedSimulated.getAmount() != prototype.getAmount()) {
                    return getComponent().getMatcher().getEmptyInstance();
                }
                return simulate ? drainedSimulated : storage.extractChemical(prototype.getAmount(), Action.EXECUTE);
            }

            // In all other cases, we have to iterate over the tank contents,
            // and drain based on their contents.
            for (int i = 0; i < storage.getChemicalTanks(); i++) {
                ChemicalStack contents = storage.getChemicalInTank(i);
                if (!contents.isEmpty()
                        && ChemicalMatch.areStacksEqual(contents, prototype, matchFlags & ~ChemicalMatch.AMOUNT)) {
                    ChemicalStack toDrain = contents.copy();
                    toDrain.setAmount(prototype.getAmount());
                    ChemicalStack drained = storage.extractChemical(toDrain, simulateToChemicalAction(simulate));
                    if (ChemicalMatch.areStacksEqual(drained, prototype, matchFlags)) {
                        return drained;
                    }
                }
            }

            return getComponent().getMatcher().getEmptyInstance();
        }

        @Override
        public ChemicalStack extract(long maxQuantity, boolean simulate) {
            return storage.extractChemical(maxQuantity, simulateToChemicalAction(simulate));
        }
    }

    public static class ChemicalStorageWrapper implements IChemicalHandler {

        private final IIngredientComponentStorage<ChemicalStack, Integer> storage;

        public ChemicalStorageWrapper(IIngredientComponentStorage<ChemicalStack, Integer> storage) {
            this.storage = storage;
        }

        @Override
        public int getChemicalTanks() {
            // +1 so that at least one slot appears empty, for when others want to insert
            return Iterators.size(storage.iterator()) + 1;
        }

        @Nonnull
        @Override
        public ChemicalStack getChemicalInTank(int tank) {
            return Iterables.get(this.storage, tank, ChemicalStack.EMPTY);
        }

        @Override
        public long getChemicalTankCapacity(int tank) {
            return Long.MAX_VALUE;
        }

        @Override
        public void setChemicalInTank(int i, ChemicalStack stack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isValid(int tank, @Nonnull ChemicalStack stack) {
            return false;
        }

        @Override
        public ChemicalStack insertChemical(int i, ChemicalStack stack, Action action) {
            return stack;
        }

        @Override
        public ChemicalStack extractChemical(int i, long l, Action action) {
            return ChemicalStack.EMPTY;
        }

        @Override
        public ChemicalStack insertChemical(ChemicalStack resource, Action action) {
            return storage.insert(resource, chemicalActionToSimulate(action));
        }

        @Override
        public ChemicalStack extractChemical(ChemicalStack resource, Action action) {
            return storage.extract(resource, ChemicalMatch.EXACT, chemicalActionToSimulate(action));
        }

        @Override
        public ChemicalStack extractChemical(long maxDrain, Action action) {
            return storage.extract(maxDrain, chemicalActionToSimulate(action));
        }
    }

    public static class ChemicalStorageWrapperSlotted extends ChemicalStorageWrapper {

        private final IIngredientComponentStorageSlotted<ChemicalStack, Integer> storage;

        public ChemicalStorageWrapperSlotted(IIngredientComponentStorageSlotted<ChemicalStack, Integer> storage) {
            super(storage);
            this.storage = storage;
        }

        @Override
        public int getChemicalTanks() {
            return this.storage.getSlots();
        }

        @Nonnull
        @Override
        public ChemicalStack getChemicalInTank(int tank) {
            int tanks = getChemicalTanks();
            if (tank < 0 || tank >= tanks) {
                throw new IndexOutOfBoundsException("Tank " + tank + " not in valid range - [0," + tanks + ")");
            }
            return this.storage.getSlotContents(tank);
        }

        @Override
        public void setChemicalInTank(int i, ChemicalStack stack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isValid(int tank, @Nonnull ChemicalStack stack) {
            return true;
        }

        @Override
        public ChemicalStack insertChemical(int i, ChemicalStack stack, Action action) {
            return this.storage.insert(i, stack, chemicalActionToSimulate(action));
        }

        @Override
        public ChemicalStack extractChemical(int i, long l, Action action) {
            return this.storage.extract(i, l, chemicalActionToSimulate(action));
        }
    }

}
