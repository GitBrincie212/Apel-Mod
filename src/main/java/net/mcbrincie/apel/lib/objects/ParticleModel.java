package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import net.mcbrincie.apel.lib.util.interceptor.ObjectInterceptor;
import net.mcbrincie.apel.lib.util.models.ModelParserManager;
import net.mcbrincie.apel.lib.util.models.ObjModel;
import net.minecraft.util.Pair;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/** The particle object class that represents a 3D model. It has a path to the 3D model
 * file which is the geometry of the model, it also has scaling which stretches the model
 * in different axis. The model is drawn in a wireframe fashion, which means that only the
 * edges are visible and for now it supports one particle for rendering the model. Textures
 * are coming soon on the next release
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleModel extends ParticleObject<ParticleModel> {
    private static final ModelParserManager MODEL_PARSER_MANAGER = new ModelParserManager();

    private String filename;
    private ObjModel objModel;
    private Vector3f scale;

    public static final DrawContext.Key<List<Pair<Vector3f, Vector3f>>> FACE_VERTICES = DrawContext.vector3fListPairKey(
            "face_vertices"
    );

    public static ParticleModel.Builder<?> builder() {
        return new ParticleModel.Builder<>();
    }

    private ParticleModel(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, new Vector3f(), builder.amount, builder.beforeDraw, builder.afterDraw);
        this.setScale(builder.scale);
        this.setFilename(builder.filename);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param model The particle model object to copy from
    */
    public ParticleModel(ParticleModel model) {
        super(model);
        this.scale = model.scale;
        this.filename = model.filename;
        this.objModel = model.objModel;
    }

    /**
     * Set the scale of this ParticleModel and returns the previous scaling that was used.
     * Negative Scaling flips the model upside down
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param newScale the new scale
     * @return the previously used scale
    */
    public final Vector3f setScale(Vector3f newScale) {
        Vector3f prevScale = this.scale;
        this.scale = newScale;
        return prevScale;
    }

    /**
     * Set the path to the 3D model for this ParticleModel and returns the previous filename that was used.
     * The model also parses the file and spits out the positions of each vertex from the different faces and lines
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param filename the new path to the file (filename)
     * @return the previously used filename
    */
    public final String setFilename(String filename) {
        String prevFilename = this.filename;
        this.filename = filename;
        this.objModel = MODEL_PARSER_MANAGER.parse(new File(this.filename));
        return prevFilename;
    }

    /** Gets the filename of the ParticleModel and returns it.
     *
     * @return the filename of the ParticleModel
    */
    public String getFilename() {return this.filename;}

    /** Gets the scale of the ParticleModel and returns it.
     *
     * @return the scale of the ParticleModel
     */
    public Vector3f getScale() {return this.scale;}

    @Override
    protected void prepareContext(DrawContext drawContext) {
        drawContext.addMetadata(FACE_VERTICES, this.face_vertices);
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(this.offset);

        for (ObjModel.Face face : this.objModel.faces()) {
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
    public static class Builder<B extends ParticleModel.Builder<B>> extends ParticleObject.Builder<B, ParticleModel> {
        protected ObjectInterceptor<ParticleModel> afterDraw;
        protected ObjectInterceptor<ParticleModel> beforeDraw;
        protected Vector3f scale = new Vector3f(1);
        protected String filename;

        /** The scale of the particle model. The provided vector can also have different scaling on different axis
         *
         * @param scale The scale per axis xyz
         * @return The builder instance
        */
        public B scale(Vector3f scale) {
            this.scale = scale;
            return self();
        }

        /** The scale of the particle model. The scaling is the same on all axis
         *
         * @param scale The scale for all the axis
         * @return The builder instance
        */
        public B scale(float scale) {
            this.scale = new Vector3f(scale);
            return self();
        }

        /** The path (filename) for loading the model. Look into the supported file formats
         *
         * @param filename The model path for loading it
         * @return The builder instance
        */
        public B filename(String filename) {
            this.filename = filename;
            return self();
        }

        private Builder() {}

        @Override
        public ParticleModel build() {
            if (this.filename == null) {
                throw new IllegalStateException("Model Path Must Be Specified");
            }
            return new ParticleModel(this);
        }
    }
}
