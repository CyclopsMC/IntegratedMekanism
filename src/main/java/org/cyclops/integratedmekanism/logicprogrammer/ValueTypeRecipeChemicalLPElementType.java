package org.cyclops.integratedmekanism.logicprogrammer;

import net.minecraft.resources.ResourceLocation;
import org.cyclops.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import org.cyclops.integrateddynamics.api.logicprogrammer.IValueTypeLogicProgrammerElement;
import org.cyclops.integratedmekanism.Reference;

import java.util.List;

/**
 * Value type element type for chemical recipes.
 * @author rubensworks
 */
public class ValueTypeRecipeChemicalLPElementType implements ILogicProgrammerElementType<IValueTypeLogicProgrammerElement> {

    @Override
    public IValueTypeLogicProgrammerElement getByName(ResourceLocation name) {
        return new ValueTypeRecipeChemicalLPElement();
    }

    @Override
    public ResourceLocation getName(IValueTypeLogicProgrammerElement element) {
        return getUniqueName();
    }

    @Override
    public ResourceLocation getUniqueName() {
        return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "valuetype_recipe_chemical");
    }

    @Override
    public List<IValueTypeLogicProgrammerElement> createElements() {
        return List.of(new ValueTypeRecipeChemicalLPElement());
    }

}
