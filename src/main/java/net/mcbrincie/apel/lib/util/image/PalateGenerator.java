package net.mcbrincie.apel.lib.util.image;

import net.minecraft.particle.ParticleEffect;
import org.joml.Vector2i;
import org.joml.Vector3f;

@FunctionalInterface
public interface PalateGenerator {
    ParticleEffect apply(int rgba, int x, int y, Vector3f position);
}
