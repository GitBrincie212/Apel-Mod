package net.mcbrincie.apel.lib.util;

import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.objects.ParticlePoint;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

public class CommonUtils {
    public void drawLine(ParticleObject object, ServerWorld world, Vector3f start, Vector3f end, int amount) {
        float dist = start.distance(end);
        float dirX = (end.x - start.x) / dist;
        float dirY = (end.y - start.y) / dist;
        float dirZ = (end.z - start.z) / dist;
        float interval = dist / amount;
        Vector3f curr = new Vector3f(start);
        for (int i = 0; i < amount; i++) {
            object.drawParticle(world, curr);
            curr = curr.add((dirX * interval), (dirY * interval), (dirZ * interval));
        }
    }
}
