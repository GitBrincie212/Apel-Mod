package net.mcbrincie.apel.lib.util.media.filters;

import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.minecraft.util.math.ColorHelper;

/** {@link MediaFilters} offers builtin commonly used filters. Which are for now:
 * <ul>
 *     <li>{@link #brightness(EasingCurve)} | {@link #brightness(float)}</li>
 *     <li>{@link #contrast(EasingCurve)} | {@link #contrast(float)}</li>
 *     <li>{@link #saturate(EasingCurve)} | {@link #saturate(float)}</li>
 *     <li>{@link #invert()}</li>
 * </ul>
 * There are more ones planned tho for the future such as:
 * <ul>
 *     <li>Posterize</li>
 *     <li>Tinting</li>
 *     <li>Temperature Shift</li>
 *     <li>Exposure</li>
 *     <li>Grayscale</li>
 *     <li>Sepia</li>
 *     <li>Vibrance</li>
 *     <li>Hue Rotation</li>
 *     <li>Average</li>
 * </ul>
 * All of the filters except {@link #invert()} support a factor that can be either an easing curve or a constant
 */
@SuppressWarnings("unused")
public class MediaFilters {
    /** Adjust the brightness of the frame, where higher values than one brighten the image and values that are lower, dim it.
     * <br /> <br />
     * This overload requires you to give the factor as an easing curve. For a constant one, check out {@link #brightness(float)}
     *
     * @param factor The brightness factor to be used
     * @return The constructed media brightness filter
     */
    public static MediaFilter brightness(EasingCurve<Float> factor) {
        return (data, t) -> {
            float computedFactor = factor.getValue(t);
            for (int x = 0; x < data.width; x++) {
                for (int y = 0; y < data.height; y++) {
                    int argb = data.frame[y * data.width + x];
                    int R = Math.clamp((int) (ColorHelper.getRed(argb) * computedFactor), 0, 255);
                    int G = Math.clamp((int) (ColorHelper.getGreen(argb) * computedFactor), 0, 255);
                    int B = Math.clamp((int) (ColorHelper.getBlue(argb) * computedFactor), 0, 255);
                    data.frame[y * data.width + x] = ColorHelper.fromFloats(ColorHelper.getAlpha(argb), R, G, B);
                }
            }
        };
    }

    /** Adjust the brightness of the frame, where higher values than one brighten the image and values that are lower, dim it.
     * <br /> <br />
     * This overload requires you to give the factor as a constant. For an easing curve one, check out {@link #brightness(EasingCurve)}
     *
     * @param factor The brightness factor to be used
     * @return The constructed media brightness filter
     */
    public static MediaFilter brightness(float factor) {
        return brightness(new ConstantEasingCurve<>(factor));
    }

    /** Adjust the contrast of the frame, where the factor must be between -1 and 1 (inclusive on both), higher values will
     * make the image have more contrast while lower ones will give less contrast to the image.
     * <br /> <br />
     * This overload requires you to give the factor as an easing curve one. For a constant one, check out {@link #contrast(float)}
     *
     * @param factor The contrast factor to be used
     * @return The constructed media contrast filter
     */
    public static MediaFilter contrast(EasingCurve<Float> factor) {
        return (data, t) -> {
            float computedFactor = factor.getValue(t);
            if (computedFactor > 1 || computedFactor < -1) {
                throw new RuntimeException("The contrast factor must be between -1 and 1 (inclusive on both)");
            }
            float c = Math.clamp((int) (computedFactor * 255), 0, 255);
            float f = (259 * (c + 255f)) / (255f * (259 - c));
            for (int x = 0; x < data.width; x++) {
                for (int y = 0; y < data.height; y++) {
                    int argb = data.frame[y * data.width + x];
                    int R = Math.clamp((int) (f * (ColorHelper.getRed(argb) - 128) + 128), 0, 255);
                    int G = Math.clamp((int) (f * (ColorHelper.getGreen(argb) - 128) + 128), 0, 255);
                    int B = Math.clamp((int) (f * (ColorHelper.getBlue(argb) - 128) + 128), 0, 255);
                    data.frame[y * data.width + x] = ColorHelper.fromFloats(ColorHelper.getAlpha(argb), R, G, B);
                }
            }
        };
    }

    /** Adjust the contrast of the frame, where the factor must be between -1 and 1 (inclusive on both), higher values will
     * make the image have more contrast while lower ones will give less contrast to the image.
     * <br /> <br />
     * This overload requires you to give the factor as a constant. For an easing curve one, check out {@link #contrast(EasingCurve)}
     *
     * @param factor The contrast factor to be used
     * @return The constructed media contrast filter
     */
    public static MediaFilter contrast(float factor) {
        return contrast(new ConstantEasingCurve<>(factor));
    }


    /** Adjust the saturation of the frame, higher values will make the image more saturated while lower ones will desaturate the image.
     * <br /> <br />
     * This overload requires you to give the factor as an easing curve one. For a constant one, check out {@link #saturate(float)}
     *
     * @param factor The saturation factor to be used
     * @return The constructed media saturation filter
     */
    public static MediaFilter saturate(EasingCurve<Float> factor) {
        return (data, t) -> {
            float computedFactor = factor.getValue(t);
            for (int x = 0; x < data.width; x++) {
                for (int y = 0; y < data.height; y++) {
                    int argb = data.frame[y * data.width + x];
                    int R = ColorHelper.getRed(argb);
                    int G = ColorHelper.getGreen(argb);
                    int B = ColorHelper.getBlue(argb);
                    float lum = 0.299f * R + 0.587f * G + 0.114f * B;
                    G = Math.clamp((int) (lum + (G - lum) * computedFactor), 0, 255);
                    R = Math.clamp((int) (lum + (R - lum) * computedFactor), 0, 255);
                    B = Math.clamp((int) (lum + (B - lum) * computedFactor), 0, 255);
                    data.frame[y * data.width + x] = ColorHelper.fromFloats(ColorHelper.getAlpha(argb), R, G, B);
                }
            }
        };
    }

    /** Adjust the saturation of the frame, higher values will make the image more saturated while lower ones will desaturate the image.
     * <br /> <br />
     * This overload requires you to give the factor as a constant one. For an easing curve one, check out {@link #saturate(EasingCurve)}
     *
     * @param factor The saturation factor to be used
     * @return The constructed media saturation filter
     */
    public static MediaFilter saturate(float factor) {
        return saturate(new ConstantEasingCurve<>(factor));
    }

    /** Inverts the frame's colors. Unlike most media filters, this doesn't require any parameters as it is an inversion
     *
     * @return The constructed media invert filter
     */
    public static MediaFilter invert() {
        return (data, t) -> {
            for (int x = 0; x < data.width; x++) {
                for (int y = 0; y < data.height; y++) {
                    int argb = data.frame[y * data.width + x];
                    int R = 255 - ColorHelper.getRed(argb);
                    int G = 255 - ColorHelper.getGreen(argb);
                    int B = 255 - ColorHelper.getBlue(argb);
                    data.frame[y * data.width + x] = ColorHelper.fromFloats(ColorHelper.getAlpha(argb), R, G, B);
                }
            }
        };
    }
}
