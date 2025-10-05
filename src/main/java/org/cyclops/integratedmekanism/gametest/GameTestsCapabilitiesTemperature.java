package org.cyclops.integratedmekanism.gametest;

import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.commoncapabilities.api.capability.Capabilities;
import org.cyclops.commoncapabilities.api.capability.temperature.ITemperature;
import org.cyclops.integratedmekanism.Reference;

import static org.cyclops.integratedmekanism.gametest.GameTestsAspectsReadMachineFission.build;

/**
 * @author rubensworks
 */
@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsCapabilitiesTemperature {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 1, 2);

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockTemperatureCapFissionReactorInvalid(GameTestHelper helper) {
        helper.setBlock(POS, GeneratorsBlocks.FISSION_REACTOR_LOGIC_ADAPTER.get());

        helper.succeedIf(() -> {
            ITemperature temperature = helper.getLevel().getCapability(Capabilities.Temperature.BLOCK, helper.absolutePos(POS), Direction.NORTH);
            helper.assertTrue(temperature != null, "Temperature handler does not exist");
            helper.assertValueEqual(temperature.getTemperature(), 0D, "Temperature does not match");
            helper.assertValueEqual(temperature.getMaximumTemperature(), Double.MAX_VALUE, "Temperature max does not match");
            helper.assertValueEqual(temperature.getMinimumTemperature(), 0D, "Temperature min does not match");
            helper.assertValueEqual(temperature.getDefaultTemperature(), 0D, "Temperature default does not match");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockTemperatureCapFissionReactorValid(GameTestHelper helper) {
        BlockPos ioPos = build(helper, POS);

        helper.succeedIf(() -> {
            ITemperature temperature = helper.getLevel().getCapability(Capabilities.Temperature.BLOCK, helper.absolutePos(ioPos), Direction.NORTH);
            helper.assertTrue(temperature != null, "Temperature handler does not exist");
            helper.assertValueEqual(temperature.getTemperature(), 0D, "Temperature does not match");
            helper.assertValueEqual(temperature.getMaximumTemperature(), Double.MAX_VALUE, "Temperature max does not match");
            helper.assertValueEqual(temperature.getMinimumTemperature(), 0D, "Temperature min does not match");
            helper.assertValueEqual(temperature.getDefaultTemperature(), 0D, "Temperature default does not match");
        });
    }

}
