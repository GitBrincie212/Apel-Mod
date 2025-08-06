package net.mcbrincie.apel.lib.util.media.displays;

import net.mcbrincie.apel.lib.objects.ParticleMedia;
import net.minecraft.particle.ParticleEffect;

/** Maps a given color into a particle effect. This is the final process of the {@link ParticleMedia} rendering pipeline.
 * There are built-in color particle mappers already, which are in {@link ColorParticleMappers}
 *
 * @see ParticleMedia
 * @see ColorParticleMappers
 */
public interface ColorParticleMapper {
    ParticleEffect apply(int rgba);
}
