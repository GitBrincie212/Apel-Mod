# Baking Animations

Some animations are used repeatedly, and it would be useful to pre-calculate them. This is what baking can do.
Instead of using the `client` or `create` renderers, selecting the `baking` renderer will render to a file that can be
used later when rendering on either the server or client sides.

Here's an example of using the baking renderer:

```java
@Override
public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    if (world.isClient) {
        return TypedActionResult.pass(user.getMainHandStack());
    }

    ParticleTetrahedron tetrahedron = ParticleTetrahedron.builder()
            .vertex1(new Vector3f(-4f, 0, -3f))
            .vertex2(new Vector3f(4f, 0, -3f))
            .vertex3(new Vector3f(0f, 0, 5f))
            .vertex4(new Vector3f(0, 7, 0))
            .amount(12)
            .particleEffect(ParticleTypes.COMPOSTER)
            .offset(new Vector3f(4f, 0f, 0f))
            .afterDraw(this::rotator)
            .build();
    
    PointAnimator pointAnimator = PointAnimator.builder()
            .particleObject(tetrahedron)
            .delay(1)
            .point(new Vector3f())
            .renderingSteps(600)
            .build();
    ApelRenderer bakingRenderer = ApelServerRenderer.baking(world, "rotating-tetrahedron");
    pointAnimator.beginAnimation(bakingRenderer);

    return TypedActionResult.pass(user.getMainHandStack());
}

private void rotator(DrawContext data, ParticleObject<? extends ParticleObject<?>> obj) {
    obj.setRotation(obj.getRotation().add(0f, 0.01f, 0f));
}
```
That will produce a file in the Minecraft current working directory that contains all the instructions necessary to 
render the tetrahedron rotating around its relative origin.