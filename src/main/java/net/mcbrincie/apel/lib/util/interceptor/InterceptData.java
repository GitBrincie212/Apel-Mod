package net.mcbrincie.apel.lib.util.interceptor;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.EnumMap;

public class InterceptData<T extends Enum<T>> {
    public final int currentStep;
    public final Vector3f position;
    public final ServerWorld world;
    private final EnumMap<T, Object> metadata;

    public InterceptData(ServerWorld world, Vector3f position, int step, Class<T> keyType) {
        this.currentStep = step;
        this.position = position;
        this.world = world;
        this.metadata = new EnumMap<>(keyType);
    }

    public void addMetadata(T name, Object value) {
        this.metadata.put(name, value);
    }

    public Object getMetadata(T name) {
        return this.metadata.get(name);
    }

    public void clearMetadata() {
        this.metadata.clear();
    }
}
