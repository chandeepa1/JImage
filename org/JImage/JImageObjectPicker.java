package org.JImage;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class JImageObjectPicker {

    private final JImage image;
    private final JImageMaths image_maths;

    public List<JImageObject> objects;

    public JImageObjectPicker(JImage image) {
        this.image = image;

        image_maths=image.getJImageMaths();
        objects=new ArrayList();
    }

    /**
     * The following method should be called inside the paintComponent(Graphics g) method of component which displays the image.
     * This is the method where all the selected points are painted on the displayed image.
     * @param g The Graphics object passed to the paintComponent method of the component which displays the image.
     */
    public void paintComponent(Graphics g) {
        /*
         * TODO: Complete this method
         */

        Graphics2D g2d=(Graphics2D)g;

        for (JImageObject object : objects) {
            JImagePosition image_curr_position=image.getCurrentPositionOnOriginal();

            if (object.object_type==JImageObject.JIMAGE_OBJECT_CIRCLE) {
                int object_radius=object.object_dimensions.width/2;

                int center_x=object.point.x+object_radius;
                int center_y=object.point.y+object_radius;

                if (center_x>=image_curr_position.TOP_LEFT_X && center_x<=image_curr_position.TOP_RIGHT_X && center_y>=image_curr_position.TOP_LEFT_Y && center_y<=image_curr_position.BOTTOM_LEFT_Y) {
                    int unscaled_start_x=object.point.x-image_curr_position.TOP_LEFT_X;
                    int unscaled_start_y=object.point.y-image_curr_position.TOP_LEFT_Y;

                    int current_image_start_x=image_maths.mapVal(unscaled_start_x, 0, image_curr_position.getWidth(), 0, image.getCurrentImageWidth());
                    int current_image_start_y=image_maths.mapVal(unscaled_start_y, 0, image_curr_position.getHeight(), 0, image.getCurrentImageHeight());

                    int scaled_side=image_maths.mapVal(object.object_dimensions.width, 0, image.getOriginalImageWidth(), 0, image.getCurrentImageWidth());

                    g2d.setColor(object.object_color);
                    g2d.draw(new Ellipse2D.Double(current_image_start_x, current_image_start_y, scaled_side, scaled_side));
                }
            }
        }
    }

    private void selectSinglePoint(Point p, int radius) {
        Point p_objstart_point=new Point(p.x-radius,p.y-radius);
        Point p_objstart_on_original=image_maths.getPointOnOriginal(p_objstart_point);

        int obj_diameter=2*image_maths.mapVal(radius, 0, image.getCurrentImageWidth(), 0, image.getOriginalImageWidth());
        Dimension obj_dimensions=new Dimension(obj_diameter, obj_diameter);

        JImageObject object_new=new JImageObject(p_objstart_on_original, JImageObject.JIMAGE_OBJECT_CIRCLE, obj_dimensions, JImageObject.JIMAGE_OBJECT_COLOR_DEFAULT);
        objects.add(object_new);
    }

    /**
     * This method draws a single circle shaped point using the Graphics object provided
     * @param p The point at which the object is selected on your component of the image(according to its dimensions)
     * @param radius The radius of the circle on your component of the image(according to its dimensions)
     * @param g The graphics object of the component which displays the image
     */
    public void drawSinglePoint(Point p, int radius, Graphics g) {
        selectSinglePoint(p, radius);
        paintComponent(g);
    }
}
