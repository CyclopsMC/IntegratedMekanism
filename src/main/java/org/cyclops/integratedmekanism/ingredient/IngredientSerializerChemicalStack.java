package org.cyclops.integratedmekanism.ingredient;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.cyclops.commoncapabilities.api.ingredient.IIngredientSerializer;

/**
 * Serializer for ChemicalStacks.
 * @author rubensworks
 */
public class IngredientSerializerChemicalStack implements IIngredientSerializer<ChemicalStack<?>, Integer> {

    @Override
    public Tag serializeInstance(ChemicalStack<?> chemicalStack) {
        CompoundTag tag = new CompoundTag();
        if (chemicalStack instanceof GasStack) {
            tag.putString("chemical", "mekanism:gas");
        } else if (chemicalStack instanceof InfusionStack) {
            tag.putString("chemical", "mekanism:infuse_type");
        } else if (chemicalStack instanceof PigmentStack) {
            tag.putString("chemical", "mekanism:pigment");
        } else if (chemicalStack instanceof SlurryStack) {
            tag.putString("chemical", "mekanism:slurry");
        }
        tag.putString("type", chemicalStack.getType().getRegistryName().toString());
        tag.putLong("amount", chemicalStack.getAmount());
        return tag;
    }

    @Override
    public ChemicalStack<?> deserializeInstance(Tag tag) throws IllegalArgumentException {
        if (!(tag instanceof CompoundTag compoundTag)) {
            throw new IllegalArgumentException("This deserializer only accepts CompoundTag");
        }
        ForgeRegistry<Object> chemicalRegistry = RegistryManager.ACTIVE.getRegistry(ResourceLocation.tryParse(compoundTag.getString("chemical")));
        Chemical<?> type = (Chemical<?>) chemicalRegistry.getValue(ResourceLocation.tryParse(compoundTag.getString("type")));
        return type.getStack(compoundTag.getLong("amount"));
    }

    @Override
    public Tag serializeCondition(Integer matchCondition) {
        return IntTag.valueOf(matchCondition);
    }

    @Override
    public Integer deserializeCondition(Tag tag) throws IllegalArgumentException {
        if (!(tag instanceof IntTag)) {
            throw new IllegalArgumentException("This deserializer only accepts IntTag");
        }
        return ((IntTag) tag).getAsInt();
    }
}
