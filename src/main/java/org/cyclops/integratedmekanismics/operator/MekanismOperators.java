package org.cyclops.integratedmekanismics.operator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.attribute.GasAttributes;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraftforge.registries.tags.IReverseTag;
import org.cyclops.commoncapabilities.api.ingredient.IMixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.MixedIngredients;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.operator.IOperator;
import org.cyclops.integrateddynamics.api.evaluate.operator.IOperatorRegistry;
import org.cyclops.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import org.cyclops.integrateddynamics.core.evaluate.OperatorBuilders;
import org.cyclops.integrateddynamics.core.evaluate.operator.IterativeFunction;
import org.cyclops.integrateddynamics.core.evaluate.operator.OperatorBase;
import org.cyclops.integrateddynamics.core.evaluate.operator.OperatorRegistry;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;
import org.cyclops.integrateddynamics.core.ingredient.ExtendedIngredientsList;
import org.cyclops.integrateddynamics.core.ingredient.ExtendedIngredientsSingle;
import org.cyclops.integratedmekanismics.Reference;
import org.cyclops.integratedmekanismics.core.CapabilityHelpers;
import org.cyclops.integratedmekanismics.core.ChemicalHelpers;
import org.cyclops.integratedmekanismics.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanismics.value.MekanismValueTypes;
import org.cyclops.integratedmekanismics.value.ValueObjectTypeChemicalStack;

import java.util.Optional;

/**
 * @author rubensworks
 */
public class MekanismOperators {

    public static final IOperatorRegistry REGISTRY = constructRegistry();

    private static IOperatorRegistry constructRegistry() {
        // This also allows this registry to be used outside of a minecraft environment.
        if(MinecraftHelpers.isModdedEnvironment()) {
            return IntegratedDynamics._instance.getRegistryManager().getRegistry(IOperatorRegistry.class);
        } else {
            return OperatorRegistry.getInstance();
        }
    }

    public static void load() {}

    /**
     * ----------------------------------- ITEM STACK OPERATORS -----------------------------------
     */

    /**
     * If the given stack has a chemical.
     */
    public static final IOperator OBJECT_ITEMSTACK_ISCHEMICALSTACK = REGISTRY.register(OperatorBuilders.ITEMSTACK_1_SUFFIX_LONG
            .modId(Reference.MOD_ID)
            .output(ValueTypes.BOOLEAN)
            .symbol("is_chemicalstack").operatorName("ischemicalstack").interactName("isChemicalStack")
            .function(OperatorBuilders.FUNCTION_ITEMSTACK_TO_BOOLEAN.build(
                    itemStack -> !itemStack.isEmpty() && CapabilityHelpers.getChemicalHandler(itemStack).isPresent()
            )).build());

    /**
     * The chemicalstack from the stack
     */
    public static final IOperator OBJECT_ITEMSTACK_CHEMICALSTACK = REGISTRY.register(OperatorBuilders.ITEMSTACK_1_SUFFIX_LONG
            .modId(Reference.MOD_ID)
            .output(MekanismValueTypes.OBJECT_CHEMICALSTACK).symbolOperator("chemicalstack").interactName("chemicalStack")
            .function(variables -> {
                ValueObjectTypeItemStack.ValueItemStack a = variables.getValue(0, ValueTypes.OBJECT_ITEMSTACK);
                return ValueObjectTypeChemicalStack.ValueChemicalStack.of(!a.getRawValue().isEmpty() ?
                        CapabilityHelpers.getChemicalHandler(a.getRawValue())
                                .map(h -> h.getTanks() > 0 ? h.getChemicalInTank(0) : GasStack.EMPTY)
                                .orElse(GasStack.EMPTY) :
                        GasStack.EMPTY);
            }).build());

    /**
     * The capacity of the chemicalstack from the stack.
     */
    public static final IOperator OBJECT_ITEMSTACK_CHEMICALSTACKCAPACITY = REGISTRY.register(OperatorBuilders.ITEMSTACK_1_SUFFIX_LONG
            .modId(Reference.MOD_ID)
            .output(ValueTypes.LONG)
            .symbol("chemicalstack_capacity").operatorName("chemicalstackcapacity").interactName("chemicalCapacity")
            .function(MekanismOperatorBuilders.FUNCTION_ITEMSTACK_TO_LONG.build(
                    itemStack -> !itemStack.isEmpty() ?
                            CapabilityHelpers.getChemicalHandler(itemStack)
                                    .map(h -> h.getTanks() > 0 ? h.getTankCapacity(0) : 0)
                                    .orElse(0L):
                            0L
            )).build());

