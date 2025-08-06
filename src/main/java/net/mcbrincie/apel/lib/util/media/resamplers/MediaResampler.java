package net.mcbrincie.apel.lib.util.media.resamplers;

import net.mcbrincie.apel.lib.objects.ParticleMedia;

/** This is the {@link MediaResampler} interface. It takes the frame data and a t parameter (which is used for computing the easing
 * curves, otherwise its useless), and it modifies the frame data, returning nothing in the process. The job of a {@link MediaResampler}
 * is to resize the given frame to a specified one. There are builtin ones you can use which are present in {@link MediaResamplers}
 *
 * @see ParticleMedia
 * @see MediaResamplers
 */
@FunctionalInterface
public interface MediaResampler {
    void apply(ParticleMedia.FrameData data, float t);
}
