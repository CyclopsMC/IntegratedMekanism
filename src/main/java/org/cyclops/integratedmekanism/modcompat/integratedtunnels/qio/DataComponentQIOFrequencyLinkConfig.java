package org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio;

import org.cyclops.cyclopscore.config.extendedconfig.DataComponentConfig;
import org.cyclops.integratedmekanism.IntegratedMekanism;

/**
 * Config for the QIO frequency stored in QIO interface items.
 * @author rubensworks
 */
public class DataComponentQIOFrequencyLinkConfig extends DataComponentConfig<QIOFrequencyLink> {

    public DataComponentQIOFrequencyLinkConfig() {
        super(IntegratedMekanism._instance, "qio_frequency_link", builder -> builder
                .persistent(QIOFrequencyLink.CODEC)
                .networkSynchronized(QIOFrequencyLink.STREAM_CODEC));
    }
}
