package net.mcbrincie.apel.lib.util.image;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.HashMap;

public class DustPalateGenerator implements PalateGenerator {
    private static final HashMap<Integer, DustParticleEffect> cache = new HashMap<>();

    @Override
    public ParticleEffect apply(int rgba, int x, int y, Vector3f position) {
        return cache.computeIfAbsent(rgba, k -> {
            Vector3f color = Vec3d.unpackRgb(rgba).toVector3f();
            return new DustParticleEffect(color, 0.2f);
        });
    }
}
