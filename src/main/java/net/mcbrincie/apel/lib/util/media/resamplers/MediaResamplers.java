package net.mcbrincie.apel.lib.util.media.resamplers;

import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.objects.ParticleMedia;
import net.minecraft.util.math.ColorHelper;

/** {@link MediaResamplers} offers built-in some commonly used media resamplers such as:
 * <ul>
 *     <li>Nearest Neighbor Resampling</li>
 *     <li>Bilinear Resampling</li>
 * </ul>
 * All of the resamplers support the use of EasingCurves, except for {@link #retain()} which is a special resampler
 * designed to return the same frame data (effectively skipping the resampling part)
 *
 * @see ParticleMedia
 * @see MediaResampler
 */
@SuppressWarnings("unused")
public class MediaResamplers {
    /** Constructs the retain media resampler, returning the function to be used. It acts more as an identity function
     * where it gives the same output back as the input. It is useful when you want to keep the original dimensions of the
     * media (effectively skipping the media resampling process in the pipeline)
     *
     * @return The constructed retain media resampler function
     */
    public static MediaResampler retain() {
        return (frame, t) -> {};
    }

    /** Constructs the nearest neighbor media resampler, returning the function to be used. It is one of the fastest
     * media resamplers out there, but one of the less pretty looking ones. It is generally the most commonly used media
     * resampler due to its performance while still giving good results
     * <br /> <br />
     * This overload requires you to give it the dimensions as easing curves. For a constant one, check out {@link #nearestNeighbour(int, int)}
     *
     * @param destWidth The target width to resize to
     * @param destHeight The target height to resize to
     * @return The constructed nearest neighbor media resampler function
    */
    public static MediaResampler nearestNeighbour(EasingCurve<Integer> destWidth, EasingCurve<Integer> destHeight) {
        return (data, t) -> {
            int destW = destWidth.getValue(t);
            int destH = destHeight.getValue(t);
            int[] dst = new int[destW * destH];
            float xRatio = data.width / (float) destW;
            float yRatio = data.height / (float) destH;

            for (int y = 0; y < destH; y++) {
                int srcY = Math.min((int)(y * yRatio), data.height - 1);
                int dstRow = y * destW;
                int srcRow = srcY * data.width;
                for (int x = 0; x < destW; x++) {
                    int srcX = Math.min((int)(x * xRatio), data.width - 1);
                    dst[dstRow + x] = data.frame[srcRow + srcX];
                }
            }
            data.frame = dst;
        };
    }

    /** Constructs the nearest neighbor media resampler, returning the function to be used. It is one of the fastest
     * media resamplers out there, but one of the less pretty looking ones. It is generally the most commonly used media
     * resampler due to its performance while still giving good results
     * <br /> <br />
     * This overload requires you to give it the dimensions as constants. For an easing curve one, check out {@link #nearestNeighbour(EasingCurve, EasingCurve)}
     *
     * @param destWidth The target width to resize to
     * @param destHeight The target height to resize to
     * @return The constructed nearest neighbor media resampler function
     */
    public static MediaResampler nearestNeighbour(int destWidth, int destHeight) {
        return nearestNeighbour(new ConstantEasingCurve<>(destWidth), new ConstantEasingCurve<>(destHeight));
    }

    /** Constructs the bilinear media resampler, returning the function to be used. It is still fast but gives more
     * smooth results, it is commonly used on large medias where quality matters more than performance
     * <br /> <br />
     * This overload requires you to give it the dimensions as easing curves. For a constant one, check out {@link #bilinear(int, int)}
     *
     * @param destWidth The target width to resize to
     * @param destHeight The target height to resize to
     * @return The constructed bilinear media resampler function
     */
    public static MediaResampler bilinear(EasingCurve<Integer> destWidth, EasingCurve<Integer> destHeight) {
        return (data, t) -> {
            int destW = destWidth.getValue(t);
            int destH = destHeight.getValue(t);
            int[] dst = new int[destW * destH];
            float xRatio = (data.width  - 1) / (float)(destW);
            float yRatio = (data.height  - 1) / (float)(destH);

            for (int y = 0; y < destH; y++) {
                float fy = y * yRatio;
                int y0 = (int) fy;
                int y1 = Math.min(y0 + 1, data.height - 1);
                float dy = fy - y0;
                int row0 = y0 * data.width;
                int row1 = y1 * data.width;

                for (int x = 0; x < destW; x++) {
                    float fx = x * xRatio;
                    int x0 = (int) fx;
                    int x1 = Math.min(x0 + 1, data.width - 1);
                    float dx = fx - x0;

                    int c00 = data.frame[row0 + x0];
                    int c10 = data.frame[row0 + x1];
                    int c01 = data.frame[row1 + x0];
                    int c11 = data.frame[row1 + x1];

                    int c0 = ColorHelper.lerp(dx, c00, c10);
                    int c1 = ColorHelper.lerp(dx, c01, c11);

                    dst[y * destW + x] = ColorHelper.lerp(dy, c0, c1);
                }
            }
            data.frame = dst;
        };
    }


    /** Constructs the bilinear media resampler, returning the function to be used. It is still fast but gives more
     * smooth results, it is commonly used on large medias where quality matters more than performance
     * <br /> <br />
     * This overload requires you to give it the dimensions as constants. For an easing curve one, check out {@link #bilinear(EasingCurve, EasingCurve)}
     *
     * @param destWidth The target width to resize to
     * @param destHeight The target height to resize to
     * @return The constructed bilinear media resampler function
    */
    public static MediaResampler bilinear(int destWidth, int destHeight) {
        return bilinear(new ConstantEasingCurve<>(destWidth), new ConstantEasingCurve<>(destHeight));
    }
}
