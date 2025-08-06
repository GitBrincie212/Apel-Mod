# <center>APEL</center>
**A**nimative **P**article **E**ngine **L**ibrary, also known as Apel is a 
fully featured minecraft library mod that promises to bring a fully featured 
particle animation engine that has never been seen before. It comes bundled 
with many predefined shapes and has huge capabilities; its goal is to provide 
the developer huge flexibility and allow to easily maintain complex scenes with 
high performance.
All without the need of knowing complex mathematics (such as linear algebra, although a basic grasp is recommended)<br><br>

## Gallery
<img alt="particle-model-1" src="https://github.com/user-attachments/assets/49195959-652f-46aa-9927-935d5be57ef3" width="35%">
<img alt="particle-model-2" src="https://github.com/user-attachments/assets/1367705d-f1da-4dc9-ad05-62025d3f5fdb" width="35%">
<img alt="particle-sphere" src="https://github.com/user-attachments/assets/416022cc-8c0d-44ad-88e4-47e65a52e7cc" width="35%">
<img alt="particle-polygon" src="https://github.com/user-attachments/assets/79bff9eb-d081-4317-a13b-ebf53ad4cf2f" width="35%">
<img alt="complex-particle-animation-1" src="https://github.com/user-attachments/assets/d1fb4dfb-32fc-40af-9154-fef28f084098" width="35%">
<img alt="complex-particle-animation-2" src="https://github.com/user-attachments/assets/00453091-4a13-4bc3-9020-c2cde9141cbc" width="35%">

## Installion
This section is for the developers, for any normal people. Its as simple as downloading the mod and adding the jar file(not the sources) to your mods folder. For developers it has a bit more steps but its generally easy as well

1. Under the ``gradle.properties`` file. Add this line:
   ```java
   apel_version=0.1.0+1.20.6
   ```
2. On ``build.gradle`` file, under the **repositery**. Add these lines if you haven't installed any other dependency mods from modrinth:
   ```java
   repositories {
    // ...
      maven {
          url = "https://api.modrinth.com/maven"
      }
    }
   ```
3. On the same ``build.gradle`` file, finally we add these lines under the **dependencies**:
   ```java
   dependencies {
      // ...
      modImplementation "maven.modrinth:apel:${project.apel_version}"
      // ...
   }
4. Refresh gradle(in Intelleji IDEA, its pressing the gradle icon with a rotate sub-icon)
5. Try to either type ``Apel`` and let your IDE autocomplete it, or import ``net.mcbrincie.apel.Apel``. If all steps are done everything should work as expected

## Key Features
- **Particle Objects:** These are objects which render, they can be 2D, 3D shapes (such as a cube, circle, triangle) or 
even more complex ones like a cat or a dog (these aren't implemented to the library), they are classes that inherit 
from / extend the ``ParticleObject`` class, they also define a draw method which accepts the renderer, the current step 
the animator is in & the position to render at. 
This is where the render calculations happen to project the result onto the world; Particle objects can be used in 
multiple animators which is called **multi-instancing**. 
They can also define their own interceptors and attributes that can be modified by using the Interceptor API. 
Particle objects can also be rotated in 3D **<ins>which is measured in radians</ins>**. 
Unlike path animators they only use an "amount" of particles for the shape 
(Since the shape is not a complete but rather a dotted one, where each dot is a particle). 
When using the Interceptors API, you are in control of what data you want to give the user & what data can be modified. 
Interceptors are expressed as function that know have the data the object gave them and know which object they modify 
which means they themselves can tweak public params. Particle objects describe things in a high-level, since they are
shapes and have specific properties to them.


- **Path Animators:** These define the trajectory a particle object should follow. They are used to create detailed
and even procedural animations. When beginning the animation logic, the system uses the so-called rendering steps. These are
basically like frames from a video(or animation) in which they define certain changes. Particle objects are aware 
what step they are on (as mentioned in the inner workings of the drawing method).
Animators can play from the start to the end or some start & some end parts can be trimmed (just like how you do in videos). 
The problem with rendering steps is that they are constant and won't look pleasing on large distances which is why 
there are rendering intervals. They measure the distance per rendering step which allows for more consistent looking 
animations on larger distances (at the expense of performance. Since the server has to process more particles and calculations). 
They hold two methods, the first being ``convertStep`` which takes care of the conversation between the rendering interval 
and the amount and ``beginAnimation`` which is where the actual logic resides in, they should support trimming. When 
trimming happens at the start, all the calculations are done, but the particle object is not rendered, trimming on the end 
just breaks the loop. Path animators **MUST** allocate a sequence first (they can do only one) which is done by 
using ``allocateToScheduler`` and to then draw, the method ``handleDrawingStep`` should be used. Path animators 
come in a bundle with listeners which listen to three specific events which happen when the animator starts 
when it processes (each step it is called) and when the animator ends (either normally or abruptly)<br><br>

- **Renderers:** Renderers draw stuff in the world. They don't know which particle object calls the method but they know what to do
with the properties that the particle object gives them. They are described as a low-level system due to their simple nature of knowing
where and what to draw in the world. They have some methods for how to draw primitive shapes(defined by the library)<br>

## Getting Started
The first obvious thing is to create an ``ParticleObject``, pick your desired object to create and supply the params,
the second thing is creating the path animator object to use which will describe the motion of that object. And finally
use on the animator the ``beginAnimation`` and supply it with ``ApelRenderer.create(world)`` where world is the 
server world, now it should play entire animation!<br>

## Credits & Contributions
This project is founded & led by McBrincie212, contributions are welcome as long as they don't do anything shady such
as embedding malware, doing inappropriate things... 
After all, the project is meant to be improved & expanded upon with new ideas, 
new systems and more stuff that boost productivity and enhance the work experience
