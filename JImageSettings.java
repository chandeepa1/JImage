public class JImageSettings {

    /**
     * The following two constants defines the minimum and maximum values ZOOM_SENSITIVITY can take.
     * These will also be used to calculate the reduced number of pixels in cropping the JImage
     */
    public static final int ZOOM_SENSITIVITY_MIN=1;
    public static final int ZOOM_SENSITIVITY_MAX=10;

    /*
     * ZOOM_SENSITIVITY: How many pixels will be reduced from each sides per mouse scroll
     */
    public int ZOOM_SENSITIVITY=1;

    public JImageSettings() {}

    public void setZoomSensitivity(int zoomSensitivity) {
        ZOOM_SENSITIVITY=zoomSensitivity;
    }
}
