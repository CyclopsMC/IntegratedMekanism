package org.cyclops.integratedmekanism.capability.temperature;

import mekanism.generators.common.tile.fission.TileEntityFissionReactorLogicAdapter;
import org.cyclops.commoncapabilities.api.capability.temperature.ITemperature;

/**
 * @author rubensworks
 */
public class FissionReactorTemperature implements ITemperature {

    private final TileEntityFissionReactorLogicAdapter blockEntity;

    public FissionReactorTemperature(TileEntityFissionReactorLogicAdapter blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public double getTemperature() {
        return this.blockEntity.getTotalTemperature();
    }

    @Override
    public double getMaximumTemperature() {
        return Double.MAX_VALUE;
    }

    @Override
    public double getMinimumTemperature() {
        return 0;
    }

    @Override
    public double getDefaultTemperature() {
        return 0;
    }
}
