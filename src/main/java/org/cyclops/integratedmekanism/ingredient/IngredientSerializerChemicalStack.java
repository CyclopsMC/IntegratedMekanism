package org.cyclops.integratedmekanism.ingredient;

import com.google.gson.JsonParseException;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.cyclops.commoncapabilities.api.ingredient.IIngredientSerializer;

/**
 * Serializer for ChemicalStacks.
 * @author rubensworks
 */
public class IngredientSerializerChemicalStack implements IIngredientSerializer<ChemicalStack, Integer> {

    @Override
    public Tag serializeInstance(HolderLookup.Provider lookupProvider, ChemicalStack chemicalStack) {
        return ChemicalStack.OPTIONAL_CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), chemicalStack).getOrThrow(JsonParseException::new);
    }

    @Override
    public ChemicalStack deserializeInstance(HolderLookup.Provider lookupProvider, Tag tag) throws IllegalArgumentException {
        return ChemicalStack.OPTIONAL_CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow(JsonParseException::new);
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
