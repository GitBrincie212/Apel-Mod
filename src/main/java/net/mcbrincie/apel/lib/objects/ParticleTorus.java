package net.mcbrincie.apel.lib.objects;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

/** The particle object class that represents a 3D ring(or a torus / donut).
 * It has an inner radius & an outer radius. The inner radius controls the center
 * gap while the outer radius controls the outer ring's size.
*/
@SuppressWarnings("unused")
public class ParticleTorus extends ParticleObject {
    protected float torusRadius;
    protected float innerRadius;

    public ParticleTorus(ParticleEffect particle, float innerRadius, float torusRadius, int amount, Vector3f rotation) {
        super(particle, rotation);
        this.setAmount(amount);
    }

    public ParticleTorus(ParticleEffect particle, float innerRadius, float torusRadius, int amount) {
        super(particle);
    }

    public ParticleTorus(ParticleObject object) {
        super(object);
    }

    public float setTorusRadius(float torusRadius) {
        if (torusRadius <= 0) {
            throw new IllegalArgumentException("Torus radius must be greater than 0");
        }
        float previousRadius = this.torusRadius;
        this.torusRadius = torusRadius;
        return previousRadius;
    }

    public float setInnerRadius(float innerRadius) {
        if (innerRadius <= 0) {
            throw new IllegalArgumentException("Inner radius must be greater than 0");
        }
        float previousRadius = this.innerRadius;
        this.innerRadius = innerRadius;
        return previousRadius;
    }

    @Override
    public void draw(ServerWorld world, int step, Vector3f pos) {

    }
}
