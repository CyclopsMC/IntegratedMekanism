package org.cyclops.integratedmekanism.gametest;

import com.google.common.collect.Sets;
import mekanism.common.content.blocktype.FactoryType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tier.FactoryTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.commoncapabilities.IngredientComponents;
import org.cyclops.commoncapabilities.api.capability.Capabilities;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.MixedIngredients;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;

import java.util.Set;

/**
 * @author rubensworks
 */
@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsChemicalRecipeHandler {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 1, 2);

    protected static void testMachine(GameTestHelper helper, Block block, Set<IngredientComponent<?, ?>> inputComponents, Set<IngredientComponent<?, ?>> outputComponents, int minRecipes) {
        helper.setBlock(POS, block);

        helper.succeedIf(() -> {
            // Check if recipe handler capability exists and is valid
            IRecipeHandler recipeHandler = helper.getLevel().getCapability(Capabilities.RecipeHandler.BLOCK, helper.absolutePos(POS), Direction.NORTH);

            helper.assertTrue(recipeHandler != null, "Recipe handler does not exist");
            helper.assertValueEqual(recipeHandler.getRecipeInputComponents(), inputComponents, "Input components are incorrect");
            helper.assertValueEqual(recipeHandler.getRecipeOutputComponents(), outputComponents, "Output components are incorrect");
            helper.assertTrue(recipeHandler.getRecipes().size() >= minRecipes, "Recipe count " + recipeHandler.getRecipes().size() + " is less than " + minRecipes);
            for (IRecipeDefinition recipe : recipeHandler.getRecipes()) {
                helper.assertTrue(recipeHandler.simulate(MixedIngredients.fromRecipeInput(recipe)) != null, "Recipe simulation failed for " + recipe);
            }
        });
    }

    protected static void testMachineFactories(GameTestHelper helper, FactoryType factoryType, Set<IngredientComponent<?, ?>> inputComponents, Set<IngredientComponent<?, ?>> outputComponents, int minRecipes) {
        for (FactoryTier factoryTier : FactoryTier.values()) {
            helper.setBlock(POS.offset(0, factoryTier.ordinal(), 0), MekanismBlocks.getFactory(factoryTier, factoryType).get());
        }

        helper.succeedIf(() -> {
            for (FactoryTier factoryTier : FactoryTier.values()) {
                // Check if recipe handler capability exists and is valid
                IRecipeHandler recipeHandler = helper.getLevel().getCapability(Capabilities.RecipeHandler.BLOCK, helper.absolutePos(POS.offset(0, factoryTier.ordinal(), 0)), Direction.NORTH);

                helper.assertTrue(recipeHandler != null, "Recipe handler does not exist");
                helper.assertValueEqual(recipeHandler.getRecipeInputComponents(), inputComponents, "Input components are incorrect");
                helper.assertValueEqual(recipeHandler.getRecipeOutputComponents(), outputComponents, "Output components are incorrect");
                helper.assertTrue(recipeHandler.getRecipes().size() >= minRecipes, "Recipe count " + recipeHandler.getRecipes().size() + " is less than " + minRecipes);
                for (IRecipeDefinition recipe : recipeHandler.getRecipes()) {
                    helper.assertTrue(recipeHandler.simulate(MixedIngredients.fromRecipeInput(recipe)) != null, "Recipe simulation failed for " + recipe);
                }
            }
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerCrusher(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.CRUSHER.get(), Sets.newHashSet(IngredientComponents.ITEMSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 200);
    }
    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerCrusherFactories(GameTestHelper helper) {
        testMachineFactories(helper, FactoryType.CRUSHING, Sets.newHashSet(IngredientComponents.ITEMSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 200);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerEnrichmentChamber(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.ENRICHMENT_CHAMBER.get(), Sets.newHashSet(IngredientComponents.ITEMSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 187);
    }
    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerEnrichmentChamberFactories(GameTestHelper helper) {
        testMachineFactories(helper, FactoryType.ENRICHING, Sets.newHashSet(IngredientComponents.ITEMSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 187);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerEnergizedSmelter(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.ENERGIZED_SMELTER.get(), Sets.newHashSet(IngredientComponents.ITEMSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 97);
    }
    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerEnergizedSmelterFactories(GameTestHelper helper) {
        testMachineFactories(helper, FactoryType.SMELTING, Sets.newHashSet(IngredientComponents.ITEMSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 97);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerChemicalInfuser(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.CHEMICAL_INFUSER.get(), Sets.newHashSet(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), 5);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerCombiner(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.COMBINER.get(), Sets.newHashSet(IngredientComponents.ITEMSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 82);
    }
    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerCombinerFactories(GameTestHelper helper) {
        testMachineFactories(helper, FactoryType.COMBINING, Sets.newHashSet(IngredientComponents.ITEMSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 82);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerElectrolyticSeparator(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.ELECTROLYTIC_SEPARATOR.get(), Sets.newHashSet(IngredientComponents.FLUIDSTACK), Sets.newHashSet(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), 3);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerWasher(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.CHEMICAL_WASHER.get(), Sets.newHashSet(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK, IngredientComponents.FLUIDSTACK), Sets.newHashSet(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), 7);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerThermalEvaporationController(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.THERMAL_EVAPORATION_CONTROLLER.get(), Sets.newHashSet(IngredientComponents.FLUIDSTACK), Sets.newHashSet(IngredientComponents.FLUIDSTACK), 2);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerThermalEvaporationValve(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.THERMAL_EVAPORATION_VALVE.get(), Sets.newHashSet(IngredientComponents.FLUIDSTACK), Sets.newHashSet(IngredientComponents.FLUIDSTACK), 2);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerSolarNeutronActivator(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.SOLAR_NEUTRON_ACTIVATOR.get(), Sets.newHashSet(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), 2);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerIsotopicCentrifige(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.ISOTOPIC_CENTRIFUGE.get(), Sets.newHashSet(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), 2);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerChemicalCrystalizer(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.CHEMICAL_CRYSTALLIZER.get(), Sets.newHashSet(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 10);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerChemicalDissolutionChamber(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.CHEMICAL_DISSOLUTION_CHAMBER.get(), Sets.newHashSet(IngredientComponents.ITEMSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), 23);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerOsmiumCompressor(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.OSMIUM_COMPRESSOR.get(), Sets.newHashSet(IngredientComponents.ITEMSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 2);
    }
    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerOsmiumCompressorFactories(GameTestHelper helper) {
        testMachineFactories(helper, FactoryType.COMPRESSING, Sets.newHashSet(IngredientComponents.ITEMSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 2);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerPurificationChamber(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.PURIFICATION_CHAMBER.get(), Sets.newHashSet(IngredientComponents.ITEMSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 28);
    }
    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerPurificationChamberFactories(GameTestHelper helper) {
        testMachineFactories(helper, FactoryType.PURIFYING, Sets.newHashSet(IngredientComponents.ITEMSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 28);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerChemicalInjectionChamber(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.CHEMICAL_INJECTION_CHAMBER.get(), Sets.newHashSet(IngredientComponents.ITEMSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 91);
    }
    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerChemicalInjectionChamberFactories(GameTestHelper helper) {
        testMachineFactories(helper, FactoryType.INJECTING, Sets.newHashSet(IngredientComponents.ITEMSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 91);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerAntiprotonic(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.ANTIPROTONIC_NUCLEOSYNTHESIZER.get(), Sets.newHashSet(IngredientComponents.ITEMSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 21);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerChemicalOxidizer(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.CHEMICAL_OXIDIZER.get(), Sets.newHashSet(IngredientComponents.ITEMSTACK), Sets.newHashSet(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), 25);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerPigmentExtractor(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.PIGMENT_EXTRACTOR.get(), Sets.newHashSet(IngredientComponents.ITEMSTACK), Sets.newHashSet(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), 394);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerPigmentMixer(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.PIGMENT_MIXER.get(), Sets.newHashSet(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), 16);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerMetallurgicInfuser(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.METALLURGIC_INFUSER.get(), Sets.newHashSet(IngredientComponents.ITEMSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 31);
    }
    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerMetallurgicInfuserFactories(GameTestHelper helper) {
        testMachineFactories(helper, FactoryType.INFUSING, Sets.newHashSet(IngredientComponents.ITEMSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 31);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerPaining(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.PAINTING_MACHINE.get(), Sets.newHashSet(IngredientComponents.ITEMSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Sets.newHashSet(IngredientComponents.ITEMSTACK), 464);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerReaction(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.PRESSURIZED_REACTION_CHAMBER.get(), Set.of(IngredientComponents.ITEMSTACK, IngredientComponents.FLUIDSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Set.of(IngredientComponents.ITEMSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), 14);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerRotary(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.ROTARY_CONDENSENTRATOR.get(), Set.of(IngredientComponents.FLUIDSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), Set.of(IngredientComponents.FLUIDSTACK, MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK), 40);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerSawmill(GameTestHelper helper) {
        testMachine(helper, MekanismBlocks.PRECISION_SAWMILL.get(), Set.of(IngredientComponents.ITEMSTACK), Set.of(IngredientComponents.ITEMSTACK), 124);
    }
    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockRecipeHandlerSawmillFactories(GameTestHelper helper) {
        testMachineFactories(helper, FactoryType.SAWING, Set.of(IngredientComponents.ITEMSTACK), Set.of(IngredientComponents.ITEMSTACK), 124);
    }

}
