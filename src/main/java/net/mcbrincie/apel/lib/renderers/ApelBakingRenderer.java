package net.mcbrincie.apel.lib.renderers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

public class ApelBakingRenderer implements ApelServerRenderer {
    private static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    private static final int WRITE_CHUNK_SIZE = 4096;

    private final ServerWorld world;
    private final String animationName;
    private final DataOutputStream dataOutputStream;
    private List<Instruction> instructions;

    // Put a Clock in so testing is flexible
    private final Clock clock;

    private ParticleEffect prevParticleEffect;

    /**
     * Constructs an {@link ApelServerRenderer} that emits instructions to an output location.
     *
     * @param world The server world instance
     */
    ApelBakingRenderer(ServerWorld world, String animationName) {
        this.world = world;
        this.animationName = animationName;
        this.clock = Clock.systemUTC();
        this.dataOutputStream = createDataOutputStream();

        this.instructions = new ArrayList<>();
    }

    private DataOutputStream createDataOutputStream() {
        try {
            String suffix = ZonedDateTime.now(this.clock).format(INSTANT_FORMATTER);
            Path path = Path.of(String.format("%s-%s", this.animationName, suffix));
            return new DataOutputStream(Files.newOutputStream(path, CREATE_NEW));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void drawParticle(ParticleEffect particleEffect, int step, Vector3f drawPos) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Particle(drawPos));
    }

    @Override
    public void drawLine(
            ParticleEffect particleEffect, int step, Vector3f drawPos, Vector3f start, Vector3f end,
            Vector3f rotation, int amount
    ) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Line(drawPos, start, end, rotation, amount));
    }

    @Override
    public void drawEllipsoid(
            ParticleEffect particleEffect, int step, Vector3f drawPos, float xSemiAxis, float ySemiAxis,
            float zSemiAxis, Vector3f rotation, int amount
    ) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Ellipsoid(drawPos, xSemiAxis, ySemiAxis, zSemiAxis, rotation, amount));
    }

    @Override
    public void drawEllipse(
            ParticleEffect particleEffect, int step, Vector3f center, float radius, float stretch, Vector3f rotation,
            int amount
    ) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Ellipse(center, radius, stretch, rotation, amount));
    }

    @Override
    public void drawBezier(
            ParticleEffect particleEffect, int step, Vector3f drawPos,
            net.mcbrincie.apel.lib.util.math.bezier.BezierCurve bezierCurve, Vector3f rotation, int amount
    ) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new BezierCurve(drawPos, bezierCurve, rotation, amount));
    }

    @Override
    public void drawCone(
            ParticleEffect particleEffect, int step, Vector3f drawPos, float height, float radius, Vector3f rotation,
            int amount
    ) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Cone(drawPos, height, radius, rotation, amount));
    }

    @Override
    public void drawCylinder(
            ParticleEffect particleEffect, int step, Vector3f center, float radius, float height, Vector3f rotation,
            int amount
    ) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Cylinder(center, radius, height, rotation, amount));
    }

    @Override
    public void beforeFrame(int step, Vector3f frameOrigin) {
        this.instructions.add(new Frame(frameOrigin));
    }

    @Override
    public void afterFrame(int step, Vector3f frameOrigin) {
        // ParticleTypes.PACKET_CODEC needs a RegistryByteBuf, can't be cast from ByteBuf
        RegistryByteBuf buffer = new RegistryByteBuf(Unpooled.buffer(4096), world.getRegistryManager());
        for (Instruction ins : this.instructions) {
            ins.write(buffer);
            // Flush periodically
            if (buffer.readableBytes() > WRITE_CHUNK_SIZE) {
                writeBytes(buffer);
            }
        }
        // Flush any remaining bytes
        writeBytes(buffer);
        try {
            this.dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Recreate, with initial capacity
        this.instructions = new ArrayList<>(this.instructions.size());
        // Clear the particle type, so the next frame will send it, too
        this.prevParticleEffect = null;
    }

    private void writeBytes(ByteBuf buffer) {
        try {
            buffer.readBytes(this.dataOutputStream, buffer.readableBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void finish() {
        try {
            this.dataOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ServerWorld getServerWorld() {
        return this.world;
    }

    private void detectParticleTypeChange(ParticleEffect particleEffect) {
        if (particleEffect != this.prevParticleEffect) {
            this.instructions.add(new PType(particleEffect));
            this.prevParticleEffect = particleEffect;
        }
    }
}
