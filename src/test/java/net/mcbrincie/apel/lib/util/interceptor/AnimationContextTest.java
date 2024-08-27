package net.mcbrincie.apel.lib.util.interceptor;

import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.objects.ParticlePoint;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimationContextTest {
    // Declaring a null to avoid mocking and needing the Minecraft startup
    private static final ServerWorld NULL_WORLD = null;

    @Test
    void testAddingPrimitives() {
        // Given a DrawContext
        AnimationContext context = new AnimationContext(NULL_WORLD);
        Key<Integer> key = Key.integerKey("foo");

        // When metadata is added, it does not throw
        context.addMetadata(key, 3);

        // Then the value is retrievable
        int foo = context.getMetadata(key);
        assertEquals(3, foo);
    }

    @Test
    void testAddingWildcardGenerics() {
        // This mimics what ParticleCombiner would do with `OBJECT_IN_USE` using a ParticleObject<?>
        // Given a DrawContext
        AnimationContext context = new AnimationContext(NULL_WORLD);
        Key<ParticleObject<?>> objectInUse = Key.particleObjectKey("objectInUse");

        // Given a ParticlePoint
        ParticlePoint particlePoint = ParticlePoint.builder().particleEffect(null).build();

        // When metadata is added, it does not throw
        context.addMetadata(objectInUse, particlePoint);

        // Then the value is retrievable, but as a ParticleObject<?>
        ParticleObject<?> retrievedParticlePoint = context.getMetadata(objectInUse);
        assertEquals(particlePoint, retrievedParticlePoint);
    }

    @Test
    void testAddingArrays() {
        // Given a DrawContext
        AnimationContext context = new AnimationContext(NULL_WORLD);
        Key<Vector3f[]> verticesKey = Key.vector3fArrayKey("vertices");

        // Given an array
        Vector3f[] vertices = new Vector3f[8];

        // When metadata is added, it does not throw
        context.addMetadata(verticesKey, vertices);

        // Then the value is retrievable
        Vector3f[] retrievedVertices = context.getMetadata(verticesKey);
        assertEquals(vertices, retrievedVertices);
    }

    @Test
    void testMultipleKeys() {
        // Given a DrawContext
        AnimationContext context = new AnimationContext(NULL_WORLD);
        Key<Integer> key = Key.integerKey("foo");
        Key<Integer> key2 = Key.integerKey("bar");

        // When metadata is added, it does not throw
        context.addMetadata(key, 3);
        context.addMetadata(key2, 5);

        // Then the value is retrievable
        int keyValue = context.getMetadata(key);
        assertEquals(3, keyValue);
        int key2Value = context.getMetadata(key2);
        assertEquals(5, key2Value);
    }
}