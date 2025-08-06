package net.mcbrincie.apel.lib.util.media.displays;

import net.minecraft.particle.DustParticleEffect;

import java.util.HashMap;

public class ColorParticleMappers {
    private static final HashMap<Integer, DustParticleEffect> cache = new HashMap<>();

    public static ColorParticleMapper dustParticleMapper() {
        return ((rgba) ->
            cache.computeIfAbsent(rgba, k -> new DustParticleEffect(rgba, 0.2f))
        );
    }
}
