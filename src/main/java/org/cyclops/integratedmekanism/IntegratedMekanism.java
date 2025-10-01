package org.cyclops.integratedmekanism;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
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
import org.cyclops.integrateddynamics.command.CommandTest;
import org.cyclops.integrateddynamics.core.event.IntegratedDynamicsSetupEvent;
import org.cyclops.integrateddynamics.infobook.OnTheDynamicsOfIntegrationBook;
import org.cyclops.integratedmekanism.capability.recipehandler.MekanismCapabilityLoader;
import org.cyclops.integratedmekanism.client.render.value.ValueTypeWorldRenderersMekanism;
import org.cyclops.integratedmekanism.ingredient.IngredientComponentCapabilitiesMekanism;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanism.logicprogrammer.MekanismLogicProgrammerElementTypes;
import org.cyclops.integratedmekanism.modcompat.integratedrest.ModCompatIntegratedRest;
import org.cyclops.integratedmekanism.modcompat.integratedscripting.ModCompatIntegratedScripting;
import org.cyclops.integratedmekanism.modcompat.integratedterminals.ModCompatIntegratedTerminals;
import org.cyclops.integratedmekanism.modcompat.integratedtunnels.ModCompatIntegratedTunnels;
import org.cyclops.integratedmekanism.network.NetworkCapabilityConstructorsMekanism;
import org.cyclops.integratedmekanism.operator.MekanismOperators;
import org.cyclops.integratedmekanism.part.PartTypesMekanism;
import org.cyclops.integratedmekanism.part.aspect.listproxy.MekanismValueTypeListProxyFactories;
import org.cyclops.integratedmekanism.proxy.ClientProxy;
import org.cyclops.integratedmekanism.proxy.CommonProxy;
import org.cyclops.integratedmekanism.test.TestChemicalStackOperators;
import org.cyclops.integratedmekanism.test.TestIngredientsOperators;
import org.cyclops.integratedmekanism.test.TestItemStackOperators;
import org.cyclops.integratedmekanism.value.MekanismValueTypes;

import java.util.List;

/**
 * The main mod class of this mod.
 * @author rubensworks (aka kroeserr)
 *
 */
@Mod(Reference.MOD_ID)
public class IntegratedMekanism extends ModBaseVersionable<IntegratedMekanism> {

    public static IntegratedMekanism _instance;

    public IntegratedMekanism(IEventBus modEventBus) {
        super(Reference.MOD_ID, (instance) -> _instance = instance, modEventBus);

        modEventBus.addListener(EventPriority.LOW, this::onRegistriesLoad);
        modEventBus.addListener(EventPriority.LOW, this::afterCapabilitiesLoaded);
        modEventBus.addListener(this::onSetup);
        modEventBus.addListener(this::onRegistriesCreate);
        modEventBus.addListener(Capabilities::registerPartCapabilities);
    }

    public void onRegistriesCreate(NewRegistryEvent event) {
        IngredientComponentCapabilitiesMekanism.load();
        PartTypesMekanism.load();
        MekanismCapabilityLoader.load();
    }

    @Override
    protected void loadModCompats(ModCompatLoader modCompatLoader) {
        super.loadModCompats(modCompatLoader);

        // Mod compats
        modCompatLoader.addModCompat(new ModCompatIntegratedTunnels());
        modCompatLoader.addModCompat(new ModCompatIntegratedTerminals());
        modCompatLoader.addModCompat(new ModCompatIntegratedScripting());
        modCompatLoader.addModCompat(new ModCompatIntegratedRest());
    }

    protected void onRegistriesLoad(RegisterEvent event) {
        event.register(IngredientComponent.REGISTRY.key(), MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK.getName(), () -> MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK);
    }

    protected void afterCapabilitiesLoaded(InterModEnqueueEvent event) {
        MekanismIngredientComponents.registerStorageWrapperHandlers();
    }

    protected void onSetup(IntegratedDynamicsSetupEvent event) {
        MekanismValueTypeListProxyFactories.load();
        MekanismValueTypes.load();
        MekanismOperators.load();
        MekanismLogicProgrammerElementTypes.load();

        // Add test classes
        CommandTest.CLASSES.addAll(List.of(
                TestChemicalStackOperators.class.getName(),
                TestIngredientsOperators.class.getName(),
                TestItemStackOperators.class.getName()
        ));

        if (MinecraftHelpers.isClientSide()) {
            ValueTypeWorldRenderersMekanism.load();
        }

        getModEventBus().addListener(new NetworkCapabilityConstructorsMekanism()::onNetworkLoad);

        // Initialize info book
        IntegratedDynamics._instance.getRegistryManager().getRegistry(IInfoBookRegistry.class)
                .registerSection(this,
                        OnTheDynamicsOfIntegrationBook.getInstance(), "info_book.integrateddynamics.manual",
                        "/data/" + Reference.MOD_ID + "/info/mekanism_info.xml");
        IntegratedDynamics._instance.getRegistryManager().getRegistry(IInfoBookRegistry.class)
                .registerSection(this,
                        OnTheDynamicsOfIntegrationBook.getInstance(), "info_book.integrateddynamics.tutorials",
                        "/data/" + Reference.MOD_ID + "/info/mekanism_tutorials.xml");
    }

    @Override
    protected CreativeModeTab.Builder constructDefaultCreativeModeTab(CreativeModeTab.Builder builder) {
        return super.constructDefaultCreativeModeTab(builder)
                .icon(() -> new ItemStack(RegistryEntries.PART_CHEMICAL_READER));
    }

    @Override
    public void onConfigsRegister(ConfigHandler configHandler) {
        super.onConfigsRegister(configHandler);

        configHandler.addConfigurable(new GeneralConfig());
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
        IntegratedMekanism._instance.getLoggerHelper().log(level, message);
    }

}
