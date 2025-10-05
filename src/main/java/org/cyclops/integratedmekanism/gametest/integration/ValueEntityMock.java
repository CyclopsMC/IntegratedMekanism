package org.cyclops.integratedmekanism.gametest.integration;

import net.minecraft.world.entity.Entity;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeEntity;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * TODO: use ID variant in 1.21.8+
 * @author rubensworks
 */
public class ValueEntityMock extends ValueObjectTypeEntity.ValueEntity {

    private final Entity entity;

    public ValueEntityMock(@Nullable Entity entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public Optional<Entity> getRawValue() {
        return Optional.of(entity);
    }
}
