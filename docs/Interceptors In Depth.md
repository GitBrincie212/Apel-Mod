# Introduction
As you may read from [Getting Started](Getting%20Started.md) which it is recommended, you read as it explains the basics
of interceptors. You are aware of the interceptor system and its API. This system is used for executing code in an object 
(either a path animator or particle object) which allows for setting the parameters of the object during its drawing and 
other stuff. In this document, we will explore more in-depth how they work under the hood and both the perspectives on 
how to use them as a "developer" and as an "object creator" interceptors can land in one of the two categories, 
these being the **object interceptors** and the **path-animator interceptors** (also called animator interceptors)

## What Are Object Interceptors
Object interceptors are used for controlling directly the particle object's parameters. **They know the particle
object** (since they have the instance of the particle object) which makes them different from path-animator
interceptors. They can modify parameters tailored to the specific particle object instead of broadly or by checking
if this is the particle object they are looking for, just like path-animator interceptors they can execute code during
the particle object drawing

_TLDR; Object interceptors unlike path-animator interceptors directly know the type of the particle object they are
dealing with. But apart from that they execute code the same way as path-animator interceptors_

## How To Use Object Interceptors
As a **developer** of a separate mod (or trying to experiment with the library), to intercept a particle object it is as
simple as using the setter of the particle object which in our example is ``setBeforeDraw`` on a particle line. Assume
that ``particleObject`` is a created particle line object. So we have:
```java
particleObject.setBeforeDraw((data, obj) -> {
    // Your own code goes here
})
```
Another way that a **developer** could use interceptors is by having a class method instead of a lambda like so
```java
// (This is located in myOwnClass)
public void myOwnInterceptorLogic(DrawContext drawContext, ParticleLine particleLine) {
    // Your own code goes here
}

// (This is located somewhere else)
particleObject.setBeforeDraw(myOwnClass::myOwnInterceptorLogic)
```

## How To Create Your Own Object Interceptors
If you are an **object creator** well, it is up to you to specify your own custom interceptor but for this document. We will
copy the default apel implementation. Suppose we have the following class
```java
public class MyCustomParticleObject extends ParticleObject{
    protected MyCustomParticleObject(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, 
                builder.amount, builder.beforeDraw, builder.afterDraw);
        // <...>
    }

    protected MyCustomParticleObject(MyCustomParticleObject object) {
        super(object);
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext data) {
        // Drawing Implementation
    }

    public static class Builder<B extends Builder<B>> 
            extends ParticleObject.Builder<B, MyCustomParticleObject> {
        protected DrawInterceptor<MyCustomParticleObject> afterDraw;
        protected DrawInterceptor<MyCustomParticleObject> beforeDraw;
        protected DrawInterceptor<MyCustomParticleObject> myCustomInterceptor;

        private Builder() {}

        // <...>
        
        @Override
        public MyCustomParticleObject build() {
            return new MyCustomParticleObject(this);
        }
    }
}
```
Seems complex, but the point is not to focus on how the particle object is created but more so the process of
incorporating a custom interceptor to the particle object. We define inside the class, a protected parameter which is
our interceptor. Then we define a setter for our interceptor to allow the user to execute their code as the interceptor
```java
public class MyCustomParticleObject extends ParticleObject<MyCustomParticleObject> {
    protected DrawInterceptor<T> myCustomInterceptor = DrawInterceptor.identity();

    protected MyCustomParticleObject(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset,
                builder.amount, builder.beforeDraw, builder.afterDraw);
        this.setMyCustomInterceptor(builder.myCustomInterceptor);
    }
    
    // <...>
    public final void setMyCustomInterceptor(DrawInterceptor<T> myCustomInterceptor) {
        this.myCustomInterceptor = Optional
                .ofNullable(myCustomInterceptor)
                .orElse(DrawInterceptor.identity());
    }
    // <...>
}
```
After this then, of course, we have to create the logic on how the interceptor is executed. In our example, let's say we
want to execute the custom interceptor if the particle effect is an "end rod particle" we will execute the interceptor
inside the ``draw`` method. So we modify the current draw into this
```java
@Override
public void draw(ApelServerRenderer renderer, DrawContext data) {
    // <...>
    if (this.particleEffect == ParticleTypes.END_ROD) {
        this.myCustomInterceptor.apply(drawContext, (T) this);
    }
    // <...>
}
```
And that is how we define our simple custom interceptor. In the next paragraph, we will take a look at how we can pass
our own metadata into the interceptor. The other developers that use the particle object can simply use it like so:
```java
MyCustomParticleObject customObj = MyCustomParticleObject.builder()
        // After some parameters supplied to the builder
        .build();

customObj.setMyCustomInterceptor((data, obj) -> {
    // Code to be executed    
})
```

