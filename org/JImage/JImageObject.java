package org.JImage;

import java.awt.*;

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
}
