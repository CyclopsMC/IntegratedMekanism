package org.cyclops.integratedmekanism.capability.recipehandler;

import com.google.common.collect.Maps;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismTileEntityTypes;
import mekanism.common.tile.factory.TileEntityFactory;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BaseCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.cyclops.commoncapabilities.api.capability.Capabilities;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import org.cyclops.cyclopscore.modcompat.capabilities.CapabilityConstructorRegistry;
import org.cyclops.cyclopscore.modcompat.capabilities.ICapabilityConstructor;
import org.cyclops.integratedmekanism.IntegratedMekanism;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author rubensworks
 */
public class MekanismCapabilityLoader {

    public static void load() {
        CapabilityConstructorRegistry registry = IntegratedMekanism._instance.getCapabilityConstructorRegistry();

        // RecipeHandlers
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent event) -> MekanismRecipeHandler.CACHED_RECIPES.clear());
        Map<IMekanismRecipeTypeProvider<?, ?, ?>, Function<Supplier<Level>, IRecipeHandler>> recipeTypeHandlers = Maps.newIdentityHashMap();
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.CRUSHER, MekanismRecipeType.CRUSHING, level -> new ItemToItemRecipeHandler(MekanismRecipeType.CRUSHING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.ENRICHMENT_CHAMBER, MekanismRecipeType.ENRICHING, level -> new ItemToItemRecipeHandler(MekanismRecipeType.ENRICHING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.ENERGIZED_SMELTER, MekanismRecipeType.SMELTING, level -> new ItemToItemRecipeHandler(MekanismRecipeType.SMELTING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.CHEMICAL_INFUSER, MekanismRecipeType.CHEMICAL_INFUSING, level -> new ChemicalChemicalToChemicalRecipeHandler(MekanismRecipeType.CHEMICAL_INFUSING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.COMBINER, MekanismRecipeType.COMBINING, CombinerRecipeHandler::new);
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.ELECTROLYTIC_SEPARATOR, MekanismRecipeType.SEPARATING, SeparatingRecipeHandler::new);
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.CHEMICAL_WASHER, MekanismRecipeType.WASHING, level -> new FluidChemicalToChemicalRecipeHandler(MekanismRecipeType.WASHING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.THERMAL_EVAPORATION_CONTROLLER, MekanismRecipeType.EVAPORATING, level -> new FluidToFluidRecipeHandler(MekanismRecipeType.EVAPORATING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.THERMAL_EVAPORATION_VALVE, MekanismRecipeType.EVAPORATING, level -> new FluidToFluidRecipeHandler(MekanismRecipeType.EVAPORATING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.SOLAR_NEUTRON_ACTIVATOR, MekanismRecipeType.ACTIVATING, level -> new ChemicalToChemicalRecipeHandler(MekanismRecipeType.ACTIVATING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.ISOTOPIC_CENTRIFUGE, MekanismRecipeType.CENTRIFUGING, level -> new ChemicalToChemicalRecipeHandler(MekanismRecipeType.CENTRIFUGING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.CHEMICAL_CRYSTALLIZER, MekanismRecipeType.CRYSTALLIZING, ChemicalCrystallizerRecipeHandler::new);
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.CHEMICAL_DISSOLUTION_CHAMBER, MekanismRecipeType.DISSOLUTION, ChemicalDissolutionRecipeHandler::new);
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.OSMIUM_COMPRESSOR, MekanismRecipeType.COMPRESSING, level -> new ItemChemicalToItemRecipeHandler(MekanismRecipeType.COMPRESSING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.PURIFICATION_CHAMBER, MekanismRecipeType.PURIFYING, level -> new ItemChemicalToItemRecipeHandler(MekanismRecipeType.PURIFYING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.CHEMICAL_INJECTION_CHAMBER, MekanismRecipeType.INJECTING, level -> new ItemChemicalToItemRecipeHandler(MekanismRecipeType.INJECTING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.ANTIPROTONIC_NUCLEOSYNTHESIZER, MekanismRecipeType.NUCLEOSYNTHESIZING, level -> new ItemChemicalToItemRecipeHandler(MekanismRecipeType.NUCLEOSYNTHESIZING, level));
        // Skipping ENERGY_CONVERSION, no need?
        // Skipping GAS_CONVERSION, no need?
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.CHEMICAL_OXIDIZER, MekanismRecipeType.OXIDIZING, level -> new ItemToChemicalRecipeHandler(MekanismRecipeType.OXIDIZING, level));
        // Skipping INFUSION_CONVERSION, no need?
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.PIGMENT_EXTRACTOR, MekanismRecipeType.PIGMENT_EXTRACTING, level -> new ItemToChemicalRecipeHandler(MekanismRecipeType.PIGMENT_EXTRACTING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.PIGMENT_MIXER, MekanismRecipeType.PIGMENT_MIXING, level -> new ChemicalChemicalToChemicalRecipeHandler(MekanismRecipeType.PIGMENT_MIXING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.METALLURGIC_INFUSER, MekanismRecipeType.METALLURGIC_INFUSING, level -> new ItemChemicalToItemRecipeHandler(MekanismRecipeType.METALLURGIC_INFUSING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.PAINTING_MACHINE, MekanismRecipeType.PAINTING, level -> new ItemChemicalToItemRecipeHandler(MekanismRecipeType.PAINTING, level));
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.PRESSURIZED_REACTION_CHAMBER, MekanismRecipeType.REACTION, PressurizedReactionRecipeHandler::new);
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.ROTARY_CONDENSENTRATOR, MekanismRecipeType.ROTARY, RotaryRecipeHandler::new);
        addRecipeHandler(registry, recipeTypeHandlers, MekanismTileEntityTypes.PRECISION_SAWMILL, MekanismRecipeType.SAWING, SawmillRecipeHandler::new);
        // Add single abstract handler for all factories
        registry.registerInheritableBlockEntity(TileEntityFactory.class, new ICapabilityConstructor<TileEntityFactory<?>, Direction, IRecipeHandler, BlockEntityType<TileEntityFactory<?>>>() {
            @Override
            public BaseCapability<IRecipeHandler, Direction> getCapability() {
                return Capabilities.RecipeHandler.BLOCK;
            }

            @Override
            public @org.jetbrains.annotations.Nullable ICapabilityProvider<TileEntityFactory<?>, Direction, IRecipeHandler> createProvider(BlockEntityType<TileEntityFactory<?>> blockEntityType) {
                return (host, direction) -> {
                    Function<Supplier<Level>, IRecipeHandler> handler = recipeTypeHandlers.get(host.getRecipeType());
                    if (handler != null) {
                        return handler.apply(host::getLevel);
                    }
                    return null;
                };
            }
        });
    }

    protected static <T extends BlockEntity> void addRecipeHandler(CapabilityConstructorRegistry registry, Map<IMekanismRecipeTypeProvider<?, ?, ?>, Function<Supplier<Level>, IRecipeHandler>> recipeTypeHandlers, Supplier<BlockEntityType<T>> blockEntityType, IMekanismRecipeTypeProvider<?, ?, ?> recipeType, Function<Supplier<Level>, IRecipeHandler> recipeHandlerConstructor) {
        // Add handler for the normal block entity
        registry.registerBlockEntity(blockEntityType, new ICapabilityConstructor<T, Direction, IRecipeHandler, BlockEntityType<T>>() {
            @Override
            public BaseCapability<IRecipeHandler, Direction> getCapability() {
                return Capabilities.RecipeHandler.BLOCK;
            }

            @Override
            public ICapabilityProvider<T, Direction, IRecipeHandler> createProvider(BlockEntityType<T> tBlockEntityType) {
                return (blockEntity, side) -> recipeHandlerConstructor.apply(blockEntity::getLevel);
            }
        });
        // Store handler for the factory variant, as this will be registered later in a common abstract handler.
        recipeTypeHandlers.put(recipeType, recipeHandlerConstructor);
    }

}
