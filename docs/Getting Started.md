# Getting Started
If you read [README.md](../README.md) you know, Apel consists of two primary systems which compliment each other.
These systems allow for the flexible behavior you see when writing code for complex particle
scenes. The systems are **Particle Objects** and **Path Animators**

# What Are Particle Objects?
A Particle Object is a desired shape that is rendered as particles and projected into the minecraft world. A particle
object has parameters that control how it is rendered in the world. A particle object **does not have a predefined center
position**, but rather the center position is given out by a different system which are called [path animators](#what-are-path-animators),
the position that is given is called the **rendering position**. A Particle Object has by default as parameters:

- **Amount:** The number of particles to use to draw the final result
- **Rotation** is the tilt in the XYZ plane, each coordinate is measured in RADIANS
- **Particle Effect:** the particle to use (this can, for example, be an "end rod particle")

The rendering is handled via the ``draw`` method which is an internal method used by the system
(so it should not be called from
elsewhere).
When path animators calls the ``draw`` method, we commonly refer to it as a **draw call**, they handle
the logic of where to spawn the particles as well as interceptors (a subsystem that is used on both path animators
and particle objects)

_TLDR; A particle object is the thing you see in the world. It has certain params which modify its looks. They also host
a method which is where the rendering happens(when a path animator calls it, we refer to it as a draw call), however,
this method is internal and should not be used outside_

## What Are Interceptors?
Interceptors come from the **Interceptor API**, interceptors are basically methods(or lambda functions) that execute in
certain places of code (these places are defined by the developer of where they are) they come with two arguments
- **Draw Context** This is where local variables and other forms of data reside in. This is basically a hashmap
  although, it also has common immutable variables stored in it which are <u>the current step, the server world and
  the position to draw the particle object in</u>(drawing position ≠ rendering position).
  <br><br>
- **Object Instance** This is where you get the object instance that you are trying to modify. You can change its
  params via setters & getters and even change its interceptors(from that interceptor). Interceptors modify the object
  _in place_, which means that you don't have to do anything else other than set some parameters of the object for the
  modification to take place

_TLDR; An interceptor is a function that modifies some metadata (given from the object) and the object instance
itself, object instance decides on whenever it wants to accept or not the modified metadata and the modified instance, 
it can also decide if it wants to put the modified instance in other interceptors that come right
after_

# What Are Path Animators
A path animator is used for defining the trajectory / path of where a particle object will go, and they inherit
from the ``ParticleAnimatorBase`` abstract class.
Unlike particle objects, they are not visually seen, 
but rather, they render the particle object in predefined positions that represent a path.
These positions are called as **rendering steps**, 
which host **rendering actions** and are similar to frames in normal animation.
Rendering
actions are the ones responsible for calling the ``draw`` method from the particle object.
Rendering steps
usually process 1 after the other, but you can change the processing speed (we will touch on that later) to make it so
the rendering steps hold more than one rendering action.
Path animators have a common set of params which are:
- **Delay:** The time between the rendering steps (which is counted in server ticks)
- **Process Speed:** The amount of draw calls in each rendering action
- **Particle Object:** The object to use when rendering actions do a draw call

_Note_: For getting the total duration of the animation: ``renderSteps • (delay / processSpeed)``. The formula on some
might differ a bit so be on the lookout for that

However, they also have **Rendering Steps Amount** and **The Rendering Interval**. They are different parameters
and one is used than the other in certain situations. When setting the rendering steps amount to any value, you tell the
path animator that this needs to be constant & the other value must be dynamic and same logic applies to the rendering
interval. When playing any animation, a path animator requires a renderer for it to function (which handles how things look)

_TLDR; Path animators define an invisible path the object can take. The path is composed of positions and at each position
a rendering step processes its rendering actions (which call the ``draw`` method). Path animators host the particle object
itself as well as the delay, the process speed, the amount of rendering steps & the rendering interval_

## Rendering Steps & Rendering Interval
Both parameters are calculated depending on which is given out to the path animator and which is not. Depending on how
the path animators define their own path, the calculations might differ. Rendering steps (even if they are not explicitly
given as a constant) are used to tell the scheduler to allocate a new **sequence chunk** for that path animator with
this amount of rendering steps to use. A rendering interval measures the number of blocks for
one rendering step (which means the rendering steps are dynamically adjusted)

> The Rendering Steps lead to better performance for larger
distances (due to being a fixed amount and not dynamically changing)
> but has the drawback of being inconsistent at large
distances due to the gaps it creates

> The Rendering Interval leads to more consistent looking animations
across large and small distances with the expense of performance (since large distances
can produce many rendering steps)

For this reason, it is best to place as constant the amount of rendering steps when dealing with an animation that
has a known distance and for the rendering interval the opposite holds true.

_TLDR; Use rendering steps when you care more for performance than visual consistency across distance, use rendering
intervals when you care more about visual consistency than performance_

# What Are Renderers
Renderers define how to draw specific things in the world, renderers are often called a **low-level system** because
the only thing they care to do is to draw the specific thing onto the world. The renderer doesn't know the particle
object that called it, but it knows the task it has to specifically do. In most cases, the default server-side or 
client-side renderer should be used in most tasks. However, feel free to make your own

Renderers come with methods to draw specifically the primitive shapes (the methods have the appropriate arguments given
to them by the particle object) that apel has to offer. This includes spheres, cuboids, triangles... etc. 
It is best to make your own custom renderer as opposed to copying the source code for making particle objects 
behave in a bit of a different manner

_TLDR; Renderers only care about the task that it's given to them, they don't know the particle object itself and
a custom one should be made in case you want the rendering to be different but not having to create different
particle objects that are almost the same as the primitive ones of apel to achieve that_ 

# Code Snippets
All of this is the basic theory of how these systems work. Let us now examine how we start using those systems in the
practical world. There will be three examples, and we will analyze the code and what it does, we will of course use the
simpler components the library has. Let's assume the code is running once to start the animation and that we have a
world variable that represents the ``ServerWorld``
## Example 1
```java
// Defining The Particle Object (in our case a point)
ParticlePoint particlePoint = ParticlePoint.builder()
        .particleEffect(ParticleTypes.END_ROD)
        .build();

// Defining The Path Animator To Use (in our case, a point path animator)
PointAnimator pointAnimator = PointAnimator.builder()
        .delay(1)
        .point(new Vector3f())
        .particleObject(particlePoint)
        .renderingSteps(100)
        .build();

// Creating the renderer(in our case server-side rendering)
ApelRenderer renderer = ApelRenderer.create(world);

// Beginning The Animation
pointAnimator.beginAnimation(renderer);
```

This in minecraft translates into visually:<br>


https://github.com/GitBrincie212/Apel-Mod/assets/92397968/8e4f3a9c-547c-4ff8-8ade-63abd7fb7f86

In this code snippet we can clearly see that we define a particle object, and we define a path animator which contains
the `point` particle object. We then create a renderer in which we will render the animation server-side in our case 
(we can use client rendering which produces the same thing but does the rendering on the client). Then we begin animation 
and supply the renderer to the animation which begins the animation and displays a point.

---
## Example 2
```java
// Defining Our Particle Line
ParticleLine particleLine = ParticleLine.builder()
    .particleEffect(ParticleTypes.END_ROD)
    .start(new Vector3f(0, -5, 0))
    .end(new Vector3f(0, 5, 0))
    .amount(50)
    .build();

// Trimming the animation (starting at a specific step then stopping at an ending step)
AnimationTrimming<Integer> trimming = new AnimationTrimming<>(
    5,    // The starting rendering step
    15    // The ending rendering step
);

LinearAnimator linearAnimator = LinearAnimator.builder()
    .delay(2)
    .endpoint(new Vector3f(0, 0, 0))
    .endpoint(new Vector3f(10, 0, 0))
    .particleObject(particleLine)
    .trimming(trimming)
    .renderingSteps(100)
    .build();

ApelRenderer renderer = ApelRenderer.client(world);
linearAnimator.beginAnimation(renderer);
```
This in minecraft translates into visually:<br>


https://github.com/GitBrincie212/Apel-Mod/assets/92397968/eb473bde-93ea-47ad-af65-f45b57c60153


In the second example,
we define a particle line which has a starting and an ending position which are represented as
offsets to the draw position (for example, on the draw position of [5, 0, 0] the starting position
becomes [5, -5, 0]), it also has an "amount" dictating number of particles to use for the line (in our case, its 50).

We create a new trim object which specifies what parts to trim the animation from - to.
In our case, we have trimmed the five starting rendering steps and end at the fifteenth rendering step

We then define a linear path animator with a delay of 2, a starting position of [0, 0, 0] and an ending position of
[10, 0, 0] (note that these are not expressed as offsets but rather real world coordinates), then we supply 
the particle line; and lastly, we define the trimming as well as the number of rendering steps for the animation.

Unlike our previous example, we now use client-side rendering for the animation, and finally we play it
