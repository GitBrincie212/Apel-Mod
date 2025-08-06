package net.mcbrincie.apel.lib.util.media.processors;

import net.mcbrincie.apel.lib.objects.ParticleMedia;

/** {@link MediaProcessor} is generally the first step of the {@link ParticleMedia} rendering pipeline, it
 * takes a specific frame index and outputs the corresponding frame data for that frame.
 * The frame data contains three things:
 * <ul>
 *     <li>The color array (containing each pixel's color)</li>
 *     <li>The width of the frame</li>
 *     <li>The height of the frame</li>
 * </ul>
 * The frame data is modified throughout the pipeline of the {@link ParticleMedia} for performance reason,
 * for now there is only one media processor and that is for images {@link ImageMediaProcessor}. There is a planned one
 * for videos as well, but it will come on the next update
 */
public interface MediaProcessor {
    ParticleMedia.FrameData process(int currentFrame);
}
