package org.cyclops.integratedmekanismics.core;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.registries.MekanismGases;
import net.minecraftforge.registries.ForgeRegistry;
import org.cyclops.integrateddynamics.api.evaluate.operator.IOperator;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integratedmekanismics.core.predicate.IngredientPredicateChemicalStackList;
import org.cyclops.integratedmekanismics.core.predicate.IngredientPredicateChemicalStackOperator;
import org.cyclops.integratedmekanismics.ingredient.ChemicalMatch;
import org.cyclops.integratedmekanismics.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanismics.value.ValueObjectTypeChemicalStack;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class ChemicalHelpers {

    public static ForgeRegistry<Chemical> getStackRegistry(ChemicalStack<?> instance) {
        if (instance instanceof GasStack) {
            return (ForgeRegistry) MekanismAPI.gasRegistry();
        } else if (instance instanceof InfusionStack) {
            return (ForgeRegistry) MekanismAPI.infuseTypeRegistry();
        } else if (instance instanceof PigmentStack) {
            return (ForgeRegistry) MekanismAPI.pigmentRegistry();
        } else if (instance instanceof SlurryStack) {
            return (ForgeRegistry) MekanismAPI.slurryRegistry();
        }
        throw new IllegalArgumentException("Unknown chemical stack: " + instance);
    }

    public static final IngredientPredicate<ChemicalStack<?>, Integer> MATCH_NONE = new IngredientPredicate<ChemicalStack<?>, Integer>(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, GasStack.EMPTY, ChemicalMatch.EXACT, false, true, 0, false) {
        @Override
        public boolean test(ChemicalStack input) {
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            return obj == ChemicalHelpers.MATCH_NONE;
        }

        @Override
        public int hashCode() {
            return 9991029;
        }
    };

    public static IngredientPredicate<ChemicalStack<?>, Integer> matchAll(final long amount, final boolean exactAmount) {
        return new IngredientPredicate<ChemicalStack<?>, Integer>(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, new GasStack(MekanismGases.HYDROGEN, amount), exactAmount ? ChemicalMatch.AMOUNT : ChemicalMatch.ANY, false, false, (int) amount, exactAmount) {
            @Override
            public boolean test(ChemicalStack input) {
                return true;
            }
        };
    }

    protected static int getChemicalStackMatchFlags(final boolean checkChemical, final boolean checkAmount) {
        int matchFlags = ChemicalMatch.ANY;
        if (checkChemical)  matchFlags = matchFlags | ChemicalMatch.TYPE;
        if (checkAmount) matchFlags = matchFlags | ChemicalMatch.AMOUNT;
        return matchFlags;
    }

    public static IngredientPredicate<ChemicalStack<?>, Integer> matchChemicalStack(final ChemicalStack<?> chemicalStack, final boolean checkChemical,
                                                                                    final boolean checkAmount,
                                                                                    final boolean blacklist, final boolean exactAmount) {
        return new IngredientPredicate<ChemicalStack<?>, Integer>(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, chemicalStack != null ? chemicalStack.copy() : null, getChemicalStackMatchFlags(checkChemical, checkAmount), blacklist, chemicalStack == null && !blacklist,
                chemicalStack == null ? 0 : (int) chemicalStack.getAmount(), exactAmount) {
            @Override
            public boolean test(@Nullable ChemicalStack input) {
                boolean result = areChemicalStackEqual(input, chemicalStack, checkChemical, checkAmount);
                if (blacklist) {
                    result = !result;
                }
                return result;
            }
        };
    }

    public static IngredientPredicate<ChemicalStack<?>, Integer> matchChemicalStacks(final IValueTypeListProxy<ValueObjectTypeChemicalStack, ValueObjectTypeChemicalStack.ValueChemicalStack> chemicalStacks,
                                                                                     final boolean checkChemical, final boolean checkAmount,
                                                                                     final boolean blacklist, final long amount, final boolean exactAmount) {
        return new IngredientPredicateChemicalStackList(blacklist, amount, exactAmount, chemicalStacks, getChemicalStackMatchFlags(checkChemical, checkAmount), checkChemical, checkAmount);
    }

    public static IngredientPredicate<ChemicalStack<?>, Integer> matchPredicate(final PartTarget partTarget, final IOperator predicate,
                                                                                final long amount, final boolean exactAmount) {
        return new IngredientPredicateChemicalStackOperator(amount, exactAmount, predicate, partTarget);
    }

    public static boolean areChemicalStackEqual(ChemicalStack<?> stackA, ChemicalStack<?> stackB,
                                                boolean checkChemical, boolean checkAmount) {
        if (stackA == null && stackB == null) return true;
        if (stackA != null && stackB != null) {
            if (checkAmount && stackA.getAmount() != stackB.getAmount()) return false;
            if (checkChemical && stackA.getType() != stackB.getType()) return false;
            return true;
        }
        return false;
    }

    /**
     * Helper function to get a copy of the given chemical stack with the given amount.
     * @param prototype A prototype chemical stack.
     * @param count A new amount.
     * @return A copy of the given chemical stack with the given count.
     */
    public static ChemicalStack<?> prototypeWithCount(ChemicalStack<?> prototype, long count) {
        if (prototype == null || prototype.getAmount() != count) {
            if (prototype == null) {
                return count == 0 ? null : new GasStack(MekanismGases.HYDROGEN, count);
            } else {
                prototype = prototype.copy();
                prototype.setAmount(count);
            }
        }
        return prototype;
    }

}
