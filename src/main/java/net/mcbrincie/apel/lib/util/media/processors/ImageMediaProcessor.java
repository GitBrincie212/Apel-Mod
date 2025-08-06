package net.mcbrincie.apel.lib.util.media.processors;

import net.mcbrincie.apel.lib.objects.ParticleMedia;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImageMediaProcessor implements MediaProcessor {
    private final List<ParticleMedia.FrameData> frames;

    public ImageMediaProcessor(Path path) {
        ImageIO.setUseCache(false);

        try (ImageInputStream in = ImageIO.createImageInputStream(Files.newInputStream(path))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (!readers.hasNext()) {
                throw new IOException("No ImageReader Found For: " + path);
            }
            ImageReader reader = readers.next();
            reader.setInput(in, false, true);

            int frameCount = reader.getNumImages(true);

            this.frames = new ArrayList<>(frameCount);

            for (int i = 0; i < frameCount; i++) {
                BufferedImage raw = reader.read(i);
                BufferedImage img = (raw.getType() == BufferedImage.TYPE_INT_ARGB)
                        ? raw
                        : toARGB(raw);

                DataBufferInt db = (DataBufferInt) img.getRaster().getDataBuffer();
                this.frames.add(new ParticleMedia.FrameData(db.getData(), raw.getWidth(), raw.getHeight()));
            }
            reader.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedImage toARGB(BufferedImage src) {
        BufferedImage dest = new BufferedImage(
                src.getWidth(), src.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        dest.getGraphics().drawImage(src, 0, 0, null);
        return dest;
    }

    public ParticleMedia.FrameData process(int currentFrame) {
        if (this.frames.size() == 1) {
            return this.frames.getFirst();
        } else if (currentFrame < this.frames.size() && currentFrame >= 0) {
            return this.frames.get(currentFrame);
        }
        throw new RuntimeException(
                String.format(
                        "Cannot get the #%s frame out of a animated image containing %s frame(s)",
                        currentFrame, this.frames.size()
                )
        );
    }
}
