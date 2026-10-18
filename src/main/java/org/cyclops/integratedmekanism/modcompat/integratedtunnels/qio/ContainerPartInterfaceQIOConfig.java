package org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.cyclops.cyclopscore.client.gui.ScreenFactorySafe;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfig;
import org.cyclops.cyclopscore.inventory.container.ContainerTypeData;
import org.cyclops.integratedmekanism.IntegratedMekanism;

/**
 * Config for {@link ContainerPartInterfaceQIO}.
 * @author rubensworks
 */
public class ContainerPartInterfaceQIOConfig extends GuiConfig<ContainerPartInterfaceQIO> {

    public ContainerPartInterfaceQIOConfig() {
        super(IntegratedMekanism._instance,
                "part_interface_qio",
                eConfig -> new ContainerTypeData<>(ContainerPartInterfaceQIO::new, FeatureFlags.VANILLA_SET));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <U extends Screen & MenuAccess<ContainerPartInterfaceQIO>> MenuScreens.ScreenConstructor<ContainerPartInterfaceQIO, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenPartInterfaceQIO::new);
    }

}
