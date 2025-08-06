package net.mcbrincie.apel.lib.util.media.resamplers;

import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.minecraft.util.math.ColorHelper;

@SuppressWarnings("unused")
public class MediaResamplers {
    public static MediaResampler retain() {
        return (frame, t) -> {};
    }

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

    public static MediaResampler nearestNeighbour(int destWidth, int destHeight) {
        return nearestNeighbour(new ConstantEasingCurve<>(destWidth), new ConstantEasingCurve<>(destHeight));
    }

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

    public static MediaResampler bilinear(int destWidth, int destHeight) {
        return bilinear(new ConstantEasingCurve<>(destWidth), new ConstantEasingCurve<>(destHeight));
    }
}
