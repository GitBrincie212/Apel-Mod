package net.mcbrincie.apel.lib.renderers;

import net.mcbrincie.apel.Apel;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record ApelFramePayload(List<ApelRenderer.Instruction> instructions) implements CustomPayload {
    public static final CustomPayload.Id<ApelFramePayload> ID = new CustomPayload.Id<>(Identifier.of(Apel.MOD_ID,
                                                                                                     "frame"));
    public static final PacketCodec<RegistryByteBuf, ApelFramePayload> PACKET_CODEC = PacketCodec.of(ApelFramePayload::write,
                                                                                                     ApelFramePayload::new);

    private ApelFramePayload(RegistryByteBuf buf) {
        this(readInstructions(buf));
    }

    private static @NotNull List<ApelRenderer.Instruction> readInstructions(RegistryByteBuf buf) {
        // TODO: Consider a value to describe the number of instructions
        List<ApelRenderer.Instruction> instructions = new ArrayList<>();
        while (buf.readableBytes() > 0) {
            instructions.add(ApelRenderer.Instruction.from(buf));
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
