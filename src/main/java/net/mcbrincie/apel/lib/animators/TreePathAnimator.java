package net.mcbrincie.apel.lib.animators;

import java.util.List;

public interface TreePathAnimator<T extends PathAnimatorBase> {
    void addAnimatorPath(PathAnimatorBase animator);
    void removeAnimatorPath(PathAnimatorBase animator);
    List<T> getPathAnimators();
    T getPathAnimator(int index);
}
