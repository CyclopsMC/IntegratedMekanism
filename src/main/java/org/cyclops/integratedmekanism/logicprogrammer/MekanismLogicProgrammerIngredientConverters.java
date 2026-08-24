package org.cyclops.integratedmekanism.logicprogrammer;

import mekanism.api.chemical.ChemicalStack;
import net.minecraft.world.item.ItemStack;
import org.cyclops.integrateddynamicscompat.modcompat.common.LogicProgrammerIngredientConverters;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;

/**
 * Makes chemicals from recipe viewers such as JEI, EMI and REI
 * draggable into the slots of the logic programmer,
 * by converting them into a chemical tank that holds them.
 *
 * @author rubensworks
 */
public class MekanismLogicProgrammerIngredientConverters {

    public static void load() {
        LogicProgrammerIngredientConverters.registerConverter(ChemicalStack.class,
                chemicalStack -> chemicalStack.isEmpty()
                        ? ItemStack.EMPTY : ValueObjectTypeChemicalStack.valueToItemStack(chemicalStack));
    }

}
