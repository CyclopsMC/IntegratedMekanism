package org.cyclops.integratedmekanism.capability.ingredient;

import com.google.common.collect.ImmutableList;
import mekanism.api.inventory.qio.IQIOComponent;
import net.minecraft.world.item.ItemStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageHandler;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Exposes the item storage of a QIO frequency.
 * This takes precedence over the item handler of QIO blocks, which only exposes drive or crafting slots.
 * @author rubensworks
 */
public class IngredientComponentStorageHandlerQIO implements IIngredientComponentStorageHandler {

    private final IngredientComponentStorageQIO storage;

    public IngredientComponentStorageHandlerQIO(IQIOComponent component) {
        this.storage = new IngredientComponentStorageQIO(component);
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
