import java.awt.*;

public class JImageMaths {

    private final JImage image;

    public JImageMaths(JImage image) {
        this.image = image;
    }

    public Point getPointOnOriginal(Point p) {
        JImagePosition curr_position=image.getCurrentPositionOnOriginal();

        int unscaled_width=curr_position.getWidth();
        int unscaled_height=curr_position.getHeight();

        int scaled_width=image.getCurrentImageWidth();
        int scaled_height=image.getCurrentImageHeight();

        int unscaled_x=mapVal(p.x, 0, scaled_width, 0, unscaled_width);
        int unscaled_y=mapVal(p.y, 0, scaled_height, 0, unscaled_height);

        int x_on_original=curr_position.TOP_LEFT_X+unscaled_x;
        int y_on_original=curr_position.TOP_LEFT_Y+unscaled_y;

        return new Point(x_on_original, y_on_original);
    }

    public int getCropXReducer(int x_affinity) {
        return mapVal(x_affinity, 0, image.getOriginalImageWidth(), JImageSettings.ZOOM_SENSITIVITY_MIN, JImageSettings.ZOOM_SENSITIVITY_MAX);
    }

    public int getCropYReducer(int y_affinity) {
        return mapVal(y_affinity, 0, image.getOriginalImageHeight(), JImageSettings.ZOOM_SENSITIVITY_MIN, JImageSettings.ZOOM_SENSITIVITY_MAX);
    }

    /*
     * Private methods
     */
    private int mapVal(int x, int in_min, int in_max, int out_min, int out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
