package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.ObjectInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.context.DrawContext;
import net.mcbrincie.apel.lib.util.media.displays.ColorParticleMapper;
import net.mcbrincie.apel.lib.util.media.displays.ColorParticleMappers;
import net.mcbrincie.apel.lib.util.media.filters.MediaFilter;
import net.mcbrincie.apel.lib.util.media.filters.MediaFilters;
import net.mcbrincie.apel.lib.util.media.processors.ImageMediaProcessor;
import net.mcbrincie.apel.lib.util.media.processors.MediaProcessor;
import net.mcbrincie.apel.lib.util.media.resamplers.MediaResampler;
import net.mcbrincie.apel.lib.util.media.resamplers.MediaResamplers;
import net.minecraft.particle.ParticleEffect;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;


/**
 * {@link ParticleMedia} allows for any media (videos and images) to be rendered with particles, you can freely rotate it around,
 * position it anywhere and even scale it. It supports the common file formats for image and video media which are:
 * <ul>
 *     <li>PNG</li>
 *     <li>WEBP</li>
 *     <li>JPEG / JPG</li>
 *     <li>GIF</li>
 *     <li>MP4</li>
 *     <li>MOV</li>
 * </ul>
 * {@link ParticleMedia} has an entire pipeline in rendering the media, which goes as follows:
 * <ol>
 *     <li><b>Media Processing</b> The {@link ParticleMedia} calls this first which then spits out the corresponding frame
 *     deduced by the current step, the frame consists of a color array; it should be noted that the frame chosen depends
 *     on the current step. A media processor may throw out an error if the current step is out of bounds. However, images do not
 *     have this problem as they are static</li>
 *
 *     <li><b>Media Resampling</b> The frame gets resized appropriately via a specific algorithm and then a new color array is returned</li>
 *
 *     <li><b>Media Filters (Optional)</b> There can be one or multiple filters at a time, they take the frame and apply color operations
 *     to it and return the new corresponding frame</li>
 *
 *     <li>
 *         <b>Display Method</b> This controls the conversion between colors and particles. There are 3 ways of converting
 *         <ul>
 *              <li><b>Palliated Particle Conversion</b> If you know exactly what colors are on the frame, you can map each of the colors to
 *              a particle, it allows you to make stylized renders of the media shown</li>
 *
 *              <li><b>Dust Particle Conversion</u> This is the typical one, where you convert each color to a dust particle and
 *              assign the dust particle with the color value of the corresponding pixel</li>
 *
 *              <li><b>Custom Conversion</b> If none of the above fit your requirements, you can always hook a function that converts
 *              the colors to whatever you want them to be via an algorithm</li>
 *         </ul>
 *     </li>
 * </ol>
 * <br />
 *
 * <b>Note:</b> It is heavily recommended to use Client-Side Rendering for this as rendering any media is an intensive process. Ideally
 * the media rendered should not be too large and should be in low resolution
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleMedia extends ParticleObject<ParticleMedia> {
    protected MediaProcessor mediaProcessor;
    protected MediaResampler mediaResampler;
    protected List<MediaFilter> mediaFilters;
    protected ColorParticleMapper colorParticleMapper;

    private ParticleMedia(Builder<?> builder) {
        super(builder.rotation, builder.offset, builder.beforeDraw, builder.afterDraw);
        this.setMediaProcessor(builder.mediaProcessor);
        this.setMediaResampler(builder.mediaResampler);
        this.setMediaFilters(builder.mediaFilters);
        this.setColorToParticleMapper(builder.colorParticleMapper);
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    /** Gets the media processor used in the rendering of a visual media
     *
     * @see MediaProcessor
     * @see ImageMediaProcessor
     *
     * @return The media processor used in rendering
     */
    public MediaProcessor getMediaProcessor() {
        return this.mediaProcessor;
    }

    /** Sets the media processor used in the rendering of a visual media.
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @see MediaProcessor
     * @see ImageMediaProcessor
     *
     * @param newMediaProcessor The new media processor to be used in rendering
     * @return The previous media processor used in rendering
     */
    public final MediaProcessor setMediaProcessor(MediaProcessor newMediaProcessor) {
        MediaProcessor prevMediaProcessor = this.mediaProcessor;
        this.mediaProcessor = newMediaProcessor;
        return prevMediaProcessor;
    }

    /** Gets the media resampler used in the rendering of a visual media
     *
     * @see MediaResampler
     * @see MediaResamplers
     *
     * @return The media resampler used in rendering
     */
    public MediaResampler getMediaResampler() {
        return this.mediaResampler;
    }

    /** Sets the media resampler used in the rendering of a visual media.
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @see MediaResampler
     * @see MediaResamplers
     *
     * @param newMediaResampler The new media resampler to be used in rendering
     * @return The previous media resampler used in rendering
     */
    public final MediaResampler setMediaResampler(MediaResampler newMediaResampler) {
        MediaResampler prevMediaResampler = this.mediaResampler;
        this.mediaResampler = newMediaResampler;
        return prevMediaResampler;
    }

    /** Gets the media filters used in the rendering of a visual media
     *
     * @see MediaFilter
     * @see MediaFilters
     *
     * @return The media resampler used in rendering
     */
    public List<MediaFilter> getMediaFilters() {
        return this.mediaFilters;
    }

    /** Sets the media filters used in the rendering of a visual media.
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @see MediaFilter
     * @see MediaFilters
     *
     * @param newMediaFilters The new media filters to be used in rendering
     * @return The previous media filters used in rendering
     */
    public final List<MediaFilter> setMediaFilters(List<MediaFilter> newMediaFilters) {
        List<MediaFilter> prevMediaFilters = this.mediaFilters;
        this.mediaFilters = newMediaFilters;
        return prevMediaFilters;
    }

    /** Sets the media filters used in the rendering of a visual media.
     * This implementation uses the {@link #setMediaFilters(List)}, as such making it overwritable is inconsistent
     *
     * @see MediaFilter
     * @see MediaFilters
     *
     * @param newMediaFilters The new media filters to be used in rendering
     * @return The previous media filters used in rendering
     */
    public final List<MediaFilter> setMediaFilters(MediaFilter... newMediaFilters) {
        return this.setMediaFilters(List.of(newMediaFilters));
    }

    /** Adds the media filters to the pool of the media filters used in the rendering of a visual media.
     * Do note that these filters are added last, in case you want to add them as first, then use {@link #addMediaFiltersFirst(MediaFilter...)}
     *
     * @see MediaFilter
     * @see MediaFilters
     *
     * @param mediaFilters The media filters to be added in the rendering
     */
    public void addMediaFilters(MediaFilter... mediaFilters) {
        this.mediaFilters.addAll(List.of(mediaFilters));
    }

    /** Adds the media filters to the pool of the media filters used in the rendering of a visual media.
     * Do note that these filters are added last, in case you want to add them as last, then use {@link #addMediaFilters(MediaFilter...)}
     *
     * @see MediaFilter
     * @see MediaFilters
     *
     * @param mediaFilters The media filters to be added in the rendering
     */
    public void addMediaFiltersFirst(MediaFilter... mediaFilters) {
        this.mediaFilters.addAll(List.of(mediaFilters));
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has.
     *
     * @param media The particle media to copy from
     */
    public ParticleMedia(ParticleMedia media) {
        super(media);
        this.mediaResampler = media.mediaResampler;
        this.mediaProcessor = media.mediaProcessor;
        this.mediaFilters = media.mediaFilters;
        this.colorParticleMapper = media.colorParticleMapper;
    }

    /** Gets the color to particle mapper method used in the rendering of a visual media
     *
     * @see ColorParticleMapper
     * @see ColorParticleMappers
     *
     * @return The color to particle mapper used in rendering
     */
    public ColorParticleMapper getColorToParticleMapper() {
        return this.colorParticleMapper;
    }

    /** Sets the color to particle mapper used in the rendering of a visual media.
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @see ColorParticleMapper
     * @see ColorParticleMappers
     *
     * @param newColorParticleMapper The new media color to particle mapper to be used in rendering
     * @return The previous color to particle mapper used in rendering
     */
    public final ColorParticleMapper setColorToParticleMapper(ColorParticleMapper newColorParticleMapper) {
        ColorParticleMapper prevColorParticleMapper = this.colorParticleMapper;
        this.colorParticleMapper = newColorParticleMapper;
        return prevColorParticleMapper;
    }

    public static class FrameData {
        public int[] frame;
        public int width;
        public int height;

        public FrameData(int[] frame, int width, int height) {
            this.frame = frame;
            this.width = width;
            this.height = height;
        }
    }

    @Override
    public void display(ApelServerRenderer renderer, DrawContext<?> drawContext, Vector3f actualSize) {
        ParticleMedia.FrameData data = this.mediaProcessor.process(drawContext.getCurrentStep());
        float t = ((float) drawContext.getCurrentStep()) / ((float) drawContext.getNumberOfStep());
        this.mediaResampler.apply(data, t);
        for (MediaFilter filter : this.mediaFilters) {
            filter.apply(data, t);
        }

        int width = data.width;
        int height = data.height;
        int currStep = drawContext.getCurrentStep();
        Vector3f drawPos = drawContext.getPosition();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                ParticleEffect particleEffect = colorParticleMapper.apply(data.frame[y * width + x]);
                Vector3f pos = new Vector3f(x, y, 0).add(drawPos).mul(-0.01f);
                renderer.drawParticle(particleEffect, currStep, pos);
            }
        }
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleMedia> {
        protected ObjectInterceptor<ParticleMedia> afterDraw;
        protected ObjectInterceptor<ParticleMedia> beforeDraw;
        protected MediaProcessor mediaProcessor;
        protected MediaResampler mediaResampler;
        protected List<MediaFilter> mediaFilters = new ArrayList<>();
        protected ColorParticleMapper colorParticleMapper;

        private Builder() {}

        /**
         * Set the media processor on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public final B mediaProcessor(MediaProcessor mediaProcessor) {
            this.mediaProcessor = mediaProcessor;
            return self();
        }

        /**
         * Set the media resampler on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public final B mediaResampler(MediaResampler mediaResampler) {
            this.mediaResampler = mediaResampler;
            return self();
        }

        /**
         * Add a media filter on the builder. This method is cumulative;
         * repeated calls will append a new media filter.
         */
        public final B addMediaFilter(MediaFilter mediaFilter) {
            this.mediaFilters.add(mediaFilter);
            return self();
        }

        /**
         * Add multiple media filters on the builder. This method is cumulative;
         * repeated calls will append new media filters.
         */
        public final B addMediaFilters(MediaFilter... mediaFilters) {
            this.mediaFilters.addAll(List.of(mediaFilters));
            return self();
        }

        /**
         * Add multiple media filters on the builder. This method is cumulative;
         * repeated calls will append new media filters.
         */
        public final B addMediaFilters(List<MediaFilter> mediaFilters) {
            this.mediaFilters.addAll(mediaFilters);
            return self();
        }

        /**
         * Set the color to particle mapper on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public final B colorParticleMapper(ColorParticleMapper colorParticleMapper) {
            this.colorParticleMapper = colorParticleMapper;
            return self();
        }

        @Override
        public ParticleMedia build() {
            return new ParticleMedia(this);
        }
    }
}
