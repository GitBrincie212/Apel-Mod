# <center>APEL</center>
**A**nimative **P**article **E**ngine **L**ibrary, also known as Apel is a 
fully featured minecraft library mod that promises to bring a fully featured 
particle animation engine that has never been seen before. It comes bundled 
with many predefined shapes and has huge capabilities, its goal is to provide 
the developer huge flexibility as well as scenes being easy maintained with 
high performance. It also features a full command interface to use the library
even if you know nothing about java or just want test out some quick stuff.<br><br>

### Key features:
- **Particle Objects:** These are objects which render. They can be 2D, 3D shapes(such as a cube, circle, triangle) or 
even more complex ones like a cat or a dog(these aren't implemented to the library). They are classes that 
inherit from / extend the ``ParticleObject`` class. They also define a draw method which accepts the server world,
the current step the animator is in & the position to render at. This is where the render calculations happen in order
to render the result. Particle objects can be used in multiple animators which is called **multi-instancing**. They can
also define their own interceptors and attributes that can be modified by using the Interceptor API. Particle objects can
also be rotated in 3D **<ins>which is measured in radians</ins>** and unlike path animators they only mostly use an amount of particles 
for the shape(Since the shape is not a complete but rather a dotted one, where each dot is a particle). When using the
Interceptors API you are in control of what data you want to give the user & what data can be modified. Interceptors are
expressed as function that know have the data the object gave them and know which object they modify which means they
themselves can tweak public params. Once their calculations are done, they can ship the modified results by returning
them. Which you are in control on whatever to accept or not


- **Path Animators:** These define the trajectory a particle object should follow. They are used to create detailed
procedural animations. When beginning the animation logic, the system uses the so-called rendering steps. These are
basically like frames from a video in which they define certain changes. Particle objects are aware what step they are
on(as mentioned in the inner workings of the drawing method). Animators can start from the start to the end or some start
& some end parts can be trimmed(just like how you do in videos). The problem with rendering steps is that they are constant
and won't look pleasing on large distances which is why there are rendering intervals. They measure the distance per
rendering step which allow for more consistent looking animations on larger distances(at the expense of performance. Since
the server has to process more particles and calculations). They hold 2 methods, the first being ``convertStep`` which
takes care of the conversation between the rendering interval and the amount and ``beginAnimation`` which is where the
actual logic resides in, they should support trimming. When trimming happens at the start, all the calculations are done 
but the particle object is not rendered, trimming on the end just closes the loop. Path animators **MUST** allocate
a sequence first(they can do only one) which is done by using ``allocateToScheduler`` and then in order to draw,
the method ``handleDrawingStep`` should be used. Path animators come in bundle with listeners which are functions that
do not modify anything but listen to 3 specific events which happen when the animator starts, when it processes
(each step it is called) and when the animator ends(either normally or abruptly)<br><br>

- **Ease Functions:** These are mathematical functions that specify how a parameter should react based
on the rendering step it is in. An ease function can be simple as a constant(which just returns the constant),
a linear function or much more complex ones like elastic, back, rough.... etc. Some values accept an ease 
function while others don't, ease functions live in the XY plane where X is the rendering steps and Y is the 
new value for the parameter. You can define your custom ease functions via 2 ways. The first is to extend the
class & the second is by using the ``CustomEase`` and providing multiple ``EaseEntry`` (although it should be used
in scenarios where the rendering steps are known)<br>

### Getting Started
The first obvious thing is to create an ``ParticleObject``, pick your desired object to create and supply the params,
the second thing is creating the path animator object to use which will describe the motion of that object. And finally
use on the animator the ``beginAnimation`` and supply it with the server world to process the entire animation!<br>

### Credits & Contributions
This project is founded & lead by McBrincie212. Contributions are welcome as long as they don't do anything shady such
as embedding malware, doing inappropriate things.... After all the project is meant to be improved & expanded upon with
new ideas, new systems and many more stuff that boost productivity and enhance the work experience