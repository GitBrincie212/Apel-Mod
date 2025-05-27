package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.image.DustPalateGenerator;
import net.mcbrincie.apel.lib.util.image.PalateGenerator;
import net.mcbrincie.apel.lib.util.interceptor.context.DrawContext;
import net.mcbrincie.apel.lib.util.interceptor.ObjectInterceptor;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;


/** Particle Image is a Work In Progress(WIP) stage.
 * Feel free to use it, but it is not complete and there are
 * some known issues as well as being unoptimized
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleImage extends ParticleObject<ParticleImage> {
    private String filename;
    private BufferedImage image;
    private boolean transparency = false;
    private PalateGenerator palateGenerator = new DustPalateGenerator();
    private int[] rgbArray;

    private HashMap<Vector3f, ParticleEffect> positions;

    public ParticleImage(String filename, Vector3f rotation) {
        super(new ConstantEasingCurve<>(rotation), new ConstantEasingCurve<>(new Vector3f(0)),
                ObjectInterceptor.identity(), ObjectInterceptor.identity()
        );
        this.setFilename(filename);
    }

    public ParticleImage(String filename) {
        this(filename, new Vector3f(0, 0, 0));
    }

    public ParticleImage(Identifier filename, Vector3f rotation) {
        this("id:" + filename.toString(), rotation);
    }

    public ParticleImage(Identifier filename) {
        this("id:" + filename.toString(), new Vector3f(0, 0, 0));
    }

    public ParticleImage(ParticleImage image) {
        super(image);
        this.filename = image.filename;
        this.rgbArray = image.rgbArray;
        this.positions = image.positions;
        this.image = image.image;
        this.palateGenerator = image.palateGenerator;
        this.transparency = image.transparency;
    }

    public String setFilename(String filename) {
        String prevFilename = this.filename;
        this.filename = filename;
        try {
            File file = new File(filename);
            this.image = ImageIO.read(file);
            int width = this.image.getWidth();
            int height = this.image.getHeight();
            this.rgbArray = this.image.getRGB(0, 0, width, height, null, 0, width);
        } catch (IOException e) {
            Apel.LOGGER.error("There was a problem loading the image {}", filename);
        }
        return prevFilename;
    }

    public PalateGenerator setPalateGenerator(PalateGenerator palateGenerator) {
        PalateGenerator prevPalateGenerator = this.palateGenerator;
        this.palateGenerator = palateGenerator;
        return prevPalateGenerator;
    }

    public PalateGenerator getPalateGenerator() {return this.palateGenerator;}

    public String getFilename() {return filename;}

    @Override
    public void display(ApelServerRenderer renderer, DrawContext<?> drawContext, Vector3f actualSize) {
        int width = this.image.getWidth();
        int height = this.image.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgba = this.rgbArray[y * width + x];
                int alpha = (rgba >> 24) & 0xff;
                ParticleEffect particle = this.palateGenerator.apply(rgba, x, y, drawContext.getPosition());
                Vector3f pos = new Vector3f(drawContext.getPosition()).add(x, y, 0).mul(0.01f);
                // this.drawParticle(particle, renderer, step, pos);
            }
        }
    }
}
