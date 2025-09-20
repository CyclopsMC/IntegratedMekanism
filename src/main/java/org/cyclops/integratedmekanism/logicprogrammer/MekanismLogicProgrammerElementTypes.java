package org.cyclops.integratedmekanism.logicprogrammer;

import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementTypeRegistry;

/**
 * @author rubensworks
 */
public class MekanismLogicProgrammerElementTypes {

    public static final ILogicProgrammerElementTypeRegistry REGISTRY = IntegratedDynamics._instance.getRegistryManager().getRegistry(ILogicProgrammerElementTypeRegistry.class);

    public static void load() {}

    public static final ValueTypeRecipeChemicalLPElementType VALUETYPE_RECIPE_CHEMICAL = REGISTRY.addType(new ValueTypeRecipeChemicalLPElementType());

}
