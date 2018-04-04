package com.axiohelix.oist.JImage;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class JImageObject {

    /**
     * Following are the universal constants for drawing the objects
     */
    public static final Color JIMAGE_OBJECT_COLOR_DEFAULT = Color.RED;

    /**
     * Following constants are the types of objects which sorround the point clicked by the user.
     * Numbers between 0 and 1000 are left off to declare other desired types
     */
    public static final int JIMAGE_OBJECT_ELLIPSE = 0;
    public static final int JIMAGE_OBJECT_SQUARE = 1;
    public static final int JIMAGE_OBJECT_DETECT = 1000;

    /**
     * Following constants defines the properties of the objects
     */

    public static final String PROPERTY_ALL_TYPE = "type";
    public static final String PROPERTY_ALL_X = "real_center_x";
    public static final String PROPERTY_ALL_Y = "real_center_y";
    public static final String PROPERTY_ALL_ENCLOSED_AREA = "enclosed_area";

    public static final String PROPERTY_ELLIPSE_RADIUS_X = "radius_horizontal";
    public static final String PROPERTY_ELLIPSE_RADIUS_Y = "radius_vertical";

    public static final String PROPERTY_SQUARE_LENGTH = "length";

    // This is a special case where ALL_X and ALL_Y properties are overriden by SEED_X and SEED_Y
    public static final String PROPERTY_AUTO_DETECT_SEED_X = "real_seed_point_x";
    public static final String PROPERTY_AUTO_DETECT_SEED_Y = "real_seed_point_y";
    public static final String CUSTOM_PROPERTY_AUTO_DETECT_INTELLI_OBJ = "intelli_object";
    public static final String CUSTOM_PROPERTY_AUTO_DETECT_EDGE_OBJECT_RATIO = "edge_object_color_ratio";

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
    public boolean object_selected = false;
    private Map<String, Object> object_custom_props;

    public JImageObject(JImagePoint point, int object_type, JImageDimension object_dimensions, Color object_color) {
        this.point = point;
        this.object_type = object_type;
        this.object_dimensions = object_dimensions;
        this.object_color = object_color;

        this.object_custom_props = new HashMap<>();
    }

    /**
     * This method returns all the properties of this object.
     *
     * @return A Hashmap containing all the properties of this object.
     */
    public Map<String, Object> getObjectProperties() {
        Map<String, Object> props = new HashMap<>();
        switch (object_type) {
            case JIMAGE_OBJECT_ELLIPSE:
                props.put(PROPERTY_ALL_TYPE, "ellipse");
                props.put(PROPERTY_ALL_X, String.valueOf(point.x));
                props.put(PROPERTY_ALL_Y, String.valueOf(point.y));
                props.put(PROPERTY_ELLIPSE_RADIUS_X, String.valueOf(object_dimensions.width / 2));
                props.put(PROPERTY_ELLIPSE_RADIUS_Y, String.valueOf(object_dimensions.height / 2));
                props.putAll(object_custom_props);

                double ellipse_enclosed_area = Math.PI * (object_dimensions.width / 2) * (object_dimensions.height / 2);
                props.put(PROPERTY_ALL_ENCLOSED_AREA, String.valueOf(ellipse_enclosed_area));

                break;
            case JIMAGE_OBJECT_SQUARE:
                props.put(PROPERTY_ALL_TYPE, "square");
                props.put(PROPERTY_ALL_X, String.valueOf(point.x));
                props.put(PROPERTY_ALL_Y, String.valueOf(point.y));
                props.put(PROPERTY_SQUARE_LENGTH, String.valueOf(object_dimensions.width));
                props.putAll(object_custom_props);

                double square_enclosed_area = object_dimensions.width * object_dimensions.height;
                props.put(PROPERTY_ALL_ENCLOSED_AREA, String.valueOf(square_enclosed_area));

                break;
            //Other object types goes here
            case JIMAGE_OBJECT_DETECT:
                props.put(PROPERTY_ALL_TYPE, "detected_object");
                props.put(PROPERTY_AUTO_DETECT_SEED_X, String.valueOf(point.x));
                props.put(PROPERTY_AUTO_DETECT_SEED_Y, String.valueOf(point.y));

                //TODO: Add dimensions, area and other related properties for a detected object here

                props.putAll(object_custom_props);

                break;
        }
        return props;
    }

    /**
     * This method returns the minimum x-coordinate where the object is bounded by along the x-axis.
     *
     * @return double value of the x coordinate of the JImagePoint
     */
    public double getXMin() {
        double x_min;
        switch (object_type) {
            case JIMAGE_OBJECT_ELLIPSE:
                x_min = point.x - (object_dimensions.width / 2.0);

                break;
            case JIMAGE_OBJECT_SQUARE:
                x_min = point.x - (object_dimensions.width / 2.0);

                break;
            default:
                x_min = 0.0;
        }

        return x_min;
    }

    /**
     * This method returns the maximum x-coordinate where the object is bounded by along the x-axis.
     *
     * @return double value of the x coordinate of the JImagePoint
     */
    public double getXMax() {
        double x_max;
        switch (object_type) {
            case JIMAGE_OBJECT_ELLIPSE:
                x_max = point.x + (object_dimensions.width / 2.0);

                break;
            case JIMAGE_OBJECT_SQUARE:
                x_max = point.x + (object_dimensions.width / 2.0);

                break;
            default:
                x_max = 0.0;
        }

        return x_max;
    }

    /**
     * This method returns the minimum y-coordinate where the object is bounded by along the y-axis.
     *
     * @return double value of the y coordinate of the JImagePoint
     */
    public double getYMin() {
        double y_min;
        switch (object_type) {
            case JIMAGE_OBJECT_ELLIPSE:
                y_min = point.y - (object_dimensions.height / 2.0);

                break;
            case JIMAGE_OBJECT_SQUARE:
                y_min = point.y - (object_dimensions.height / 2.0);

                break;
            default:
                y_min = 0.0;
        }

        return y_min;
    }

    /**
     * This method returns the maximum y-coordinate where the object is bounded by along the y-axis.
     *
     * @return double value of the y coordinate of the JImagePoint
     */
    public double getYMax() {
        double y_max;
        switch (object_type) {
            case JIMAGE_OBJECT_ELLIPSE:
                y_max = point.y + (object_dimensions.height / 2.0);

                break;
            case JIMAGE_OBJECT_SQUARE:
                y_max = point.y + (object_dimensions.height / 2.0);

                break;
            default:
                y_max = 0.0;
        }

        return y_max;
    }

    /**
     * Sets a  property for an object.
     *
     * @param key   The identifier of the value to be added
     * @param value The value itself
     */
    public void setProperty(String key, Object value) {
        if (object_custom_props.containsKey(key)) {
            object_custom_props.put(key, value);
        } else {
            switch (key) {
                case PROPERTY_ALL_TYPE:
                    //This property cannot be changed
                    break;
                case PROPERTY_ALL_X:
                    point.x = Double.parseDouble(value.toString());
                    break;
                case PROPERTY_ALL_Y:
                    point.y = Double.parseDouble(value.toString());
                    break;
                case PROPERTY_ELLIPSE_RADIUS_X:
                    object_dimensions.width = Double.parseDouble(value.toString()) * 2.0;
                    break;
                case PROPERTY_ELLIPSE_RADIUS_Y:
                    object_dimensions.height = Double.parseDouble(value.toString()) * 2.0;
                    break;
                case PROPERTY_SQUARE_LENGTH:
                    object_dimensions.width = Double.parseDouble(value.toString());
                    object_dimensions.height = Double.parseDouble(value.toString());
                    break;
                // Everything else is set as a custom property
                default:
                    object_custom_props.put(key, value);
            }
        }
    }
}
