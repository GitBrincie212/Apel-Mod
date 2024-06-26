package net.mcbrincie.apel.lib.renderers;

import net.mcbrincie.apel.Apel;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record ApelFramePayload(List<ApelNetworkRenderer.Instruction> instructions) implements CustomPayload {
    public static final CustomPayload.Id<ApelFramePayload> ID = new CustomPayload.Id<>(Identifier.of(Apel.mod_id,
                                                                                                     "frame"));
    public static final PacketCodec<RegistryByteBuf, ApelFramePayload> PACKET_CODEC = PacketCodec.of(ApelFramePayload::write,
                                                                                                     ApelFramePayload::new);

    private ApelFramePayload(RegistryByteBuf buf) {
        this(readInstructions(buf));
    }

    private static @NotNull List<ApelNetworkRenderer.Instruction> readInstructions(RegistryByteBuf buf) {
        // TODO: Consider a value to describe the number of instructions
        List<ApelNetworkRenderer.Instruction> instructions = new ArrayList<>();
        while (buf.readableBytes() > 0) {
            switch (buf.readByte()) {
                case 'F' -> instructions.add(ApelRenderer.Frame.from(buf));
                case 'T' -> instructions.add(ApelRenderer.PType.from(buf));
                case 'L' -> instructions.add(ApelRenderer.Line.from(buf));
                case 'P' -> instructions.add(ApelRenderer.Particle.from(buf));
                case 'E' -> instructions.add(ApelRenderer.Ellipse.from(buf));
                case 'S' -> instructions.add(ApelRenderer.Ellipsoid.from(buf));
                case 'B' -> instructions.add(ApelRenderer.BezierCurve.from(buf));
                case 'C' -> instructions.add(ApelRenderer.Cone.from(buf));
            }
        }
        return instructions;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    void write(RegistryByteBuf buf) {
        this.instructions.forEach(ins -> ins.write(buf));
    }
}
