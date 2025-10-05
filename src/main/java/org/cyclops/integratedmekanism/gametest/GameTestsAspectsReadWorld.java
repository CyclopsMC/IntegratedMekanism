package org.cyclops.integratedmekanism.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeDouble;
import org.cyclops.integrateddynamics.core.part.PartTypes;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.part.aspect.MekanismAspects;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.testReadAspect;

@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsAspectsReadWorld {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 0, 2);

    @GameTest(template = TEMPLATE_EMPTY)
    public void testAspectsReadRadiationDefault(GameTestHelper helper) {
        testReadAspect(POS, helper, PartTypes.WORLD_READER, MekanismAspects.Read.World.DOUBLE_RADIATION, ValueTypeDouble.ValueDouble.of(0.0000001));
    }

}
