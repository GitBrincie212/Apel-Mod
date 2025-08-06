package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.ComputedEasingPO;
import net.mcbrincie.apel.lib.util.interceptor.context.DrawContext;
import net.mcbrincie.apel.lib.util.interceptor.context.Key;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DrawContextTest {
    // Declaring a null to avoid mocking and needing the Minecraft startup
    private static final ServerWorld NULL_WORLD = null;
    private static final ComputedEasingPO NULL_COMPUTED_EASINGS = null;

    @Test
    void testAddingPrimitives() {
        // Given a DrawContext
        DrawContext context = new DrawContext(NULL_WORLD, new Vector3f(0), 0, 0, 0, NULL_COMPUTED_EASINGS);
        Key<Integer> key = Key.integerKey("foo");

        // When metadata is added, it does not throw
        context.addMetadata(key, 3);

        // Then the value is retrievable
        int foo = (int) context.getMetadata(key);
        assertEquals(3, foo);
    }

    @Test
    void testAddingWildcardGenerics() {
        // This mimics what ParticleCombiner would do with `OBJECT_IN_USE` using a ParticleObject<?>
        // Given a DrawContext
        DrawContext<?> context = new DrawContext<>(NULL_WORLD, new Vector3f(0), 0, 0, 0, NULL_COMPUTED_EASINGS);
        Key<ParticleObject<?>> objectInUse = Key.particleObjectKey("objectInUse");

        // Given a ParticlePoint
        ParticlePoint particlePoint = ParticlePoint.builder().particleEffect(null).build();

        // When metadata is added, it does not throw
        context.addMetadata(objectInUse, particlePoint);

        // Then the value is retrievable
        ParticleObject<?> retrievedParticlePoint = context.getMetadata(objectInUse);
        assertEquals(particlePoint, retrievedParticlePoint);
    }

    @Test
    void testAddingArrays() {
        // Given a DrawContext
        DrawContext<?> context = new DrawContext<>(NULL_WORLD, new Vector3f(0), 0, 0, 0, NULL_COMPUTED_EASINGS);
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
        DrawContext<?> context = new DrawContext<>(NULL_WORLD, new Vector3f(0), 0, 0, 0, NULL_COMPUTED_EASINGS);
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

    @Test
    void testEquals() {
        // Given four Keys, two of which should be equal, and two that vary on type or name
        Key<Integer> key1 = Key.integerKey("foo");
        Key<Integer> key2 = Key.integerKey("foo");
        Key<Integer> keyWrongName = Key.integerKey("bar");
        Key<Integer> keyDifferentSource = new Key<>("foo") {};
        Key<Boolean> keyWrongType = Key.booleanKey("foo");

        // Then equality works
        assertEquals(key1, key2, "Keys of same type and name should be equal");
        assertEquals(key1, keyDifferentSource, "Keys of same type and name, but different declarations, should be equal");
        assertNotEquals(key1, keyWrongName, "Keys with different names should not be equal");
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(key1, keyWrongType, "Keys with different types should not be equal");
    }
}