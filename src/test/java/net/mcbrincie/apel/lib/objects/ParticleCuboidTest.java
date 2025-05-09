package net.mcbrincie.apel.lib.objects;

import org.joml.Vector3i;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticleCuboidTest {

    @Test
    void builderAmountAsIntegerWorks() {
        // Given a minimally configured ParticleCuboid Builder and particle amount
        int amount = 10;
        ParticleCuboid.Builder<?> builder = ParticleCuboid.builder().size(1f).amount(amount);

        // When built
        ParticleCuboid cuboid = builder.build();

        // Then the amount is a vector with components equal to the integer
        assertEquals(new Vector3i(amount), cuboid.getAmounts().getValue(0f));
    }

}