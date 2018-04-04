package com.axiohelix.oist.JImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class JImage {

    public static final int MODE_IMAGE_SIZED_CONTAINER=0;
    public static final int MODE_CONTAINER_SIZED_IMAGE=1;

    public static final int ZOOM_TYPE_ZOOM_IN=1;
    public static final int ZOOM_TYPE_ZOOM_OUT=-1;

    private final BufferedImage image;
    private final int image_mode;
    private BufferedImage image_proc;
    private JImagePosition image_position;
    private final JImageMaths image_maths;
    private final JImageSettings image_settings;
    private final JImageFilters image_filters;

    public JImage(BufferedImage image, int image_mode) {
        this.image = image;
        this.image_mode = image_mode;

        image_proc = image;
        image_position=new JImagePosition(0, 0, image.getWidth(), image.getHeight());

        image_maths=new JImageMaths(this);
        image_settings=new JImageSettings(this);
        image_filters = new JImageFilters(this);
    }

    /**
     * Getter methods
     */

    public int getImageMode() {
        return image_mode;
    }

    /*
     * Image Maths Methods
     */

    public JImageMaths getJImageMaths() {
        return image_maths;
    }

    /*
     * Image Settings Methods
     */

    public JImageSettings getJImageSettings() {
        return image_settings;
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
        Image obj_scaled;

        if (image_mode==MODE_CONTAINER_SIZED_IMAGE) {
            obj_scaled=image.getSubimage(image_position.TOP_LEFT_X, image_position.TOP_LEFT_Y, image_position.getWidth(), image_position.getHeight()).getScaledInstance(width, height, SCALE_METHOD);
        }
        else if (image_mode==MODE_IMAGE_SIZED_CONTAINER) {
            obj_scaled=image.getScaledInstance(width, height, SCALE_METHOD);
        }
        else {
            obj_scaled=null;
        }

        BufferedImage image_scaled=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics_scaled=image_scaled.createGraphics();
        graphics_scaled.drawImage(obj_scaled,0,0,null);
        graphics_scaled.dispose();

        image_proc=image_scaled;
    }

    public BufferedImage getOriginalScaled(int width, int height, int SCALE_METHOD) {
        Image obj_scaled=image.getScaledInstance(width, height, SCALE_METHOD);

        BufferedImage image_scaled=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics_scaled=image_scaled.createGraphics();
        graphics_scaled.drawImage(obj_scaled,0,0,null);
        graphics_scaled.dispose();

        return image_scaled;
    }

    public void applyFilters() {
        Map<JImageFilters.FilterType, List<Object>> appliedFilters = image_filters.getAppliedFilters();
        Object[] appliedFilterTypes = appliedFilters.keySet().toArray();

        BufferedImage processingImage = null;
        if (image_mode == MODE_IMAGE_SIZED_CONTAINER) {
            processingImage = getOriginalScaled(getCurrentImageWidth(), getCurrentImageHeight(), BufferedImage.SCALE_REPLICATE);
        }
        else if (image_mode == MODE_CONTAINER_SIZED_IMAGE) {
            int procWidth = image_position.TOP_RIGHT_X - image_position.TOP_LEFT_X;
            int procHeight = image_position.BOTTOM_LEFT_Y - image_position.TOP_LEFT_Y;
            processingImage = image.getSubimage(image_position.TOP_LEFT_X, image_position.TOP_LEFT_Y, procWidth, procHeight);
        }

        for (Object objAppliedFilterType : appliedFilterTypes) {
            JImageFilters.FilterType appliedFilterType = (JImageFilters.FilterType) objAppliedFilterType;
            List<Object> methodArgs = appliedFilters.get(appliedFilterType);

            processingImage = image_filters.applyFilter(processingImage, appliedFilterType, methodArgs);
        }

        image_proc = processingImage;
    }

    public void cropCurrentImage(Point p_on_curr, int zoomType, boolean maintainAR) throws Exception {
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

        // If aspect ratio is to be maintained
        if (maintainAR) {
            int old_width=image.getWidth();
            int old_height=image.getHeight();

            if (old_width>old_height) {
                double hw_ratio=(double)old_height/(double)old_width;
                int new_height=(int)Math.round(image_position.getWidth()*hw_ratio);
                int new_height_reducer=(image_position.getHeight()-new_height)/2;

                new_top_left_y=image_position.TOP_LEFT_Y+new_height_reducer;
                new_bottom_left_y=image_position.BOTTOM_LEFT_Y-new_height_reducer;

                if (new_top_left_y>=image.getHeight() || new_bottom_left_y<0) {
                    throw new Exception("Cannot be zoomed further maintaining aspect ratio");
                }
                else {
                    if (new_top_left_y<0) {
                        new_bottom_left_y+=Math.abs(new_top_left_y);
                        if (new_bottom_left_y>=image.getHeight()) {
                            new_bottom_left_y=image.getHeight();
                        }
                        new_top_left_y=0;
                    }

                    if (new_bottom_left_y>=image.getHeight()) {
                        new_top_left_y-=Math.abs(new_bottom_left_y-image.getHeight());
                        if (new_top_left_y<0) {
                            new_top_left_y=0;
                        }
                        new_bottom_left_y=image.getHeight();
                    }
                }
            }
            else {
                double wh_ratio=(double)old_width/(double)old_height;
                int new_width=(int)Math.round(image_position.getHeight()*wh_ratio);
                int new_width_reducer=(image_position.getWidth()-new_width)/2;

                new_top_left_x=image_position.TOP_LEFT_X+new_width_reducer;
                new_top_right_x=image_position.TOP_RIGHT_X-new_width_reducer;

                if (new_top_left_x>=image.getWidth() || new_top_right_x<0) {
                    throw new Exception("Cannot be zoomed further maintaining aspect ratio");
                }
                else {
                    if (new_top_left_x<0) {
                        new_top_right_x+=Math.abs(new_top_left_x);
                        if (new_top_right_x>=image.getWidth()) {
                            new_top_right_x=image.getWidth();
                        }
                        new_top_left_x=0;
                    }

                    if (new_top_right_x>=image.getWidth()) {
                        new_top_left_x-=Math.abs(new_top_right_x-image.getWidth());
                        if (new_top_left_x<0) {
                            new_top_left_x=0;
                        }
                        new_top_right_x=image.getWidth();
                    }
                }
            }

            image_position.setParams(new_top_left_x, new_top_left_y, new_top_right_x, new_bottom_left_y);
        }

        image_proc=image.getSubimage(new_top_left_x, new_top_left_y, image_position.getWidth(), image_position.getHeight());

        applyFilters();
    }

    public void panCurrentImage(Point drag_amount_on_curr) {
        Point drag_amount_on_original=image_maths.getDragOnOriginal(drag_amount_on_curr);

        int reduced_drag_x=image_maths.geDragXReduced(drag_amount_on_original.x);
        int reduced_drag_y=image_maths.getDragYReduced(drag_amount_on_original.y);

        int new_top_left_x=image_position.TOP_LEFT_X+reduced_drag_x;
        int new_top_right_x=image_position.TOP_RIGHT_X+reduced_drag_x;

        int new_top_left_y=image_position.TOP_LEFT_Y+reduced_drag_y;
        int new_bottom_left_y=image_position.BOTTOM_LEFT_Y+reduced_drag_y;

        if (new_top_left_x<0) {
            new_top_left_x=0;
            new_top_right_x=image_position.TOP_RIGHT_X;
        }

        if (new_top_right_x>image.getWidth()) {
            new_top_left_x=image_position.TOP_LEFT_X;
            new_top_right_x=image.getWidth();
        }

        if (new_top_left_y<0) {
            new_top_left_y=0;
            new_bottom_left_y=image_position.BOTTOM_LEFT_Y;
        }

        if (new_bottom_left_y>image.getHeight()) {
            new_top_left_y=image_position.TOP_LEFT_Y;
            new_bottom_left_y=image.getHeight();
        }

        image_position.setParams(new_top_left_x, new_top_left_y, new_top_right_x, new_bottom_left_y);

        image_proc=image.getSubimage(new_top_left_x, new_top_left_y, image_position.getWidth(), image_position.getHeight());

        applyFilters();
    }

    public void setPositionOnOriginal(JImagePosition new_position) {
        image_position=new_position;
    }

    public void cropZoomCurrentImage(Point p, int zoomType, int scaleWidth, int scaleHeight, int scaleMethod, int zoomExtent, boolean maintainAR) throws Exception {
        if (image_mode == MODE_CONTAINER_SIZED_IMAGE) {
            cropCurrentImage(p, zoomType, maintainAR);
            setCurrentScaled(scaleWidth, scaleHeight, scaleMethod);
        }
        else if (image_mode == MODE_IMAGE_SIZED_CONTAINER) {
            int extX = image_maths.getCropXReducer(zoomExtent) * image_settings.ZOOM_SENSITIVITY;
            int extY = image_maths.getCropYReducer(zoomExtent) * image_settings.ZOOM_SENSITIVITY;

            int new_width, new_height;

            if (zoomType == ZOOM_TYPE_ZOOM_IN) {
                new_width = this.getCurrentImageWidth() + extX;
                new_height = this.getCurrentImageHeight() + extY;
            }
            else if (zoomType == ZOOM_TYPE_ZOOM_OUT) {
                new_width = this.getCurrentImageWidth() - extX;
                new_height = this.getCurrentImageHeight() - extY;
            }
            else {
                new_width = scaleWidth;
                new_height = scaleHeight;
            }

            if (new_width < getOriginalImageWidth()) {
                new_width = getOriginalImageWidth();
            }

            if (new_height < getOriginalImageHeight()) {
                new_height = getOriginalImageHeight();
            }

            setCurrentScaled(new_width, new_height, scaleMethod);
        }
        else {
            // Do nothing
        }

        applyFilters();
    }

    public void panZoomCurrentImage(Point p,  int scaleWidth, int scaleHeight, int scaleMethod) {
        panCurrentImage(p);
        setCurrentScaled(scaleWidth, scaleHeight, scaleMethod);
    }

    public void setPositionZoomCurrentImage(JImagePosition new_position, int scaleWidth, int scaleHeight, int scaleMethod) {
        setPositionOnOriginal(new_position);
        setCurrentScaled(scaleWidth, scaleHeight, scaleMethod);
    }

    public void changeContrast(float factor) {
        float scaleFactor = factor;
        float offset = 10.0f;

        List<Object> constrastArgs = new ArrayList<>();
        constrastArgs.add(scaleFactor);
        constrastArgs.add(offset);

        image_filters.setFilter(JImageFilters.FilterType.CONTRAST, constrastArgs);

        applyFilters();
    }

    /*
     * Setter methods for JImageSettings
     */

    public void setZoomSensitivity(int zoomSensitivity) {
        image_settings.setZoomSensitivity(zoomSensitivity);
    }

    public void setPanSensitivity(int panSensitivity) {
        image_settings.setPanSensitivity(panSensitivity);
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

    public void setImageFromPosition(BufferedImage image_to_set, JImagePosition position) throws Exception {
        if (position.TOP_LEFT_X<0 || position.TOP_RIGHT_X>image.getWidth() || position.TOP_LEFT_Y<0 || position.BOTTOM_LEFT_Y>image.getHeight()) {
            throw new Exception("Position out of bounds of the original image");
        }
        else {
            image_proc=image_to_set;
        }
    }

    public BufferedImage getImageFromPosition(JImagePosition position) {
        return image.getSubimage(position.TOP_LEFT_X, position.TOP_LEFT_Y, position.getWidth(), position.getHeight());
    }
}
