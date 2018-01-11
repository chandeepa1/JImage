package org.JImage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class JImage {

    public static final int ZOOM_TYPE_ZOOM_IN=1;
    public static final int ZOOM_TYPE_ZOOM_OUT=-1;

    private final BufferedImage image;
    private BufferedImage image_proc;
    private final JImagePosition image_position;
    private final JImageMaths image_maths;
    private final JImageSettings image_settings;

    public JImage(BufferedImage image) {
        this.image = image;

        image_proc = image;
        image_position=new JImagePosition(0, 0, image.getWidth(), image.getHeight());

        image_maths=new JImageMaths(this);
        image_settings=new JImageSettings();
    }

    /*
     * Current Image Methods
     */
    public BufferedImage getCurrentImage() {
        return image_proc;
    }

    public int getCurrentImageWidth() {
        return image_proc.getWidth();
    }

    public int getCurrentImageHeight() {
        return image_proc.getHeight();
    }

    public JImagePosition getCurrentPositionOnOriginal() {
        return image_position;
    }

    public void setCurrentScaled(int width, int height, int SCALE_METHOD) {
        Image obj_scaled=image_proc.getScaledInstance(width, height, SCALE_METHOD);

        BufferedImage image_scaled=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics_scaled=image_scaled.createGraphics();
        graphics_scaled.drawImage(obj_scaled,0,0,null);
        graphics_scaled.dispose();

        image_proc=image_scaled;
    }

    public void cropCurrentImage(Point p_on_curr, int zoomType) throws Exception {
        Point p_on_original=image_maths.getPointOnOriginal(p_on_curr);

        int x_affinity_left=p_on_original.x-image_position.TOP_LEFT_X;
        int x_affinity_right=image_position.TOP_RIGHT_X-p_on_original.x;

        int y_affinity_top=p_on_original.y-image_position.TOP_LEFT_Y;
        int y_affinity_bottom=image_position.BOTTOM_LEFT_Y-p_on_original.y;

        int x_left_pixels_reduced=image_settings.ZOOM_SENSITIVITY*image_maths.getCropXReducer(x_affinity_left)*zoomType;
        int x_right_pixels_reduced=image_settings.ZOOM_SENSITIVITY*image_maths.getCropXReducer(x_affinity_right)*zoomType;

        int y_top_pixels_reduced=image_settings.ZOOM_SENSITIVITY*image_maths.getCropYReducer(y_affinity_top)*zoomType;
        int y_bottom_pixels_reduced=image_settings.ZOOM_SENSITIVITY*image_maths.getCropYReducer(y_affinity_bottom)*zoomType;

        int new_top_left_x=image_position.TOP_LEFT_X+x_left_pixels_reduced;
        int new_top_right_x=image_position.TOP_RIGHT_X-x_right_pixels_reduced;
        int new_top_left_y=image_position.TOP_LEFT_Y+y_top_pixels_reduced;
        int new_bottom_left_y=image_position.BOTTOM_LEFT_Y-y_bottom_pixels_reduced;

        if ((new_top_right_x-new_top_left_x<=0) || (new_bottom_left_y-new_top_left_y<=0)) {
            throw new Exception("Cannot be zoomed further");
        }

        if (new_top_left_x<0) {
            new_top_left_x=0;
        }

        if (new_top_right_x>image.getWidth()) {
            new_top_right_x=image.getWidth();
        }

        if (new_top_left_y<0) {
            new_top_left_y=0;
        }

        if (new_bottom_left_y>image.getHeight()) {
            new_bottom_left_y=image.getHeight();
        }

        image_position.setParams(new_top_left_x, new_top_left_y, new_top_right_x, new_bottom_left_y);

        image_proc=image.getSubimage(new_top_left_x, new_top_left_y, image_position.getWidth(), image_position.getHeight());
    }

    public void cropZoomCurrentImage(Point p, int zoomType, int scaleWidth, int scaleHeight, int scaleMethod) throws Exception {
        cropCurrentImage(p, zoomType);
        setCurrentScaled(scaleWidth, scaleHeight, scaleMethod);
    }

    /*
     * Setter methods for org.JImage.JImageSettings
     */

    public void setZoomSensitivity(int zoomSensitivity) {
        image_settings.setZoomSensitivity(zoomSensitivity);
    }

    /*
     * Original Image Methods
     */
    public BufferedImage getOriginalImage() {
        return image;
    }

    public int getOriginalImageWidth() {
        return image.getWidth();
    }

    public int getOriginalImageHeight() {
        return image.getHeight();
    }
}
