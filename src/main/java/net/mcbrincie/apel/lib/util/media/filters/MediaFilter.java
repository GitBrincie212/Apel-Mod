package net.mcbrincie.apel.lib.util.media.filters;

import net.mcbrincie.apel.lib.objects.ParticleMedia;

@FunctionalInterface
public interface MediaFilter {
    void apply(ParticleMedia.FrameData data, float t);
}
