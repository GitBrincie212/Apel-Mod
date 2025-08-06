package net.mcbrincie.apel.lib.util.media.filters;

import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.minecraft.util.math.ColorHelper;

@SuppressWarnings("unused")
public class MediaFilters {
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

    public static MediaFilter brightness(float factor) {
        return brightness(new ConstantEasingCurve<>(factor));
    }

    public static MediaFilter contrast(EasingCurve<Float> factor) {
        return (data, t) -> {
            float computedFactor = factor.getValue(t);
            if (computedFactor > 1 || computedFactor < 1) {
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

    public static MediaFilter contrast(float factor) {
        return contrast(new ConstantEasingCurve<>(factor));
    }

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

    public static MediaFilter saturate(float factor) {
        return saturate(new ConstantEasingCurve<>(factor));
    }

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
