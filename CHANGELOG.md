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
- [ ] Added more markdown documentation on specific topics
- [ ] Added the ability to allow for indefinite animations
- [ ] Added the ability to allow for looping animations
- [x] Added ``ParticleMirror`` for mirroring a particle object
- [ ] Added ``ParticleGrid`` for copying particle objects in an alternating pattern
- [ ] Added ``ParticleTrial`` for making a trial of particle objects



## Modifications
- [x] Reworked the way **Particle Objects** are created by using the builder pattern
- [x] Reworked the way **Path Animators** are created by using the builder pattern
- [ ] Simplified the ``ParticleBezierCurve`` interface
- [ ] Reworked the internal system of **Path Animators** to make it easier to create new ones
- [x] Reworked the handling of the metadata from interceptors

## Removals
- [x] Removed Listeners for **Path Animators**
- [x] Removed `RecursiveApelRenderer` (Even tho it was WIP)

## Patches
- [x] Fixed a bug where setting a vertex on ``ParticleTriangle`` wouldn't check if all of them are making up a triangle
- [x] Fixed a bug where setting a vertex on ``ParticleTetrahedron`` wouldn't check if all of them are making up a tetrahedron
- [ ] Fixed a unintended side effect of ``ParticleQuad`` 