    /**
     * ----------------------------------- CHEMICAL STACK OBJECT OPERATORS -----------------------------------
     */

    /**
     * The amount of chemical in the chemicalstack
     */
    public static final IOperator OBJECT_CHEMICALSTACK_AMOUNT = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.LONG).symbolOperatorInteract("amount")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_LONG.build(
                    ChemicalStack::getAmount
            )).build());

    /**
     * The chemicalstack tint
     */
    public static final IOperator OBJECT_CHEMICALSTACK_TINT = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.INTEGER).symbolOperatorInteract("tint")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_INT.build(
                    ChemicalStack::getChemicalTint
            )).build());

    /**
     * If the chemicalstack is radioactive
     */
    public static final IOperator OBJECT_CHEMICALSTACK_ISRADIOACTIVE = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.BOOLEAN).symbolOperator("is_radioactive").interactName("isRadioactive")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_BOOLEAN.build(
                    chemicalStack -> Optional.ofNullable(chemicalStack.get(GasAttributes.Radiation.class)).map(a -> a.needsValidation() && a.getRadioactivity() > 0).orElse(false)
            )).build());

    /**
     * The chemicalstack radioactivity
     */
    public static final IOperator OBJECT_CHEMICALSTACK_RADIOACTIVITY = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.DOUBLE).symbolOperatorInteract("radioactivity")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_DOUBLE.build(
                    chemicalStack -> Optional.ofNullable(chemicalStack.get(GasAttributes.Radiation.class)).map(GasAttributes.Radiation::getRadioactivity).orElse(0D)
            )).build());

    /**
     * If the chemicalstack is a coolant
     */
    public static final IOperator OBJECT_CHEMICALSTACK_ISCOOLANT = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.BOOLEAN).symbolOperator("is_coolant").interactName("isCoolant")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_BOOLEAN.build(
                    chemicalStack -> chemicalStack.get(GasAttributes.CooledCoolant.class) != null || chemicalStack.get(GasAttributes.HeatedCoolant.class) != null
            )).build());

    /**
     * The chemicalstack thermal enthalpy
     */
    public static final IOperator OBJECT_CHEMICALSTACK_THERMALENTHALPY = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.DOUBLE).symbolOperator("thermal_enthalpy").interactName("thermalEnthalpy")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_DOUBLE.build(
                    chemicalStack -> Optional.<GasAttributes.Coolant>ofNullable(chemicalStack.get(GasAttributes.CooledCoolant.class))
                            .or(() -> Optional.ofNullable(chemicalStack.get(GasAttributes.HeatedCoolant.class)))
                            .map(GasAttributes.Coolant::getThermalEnthalpy)
                            .orElse(0D)
            )).build());

    /**
     * The chemicalstack conductivity
     */
    public static final IOperator OBJECT_CHEMICALSTACK_CONDUCTIVITY = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.DOUBLE).symbolOperatorInteract("conductivity")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_DOUBLE.build(
                    chemicalStack -> Optional.<GasAttributes.Coolant>ofNullable(chemicalStack.get(GasAttributes.CooledCoolant.class))
                            .or(() -> Optional.ofNullable(chemicalStack.get(GasAttributes.HeatedCoolant.class)))
                            .map(GasAttributes.Coolant::getConductivity)
                            .orElse(0D)
            )).build());

    /**
     * If the chemicalstack is a cooled coolant
     */
    public static final IOperator OBJECT_CHEMICALSTACK_ISCOOLEDCOOLANT = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.BOOLEAN).symbolOperator("is_cooled_coolant").interactName("isCooledCoolant")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_BOOLEAN.build(
                    chemicalStack -> chemicalStack.get(GasAttributes.CooledCoolant.class) != null
            )).build());

    /**
     * Get the chemical this chemical was cooled from
     */
    public static final IOperator OBJECT_CHEMICALSTACK_COOLEDCOOLANTOF = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(MekanismValueTypes.OBJECT_CHEMICALSTACK).symbolOperator("cooled_coolant_of").interactName("cooledCoolantOf")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_CHEMICALSTACK.build(
                    chemicalStack -> Optional.ofNullable(chemicalStack.get(GasAttributes.CooledCoolant.class))
                            .map(GasAttributes.CooledCoolant::getHeatedGas)
                            .map(gas -> new GasStack(gas, chemicalStack.getAmount()))
                            .orElse(GasStack.EMPTY)
            )).build());

    /**
     * If the chemicalstack is a heated coolant
     */
    public static final IOperator OBJECT_CHEMICALSTACK_ISHEATEDCOOLANT = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.BOOLEAN).symbolOperator("is_heated_coolant").interactName("isHeatedCoolant")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_BOOLEAN.build(
                    chemicalStack -> chemicalStack.get(GasAttributes.HeatedCoolant.class) != null
            )).build());

    /**
     * Get the chemical this chemical was heated from
     */
    public static final IOperator OBJECT_CHEMICALSTACK_HEATEDCOOLANTOF = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(MekanismValueTypes.OBJECT_CHEMICALSTACK).symbolOperator("heated_coolant_of").interactName("heatedCoolantOf")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_CHEMICALSTACK.build(
                    chemicalStack -> Optional.ofNullable(chemicalStack.get(GasAttributes.HeatedCoolant.class))
                            .map(GasAttributes.HeatedCoolant::getCooledGas)
                            .map(gas -> new GasStack(gas, chemicalStack.getAmount()))
                            .orElse(GasStack.EMPTY)
            )).build());

    /**
     * If the chemicalstack is fuel
     */
    public static final IOperator OBJECT_CHEMICALSTACK_ISFUEL = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.BOOLEAN).symbolOperator("is_fuel").interactName("isFuel")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_BOOLEAN.build(
                    chemicalStack -> chemicalStack.get(GasAttributes.Fuel.class) != null
            )).build());

    /**
     * The chemicalstack burn ticks
     */
    public static final IOperator OBJECT_CHEMICALSTACK_BURN_TICKS = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.INTEGER).symbolOperator("burn_ticks").interactName("burnTicks")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_INT.build(
                    chemicalStack -> Optional.ofNullable(chemicalStack.get(GasAttributes.Fuel.class))
                            .map(GasAttributes.Fuel::getBurnTicks)
                            .orElse(0)
            )).build());

    /**
     * The chemicalstack energy per tick
     */
    public static final IOperator OBJECT_CHEMICALSTACK_ENERGY_PER_TICK = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.LONG).symbolOperator("energy_per_tick").interactName("energyPerTick")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_LONG.build(
                    chemicalStack -> Optional.ofNullable(chemicalStack.get(GasAttributes.Fuel.class))
                            .map(f -> f.getEnergyPerTick().longValue())
                            .orElse(0L)
            )).build());

    /**
     * If the chemicalstack is a gas
     */
    public static final IOperator OBJECT_CHEMICALSTACK_ISGAS = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.BOOLEAN).symbolOperator("is_gas").interactName("isGas")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_BOOLEAN.build(
                    chemicalStack -> chemicalStack instanceof GasStack
            )).build());

    /**
     * If the chemicalstack is an infusion
     */
    public static final IOperator OBJECT_CHEMICALSTACK_ISINFUSED = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.BOOLEAN).symbolOperator("is_infusion").interactName("isInfusion")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_BOOLEAN.build(
                    chemicalStack -> chemicalStack instanceof InfusionStack
            )).build());

    /**
     * If the chemicalstack is a pigment
     */
    public static final IOperator OBJECT_CHEMICALSTACK_ISPIGMENT = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.BOOLEAN).symbolOperator("is_pigment").interactName("isPigment")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_BOOLEAN.build(
                    chemicalStack -> chemicalStack instanceof PigmentStack
            )).build());

    /**
     * If the chemicalstack is a slurry
     */
    public static final IOperator OBJECT_CHEMICALSTACK_ISSLURRY = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.BOOLEAN).symbolOperator("is_slurry").interactName("isSlurry")
            .function(MekanismOperatorBuilders.FUNCTION_CHEMICALSTACK_TO_BOOLEAN.build(
                    chemicalStack -> chemicalStack instanceof SlurryStack
            )).build());

    /**
     * If the chemical types of the two given chemicalstacks are equal
     */
    public static final IOperator OBJECT_CHEMICALSTACK_ISRAWCHEMICALEQUAL = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_2
            .output(ValueTypes.BOOLEAN).symbol("=Raw=").operatorName("israwchemicalequal").interactName("isRawEqual")
            .function(variables -> {
                ValueObjectTypeChemicalStack.ValueChemicalStack valueChemicalStack0 = variables.getValue(0, MekanismValueTypes.OBJECT_CHEMICALSTACK);
                ValueObjectTypeChemicalStack.ValueChemicalStack valueChemicalStack1 = variables.getValue(1, MekanismValueTypes.OBJECT_CHEMICALSTACK);
                return ValueTypeBoolean.ValueBoolean.of(valueChemicalStack0.getRawValue().isTypeEqual((ChemicalStack) valueChemicalStack1.getRawValue()));
            }).build());

    /**
     * The name of the mod owning this chemical
     */
    public static final IOperator OBJECT_CHEMICALSTACK_MODNAME = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG.output(ValueTypes.STRING)
            .symbolOperatorInteract("mod")
            .function(new IterativeFunction(Lists.newArrayList(
                    (OperatorBase.SafeVariablesGetter variables) -> {
                        ValueObjectTypeChemicalStack.ValueChemicalStack a = variables.getValue(0, MekanismValueTypes.OBJECT_CHEMICALSTACK);
                        return ChemicalHelpers.getStackRegistry(a.getRawValue()).getKey(a.getRawValue().getType());
                    },
                    OperatorBuilders.PROPAGATOR_RESOURCELOCATION_MODNAME
            ))).build());

    /**
     * Create a new chemicalstack with the given amount.
     */
    public static final IOperator OBJECT_CHEMICALSTACK_WITH_AMOUNT = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_2
            .inputTypes(MekanismValueTypes.OBJECT_CHEMICALSTACK, ValueTypes.LONG)
            .output(MekanismValueTypes.OBJECT_CHEMICALSTACK).symbolOperator("with_amount").interactName("withAmount")
            .function(variables -> {
                ValueObjectTypeChemicalStack.ValueChemicalStack valueChemicalStack = variables.getValue(0, MekanismValueTypes.OBJECT_CHEMICALSTACK);
                ValueTypeLong.ValueLong valueLong = variables.getValue(1, ValueTypes.LONG);
                ChemicalStack<?> chemicalStack = valueChemicalStack.getRawValue().copy();
                chemicalStack.setAmount(valueLong.getRawValue());
                return ValueObjectTypeChemicalStack.ValueChemicalStack.of(chemicalStack);
            }).build());

    /**
     * The tag entries of the given chemicalstack
     */
    public static final IOperator OBJECT_CHEMICALSTACK_TAG = REGISTRY.register(MekanismOperatorBuilders.CHEMICALSTACK_1_SUFFIX_LONG
            .output(ValueTypes.LIST)
            .symbol("chemical_tag_names").operatorName("tag").interactName("tags")
            .function(variables -> {
                ValueObjectTypeChemicalStack.ValueChemicalStack a = variables.getValue(0, MekanismValueTypes.OBJECT_CHEMICALSTACK);
                ImmutableList.Builder<ValueTypeString.ValueString> builder = ImmutableList.builder();
                if(!a.getRawValue().isEmpty()) {
                    Optional<IReverseTag<Chemical>> optionalReverseTag = ChemicalHelpers.getStackRegistry(a.getRawValue()).tags().getReverseTag(a.getRawValue().getType());
                    optionalReverseTag
                            .ifPresent(reverseTag -> reverseTag.getTagKeys()
                                    .forEach(owningTag -> builder.add(ValueTypeString.ValueString
                                            .of(owningTag.location().toString()))));
                }
                return ValueTypeList.ValueList.ofList(ValueTypes.STRING, builder.build());
            }).build());

    /**
     * Get a list of chemicalstacks that correspond to the given tag key.
     */
    public static final IOperator OBJECT_CHEMICALSTACK_TAG_STACKS = REGISTRY.register(OperatorBuilders.STRING_1_PREFIX
            .modId(Reference.MOD_ID)
            .output(ValueTypes.LIST)
            .symbol("chemical_tag_values").operatorName("chemicaltag").interactName("chemicalsByTag")
            .inputType(ValueTypes.STRING).renderPattern(IConfigRenderPattern.SUFFIX_1_LONG)
            .function(variables -> {
                ValueTypeString.ValueString a = variables.getValue(0, ValueTypes.STRING);
                ImmutableList.Builder<ValueObjectTypeChemicalStack.ValueChemicalStack> builder = ImmutableList.builder();
                if (!StringUtil.isNullOrEmpty(a.getRawValue())) {
                    try {
                        ChemicalHelpers.getChemicalTagValues(a.getRawValue())
                                .map(ValueObjectTypeChemicalStack.ValueChemicalStack::of)
                                .forEach(builder::add);
                    } catch (ResourceLocationException e) {
                        throw new EvaluationException(Component.translatable(e.getMessage()));
                    }
                }
                return ValueTypeList.ValueList.ofList(MekanismValueTypes.OBJECT_CHEMICALSTACK, builder.build());
            }).build());

    /**
     * ----------------------------------- INGREDIENTS OPERATORS -----------------------------------
     */

    /**
     * The list of chemicals
     */
    public static final IOperator INGREDIENTS_CHEMICALS = REGISTRY.register(OperatorBuilders.INGREDIENTS_1_PREFIX_LONG
            .modId(Reference.MOD_ID)
            .output(ValueTypes.LIST).operatorInteract("chemicals").symbol("Ingr.chemicals")
            .function(OperatorBuilders.createFunctionIngredientsList(() -> MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK))
            .build());

    /**
     * Set an ingredient chemical
     */
    public static final IOperator INGREDIENTS_WITH_CHEMICAL = REGISTRY.register(MekanismOperatorBuilders.INGREDIENTS_3_CHEMICALSTACK
            .operatorName("with_chemical").symbol("Ingr.with_chemical").interactName("withChemical")
            .function(variables -> {
                ValueObjectTypeIngredients.ValueIngredients value = variables.getValue(0, ValueTypes.OBJECT_INGREDIENTS);
                ValueTypeInteger.ValueInteger index = variables.getValue(1, ValueTypes.INTEGER);
                ValueObjectTypeChemicalStack.ValueChemicalStack chemicalStack = variables.getValue(2, MekanismValueTypes.OBJECT_CHEMICALSTACK);
                if (value.getRawValue().isEmpty()) {
                    value = ValueObjectTypeIngredients.ValueIngredients.of(new MixedIngredients(Maps.newIdentityHashMap()));
                }
                IMixedIngredients baseIngredients = value.getRawValue().get();
                return ValueObjectTypeIngredients.ValueIngredients.of(new ExtendedIngredientsSingle<>(baseIngredients,
                        index.getRawValue(), MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, chemicalStack.getRawValue()));
            }).build());

    /**
     * Set the list of chemicals
     */
    public static final IOperator INGREDIENTS_WITH_CHEMICALS = REGISTRY.register(OperatorBuilders.INGREDIENTS_2_LIST
            .modId(Reference.MOD_ID)
            .operatorName("with_chemicals").symbol("Ingr.with_chemicals").interactName("withChemicals")
            .function(variables -> {
                ValueObjectTypeIngredients.ValueIngredients valueIngredients = variables.getValue(0, ValueTypes.OBJECT_INGREDIENTS);
                ValueTypeList.ValueList<ValueObjectTypeChemicalStack, ValueObjectTypeChemicalStack.ValueChemicalStack> list = variables.getValue(1, ValueTypes.LIST);
                if (valueIngredients.getRawValue().isEmpty()) {
                    valueIngredients = ValueObjectTypeIngredients.ValueIngredients.of(new MixedIngredients(Maps.newIdentityHashMap()));
                }
                IMixedIngredients baseIngredients = valueIngredients.getRawValue().get();
                return ValueObjectTypeIngredients.ValueIngredients.of(new ExtendedIngredientsList<>(baseIngredients,
                        MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, OperatorBuilders.unwrapIngredientComponentList(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, list)));
            }).build());

}
