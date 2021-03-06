package com.axiohelix.oist.JImage;

import java.awt.*;

public class JImageMaths {

    private final JImage image;

    public JImageMaths(JImage image) {
        this.image = image;
    }

    public Point getPointOnOriginal(Point p) {
        int imageMode = image.getImageMode();
        if (imageMode == JImage.MODE_CONTAINER_SIZED_IMAGE) {
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
        else if (imageMode == JImage.MODE_IMAGE_SIZED_CONTAINER) {
            int origX = mapVal(p.x, 0, image.getCurrentImageWidth(), 0, image.getOriginalImageWidth());
            int origY = mapVal(p.y, 0, image.getCurrentImageHeight(), 0, image.getOriginalImageHeight());

            return new Point(origX, origY);
        }
        else {
            return new Point(0, 0);
        }
    }

    public JImagePoint getJImagePointOnOriginal(JImagePoint p) {
        int imageMode = image.getImageMode();
        if (imageMode == JImage.MODE_CONTAINER_SIZED_IMAGE) {
            JImagePosition curr_position=image.getCurrentPositionOnOriginal();

            double unscaled_width=curr_position.getWidth();
            double unscaled_height=curr_position.getHeight();

            double scaled_width=image.getCurrentImageWidth();
            double scaled_height=image.getCurrentImageHeight();

            double unscaled_x=mapVal(p.x, 0.0, scaled_width, 0.0, unscaled_width);
            double unscaled_y=mapVal(p.y, 0.0, scaled_height, 0.0, unscaled_height);

            double x_on_original=curr_position.TOP_LEFT_X+unscaled_x;
            double y_on_original=curr_position.TOP_LEFT_Y+unscaled_y;

            return new JImagePoint(x_on_original, y_on_original);
        }
        else if (imageMode == JImage.MODE_IMAGE_SIZED_CONTAINER) {
            double origX = mapVal(p.x, 0, image.getCurrentImageWidth(), 0, image.getOriginalImageWidth());
            double origY = mapVal(p.y, 0, image.getCurrentImageHeight(), 0, image.getOriginalImageHeight());

            return new JImagePoint(origX, origY);
        }
        else {
            return new JImagePoint(0, 0);
        }
    }

    public Point getDragOnOriginal(Point p) {
        JImagePosition curr_position=image.getCurrentPositionOnOriginal();

        int unscaled_width=curr_position.getWidth();
        int unscaled_height=curr_position.getHeight();

        int scaled_width=image.getCurrentImageWidth();
        int scaled_height=image.getCurrentImageHeight();

        int unscaled_x=mapVal(p.x, 0, scaled_width, 0, unscaled_width);
        int unscaled_y=mapVal(p.y, 0, scaled_height, 0, unscaled_height);

        return new Point(unscaled_x, unscaled_y);
    }

    public int getCropXReducer(int x_affinity) {
        int image_mode = image.getImageMode();
        JImageSettings image_settings=image.getJImageSettings();

        if (image_mode == JImage.MODE_CONTAINER_SIZED_IMAGE) {
            return mapVal(x_affinity, 0, image.getOriginalImageWidth(), image_settings.ZOOM_SENSITIVITY_MIN, image_settings.ZOOM_SENSITIVITY_MAX);
        }
        else if (image_mode == JImage.MODE_IMAGE_SIZED_CONTAINER) {
            return mapVal(x_affinity, 0, image.getCurrentImageWidth(), image_settings.ZOOM_SENSITIVITY_MIN, image_settings.ZOOM_SENSITIVITY_MAX);
        }
        else {
            return 0;
        }
    }

    public int getCropYReducer(int y_affinity) {
        int image_mode = image.getImageMode();
        JImageSettings image_settings=image.getJImageSettings();

        if (image_mode == JImage.MODE_CONTAINER_SIZED_IMAGE) {
            return mapVal(y_affinity, 0, image.getOriginalImageHeight(), image_settings.ZOOM_SENSITIVITY_MIN, image_settings.ZOOM_SENSITIVITY_MAX);
        }
        else if (image_mode == JImage.MODE_IMAGE_SIZED_CONTAINER) {
            return mapVal(y_affinity, 0, image.getCurrentImageHeight(), image_settings.ZOOM_SENSITIVITY_MIN, image_settings.ZOOM_SENSITIVITY_MAX);
        }
        else {
            return 0;
        }
    }

    public int geDragXReduced(int drag_x) {
        if (drag_x==0) {
            return 0;
        }

        JImageSettings image_settings=image.getJImageSettings();
        int pan_sensitivity_min=image_settings.PAN_SENSITIVITY_MIN*drag_x/Math.abs(drag_x);
        int pan_sensitivity_max=image_settings.PAN_SENSITIVITY_MAX*drag_x/Math.abs(drag_x);

        return mapVal(drag_x, 0, image.getOriginalImageWidth(), pan_sensitivity_min, pan_sensitivity_max);
    }

    public int getDragYReduced(int drag_y) {
        if (drag_y==0) {
            return 0;
        }

        JImageSettings image_settings=image.getJImageSettings();
        int pan_sensitivity_min=image_settings.PAN_SENSITIVITY_MIN*drag_y/Math.abs(drag_y);
        int pan_sensitivity_max=image_settings.PAN_SENSITIVITY_MAX*drag_y/Math.abs(drag_y);

        return mapVal(drag_y, 0, image.getOriginalImageHeight(), pan_sensitivity_min, pan_sensitivity_max);
    }

    /*
     * General methods
     */
    public int mapVal(int x, int in_min, int in_max, int out_min, int out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public double mapVal(double x, double in_min, double in_max, double out_min, double out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public double mapValInverse(double x, double in_min, double in_max, double out_min, double out_max) {
        return (x - in_min) * (in_max - in_min) / (out_max - out_min) + out_min;
    }
}
