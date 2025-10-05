package org.cyclops.integratedmekanism.gametest;

import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeDouble;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import org.cyclops.integrateddynamics.core.part.PartTypes;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.part.aspect.MekanismAspects;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.testReadAspect;

@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsAspectsReadMachineFission {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 0, 2);

    public static BlockPos build(GameTestHelper helper, BlockPos pos) {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 3; z++) {
                    BlockPos posi = pos.offset(x, y, z);
                    if ((y == 0 || y == 3) || x == 0 || x == 2 || z == 0 || z == 2) {
                        helper.setBlock(posi, GeneratorsBlocks.FISSION_REACTOR_CASING.defaultState());
                    } else if (x % 2 == z % 2) {
                        if (y == 2) {
                            helper.setBlock(posi, GeneratorsBlocks.CONTROL_ROD_ASSEMBLY.defaultState());
                        } else {
                            helper.setBlock(posi, GeneratorsBlocks.FISSION_FUEL_ASSEMBLY.defaultState());
                        }
                    } else {
                        // Air
                    }
                }
            }
        }
        BlockPos ioPos = pos.east().east().above().south();
        helper.setBlock(ioPos, GeneratorsBlocks.FISSION_REACTOR_LOGIC_ADAPTER.get());
        return ioPos;
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadFissionDamageInvalid(GameTestHelper helper) {
        helper.setBlock(POS, GeneratorsBlocks.FISSION_REACTOR_LOGIC_ADAPTER.get());
        testReadAspect(POS, helper, PartTypes.MACHINE_READER, MekanismAspects.Read.Machine.DOUBLE_FISSIONREACTOR_DAMAGE, ValueTypeDouble.ValueDouble.of(0));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadFissionDamageValid(GameTestHelper helper) {
        BlockPos ioPos = build(helper, POS);
        testReadAspect(ioPos.east(), helper, PartTypes.MACHINE_READER, MekanismAspects.Read.Machine.DOUBLE_FISSIONREACTOR_DAMAGE, ValueTypeDouble.ValueDouble.of(0));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadFissionBurnRateInvalid(GameTestHelper helper) {
        helper.setBlock(POS, GeneratorsBlocks.FISSION_REACTOR_LOGIC_ADAPTER.get());
        testReadAspect(POS, helper, PartTypes.MACHINE_READER, MekanismAspects.Read.Machine.DOUBLE_FISSIONREACTOR_BURNRATE, ValueTypeDouble.ValueDouble.of(0));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadFissionBurnRateValid(GameTestHelper helper) {
        BlockPos ioPos = build(helper, POS);
        testReadAspect(ioPos.east(), helper, PartTypes.MACHINE_READER, MekanismAspects.Read.Machine.DOUBLE_FISSIONREACTOR_BURNRATE, ValueTypeDouble.ValueDouble.of(0));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadFissionBurnRateMaxInvalid(GameTestHelper helper) {
        helper.setBlock(POS, GeneratorsBlocks.FISSION_REACTOR_LOGIC_ADAPTER.get());
        testReadAspect(POS, helper, PartTypes.MACHINE_READER, MekanismAspects.Read.Machine.DOUBLE_FISSIONREACTOR_BURNRATEMAX, ValueTypeDouble.ValueDouble.of(0));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadFissionBurnRateMaxValid(GameTestHelper helper) {
        BlockPos ioPos = build(helper, POS);
        testReadAspect(ioPos.east(), helper, PartTypes.MACHINE_READER, MekanismAspects.Read.Machine.DOUBLE_FISSIONREACTOR_BURNRATEMAX, ValueTypeDouble.ValueDouble.of(0));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadFissionBurnRateLimitInvalid(GameTestHelper helper) {
        helper.setBlock(POS, GeneratorsBlocks.FISSION_REACTOR_LOGIC_ADAPTER.get());
        testReadAspect(POS, helper, PartTypes.MACHINE_READER, MekanismAspects.Read.Machine.DOUBLE_FISSIONREACTOR_BURNRATELIMIT, ValueTypeDouble.ValueDouble.of(0));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadFissionBurnRateLimitValid(GameTestHelper helper) {
        BlockPos ioPos = build(helper, POS);
        testReadAspect(ioPos.east(), helper, PartTypes.MACHINE_READER, MekanismAspects.Read.Machine.DOUBLE_FISSIONREACTOR_BURNRATELIMIT, ValueTypeDouble.ValueDouble.of(0.1));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadFissionHeatRateInvalid(GameTestHelper helper) {
        helper.setBlock(POS, GeneratorsBlocks.FISSION_REACTOR_LOGIC_ADAPTER.get());
        testReadAspect(POS, helper, PartTypes.MACHINE_READER, MekanismAspects.Read.Machine.LONG_FISSIONREACTOR_HEATERATE, ValueTypeLong.ValueLong.of(0));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadFissionHeatRateValid(GameTestHelper helper) {
        BlockPos ioPos = build(helper, POS);
        testReadAspect(ioPos.east(), helper, PartTypes.MACHINE_READER, MekanismAspects.Read.Machine.LONG_FISSIONREACTOR_HEATERATE, ValueTypeLong.ValueLong.of(0));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadFissionEnvironmentLossInvalid(GameTestHelper helper) {
        helper.setBlock(POS, GeneratorsBlocks.FISSION_REACTOR_LOGIC_ADAPTER.get());
        testReadAspect(POS, helper, PartTypes.MACHINE_READER, MekanismAspects.Read.Machine.DOUBLE_FISSIONREACTOR_ENVIRONMENTLOSS, ValueTypeDouble.ValueDouble.of(0));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadFissionEnvironmentLossValid(GameTestHelper helper) {
        BlockPos ioPos = build(helper, POS);
        testReadAspect(ioPos.east(), helper, PartTypes.MACHINE_READER, MekanismAspects.Read.Machine.DOUBLE_FISSIONREACTOR_ENVIRONMENTLOSS, ValueTypeDouble.ValueDouble.of(0));
    }

}
