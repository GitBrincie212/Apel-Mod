package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.models.ObjParser;
import net.minecraft.particle.ParticleEffect;
import org.joml.Vector3f;

import java.io.File;
import java.util.HashMap;


/** Particle Model is a Work In Progress(WIP) stage.
 * Feel free to use it, but it is not complete and there are
 * some known issues as well as being unoptimized
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleModel extends ParticleObject<ParticleModel> {
    private String filename;
    private File model_file;
    private HashMap<Vector3f, ParticleEffect> positions;
    private final ObjParser objParser = new ObjParser();

    public ParticleModel(String filename, Vector3f rotation) {
        super(null, rotation, new Vector3f(0), 1, DrawInterceptor.identity(), DrawInterceptor.identity());
        this.setFilename(filename);
    }

    public ParticleModel(String filename) {
        this(filename, new Vector3f(0, 0, 0));
    }


    public ParticleModel(ParticleModel model) {
        super(model);
        this.filename = model.filename;
        this.positions = model.positions;
        this.model_file = model.model_file;
    }

    public String setFilename(String filename) {
        String prevFilename = this.filename;
        this.filename = filename;
        this.model_file = new File(filename);
        this.objParser.parseObjFile(model_file);
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

    }
}
