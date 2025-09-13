package org.cyclops.integratedmekanism.modcompat.integratedtunnels.aspect.operator;

import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integratedmekanism.network.ChemicalNetworkConfig;
import org.cyclops.integratedmekanism.value.MekanismValueTypes;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;
import org.cyclops.integratedtunnels.part.aspect.operator.PositionedOperatorIngredientIndex;

/**
 * @author rubensworks
 */
public class PositionedOperatorIngredientIndexChemical extends PositionedOperatorIngredientIndex<ChemicalStack<?>, Integer> {

    public PositionedOperatorIngredientIndexChemical() {
        this(null, Direction.NORTH, -1);
    }

    public PositionedOperatorIngredientIndexChemical(DimPos pos, Direction side, int channel) {
        super("countbychemical", new Function(), MekanismValueTypes.OBJECT_CHEMICALSTACK, ValueTypes.LONG, pos, side, channel);
    }

    @Override
    protected Capability<? extends IPositionedAddonsNetworkIngredients<ChemicalStack<?>, Integer>> getNetworkCapability() {
        return ChemicalNetworkConfig.CAPABILITY;
    }

    public static class Function extends PositionedOperatorIngredientIndex.Function<ChemicalStack<?>, Integer> {
        @Override
        public IValue evaluate(SafeVariablesGetter variables) throws EvaluationException {
            ValueObjectTypeChemicalStack.ValueChemicalStack chemicalStack = variables.getValue(0, MekanismValueTypes.OBJECT_CHEMICALSTACK);
            return ValueTypeLong.ValueLong.of(getOperator().getChannelIndex()
                    .map(index -> index.getQuantity(chemicalStack.getRawValue()))
                    .orElse(0L));
        }
    }
}
