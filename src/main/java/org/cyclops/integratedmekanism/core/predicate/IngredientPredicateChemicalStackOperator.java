package org.cyclops.integratedmekanism.core.predicate;

import mekanism.api.chemical.ChemicalStack;
import net.minecraft.network.chat.MutableComponent;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.operator.IOperator;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueHelpers;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class IngredientPredicateChemicalStackOperator extends IngredientPredicate<ChemicalStack<?>, Integer> {
    private final IOperator predicate;
    private final PartTarget partTarget;

    public IngredientPredicateChemicalStackOperator(long amount, boolean exactAmount, IOperator predicate, PartTarget partTarget) {
        super(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, false, false, (int) amount, exactAmount);
        this.predicate = predicate;
        this.partTarget = partTarget;
    }

    @Override
    public boolean test(@Nullable ChemicalStack input) {
        ValueObjectTypeChemicalStack.ValueChemicalStack valueChemicalStack = ValueObjectTypeChemicalStack.ValueChemicalStack.of(input);
        try {
            IValue result = ValueHelpers.evaluateOperator(predicate, valueChemicalStack);
            ValueHelpers.validatePredicateOutput(predicate, result);
            return ((ValueTypeBoolean.ValueBoolean) result).getRawValue();
        } catch (EvaluationException e) {
            PartHelpers.PartStateHolder<?, ?> partData = PartHelpers.getPart(partTarget.getCenter());
            if (partData != null) {
                IPartStateWriter partState = (IPartStateWriter) partData.getState();
                partState.addError(partState.getActiveAspect(), (MutableComponent) e.getErrorMessage());
                partState.setDeactivated(true);
            }
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IngredientPredicateChemicalStackOperator)) {
            return false;
        }
        IngredientPredicateChemicalStackOperator that = (IngredientPredicateChemicalStackOperator) obj;
        return super.equals(obj)
                && this.predicate.equals(that.predicate)
                && this.partTarget.equals(that.partTarget);
    }

    @Override
    public int hashCode() {
        return super.hashCode()
                ^ this.predicate.hashCode()
                ^ this.partTarget.hashCode();
    }
}
