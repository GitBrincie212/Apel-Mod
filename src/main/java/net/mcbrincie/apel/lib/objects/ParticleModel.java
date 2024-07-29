package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.models.ObjParser;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
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
    private final ObjParser objParser = new ObjParser();

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
        this.objParser.parseObjFile(this.model_file);
        for (ObjParser.FaceToken faceToken : this.objParser.faceTokens) {
            Vector3f prevVertex = faceToken.vertices[0];
            int index = 0;
            for (Vector3f vertex : faceToken.vertices) {
                if (index == 0) {
                    index++;
                    continue;
                } else if (index == faceToken.vertices.length - 1) {
                    prevVertex = faceToken.vertices[0];
                }
                this.positions.put(new Pair<>(prevVertex, vertex), ParticleTypes.END_ROD);
                prevVertex = vertex;
                index++;
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
        Quaternionfc quaternion = new Quaternionf().rotateZ(this.rotation.z).rotateY(this.rotation.y).rotateX(this.rotation.x);
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(this.offset);
        for (Pair<Vector3f, Vector3f> vertexPair : positions.keySet()) {
            ParticleEffect currParticle = positions.get(vertexPair);
            Vector3f vertex1 = new Vector3f(vertexPair.getA()).mul(this.scale);
            Vector3f vertex2 = new Vector3f(vertexPair.getB()).mul(this.scale);
            vertex1 = this.rigidTransformation(vertex1, quaternion, objectDrawPos);
            vertex2 = this.rigidTransformation(vertex2, quaternion, objectDrawPos);
            renderer.drawLine(
                    currParticle, drawContext.getCurrentStep(),
                    vertex1, vertex2, 10
            );
        }
    }
}
