package org.cyclops.integratedmekanism.test;

import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeEntity;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;

/**
 * TODO: use ID variant in 1.21.8+
 * Dummy entity variable.
 * @author rubensworks
 */
public class DummyVariableEntity extends DummyVariable<ValueObjectTypeEntity.ValueEntity> {

    public DummyVariableEntity(ValueObjectTypeEntity.ValueEntity value) {
        super(ValueTypes.OBJECT_ENTITY, value);
    }

    public DummyVariableEntity() {
        super(ValueTypes.OBJECT_ENTITY);
    }

}
