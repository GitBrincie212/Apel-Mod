package net.mcbrincie.apel.lib.ease.vectors;
import net.mcbrincie.apel.lib.ease.ConstantEase;
import net.mcbrincie.apel.lib.ease.EaseCurve;
import org.joml.Vector2d;
import org.joml.Vector2i;


@SuppressWarnings("unused")
public class EaseVector2i extends Vector2i {
    EaseCurve<Integer> x = new ConstantEase<>(0);
    EaseCurve<Integer> y  = new ConstantEase<>(0);

    public EaseVector2i(EaseCurve<Integer> x, EaseCurve<Integer> y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX(int renderStep) {
        return this.x.compute(renderStep);
    }

    public Integer getY(int renderStep) {
        return this.y.compute(renderStep);
    }

    public EaseCurve<Integer> getX() {
        return this.x;
    }

    public EaseCurve<Integer> getY() {
        return this.y;
    }

    public EaseVector2i() {}

    public Vector2d compute(int renderStep) {
        return new Vector2d(this.getX(renderStep), this.getY(renderStep));
    }
}
