package org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.ItemStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageHandler;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Exposes the item storage of a QIO frequency.
 * @author rubensworks
 */
public class IngredientComponentStorageHandlerQIO implements IIngredientComponentStorageHandler {

    private final IngredientComponentStorageQIO storage;

    public IngredientComponentStorageHandlerQIO(IngredientComponentStorageQIO storage) {
        this.storage = storage;
    }

    @Override
    public Collection<IngredientComponent<?, ?>> getComponents() {
        return ImmutableList.of(IngredientComponent.ITEMSTACK);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T, M> IIngredientComponentStorage<T, M> getStorage(IngredientComponent<T, M> ingredientComponent) {
        if (ingredientComponent == IngredientComponent.ITEMSTACK) {
            return (IIngredientComponentStorage<T, M>) (IIngredientComponentStorage<ItemStack, Integer>) storage;
        }
        return null;
    }
}
