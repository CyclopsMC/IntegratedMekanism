package org.cyclops.integratedmekanismics.modcompat.integratedtunnels.part;

import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.part.IPartTypeRegistry;

/**
 * @author rubensworks
 */
public class PartTypesMekanismTunnels {

    public static final IPartTypeRegistry REGISTRY = IntegratedDynamics._instance.getRegistryManager().getRegistry(IPartTypeRegistry.class);

    public static void load() {}

    public static final PartTypeInterfaceChemical INTERFACE_CHEMICAL = REGISTRY.register(new PartTypeInterfaceChemical("interface_chemical"));
    public static final PartTypeInterfaceFilteringChemical INTERFACE_FILTERING_CHEMICAL = REGISTRY.register(new PartTypeInterfaceFilteringChemical("interface_filter_chemical"));
    public static final PartTypeImporterChemical IMPORTER_CHEMICAL = REGISTRY.register(new PartTypeImporterChemical("importer_chemical"));
    public static final PartTypeExporterChemical EXPORTER_CHEMICAL = REGISTRY.register(new PartTypeExporterChemical("exporter_chemical"));
    public static final PartTypeImporterWorldChemical IMPORTER_WORLD_CHEMICAL = REGISTRY.register(new PartTypeImporterWorldChemical("importer_world_chemical"));
    public static final PartTypeExporterWorldChemical EXPORTER_WORLD_CHEMICAL = REGISTRY.register(new PartTypeExporterWorldChemical("exporter_world_chemical"));

}
