package net.mcbrincie.apel.lib.util.media.displays;

import net.minecraft.particle.DustParticleEffect;

import java.util.HashMap;

/** Provides some builtin color to particle mapper functions. For now, there is a specific one and that is
 * the dust particle mapper, where it maps a color to a dust particle with that color. However there are more
 * planned on the future
 */
public class ColorParticleMappers {
    private static final HashMap<Integer, DustParticleEffect> cache = new HashMap<>();

    /** Constructs a color to dust particle mapper function
     *
     * @return The color to dust particle mapper
     */
    public static ColorParticleMapper dustParticleMapper() {
        return ((rgba) ->
            cache.computeIfAbsent(rgba, k -> new DustParticleEffect(rgba, 0.2f))
        );
    }
}
