package org.cyclops.integratedmekanismics;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.Level;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.cyclopscore.config.ConfigHandler;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.infobook.IInfoBookRegistry;
import org.cyclops.cyclopscore.init.ModBaseVersionable;
import org.cyclops.cyclopscore.modcompat.ModCompatLoader;
import org.cyclops.cyclopscore.proxy.IClientProxy;
import org.cyclops.cyclopscore.proxy.ICommonProxy;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.command.CommandTest;
import org.cyclops.integrateddynamics.core.event.IntegratedDynamicsSetupEvent;
import org.cyclops.integrateddynamics.infobook.OnTheDynamicsOfIntegrationBook;
import org.cyclops.integratedmekanismics.client.render.value.ValueTypeWorldRenderersMekanism;
import org.cyclops.integratedmekanismics.ingredient.IngredientComponentCapabilitiesMekanism;
import org.cyclops.integratedmekanismics.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanismics.modcompat.integratedterminals.ModCompatIntegratedTerminals;
import org.cyclops.integratedmekanismics.modcompat.integratedtunnels.ModCompatIntegratedTunnels;
import org.cyclops.integratedmekanismics.network.ChemicalNetworkConfig;
import org.cyclops.integratedmekanismics.network.NetworkCapabilityConstructorsMekanism;
import org.cyclops.integratedmekanismics.operator.MekanismOperators;
import org.cyclops.integratedmekanismics.proxy.ClientProxy;
import org.cyclops.integratedmekanismics.proxy.CommonProxy;
import org.cyclops.integratedmekanismics.test.TestChemicalStackOperators;
import org.cyclops.integratedmekanismics.test.TestItemStackOperators;
import org.cyclops.integratedmekanismics.value.MekanismValueTypes;

import java.util.List;

/**
 * The main mod class of this mod.
 * @author rubensworks (aka kroeserr)
 *
 */
@Mod(Reference.MOD_ID)
public class IntegratedMekanismics extends ModBaseVersionable<IntegratedMekanismics> {

    public static IntegratedMekanismics _instance;

    public IntegratedMekanismics() {
        super(Reference.MOD_ID, (instance) -> _instance = instance);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::onRegistriesLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::afterCapabilitiesLoaded);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegistriesCreate);
    }

    public void onRegistriesCreate(NewRegistryEvent event) {
        IngredientComponentCapabilitiesMekanism.load();
    }

    @Override
    protected void loadModCompats(ModCompatLoader modCompatLoader) {
        super.loadModCompats(modCompatLoader);

        // Mod compats
        modCompatLoader.addModCompat(new ModCompatIntegratedTunnels());
        modCompatLoader.addModCompat(new ModCompatIntegratedTerminals());
    }

    protected void onRegistriesLoad(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.BLOCKS.getRegistryKey())) {
            IngredientComponent.REGISTRY.register(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK.getName(), MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK);
        }
    }

    protected void afterCapabilitiesLoaded(InterModEnqueueEvent event) {
        MekanismIngredientComponents.registerStorageWrapperHandlers();
    }

    protected void onSetup(IntegratedDynamicsSetupEvent event) {
        MekanismValueTypes.load();
        MekanismOperators.load();

        // Add test classes
        CommandTest.CLASSES.addAll(List.of(
                TestChemicalStackOperators.class.getName(),
                TestItemStackOperators.class.getName()
        ));

        if (MinecraftHelpers.isClientSide()) {
            ValueTypeWorldRenderersMekanism.load();
        }

        MinecraftForge.EVENT_BUS.addGenericListener(INetwork.class, new NetworkCapabilityConstructorsMekanism()::onNetworkLoad);

        // Initialize info book
        IntegratedDynamics._instance.getRegistryManager().getRegistry(IInfoBookRegistry.class)
                .registerSection(this,
                        OnTheDynamicsOfIntegrationBook.getInstance(), "info_book.integrateddynamics.manual",
                        "/data/" + Reference.MOD_ID + "/info/mekanismics_info.xml");
        IntegratedDynamics._instance.getRegistryManager().getRegistry(IInfoBookRegistry.class)
                .registerSection(this,
                        OnTheDynamicsOfIntegrationBook.getInstance(), "info_book.integrateddynamics.tutorials",
                        "/data/" + Reference.MOD_ID + "/info/mekanismics_tutorials.xml");
    }

    @Override
    protected CreativeModeTab.Builder constructDefaultCreativeModeTab(CreativeModeTab.Builder builder) {
        return super.constructDefaultCreativeModeTab(builder)
                .icon(() -> new ItemStack(RegistryEntries.CHEMICAL_PART_INTERFACE));
    }

    @Override
    public void onConfigsRegister(ConfigHandler configHandler) {
        super.onConfigsRegister(configHandler);

        configHandler.addConfigurable(new GeneralConfig());

        configHandler.addConfigurable(new ChemicalNetworkConfig());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected IClientProxy constructClientProxy() {
        return new ClientProxy();
    }

    @Override
    protected ICommonProxy constructCommonProxy() {
        return new CommonProxy();
    }

    /**
     * Log a new info message for this mod.
     * @param message The message to show.
     */
    public static void clog(String message) {
        clog(Level.INFO, message);
    }

    /**
     * Log a new message of the given level for this mod.
     * @param level The level in which the message must be shown.
     * @param message The message to show.
     */
    public static void clog(Level level, String message) {
        IntegratedMekanismics._instance.getLoggerHelper().log(level, message);
    }

}
