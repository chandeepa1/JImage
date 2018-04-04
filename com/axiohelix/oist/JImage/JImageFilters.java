package com.axiohelix.oist.JImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Filter;
import java.util.List;

public class JImageFilters {

    private final JImage image;
    private final Map<FilterType, List<Object>> appliedFilters;

    public enum FilterType {
        GAMMA_CORRECTION, CONTRAST
    }

    public JImageFilters(JImage image) {
        this.image = image;
        appliedFilters = new HashMap<>();
    }

    /**
     * Public methods
     */

    public void setFilter(FilterType filterType, List<Object> methodArgs) {
        appliedFilters.put(filterType, methodArgs);
    }

    public BufferedImage applyFilter(BufferedImage onImage, FilterType filterType, List<Object> methodArgs) {
        switch (filterType) {
            case GAMMA_CORRECTION:
                int gammaValue = Integer.parseInt(methodArgs.get(0).toString());
                return getGammaCorrected(onImage, gammaValue);
            case CONTRAST:
                float scaleFactor = Float.parseFloat(methodArgs.get(0).toString());
                float offset = Float.parseFloat(methodArgs.get(1).toString());

                return getContrastApplied(onImage, scaleFactor, offset);
            default:
                return null;
        }
    }

    public Map<FilterType, List<Object>> getAppliedFilters() {
        return appliedFilters;
    }

    /**
     * This applies the gamma correction to the JImage given and returns the value.
     * @param gammaValue The gamma value to be applied
     * @return BufferedImage with gamma correction applied to it
     */
    public BufferedImage getGammaCorrected(double gammaValue) {
        return getGammaCorrected(image.getCurrentImage(), gammaValue);
    }

    public static BufferedImage getGammaCorrected(BufferedImage bufImage, double gammaValue) {
        double correctionValue=1/gammaValue;

        BufferedImage image_current=bufImage;
        BufferedImage image_correct=new BufferedImage(image_current.getWidth(), image_current.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int i=0; i<image_current.getWidth(); i++) {
            for (int j=0; j<image_current.getHeight(); j++) {
                // Get pixels by R, G, B
                int alpha=new Color(image_current.getRGB(i, j)).getAlpha();
                int red=new Color(image_current.getRGB(i, j)).getRed();
                int green=new Color(image_current.getRGB(i, j)).getGreen();
                int blue=new Color(image_current.getRGB(i, j)).getBlue();

                red=(int)(255*(Math.pow((double)red/(double)255, correctionValue)));
                green=(int)(255*(Math.pow((double)green/(double)255, correctionValue)));
                blue=(int)(255*(Math.pow((double)blue/(double)255, correctionValue)));

                // Return back to original format
                int newPixel=colorToRGB(alpha, red, green, blue);

                // Write pixels into image
                image_correct.setRGB(i, j, newPixel);
            }
        }

        return image_correct;
    }

    public BufferedImage getContrastApplied(int scaleFactor, int offset) {
        return getContrastApplied(image.getCurrentImage(), scaleFactor, offset);
    }

    public BufferedImage getContrastApplied(BufferedImage bufImage, float scaleFactor, float offset) {
        RescaleOp rescaleOp = new RescaleOp(scaleFactor, offset, null);
        return rescaleOp.filter(bufImage, null);
    }

    /**
     * Private methods
     */

    private static int colorToRGB(int alpha, int red, int green, int blue) {
        int newPixel= 0;
        newPixel+=alpha; newPixel=newPixel<<8;
        newPixel+=red; newPixel=newPixel<<8;
        newPixel+=green; newPixel=newPixel<<8;
        newPixel+=blue;

        return newPixel;
    }
}
