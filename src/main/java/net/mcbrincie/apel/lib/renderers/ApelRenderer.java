package net.mcbrincie.apel.lib.renderers;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.util.math.TrigTable;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * ApelRenderer allows Apel animations to be rendered to multiple "canvases."
 * <p>
 * ApelRenderer offers a factory method to render to the {@link ServerWorld} powering the current instance of Minecraft.
 * This can be used as follows:
 * <pre>
 * void animate(ServerWorld world, PlayerEntity user) {
 *     // Setup a line
 *     Vector3f start = new Vector3f(-3, 1, 1);
 *     Vector3f end = new Vector3f(3, 1, 1);
 *     ParticleLine line1 = new ParticleLine(ParticleTypes.COMPOSTER, start, end, 5);
 *
 *     // Setup an Animator
 *     Vector3f origin = user.getPos().toVector3f().add(0, 1, 4);
 *     PointAnimator animator = new PointAnimator(1, line1, origin, 100);
 *
 *     // Perform the animation
 *     animator.beginAnimation(ApelRenderer.create(world));
 * }
 * </pre>
 */
public interface ApelRenderer {
    TrigTable trigTable = Apel.TRIG_TABLE;

    /**
     * Instructs the renderer to draw the given particle effect at the given position.
     *
     * @param particleEffect The ParticleEffect to draw
     * @param drawPos The position at which to draw
     */
    void drawParticle(ParticleEffect particleEffect, int step, Vector3f drawPos);

    /**
     * Instructs the renderer to draw a line of the given particle effect from {@code start} to {@code end} using
     * {@code count} particles.  Particles
     * will be spaced evenly along the defined line.
     *
     * @param particleEffect The ParticleEffect to use
     * @param start The 3D point at which to start
     * @param end The 3D point at which to end
     * @param amount The number of particles to draw along the line
     */
    default void drawLine(ParticleEffect particleEffect, int step, Vector3f start, Vector3f end, int amount) {
        int amountSubOne = (amount - 1);
        // Do not use 'sub', it modifies in-place
        float stepX = (end.x - start.x) / amountSubOne;
        float stepY = (end.y - start.y) / amountSubOne;
        float stepZ = (end.z - start.z) / amountSubOne;
        Vector3f curr = new Vector3f(start);
        for (int i = 0; i < amount; i++) {
            drawParticle(particleEffect, step, curr);
            curr.add(stepX, stepY, stepZ);
        }
    }

    /**
     * Instructs the renderer to draw an ellipsoid of the given particle effect at {@code drawPos} with the given
     * {@code xSemiAxis}, {@code ySemiAxis}, {@code zSemiAxis}, {@code rotation}, and {@code amount} of particles.
     * <p>
     * The default implementation is inefficient due to repeated calculations, so it is strongly recommended that
     * implementations needing the list of specific particles override this to provide some amount of caching.
     * </p>
     * Reference: <a href="https://stackoverflow.com/a/44164075">"golden spiral" algorithm</a>
     *
     * @param particleEffect The ParticleEffect to use
     * @param step The current animation step
     * @param drawPos The center position of the sphere
     * @param xSemiAxis The length of the x semi-axis of the ellipsoid
     * @param ySemiAxis The length of the y semi-axis of the ellipsoid
     * @param zSemiAxis The length of the z semi-axis of the ellipsoid
     * @param rotation The rotation of the ellipsoid
     * @param amount The number of particles in the ellipsoid
     */
    default void drawEllipsoid(
            ParticleEffect particleEffect, int step, Vector3f drawPos, float xSemiAxis, float ySemiAxis,
            float zSemiAxis, Vector3f rotation, int amount
    ) {
        final double sqrt5Plus1 = 3.23606;
        Vector3f scalar = new Vector3f(xSemiAxis, ySemiAxis, zSemiAxis);
        Quaternionfc quaternion = new Quaternionf().rotateZ(rotation.z).rotateY(rotation.y).rotateX(rotation.x);
        for (int i = 0; i < amount; i++) {
            // Offset into the real-number distribution
            float k = i + .5f;
            // Project point on a unit sphere
            float phi = trigTable.getArcCosine(1f - ((2f * k) / amount));
            float theta = (float) (Math.PI * k * sqrt5Plus1);
            float sinPhi = trigTable.getSine(phi);
            float x = (trigTable.getCosine(theta) * sinPhi);
            float y = (trigTable.getSine(theta) * sinPhi);
            float z = trigTable.getCosine(phi);
            // Scale, rotate, translate
            Vector3f pos = new Vector3f(x, y, z).mul(scalar).rotate(quaternion).add(drawPos);
            drawParticle(particleEffect, step, pos);
        }
    }

