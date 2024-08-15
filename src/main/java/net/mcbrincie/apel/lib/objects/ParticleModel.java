package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import net.mcbrincie.apel.lib.util.models.ModelParserManager;
import net.mcbrincie.apel.lib.util.models.ObjModel;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code ParticleModel} can render 3D Model files (*.obj, *.fbx, *.gltf) as a particle object.  These models inherit
 * everything allowed by {@link ParticleObject}, and may also be scaled along each of the three axes.  The model is
 * drawn in a wireframe fashion, so only the edges are visible.  A single particle effect is used for the entire
 * model.
 * <p>
 * Implementation-specific details:
 * <ul>
 *     <li>{@code amount} will set the number of particles to use on every edge in the model</li>
 * </ul>
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleModel extends ParticleObject<ParticleModel> {

    protected final ObjModel objModel;
    protected Vector3f scale;

    public static DrawContext.Key<ObjModel> objectModelKey(String name) {
        return new DrawContext.Key<>(name) { };
    }

    public static final DrawContext.Key<ObjModel> OBJECT_MODEL = objectModelKey("object_model");

    public static <B extends Builder<B>> Builder<B> builder() {
        return new Builder<>();
    }

    private <B extends Builder<B>> ParticleModel(Builder<B> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
                builder.afterDraw);
        this.objModel = builder.objectModel;
        this.setScale(builder.scale);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param model The particle model object to copy from
    */
    public ParticleModel(ParticleModel model) {
        super(model);
        this.objModel = model.objModel;
        this.scale = model.scale;
    }

    /**
     * Set the scale of this ParticleModel and returns the previous scaling that was used.
     * Negative scaling will invert the corresponding axis.  Zero scaling is not allowed.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param newScale the new scale
     * @return the previously used scale
    */
    public final Vector3f setScale(Vector3f newScale) {
        if (newScale.x == 0 || newScale.y == 0 || newScale.z == 0) {
            throw new IllegalArgumentException("Scale must non-zero");
        }
        Vector3f prevScale = this.scale;
        this.scale = newScale;
        return prevScale;
    }

    /** Gets the scale of the ParticleModel and returns it.
     *
     * @return the scale of the ParticleModel
     */
    public Vector3f getScale() {return this.scale;}

    @Override
    protected void prepareContext(DrawContext drawContext) {
        drawContext.addMetadata(OBJECT_MODEL, this.objModel);
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(this.offset);

        ObjModel objectModel = drawContext.getMetadata(OBJECT_MODEL, this.objModel);
        for (ObjModel.Face face : objectModel.faces()) {
            List<ObjModel.Vertex> vertices = face.vertices();
            List<Vector3f> positions = new ArrayList<>(vertices.size());

            for (ObjModel.Vertex vertex : face.vertices()) {
                // Defensive copies of internal vertices
                positions.add(new Vector3f(vertex.position()).mul(this.scale));
            }

            int step = drawContext.getCurrentStep();
            for (int i = 0; i < positions.size() - 1; i++) {
                renderer.drawLine(this.particleEffect, step, objectDrawPos, positions.get(i), positions.get(i + 1),
                        this.rotation, this.amount);
            }
            // Close the face
            renderer.drawLine(this.particleEffect, step, objectDrawPos, positions.getLast(), positions.getFirst(),
                    this.rotation, this.amount);
        }
    }

    /** This is the particle model object builder used for setting up a new particle model instance.
     * It is designed to be more friendly of how you arrange the parameters. Call {@code .builder()} to initiate
     * the builder, once you supplied the parameters then you can call {@code .build()} to create the instance
     *
     * @param <B> The builder type itself
    */
    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleModel> {
        private static final ModelParserManager MODEL_PARSER_MANAGER = new ModelParserManager();
        protected Vector3f scale = new Vector3f(1);
        protected String filename;
        protected ObjModel objectModel;

        private Builder() {}

        /**
         * Scale the particle model by distinct values per axis.  This method is not cumulative; repeated calls will
         * overwrite values.  To scale uniformly, see {@link #scale(float)}, which shares overwrite behavior with this
         * method.
         *
         * @param scale The scale per axis xyz
         * @return The builder instance
        */
        public B scale(Vector3f scale) {
            this.scale = scale;
            return self();
        }

        /**
         * Scale the model uniformly on all axes.  This method is not cumulative; repeated calls will overwrite values.
         * To scale per-axis, see {@link #scale(Vector3f)}, which shares overwrite behavior with this method.
         *
         * @param scale The scale for all the axis
         * @return The builder instance
        */
        public B scale(float scale) {
            this.scale = new Vector3f(scale);
            return self();
        }

        /**
         * Load a model from the given filename.  This method is not cumulative; repeated calls will overwrite the
         * value.  This should be exclusive with {@link #model(ObjModel)}.
         *
         * @param filename The model path for loading it
         * @return The builder instance
         */
        public B filename(String filename) {
            this.filename = filename;
            return self();
        }

        /**
         * Set the {@code ObjModel} to use.  This method is not cumulative; repeated calls will overwrite the value.
         * This should be exclusive with {@link #filename(String)}.
         */
        public B model(ObjModel objectModel) {
            this.objectModel = objectModel;
            return self();
        }

        @Override
        public ParticleModel build() {
            if (filename == null && objectModel == null) {
                throw new IllegalStateException("Filename or object model must be provided");
            }
            if (objectModel == null) {
                objectModel = MODEL_PARSER_MANAGER.parse(new File(this.filename));
            }
            return new ParticleModel(this);
        }
    }
}
