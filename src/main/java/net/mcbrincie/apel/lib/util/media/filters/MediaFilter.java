package net.mcbrincie.apel.lib.util.media.filters;

import net.mcbrincie.apel.lib.objects.ParticleMedia;

/** This is the media filter. It takes the frame data and a t parameter (for the computing of the easing curves) and
 * modifies this frame data depending on the filter itself, returning nothing in the process. There are builtin filters
 * to choose from in {@link MediaFilters}
 *
 * @see MediaFilters
 * @see ParticleMedia
 */
@FunctionalInterface
public interface MediaFilter {
    void apply(ParticleMedia.FrameData data, float t);
}
