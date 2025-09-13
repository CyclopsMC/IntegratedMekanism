package org.cyclops.integratedmekanism.part.aspect.listproxy;

import net.minecraft.resources.ResourceLocation;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeListProxyFactories;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeListProxyNBTFactory;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;

/**
 * @author rubensworks
 */
public class MekanismValueTypeListProxyFactories {

    public static ValueTypeListProxyNBTFactory<ValueTypeLong, ValueTypeLong.ValueLong, ValueTypeListProxyPositionedChemicalTankCapacities> POSITIONED_CHEMICAL_TANK_CAPACITIES;
    public static ValueTypeListProxyNBTFactory<ValueObjectTypeChemicalStack, ValueObjectTypeChemicalStack.ValueChemicalStack, ValueTypeListProxyPositionedChemicalTankChemicalStacks> POSITIONED_CHEMICAL_TANK_CHEMICAL_STACKS;

    public static void load() {
        if (POSITIONED_CHEMICAL_TANK_CAPACITIES == null) {
            POSITIONED_CHEMICAL_TANK_CAPACITIES = ValueTypeListProxyFactories.REGISTRY.register(new ValueTypeListProxyNBTFactory<>(new ResourceLocation(Reference.MOD_ID, "positioned_chemical_tank_capacities"), ValueTypeListProxyPositionedChemicalTankCapacities.class));
            POSITIONED_CHEMICAL_TANK_CHEMICAL_STACKS = ValueTypeListProxyFactories.REGISTRY.register(new ValueTypeListProxyNBTFactory<>(new ResourceLocation(Reference.MOD_ID, "positioned_chemical_tank_chemicalstacks"), ValueTypeListProxyPositionedChemicalTankChemicalStacks.class));
        }
    }

}
