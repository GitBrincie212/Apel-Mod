# Introduction
There are a bunch of path animators to choose from in APEL. Each does a different functionality than the other. 
You can also combine them in all sorts of ways. It is best to read [Getting Started](Getting%20Started.md) for more
detail on the theory of path animators and how they work. In this document, we will explore all the path animators that
APEL provides and examine what they achieve as well as take a look at some code snippets with visual examples to see how
to work with them.

## Brief Summary Of Path Animators
A path animator defines the path a particle object follows. You can make the path more detailed by adding more rendering
steps or if you are using a particle interval, shrink its size. Path animators host path-animator interceptors which are
able to modify the path, and they can also modify broader properties of a ParticleObject without knowing it.

## Visual Path Animators
These path-animators define the path and where the particle object should be drawn at, hence that is why they are called
visual. Examples of those can be [Point Path Animator](../src/main/java/net/mcbrincie/apel/lib/animators/PointAnimator.java),
[Linear Path Animator](../src/main/java/net/mcbrincie/apel/lib/animators/LinearAnimator.java)... etc. Unlike other
path animators (like tree path animators which we will touch on a bit soon), they do not handle other path-animators

## Utility Path Animators
Utility path-animators specialize in providing specific QoL stuff to the user, this can be nesting path animators in a
tree form (these path animators are referred to tree path animators). Having a hierarchy of path-animators is more useful 
than just being complex. Currently, there are two path-animators that are the type of tree-path animators, those being 
[Parallel Animator](../src/main/java/net/mcbrincie/apel/lib/animators/ParallelAnimator.java) and [Sequential Path Animator](../src/main/java/net/mcbrincie/apel/lib/animators/SequentialAnimator.java). These path animators orchestrate the timings of other path-animators. 
Parallel Path Animators trigger all path-animators to run in parallel with each other while sequential waits for the other
path-animators to finish before executing the next path-animator and repeating the process

_TL;DR: Utility path animators specialise in providing QoL features, a sub category of them are tree-path animators
which define a tree-like structure for path animators which allows for nesting_

## Overview Of All Visual Path Animators
We will start from the simpler ones and work our way to the more niche and complex path animators. Suppose our particle
object is a cuboid attached to the various path-animators that apel provides to us. Also assume that the path-animator
begins right away with server-side rendering. This is the code for the particle object we have

```java
ParticleCuboid particleCuboid = ParticleCuboid.builder()
    .amount(100)
    .size(1f)
    .particleEffect(ParticleTypes.END_ROD)
    .build();
```
**Disclaimer:** There may be lag during those videos. This lag is caused by the video itself, we apologise for this inconvinience that may be caused
<br><br>

