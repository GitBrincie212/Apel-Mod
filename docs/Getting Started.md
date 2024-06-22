# Getting Started
As from the `README.md` you know, Apel consists of two systems which compliment each other.
These systems allow for the flexible behaviour you see when writing code for complex particle 
scenes. The systems are **Particle Objects** and **Path Animators**

# What Are Particle Objects?
A Particle Object is a desired shape that is rendered as particles and projected into the minecraft world. A particle 
object has parameters that control how it is rendered in the world. A particle object**does not have a predefined center 
position**, the center position is given out by a different system which are called [path animators](#what-are-path-animators), 
the position that is given is called the **rendering position**. A Particle Object has by default as parameters:

- **Amount:** The number of particles to use to draw the final result
- **Rotation** is the tilt in the XYZ plane, each coordinate is measured in RADIANS
- **Particle Effect:** the particle to use (this can, for example, be the "end rod particle", "bubble particle"...)

The rendering is handled via the ``draw`` method which is an internal method used by the system
(so it should not be called from 
elsewhere).
When path animators calls the ``draw`` method, we commonly refer to it as a **draw call**, they handle 
the logic of where to spawn the particles as well as interceptors (a subsystem that is used in particle objects)

_TLDR; A particle object is the thing you see in the world. It has certain params which modify its looks. They also host
a method which is where the rendering happens(when a path animator calls it, we refer to it as a draw call), however,
this method is internal and should not be used outside_

## What Are Interceptors?
Interceptors come from the **Interceptor API**, interceptors are basically methods(or lambda functions) that execute in
certain places of code (these places are defined by the developer of where they are) they come with two arguments
- **Intercept Metadata** This is where local variables and other forms of data reside in. This is basically a hashmap
although, it also has common immutable variables stored in it which are <u>the current step, the server world and
the position to draw the particle object in</u>(drawing position ≠ rendering position). Some metadata might not be used
which is referred to as <u>metadata rejection</u> and for metadata that is used its referred as <u>metadata acceptance</u>
<br><br>
- **Object Instance** This is where you get the object instance that you are trying to modify. You can change its
params via setters & getters and even change its interceptors(from that interceptor). On most cases the object accepts
its modified counterpart(however sometimes it doesn't happen if it is made from another developer), in the case where
the modified instance is accepted it is referred to as <u>instance acceptance</u> and for the case when it isn't we call
it as <u>instance rejection</u>. Modified instances also stack on other interceptors sometimes this guideline may not
be followed from inexperienced developers (or for some special case scenarios), it is commonly called as <u>instance stacking</u>

When interceptors complete the modifications, they return a `InterceptResult` which holds the modified object and the
modified metadata, after which the code can dictate when should it use them, when not to use them or when to make some
adjustments

_TLDR; An interceptor is a function that modifies some metadata (given from the particle object) and the particle object 
itself. The particle object instance decides on whenever it wants to accept or not the modified metadata and the 
modified instance, it can also decide if it wants to put the modified instance in other interceptors that come right
after_

# What Are Path Animators
A path animator is used for defining the trajectory / path of where a particle object will go, and they inherit
from the ``ParticleAnimatorBase`` abstract class.
Unlike particle objects, they are not visually seen, but rather,  
they render the particle object in predefined positions that represent a path.
These positions are called as 
**rendering steps**, which host **rendering actions** and are similar to frames in normal animation.
Rendering 
actions are the ones responsible for calling the ``draw`` method from the particle object.
Rendering steps
usually process 1 after the other, but you can change the processing speed (we will touch on that later) to make it so
the rendering steps hold more than one rendering action.
Path animators have a common set of params which are:
- **Delay:** The time between the rendering steps (which is counted in server ticks)
- **Process Speed:** The amount of draw calls in each rendering action
- **Particle Object:** The object to use when rendering actions do a draw call

However, they also have **Rendering Steps Amount** and **The Rendering Interval**. They are different parameters
and one is used than the other in certain situations. When setting the rendering steps amount to any value, you tell the
path animator that this needs to be constant & the other value must be dynamic and same logic applies to the rendering 
interval. When playing any animation, a path animator requires a renderer for it to function (which handles how things look)

_TLDR; Path animators define an invisible path the object can take. The path is composed of positions and at each position
a rendering step processes its rendering actions (which call the ``draw`` method). Path animators host the particle object
itself as well as the delay, the process speed, the amount of rendering steps & the rendering interval

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
Renderers define how things should render and where to render from (server-side or client-side rendering).
Most of the time its best to use either the `DefaultApelRenderer` or `ApelNetworkRenderer` for rendering but
there are niche cases where you don't want to create too many particle objects that do the one rendering task
you want, an example of this is the `RecursiveApelRenderer`.
It renders recursive patterns of shapes without the need to create a bunch of particle objects that are modified
existing ones (it also means that there is better compatability with addon mods for apel).

Renderers also come bundled up with methods to modify the primitive shapes that apel offers
(like an ellipse, a sphere... etc.), this is why it is best to consider making a custom renderer
for specific characteristics that you want on all particle objects

_TLDR;_ Renderers define how particles are projected / drawn into the world. 
It is best to make a custom renderer instead of repetitively adding particle objects that 
have that same characteristic

# Code Snippets
All of this is the basic theory of how these systems work. Let us now examine how we start using those systems in the
practical world. There will be three examples, and we will analyze the code and what it does, we will of course use the
simpler components the library has. Let's assume the code is running once to start the animation and that we have a
world variable that represents the ``ServerWorld``
## Example 1
```java
// Defining The Particle Object(in our case a point)
ParticlePoint point = new ParticlePoint(
        ParticleTypes.END_ROD // The particle to use
);

// Defining The Path Animator To Use(in our case a point path animator)
PointAnimator pointAnimator = new PointAnimator(
        1, // The delay per rendering step
        point, // The particle object to use
        new Vector3f(0, 0, 0), // The point to animate at
        20 // The number of rendering steps
);

// Creating the renderer(in our case server-side rendering)
ApelRenderer renderer = ApelRenderer.create(world);

// Beginning The Animation
pointAnimator.beginAnimation(renderer);
```

This in minecraft translates into visually:<br>


https://github.com/GitBrincie212/Apel-Mod/assets/92397968/8e4f3a9c-547c-4ff8-8ade-63abd7fb7f86


In this code snippet we can clearly see that we define a particle object, and we define a path animator which contains
the `point` particle object.
We then create a renderer which is rendered as server-side in our case (we can use
client rendering which produces the same thing but does the rendering on the client).
Then we begin animation and supply the renderer to the animation which begins the animation
and displays a point (with that this explanation is done).
---
## Example 2
```java
ParticleLine line = new ParticleLine(
    ParticleTypes.END_ROD,        // The particle to use
    new Vector3f(0, -5, 0),       // The starting position of the line
    new Vector3f(0, 5, 0),        // The ending position
    50                            // The number of particles(as individual points)
);

LinearAnimator linearAnimator = new LinearAnimator(
    2,                            // The delay per rendering step
    new Vector3f(0, 0, 0),        // The starting position of the linear path
    new Vector3f(10, 0, 0),       // The ending position of the linear path
    line,                         // The particle object to use
    20                            // The number of rendering steps
);
        
// Trimming the animation (starting at a specific step then stopping at an ending step)
AnimationTrimming<Integer> trimming = new AnimationTrimming<>(
        5,    // The starting rendering step
        15    // The ending rendering step
);
linearAnimator.setTrimming(trimming);

ApelRenderer renderer = ApelRenderer.client(world);
linearAnimator.beginAnimation(renderer);
```

In the second example,
we define a particle line which has a starting and an ending position which are represented as
offsets to the draw position (for example, on the draw position of [5, 0, 0] the starting position
becomes [5, -5, 0]), it also has an "amount" dictating number of particles to use for the line (in our case, its 50).


We then define a linear path animator with a delay of 2, a starting position of [0, 0, 0] and an ending position of
[10, 0, 0] (note that these are not expressed as offsets but rather real world coordinates),
then we supply the particle line; Finally we define the number of rendering steps for the animation.

We create a new trim object which specifies what parts to trim the animation from - to. 
In our case, we have trimmed the five starting rendering steps and end at the fifteenth rendering step 

Unlike our previous example, we now use client-side rendering for the animation and then we play it