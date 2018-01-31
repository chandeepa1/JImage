package org.JImage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class JImageFilters {

    private final JImage image;

    public JImageFilters(JImage image) {
        this.image = image;
    }

    /**
     * Public methods
     */

    /**
     * This applies the gamma correction to the JImage given and returns the value.
     * @param gammaValue The gamma value to be applied
     * @return BufferedImage with gamma correction applied to it
     */
    public BufferedImage getGammaCorrected(double gammaValue) {
        double correctionValue=1/gammaValue;

        BufferedImage image_current=image.getCurrentImage();
        BufferedImage image_correct=new BufferedImage(image_current.getWidth(), image_current.getHeight(), BufferedImage.TYPE_INT_RGB);

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
