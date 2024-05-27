package net.mcbrincie.apel.lib.util.interceptor;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;

public class InterceptData extends HashMap<String, Object> {
    public final int currentStep;
    public final Vec3d position;

    public InterceptData(ServerWorld world, Vec3d position, int step) {
        this.currentStep = step;
        this.position = position;
        this.put("world", world);
    }
}