## Handling Object Interceptor Metadata
To append our metadata, we have to first learn about ``DrawContext``. This allows us to store the metadata in
a type safe way, it also has some specific immutable variables in store like the rendering step and the drawing
position. Let us return to our custom particle object and create a metadata key
```java
public class MyCustomParticleObject extends ParticleObject<MyCustomParticleObject> {
    // <...>
    public static final DrawContext.Key<Vector3f[]> OUR_KEY = DrawContext.integerKey("our_key");
    // <...>
}
```
Then we override ``prepareContext`` to append our metadata
```java
@Override
protected void prepareContext(DrawContext drawContext) {
    // <...>
    drawContext.addMetadata(OUR_KEY, 123456);
}
```
And now we are done, lets check if the metadata is modified to something else like -1 and then execute our custom
interceptor if ``OUR_KEY`` is -1
```java
@Override
public void draw(ApelServerRenderer renderer, DrawContext data) {
    // <...>
    int ourKey = data.getMetadata(OUR_KEY);
    if (ourKey == -1) {
        this.myCustomInterceptor.apply(drawContext, (T) this);
    }
    // <...>
}
```
The metadata will also be accessed via the custom interceptor. We can prevent this if we want by keeping a copy
of the ``DrawContext`` metadata

## What Are Path Animation Interceptors
Path animation interceptors, also known as animation interceptors, are similar in spirit to object interceptors. 
However, they handle their metadata with ``AnimationContext`` and instead of modifying a particle object, they modify 
a path animator. They can also modify more generally the particle object without knowing its type, for example,
ParticleCircle, ParticlePolygon... etc. Animation interceptors execute before any object interceptors

_TLDR;_ Path animation interceptors modify the path of the path animator and the path animator itself, they have their
own way of handling metadata using ``AnimationContext``, but it is very similar to object interceptor. They can also
modify the particle objects without knowing its type

## How To Use Path Animation Interceptors
As a **developer** of a separate mod (or trying to experiment with the library), to intercept a path animators it is as
simple as using the setter of the path animator which in our example is ``setBeforeRender`` on a linear path animator. Assume
that ``pathAnimator`` is a created linear path animator. So we have:
```java
pathAnimator.setBeforeRender((data, obj) -> {
    // Your own code goes here
})
```
Another way that a **developer** could use interceptors is by having a class method instead of a lambda like so
```java
// (This is located in myOwnClass)
public void myOwnInterceptorLogic(AnimationContext animationContext, LinearAnimator linearAnimator) {
    // Your own code goes here
}

// (This is located somewhere else)
pathAnimator.setBeforeDraw(myOwnClass::myOwnInterceptorLogic)
```

