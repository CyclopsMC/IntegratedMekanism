package org.cyclops.integratedmekanism.capability.recipehandler;

import com.google.common.collect.Maps;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.machine.*;
import mekanism.common.tile.multiblock.TileEntityThermalEvaporationController;
import mekanism.common.tile.multiblock.TileEntityThermalEvaporationValve;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.server.ServerStoppedEvent;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import org.cyclops.commoncapabilities.capability.recipehandler.RecipeHandlerConfig;
import org.cyclops.cyclopscore.modcompat.capabilities.CapabilityConstructorRegistry;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.cyclopscore.modcompat.capabilities.ICapabilityConstructor;
import org.cyclops.integratedmekanism.IntegratedMekanism;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
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
        MinecraftForge.EVENT_BUS.addListener((ServerStoppedEvent event) -> MekanismRecipeHandler.CACHED_RECIPES.clear());
        Map<IMekanismRecipeTypeProvider<?, ?>, Function<Supplier<Level>, IRecipeHandler>> recipeTypeHandlers = Maps.newIdentityHashMap();
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityCrusher.class, MekanismRecipeType.CRUSHING, level -> new ItemToItemRecipeHandler(MekanismRecipeType.CRUSHING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityEnrichmentChamber.class, MekanismRecipeType.ENRICHING, level -> new ItemToItemRecipeHandler(MekanismRecipeType.ENRICHING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityEnergizedSmelter.class, MekanismRecipeType.SMELTING, level -> new ItemToItemRecipeHandler(MekanismRecipeType.SMELTING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityChemicalInfuser.class, MekanismRecipeType.CHEMICAL_INFUSING, level -> new ChemicalChemicalToChemicalRecipeHandler<>(MekanismRecipeType.CHEMICAL_INFUSING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityCombiner.class, MekanismRecipeType.COMBINING, CombinerRecipeHandler::new);
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityElectrolyticSeparator.class, MekanismRecipeType.SEPARATING, SeparatingRecipeHandler::new);
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityChemicalWasher.class, MekanismRecipeType.WASHING, level -> new FluidChemicalToChemicalRecipeHandler<>(MekanismRecipeType.WASHING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityThermalEvaporationController.class, MekanismRecipeType.EVAPORATING, level -> new FluidToFluidRecipeHandler(MekanismRecipeType.EVAPORATING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityThermalEvaporationValve.class, MekanismRecipeType.EVAPORATING, level -> new FluidToFluidRecipeHandler(MekanismRecipeType.EVAPORATING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntitySolarNeutronActivator.class, MekanismRecipeType.ACTIVATING, level -> new ChemicalToChemicalRecipeHandler<>(MekanismRecipeType.ACTIVATING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityIsotopicCentrifuge.class, MekanismRecipeType.CENTRIFUGING, level -> new ChemicalToChemicalRecipeHandler<>(MekanismRecipeType.CENTRIFUGING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityChemicalCrystallizer.class, MekanismRecipeType.CRYSTALLIZING, ChemicalCrystallizerRecipeHandler::new);
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityChemicalDissolutionChamber.class, MekanismRecipeType.DISSOLUTION, ChemicalDissolutionRecipeHandler::new);
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityOsmiumCompressor.class, MekanismRecipeType.COMPRESSING, level -> new ItemChemicalToItemRecipeHandler<>(MekanismRecipeType.COMPRESSING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityPurificationChamber.class, MekanismRecipeType.PURIFYING, level -> new ItemChemicalToItemRecipeHandler<>(MekanismRecipeType.PURIFYING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityChemicalInjectionChamber.class, MekanismRecipeType.INJECTING, level -> new ItemChemicalToItemRecipeHandler<>(MekanismRecipeType.INJECTING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityAntiprotonicNucleosynthesizer.class, MekanismRecipeType.NUCLEOSYNTHESIZING, level -> new ItemChemicalToItemRecipeHandler<>(MekanismRecipeType.NUCLEOSYNTHESIZING, level));
        // Skipping ENERGY_CONVERSION, no need?
        // Skipping GAS_CONVERSION, no need?
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityChemicalOxidizer.class, MekanismRecipeType.OXIDIZING, level -> new ItemToChemicalRecipeHandler<>(MekanismRecipeType.OXIDIZING, level));
        // Skipping INFUSION_CONVERSION, no need?
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityPigmentExtractor.class, MekanismRecipeType.PIGMENT_EXTRACTING, level -> new ItemToChemicalRecipeHandler<>(MekanismRecipeType.PIGMENT_EXTRACTING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityPigmentMixer.class, MekanismRecipeType.PIGMENT_MIXING, level -> new ChemicalChemicalToChemicalRecipeHandler<>(MekanismRecipeType.PIGMENT_MIXING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityMetallurgicInfuser.class, MekanismRecipeType.METALLURGIC_INFUSING, level -> new ItemChemicalToItemRecipeHandler<>(MekanismRecipeType.METALLURGIC_INFUSING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityPaintingMachine.class, MekanismRecipeType.PAINTING, level -> new ItemChemicalToItemRecipeHandler<>(MekanismRecipeType.PAINTING, level));
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityPressurizedReactionChamber.class, MekanismRecipeType.REACTION, PressurizedReactionRecipeHandler::new);
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityRotaryCondensentrator.class, MekanismRecipeType.ROTARY, RotaryRecipeHandler::new);
        addRecipeHandler(registry, recipeTypeHandlers, TileEntityPrecisionSawmill.class, MekanismRecipeType.SAWING, SawmillRecipeHandler::new);
        // Add single abstract handler for all factories
        registry.registerInheritableTile(TileEntityFactory.class, new ICapabilityConstructor<IRecipeHandler, TileEntityFactory<?>, TileEntityFactory<?>>() {
            @Override
            public Capability<IRecipeHandler> getCapability() {
                return RecipeHandlerConfig.CAPABILITY;
            }

            @Nullable
            @Override
            public ICapabilityProvider createProvider(TileEntityFactory<?> hostType, TileEntityFactory<?> host) {
                return new ICapabilityProvider() {
                    @Override
                    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @org.jetbrains.annotations.Nullable Direction direction) {
                        if (capability == RecipeHandlerConfig.CAPABILITY) {
                            Function<Supplier<Level>, IRecipeHandler> handler = recipeTypeHandlers.get(host.getRecipeType());
                            if (handler != null) {
                                return LazyOptional.of(() -> handler.apply(host::getLevel)).cast();
                            }
                        }
                        return LazyOptional.empty();
                    }
                };
            }
        });
    }

    protected static <T extends BlockEntity> void addRecipeHandler(CapabilityConstructorRegistry registry, Map<IMekanismRecipeTypeProvider<?, ?>, Function<Supplier<Level>, IRecipeHandler>> recipeTypeHandlers, Class<T> clazz, IMekanismRecipeTypeProvider<?, ?> recipeType, Function<Supplier<Level>, IRecipeHandler> recipeHandlerConstructor) {
        // Add handler for the normal block entity
        registry.registerTile(clazz, new ICapabilityConstructor<IRecipeHandler, T, T>() {
            @Override
            public Capability<IRecipeHandler> getCapability() {
                return RecipeHandlerConfig.CAPABILITY;
            }

            @Nullable
            @Override
            public ICapabilityProvider createProvider(T hostType, T host) {
                return new DefaultCapabilityProvider<>(this::getCapability, recipeHandlerConstructor.apply(host::getLevel));
            }
        });
        // Store handler for the factory variant, as this will be registered later in a common abstract handler.
        recipeTypeHandlers.put(recipeType, recipeHandlerConstructor);
    }

}
