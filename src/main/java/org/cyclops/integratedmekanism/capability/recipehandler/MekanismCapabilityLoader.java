package org.cyclops.integratedmekanism.capability.recipehandler;

import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.content.blocktype.FactoryType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismTileEntityTypes;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.generators.common.registries.GeneratorsTileEntityTypes;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorLogicAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BaseCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.cyclops.commoncapabilities.api.capability.Capabilities;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import org.cyclops.commoncapabilities.api.capability.temperature.ITemperature;
import org.cyclops.commoncapabilities.api.capability.work.IWorker;
import org.cyclops.cyclopscore.modcompat.capabilities.CapabilityConstructorRegistry;
import org.cyclops.cyclopscore.modcompat.capabilities.ICapabilityConstructor;
import org.cyclops.integratedmekanism.IntegratedMekanism;
import org.cyclops.integratedmekanism.capability.chemicalhandler.VanillaEntityItemChemicalHandler;
import org.cyclops.integratedmekanism.capability.chemicalhandler.VanillaEntityItemFrameChemicalHandler;
import org.cyclops.integratedmekanism.capability.temperature.FissionReactorTemperature;
import org.cyclops.integratedmekanism.capability.worker.FissionReactorWorker;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author rubensworks
 */
public class MekanismCapabilityLoader {

