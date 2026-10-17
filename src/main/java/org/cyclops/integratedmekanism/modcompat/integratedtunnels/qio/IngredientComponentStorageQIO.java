package org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import mekanism.api.Action;
import mekanism.api.inventory.qio.IQIOFrequency;
import mekanism.common.content.qio.QIOFrequency;
import net.minecraft.world.item.ItemStack;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ItemMatch;
import org.cyclops.commoncapabilities.api.ingredient.IIngredientMatcher;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * Exposes the contents of a QIO frequency as an item storage.
 * @author rubensworks
 */
public class IngredientComponentStorageQIO implements IIngredientComponentStorage<ItemStack, Integer> {

    private final Supplier<IQIOFrequency> frequencySupplier;

    public IngredientComponentStorageQIO(Supplier<IQIOFrequency> frequencySupplier) {
        this.frequencySupplier = frequencySupplier;
    }

    @Nullable
    protected IQIOFrequency getFrequency() {
        return frequencySupplier.get();
    }

    @Override
    public IngredientComponent<ItemStack, Integer> getComponent() {
        return IngredientComponent.ITEMSTACK;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        IQIOFrequency frequency = getFrequency();
        if (frequency == null) {
            return Collections.emptyIterator();
        }
        List<ItemStack> stacks = Lists.newArrayList();
        frequency.forAllHashedStored((type, amount) -> {
            // QIO amounts are longs, so split them over multiple stacks if needed
            while (amount > 0) {
                int count = (int) Math.min(amount, Integer.MAX_VALUE);
                stacks.add(type.createStack(count));
                amount -= count;
            }
        });
        return stacks.iterator();
    }

    @Override
    public Iterator<ItemStack> iterator(@Nonnull ItemStack prototype, Integer matchCondition) {
        IIngredientMatcher<ItemStack, Integer> matcher = getComponent().getMatcher();
        Iterator<ItemStack> it = isTypeExact(matchCondition) ? iteratorForType(prototype) : iterator();
        return Iterators.filter(it, stack -> matcher.matches(prototype, stack, matchCondition));
    }

    protected Iterator<ItemStack> iteratorForType(ItemStack prototype) {
        IQIOFrequency frequency = getFrequency();
        if (frequency == null || prototype.isEmpty()) {
            return Collections.emptyIterator();
        }
        long amount = frequency.getStored(prototype);
        List<ItemStack> stacks = Lists.newArrayList();
        while (amount > 0) {
            int count = (int) Math.min(amount, Integer.MAX_VALUE);
            stacks.add(prototype.copyWithCount(count));
            amount -= count;
        }
        return stacks.iterator();
    }

    @Override
    public long getMaxQuantity() {
        IQIOFrequency frequency = getFrequency();
        if (frequency == null) {
            return 0;
        }
        return frequency instanceof QIOFrequency qioFrequency ? qioFrequency.getTotalItemCountCapacity() : Long.MAX_VALUE;
    }

    @Override
    public ItemStack insert(@Nonnull ItemStack ingredient, boolean simulate) {
        IQIOFrequency frequency = getFrequency();
        if (frequency == null || ingredient.isEmpty()) {
            return ingredient;
        }
        long inserted = frequency.massInsert(ingredient, ingredient.getCount(), Action.get(!simulate));
        if (inserted <= 0) {
            return ingredient;
        }
        return ingredient.copyWithCount(ingredient.getCount() - (int) inserted);
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack prototype, Integer matchCondition, boolean simulate) {
        IQIOFrequency frequency = getFrequency();
        if (frequency == null || prototype.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // Determine the concrete type to extract
        ItemStack type;
        if (isTypeExact(matchCondition)) {
            type = prototype;
        } else {
            IIngredientMatcher<ItemStack, Integer> matcher = getComponent().getMatcher();
            int matchConditionNoQuantity = matchCondition & ~ItemMatch.STACKSIZE;
            Iterator<ItemStack> it = Iterators.filter(iterator(), stack -> matcher.matches(prototype, stack, matchConditionNoQuantity));
            if (!it.hasNext()) {
                return ItemStack.EMPTY;
            }
            type = it.next();
        }

        // Without STACKSIZE, the prototype count is the maximum to extract
        long amount = prototype.getCount();
        if ((matchCondition & ItemMatch.STACKSIZE) > 0
                && frequency.massExtract(type, amount, Action.SIMULATE) != amount) {
            return ItemStack.EMPTY;
        }
        long extracted = frequency.massExtract(type, amount, Action.get(!simulate));
        if (extracted <= 0) {
            return ItemStack.EMPTY;
        }
        return type.copyWithCount((int) extracted);
    }

    @Override
    public ItemStack extract(long maxQuantity, boolean simulate) {
        IQIOFrequency frequency = getFrequency();
        if (frequency == null || maxQuantity <= 0) {
            return ItemStack.EMPTY;
        }
        Iterator<ItemStack> it = iterator();
        if (!it.hasNext()) {
            return ItemStack.EMPTY;
        }
        ItemStack type = it.next();
        long extracted = frequency.massExtract(type, Math.min(maxQuantity, Integer.MAX_VALUE), Action.get(!simulate));
        if (extracted <= 0) {
            return ItemStack.EMPTY;
        }
        return type.copyWithCount((int) extracted);
    }

    /**
     * @param matchCondition An item match condition.
     * @return If the condition identifies a single QIO item type, so a direct lookup can be done.
     */
    protected static boolean isTypeExact(int matchCondition) {
        return (matchCondition & ItemMatch.ITEM) > 0 && (matchCondition & ItemMatch.DATA) > 0;
    }
}
