package org.JImage;

public class JImageSettings {

    /**
     * The following two constants defines the minimum and maximum values ZOOM_SENSITIVITY can take.
     * These will also be used to calculate the reduced number of pixels in cropping the org.JImage.JImage
     */
    public int ZOOM_SENSITIVITY_MIN=1;
    public int ZOOM_SENSITIVITY_MAX=10;

    /**
     * The following two variables defines the minimum and maximum values for panning the image.
     * These values usually take the same values as the ZOOM_SENSITIVITY_MIN and ZOOM_SENSITIVITY_MAX.
     * This makes the image to pan in the same scale/(x,y) gaps as the zoom does while cropping the image
     */
    public int PAN_SENSITIVITY_MIN=ZOOM_SENSITIVITY_MIN;
    public int PAN_SENSITIVITY_MAX=ZOOM_SENSITIVITY_MAX;

    /**
     * ZOOM_SENSITIVITY: How many pixels will be reduced from each sides per mouse scroll
     */
    public int ZOOM_SENSITIVITY=1;

    private final JImage image;

    public JImageSettings(JImage image) {
        this.image = image;
    }

    public void setZoomSensitivity(int zoomSensitivity) {
        ZOOM_SENSITIVITY=zoomSensitivity;
    }

    public void setPanSensitivity(int panSensitivity) {
        PAN_SENSITIVITY_MIN=ZOOM_SENSITIVITY_MIN*panSensitivity;
        PAN_SENSITIVITY_MAX=ZOOM_SENSITIVITY_MAX*panSensitivity;
    }
}
