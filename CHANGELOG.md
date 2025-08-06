<img src="./media/APEL_Changelog_Banner.png" alt="APEL Version 0.1.5 Release"/><br>
The **FIRST** ever update for _APEL_ to ever receive. It is a moderate update and brings to the table new things 
as well as reworks while also patching unintended stuff. We always wanted and still want to expand upon the library and 
create a better place for developers to use in their own projects

## Additions
- Added ``EasingCurve<T>``, allowing easy smooth-based animation on a parameter without the use of interceptors
- Added ``ParticleModel`` for 3D model compatibility (Wireframe Is Supported For Now)
- Added animation interceptors for **Path Animators**
- Added more markdown documentation on specific topics
- Added ``ParticleMirror`` for mirroring a particle object
- Added ``ParticleArray`` for copying particle objects in an alternating pattern
- Added ``ParticleBranchGen`` which allows the rendering of branch fractal patterns
- Added ``roundness`` to ``ParticlePolygon`` for rounding the shape
- Added ``ParticleMedia`` for rendering images (including even GIFs) and videos
- Added ``scale`` to ParticleObject. Used for adjusting the scale (can also be negative)
- Added ``deltaTimeTick`` to DrawContext, which is used for knowing the difference of time between the last tick and current tick
- Added ``numberOfSteps`` to DrawContext, which is used for knowing the steps of the path animator
- Support for 1.21.4

## Modifications
- Reworked the ``Interceptors API`` to allow stacking 
- Reworked the parameters in such a way to make them able to use ease curves (For particle objects for now)
- Reworked the way **Particle Objects** are created by using the builder pattern
- Reworked the way **Path Animators** are created by using the builder pattern
- Simplified the ``ParticleBezierCurve`` interface
- Reworked the internal system of **Path Animators** to make it easier to create new ones
- Reworked the handling of the metadata from interceptors
- Reworked the **Particle Objects** to separate Utility Particle Objects from Renderable Particle Objects 
(The ParticleCombiner is a particle object but not a utility one due to its nature)
- Redesigned The APEL Logo into a more modern slick design (For The RadiantFrame Project)

## Removals
- Removed Listeners for **Path Animators**
- Removed `RecursiveApelRenderer` (Even tho it was WIP)

## Patches
- Fixed a bug where setting a vertex on ``ParticleTriangle`` wouldn't check if all of them are making up a triangle
- Fixed a bug where setting a vertex on ``ParticleTetrahedron`` wouldn't check if all of them are making up a tetrahedron
- Fixed a bug where ``EllipseAnimator`` does squared the revolutions
- Fixed a bug where the game would crash with ``net.minecraft.util.crash.CrashException: Accessing LegacyRandomSource from multiple threads``
randomly (because APEL under the hood uses threads and because it renders the particles, it is unsafe)