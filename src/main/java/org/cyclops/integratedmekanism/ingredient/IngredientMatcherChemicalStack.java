package org.cyclops.integratedmekanism.ingredient;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.registries.ForgeRegistry;
import org.cyclops.commoncapabilities.api.ingredient.IIngredientMatcher;
import org.cyclops.integratedmekanism.core.ChemicalHelpers;

/**
 * Matcher for ChemicalStacks.
 * @author rubensworks
 */
public class IngredientMatcherChemicalStack implements IIngredientMatcher<ChemicalStack<?>, Integer> {

    @Override
    public boolean isInstance(Object object) {
        return object instanceof ChemicalStack;
    }

    @Override
    public Integer getAnyMatchCondition() {
        return ChemicalMatch.ANY;
    }

    @Override
    public Integer getExactMatchCondition() {
        return ChemicalMatch.EXACT;
    }

    @Override
    public Integer getExactMatchNoQuantityCondition() {
        return ChemicalMatch.TYPE;
    }

    @Override
    public Integer withCondition(Integer matchCondition, Integer with) {
        return matchCondition | with;
    }

    @Override
    public Integer withoutCondition(Integer matchCondition, Integer without) {
        return matchCondition & ~without;
    }

    @Override
    public boolean hasCondition(Integer matchCondition, Integer searchCondition) {
        return (matchCondition & searchCondition) > 0;
    }

    @Override
    public int hash(ChemicalStack<?> instance) {
        if (instance.isEmpty()) {
            return 0;
        }

        int code = 1;
        code = 31 * code + instance.getType().hashCode();
        code = 31 * code + Long.hashCode(instance.getAmount());
        return code;
    }

    @Override
    public ChemicalStack<?> copy(ChemicalStack<?> instance) {
        if (instance.isEmpty()) {
            return getEmptyInstance();
        }
        return instance.copy();
    }

    @Override
    public long getQuantity(ChemicalStack<?> instance) {
        return instance.getAmount();
    }

    @Override
    public boolean matches(ChemicalStack<?> a, ChemicalStack<?> b, Integer matchCondition) {
        return ChemicalMatch.areStacksEqual(a, b, matchCondition);
    }

    @Override
    public ChemicalStack<?> getEmptyInstance() {
        return GasStack.EMPTY;
    }

    @Override
    public boolean isEmpty(ChemicalStack<?> instance) {
        return instance.isEmpty();
    }

    @Override
    public ChemicalStack<?> withQuantity(ChemicalStack<?> instance, long quantity) {
        if (quantity == 0) {
            return getEmptyInstance();
        }
        if (instance.isEmpty()) {
            Chemical someType = ChemicalHelpers.getStackRegistry(instance).getValues().iterator().next();
            return someType.getStack(quantity);
        }
        if (instance.getAmount() == quantity) {
            return instance;
        }
        ChemicalStack<?> copy = instance.copy();
        copy.setAmount(quantity);
        return copy;
    }

    @Override
    public long getMaximumQuantity() {
        return Long.MAX_VALUE;
    }

    @Override
    public int conditionCompare(Integer a, Integer b) {
        return Integer.compare(a, b);
    }

    @Override
    public String localize(ChemicalStack<?> instance) {
        return instance.getTextComponent().getString();
    }

    @Override
    public MutableComponent getDisplayName(ChemicalStack<?> instance) {
        return instance.getTextComponent().plainCopy();
    }

    @Override
    public String toString(ChemicalStack<? extends Chemical> instance) {
        ForgeRegistry registry = ChemicalHelpers.getStackRegistry(instance);
        return String.format("%s %s", registry.getKey(instance.getType()), instance.getAmount());
    }

    @Override
    public int compare(ChemicalStack<?> o1, ChemicalStack<?> o2) {
        if (o1.isEmpty()) {
            if (o2.isEmpty()) {
                return 0;
            } else {
                return -1;
            }
        } else if (o2.isEmpty()) {
            return 1;
        } else if (o1.getType() == o2.getType()) {
            return (int) (o1.getAmount() - o2.getAmount());
        }
        return o1.getType().getRegistryName().compareTo(o2.getType().getRegistryName());
    }

}
