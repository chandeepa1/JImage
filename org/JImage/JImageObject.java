package org.JImage;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class JImageObject {

    /**
     * Following are the universal constants for drawing the objects
     */
    public static final Color JIMAGE_OBJECT_COLOR_DEFAULT=Color.RED;

    /**
     * Following constants are the types of objects which sorround the point clicked by the user.
     */
    public static final int JIMAGE_OBJECT_CIRCLE=0;

    /**
     * Following point is the center of the object. This contains its (x,y) coordinates.
     */
    public final JImagePoint point;

    /**
     * The following parameters includes the details about the shape, dimensions of the object generated.
     */
    public int object_type;
    public JImageDimension object_dimensions;
    public Color object_color;

    public JImageObject(JImagePoint point, int object_type, JImageDimension object_dimensions, Color object_color) {
        this.point = point;
        this.object_type = object_type;
        this.object_dimensions = object_dimensions;
        this.object_color = object_color;
    }

    /**
     * This method returns all the properties of this object.
     * @return A Hashmap containing all the properties of this object.
     */
    public Map<String, String> getObjectProperties() {
        Map<String, String> props=new HashMap<>();
        switch (object_type) {
            case JIMAGE_OBJECT_CIRCLE:
                props.put("type", "circle");
                props.put("real_center_x", String.valueOf(point.x));
                props.put("real_center_y", String.valueOf(point.y));
                props.put("radius", String.valueOf(object_dimensions.width/2));

                double enclosed_area=Math.PI*(object_dimensions.width/2)*(object_dimensions.height/2);
                props.put("enclosed_area", String.valueOf(enclosed_area));

                break;
            //Other object types goes here
        }
        return props;
    }

    /**
     * This method returns the minimum x-coordinate where the object is bounded by along the x-axis.
     * @return double value of the x coordinate of the JImagePoint
     */
    public double getXMin() {
        if (object_type==JIMAGE_OBJECT_CIRCLE) {
            double x_min=point.x-(object_dimensions.width/2.0);
            return x_min;
        }
        else {
            //Remove this line after completing all of the code for all types of objects.
            return 0.0;
        }
    }

    /**
     * This method returns the maximum x-coordinate where the object is bounded by along the x-axis.
     * @return double value of the x coordinate of the JImagePoint
     */
    public double getXMax() {
        if (object_type==JIMAGE_OBJECT_CIRCLE) {
            double x_max=point.x+(object_dimensions.width/2.0);
            return x_max;
        }
        else {
            //Remove this line after completing all of the code for all types of objects.
            return 0.0;
        }
    }

    /**
     * This method returns the minimum y-coordinate where the object is bounded by along the y-axis.
     * @return double value of the y coordinate of the JImagePoint
     */
    public double getYMin() {
        if (object_type==JIMAGE_OBJECT_CIRCLE) {
            double y_min=point.y-(object_dimensions.height/2.0);
            return y_min;
        }
        else {
            //Remove this line after completing all of the code for all types of objects.
            return 0.0;
        }
    }

    /**
     * This method returns the maximum y-coordinate where the object is bounded by along the y-axis.
     * @return double value of the y coordinate of the JImagePoint
     */
    public double getYMax() {
        if (object_type==JIMAGE_OBJECT_CIRCLE) {
            double y_max=point.y+(object_dimensions.height/2.0);
            return y_max;
        }
        else {
            //Remove this line after completing all of the code for all types of objects.
            return 0.0;
        }
    }
}