    public static void load() {
        CapabilityConstructorRegistry registry = IntegratedMekanism._instance.getCapabilityConstructorRegistry();

        // Worker
        registry.registerBlockEntity(GeneratorsTileEntityTypes.FISSION_REACTOR_LOGIC_ADAPTER,
                new ICapabilityConstructor<TileEntityFissionReactorLogicAdapter, Direction, IWorker, BlockEntityType<TileEntityFissionReactorLogicAdapter>>() {
                    @Override
                    public BaseCapability<IWorker, Direction> getCapability() {
                        return Capabilities.Worker.BLOCK;
                    }

                    @Override
                    public ICapabilityProvider<TileEntityFissionReactorLogicAdapter, Direction, IWorker> createProvider(BlockEntityType<TileEntityFissionReactorLogicAdapter> tBlockEntityType) {
                        return (blockEntity, side) -> new FissionReactorWorker(blockEntity);
                    }
                });

        // Temperature
        registry.registerBlockEntity(GeneratorsTileEntityTypes.FISSION_REACTOR_LOGIC_ADAPTER,
                new ICapabilityConstructor<TileEntityFissionReactorLogicAdapter, Direction, ITemperature, BlockEntityType<TileEntityFissionReactorLogicAdapter>>() {
                    @Override
                    public BaseCapability<ITemperature, Direction> getCapability() {
                        return Capabilities.Temperature.BLOCK;
                    }

                    @Override
                    public ICapabilityProvider<TileEntityFissionReactorLogicAdapter, Direction, ITemperature> createProvider(BlockEntityType<TileEntityFissionReactorLogicAdapter> tBlockEntityType) {
                        return (blockEntity, side) -> new FissionReactorTemperature(blockEntity);
                    }
                });

        // RecipeHandlers
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent event) -> MekanismRecipeHandler.CACHED_RECIPES.clear());
        addRecipeHandler(registry, MekanismTileEntityTypes.CRUSHER, MekanismRecipeType.CRUSHING, FactoryType.CRUSHING, level -> new ItemToItemRecipeHandler(MekanismRecipeType.CRUSHING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.ENRICHMENT_CHAMBER, MekanismRecipeType.ENRICHING, FactoryType.ENRICHING, level -> new ItemToItemRecipeHandler(MekanismRecipeType.ENRICHING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.ENERGIZED_SMELTER, MekanismRecipeType.SMELTING, FactoryType.SMELTING, level -> new ItemToItemRecipeHandler(MekanismRecipeType.SMELTING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.CHEMICAL_INFUSER, MekanismRecipeType.CHEMICAL_INFUSING, null, level -> new ChemicalChemicalToChemicalRecipeHandler(MekanismRecipeType.CHEMICAL_INFUSING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.COMBINER, MekanismRecipeType.COMBINING, FactoryType.COMBINING, CombinerRecipeHandler::new);
        addRecipeHandler(registry, MekanismTileEntityTypes.ELECTROLYTIC_SEPARATOR, MekanismRecipeType.SEPARATING, null, SeparatingRecipeHandler::new);
        addRecipeHandler(registry, MekanismTileEntityTypes.CHEMICAL_WASHER, MekanismRecipeType.WASHING, null, level -> new FluidChemicalToChemicalRecipeHandler(MekanismRecipeType.WASHING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.THERMAL_EVAPORATION_CONTROLLER, MekanismRecipeType.EVAPORATING, null, level -> new FluidToFluidRecipeHandler(MekanismRecipeType.EVAPORATING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.THERMAL_EVAPORATION_VALVE, MekanismRecipeType.EVAPORATING, null, level -> new FluidToFluidRecipeHandler(MekanismRecipeType.EVAPORATING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.SOLAR_NEUTRON_ACTIVATOR, MekanismRecipeType.ACTIVATING, null, level -> new ChemicalToChemicalRecipeHandler(MekanismRecipeType.ACTIVATING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.ISOTOPIC_CENTRIFUGE, MekanismRecipeType.CENTRIFUGING, null, level -> new ChemicalToChemicalRecipeHandler(MekanismRecipeType.CENTRIFUGING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.CHEMICAL_CRYSTALLIZER, MekanismRecipeType.CRYSTALLIZING, null, ChemicalCrystallizerRecipeHandler::new);
        addRecipeHandler(registry, MekanismTileEntityTypes.CHEMICAL_DISSOLUTION_CHAMBER, MekanismRecipeType.DISSOLUTION, null, ChemicalDissolutionRecipeHandler::new);
        addRecipeHandler(registry, MekanismTileEntityTypes.OSMIUM_COMPRESSOR, MekanismRecipeType.COMPRESSING, FactoryType.COMPRESSING, level -> new ItemChemicalToItemRecipeHandler(MekanismRecipeType.COMPRESSING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.PURIFICATION_CHAMBER, MekanismRecipeType.PURIFYING, FactoryType.PURIFYING, level -> new ItemChemicalToItemRecipeHandler(MekanismRecipeType.PURIFYING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.CHEMICAL_INJECTION_CHAMBER, MekanismRecipeType.INJECTING, FactoryType.INJECTING, level -> new ItemChemicalToItemRecipeHandler(MekanismRecipeType.INJECTING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.ANTIPROTONIC_NUCLEOSYNTHESIZER, MekanismRecipeType.NUCLEOSYNTHESIZING, null, level -> new ItemChemicalToItemRecipeHandler(MekanismRecipeType.NUCLEOSYNTHESIZING, level));
        // Skipping ENERGY_CONVERSION, no need?
        // Skipping GAS_CONVERSION, no need?
        addRecipeHandler(registry, MekanismTileEntityTypes.CHEMICAL_OXIDIZER, MekanismRecipeType.OXIDIZING, null, level -> new ItemToChemicalRecipeHandler(MekanismRecipeType.OXIDIZING, level));
        // Skipping INFUSION_CONVERSION, no need?
        addRecipeHandler(registry, MekanismTileEntityTypes.PIGMENT_EXTRACTOR, MekanismRecipeType.PIGMENT_EXTRACTING, null, level -> new ItemToChemicalRecipeHandler(MekanismRecipeType.PIGMENT_EXTRACTING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.PIGMENT_MIXER, MekanismRecipeType.PIGMENT_MIXING, null, level -> new ChemicalChemicalToChemicalRecipeHandler(MekanismRecipeType.PIGMENT_MIXING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.METALLURGIC_INFUSER, MekanismRecipeType.METALLURGIC_INFUSING, FactoryType.INFUSING, level -> new ItemChemicalToItemRecipeHandler(MekanismRecipeType.METALLURGIC_INFUSING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.PAINTING_MACHINE, MekanismRecipeType.PAINTING, null, level -> new ItemChemicalToItemRecipeHandler(MekanismRecipeType.PAINTING, level));
        addRecipeHandler(registry, MekanismTileEntityTypes.PRESSURIZED_REACTION_CHAMBER, MekanismRecipeType.REACTION, null, PressurizedReactionRecipeHandler::new);
        addRecipeHandler(registry, MekanismTileEntityTypes.ROTARY_CONDENSENTRATOR, MekanismRecipeType.ROTARY, null, RotaryRecipeHandler::new);
        addRecipeHandler(registry, MekanismTileEntityTypes.PRECISION_SAWMILL, MekanismRecipeType.SAWING, FactoryType.SAWING, SawmillRecipeHandler::new);

        // Delegate chemical handlers from entity items to items
        registry.registerEntity(() -> EntityType.ITEM,
                new ICapabilityConstructor<ItemEntity, Direction, IChemicalHandler, EntityType<ItemEntity>>() {
                    @Override
                    public BaseCapability<IChemicalHandler, Direction> getCapability() {
                        return (BaseCapability<IChemicalHandler, Direction>) mekanism.common.capabilities.Capabilities.CHEMICAL.entity();
                    }

                    @Override
                    public ICapabilityProvider<ItemEntity, Direction, IChemicalHandler> createProvider(EntityType<ItemEntity> capabilityKey) {
                        return (entity, context) -> {
                            if (entity.getItem().getCapability(mekanism.common.capabilities.Capabilities.CHEMICAL.item()) != null) {
                                return new VanillaEntityItemChemicalHandler(entity);
                            }
                            return null;
                        };
                    }
                });
        registry.registerEntity(() -> EntityType.ITEM_FRAME,
                new ICapabilityConstructor<ItemFrame, Direction, IChemicalHandler, EntityType<ItemFrame>>() {
                    @Override
                    public BaseCapability<IChemicalHandler, Direction> getCapability() {
                        return (BaseCapability<IChemicalHandler, Direction>) mekanism.common.capabilities.Capabilities.CHEMICAL.entity();
                    }

                    @Override
                    public ICapabilityProvider<ItemFrame, Direction, IChemicalHandler> createProvider(EntityType<ItemFrame> capabilityKey) {
                        return (entity, context) -> {
                            if (entity.getItem().getCapability(mekanism.common.capabilities.Capabilities.CHEMICAL.item()) != null) {
                                return new VanillaEntityItemFrameChemicalHandler(entity);
                            }
                            return null;
                        };
                    }
                });
        registry.registerEntity(() -> EntityType.GLOW_ITEM_FRAME,
                new ICapabilityConstructor<GlowItemFrame, Direction, IChemicalHandler, EntityType<GlowItemFrame>>() {
                    @Override
                    public BaseCapability<IChemicalHandler, Direction> getCapability() {
                        return (BaseCapability<IChemicalHandler, Direction>) mekanism.common.capabilities.Capabilities.CHEMICAL.entity();
                    }

                    @Override
                    public ICapabilityProvider<GlowItemFrame, Direction, IChemicalHandler> createProvider(EntityType<GlowItemFrame> capabilityKey) {
                        return (entity, context) -> {
                            if (entity.getItem().getCapability(mekanism.common.capabilities.Capabilities.CHEMICAL.item()) != null) {
                                return new VanillaEntityItemFrameChemicalHandler(entity);
                            }
                            return null;
                        };
                    }
                });
    }

    protected static <T extends BlockEntity> void addRecipeHandler(CapabilityConstructorRegistry registry, Supplier<BlockEntityType<T>> blockEntityType, IMekanismRecipeTypeProvider<?, ?, ?> recipeType, @Nullable FactoryType factoryType, Function<Supplier<Level>, IRecipeHandler> recipeHandlerConstructor) {
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

        // Register handlers for factory types
        if (factoryType != null) {
            for (FactoryTier factoryTier : FactoryTier.values()) {
                registry.registerBlockEntity(() -> (BlockEntityType<TileEntityFactory>) (BlockEntityType) MekanismTileEntityTypes.getFactoryTile(factoryTier, factoryType).get(), new ICapabilityConstructor<TileEntityFactory, Direction, IRecipeHandler, BlockEntityType<TileEntityFactory>>() {
                    @Override
                    public BaseCapability<IRecipeHandler, Direction> getCapability() {
                        return Capabilities.RecipeHandler.BLOCK;
                    }

                    @Override
                    public ICapabilityProvider<TileEntityFactory, Direction, IRecipeHandler> createProvider(BlockEntityType<TileEntityFactory> tBlockEntityType) {
                        return (blockEntity, side) -> recipeHandlerConstructor.apply(blockEntity::getLevel);
                    }
                });
            }
        }
    }

}
