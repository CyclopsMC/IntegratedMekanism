package org.cyclops.integratedmekanismics.modcompat.integratedrest.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.cyclops.integratedmekanismics.core.ChemicalHelpers;
import org.cyclops.integratedmekanismics.value.MekanismValueTypes;
import org.cyclops.integratedmekanismics.value.ValueObjectTypeChemicalStack;
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
                ChemicalStack<?> chemicalStack = value.getRawValue();
                if (chemicalStack instanceof GasStack) {
                    jsonObject.addProperty("chemical", JsonUtil.absolutizePath("registry/chemical/mekanism:gas"));
                    jsonObject.addProperty("chemicalName", "mekanism:gas");
                } else if (chemicalStack instanceof InfusionStack) {
                    jsonObject.addProperty("chemical", JsonUtil.absolutizePath("registry/chemical/mekanism:infuse_type"));
                    jsonObject.addProperty("chemicalName", "mekanism:infuse_type");
                } else if (chemicalStack instanceof PigmentStack) {
                    jsonObject.addProperty("chemical", JsonUtil.absolutizePath("registry/chemical/mekanism:pigment"));
                    jsonObject.addProperty("chemicalName", "mekanism:pigment");
                } else if (chemicalStack instanceof SlurryStack) {
                    jsonObject.addProperty("chemical", JsonUtil.absolutizePath("registry/chemical/mekanism:slurry"));
                    jsonObject.addProperty("chemicalName", "mekanism:slurry");
                }
                jsonObject.addProperty("type", JsonUtil.absolutizePath("registry/fluid/" + chemicalStack.getType().getRegistryName().toString()));
                jsonObject.addProperty("typeName", chemicalStack.getType().getRegistryName().toString());
                jsonObject.addProperty("count", chemicalStack.getAmount());
            }
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueObjectTypeChemicalStack.ValueChemicalStack>() {
            @Override
            public ValueObjectTypeChemicalStack.ValueChemicalStack handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("@type") && ((JsonObject) jsonElement).get("@type").getAsString().equals("ValueChemical")) {
                    JsonObject jsonObject = (JsonObject) jsonElement;
                    if (!jsonObject.has("chemicalName") || !jsonObject.has("typeName")) {
                        return ValueObjectTypeChemicalStack.ValueChemicalStack.of(GasStack.EMPTY);
                    } else {
                        ForgeRegistry<Object> chemicalRegistry = RegistryManager.ACTIVE.getRegistry(ResourceLocation.tryParse(jsonObject.get("chemicalName").getAsString()));
                        if (chemicalRegistry != null) {
                            Chemical<?> chemicalType = (Chemical<?>) chemicalRegistry.getValue(ResourceLocation.tryParse(jsonObject.get("typeName").getAsString()));
                            if (chemicalType != null) {
                                long count = ChemicalHelpers.BUCKET_VOLUME;
                                if (jsonObject.has("count")) {
                                    count = jsonObject.get("count").getAsLong();
                                }
                                return ValueObjectTypeChemicalStack.ValueChemicalStack.of(chemicalType.getStack(count));
                            }
                        }
                    }
                }
                return null;
            }
        });

        // TODO: in 1.21.1 and above, also add a RegistryNamespacedRequestHandler for chemicals. See RequestHandlers in IREST.
    }

}
