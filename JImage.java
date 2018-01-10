import java.awt.image.BufferedImage;

public class JImage {

    private final BufferedImage image;
    private final BufferedImage image_proc;

    public JImage(BufferedImage image) {
        this.image = image;

        image_proc = image;
    }

    public BufferedImage getCurrentImage() {
        return image_proc;
    }

    public BufferedImage getOriginalImage() {
        return image;
    }
}
