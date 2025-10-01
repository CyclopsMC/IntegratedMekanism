package org.cyclops.integratedmekanism.modcompat.integratedrest.request;

import com.google.gson.JsonObject;
import mekanism.api.chemical.Chemical;
import net.minecraft.core.Registry;
import org.cyclops.integratedmekanism.core.ChemicalHelpers;
import org.cyclops.integratedrest.http.request.handler.RegistryNamespacedRequestHandler;
import org.cyclops.integratedrest.json.JsonUtil;

/**
 * Request handler for registry/chemical requests.
 * @author rubensworks
 */
public class RegistryChemicalRequestHandler extends RegistryNamespacedRequestHandler<Chemical> {

    @Override
    protected Registry<Chemical> getRegistry() {
        return ChemicalHelpers.getStackRegistry();
    }

    @Override
    protected void handleElement(Chemical element, JsonObject jsonObject) {
        jsonObject.addProperty("@id", JsonUtil.absolutizePath("registry/chemical/" + JsonUtil.resourceLocationToPath(getRegistry().getKey(element))));
        jsonObject.addProperty("mod", JsonUtil.absolutizePath("registry/mod/" + getRegistry().getKey(element).getNamespace()));
        jsonObject.addProperty("unlocalizedName", element.getTranslationKey());
        jsonObject.addProperty("resourceLocation", getRegistry().getKey(element).toString());
    }

    @Override
    protected String getElementsName() {
        return "chemicals";
    }
}
