package org.cyclops.integratedmekanism;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio.ContainerPartInterfaceQIO;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.qio.QIOFrequencyLink;

/**
 * Referenced registry entries.
 * @author rubensworks
 */
public class RegistryEntries {

    public static final DeferredHolder<Item, Item> PART_CHEMICAL_READER = DeferredHolder.create(Registries.ITEM, ResourceLocation.parse("integratedmekanism:part_chemical_reader"));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<QIOFrequencyLink>> DATACOMPONENT_QIO_FREQUENCY_LINK = DeferredHolder.create(Registries.DATA_COMPONENT_TYPE, ResourceLocation.parse("integratedmekanism:qio_frequency_link"));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerPartInterfaceQIO>> CONTAINER_PART_INTERFACE_QIO = DeferredHolder.create(Registries.MENU, ResourceLocation.parse("integratedmekanism:part_interface_qio"));

}
