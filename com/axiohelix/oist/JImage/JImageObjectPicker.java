package com.axiohelix.oist.JImage;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JImageObjectPicker {

    public static final int SEARCH_MODE_AREA = 0;
    public static final int SEARCH_MODE_POINT = 1;

    public static final List<String> JIMAGEOBJECT_HIDDEN_PROPERTIES = new ArrayList<String>() {{
        add(JImageObject.CUSTOM_HIDDEN_PROPERTY_AUTO_DETECT_EDGES_ON_ORIGINAL);
        add(JImageObject.CUSTOM_HIDDEN_PROPERTY_AUTO_DETECT_EDGES_LAST_REPAINT);
        add(JImageObject.CUSTOM_HIDDEN_PROPERTY_AUTO_DETECT_INTELLI_OBJ);
        add(JImageObject.CUSTOM_HIDDEN_PROPERTY_IMAGE_DIMENSIONS);
    }};

    private JImage image;
    private final Color object_select_color;
    private JImageMaths image_maths;

    private List<JImageObject> objects;

    public JImageObjectPicker(JImage image, Color object_select_color) {
        this.image = image;
        this.object_select_color = object_select_color;

        image_maths = image.getJImageMaths();
        objects = new ArrayList();
    }

    /**
     * The following method should be called inside the paintComponent(Graphics g) method of component which displays the image.
     * This is the method where all the selected points are painted on the displayed image.
     */
    public void paintComponent(Graphics g) {
        /*
         * TODO: Complete this method
         */

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));

        int imageMode = image.getImageMode();

        for (JImageObject object : objects) {
            JImagePosition image_curr_position = image.getCurrentPositionOnOriginal();

            if (object.object_type == JImageObject.JIMAGE_OBJECT_ELLIPSE) {
                if (imageMode == JImage.MODE_CONTAINER_SIZED_IMAGE) {
                    if (object.point.x >= image_curr_position.TOP_LEFT_X && object.point.x <= image_curr_position.TOP_RIGHT_X && object.point.y >= image_curr_position.TOP_LEFT_Y && object.point.y <= image_curr_position.BOTTOM_LEFT_Y) {
                        double unscaled_center_x = object.point.x - image_curr_position.TOP_LEFT_X;
                        double unscaled_center_y = object.point.y - image_curr_position.TOP_LEFT_Y;

                        double current_image_center_x = image_maths.mapVal(unscaled_center_x, 0.0, image_curr_position.getWidth(), 0.0, image.getCurrentImageWidth());
                        double current_image_center_y = image_maths.mapVal(unscaled_center_y, 0.0, image_curr_position.getHeight(), 0.0, image.getCurrentImageHeight());

                        /**
                         * This scaling does mean scaling just in tho given range of values. It is just a mapping, if the radius has been 'r' in the original image, how much will it be when it is zoomed
                         * to the current range.
                         */
                        double object_radius_for_xy = object.object_dimensions.width / 2.0;
                        double scaled_object_radius_for_xy = image_maths.mapValInverse(object_radius_for_xy, 0, image.getOriginalImageWidth(), 0, image_curr_position.getWidth());

                        double current_image_start_x = current_image_center_x - scaled_object_radius_for_xy;
                        double current_image_start_y = current_image_center_y - scaled_object_radius_for_xy;

                        double zoomed_scaled_radius = object.object_dimensions.width;
                        double current_scaled_radius = image_maths.mapValInverse(zoomed_scaled_radius, 0.0, (double) image.getOriginalImageWidth(), 0.0, (double) image_curr_position.getWidth());

                        if (object.object_selected) {
                            g2d.setColor(object_select_color);
                        } else {
                            g2d.setColor(object.object_color);
                        }
                        g2d.draw(new Ellipse2D.Double(current_image_start_x, current_image_start_y, current_scaled_radius, current_scaled_radius));
                    }
                }
                else if (imageMode == JImage.MODE_IMAGE_SIZED_CONTAINER) {
                    double unscaled_center_x = object.point.x;
                    double unscaled_center_y = object.point.y;

                    double current_image_center_x = image_maths.mapVal(unscaled_center_x, 0.0, image.getOriginalImageWidth(), 0.0, image.getCurrentImageWidth());
                    double current_image_center_y = image_maths.mapVal(unscaled_center_y, 0.0, image.getOriginalImageHeight(), 0.0, image.getCurrentImageHeight());

                    /**
                     * This scaling does mean scaling just in tho given range of values. It is just a mapping, if the radius has been 'r' in the original image, how much will it be when it is zoomed
                     * to the current range.
                     */
                    double object_radius_for_xy = object.object_dimensions.width / 2.0;
                    double scaled_object_radius_for_xy = image_maths.mapVal(object_radius_for_xy, 0, image.getOriginalImageWidth(), 0, image.getCurrentImageWidth());

                    double current_image_start_x = current_image_center_x - scaled_object_radius_for_xy;
                    double current_image_start_y = current_image_center_y - scaled_object_radius_for_xy;

                    double current_scaled_diameter = image_maths.mapVal(object.object_dimensions.width, 0.0, (double) image.getOriginalImageWidth(), 0.0, (double) image.getCurrentImageWidth());

                    if (object.object_selected) {
                        g2d.setColor(object_select_color);
                    } else {
                        g2d.setColor(object.object_color);
                    }
                    g2d.draw(new Ellipse2D.Double(current_image_start_x, current_image_start_y, current_scaled_diameter, current_scaled_diameter));
                }
            } else if (object.object_type == JImageObject.JIMAGE_OBJECT_SQUARE) {
                if (imageMode == JImage.MODE_CONTAINER_SIZED_IMAGE) {
                    if (object.point.x >= image_curr_position.TOP_LEFT_X && object.point.x <= image_curr_position.TOP_RIGHT_X && object.point.y >= image_curr_position.TOP_LEFT_Y && object.point.y <= image_curr_position.BOTTOM_LEFT_Y) {
                        double unscaled_center_x = object.point.x - image_curr_position.TOP_LEFT_X;
                        double unscaled_center_y = object.point.y - image_curr_position.TOP_LEFT_Y;

                        double current_image_center_x = image_maths.mapVal(unscaled_center_x, 0.0, image_curr_position.getWidth(), 0.0, image.getCurrentImageWidth());
                        double current_image_center_y = image_maths.mapVal(unscaled_center_y, 0.0, image_curr_position.getHeight(), 0.0, image.getCurrentImageHeight());

                        /**
                         * This scaling does mean scaling just in tho given range of values. It is just a mapping, if the radius has been 'r' in the original image, how much will it be when it is zoomed
                         * to the current range.
                         */
                        double object_radius_length_for_xy = object.object_dimensions.width / 2.0;
                        double scaled_object_length_for_xy = image_maths.mapValInverse(object_radius_length_for_xy, 0, image.getOriginalImageWidth(), 0, image_curr_position.getWidth());

                        double current_image_start_x = current_image_center_x - scaled_object_length_for_xy;
                        double current_image_start_y = current_image_center_y - scaled_object_length_for_xy;

                        double zoomed_scaled_length = object.object_dimensions.width;
                        double current_scaled_length = image_maths.mapValInverse(zoomed_scaled_length, 0.0, (double) image.getOriginalImageWidth(), 0.0, (double) image_curr_position.getWidth());

                        if (object.object_selected) {
                            g2d.setColor(object_select_color);
                        } else {
                            g2d.setColor(object.object_color);
                        }
                        g2d.draw(new Rectangle2D.Double(current_image_start_x, current_image_start_y, current_scaled_length, current_scaled_length));
                    }
                } else if (imageMode == JImage.MODE_IMAGE_SIZED_CONTAINER) {
                    double unscaled_x = object.point.x;
                    double unscaled_y = object.point.y;

                    double unscaled_start_x = unscaled_x - (object.object_dimensions.width / 2);
                    double unscaled_start_y = unscaled_y - (object.object_dimensions.height / 2);

                    double scaled_start_x = image_maths.mapVal(unscaled_start_x, 0, image.getOriginalImageWidth(), 0, image.getCurrentImageWidth());
                    double scaled_start_y = image_maths.mapVal(unscaled_start_y, 0, image.getOriginalImageHeight(), 0, image.getCurrentImageHeight());

                    double scaled_length = image_maths.mapVal(object.object_dimensions.width, 0, image.getOriginalImageWidth(), 0, image.getCurrentImageWidth());

                    if (object.object_selected) {
                        g2d.setColor(object_select_color);
                    } else {
                        g2d.setColor(object.object_color);
                    }
                    g2d.draw(new Rectangle2D.Double(scaled_start_x, scaled_start_y, scaled_length, scaled_length));
                }
            } else if (object.object_type == JImageObject.JIMAGE_OBJECT_RECTANGLE) {
                if (imageMode == JImage.MODE_CONTAINER_SIZED_IMAGE) {
                    if (object.point.x >= image_curr_position.TOP_LEFT_X && object.point.x <= image_curr_position.TOP_RIGHT_X && object.point.y >= image_curr_position.TOP_LEFT_Y && object.point.y <= image_curr_position.BOTTOM_LEFT_Y) {
                        double unscaled_center_x = object.point.x - image_curr_position.TOP_LEFT_X;
                        double unscaled_center_y = object.point.y - image_curr_position.TOP_LEFT_Y;

                        double current_image_center_x = image_maths.mapVal(unscaled_center_x, 0.0, image_curr_position.getWidth(), 0.0, image.getCurrentImageWidth());
                        double current_image_center_y = image_maths.mapVal(unscaled_center_y, 0.0, image_curr_position.getHeight(), 0.0, image.getCurrentImageHeight());

                        /**
                         * This scaling does mean scaling just in tho given range of values. It is just a mapping, if the radius has been 'r' in the original image, how much will it be when it is zoomed
                         * to the current range.
                         */
                        double object_radius_length_for_x = object.object_dimensions.width / 2.0;
                        double scaled_object_length_for_x = image_maths.mapValInverse(object_radius_length_for_x, 0, image.getOriginalImageWidth(), 0, image_curr_position.getWidth());
                        double object_radius_length_for_y = object.object_dimensions.height / 2.0;
                        double scaled_object_length_for_y = image_maths.mapValInverse(object_radius_length_for_y, 0, image.getOriginalImageHeight(), 0, image_curr_position.getHeight());

                        double current_image_start_x = current_image_center_x - scaled_object_length_for_x;
                        double current_image_start_y = current_image_center_y - scaled_object_length_for_y;

                        double zoomed_scaled_width = object.object_dimensions.width;
                        double current_scaled_width = image_maths.mapValInverse(zoomed_scaled_width, 0.0, (double) image.getOriginalImageWidth(), 0.0, (double) image_curr_position.getWidth());

                        double zoomed_scaled_height = object.object_dimensions.height;
                        double current_scaled_height = image_maths.mapValInverse(zoomed_scaled_height, 0.0, (double) image.getOriginalImageHeight(), 0.0, (double) image_curr_position.getHeight());

                        if (object.object_selected) {
                            g2d.setColor(object_select_color);
                        } else {
                            g2d.setColor(object.object_color);
                        }
                        g2d.draw(new Rectangle2D.Double(current_image_start_x, current_image_start_y, current_scaled_width, current_scaled_height));
                    }
                } else if (imageMode == JImage.MODE_IMAGE_SIZED_CONTAINER) {
                    double unscaled_x = object.point.x;
                    double unscaled_y = object.point.y;

                    double unscaled_start_x = unscaled_x - (object.object_dimensions.width / 2);
                    double unscaled_start_y = unscaled_y - (object.object_dimensions.height / 2);

                    double scaled_start_x = image_maths.mapVal(unscaled_start_x, 0, image.getOriginalImageWidth(), 0, image.getCurrentImageWidth());
                    double scaled_start_y = image_maths.mapVal(unscaled_start_y, 0, image.getOriginalImageHeight(), 0, image.getCurrentImageHeight());

                    double scaled_width = image_maths.mapVal(object.object_dimensions.width, 0, image.getOriginalImageWidth(), 0, image.getCurrentImageWidth());
                    double scaled_height = image_maths.mapVal(object.object_dimensions.height, 0, image.getOriginalImageHeight(), 0, image.getCurrentImageHeight());

                    if (object.object_selected) {
                        g2d.setColor(object_select_color);
                    } else {
                        g2d.setColor(object.object_color);
                    }
                    g2d.draw(new Rectangle2D.Double(scaled_start_x, scaled_start_y, scaled_width, scaled_height));
                }
            } else if (object.object_type == JImageObject.JIMAGE_OBJECT_DETECT) {
                Map<String, Object> obj_props = object.getObjectProperties();
                JImageIntelligence image_intelli = (JImageIntelligence) obj_props.get(JImageObject.CUSTOM_HIDDEN_PROPERTY_AUTO_DETECT_INTELLI_OBJ);

                int realX = (int) object.point.x;
                int realY = (int) object.point.y;
                BufferedImage originalImageGray = image_intelli.getGrayScale(image.getOriginalImageWithFilters());
                int originalSeedGray = originalImageGray.getRGB(realX, realY);
                BufferedImage currentImageGray = image_intelli.getGrayScale(image.getCurrentImage());

                List<JImagePoint> edges = new ArrayList<>();

                if (imageMode == JImage.MODE_CONTAINER_SIZED_IMAGE) {
                    if (object.point.x >= image_curr_position.TOP_LEFT_X && object.point.x <= image_curr_position.TOP_RIGHT_X && object.point.y >= image_curr_position.TOP_LEFT_Y && object.point.y <= image_curr_position.BOTTOM_LEFT_Y) {
                        int unscaled_x = (int) (image_curr_position.TOP_LEFT_X - object.point.x);
                        int unscaled_y = (int) (image_curr_position.TOP_LEFT_X - object.point.x);

                        int scaled_x = (int) image_maths.mapVal(unscaled_x, 0.0, image_curr_position.getWidth(), 0.0, image.getCurrentImageWidth());
                        int scaled_y = (int) image_maths.mapVal(unscaled_y, 0.0, image_curr_position.getHeight(), 0.0, image.getCurrentImageHeight());

                        JImagePoint pointOnCurrent = new JImagePoint(scaled_x, scaled_y);
                        JImagePoint pointSeedCorrected = image_intelli.getCorrectedSeedPoint(currentImageGray, pointOnCurrent, originalSeedGray);
                        Double edgeObjectRatio = Double.parseDouble(obj_props.get(JImageObject.CUSTOM_PROPERTY_AUTO_DETECT_EDGE_OBJECT_RATIO).toString());

                        byte raster_edges[][] = image_intelli.getEdgesFromSeed(image.getCurrentImage(), pointSeedCorrected, edgeObjectRatio);

                        for (int x = 0; x < raster_edges.length; x++) {
                            for (int y = 0; y < raster_edges[x].length; y++) {
                                JImagePoint edgeOnCurrent = new JImagePoint(x, y);
                                JImagePoint edgeOnOriginal = image_maths.getJImagePointOnOriginal(edgeOnCurrent);

                                if (edgeOnOriginal.x >= image_curr_position.TOP_LEFT_X && edgeOnOriginal.x <= image_curr_position.TOP_RIGHT_X && edgeOnOriginal.y >= image_curr_position.TOP_LEFT_Y && edgeOnOriginal.y <= image_curr_position.BOTTOM_LEFT_Y) {
                                    if (raster_edges[x][y] == (byte)1) {
                                        edges.add(edgeOnCurrent);
                                    }
                                }
                            }
                        }
                    }
                }
                else if (imageMode == JImage.MODE_IMAGE_SIZED_CONTAINER) {
                    int scaled_x = (int) image_maths.mapVal(object.point.x, 0.0, image.getOriginalImageWidth(), 0.0, image.getCurrentImageWidth());
                    int scaled_y = (int) image_maths.mapVal(object.point.y, 0.0, image.getOriginalImageHeight(), 0.0, image.getCurrentImageHeight());

                    JImagePoint pointOnCurrent = new JImagePoint(scaled_x, scaled_y);
                    JImagePoint pointSeedCorrected = image_intelli.getCorrectedSeedPoint(currentImageGray, pointOnCurrent, originalSeedGray);
                    Double edgeObjectRatio = Double.parseDouble(obj_props.get(JImageObject.CUSTOM_PROPERTY_AUTO_DETECT_EDGE_OBJECT_RATIO).toString());

                    byte raster_edges[][] = image_intelli.getEdgesFromSeed(image.getCurrentImage(), pointSeedCorrected, edgeObjectRatio);

                    for (int x = 0; x < raster_edges.length; x++) {
                        for (int y = 0; y < raster_edges[x].length; y++) {
                            if (raster_edges[x][y] == (byte)1) {
                                edges.add(new JImagePoint(x, y));
                            }
                        }
                    }
                }

                object.setProperty(JImageObject.CUSTOM_HIDDEN_PROPERTY_AUTO_DETECT_EDGES_LAST_REPAINT, edges);

                if (object.object_selected) {
                    g2d.setColor(object_select_color);
                } else {
                    g2d.setColor(object.object_color);
                }

                for (JImagePoint edge : edges) {
                    int edgeX = (int) edge.x;
                    int edgeY = (int) edge.y;
                    g2d.drawLine(edgeX, edgeY, edgeX, edgeY);
                }
            }
        }

    }

    private void selectSinglePoint(JImagePoint p, int radius_x, Color obj_color) {
        JImagePoint p_on_original = image_maths.getJImagePointOnOriginal(p);

        /**
         * Here when drawing a circle, there are no two radii. But here we calculate 2 radii and add that to the dimensions with the intention of
         * calculating the starting point of the circle along x and y coordinates separately.
         * Otherwise, if this is to be calculated along only width or height, the starting point of the object along the axis which we've not calculated the point,
         * will be incorrect.
         * Please check the contents of the paintComponent method for understanding.
         */
        double obj_diameter = 2.0 * image_maths.mapVal((double) radius_x, 0.0, (double) image.getCurrentImageWidth(), 0.0, (double) image.getOriginalImageWidth());
        JImageDimension obj_dimensions = new JImageDimension(obj_diameter, obj_diameter);

        JImageObject object_new = new JImageObject(p_on_original, JImageObject.JIMAGE_OBJECT_ELLIPSE, obj_dimensions, obj_color);
        objects.add(object_new);
    }

    private void selectSquareObject(JImagePoint p, int length, Color obj_color) {
        JImagePoint p_on_original = image_maths.getJImagePointOnOriginal(p);

        int imageMode = image.getImageMode();

        double obj_length;
        if (imageMode == JImage.MODE_CONTAINER_SIZED_IMAGE) {
            obj_length = image_maths.mapVal((double) length, 0.0, (double) image.getCurrentImageWidth(), 0.0, (double) image.getOriginalImageWidth());
        } else if (imageMode == JImage.MODE_IMAGE_SIZED_CONTAINER) {
            obj_length = length;
        } else {
            obj_length = 0;
        }

        JImageDimension obj_dimensions = new JImageDimension(obj_length, obj_length);

        JImageObject object_new = new JImageObject(p_on_original, JImageObject.JIMAGE_OBJECT_SQUARE, obj_dimensions, obj_color);
        objects.add(object_new);
    }

    private void selectRectangleObject(JImagePoint point, int width, int height, Color objectColor) {
        JImagePoint pointOnOriginal = image_maths.getJImagePointOnOriginal(point);

        int imageMode = image.getImageMode();

        double objectWidth, objectHeight;
        if (imageMode == JImage.MODE_CONTAINER_SIZED_IMAGE) {
            objectWidth = image_maths.mapVal((double) width, 0.0, (double) image.getCurrentImageWidth(), 0.0, (double) image.getOriginalImageWidth());
            objectHeight = image_maths.mapVal((double) height, 0.0, (double) image.getCurrentImageHeight(), 0.0, (double) image.getOriginalImageHeight());
        } else if (imageMode == JImage.MODE_IMAGE_SIZED_CONTAINER) {
            objectWidth = width;
            objectHeight = height;
        } else {
            objectWidth = 0;
            objectHeight = 0;
        }

        JImageDimension objectDimensions = new JImageDimension(objectWidth, objectHeight);

        JImageObject objectNew = new JImageObject(pointOnOriginal, JImageObject.JIMAGE_OBJECT_RECTANGLE, objectDimensions, objectColor);
        objects.add(objectNew);
    }

    private void selectArbitraryObject(JImageIntelligence image_intelli, JImagePoint seed_point, double edge_object_color_ratio, Color obj_color) {
        JImagePoint seed_point_on_original = image_maths.getJImagePointOnOriginal(seed_point);

        /**
         * TODO: Add some method in JImageIntelligence to check whether object exists in the given boundaries
         */

        JImageObject object_new = new JImageObject(seed_point_on_original, JImageObject.JIMAGE_OBJECT_DETECT, new JImageDimension(0.0, 0.0), obj_color);
        object_new.setProperty(JImageObject.CUSTOM_HIDDEN_PROPERTY_AUTO_DETECT_INTELLI_OBJ, image_intelli);
        object_new.setProperty(JImageObject.CUSTOM_PROPERTY_AUTO_DETECT_EDGE_OBJECT_RATIO, String.valueOf(edge_object_color_ratio));

        // This line is used to fill the properties used in JImageIntelligence.getLastProperties()
        byte raster_edges[][] = image_intelli.getEdgesFromSeed(image.getOriginalImageWithFilters(), seed_point_on_original, edge_object_color_ratio);

        Map<String, Object> detected_obj_props = image_intelli.getLastProperties();
        Object keys[] = detected_obj_props.keySet().toArray();
        for (Object key : keys) {
            object_new.setProperty(key.toString(), detected_obj_props.get(key.toString()));
        }

        /* Setting hidden properties */

        // Setting edges on original image
        List<JImagePoint> edgePoints = new ArrayList<>();
        for (int x = 0; x < raster_edges.length; x++) {
            for (int y = 0; y < raster_edges[0].length; y++) {
                if (raster_edges[x][y] == 1) {
                    JImagePoint edgePoint = new JImagePoint(x, y);
                    edgePoints.add(edgePoint);
                }
            }
        }
        object_new.setProperty(JImageObject.CUSTOM_HIDDEN_PROPERTY_AUTO_DETECT_EDGES_ON_ORIGINAL, edgePoints);

        // Setting original image dimensions
        object_new.setProperty(JImageObject.CUSTOM_HIDDEN_PROPERTY_IMAGE_DIMENSIONS, new JImageDimension(image.getOriginalImageWidth(), image.getOriginalImageHeight()));

        objects.add(object_new);
    }

    /**
     * This method draws a single circle shaped point using the Graphics object provided.
     * This method needs the user to call repaint() on the container of the image
     *
     * @param p           The point at which the object is selected on your component of the image(according to its dimensions)
     * @param radius      The radius of the circle on your component of the image(according to its dimensions)
     * @param objectColor The color of the ellipse that should be drawn
     */
    public void drawSinglePoint(JImagePoint p, int radius, Color objectColor) {
        selectSinglePoint(p, radius, objectColor);
    }

    /**
     * This method draws a square shape around the given point using the Graphics object provided.
     * This method needs the user to call repaint() on the container of the image
     *
     * @param p           The point at which the object is selected on your component of the image(according to its dimensions)
     * @param length      The length of the square on your component of the image(according to its dimensions)
     * @param objectColor The color of the square that should be drawn
     */

    public void drawSquareObject(JImagePoint p, int length, Color objectColor) {
        selectSquareObject(p, length, objectColor);
    }

    /**
     * This method draws a rectangle shape around the given point using the Graphics object provided.
     * This method needs the user to call repaint() on the container of the image
     *
     * @param p           The point at which the object is selected on your component of the image(according to its dimensions)
     * @param width       The width of the rectangle on your component of the image(according to its dimensions)
     * @param height      The height of the rectangle on your component of the image(according to its dimensions)
     * @param objectColor The color of the square that should be drawn
     */
    public void drawRectangleObject(JImagePoint p, int width, int height, Color objectColor) {
        selectRectangleObject(p, width, height, objectColor);
    }

    /**
     * This method detects an object on the image using seed fill methods and draws a border on the edges of the object detected.
     * It continues to search for an object until the boundaries of the image are reached and sets the edges on the boundaries if not any edge is found.
     * This method needs the user to call repaint() on the container of the image
     *
     * @param image_intelli           JImageIntelligence object initiated on the respective image by the user. This JImage object should be the same object which was used to render the image on your container
     * @param seed_point              The seed point where the user clicked(where the search of the object should be started.
     * @param edge_object_color_ratio The ratio of the grey value of the edge to the grey value of the object's contents
     * @param obj_color               The color in which the edges should be drawn on the image
     */
    public void drawArbitraryObject(JImageIntelligence image_intelli, JImagePoint seed_point, double edge_object_color_ratio, Color obj_color) {
        selectArbitraryObject(image_intelli, seed_point, edge_object_color_ratio, obj_color);
    }

    /**
     * This method is used to add an existing JImageObject to the current set of JImageObjects.
     *
     * @param object JImageObject which has been initialized by user.
     */
    public void addObject(JImageObject object) {
        objects.add(object);
    }

    /**
     * Methods regarding the currently selected points
     */

    /**
     * Selects the object specified by the index given
     * This method needs the user to call repaint() on the container of the image
     *
     * @param index Index of the object to be chosen.
     */
    public void selectObject(int index) {
        objects.get(index).object_selected = true;
    }

    /**
     * Deselects the object specified by the index given
     * This method needs the user to call repaint() on the container of the image
     *
     * @param index Index of the object to be chosen.
     */
    public void deselectObject(int index) {
        objects.get(index).object_selected = false;
    }

    public void deselectAll() {
        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).object_selected = false;
        }
    }

    /**
     * Selection Checkers
     */

    public boolean isPointSelected(JImagePoint p, int SEARCH_MODE) {
        JImageObject objectAtPoint = getObjectSorroundingPoint(p, SEARCH_MODE);

        return objectAtPoint != null;
    }

    public JImageObject getObjectSorroundingPoint(JImagePoint p, int SEARCH_MODE) {
        JImagePoint pOnOriginal = image_maths.getJImagePointOnOriginal(p);

        for (JImageObject object : objects) {
            switch (object.object_type) {
                case JImageObject.JIMAGE_OBJECT_ELLIPSE:
                    // TODO: Distance to a point from the center of a circle and compare with the radius
                case JImageObject.JIMAGE_OBJECT_SQUARE:
                case JImageObject.JIMAGE_OBJECT_RECTANGLE:
                    if (SEARCH_MODE == SEARCH_MODE_POINT) {
                        int objectX = (int) Math.round(object.point.x);
                        int objectY = (int) Math.round(object.point.y);

                        int pOnOriginalX = (int) Math.round(pOnOriginal.x);
                        int pOnOriginalY = (int) Math.round(pOnOriginal.y);

                        if (objectX == pOnOriginalX && objectY == pOnOriginalY) {
                            return object;
                        }
                    } else if (SEARCH_MODE == SEARCH_MODE_AREA) {
                        double topLeftX = object.getXMin();
                        double topRightX = object.getXMax();
                        double topLeftY = object.getYMin();
                        double bottomLeftY = object.getYMax();

                        if (pOnOriginal.x >= topLeftX && pOnOriginal.x <= topRightX && pOnOriginal.y >= topLeftY && pOnOriginal.y <= bottomLeftY) {
                            return object;
                        }
                    }

                    break;
                case JImageObject.JIMAGE_OBJECT_DETECT:
                    Map<String, Object> objProps = object.getObjectProperties();
                    double edge_object_ratio = Double.parseDouble(objProps.get(JImageObject.CUSTOM_PROPERTY_AUTO_DETECT_EDGE_OBJECT_RATIO).toString());

                    int seedX = (int)object.point.x;
                    int seedY = (int)object.point.y;

                    if (SEARCH_MODE == SEARCH_MODE_POINT) {
                        if (seedX == pOnOriginal.x && seedY == pOnOriginal.y) {
                            return object;
                        }
                    } else if (SEARCH_MODE == SEARCH_MODE_AREA) {
                        /* Obtaining seed point Gray value */
                        BufferedImage copiedImage = image.getOriginalImageWithFilters();
                        JImageIntelligence imageIntelli = new JImageIntelligence(image);
                        BufferedImage copiedGrayImage = imageIntelli.getGrayScale(copiedImage);
                        int originalSeedGray = copiedGrayImage.getRGB(seedX, seedY);

                        /* Obtaining clicked point Gray value */
                        BufferedImage currentImage = image.getCurrentImage();
                        int clickedPointGray = currentImage.getRGB((int)p.x, (int)p.y);

                        /* Obtaining relative edge object ratio for the clicked point Gray value */
                        double mappedEdgeObjectRatioForCP = image_maths.mapVal(edge_object_ratio, 0, originalSeedGray, 0, clickedPointGray);

                        byte raster_edges[][] = imageIntelli.getEdgesFromSeed(image.getCurrentImage(), p, mappedEdgeObjectRatioForCP);
                        List<JImagePoint> edgesFromSeedPoint = (List<JImagePoint>) objProps.get(JImageObject.CUSTOM_HIDDEN_PROPERTY_AUTO_DETECT_EDGES_LAST_REPAINT);
                        for (int x = 0; x < raster_edges.length; x++) {
                            for (int y = 0; y < raster_edges[0].length; y++) {
                                if (raster_edges[x][y] == (byte)1) {
                                    JImagePoint edgePoint = new JImagePoint(x, y);
//                                    System.out.println(x+"\t"+y);

                                    if (!edgesFromSeedPoint.contains(edgePoint)) {
                                        return null;
                                    }
                                }
                            }
                        }

//                        System.out.println("++++++++++++++++++++++++");
                        for (JImagePoint edgeFromSeedPoint : edgesFromSeedPoint) {
                            int x = (int)edgeFromSeedPoint.x;
                            int y = (int)edgeFromSeedPoint.y;
//                            System.out.println(x+"\t"+y);
                        }

//                        return null;
                        return object;
                    }

                    break;
            }
        }

        return null;
    }

    /**
     * Getter methods
     */

    /**
     * This returns the JImageObject at the specified index
     *
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
     * Sets the JImage which the current objects belong to.
     * @param image The JImage which objects are related with.
     */
    public void setJImage(JImage image) {
        this.image = image;
        image_maths = image.getJImageMaths();
    }

    /**
     * Sets the JImageObject at the given index
     *
     * @param index  The index of the JimageObject to set
     * @param object JImageObject to set.
     */
    public void setObjectAt(int index, JImageObject object) {
        objects.set(index, object);
    }

    /**
     * Removes an selected object.
     * This method needs the user to call repaint() on the container of the image
     *
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