[Point Path Animator](../src/main/java/net/mcbrincie/apel/lib/animators/PointAnimator.java) the most beginner-friendly
path animator out there, they define a point that the particle object is anchored to. They do not provide any motion to
the particle object
```java
PointAnimator pointAnimator = PointAnimator.builder()
    .point(new Vector3f())
    .particleObject(particleCuboid)
    .delay(1)
    .renderingSteps(100)
    .build();
```
[![Video Title](https://img.youtube.com/vi/WF3Dy24YNeQ/0.jpg)](https://www.youtube.com/watch?v=WF3Dy24YNeQ)


[Linear Path Animator](../src/main/java/net/mcbrincie/apel/lib/animators/LinearAnimator.java) another beginner-friendly 
path animator, a tiny bit more complex than the previous path animator, but linear path animators define one or more lines
for the particle object to traverse<br><br>
```java
LinearAnimator linearAnimator = LinearAnimator.builder()
    .endpoint(new Vector3f())
    .endpoint(new Vector3f(0, 10, 10))
    .endpoint(new Vector3f(0, 0, 10))
    .endpoint(new Vector3f(0, -10, -10))
    .particleObject(particleCuboid)
    .delay(1)
    .stepsForAllSegments(100)
    .build();
```
[![Video Title](https://img.youtube.com/vi/pI0jWMVeW3k/0.jpg)](https://www.youtube.com/watch?v=pI0jWMVeW3k)


[Circular Path Animator](../src/main/java/net/mcbrincie/apel/lib/animators/CircularAnimator.java) a more advanced path
animator that allows to define a circle for the particle object to revolve around ``revolution`` times
```java
CircularAnimator circularAnimator = CircularAnimator.builder()
    .radius(2f)
    .center(new Vector3f())
    .revolutions(3)
    .particleObject(particleCuboid)
    .delay(1)
    .renderingSteps(100)
    .build();
```
[![Video Title](https://img.youtube.com/vi/Cb-sOY14CvU/0.jpg)](https://www.youtube.com/watch?v=Cb-sOY14CvU)


[Ellipse Path Animator](../src/main/java/net/mcbrincie/apel/lib/animators/EllipseAnimator.java) yet another more advanced 
path animator that allows to define an ellipse for the particle object to revolve around ``revolution`` times
```java
EllipseAnimator ellipseAnimator = EllipseAnimator.builder()
    .radius(2f)
    .stretch(3f)
    .center(new Vector3f())
    .revolutions(3)
    .particleObject(particleCuboid)
    .delay(1)
    .renderingSteps(100)
    .build();
```
[![Video Title](https://img.youtube.com/vi/RgR8Nx47iqQ/0.jpg)](https://www.youtube.com/watch?v=RgR8Nx47iqQ)


[Bezier Path Animator](../src/main/java/net/mcbrincie/apel/lib/animators/BezierCurveAnimator.java) by far a tricky path
 animator, especially when using the raw interface of the library, it defines a Bézier curved path
```java
BezierCurveAnimator bezierCurveAnimator = BezierCurveAnimator.builder()
    .bezierCurve(new CubicBezierCurve(
        new Vector3f(),
        new Vector3f(0, 10, 0),
        new Vector3f(20, 3, 14),
        new Vector3f(0, 2, -12)
    ))
    .delay(1)
    .particleObject(particleCuboid)
    .stepsForAllCurves(200)
    .build();
```
[![Video Title](https://img.youtube.com/vi/ZRQDuM9RuKg/0.jpg)](https://www.youtube.com/watch?v=ZRQDuM9RuKg)

## Overview Of All Tree-Path Animators
Again, we will start from the simpler path animators (as of the time of writing, this doesn't really matter as there
are only 2 utility path animators to view) and move our way to the more complex and niche path animators. Suppose we have 
the same particle cuboid object and two more particle cuboids being the same size but with different particle effects which 
they are attached to different path animators

[Parallel Path Animator](../src/main/java/net/mcbrincie/apel/lib/animators/ParallelAnimator.java) this path animator
runs child path animators in parallel. You can give it delay offsets and control via interceptors if the path animator
should render or not, and even switch the path animator during runtime
```java
ParallelAnimator parallelAnimator = ParallelAnimator.builder()
    .animator(pointAnimator)
    .animator(pointAnimator2)
    .animator(pointAnimator3)
    .delay(1)
    .build();
```
In this example, we shouldn't care how the other path animators behave but more so
the timings on when the path animators first appear

[![Video Title](https://img.youtube.com/vi/ZEpDaA-iEDI/0.jpg)](https://www.youtube.com/watch?v=ZEpDaA-iEDI)


[Sequential Path Animator](../src/main/java/net/mcbrincie/apel/lib/animators/SequentialAnimator.java) this path animator
runs child path animators in a sequence. It waits for the first path animator to finish before playing the next one, you 
can give it delay offsets and control via interceptors if the path animator should render or not. And even switch the path 
animator during runtime
```java
SequentialAnimator sequentialAnimator = SequentialAnimator.builder()
    .animator(pointAnimator)
    .animator(pointAnimator2)
    .animator(pointAnimator3)
    .delay(1)
    .build();
```
In this example, we shouldn't care how the other path animators behave but more so
the order of timings on when each of the path animators plays

[![Video Title](https://img.youtube.com/vi/DXIaQGQYl7E/0.jpg)](https://www.youtube.com/watch?v=DXIaQGQYl7E)
