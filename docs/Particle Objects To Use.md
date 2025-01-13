# Particle Objects To Use
This section describes the various pre-defined (mostly simple) particle objects that APEL includes. It is advised to
read upon [Getting Started Guide](Getting%20Started.md) for how particle objects work in a more detailed version

## Brief Summary Of Particle Objects
A particle object defines the general shape which is composed of multiple particles forming the desired representation.
They can be small such as an umbrella, duck or a skateboard to something more grand scale such as a skyscraper, a 
planet, a black hole... etc. Particle objects can hold properties to manipulate the way they are rendered. Particle
objects can belong to the **Drawable Category** or to the **Utility Category**. Where the utility ones are used for
controlling other particle objects and host parameters todo so, and the drawable objects are the rendered result

## All 2D Drawable Particle Objects
We will start from the simpler ones and work our way to the more niche and complex particle objects. Suppose our particle
objects are attached to the point path-animator that apel provides to us. Also assume that the path-animator
begins right away with server-side rendering. This is the code for the particle object we have

```java
PointAnimator pointAnimator = PointAnimator.builder()
    .point(new Vector3f())
    .particleObject(myParticleObject) // Particle Object
    .delay(1)
    .renderingSteps(100)
    .build();
```
**Disclaimer:** There may be lag during those videos. This lag is caused by the video itself, we apologize 
for this inconvenience that may be caused<br><br>

[Particle Point Object](../src/main/java/net/mcbrincie/apel/lib/objects/ParticlePoint.java) the most beginner-friendly
particle object out there, it simply draws one particle at that spot. It is useful when you don't want to take to account
moving the particle object along a predefined path and want to draw them using a path animator
```java
ParticlePoint particlePoint = ParticlePoint.builder()
        .particleEffect(ParticleTypes.END_ROD)
        .build();
```
https://github.com/GitBrincie212/Apel-Mod/assets/92397968/8e4f3a9c-547c-4ff8-8ade-63abd7fb7f86


[Particle Line Object](../src/main/java/net/mcbrincie/apel/lib/objects/ParticlePoint.java) another beginner-friendly
particle object to use, it simply draws a line composed of particles in a dotted fashion. You define a starting point 
and an ending point for the line
```java
ParticleLine particleLine = ParticleLine.builder()
        .particleEffect(ParticleTypes.END_ROD)
        .start(new Vector3f(0, -5, -5))
        .end(new Vector3f(5, 5, 0))
        .amount(50)
        .build();
```
[![Video Title](https://img.youtube.com/vi/WmOIl_7Ewfs/0.jpg)](https://www.youtube.com/watch?v=WmOIl_7Ewfs)


[Particle Circle Object](../src/main/java/net/mcbrincie/apel/lib/objects/ParticleCircle.java) yet another easy to use
particle object, it simply draws a circle composed of particles in a dotted fashion. You define a radius for the circle
and APEL takes care of the rest
```java
ParticleCircle particleCircle = ParticleCircle.builder()
    .particleEffect(ParticleTypes.END_ROD)
    .radius(2)
    .amount(50)
    .build();
```
[![Video Title](https://img.youtube.com/vi/qnh9hRActlM/0.jpg)](https://www.youtube.com/watch?v=qnh9hRActlM)


[Particle Ellipse Object](../src/main/java/net/mcbrincie/apel/lib/objects/ParticleEllipse.java) a bit more technical than
the particle circle object but still simple, it draws an ellipse composed of particles in a dotted fashion. 
You define a radius and a stretch for the ellipse and APEL does the rendering
```java
ParticleEllipse particleEllipse = ParticleEllipse.builder()
    .particleEffect(ParticleTypes.END_ROD)
    .radius(5)
    .stretch(3)
    .amount(80)
    .build();
```
[![Video Title](https://img.youtube.com/vi/xJA6HXZ-wk4/0.jpg)](https://www.youtube.com/watch?v=xJA6HXZ-wk4)


[Particle Triangle Object](../src/main/java/net/mcbrincie/apel/lib/objects/ParticleTriangle.java) while simple, it requires
passing 3 non-parallel points to form a triangle, it draws a triangle composed of particles in a dotted fashion.
```java
ParticleTriangle particleTriangle = ParticleTriangle.builder()
    .particleEffect(ParticleTypes.END_ROD)
    .vertex1(new Vector3f(-5, 0, 0))
    .vertex2(new Vector3f(5, 0, 0))
    .vertex3(new Vector3f(0, 5, 0))
    .amount(80)
    .build();
```
[![Video Title](https://img.youtube.com/vi/VtCu3HDuhAY/0.jpg)](https://www.youtube.com/watch?v=VtCu3HDuhAY)


[Particle Quad Object](../src/main/java/net/mcbrincie/apel/lib/objects/ParticleQuad.java) again its simple, but **may**
require passing 4 points to form a plane(or quad), it draws a quad composed of particles in a dotted fashion. You can
choose to whenever create a rectangle or any other four-sided polygons; for the sake of demonstration, we used the 
``rectangle`` builder property to alleviate some headaches of managing four vertices for a random four sided polygon
```java
ParticleQuad particleQuad = ParticleQuad.builder()
    .particleEffect(ParticleTypes.END_ROD)
    .rectangle(5, 3)
    .amount(80)
    .build();
```
[![Video Title](https://img.youtube.com/vi/HSENfY6aU4s/0.jpg)](https://www.youtube.com/watch?v=HSENfY6aU4s)


[Particle Polygon Object](../src/main/java/net/mcbrincie/apel/lib/objects/ParticlePolygon.java) one of the most simple
particle objects for generating more complex polygons. However, these are limited to normal polygons (isosceles triangle, 
square, pentagon... etc.)

**Disclaimer: there is a slight tilt without me applying it, it is unintended and is patched in this update. The video
is recorded before the polygon bug fix**
```java
ParticlePolygon particlePolygon = ParticlePolygon.builder()
    .particleEffect(ParticleTypes.END_ROD)
    .sides(5)
    .size(4)
    .amount(80)
    .build();
```
[![Video Title](https://img.youtube.com/vi/J6y32pkzLKw/0.jpg)](https://www.youtube.com/watch?v=J6y32pkzLKw)


[Particle Bézier Curve Object](../src/main/java/net/mcbrincie/apel/lib/objects/ParticleBezierCurve.java) more difficult
than particle line object since it deals with a curve (specifically a Bézier one), it draws the curve by composing particles 
in a dotted fashion. You supply a Bézier curve class in order for it to be displayed
```java
ParticleBezierCurve particleBezierCurve = ParticleBezierCurve.builder()
    .particleEffect(ParticleTypes.END_ROD)
    .bezierCurve(
            new CubicBezierCurve(
                    new Vector3f(-5, -5, -5),
                    new Vector3f(5, 5, 5),
                    new Vector3f(3, 3, 3),
                    new Vector3f(-10, -2, -6)
            )
    )
    .amounts(80)
    .build();
```
[![Video Title](https://img.youtube.com/vi/KSujqOWW28Y/0.jpg)](https://www.youtube.com/watch?v=KSujqOWW28Y)