## How To Create Your Own Path Animation Interceptors
If you are a **path animation creator** well, it is up to you to specify your own custom interceptor but for this document. We will
copy the default apel implementation. Suppose we have the following class
```java
public class MyCustomPathAnimator extends PathAnimatorBase<MyCustomPathAnimator> {
    private <B extends Builder<B>> MyCustomPathAnimator(Builder<B> builder) {
        super(builder);
        // <...>
    }

    public MyCustomPathAnimator(MyCustomPathAnimator animator) {
        super(animator);
        // <...>
    }

    @Override
    public void beginAnimation(ApelServerRenderer renderer) throws SeqDuplicateException, SeqMissingException {
        // Animation logic goes here
    }

    public static class Builder<B extends Builder<B>> extends PathAnimatorBase.Builder<B, LinearAnimator> {
        protected AnimationInterceptor<T> myCustomInterceptor = AnimationInterceptor.identity();
        // <...>
        
        @Override
        public MyCustomPathAnimator build() {
            return new MyCustomPathAnimator(this);
        }
    }
}
```
Seems complex, but the point is not to focus on how the path animator is created but more so the process of
incorporating a custom interceptor to the path animator. We define inside the class, a protected parameter which is
our interceptor. Then we define a setter for our interceptor to allow the user to execute their code as the interceptor
```java
public class MyCustomPathAnimator extends PathAnimatorBase<MyCustomPathAnimator> {
    protected AnimationInterceptor<T> myCustomInterceptor = AnimationInterceptor.identity();

    public MyCustomPathAnimator(MyCustomPathAnimator animator) {
        super(animator);
        this.setMyCustomInterceptor(builder.myCustomInterceptor);
    }
    
    // <...>
    public final void setMyCustomInterceptor(AnimationInterceptor<T> myCustomInterceptor) {
        this.myCustomInterceptor = Optional
                .ofNullable(myCustomInterceptor)
                .orElse(DrawInterceptor.identity());
    }
    // <...>
}
```
After this then, of course, we have to create the logic on how the interceptor is executed. In our example, let's say we
want to execute the custom interceptor if the path animator is at an even rendering step we will execute the interceptor
inside the ``beginAnimation`` method. So we modify the current draw into this
```java
@Override
public void beginAnimation(ApelServerRenderer renderer) throws SeqDuplicateException, SeqMissingException {
    for (int i = 0; i < stepsForMyAnimation; i++) {
        // <...>
        if (i % 2 == 0) {
            AnimationContext animationContext = new AnimationContext(renderer.getServerWorld());
            this.beforeRender.apply(animationContext, this);
        }
        // <...>
    }
}
```
And that is how we define our simple custom interceptor. In the next paragraph, we will take a look at how we can pass
our own metadata into the interceptor. The other developers that use the path animator can simply use it like so:
```java
MyCustomPathAnimator customPathAnimator = MyCustomPathAnimator.builder()
        // After some parameters supplied to the buidler
        .build();

customPathAnimator.setMyCustomInterceptor((data, obj) -> {
    // Code to be executed    
})
```

## Handling Object Interceptor Metadata
To append our metadata, we have to first learn about ``AnimationContext``. This allows us to store the metadata in
a type safe way, it also has some specific immutable variables in store like the server world. Let us return to our 
custom particle object and create a metadata key
```java
public class MyCustomParticleObject extends ParticleObject<MyCustomParticleObject> {
    // <...>
    public static final Key<Integer> OUR_KEY = Key.integerKey("our_key");
    // <...>
}
```
Then we append the key to the animation context before executing using the following code
```java
animationContext.addMetadata(OUR_KEY, 123456);
```
And now we are done, lets check if the metadata is modified to something else like -1 and then execute some 
other arbitrary code if ``OUR_KEY`` is -1
```java
@Override
public void draw(ApelServerRenderer renderer, DrawContext data) {
    // <...>
    if (i % 2 == 0) {
        AnimationContext animationContext = new AnimationContext(renderer.getServerWorld());
        animationContext.addMetadata(OUR_KEY, 123456);
        this.beforeRender.apply(animationContext, this);
        int ourKey = animationContext.getMetadata(OUR_KEY);
        if (ourKey == -1) {
            System.out.println("Secret Code Unlocked?");
        }
    }
    // <...>
}
```
The metadata will also be accessed via the custom interceptor. We can prevent this if we want by keeping a copy
of the ``AnimationContext`` metadata