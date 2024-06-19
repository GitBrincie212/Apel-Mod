package net.mcbrincie.apel.lib.animators;

import java.util.List;

/** The TreePathAnimator is an interface for path animators that define a tree-like structure,
 * this tree-like structure is composed of multiple path animator(s) as children(s).
 * Parallel and Sequential path animator uses this interface if you plan to make something
 * similar, you should also use it
 *
 * @param <T> The path animator to host
 */
@SuppressWarnings("unused")
public interface TreePathAnimator<T extends PathAnimatorBase> {
    void addAnimatorPath(PathAnimatorBase animator);
    void removeAnimatorPath(PathAnimatorBase animator);
    List<T> getPathAnimators();
    T getPathAnimator(int index);
}
