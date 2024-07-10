<h1 style="text-align: center;"> APEL - v0.2.0</h1><br>
The first update ever for apel to receive, brings to the table new things as well as reworks and additionally patches
unintended stuff. We always wanted and still want to expand upon the library and making it a better place for developers
to use in their own projects

## Additions
- [ ] Added new baking method with various options
- [ ] Added full support for ``ParticleImage``
- [ ] Added ``ParticleModel`` for 3D model compatibility
- [ ] Added animation interceptors for **Path Animators**
- [ ] Added the ability to stack interceptors
- [ ] Added ``ParticleText`` for text rendering with particles
- [ ] Added more markdown documentation on specific topics



## Modifications
- [ ] Reworked the way **Particle Objects** are created by using the builder pattern
- [ ] Reworked the way **Path Animators** are created by using the builder pattern
- [ ] Simplified the ``ParticleBezierCurve`` interface
- [ ] Reworked the internal system of **Path Animators** to make it easier to create new ones
- [ ] Reworked the object interceptors to intercept for the drawing of individual particles
- [ ] Reworked the handling of the metadata from interceptors

## Removals
- [ ] Removed Listeners for **Path Animators**
- [ ] Removed `RecursiveApelRenderer` (Even tho it was WIP)

## Patches
- [ ] Fixed a bug where setting a vertex on ``ParticleTriangle`` wouldn't check if all of them are making up a triangle
- [ ] Fixed a bug where setting a vertex on ``ParticleTetrahedron`` wouldn't check if all of them are making up a tetrahedron