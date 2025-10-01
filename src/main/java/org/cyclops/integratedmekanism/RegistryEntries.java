package org.cyclops.integratedmekanism;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Referenced registry entries.
 * @author rubensworks
 */
public class RegistryEntries {

    public static final DeferredHolder<Item, Item> PART_CHEMICAL_READER = DeferredHolder.create(Registries.ITEM, ResourceLocation.parse("integratedmekanism:part_chemical_reader"));

}