    /**
     * Instructs the renderer to draw an ellipse at {@code drawPos} with {@code radius} and {@code rotation} applied
     * using {@code amount} particles.  The ellipse will be stretched by {@code stretch}.  Particles will be spaced
     * evenly around the ellipse.
     *
     * @param particleEffect The ParticleEffect to use
     * @param center The point at the center of the ellipse
     * @param radius The radius of the ellipse
     * @param stretch The stretch of the ellipse
     * @param rotation Rotation applied to the ellipse (to change the plane in which it's drawn)
     * @param amount The number of particles to use to draw the ellipse
     */
    default void drawEllipse(
            ParticleEffect particleEffect, int step, Vector3f center, float radius, float stretch, Vector3f rotation,
            int amount
    ) {
        float angleInterval = (float) Math.TAU / (float) amount;
        Quaternionfc quaternion = new Quaternionf().rotateZ(rotation.z).rotateY(rotation.y).rotateX(rotation.x);
        for (int i = 0; i < amount; i++) {
            float currRot = angleInterval * i;
            float x = trigTable.getCosine(currRot) * radius;
            float y = trigTable.getSine(currRot) * stretch;
            Vector3f pos = new Vector3f(x, y, 0).rotate(quaternion).add(center);
            drawParticle(particleEffect, step, pos);
        }
    }

    /**
     * Instructs the renderer to draw a Bézier curve at {@code drawPos} described by {@code bezierCurve} with the given
     * {@code rotation} using {@code amount} of particles.
     *
     * @param particleEffect The ParticleEffect to use
     * @param step The current animation step
     * @param drawPos The point used as the origin for the defined curve
     * @param bezierCurve The Bézier curve to draw
     * @param rotation Rotation applied to the Bézier curve
     * @param amount The number of particles to use to draw the Bézier curve
     */
    default void drawBezier(
            ParticleEffect particleEffect, int step, Vector3f drawPos,
            net.mcbrincie.apel.lib.util.math.bezier.BezierCurve bezierCurve, Vector3f rotation, int amount
    ) {
        float interval = 1.0f / amount;
        Quaternionfc quaternion = new Quaternionf().rotateZ(rotation.z).rotateY(rotation.y).rotateX(rotation.x);

        for (int i = 0; i < amount; i++) {
            Vector3f pos = bezierCurve.compute(interval * i);
            pos.rotate(quaternion).add(drawPos);
            this.drawParticle(particleEffect, step, pos);
        }
    }

    /**
     * Instructs the renderer to draw a cone with the tip of the cone at {@code drawPos} using the given {@code height}
     * and {@code radius}.  The cone is drawn with the {@code rotation} applied and using the provided {@code amount}
     * of particles.
     * <p>
     * Note: This is not a true cone: the tip is rounded.
     *
     * @param particleEffect The ParticleEffect to use
     * @param step The current animation step
     * @param drawPos The point where the base of the cone is
     * @param height The height of the cone
     * @param radius The radius of the cone
     * @param rotation The rotation of the cone
     * @param amount The number of particles in the cone
     */
    default void drawCone(
            ParticleEffect particleEffect, int step, Vector3f drawPos, float height, float radius, Vector3f rotation,
            int amount
    ) {
        final double sqrt5Plus1 = 3.23606;
        Vector3f scale = new Vector3f(radius, height, radius);
        Quaternionfc quaternion = new Quaternionf().rotateZ(rotation.z).rotateY(rotation.y).rotateX(rotation.x);
        for (int i = 0; i < amount; i++) {
            // Offset into the real-number distribution
            float k = i + .5f;
            // Project point on a cone
            float phi = trigTable.getArcCosine(1f - ((2f * k) / amount));
            float theta = (float) (Math.PI * k * sqrt5Plus1);
            double sinPhi = trigTable.getSine(phi);
            float x = (float) (trigTable.getCosine(theta) * sinPhi);
            float z = (float) (trigTable.getSine(theta) * sinPhi);
            float y = (x * x + z * z);
            // Scale, rotate, translate
            Vector3f pos = new Vector3f(x, y, z).mul(scale).rotate(quaternion).add(drawPos);
            drawParticle(particleEffect, step, pos);
        }
    }

