package org.cyclops.integratedmekanism.capability.worker;

import mekanism.generators.common.tile.fission.TileEntityFissionReactorLogicAdapter;
import org.cyclops.commoncapabilities.api.capability.work.IWorker;

/**
 * @author rubensworks
 */
public class FissionReactorWorker implements IWorker {

    private final TileEntityFissionReactorLogicAdapter blockEntity;

    public FissionReactorWorker(TileEntityFissionReactorLogicAdapter blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public boolean hasWork() {
        return blockEntity.getActive();
    }

    @Override
    public boolean canWork() {
        return blockEntity.getActive();
    }
}
