package org.JImage;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class JImageObjectPicker {

    private final JImage image;
    private final Color object_select_color;
    private final JImageMaths image_maths;

    private List<JImageObject> objects;

    public JImageObjectPicker(JImage image, Color object_select_color) {
        this.image = image;
        this.object_select_color = object_select_color;

        image_maths=image.getJImageMaths();
        objects=new ArrayList();
    }

    /**
     * The following method should be called inside the paintComponent(Graphics g) method of component which displays the image.
     * This is the method where all the selected points are painted on the displayed image.
     */
    public void paintComponent(Graphics g) {
        /*
         * TODO: Complete this method
         */

        Graphics2D g2d=(Graphics2D)g;

        for (JImageObject object : objects) {
            JImagePosition image_curr_position = image.getCurrentPositionOnOriginal();

            if (object.object_type == JImageObject.JIMAGE_OBJECT_ELLIPSE) {
                if (object.point.x >= image_curr_position.TOP_LEFT_X && object.point.x <= image_curr_position.TOP_RIGHT_X && object.point.y >= image_curr_position.TOP_LEFT_Y && object.point.y <= image_curr_position.BOTTOM_LEFT_Y) {
                    double unscaled_center_x = object.point.x - image_curr_position.TOP_LEFT_X;
                    double unscaled_center_y = object.point.y - image_curr_position.TOP_LEFT_Y;

                    double current_image_center_x = image_maths.mapVal(unscaled_center_x, 0.0, image_curr_position.getWidth(), 0.0, image.getCurrentImageWidth());
                    double current_image_center_y = image_maths.mapVal(unscaled_center_y, 0.0, image_curr_position.getHeight(), 0.0, image.getCurrentImageHeight());

                    /**
                     * This scaling does mean scaling just in tho given range of values. It is just a mapping, if the radius has been 'r' in the original image, how much will it be when it is zoomed
                     * to the current range.
                     */
                    double object_radius_width_for_xy = object.object_dimensions.width / 2.0;
                    double object_radius_height_for_xy = object.object_dimensions.height / 2.0;
                    double scaled_object_radius_width_for_xy = image_maths.mapValInverse(object_radius_width_for_xy, 0, image.getOriginalImageWidth(), 0, image_curr_position.getWidth());
                    double scaled_object_radius_height_for_xy = image_maths.mapValInverse(object_radius_height_for_xy, 0, image.getOriginalImageHeight(), 0, image_curr_position.getHeight());

                    double current_image_start_x = current_image_center_x - scaled_object_radius_width_for_xy;
                    double current_image_start_y = current_image_center_y - scaled_object_radius_height_for_xy;

                    double zoomed_scaled_width = object.object_dimensions.width;
                    double current_scaled_width = image_maths.mapValInverse(zoomed_scaled_width, 0.0, (double) image.getOriginalImageWidth(), 0.0, (double) image_curr_position.getWidth());

                    double zoomed_scaled_height = object.object_dimensions.height;
                    double current_scaled_height = image_maths.mapValInverse(zoomed_scaled_height, 0.0, (double) image.getOriginalImageHeight(), 0.0, (double) image_curr_position.getHeight());

                    if (object.object_selected) {
                        g2d.setColor(object_select_color);
                    }
                    else {
                        g2d.setColor(object.object_color);
                    }
                    g2d.draw(new Ellipse2D.Double(current_image_start_x, current_image_start_y, current_scaled_width, current_scaled_height));
                }
            }
        }
    }

    private void selectSinglePoint(JImagePoint p, int radius_x, Color obj_color) {
        JImagePoint p_on_original=image_maths.getJImagePointOnOriginal(p);

        int radius_y=image_maths.mapVal(radius_x, 0, image.getCurrentImageWidth(), 0, image.getCurrentImageHeight());

        /**
         * Here when drawing a circle, there are no two radii. But here we calculate 2 radii and add that to the dimensions with the intention of
         * calculating the starting point of the circle along x and y coordinates separately.
         * Otherwise, if this is to be calculated along only width or height, the starting point of the object along the axis which we've not calculated the point,
         * will be incorrect.
         * Please check the contents of the paintComponent method for understanding.
         */
        double obj_diameter_width=2.0*image_maths.mapVal((double)radius_x, 0.0, (double)image.getCurrentImageWidth(), 0.0, (double)image.getOriginalImageWidth());
        double obj_diameter_height=2.0*image_maths.mapVal((double)radius_y, 0.0, (double)image.getCurrentImageHeight(), 0.0, (double)image.getOriginalImageHeight());
        JImageDimension obj_dimensions=new JImageDimension(obj_diameter_width, obj_diameter_height);

        JImageObject object_new=new JImageObject(p_on_original, JImageObject.JIMAGE_OBJECT_ELLIPSE, obj_dimensions, obj_color);
        objects.add(object_new);
    }

    /**
     * This method draws a single circle shaped point using the Graphics object provided.
     * This method needs the user to call repaint() on the container of the image
     * @param p The point at which the object is selected on your component of the image(according to its dimensions)
     * @param radius The radius of the circle on your component of the image(according to its dimensions)
*      @param obj_color The color of the ellipse that should be drawn
     */
    public void drawSinglePoint(JImagePoint p, int radius, Color obj_color) {
        selectSinglePoint(p, radius, obj_color);
    }

    /**
     * Methods regarding the currently selected points
     */

    /**
     * Selects the object specified by the index given
     * This method needs the user to call repaint() on the container of the image
     * @param index Index of the object to be chosen.
     */
    public void selectObject(int index) {
        objects.get(index).object_selected=true;
    }

    /**
     * Deselects the object specified by the index given
     * This method needs the user to call repaint() on the container of the image
     * @param index Index of the object to be chosen.
     */
    public void deselectObject(int index) {
        objects.get(index).object_selected=false;
    }

    public void deselectAll() {
        for (int i=0;i<objects.size();i++) {
            objects.get(i).object_selected=false;
        }
    }

    /**
     * Getter methods
     */

    /**
     * This returns the JImageObject at the specified index
     * @param index Index of the JImageObject to return
     * @return JImageObject at the given position
     */

    public JImageObject getObjectAt(int index) {
        return objects.get(index);
    }

    public int getObjectCount() {
        return objects.size();
    }

    public List<JImageObject> getSelectedObjects() {
        return objects;
    }

    /**
     * Setter methods
     */

    /**
     * Sets the JImageObject at the given index
     * @param index The index of the JimageObject to set
     * @param object JImageObject to set.
     */
    public void setObjectAt(int index, JImageObject object) {
        objects.set(index, object);
    }

    /**
     * Removes an selected object.
     * This method needs the user to call repaint() on the container of the image
     * @param index Index of the object in the array
     */
    public void removeObject(int index) {
        objects.remove(index);
    }

    /**
     * Removes all of the selected objects.
     * This method needs the user to call repaint() on the container of the image
     */
    public void resetAllSelections() {
        objects.clear();
    }
}