    /**
     * Instructs the renderer to draw a cylinder at {@code drawPos} with {@code radius}, {@code height}, and
     * {@code rotation} applied using {@code amount} particles.  Particles will be spaced evenly around the cylinder.
     *
     * @param particleEffect The ParticleEffect to use
     * @param center The point at the center of the base of the cylinder
     * @param radius The radius of the cylinder
     * @param height The height of the cylinder
     * @param rotation Rotation applied to the cylinder
     * @param amount The number of particles to use to draw the cylinder
     */
    default void drawCylinder(
            ParticleEffect particleEffect, int step, Vector3f center, float radius, float height, Vector3f rotation,
            int amount
    ) {
        float stepHeight = height / amount;
        float stepAngle = (float) Math.TAU / 1.618033f;
        Quaternionfc quaternion = new Quaternionf().rotateZ(rotation.z).rotateY(rotation.y).rotateX(rotation.x);
        for (int i = 0; i < amount; i++) {
            float angle = i * stepAngle;
            float x = radius * trigTable.getCosine(angle);
            float y = stepHeight * i;
            float z = radius * trigTable.getSine(angle);
            Vector3f pos = new Vector3f(x, y, z).rotate(quaternion).add(center);
            drawParticle(particleEffect, step, pos);
        }
    }


    default void beforeFrame(int step, Vector3f frameOrigin) {
    }

    default void afterFrame(int step, Vector3f frameOrigin) {
    }

    sealed interface Instruction {
        void write(RegistryByteBuf buf);

        /**
         * Computes the points involved in a unit variant of the instructed shape composed of {@code amount} particles.
         *
         * @return an array of Vector3f instances representing the unit points
         */
        Vector3f[] computePoints();
    }

    record Frame(Vector3f origin) implements Instruction {

        static Frame from(RegistryByteBuf buf) {
            return new Frame(new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()));
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('F');
            buf.writeFloat(origin.x);
            buf.writeFloat(origin.y);
            buf.writeFloat(origin.z);
        }

