package net.mcbrincie.apel.lib.objects;

import net.minecraft.particle.ParticleEffect;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ParticleCuboidTest {

    ParticleEffect mockParticleEffect;

    @BeforeEach
    void setup() {
        this.mockParticleEffect = mock(ParticleEffect.class);
    }

    @Test
    void setSizeWithFloat() {
        // Given a ParticleCuboid
        Vector3f beginningSize = new Vector3f(5.0f, 10.0f, 20.0f);
        ParticleCuboid cuboid = new ParticleCuboid(this.mockParticleEffect, 10, beginningSize);

        // When a new size is set
        Vector3f prevSize = cuboid.setSize(12.0f);

        // Then the old size is returned
        assertEquals(beginningSize, prevSize);

        // Then the new size is correct
        assertEquals(new Vector3f(12.0f), cuboid.getSize());
    }

    @Test
    void setSizeWithVector3f() {
        // Given a ParticleCuboid
        Vector3f beginningSize = new Vector3f(5.0f, 10.0f, 20.0f);
        ParticleCuboid cuboid = new ParticleCuboid(this.mockParticleEffect, 10, beginningSize);

        // When a new size is set
        Vector3f prevSize = cuboid.setSize(new Vector3f(12.0f, 12.0f, 12.0f));

        // Then the old size is returned
        assertEquals(beginningSize, prevSize);

        // Then the new size is correct
        assertEquals(new Vector3f(12.0f), cuboid.getSize());
    }
}