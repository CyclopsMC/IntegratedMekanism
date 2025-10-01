package org.cyclops.integratedmekanism.modcompat.integratedrest.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.integratedmekanism.core.ChemicalHelpers;
import org.cyclops.integratedmekanism.value.MekanismValueTypes;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;
import org.cyclops.integratedrest.IntegratedRest;
import org.cyclops.integratedrest.api.json.IValueTypeJsonHandlerRegistry;
import org.cyclops.integratedrest.json.JsonUtil;
import org.cyclops.integratedrest.json.handler.CheckedValueTypeJsonHandlerBase;

/**
 * @author rubensworks
 */
public class MekanismValueTypeJsonHandlers {

    public static IValueTypeJsonHandlerRegistry REGISTRY = IntegratedRest._instance.getRegistryManager().getRegistry(IValueTypeJsonHandlerRegistry.class);

    public static void load() {
        REGISTRY.registerHandler(MekanismValueTypes.OBJECT_CHEMICALSTACK, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", "ValueChemical");
            if (!value.getRawValue().isEmpty()) {
                ChemicalStack chemicalStack = value.getRawValue();
                String name = MekanismAPI.CHEMICAL_REGISTRY.getKey(chemicalStack.getChemical()).toString();
                jsonObject.addProperty("chemical", JsonUtil.absolutizePath("registry/chemical/" + name));
                jsonObject.addProperty("chemicalName", name);
                jsonObject.addProperty("count", chemicalStack.getAmount());
            }
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueObjectTypeChemicalStack.ValueChemicalStack>() {
            @Override
            public ValueObjectTypeChemicalStack.ValueChemicalStack handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("@type") && ((JsonObject) jsonElement).get("@type").getAsString().equals("ValueChemical")) {
                    JsonObject jsonObject = (JsonObject) jsonElement;
                    if (!jsonObject.has("chemicalName")) {
                        return ValueObjectTypeChemicalStack.ValueChemicalStack.of(ChemicalStack.EMPTY);
                    } else {
                        Chemical chemicalType = MekanismAPI.CHEMICAL_REGISTRY.get(ResourceLocation.tryParse(jsonObject.get("typeName").getAsString()));
                        if (chemicalType != null) {
                            long count = ChemicalHelpers.BUCKET_VOLUME;
                            if (jsonObject.has("count")) {
                                count = jsonObject.get("count").getAsLong();
                            }
                            return ValueObjectTypeChemicalStack.ValueChemicalStack.of(chemicalType.getStack(count));
                        }
                    }
                }
                return null;
            }
        });
    }

}
