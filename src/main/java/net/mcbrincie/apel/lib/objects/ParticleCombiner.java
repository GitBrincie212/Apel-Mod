package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/** A utility particle object class that groups all particle objects as
 * one object instead of multiple. Particle combiners can also group themselves
 * which can produce an object hierarchy. There many good things about using
 * a particle combiner in most cases, examples include but are not limited to
 * <br><br>
 * <center><h2>Advantages</h2></center>
 * <br>
 *
 * <b>Fewer Memory Overhead(s) & Smaller Memory Footprint</b><br>
 * Since objects are grouped together. This means that there will be fewer particle animators created
 * as well as the object being handled as 1 particle object instance instead of being multiple. Which
 * means that memory is dramatically reduced down for complex scenes(if you had 10 path animators for 10
 * objects, in which the path animators do 1 million rendering step. The entire bandwidth is cut down
 * from 10 million -> 1 million steps allocated to the scheduler for the processing).<br><br>
 *
 * <b>Easier Management On Multiple Complex Objects</b><br>
 * The main premise of the particle combiner is to combine particle objects as 1. Which can simplify
 * repetitive logic and instead of passing the objects into separate path animators(that contain the
 * almost same params and are the same type). Now you can pass it in only 1 path animator, there are
 * common methods for managing the object instances which further simplify the repetitive process.<br><br>
 *
 * <b>Dynamic Object Allocation At Runtime</b><br>
 * Without the particle combiner, it is difficult to create objects at runtime and create a new path animator
 * that inherits almost all the same attributes as all the other animators for the other objects & programmatically
 * changing the params is very tedious. This doesn't have to be the case, because you can allocate a new particle
 * object to the particle combiner and from there APEL would take care the rest.<br><br>
 *
 * <b>Controlling Objects Before Being Drawn</b><br>
 * Developers may use {@link #setBeforeChildDraw(DrawInterceptor)} to control the object itself before other
 * interceptors from that object apply.  They may also choose whether to draw the object or not by modifying
 * {@code CAN_DRAW_OBJECT}, which this logic is not possible without changing the particle object's class.<br><br>
 *
 * <b>Hierarchical Grouping</b><br>
 * Particle combiners can also combine themselves which can allow for the creation of tree-like particle hierarchies,
 * without the need of making your own particle object class and configuring the params to work that way. All
 * this logic you would have to go and implement is handled for you. You can also use recursive
 * methods to scale down the tree and modify the values<br><br>
 *
 * @param <T> The type of the object, can also be set to <?> to accept all particle objects
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCombiner<T extends ParticleObject> extends ParticleObject {
    protected List<T> objects = new ArrayList<>();
    protected int amount = -1;
    protected List<Vector3f> offsets = new ArrayList<>();

    public DrawInterceptor<ParticleCombiner<T>, AfterChildDrawData> afterChildDraw = DrawInterceptor.identity();
    public DrawInterceptor<ParticleCombiner<T>, BeforeChildDrawData> beforeChildDraw = DrawInterceptor.identity();

    /** There is no data being transmitted */
    public enum AfterChildDrawData {}

    /** This data is used after calculations(it contains the modified 4 vertices) */
    public enum BeforeChildDrawData {
        OBJECT_IN_USE, CAN_DRAW_OBJECT
    }

    /** The constructor for the particle combiner. Which is a utility class that
     * helps in grouping particle objects together as 1 single particle object.
     * Which of course has many benefits such as being able to directly modify
     * the objects themselves without needing to set one after the other to a
     * specific value. There is a simpler constructor for no rotation
     * <br><br>
     * <b>Note:</b> it uses the {@code setRotation} which sets all the
     * particle object's rotation values to the provided rotation value
     *
     * @param rotation The rotation to apply
     * @param objects The objects to group together
     *
     * @see ParticleCombiner#ParticleCombiner(ParticleObject[])
     */
    @SafeVarargs
    public ParticleCombiner(Vector3f rotation, T... objects) {
        super(ParticleTypes.SCRAPE); // We do not care about the particle
        if (objects.length == 0) {
            throw new IllegalArgumentException("At least one object has to be supplied");
        }
        this.particleEffect = null;
        this.setObjects(objects);
        this.setRotation(rotation);
    }

    /** The constructor for the particle combiner. Which is a utility class that
     * helps in grouping particle objects together as 1 single particle object.
     * Which of course has many benefits such as being able to directly modify
     * the objects themselves without needing to set one after the other to a
     * specific value. There is a simpler constructor for no rotation
     * <br><br>
     * <b>Note:</b> it uses the {@code setRotation} which sets all the
     * particle object's rotation values to the provided rotation value
     *
     * @param objects The objects to group together
     *
     * @see ParticleCombiner#ParticleCombiner(ParticleObject[])
     */
    public ParticleCombiner(List<T> objects) {
        super(ParticleTypes.SCRAPE); // We do not care about the particle
        this.particleEffect = null;
        this.setObjects(objects);
        this.setRotation(rotation);
    }

    /** The constructor for the particle combiner. Which is a utility class that
     * helps in grouping particle objects together as 1 single particle object.
     * Which of course has many benefits such as being able to directly modify
     * the objects themselves without needing to set one after the other to a
     * specific value. There is a more complex constructor for rotation
     *
     * @param objects The objects to group together
     *
     * @see ParticleCombiner#ParticleCombiner(Vector3f, ParticleObject[])
    */
    @SafeVarargs
    public ParticleCombiner(T... objects) {
        super((ParticleEffect) null); // We do not care about the particle
        this.setObjects(objects);
    }

    public ParticleCombiner() {
        super((ParticleEffect) null); // We do not care about the particle
    }

    /** The copy constructor for the particle combiner. Which
     * copies all the objects and the interceptors into a brand-new
     * instance of the particle combiner
     *
     * @param combiner The particle combiner to copy from
     */
    public ParticleCombiner(ParticleCombiner<T> combiner) {
        super(combiner);
        this.objects = combiner.objects;
        this.amount = combiner.amount;
        this.offsets = combiner.offsets;
        this.afterChildDraw = combiner.afterChildDraw;
        this.beforeChildDraw = combiner.beforeChildDraw;
    }

    /** Sets the rotation for all the particle objects. There is
     * another method that allows for offsetting the rotation per particle
     * object, it is the same as using this one with the offset params
     *
     * @param rotation The new rotation(IN RADIANS)
     * @return The previous rotation used
     *
     * @see ParticleCombiner#setRotation(Vector3f, float, float, float)
     * @see ParticleCombiner#setRotation(Vector3f, Vector3f)
    */
    @Override
    public Vector3f setRotation(Vector3f rotation) {
        Vector3f prevRotation = this.rotation;
        this.rotation = rotation;
        for (T object : this.objects) {
            object.setRotation(this.rotation);
        }
        return prevRotation;
    }

    /** Sets the rotation for all the particle objects, there
     * is an offset of XYZ per object. There is also a simplified
     * method that doesn't use offsets
     *
     * @param rotation The new rotation(IN RADIANS)
     * @param offsetX the offset x
     * @param offsetY the offset y
     * @param offsetZ the offset z
     * @return The previous rotation
     *
     * @see ParticleCombiner#setRotation(Vector3f)
     */
    public Vector3f setRotation(Vector3f rotation, float offsetX, float offsetY, float offsetZ) {
        if (offsetX == 0 && offsetY == 0 && offsetZ == 0) {
            throw new IllegalArgumentException("offset rotation must not equal (0, 0, 0)");
        }
        Vector3f prevRotation = this.rotation;
        this.rotation = rotation;
        int i = 0;
        for (T object : this.objects) {
            object.setRotation(this.rotation.add(offsetX * i, offsetY * i, offsetZ * i));
            i++;
        }
        return prevRotation;
    }

    /** Sets the rotation for all the particle objects, there
     * is an offset of XYZ per object. There is also a simplified
     * method that doesn't use offsets
     *
     * @param rotation The new rotation(IN RADIANS)
     * @param offset the offset x
     * @return The previous rotation
     *
     * @see ParticleCombiner#setRotation(Vector3f)
     */
    public Vector3f setRotation(Vector3f rotation, Vector3f offset) {
        if (offset.equals(new Vector3f())) {
            throw new IllegalArgumentException("offset rotation must not equal (0, 0, 0)");
        }
        Vector3f prevRotation = super.setRotation(rotation);
        int i = 0;
        for (T object : this.objects) {
            object.setRotation(this.rotation.add(offset.x * i, offset.y * i, offset.z * i));
            i++;
        }
        return prevRotation;
    }

    /** Sets the rotation for all the particle objects(this includes objects
     * that are way below the hierarchy). There is another method that allows
     * for offsetting the rotation per particle object, it is the same
     * as using this one with the offset params
     *
     * @param rotation The new rotation(IN RADIANS)
     *
     * @see ParticleCombiner#setRotation(Vector3f, float, float, float)
     * @see ParticleCombiner#setRotation(Vector3f, Vector3f)
     */
    public void setRotationRecursively(Vector3f rotation) {
        for (T object : this.objects) {
            if (object instanceof ParticleCombiner<?> combiner) {
                combiner.setRotationRecursively(rotation);
            }
            object.setRotation(rotation);
        }
    }

    /** Sets the rotation for all the particle objects(including the objects that are
     * below the hierarchy), there is an offset of XYZ per object. There is as well a
     * simplified method that doesn't use offsets
     *
     * @param rotation The new rotation(IN RADIANS)
     * @param offset the offset rotation
     *
     * @see ParticleCombiner#setRotationRecursively(Vector3f)
     */
    public void setRotationRecursively(Vector3f rotation, Vector3f offset) {
        if (offset.equals(new Vector3f())) {
            throw new IllegalArgumentException("offset rotation must not equal (0, 0, 0)");
        }
        int i = 0;
        for (T object : this.objects) {
            if (object instanceof ParticleCombiner<?> combiner) {
                combiner.setRotationRecursively(
                        rotation.add(offset.x * i, offset.y * i, offset.z * i), offset
                );
            }
            object.setRotation(rotation.add(offset.x * i, offset.y * i, offset.z * i));
            i++;
        }
    }

    /**
     * Gets the offsets per object and returns a list
     *
     * @return The list of offsets per object
     */
    public List<Vector3f> getOffsets() {
        return this.offsets;
    }

    /** Get the offset for the object at the given index.
     *
     * @param index the index of the object
     * @return the offset of the object at the given index
     */
    public Vector3f getOffset(int index) {
        return this.offsets.get(index);
    }

    /**
     * Sets the offset position per object. The offset positions have to be the same
     * amount as the objects. New objects added will have their offset to (0,0,0). There
     * is a helper method to allow to set for all objects the same offset
     *
     * @param offset The offsets for each object(corresponding on each index)
     * @return The previous offsets
     */
    public List<Vector3f> setOffsets(Vector3f... offset) {
        if (offset.length != this.objects.size()) {
            throw new IllegalArgumentException("Must provide an offset for every object!");
        }
        List<Vector3f> prevOffset = this.offsets;
        // Do not use 'toList()' as it results in an unmodifiable List
        this.offsets = Arrays.stream(offset).collect(Collectors.toCollection(ArrayList::new));
        return prevOffset;
    }

    /**
     * Sets the offset position to all objects. The offset applies to all objects and overwrites
     * the values. New objects added will have their offset to (0,0,0). There is a
     * helper method to allows to set different offsets to different objects
     *
     * @param offset The offsets for all the objects
     * @return The previous offsets
     */
    public List<Vector3f> setOffsets(Vector3f offset) {
        List<Vector3f> prevOffset = this.offsets;
        Vector3f newOffset = Optional.ofNullable(offset).orElse(new Vector3f());
        int objectCount = this.objects.size();
        this.offsets = new ArrayList<>(objectCount);
        for(int i = 0; i < objectCount; i++) {
            // Ensure every offset has a unique reference, so a future modification doesn't impact all of them
            this.offsets.add(new Vector3f(newOffset));
        }
        return prevOffset;
    }

    /** Sets the offset position for the individual object by supplying an index and the
     * new offset for that object.
     *
     * @param offset The offsets for the indexed object
     * @return The previous offset
     */
    public Vector3f setOffset(int index, Vector3f offset) {
        Vector3f prevOffset = new Vector3f(this.offsets.get(index));
        this.offsets.set(index, offset);
        return prevOffset;
    }

    /** Sets the offset position for the individual object by supplying the object and the
     * new offset for that object. If the object is not found it will return null.
     *
     * @param offset The offsets for the individual object
     * @return The previous offset
     */
    public Vector3f setOffset(T object, Vector3f offset) {
        int index = this.objects.indexOf(object);
        if (index == -1) return null;
        return this.setOffset(index, offset);
    }

    /** Sets the particle to use to a new value and returns the previous particle that was used.
     * This applies to all the object. The value can also be null meaning that there are different
     * particle effects at play in the object
     *
     * @param particle The new particle
     * @return The previous particle
    */
    @Override
    public ParticleEffect setParticleEffect(ParticleEffect particle) {
        ParticleEffect prevParticle = super.setParticleEffect(particle);
        for (T object : this.objects) {
            object.setParticleEffect(particle);
        }
        return prevParticle;
    }

    /** Sets the particle to use to a new value and returns the previous particle that was used.
     * This applies to all the object(including objects that are way below the hierarchy). The value
     * can also be null meaning that there are different particle effects at play in the object
     * <br><br>
     *
     * @param particle The new particle
     * @return The previous particle
     */
    public ParticleEffect setParticleEffectRecursively(ParticleEffect particle) {
        ParticleEffect prevParticle = super.setParticleEffect(particle);
        for (T object : this.objects) {
            if (object instanceof ParticleCombiner<?> combiner) {
                combiner.setParticleEffectRecursively(particle);
            }
            object.setParticleEffect(particle);
        }
        return prevParticle;
    }

    /** Sets the particle to use to a new value and returns the previous particle that was used.
     * This applies to all the object(including objects that are way below the hierarchy). The value
     * can also be null meaning that there are different particle effects at play in the object.
     * However unlike the {@code setParticleEffectRecursively(ParticleEffect)} which is a list of
     * the particles to use per depth level<br><br>
     *
     * <b>Note:</b> If there is no more particles to supply in the current depth level the system is,
     * it will resort in not going below. Keep that in mind
     *
     * @param particle The particles per level
     *
     * @see ParticleCombiner#setParticleEffectRecursively(ParticleEffect)
     */
    public void setParticleEffectRecursively(ParticleEffect[] particle) {
        this.particleEffect = null;
        this.particleEffectRecursiveLogic(particle, 0);
    }

    private void particleEffectRecursiveLogic(ParticleEffect[] particle, int depth) {
        for (T object : this.objects) {
            if (depth >= particle.length) break;
            if (object instanceof ParticleCombiner<?> combiner) {
                object.particleEffect = null;
                combiner.particleEffectRecursiveLogic(particle, depth + 1);
            }
            object.setParticleEffect(particle[depth]);
        }
    }

    /** Sets the amount to use to a new value and returns the previous amount that was used.
     * This applies to all the object. The value can also be -1 meaning that there are different
     * particle effects at play in the object. There is a method that allows for offsets per
     * particle objects
     *
     * @param amount The new particle
     *
     * @return The previous particle
     *
     * @see ParticleCombiner#setAmount(int, int)
    */
    @Override
    public int setAmount(int amount) {
        int prevAmount = super.setAmount(amount);
        for (T object : this.objects) {
            object.setAmount(amount);
        }
        return prevAmount;
    }

    /** Sets the amount to use to a new value and returns the previous amount that was used.
     * This applies to all the object. The value can also be -1 meaning that there are different
     * particle effects at play in the object. The offset param changes the amount per object by
     * a specified amount. There is also a simplified version that doesn't use offsets
     *
     * @param amount The new particle
     * @param offset The offset of the amount(can be positive or negative)
     * @return The previous particle
     *
     * @see ParticleCombiner#setAmount(int)
     */
    public int setAmount(int amount, int offset) {
        if (offset == 0) {
            throw new IllegalArgumentException("offset must not equal to 0");
        }
        int prevAmount = super.setAmount(amount);
        int i = 0;
        for (T object : this.objects) {
            object.setAmount(amount + (offset * i));
            i++;
        }
        return prevAmount;
    }

    /** Sets the amount to use to a new value and returns the previous amount that was used.
     * This applies to all the object(including objects that are way below the hierarchy). The returned
     * value can also be -1 meaning that there are different particle effects at play in the object.
     * The offset param changes the amount per object by a specified amount. There is also a simplified version
     * that doesn't recursively scale down the tree & another that allows specifying the recursion offset<br><br>
     *
     * <b>Note:</b> when the program encounters another combiner. It calls the same method but
     * the amount is added with the iterated offset and the offset remains unchanged
     *
     * @param amount The new particle
     * @param offset The offset of the amount(can be positive or negative)
     *
     * @see ParticleCombiner#setAmount(int)
     * @see ParticleCombiner#setAmount(int, int)
     */
    public void setAmountRecursively(int amount, int offset) {
        if (offset == 0) {
            throw new IllegalArgumentException("offset must not equal to 0");
        }
        int i = -1;
        for (T object : this.objects) {
            i++;
            if (object instanceof ParticleCombiner<?> combiner) {
                combiner.setAmountRecursively(amount + (offset * i), offset);
            }
            object.setAmount(amount + (offset * i));
        }
        this.amount = -1;
    }

    /** Sets the amount to use to a new value and returns the previous amount that was used.
     * This applies to all the object(including objects that are way below the hierarchy). The returned
     * value can also be -1 meaning that there are different particle effects at play in the object.
     * The offset param changes the amount per object by a specified amount. There is also a simplified version
     * that doesn't recursively scale down the tree & another that doesn't accept the recursion offset<br><br>
     *
     * <b>Note:</b> when the program encounters another combiner. It calls the same method but
     * the amount is added with the recursive offset and the recursive offset remains unchanged
     *
     * @param amount The new particle
     * @param offset The offset of the amount(can be positive or negative)
     * @param recursiveOffset The offset of the amount once the program encounters another combiner
     *
     * @see ParticleCombiner#setAmountRecursively(int, int)
     * @see ParticleCombiner#setAmount(int, int)
     */
    public void setAmountRecursively(int amount, int offset, int recursiveOffset) {
        if (offset == 0) {
            throw new IllegalArgumentException("Normal Offset must not equal to 0");
        }
        if (recursiveOffset == 0) {
            throw new IllegalArgumentException("Recursive offset must not equal to 0");
        }
        int i = -1;
        for (T object : this.objects) {
            i++;
            if (object instanceof ParticleCombiner<?> combiner) {
                combiner.setAmountRecursively(amount + (recursiveOffset * i), recursiveOffset);
            }
            object.setAmount(amount + (offset * i));
        }
        this.amount = -1;
    }

    /** Gets the list of particle objects and returns them
     *
     * @return The list of particle objects
     */
    public List<T> getObjects() {
        return this.objects;
    }

    /** Gets the particle objects at that index & returns it
     *
     * @param index the index of the particle object
     * @return The list of particle objects
     */
    public T getObject(int index) {
        return objects.get(index);
    }

    /** Sets the amount of particle objects to use and returns the previous objects that were used.
     *
     * @param objects The particle objects list
     * @return The previous particle objects list
    */
    @SafeVarargs
    public final List<T> setObjects(T... objects) {
        return this.setObjects(Arrays.asList(objects));
    }

    /** Sets the amount of particle objects to use and returns the previous objects that were used.
     *
     * @param objects The particle objects list
     * @return The previous particle objects list
     */
    public final List<T> setObjects(List<T> objects) {
        if (objects.size() <= 1) {
            throw new IllegalArgumentException("There has to be more than 1 object supplied");
        }
        List<T> prevObjects = this.objects;

        // Create a new list to control mutability
        this.objects = new ArrayList<>(objects);
        // Don't use Arrays.asList(): it results in an unmodifiable list, and then appendObject(s) will break.
        this.offsets = IntStream.range(0, this.objects.size()).mapToObj(Vector3f::new).collect(Collectors.toCollection(ArrayList::new));

        for (T object : this.objects) {
            if (this.amount == -1 && this.particleEffect == null) break;
            this.amount = (object.amount != this.amount) ? -1 : this.amount;
            this.particleEffect = (object.particleEffect != this.particleEffect) ? null : this.particleEffect;
        }
        return prevObjects;
    }

    /** Sets the individual object at that index to a different object
     *
     * @param index The index of the particle object to replace at
     * @param newObject The new particle object
     * @return The previous particle object
     */
    public T setObject(int index, T newObject) {
        T prevObject = this.objects.get(index);
        this.objects.set(index, newObject);
        if (this.particleEffect != newObject.particleEffect) {
            this.particleEffect = null;
        }
        if (this.amount != newObject.amount) {
            this.amount = -1;
        }
        return prevObject;
    }

    /** Adds all the objects at the back of the list. All the objects
     * will have an offset of (0,0,0).
     *
     * @param objects The objects to add
     */
    @SafeVarargs
    public final void appendObjects(T... objects) {
        List<T> objectList = Arrays.asList(objects);
        this.objects.addAll(objectList);

        Vector3f[] offsets = new Vector3f[objects.length];
        Arrays.fill(offsets, new Vector3f());
        this.offsets.addAll(Arrays.stream(offsets).toList());

        for (T object : this.objects) {
            if (object.amount != this.amount) this.amount = -1;
            if (object.particleEffect != this.particleEffect) this.particleEffect = null;
        }
    }

    /** Appends a new particle object to the combiner. This is at the back
     *  of the list. Meaning that this object is the last one in the list.
     *  The offset position is (0,0,0) when appending, although you can use
     *  the same method but just supplying the offset
     *
     * @param object The object to add to the list
     *
     * @see ParticleCombiner#appendObject(ParticleObject, Vector3f)
     */
    public void appendObject(T object) {
        this.appendObject(object, new Vector3f());
    }

    /** Appends a new particle object to the combiner. This is at the back
     *  of the list. Meaning that this object is the last one in the list.
     *  The offset position is (0,0,0) when appending, although you can use
     *  the same method but just supplying the offset
     *
     * @param object The object to add to the list
     *
     * @see ParticleCombiner#appendObject(ParticleObject)
     */
    public void appendObject(T object, Vector3f offset) {
        if (object.amount != this.amount) this.amount = -1;
        if (object.particleEffect != this.particleEffect) this.particleEffect = null;
        this.objects.add(object);
        this.offsets.add(offset);
    }

    /** Removes an object from the combiner including its offset. Returns
     * the object that was removed as well as the offset position
     *
     * @param index The index to remove at
     * @return The pair of the object & offset
     */
    public Pair<T, Vector3f> removeObject(int index) {
        return new Pair<>(this.objects.remove(index), this.offsets.remove(index));
    }

    /** Removes an object from the combiner including its offset. Returns
     * the object that was removed as well as the offset position
     *
     * @param object The index to remove at
     * @return The pair of the object & offset
     */
    public Pair<T, Vector3f> removeObject(T object) {
        int index = this.objects.indexOf(object);
        return this.removeObject(index);
    }

    @Override
    public void draw(ServerWorld world, int step, Vector3f drawPos) {
        int index = -1;
        for (T object : this.objects) {
            index++;
            InterceptData<BeforeChildDrawData> interceptData = this.doBeforeChildDraw(world, step, object);
            boolean shouldDraw = interceptData.getMetadata(BeforeChildDrawData.CAN_DRAW_OBJECT, true);
            if (!shouldDraw) {
                continue;
            }
            ParticleObject childObject = interceptData.getMetadata(BeforeChildDrawData.OBJECT_IN_USE, object);
            // Defensive copy before passing to child object
            Vector3f childDrawPos = new Vector3f(drawPos).add(this.offsets.get(index));
            childObject.draw(world, step, childDrawPos);
            this.doAfterChildDraw(world, step);
        }
    }


    public void setBeforeChildDraw(DrawInterceptor<ParticleCombiner<T>, BeforeChildDrawData> beforeChildDraw) {
        this.beforeChildDraw = Optional.ofNullable(beforeChildDraw).orElse(DrawInterceptor.identity());
    }

    public void setAfterChildDraw(DrawInterceptor<ParticleCombiner<T>, AfterChildDrawData> afterChildDraw) {
        this.afterChildDraw = Optional.ofNullable(afterChildDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterChildDraw(ServerWorld world, int step) {
        InterceptData<AfterChildDrawData> interceptData = new InterceptData<>(world, null, step, AfterChildDrawData.class);
        this.afterChildDraw.apply(interceptData, this);
    }

    private InterceptData<BeforeChildDrawData> doBeforeChildDraw(ServerWorld world, int step, T objectInUse) {
        InterceptData<BeforeChildDrawData> interceptData = new InterceptData<>(world, null, step, BeforeChildDrawData.class);
        interceptData.addMetadata(BeforeChildDrawData.OBJECT_IN_USE, objectInUse);
        interceptData.addMetadata(BeforeChildDrawData.CAN_DRAW_OBJECT, true);
        this.beforeChildDraw.apply(interceptData, this);
        return interceptData;
    }
}
