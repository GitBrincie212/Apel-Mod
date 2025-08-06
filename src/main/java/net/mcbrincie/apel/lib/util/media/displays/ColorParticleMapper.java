package net.mcbrincie.apel.lib.util.media.displays;

import net.minecraft.particle.ParticleEffect;

public interface ColorParticleMapper {
    ParticleEffect apply(int rgba);
}