        @Override
        public Vector3f[] computePoints() {
            throw new UnsupportedOperationException("Frames do not have points");
        }
    }

    record PType(ParticleEffect particleEffect) implements Instruction {

        static PType from(RegistryByteBuf buf) {
            return new PType(ParticleTypes.PACKET_CODEC.decode(buf));
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('T');
            ParticleTypes.PACKET_CODEC.encode(buf, this.particleEffect);
        }

        @Override
        public Vector3f[] computePoints() {
            throw new UnsupportedOperationException("PTypes do not have points");
        }
    }

    record Particle(Vector3f pos) implements Instruction {

        static Particle from(RegistryByteBuf buf) {
            return new Particle(new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()));
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('P');
            buf.writeFloat(pos.x);
            buf.writeFloat(pos.y);
            buf.writeFloat(pos.z);
        }

        @Override
        public Vector3f[] computePoints() {
            return new Vector3f[] { new Vector3f(pos) };
        }
    }

    record Line(Vector3f start, Vector3f end, int amount) implements Instruction {

        static Line from(RegistryByteBuf buf) {
            return new Line(new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                            new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                            buf.readShort());
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('L');
            buf.writeFloat(start.x);
            buf.writeFloat(start.y);
            buf.writeFloat(start.z);
            buf.writeFloat(end.x);
            buf.writeFloat(end.y);
            buf.writeFloat(end.z);
            buf.writeShort(amount);
        }

        @Override
        public Vector3f[] computePoints() {
            Vector3f[] points = new Vector3f[amount];
            int amountSubOne = (amount - 1);
            // Do not use 'sub', it modifies in-place
            float stepX = (end.x - start.x) / amountSubOne;
            float stepY = (end.y - start.y) / amountSubOne;
            float stepZ = (end.z - start.z) / amountSubOne;
            Vector3f curr = new Vector3f(start);
            for (int i = 0; i < amount; i++) {
                points[i] = new Vector3f(curr);
                curr.add(stepX, stepY, stepZ);
            }
            return points;
        }
    }

    record Ellipse(Vector3f center, float radius, float stretch, Vector3f rotation, int amount)
            implements Instruction {

        static Ellipse from(RegistryByteBuf buf) {
            return new Ellipse(new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                               buf.readFloat(),
                               buf.readFloat(),
                               new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                               buf.readShort());
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('E');
            buf.writeFloat(center.x);
            buf.writeFloat(center.y);
            buf.writeFloat(center.z);
            buf.writeFloat(radius);
            buf.writeFloat(stretch);
            buf.writeFloat(rotation.x);
            buf.writeFloat(rotation.y);
            buf.writeFloat(rotation.z);
            buf.writeShort(amount);
        }

        @Override
        public Vector3f[] computePoints() {
            Vector3f[] points = new Vector3f[amount];
            float angleInterval = (float) Math.TAU / (float) amount;
            for (int i = 0; i < amount; i++) {
                float currRot = angleInterval * i;
                float x = trigTable.getCosine(currRot);
                float y = trigTable.getSine(currRot);
                points[i] = new Vector3f(x, y, 0);
            }
            return points;
        }
    }

    record Ellipsoid(Vector3f drawPos, float xSemiAxis, float ySemiAxis, float zSemiAxis, Vector3f rotation,
                     int amount) implements Instruction {

        static Ellipsoid from(RegistryByteBuf buf) {
            return new Ellipsoid(
                    // drawPos (center)
                    new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                    // x semi-axis
                    buf.readFloat(),
                    // y semi-axis
                    buf.readFloat(),
                    // z semi-axis
                    buf.readFloat(),
                    // rotation
                    new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                    // amount
                    buf.readShort()
            );
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('S');
            buf.writeFloat(drawPos.x);
            buf.writeFloat(drawPos.y);
            buf.writeFloat(drawPos.z);
            buf.writeFloat(xSemiAxis);
            buf.writeFloat(ySemiAxis);
            buf.writeFloat(zSemiAxis);
            buf.writeFloat(rotation.x);
            buf.writeFloat(rotation.y);
            buf.writeFloat(rotation.z);
            buf.writeShort(amount);
        }

        @Override
        public Vector3f[] computePoints() {
            Vector3f[] points = new Vector3f[amount];
            final double sqrt5Plus1 = 3.23606;
            for (int i = 0; i < amount; i++) {
                // Offset into the real-number distribution
                float k = i + .5f;
                // Project point on a unit sphere
                float phi = trigTable.getArcCosine(1f - ((2f * k) / amount));
                float theta = (float) (Math.PI * k * sqrt5Plus1);
                float sinPhi = trigTable.getSine(phi);
                float x = trigTable.getCosine(theta) * sinPhi;
                float y = trigTable.getSine(theta) * sinPhi;
                float z = trigTable.getCosine(phi);
                points[i] = new Vector3f(x, y, z);
            }
            return points;
        }
    }

    record BezierCurve(Vector3f drawPos, net.mcbrincie.apel.lib.util.math.bezier.BezierCurve bezierCurve,
                       Vector3f rotation, int amount) implements Instruction {

        static BezierCurve from(RegistryByteBuf buf) {
            int controlPointCount = buf.readByte();
            Vector3f drawPos = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
            Vector3f start = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
            List<Vector3f> controlPoints = new ArrayList<>(controlPointCount);
            for (int i = 0; i < controlPointCount; i++) {
                controlPoints.add(new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()));
            }
            Vector3f end = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
            net.mcbrincie.apel.lib.util.math.bezier.BezierCurve bezierCurve = net.mcbrincie.apel.lib.util.math.bezier.BezierCurve.of(start, end, controlPoints);
            Vector3f rotation = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
            int amount = buf.readShort();

            return new BezierCurve(drawPos, bezierCurve, rotation, amount);
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('B');
            buf.writeByte(bezierCurve.getControlPoints().size());
            buf.writeFloat(drawPos.x);
            buf.writeFloat(drawPos.y);
            buf.writeFloat(drawPos.z);
            Vector3f start = bezierCurve.getStart();
            buf.writeFloat(start.x);
            buf.writeFloat(start.y);
            buf.writeFloat(start.z);
            for (Vector3f controlPoint : bezierCurve.getControlPoints()) {
                buf.writeFloat(controlPoint.x);
                buf.writeFloat(controlPoint.y);
                buf.writeFloat(controlPoint.z);
            }
            Vector3f end = bezierCurve.getEnd();
            buf.writeFloat(end.x);
            buf.writeFloat(end.y);
            buf.writeFloat(end.z);
            buf.writeFloat(rotation.x);
            buf.writeFloat(rotation.y);
            buf.writeFloat(rotation.z);
            buf.writeShort(amount);
        }

        @Override
        public Vector3f[] computePoints() {
            Vector3f[] points = new Vector3f[amount];
            float interval = 1.0f / amount;
            for (int i = 0; i < amount; i++) {
                points[i] = bezierCurve.compute(interval * i);
            }
            return points;
        }
    }

    record Cone(Vector3f drawPos, float height, float radius, Vector3f rotation, int amount) implements Instruction {

        static Cone from(RegistryByteBuf buf) {
            return new Cone(
                    // drawPos (at the tip)
                    new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                    // height
                    buf.readFloat(),
                    // radius
                    buf.readFloat(),
                    // rotation
                    new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                    // amount
                    buf.readShort()
            );
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('C');
            buf.writeFloat(drawPos.x);
            buf.writeFloat(drawPos.y);
            buf.writeFloat(drawPos.z);
            buf.writeFloat(height);
            buf.writeFloat(radius);
            buf.writeFloat(rotation.x);
            buf.writeFloat(rotation.y);
            buf.writeFloat(rotation.z);
            buf.writeShort(amount);
        }

        @Override
        public Vector3f[] computePoints() {
            Vector3f[] points = new Vector3f[amount];
            final double sqrt5Plus1 = 3.23606;
            for (int i = 0; i < this.amount; i++) {
                // Offset into the real-number distribution
                float k = i + .5f;
                // Project point on a unit cone
                float phi = trigTable.getArcCosine(1f - ((2f * k) / this.amount));
                float theta = (float) (Math.PI * k * sqrt5Plus1);
                double sinPhi = trigTable.getSine(phi);
                float x = (float) (trigTable.getCosine(theta) * sinPhi);
                float z = (float) (trigTable.getSine(theta) * sinPhi);
                float y = (x * x + z * z);
                points[i] = new Vector3f(x, y, z);
            }
            return points;
        }
    }

    record Cylinder(Vector3f center, float radius, float height, Vector3f rotation, int amount) implements Instruction {

        static final float ANGLE_INCREMENT = (float) (Math.TAU / 1.618033f);

        static Cylinder from(RegistryByteBuf buf) {
            return new Cylinder(
                    // center
                    new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                    // radius
                    buf.readFloat(),
                    // height
                    buf.readFloat(),
                    // rotation
                    new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                    // amount
                    buf.readShort()
            );
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('Y');
            buf.writeFloat(center.x);
            buf.writeFloat(center.y);
            buf.writeFloat(center.z);
            buf.writeFloat(radius);
            buf.writeFloat(height);
            buf.writeFloat(rotation.x);
            buf.writeFloat(rotation.y);
            buf.writeFloat(rotation.z);
            buf.writeShort(amount);
        }

        @Override
        public Vector3f[] computePoints() {
            Vector3f[] points = new Vector3f[amount];
            // Unit height/radius
            for (int i = 0; i < amount; i++) {
                float angle = i * ANGLE_INCREMENT;
                float x = trigTable.getCosine(angle);
                float y = (float) i / amount;
                float z = trigTable.getSine(angle);
                points[i] = new Vector3f(x, y, z);
            }
            return points;
        }
    }
}
