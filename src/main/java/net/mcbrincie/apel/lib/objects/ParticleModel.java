package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.models.ModelParserManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.joml.Vector3f;
import oshi.util.tuples.Pair;

import java.io.File;
import java.util.HashMap;


/** Particle Model is a Work In Progress(WIP) stage.
 * Feel free to use it, but it is not complete and there are
 * some known issues as well as being unoptimized
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleModel extends ParticleObject<ParticleModel> {
    private String filename;
    private Vector3f scale;
    private File model_file;
    private HashMap<Pair<Vector3f, Vector3f>, ParticleEffect> positions = new HashMap<>();
    private final ModelParserManager modelParserManager = new ModelParserManager();

    public ParticleModel(String filename, Vector3f rotation) {
        this(filename, rotation, new Vector3f(1, 1, 1));
    }

    public ParticleModel(String filename, Vector3f rotation, Vector3f scale) {
        super(null, rotation, new Vector3f(0), 1, DrawInterceptor.identity(), DrawInterceptor.identity());
        this.setScale(scale);
        this.setFilename(filename);
    }

    public ParticleModel(String filename) {
        this(filename, new Vector3f(0, 0, 0));
    }


    public ParticleModel(ParticleModel model) {
        super(model);
        this.scale = model.scale;
        this.filename = model.filename;
        this.positions = model.positions;
        this.model_file = model.model_file;
    }

    public Vector3f setScale(Vector3f newScale) {
        if (newScale.x <= 0 || newScale.y <= 0 || newScale.z <= 0) {
            throw new IllegalArgumentException("Scale must be positive and above 0");
        }
        Vector3f prevScale = this.scale;
        this.scale = newScale;
        return prevScale;
    }

    public String setFilename(String filename) {
        String prevFilename = this.filename;
        this.filename = filename;
        this.model_file = new File(filename);
        this.modelParserManager.parseFile(this.model_file);
        for (ModelParserManager.FaceToken faceToken : this.modelParserManager.drawableFaces) {
            Vector3f prevVertex = faceToken.vertices[0];
            int vertexIndex = 0;
            for (Vector3f vertex : faceToken.vertices) {
                if (vertexIndex == 0) {
                    vertexIndex++;
                    continue;
                } else if (vertexIndex == faceToken.vertices.length - 1) {
                    prevVertex = faceToken.vertices[0];
                }
                this.positions.put(new Pair<>(prevVertex, vertex), ParticleTypes.END_ROD);
                prevVertex = vertex;
                vertexIndex++;
            }
        }
        return prevFilename;
    }

    /*
    public PalateGenerator setPalateGenerator(PalateGenerator palateGenerator) {
        PalateGenerator prevPalateGenerator = this.palateGenerator;
        this.palateGenerator = palateGenerator;
        return prevPalateGenerator;
    }
    */

    // public PalateGenerator getPalateGenerator() {return this.palateGenerator;}

    public String getFilename() {return filename;}

    /** THIS METHOD SHOULD NOT BE USED */
    @Override
    @Deprecated
    public ParticleEffect getParticleEffect() {
        throw new UnsupportedOperationException("ParticleImage doesn't support getting a particle effect.");
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(this.offset);
        for (Pair<Vector3f, Vector3f> vertexPair : this.positions.keySet()) {
            ParticleEffect currParticle = this.positions.get(vertexPair);
            Vector3f vertex1 = new Vector3f(vertexPair.getA()).mul(this.scale);
            Vector3f vertex2 = new Vector3f(vertexPair.getB()).mul(this.scale);
            renderer.drawLine(
                    currParticle, drawContext.getCurrentStep(), objectDrawPos,
                    vertex1, vertex2, this.rotation, 10
            );
        }
    }
}
