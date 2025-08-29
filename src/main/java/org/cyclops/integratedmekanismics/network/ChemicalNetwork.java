package org.cyclops.integratedmekanismics.network;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Pair;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageWrapperHandler;
import org.cyclops.commoncapabilities.api.ingredient.storage.IngredientComponentStorageEmpty;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.helper.BlockEntityHelpers;
import org.cyclops.cyclopscore.ingredient.storage.IngredientComponentStorageComposite;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.network.PositionedAddonsNetworkIngredients;
import org.cyclops.integratedmekanismics.GeneralConfig;
import org.cyclops.integratedmekanismics.core.CapabilityHelpers;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * @author rubensworks
 */
public class ChemicalNetwork extends PositionedAddonsNetworkIngredients<ChemicalStack<?>, Integer> implements IChemicalNetwork {

    // TODO: this hack can be removed in 1.21 when Mekanism puts all chemicals in a single registry
    public static Capability<? extends IChemicalHandler<?, ?>> ACTIVE_CAPABILITY = null;
    private final LoadingCache<Pair<Capability<? extends IChemicalHandler<?, ?>>,  PartPos>, IIngredientComponentStorage<ChemicalStack<?>, Integer>> cacheStorageChemicals;

    public ChemicalNetwork(IngredientComponent<ChemicalStack<?>, Integer> component) {
        super(component);
        this.cacheStorageChemicals = CacheBuilder.newBuilder().build(new CacheLoader<Pair<Capability<? extends IChemicalHandler<?, ?>>,  PartPos>, IIngredientComponentStorage<ChemicalStack<?>, Integer>>() {
            public IIngredientComponentStorage<ChemicalStack<?>, Integer> load(Pair<Capability<? extends IChemicalHandler<?, ?>>,  PartPos> key) {
                IIngredientComponentStorage<ChemicalStack<?>, Integer> storage = ChemicalNetwork.this.getPositionedStorageUnsafe(key.getLeft(), key.getRight());
                return (IIngredientComponentStorage<ChemicalStack<?>, Integer>)(storage == null ? new IngredientComponentStorageEmpty(ChemicalNetwork.this.getComponent()) : storage);
            }
        });
    }

    @Override
    public long getRateLimit() {
        return GeneralConfig.chemicalRateLimit;
    }

    // TODO: this hack can be removed in 1.21 when Mekanism puts all chemicals in a single registry
    public @Nullable IIngredientComponentStorage<ChemicalStack<?>, Integer> getPositionedStorageUnsafe(Capability<? extends IChemicalHandler<?, ?>> capability, PartPos pos) {
        DimPos dimPos = pos.getPos();
        Level world = dimPos.getLevel(true);
        if (world == null) {
            return null;
        } else {
            Optional<BlockEntity> tile = BlockEntityHelpers.get(world, dimPos.getBlockPos(), BlockEntity.class);
            IIngredientComponentStorageWrapperHandler<ChemicalStack<?>, Integer, IChemicalHandler<?, ?>> wrapperHandler = (IIngredientComponentStorageWrapperHandler) getComponent().getStorageWrapperHandler(capability);
            return tile.map((tileEntity) -> {
                IChemicalHandler<?, ?> storage = wrapperHandler.getStorage(tileEntity, pos.getSide()).orElse(null);
                if (storage == null) {
                    return null;
                }
                return wrapperHandler.wrapComponentStorage(storage);
            }).orElse(null);
        }
    }

    // TODO: this hack can be removed in 1.21 when Mekanism puts all chemicals in a single registry
    @Override
    public IIngredientComponentStorage<ChemicalStack<?>, Integer> getPositionedStorage(PartPos pos) {
        Capability<? extends IChemicalHandler<?, ?>> capability = ACTIVE_CAPABILITY;
        if (capability != null) {
            try {
                return this.cacheStorageChemicals.get(Pair.of(capability, pos));
            } catch (ExecutionException e) {
                return new IngredientComponentStorageEmpty<>(this.getComponent());
            }
        } else {
            List<IIngredientComponentStorage<ChemicalStack<?>, Integer>> storages = Lists.newArrayList();
            for (Capability<? extends IChemicalHandler<?, ?>> chemicalCapability : CapabilityHelpers.CHEMICAL_CAPABILITIES) {
                ChemicalNetwork.ACTIVE_CAPABILITY = chemicalCapability;
                storages.add(getPositionedStorage(pos));
                ChemicalNetwork.ACTIVE_CAPABILITY = null;
            }
            return new IngredientComponentStorageComposite<>(getComponent(), storages);
        }
    }
}
