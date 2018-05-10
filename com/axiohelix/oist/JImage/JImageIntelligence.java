package com.axiohelix.oist.JImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JImageIntelligence {

    public static final int IMAGE_TYPE_RGB = 0;
    public static final int IMAGE_TYPE_GRAY = 1;

    private final JImage image;

    public JImageIntelligence() {
        this.image = new JImage(new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB), JImage.MODE_IMAGE_SIZED_CONTAINER);
    }
    public JImageIntelligence(JImage image) {
        this.image = image;
    }

    /* Private methods */

    private int[][] getGrayByteArrayFromBufferedImage(BufferedImage bufImage) {
        int returnArr[][] = new int[bufImage.getWidth()][bufImage.getHeight()];

        for (int x = 0; x < bufImage.getWidth(); x++) {
            for (int y = 0; y < bufImage.getHeight(); y++) {
                returnArr[x][y] = bufImage.getRGB(x, y);
            }
        }

        return returnArr;
    }

    /* Public methods */

    /**
     * This method returns a grayscale image of the given buffered image
     * @param imageOriginal The image which is to to grayscaled.
     * @return BufferedImage of the grayscaled original image
     */
    public BufferedImage getGrayScale(BufferedImage imageOriginal) {
        BufferedImage image_gray = new BufferedImage(imageOriginal.getWidth(), imageOriginal.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < imageOriginal.getWidth(); x++) {
            for (int y = 0; y < imageOriginal.getHeight(); y++) {
                int rgb = imageOriginal.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);

                int gray = (r + g + b) / 3;
                gray = 0xFF000000 | (gray << 16 | gray << 8 | gray);
                image_gray.setRGB(x, y, gray);
            }
        }
        return image_gray;
    }

    public List<Integer> getBackgroundGrays(BufferedImage imageOriginal) {
        BufferedImage imageGrayscale = getGrayScale(imageOriginal);
        int imageWidth = imageGrayscale.getWidth();
        int imageHeight = imageGrayscale.getHeight();

        /* Filling with the gray values */
        int grayColorSpace[] = new int[256];
        for (int i = 0; i < grayColorSpace.length; i++) {
            grayColorSpace[i] = i;
        }

        int grayColorSpaceFreq[] = new int[256];
        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                int grayXY = imageGrayscale.getRGB(x, y);

                grayColorSpaceFreq[grayXY]++;
            }
        }
        /* Sorting in descending order */
        for (int i = 0; i < grayColorSpaceFreq.length; i++) {
            for (int k = i + 1; k < grayColorSpaceFreq.length; k++) {
                if (grayColorSpaceFreq[i] < grayColorSpaceFreq[k]) {
                    int temp = grayColorSpace[i];
                    grayColorSpace[i] = grayColorSpace[k];
                    grayColorSpace[k] = temp;

                    temp = grayColorSpaceFreq[i];
                    grayColorSpaceFreq[i] = grayColorSpaceFreq[k];
                    grayColorSpaceFreq[k] = temp;
                }
            }
        }

        int highestFreqGapAt = 0;
        int highestFreqGap = 0;
        for (int i = 0; i < grayColorSpace.length - 1; i++) {
            int gapWithNextGray = Math.abs(grayColorSpaceFreq[i] - grayColorSpaceFreq[i + 1]);

            if (gapWithNextGray > highestFreqGap) {
                highestFreqGapAt = i;
                highestFreqGap = gapWithNextGray;
            }
        }

        List<Integer> backgroundGrays = new ArrayList<>();
        for (int i = 0; i <= highestFreqGapAt; i++) {
            backgroundGrays.add(grayColorSpace[i]);
        }

        return backgroundGrays;
    }

    public BufferedImage getBackgroundRemoved(BufferedImage imageOriginal, Color newBackgroundColor, int returnImageType) {
        List<Integer> backgroundGrays = getBackgroundGrays(imageOriginal);
        BufferedImage imageGray = getGrayScale(imageOriginal);

        BufferedImage imageForeground = new BufferedImage(imageGray.getWidth(), imageGray.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < imageGray.getWidth(); x++) {
            for (int y = 0; y < imageGray.getHeight(); y++) {
                int currentGray = imageGray.getRGB(x, y);
                if (!backgroundGrays.contains(currentGray)) {
                    if (returnImageType == IMAGE_TYPE_RGB) {
                        imageForeground.setRGB(x, y, imageOriginal.getRGB(x, y));
                    } else {
                        imageForeground.setRGB(x, y, imageGray.getRGB(x, y));
                    }
                } else {
                    imageForeground.setRGB(x, y, newBackgroundColor.getRGB());
                }
            }
        }

        return imageForeground;
    }

    /* Seed Fill Method and its properties */
    private int pixel_status[][];
    private byte edges[][];
    private Map<String, Object> obj_props;
    private int enclosed_area = 0;

    /* Variables for seed fill with object size count */
    private boolean maxSizeExceeded = false;

    private void resetProperties() {
        obj_props = new HashMap<>();
        enclosed_area = 0;
    }

    /**
     * Fills the nearest edges found from the given seed point. A byte array containing '1's at the positions of edges will be returned, all other pixels being 0
     * This method uses the original image x,y coordinates
     *
     * @param image_original          The image(grayscale or colored) to be used in edge detection.
     * @param seed_point              The seed point
     * @param edge_object_color_ratio Minimum ratio between the color(RGB) of the object's contents and color(RGB) of the edge's contents
     * @return A byte array containing '1's at the positions of edges, all other pixels being 0
     */

    public byte[][] getEdgesFromSeed(BufferedImage image_original, JImagePoint seed_point, double edge_object_color_ratio) {
        BufferedImage image_gray = getGrayScale(image_original);
        int raster[][] = new int[image_original.getWidth()][image_original.getHeight()];
        pixel_status = new int[raster.length][raster[0].length];
        edges = new byte[raster.length][raster[0].length];
        resetProperties();

        for (int x = 0; x < image_original.getWidth(); x++) {
            for (int y = 0; y < image_original.getHeight(); y++) {
                raster[x][y] = image_gray.getRGB(x, y) & 0xFF;
                pixel_status[x][y] = 0;
                edges[x][y] = (byte) 0;
            }
        }

        seedFill(raster, seed_point, (int) seed_point.x, (int) seed_point.y, edge_object_color_ratio);

        obj_props.put(JImageObject.PROPERTY_ALL_ENCLOSED_AREA, enclosed_area);

        enclosed_area = 0;

        return edges;
    }

    public byte[][] getEdgesFromSeed(BufferedImage image_original, JImagePoint seed_point, double edge_object_color_ratio, int maxObjectSize) {
        BufferedImage image_gray = getGrayScale(image_original);
        int raster[][] = new int[image_original.getWidth()][image_original.getHeight()];
        pixel_status = new int[raster.length][raster[0].length];
        edges = new byte[raster.length][raster[0].length];
        resetProperties();

        for (int x = 0; x < image_original.getWidth(); x++) {
            for (int y = 0; y < image_original.getHeight(); y++) {
                raster[x][y] = image_gray.getRGB(x, y) & 0xFF;
                pixel_status[x][y] = 0;
                edges[x][y] = (byte) 0;
            }
        }
        maxSizeExceeded = false;

        seedFill(raster, seed_point, (int) seed_point.x, (int) seed_point.y, edge_object_color_ratio, maxObjectSize);

        obj_props.put(JImageObject.PROPERTY_ALL_ENCLOSED_AREA, enclosed_area);

        enclosed_area = 0;

        return edges;
    }

    public Map<String, Object> getLastProperties() {
        return obj_props;
    }

    public List<JImagePoint> getLastFilledPixels() {
        List<JImagePoint> listFilled = new ArrayList<>();
        for (int x = 0; x < pixel_status.length; x++) {
            for (int y = 0; y < pixel_status[0].length; y++) {
                if (pixel_status[x][y] == 1) {
                    listFilled.add(new JImagePoint(x, y));
                }
            }
        }

        return listFilled;
    }

    private final int SEED_CORRECTION_WEIGHT_MASK[][] = {
            {5, 6, 4},
            {8, 9, 7},
            {2, 3, 1}
    };

    private final JImagePoint SEED_CORRECTION_MASK_SEED_POSITION = new JImagePoint(1, 1);

    /**
     * Used to obtain the correct seed point after zooming/scaling the image into different sizes.
     * This is useful when the image is rescaled/zoomed into different sizes. The seed point used should also be rescaled to match the scaled/zoomed image.
     * But when this is done, as the coordinate of the seed point is an integer, the actual value may shift to neighbouring pixels upon calculations.
     * This method rectifies the above error by searching the neighbouring pixels with the same gray value as the original seed point with different weights applied to them.
     * @param grayImage The grayscale image to be used.
     * @param calcSeedPoint The rescaled seed point which matches the scaled/zoomed image <b>(Calculated seed point)</b>
     * @param originalSeedGrayValue The gray value on the original seed point on original image.
     * @return JImagePoint object of the corrected seed point.
     */
    public JImagePoint getCorrectedSeedPoint(BufferedImage grayImage, JImagePoint calcSeedPoint, int originalSeedGrayValue) {
        int seedMaskWidth = SEED_CORRECTION_WEIGHT_MASK.length;
        int seedMaskHeight = SEED_CORRECTION_WEIGHT_MASK[0].length;

        int startX = (int) calcSeedPoint.x - (int)SEED_CORRECTION_MASK_SEED_POSITION.x;
        int startY = (int) calcSeedPoint.y - (int)SEED_CORRECTION_MASK_SEED_POSITION.y;

        int endX = startX + seedMaskWidth;
        int endY = startY + seedMaskHeight;

        int maxVal = 0;
        JImagePoint correctedSeedPoint = calcSeedPoint;

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                int maskX = x - startX;
                int maskY = y - startY;

                if (x > 0 && y > 0 && x < grayImage.getWidth() && y < grayImage.getHeight()) {
                    int xyGrayValue = grayImage.getRGB(x, y);

                    if (xyGrayValue == originalSeedGrayValue) {
                        int xyMaskedVal = SEED_CORRECTION_WEIGHT_MASK[maskX][maskY];

                        if (xyMaskedVal > maxVal) {
                            correctedSeedPoint = new JImagePoint(x, y);
                            maxVal = xyMaskedVal;
                        }
                    }
                }
            }
        }

        return correctedSeedPoint;
    }

    /* Actual seed fill method */
    public void seedFill(int[][] raster, JImagePoint seed_point, int x, int y, double edge_object_color_ratio) {
        double color_diff = Math.abs(raster[x][y] - raster[(int) Math.round(seed_point.x)][(int) Math.round(seed_point.y)]);
        double color_diff_ratio = color_diff / 255.0;

        //Check whether the current pixel has been visited earlier
        if (pixel_status[x][y] == 1) {
            return;
        }

        //Mark the cell as visited
        pixel_status[x][y] = 1;

        if (color_diff_ratio >= edge_object_color_ratio || x == 0 || y == 0 || x == raster.length - 1 || y == raster[0].length - 1) {
            edges[x][y] = (byte) 1;
            return;
        }

        enclosed_area++;

        seedFill(raster, seed_point, x - 1, y, edge_object_color_ratio); // Left
        seedFill(raster, seed_point, x + 1, y, edge_object_color_ratio); // Right
        seedFill(raster, seed_point, x, y - 1, edge_object_color_ratio); // Up
        seedFill(raster, seed_point, x, y + 1, edge_object_color_ratio); // Down
    }

    /* Actual seed fill method with maximum object size */
    public void seedFill(int[][] raster, JImagePoint seed_point, int x, int y, double edge_object_color_ratio, int maxObjectSizePossible) {
        // Checking the current size of the object : performance enhanced when the following block is here.
        if (maxSizeExceeded) {
            return;
        }

        double color_diff = Math.abs(raster[x][y] - raster[(int) Math.round(seed_point.x)][(int) Math.round(seed_point.y)]);
        double color_diff_ratio = color_diff / 255.0;

        //Check whether the current pixel has been visited earlier
        if (pixel_status[x][y] == 1) {
            return;
        }

        //Mark the cell as visited
        pixel_status[x][y] = 1;

        if (color_diff_ratio >= edge_object_color_ratio || x == 0 || y == 0 || x == raster.length - 1 || y == raster[0].length - 1) {
            edges[x][y] = (byte) 1;
            return;
        }

        enclosed_area++;
        if (enclosed_area > maxObjectSizePossible) {
            maxSizeExceeded = true;
        }

        seedFill(raster, seed_point, x - 1, y, edge_object_color_ratio, maxObjectSizePossible); // Left
        seedFill(raster, seed_point, x + 1, y, edge_object_color_ratio, maxObjectSizePossible); // Right
        seedFill(raster, seed_point, x, y - 1, edge_object_color_ratio, maxObjectSizePossible); // Up
        seedFill(raster, seed_point, x, y + 1, edge_object_color_ratio, maxObjectSizePossible); // Down
    }

    /* End of Seed Fill methods. */

    /* Sobel Operator Methods */

    private final int[][] MASK_SOBEL_ARR_HORIZONTAL = {
            {-1, -1, -1},
            {0, 0, 0},
            {1, 1, 1}
    };

    private final int[][] MASK_SOBEL_ARR_VERTICAL = {
            {-1, 0, 1},
            {-1, 0, 1},
            {-1, 0, 1}
    };

    public BufferedImage getSobelOperatorAppliedToGrayImage(BufferedImage imageGrayScale, int horizontalWeight, int verticalWeight) {
        // Adding weights to the mask
        int[][] WEIGHTED_MASK_SOBEL_ARR_HORIZONTAL = MASK_SOBEL_ARR_HORIZONTAL;
        WEIGHTED_MASK_SOBEL_ARR_HORIZONTAL[1][0] *= horizontalWeight;
        WEIGHTED_MASK_SOBEL_ARR_HORIZONTAL[1][2] *= horizontalWeight;

        int[][] WEIGHTED_MASK_SOBEL_ARR_VERTICAL = MASK_SOBEL_ARR_VERTICAL;
        WEIGHTED_MASK_SOBEL_ARR_VERTICAL[0][1] *= verticalWeight;
        WEIGHTED_MASK_SOBEL_ARR_VERTICAL[2][1] *= verticalWeight;

        int width = imageGrayScale.getWidth();
        int height = imageGrayScale.getHeight();

        int arrImage[][] = getGrayByteArrayFromBufferedImage(imageGrayScale);
        int maskedArrHorizontal[][] = new int[width][height];
        int maskedArrVertical[][] = new int[width][height];

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int maskingPixel = 0;

                for (int i = 0; i < WEIGHTED_MASK_SOBEL_ARR_HORIZONTAL.length; i++) {
                    for (int j = 0; j < WEIGHTED_MASK_SOBEL_ARR_HORIZONTAL[0].length; j++) {
                        maskingPixel += (arrImage[(x - 1) + i][(y - 1) + j] & 0xFF) * WEIGHTED_MASK_SOBEL_ARR_HORIZONTAL[i][j];
                    }
                }

                maskedArrHorizontal[x][y] = maskingPixel;

                maskingPixel = 0;
                for (int i = 0; i < MASK_SOBEL_ARR_VERTICAL.length; i++) {
                    for (int j = 0; j < MASK_SOBEL_ARR_VERTICAL[0].length; j++) {
                        maskingPixel += (arrImage[(x - 1) + i][(y - 1) + j] & 0xFF) * WEIGHTED_MASK_SOBEL_ARR_VERTICAL[i][j];
                    }
                }

                maskedArrVertical[x][y] = maskingPixel;
            }
        }

        int maxPixelVal = 0;
        int sumGrayscale[][] = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                sumGrayscale[x][y] = (int) Math.round(Math.sqrt(Math.pow(maskedArrHorizontal[x][y], 2) + Math.pow(maskedArrVertical[x][y], 2)));

                if (maxPixelVal < sumGrayscale[x][y]) {
                    maxPixelVal = sumGrayscale[x][y];
                }
            }
        }

        BufferedImage sobelGrayScale = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        double maxColorRatio = maxPixelVal / 255;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int scaledPixelValue = (int)(sumGrayscale[x][y] / maxColorRatio);
                scaledPixelValue = 0xFF000000 | (scaledPixelValue << 16 | scaledPixelValue << 8 | scaledPixelValue);
                sobelGrayScale.setRGB(x, y, scaledPixelValue);
            }
        }

        return sobelGrayScale;
    }

    public BufferedImage getSobelOperatorAppliedToRGBImage(BufferedImage imageRGB, int horizontalWeight, int verticalWeight) {
        BufferedImage imageGrayscale = getGrayScale(imageRGB);
        return getSobelOperatorAppliedToGrayImage(imageGrayscale, horizontalWeight, verticalWeight);
    }

    /* End of Sobel Operator Methods */

    /* Beginning of Image Averaging methods */

    public BufferedImage getAveragedImage(BufferedImage[] imageSet) throws Exception {
        int imageCount = imageSet.length;

        for (int i = 1; i < imageCount; i++) {
            if (imageSet[0].getWidth() != imageSet[i].getWidth() || imageSet[0].getHeight() != imageSet[i].getHeight()) {
                throw new Exception("All the images should be in the same dimensions to average them.");
            }
        }

        int imageWidth = imageSet[0].getWidth();
        int imageHeight = imageSet[0].getHeight();

        BufferedImage outImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                int redSum = 0, greenSum = 0, blueSum = 0;
                for (int i = 0; i < imageCount; i++) {
                    int rgb = imageSet[i].getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = (rgb & 0xFF);

                    redSum += r;
                    greenSum += g;
                    blueSum += b;
                }

                int redAvg = redSum / imageCount;
                int greenAvg = greenSum / imageCount;
                int blueAvg = blueSum / imageCount;

                int outPixel = 0xFF000000 | (redAvg << 16 | greenAvg << 8 | blueAvg);
                outImage.setRGB(x, y, outPixel);
            }
        }

        return outImage;
    }

    /* End of Image Averaging methods */

}
