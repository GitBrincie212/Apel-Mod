package net.mcbrincie.apel.lib.renderers;

import net.minecraft.particle.ParticleEffect;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseApelRenderer implements ApelRenderer {

    // Used in position caches, since they cached the unrotated, non-translated positions of shapes
    private static final Vector3f IGNORED_ROTATION = new Vector3f();
    private static final Vector3f IGNORED_OFFSET = new Vector3f();

    // TODO: Consider some sort of cache eviction
    private final Map<Instruction, Vector3f[]> positionsCache;

    public BaseApelRenderer() {
        this.positionsCache = new HashMap<>();
    }

    @Override
    public void drawLine(ParticleEffect particleEffect, int step, Vector3f start, Vector3f end, int amount) {
        Instruction line = new Line(start, end, amount);
        Vector3f[] positions = this.positionsCache.computeIfAbsent(line, Instruction::computePoints);

        // Lines do not rotate or translate
        for (Vector3f position : positions) {
            drawParticle(particleEffect, step, position);
        }
    }

    @Override
    public void drawEllipsoid(
            ParticleEffect particleEffect, int step, Vector3f drawPos, float radius, float stretch1, float stretch2,
            Vector3f rotation, int amount
    ) {
        // Compute ellipsoid points, if necessary
        Instruction ellipsoid = new Ellipsoid(IGNORED_OFFSET, radius, stretch1, stretch2, IGNORED_ROTATION, amount);
        Vector3f[] positions = this.positionsCache.computeIfAbsent(ellipsoid, Instruction::computePoints);

        // Rotate and translate
        Quaternionfc quaternion = new Quaternionf().rotateZ(rotation.z).rotateY(rotation.y).rotateX(rotation.x);
        for (Vector3f position : positions) {
            Vector3f pos = new Vector3f(position).rotate(quaternion).add(drawPos);
            drawParticle(particleEffect, step, pos);
        }

    }

    @Override
    public void drawEllipse(
            ParticleEffect particleEffect, int step, Vector3f center, float radius, float stretch, Vector3f rotation,
            int amount
    ) {
        // Compute ellipse points, if necessary
        Instruction ellipse = new Ellipse(IGNORED_OFFSET, radius, stretch, IGNORED_ROTATION, amount);
        Vector3f[] positions = this.positionsCache.computeIfAbsent(ellipse, Instruction::computePoints);

        // Rotate and translate
        Quaternionfc quaternion = new Quaternionf().rotateZ(rotation.z).rotateY(rotation.y).rotateX(rotation.x);
        for (Vector3f position : positions) {
            Vector3f pos = new Vector3f(position).rotate(quaternion).add(center);
            drawParticle(particleEffect, step, pos);
        }
    }

    @Override
    public void drawBezier(
            ParticleEffect particleEffect, int step, Vector3f drawPos,
            net.mcbrincie.apel.lib.util.math.bezier.BezierCurve _bezierCurve, Vector3f rotation, int amount
    ) {
        // Compute Bezier curve points, if necessary
        Instruction bezierCurve = new BezierCurve(IGNORED_OFFSET, _bezierCurve, IGNORED_ROTATION, amount);
        Vector3f[] positions = this.positionsCache.computeIfAbsent(bezierCurve, Instruction::computePoints);

        // Rotate and translate
        Quaternionfc quaternion = new Quaternionf().rotateZ(rotation.z).rotateY(rotation.y).rotateX(rotation.x);
        for (Vector3f position : positions) {
            Vector3f pos = new Vector3f(position).rotate(quaternion).add(drawPos);
            drawParticle(particleEffect, step, pos);
        }
    }
}
