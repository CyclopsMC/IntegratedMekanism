package org.cyclops.integratedmekanism.test;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.core.evaluate.operator.Operators;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeEntity;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeDouble;
import org.cyclops.integrateddynamics.core.test.IntegrationBefore;
import org.cyclops.integrateddynamics.core.test.IntegrationTest;
import org.cyclops.integratedmekanism.operator.MekanismOperators;

import java.util.Objects;

/**
 * @author rubensworks
 */
public class TestEntityOperators {

    private static final DummyValueType DUMMY_TYPE = DummyValueType.TYPE;
    private static final DummyVariable<DummyValueType.DummyValue> DUMMY_VARIABLE =
            new DummyVariable<DummyValueType.DummyValue>(DUMMY_TYPE, DummyValueType.DummyValue.of());

    private DummyVariableEntity eZombie;

    protected ValueObjectTypeEntity.ValueEntity makeEntity(Entity entity) {
        return new ValueEntityMock(entity);
    }

    @IntegrationBefore
    public void before() {
        Level world = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
        eZombie = new DummyVariableEntity(makeEntity(new Zombie(world)));
    }

    /**
     * ----------------------------------- RADIATION -----------------------------------
     */

    @IntegrationTest
    public void testEntityRadiation() throws EvaluationException {
        IValue res1 = MekanismOperators.OBJECT_ENTITY_RADIATION.evaluate(eZombie);
        Asserts.check(res1 instanceof ValueTypeDouble.ValueDouble, "result is a double");
        org.apache.http.util.Asserts.check(Objects.equals(((ValueTypeDouble.ValueDouble) res1).getRawValue(), 0.0000001D), "radiation(zombie) = 1.0E-7");
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testEntityRadiationLarge() throws EvaluationException {
        Operators.OBJECT_ENTITY_ISMOB.evaluate(eZombie, eZombie);
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testEntityRadiationSmall() throws EvaluationException {
        Operators.OBJECT_ENTITY_ISMOB.evaluate();
    }

    @IntegrationTest(expected = EvaluationException.class)
    public void testInvalidInputTypeEntityRadiation() throws EvaluationException {
        Operators.OBJECT_ENTITY_ISMOB.evaluate(DUMMY_VARIABLE);
    }

}
