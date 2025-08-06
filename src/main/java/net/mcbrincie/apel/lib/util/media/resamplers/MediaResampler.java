package net.mcbrincie.apel.lib.util.media.resamplers;

import net.mcbrincie.apel.lib.objects.ParticleMedia;

@FunctionalInterface
public interface MediaResampler {
    void apply(ParticleMedia.FrameData data, float t);
}
