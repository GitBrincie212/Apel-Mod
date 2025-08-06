package net.mcbrincie.apel.lib.util.media.processors;

import net.mcbrincie.apel.lib.objects.ParticleMedia;

public interface MediaProcessor {
    ParticleMedia.FrameData process(int currentFrame);
}
