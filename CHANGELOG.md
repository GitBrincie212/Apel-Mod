<h1 style="text-align: center;"> APEL - v0.2.0</h1><br>
The first update for apel to ever receive, brings to the table new things as well as reworks and additionally patches
unintended stuff. We always wanted and still want to expand upon the library and making it a better place for developers
to use in their own projects

## Additions
- [ ] Added new baking method with various options
- [ ] Added full support for ``ParticleImage``
- [ ] Added ``ParticleModel`` for 3D model compatibility
- [x] Added animation interceptors for **Path Animators**
- [ ] Added the ability to stack interceptors
- [ ] Added ``ParticleText`` for text rendering with particles
- [x] Added more markdown documentation on specific topics
- [x] Added ``ParticleMirror`` for mirroring a particle object
- [x] Added ``ParticleArray`` for copying particle objects in an alternating pattern
- [ ] Added ``ParticleTrial`` for making a trial of particle objects
- [x] Added ``roundness`` to ``ParticlePolygon`` for rounding the shape
- [ ] Added ``scale`` to ParticleObject. Used for adjusting the scale(can also be negative)
- [ ] Support for 1.20.1 (due to its popularity)
- [ ] Support for 1.21.4

## Modifications
- [x] Reworked the way **Particle Objects** are created by using the builder pattern
- [x] Reworked the way **Path Animators** are created by using the builder pattern
- [x] Simplified the ``ParticleBezierCurve`` interface
- [ ] Reworked the internal system of **Path Animators** to make it easier to create new ones
- [x] Reworked the handling of the metadata from interceptors

## Removals
- [x] Removed Listeners for **Path Animators**
- [x] Removed `RecursiveApelRenderer` (Even tho it was WIP)

## Patches
- [x] Fixed a bug where setting a vertex on ``ParticleTriangle`` wouldn't check if all of them are making up a triangle
- [x] Fixed a bug where setting a vertex on ``ParticleTetrahedron`` wouldn't check if all of them are making up a tetrahedron
- [x] Fixed a bug where ``EllipseAnimator`` does squared the revolutions
- [x] Fixed a small oversight where ``ParticleCylinder`` builder wouldn't create the ``ParticleCylinder`` and just return null