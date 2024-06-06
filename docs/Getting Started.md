
https://github.com/GitBrincie212/Apel-Mod/assets/92397968/d6a642e8-77fc-4eaa-bd54-4bdf2d398204
# Getting Started
As from the `README.md` you know, Apel consists of 2 systems which compliment each other.
These systems allow for the flexible behaviour you see when writing code for complex particle 
scenes. The systems are **Particle Objects** and **Path Animators**

# What Are Particle Objects?
A Particle Object is a desired shape that is rendered as particles and projected into the minecraft world. A particle 
object has parameters that control how it is rendered in the world. A particle object**does not have a predefined center 
position**, the center position is given out by a different system which are called [path animators](#what-are-path-animators), 
the position that is given is called the **rendering position**. A Particle Object has by default as parameters:

- **Amount:** The amount of particles to use to draw the final result
- **Rotation** is the tilt in the XYZ plane, each coordinate is measured in RADIANS
- **Particle Effect:** the particle to use(this can be for example the "end rod particle", "bubble particle"....)

The rendering is handled via the ``draw`` method which is an internal method used by the system(so it should not be called from 
elsewhere). When path animators calls the ``draw`` method, we commonly refer to it as a **draw call**, they handle 
the logic of where to spawn the particles as well as interceptors(a sub system that is used in particle objects)

_TLDR; A particle object is the thing you see in the world. It has certain params which modify its looks. They also host
a method which is where the rendering happens(when a path animator calls it, we refer to it as a draw call), however
this method is internal and should not be used outside_

## What Are Interceptors?
Interceptors come from the **Interceptor API**, interceptors are basically methods(or lambda functions) that execute in
a certain places of code(these places are defined by the developer of where they are), they come with 2 arguments
- **Intercept Metadata** This is where local variables and other forms of data reside in. This is basically a hashmap
although, it also has common immutable variables stored in it which are <u>the current step, the server world and
the position to draw the particle object in</u>(drawing position ≠ render position). Some metadata might not be used
which is referred to as <u>metadata rejection</u> and for metadata that is used its referred as <u>metadata acceptance</u>
<br><br>
- **Object Instance** This is where you get the object instance that you are trying to modify. You can change its
params via setters & getters and even change its interceptors(from that interceptor). On most cases the object accepts
its modified counterpart(however sometimes it doesn't happen if it is made from another developer), in the case where
the modified instance is accepted it is referred to as <u>instance acceptance</u> and for the case when it isn't we call
it as <u>instance rejection</u>. Modified instances also stack on other interceptors sometimes this guideline may not
be followed from inexperienced developers(or for some special case scenarios), it is commonly called as <u>instance stacking</u>

When interceptors complete the modifications, they return a `InterceptResult` which holds the modified object and the
modified metadata, after which the code can dictate when should it use them, when not to use them or when to make some
adjustments

_TLDR; An interceptor is a function that modifies some metadata(given from the particle object) and the particle object 
itself. The particle object instance decides on whenever it wants to accept or not the modified metadata and the 
modified instance, it can also decide if it wants to put the modified instance in other interceptors that come right
after_

# What Are Path Animators
A path animator is used for defining the trajectory / path of where a particle object will go, and they inherit
from the ``ParticleAnimatorBase`` abstract class. Unlike particle objects they are not visually seen but rather 
they render the particle object on predefined positions that represent a path these positions are called as 
**rendering steps**, which host **rendering actions** and are similar to frames in normal animation. Rendering 
actions are the ones responsible for calling the ``draw`` method from the particle object. Rendering steps
usually process 1 after the other, but you can change the processing speed(we will touch on that later) to make it so
the rendering steps hold more than 1 rendering action. Path animators have a common set of params which are:
- **Delay:** The time between the rendering steps(which is counted in server ticks)
- **Process Speed:** The amount of draw calls in each rendering actions
- **Particle Object:** The object to use when rendering actions do a draw call

However, they also have **Rendering Steps Amount** and **The Rendering Interval**. They are different parameters
and one is used than the other in certain situations. When setting the rendering steps amount to any value, you tell the
path animator that this needs to be constant & the other value must be dynamic and same logic applies to the rendering 
interval

_TLDR; Path animators define an invisible path the object can take. The path is composed of positions and at each position
a rendering step processes its rendering actions(which call the ``draw`` method). Path animators host the particle object
itself as well as the delay, the process speed, the amount of rendering steps & the rendering interval

## Rendering Steps & Rendering Interval
Both parameters are calculated depending on which is given out to the path animator and which is not. Depending on how
the path animators defines their own path, the calculations might differ. Rendering steps(even if they are not explicitly
given as a constant) are used to tell the scheduler to allocate a new **sequence chunk** for that path animator with
this amount of rendering steps for the path animator to use. A rendering interval measures the amount of blocks for
one rendering step(which means the rendering steps are dynamically adjusted)

> The Rendering Steps leads to better performance for larger
distances(due to being a fixed amount and not dynamically changing) but has the drawback of being inconsistent at large 
distances due to the gaps it creates

> The Rendering Interval leads to more consistent looking animations
across large and small distances with the expense of performance(since very large distances
can produce many rendering steps)

For this reason its best to place as constant the amount of rendering steps when dealing with an animation that
has a known distance and for the rendering interval the opposite holds true

# Code Snippets
All of this is the basic theory of how these systems work. Let us now examine how we start using those systems in the
practical world. There will be 3 examples, and we will analyse the code and what it does, we will of course use the
simpler components the library has. Let's assume the code is running once to start the animation and that we have a
world variable that represents the ``ServerWorld``
## Example 1
```java
// Defining The Particle Object
ParticleObject point = new ParticleObject(
        ParticleTypes.END_ROD
);

// Defining The Path Animator To Use
PointAnimator pointAnimator = new PointAnimator(
        1, 
        point, 
        new Vector3f(0, 0, 0), 
        20
);

// Begin The Animation
pointAnimator.beginAnimation(world);
```

This in minecraft translates into visually:<br>


https://github.com/GitBrincie212/Apel-Mod/assets/92397968/8e4f3a9c-547c-4ff8-8ade-63abd7fb7f86


In this code snippet we can clearly see that we define a particle object, and we define a path animator which contains
the `point` particle object. We then use the ``beginAnimation`` method from the path animator by supplying the server
world instance to begin our animations
