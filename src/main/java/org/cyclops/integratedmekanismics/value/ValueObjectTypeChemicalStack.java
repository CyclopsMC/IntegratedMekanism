package org.cyclops.integratedmekanismics.value;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeNamed;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeNullable;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeUniquelyNamed;
import org.cyclops.integrateddynamics.api.evaluate.variable.ValueDeseralizationContext;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueBase;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeBase;
import org.cyclops.integrateddynamics.core.logicprogrammer.ValueTypeItemStackLPElement;
import org.cyclops.integrateddynamics.core.logicprogrammer.ValueTypeLPElementBase;
import org.cyclops.integratedmekanismics.Reference;
import org.cyclops.integratedmekanismics.core.CapabilityHelpers;
import org.cyclops.integratedmekanismics.core.ChemicalHelpers;
import org.cyclops.integratedmekanismics.ingredient.MekanismIngredientComponents;

import java.util.Objects;

/**
 * Value type with values that are fluidstacks.
 * @author rubensworks
 */
public class ValueObjectTypeChemicalStack extends ValueObjectTypeBase<ValueObjectTypeChemicalStack.ValueChemicalStack> implements
        IValueTypeNamed<ValueObjectTypeChemicalStack.ValueChemicalStack>,
        IValueTypeUniquelyNamed<ValueObjectTypeChemicalStack.ValueChemicalStack>,
        IValueTypeNullable<ValueObjectTypeChemicalStack.ValueChemicalStack> {

    public ValueObjectTypeChemicalStack() {
        super("chemicalstack", ValueObjectTypeChemicalStack.ValueChemicalStack.class);
    }

    @Override
    protected String getModId() {
        return Reference.MOD_ID;
    }

    @Override
    public ValueChemicalStack getDefault() {
        return ValueChemicalStack.of(GasStack.EMPTY);
    }

    @Override
    public MutableComponent toCompactString(ValueChemicalStack value) {
        ChemicalStack<?> chemicalStack = value.getRawValue();
        return !chemicalStack.isEmpty() ? ((MutableComponent) chemicalStack.getTextComponent()).append(String.format(" (%s mB)", chemicalStack.getAmount())) : Component.literal("");
    }

    @Override
    public Tag serialize(ValueChemicalStack value) {
        return MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK.getSerializer().serializeInstance(value.chemicalStack);
    }

    @Override
    public ValueChemicalStack deserialize(ValueDeseralizationContext valueDeseralizationContext, Tag value) {
        if (value instanceof CompoundTag) {
            return ValueChemicalStack.of(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK.getSerializer().deserializeInstance(value));
        } else {
            return null;
        }
    }

    @Override
    public String getName(ValueChemicalStack a) {
        return toCompactString(a).getString();
    }

    @Override
    public boolean isNull(ValueChemicalStack a) {
        return a.getRawValue().isEmpty();
    }

    @Override
    public ValueTypeLPElementBase createLogicProgrammerElement() {
        return new ValueTypeItemStackLPElement<>(this, new ValueTypeItemStackLPElement.IItemStackToValue<ValueObjectTypeChemicalStack.ValueChemicalStack>() {
            @Override
            public boolean isNullable() {
                return true;
            }

            @Override
            public Component validate(ItemStack itemStack) {
                return itemStack.isEmpty() ||
                        CapabilityHelpers.getChemicalHandler(itemStack).isPresent() ?
                        null :
                        Component.translatable("valuetype.integratedmekanismics.error.chemical.no_chemical");
            }

            @Override
            public ValueObjectTypeChemicalStack.ValueChemicalStack getValue(ItemStack itemStack) {
                return ValueObjectTypeChemicalStack.ValueChemicalStack.of(CapabilityHelpers.getChemicalHandler(itemStack)
                        .map(handler -> handler.getTanks() > 0 ? handler.getChemicalInTank(0) : handler.getEmptyStack())
                        .orElse(GasStack.EMPTY));
            }

            @Override
            public ItemStack getValueAsItemStack(ValueChemicalStack value) {
                ItemStack itemStack = new ItemStack(MekanismBlocks.BASIC_CHEMICAL_TANK);
                if (value.getRawValue() instanceof GasStack chemicalStack) {
                    itemStack.getCapability(Capabilities.GAS_HANDLER)
                            .ifPresent(handler ->  handler.insertChemical(chemicalStack, Action.EXECUTE));
                } else if (value.getRawValue() instanceof InfusionStack chemicalStack) {
                    itemStack.getCapability(Capabilities.INFUSION_HANDLER)
                            .ifPresent(handler ->  handler.insertChemical(chemicalStack, Action.EXECUTE));
                } else if (value.getRawValue() instanceof PigmentStack chemicalStack) {
                    itemStack.getCapability(Capabilities.PIGMENT_HANDLER)
                            .ifPresent(handler ->  handler.insertChemical(chemicalStack, Action.EXECUTE));
                } else if (value.getRawValue() instanceof SlurryStack chemicalStack) {
                    itemStack.getCapability(Capabilities.SLURRY_HANDLER)
                            .ifPresent(handler ->  handler.insertChemical(chemicalStack, Action.EXECUTE));
                }
                return itemStack;
            }
        });
    }

    @Override
    public String getUniqueName(ValueChemicalStack value) {
        ChemicalStack<?> chemicalStack = value.getRawValue();
        return !chemicalStack.isEmpty() ?
                String.format("%s %s", ChemicalHelpers.getStackRegistry(chemicalStack).getKey(chemicalStack.getType()), chemicalStack.getAmount()) : "";
    }

    public static class ValueChemicalStack extends ValueBase {

        private final ChemicalStack<?> chemicalStack;

        private ValueChemicalStack(ChemicalStack<?> itemStack) {
            super(MekanismValueTypes.OBJECT_CHEMICALSTACK);
            this.chemicalStack = Objects.requireNonNull(itemStack, "Attempted to create a ValueChemicalStack for a null ChemicalStack.");
        }

        public static ValueChemicalStack of(ChemicalStack<?> itemStack) {
            return new ValueChemicalStack(itemStack);
        }

        public ChemicalStack<?> getRawValue() {
            return chemicalStack;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ValueChemicalStack && this.getRawValue().isStackIdentical((ChemicalStack) ((ValueChemicalStack) o).getRawValue());
        }

        @Override
        public int hashCode() {
            return chemicalStack.hashCode();
        }

        @Override
        public String toString() {
            return "ValueChemicalStack{" +
                    "chemicalStack=" + chemicalStack +
                    '}';
        }
    }

}
