package org.cyclops.integratedmekanism.gametest;

import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.commoncapabilities.api.capability.Capabilities;
import org.cyclops.commoncapabilities.api.capability.work.IWorker;
import org.cyclops.integratedmekanism.Reference;

import static org.cyclops.integratedmekanism.gametest.GameTestsAspectsReadMachineFission.build;

/**
 * @author rubensworks
 */
@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsCapabilitiesWorker {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 1, 2);

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockWorkerCapFissionReactorInvalid(GameTestHelper helper) {
        helper.setBlock(POS, GeneratorsBlocks.FISSION_REACTOR_LOGIC_ADAPTER.get());

        helper.succeedIf(() -> {
            IWorker worker = helper.getLevel().getCapability(Capabilities.Worker.BLOCK, helper.absolutePos(POS), Direction.NORTH);
            helper.assertTrue(worker != null, "Worker does not exist");
            helper.assertValueEqual(worker.hasWork(), false, "Worker has work does not match");
            helper.assertValueEqual(worker.canWork(), false, "Worker can work does not match");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testBlockWorkerCapFissionReactorValid(GameTestHelper helper) {
        BlockPos ioPos = build(helper, POS);

        helper.succeedIf(() -> {
            IWorker worker = helper.getLevel().getCapability(Capabilities.Worker.BLOCK, helper.absolutePos(ioPos), Direction.NORTH);
            helper.assertTrue(worker != null, "Worker does not exist");
            helper.assertValueEqual(worker.hasWork(), false, "Worker has work does not match");
            helper.assertValueEqual(worker.canWork(), false, "Worker can work does not match");
        });
    }

}
