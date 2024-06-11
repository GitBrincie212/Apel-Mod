package net.mcbrincie.apel.lib.util.image;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class DustPalateGenerator implements PalateGenerator {
    @Override
    public ParticleEffect apply(int rgba, int x, int y, Vector3f position) {
        Vector3f color = Vec3d.unpackRgb(rgba).toVector3f();
        DustParticleEffect dust = new DustParticleEffect(color, 0.1f);
        return dust;
    }
}
