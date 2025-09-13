package org.cyclops.integratedmekanism.core.predicate;

import com.google.common.collect.Iterables;
import mekanism.api.chemical.ChemicalStack;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import org.cyclops.integratedmekanism.core.ChemicalHelpers;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class IngredientPredicateChemicalStackList extends IngredientPredicate<ChemicalStack<?>, Integer> {
    private final boolean blacklist;
    private final IValueTypeListProxy<ValueObjectTypeChemicalStack, ValueObjectTypeChemicalStack.ValueChemicalStack> chemicalStacks;
    private final boolean checkChemical;
    private final boolean checkAmount;

    public IngredientPredicateChemicalStackList(boolean blacklist, long amount, boolean exactAmount, IValueTypeListProxy<ValueObjectTypeChemicalStack, ValueObjectTypeChemicalStack.ValueChemicalStack> chemicalStacks, int matchFlags, boolean checkChemical, boolean checkAmount) {
        super(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK,
                Iterables.transform(
                        Iterables.filter(chemicalStacks, chemicalStack -> !chemicalStack.getRawValue().isEmpty()),
                        stack -> ChemicalHelpers.prototypeWithCount(stack.getRawValue(), amount)), matchFlags, blacklist, false, (int) amount, exactAmount);
        this.blacklist = blacklist;
        this.chemicalStacks = chemicalStacks;
        this.checkChemical = checkChemical;
        this.checkAmount = checkAmount;
    }

    @Override
    public boolean test(@Nullable ChemicalStack input) {
        for (ValueObjectTypeChemicalStack.ValueChemicalStack chemicalStack : chemicalStacks) {
            if (!chemicalStack.getRawValue().isEmpty()
                    && ChemicalHelpers.areChemicalStackEqual(input, chemicalStack.getRawValue(), checkChemical, false)) { // TODO: hardcoded 'false' may have to be removed when restoring exact amount
                return !blacklist;
            }
        }
        return blacklist;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IngredientPredicateChemicalStackList)) {
            return false;
        }
        IngredientPredicateChemicalStackList that = (IngredientPredicateChemicalStackList) obj;
        return super.equals(obj)
                && this.blacklist == that.blacklist
                && this.checkChemical == that.checkChemical
                && this.checkAmount == that.checkAmount
                && this.chemicalStacks.equals(that.chemicalStacks);
    }

    @Override
    public int hashCode() {
        return super.hashCode()
                ^ (this.blacklist ? 1 : 0) << 1
                ^ (this.checkChemical ? 1 : 0) << 2
                ^ (this.checkAmount ? 1 : 0) << 3
                ^ this.chemicalStacks.hashCode();
    }
}
