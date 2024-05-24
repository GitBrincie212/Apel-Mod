package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CombinativeAnimator<T extends PathAnimatorBase> extends PathAnimatorBase {
    public T[] objects;
    private List<List<?>> argumentsBinded;

    @SafeVarargs
    public CombinativeAnimator(int delay, float renderingInterval, @NotNull T... animators) {
        super(delay, new ParticleObject(ParticleTypes.EFFECT), renderingInterval); // Kinda clunky but idc
        if (animators.length == 1) {
            throw new IllegalArgumentException("Animators must not have below 2 animators");
        }
        this.objects = animators;
    }

    @SafeVarargs
    public CombinativeAnimator(int delay, int renderingSteps, @NotNull T... animators) {
        super(delay, new ParticleObject(ParticleTypes.EFFECT), renderingSteps); // Kinda clunky but idc
        if (animators.length == 1) {
            throw new IllegalArgumentException("Animators must not have below 2 animators");
        }
        this.objects = animators;
    }

    /**
     * Constructor for the combinative animator. This constructor is
     * meant to be used in the case that you want to fully copy a new
     * combinative animator instance with all of its parameters regardless
     * of their visibility(this means protected & private params are copied)
     *
     * @param animator The animator to copy from
     */
    public CombinativeAnimator(CombinativeAnimator<T> animator) {
        super(animator.delay, new ParticleObject(ParticleTypes.EFFECT), 0); // Kinda clunky but idc
        this.objects = animator.objects.clone();
        this.processSpeed = animator.processSpeed;
    }

    public CombinativeAnimator<T> attachArguments(List<?> args) {
        if (this.argumentsBinded.size() == this.objects.length) {
            throw new IndexOutOfBoundsException("Cannot add more arguments than there are path animators");
        }
        this.argumentsBinded.add(args);
        return this;
    }

    @Override
    public int convertToSteps() {
        int steps = 0;
        for (T object : objects) {
            steps = Math.max(object.renderingSteps, steps);
            if (object.renderingSteps == 0) {
                steps = Math.max(object.convertToSteps(), steps);
            }
        }
        return steps;
    }

    @Override
    public void beginAnimation(ServerWorld world, int startStep, int endStep) throws SeqDuplicateException, SeqMissingException {
        if (this.argumentsBinded.size() != this.objects.length) {
            throw new IndexOutOfBoundsException("Some objects or arguments are out of bounds");
        }
        int index = 0;
        for (T animator : this.objects) {
            List<?> argsToBind = this.argumentsBinded.get(index);
            index++;
        }
    }
}
