package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.util.interceptor.DrawContext;

import java.util.List;

/** The TreePathAnimator is an interface for path animators that define a tree-like structure,
 * this tree-like structure is composed of multiple path animator(s) as children(s).
 * Parallel and Sequential path animator uses this interface if you plan to make something
 * similar, you should also use it
 */
@SuppressWarnings("unused")
public interface TreePathAnimator {
    void addAnimatorPath(PathAnimatorBase<? extends PathAnimatorBase<?>> animator);
    void removeAnimatorPath(PathAnimatorBase<? extends PathAnimatorBase<?>> animator);
    List<PathAnimatorBase<? extends PathAnimatorBase<?>>> getPathAnimators();
    PathAnimatorBase<? extends PathAnimatorBase<?>> getPathAnimator(int index);

    static DrawContext.Key<PathAnimatorBase<? extends PathAnimatorBase<?>>> animatorKey(String name) {
        return new DrawContext.Key<>(name) { };
    }

    // Shared keys
    DrawContext.Key<PathAnimatorBase<? extends PathAnimatorBase<?>>> PATH_ANIMATOR = animatorKey("pathAnimator");
    DrawContext.Key<Integer> DELAY = DrawContext.integerKey("delay");

}
