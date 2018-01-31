package org.JImage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class JImageIntelligence {

    public static final int IMAGE_TYPE_RGB=0;
    public static final int IMAGE_TYPE_GRAY=1;

    private final JImage image;

    public JImageIntelligence(JImage image) {
        this.image = image;
    }

    /**
     * Public methods
     */

    public BufferedImage getBackgroundRemoved(Color newBackgroundColor, int returnImageType) {
        BufferedImage image_current=image.getCurrentImage();
        BufferedImage image_gray=getGrayScale();

        int color_freq[]=new int[255];
        for (int x=0;x<image_gray.getWidth();x++) {
            for (int y=0;y<image_gray.getHeight();y++) {
                color_freq[image_gray.getRGB(x, y)]++;
            }
        }

        int max_occuring_gray=-1;
        int max_occuring_gray_freq=0;
        for (int i=0;i<color_freq.length;i++) {
            if (color_freq[i]>max_occuring_gray_freq) {
                max_occuring_gray=i;
                max_occuring_gray_freq=color_freq[i];
            }
        }

        BufferedImage image_backremoved=new BufferedImage(image_gray.getWidth(), image_gray.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x=0;x<image_gray.getWidth();x++) {
            for (int y=0;y<image_gray.getHeight();y++) {
                if (image_gray.getRGB(x, y)!=max_occuring_gray) {
                    if (returnImageType==IMAGE_TYPE_RGB) {
                        image_backremoved.setRGB(x, y, image_current.getRGB(x, y));
                    }
                    else {
                        image_backremoved.setRGB(x, y, image_gray.getRGB(x, y));
                    }
                }
                else {
                    image_backremoved.setRGB(x, y, newBackgroundColor.getRGB());
                }
            }
        }

        return image_backremoved;
    }

    private int pixel_status[][];
    private int edges[][];

    /**
     * Fills the nearest edges found from the given seed point. A byte array containing '1's at the positions of edges will be returned, all other pixels being 0
     * This method uses the original image x,y coordinates
     * @param seed_point The seed point
     * @param edge_object_color_ratio Minimum ratio between the color(RGB) of the object's contents and color(RGB) of the edge's contents
     * @return A byte array containing '1's at the positions of edges, all other pixels being 0
     */

    public int[][] getEdgesFromSeed(JImagePoint seed_point, double edge_object_color_ratio) {
        BufferedImage image_original=image.getOriginalImage();
        BufferedImage image_gray=getGrayScale();

        int raster[][]=new int[image_original.getWidth()][image_original.getHeight()];
        pixel_status=new int[raster.length][raster[0].length];
        edges=new int[raster.length][raster[0].length];

        for (int x=0;x<image_original.getWidth();x++) {
            for (int y=0;y<image_original.getHeight();y++) {
                raster[x][y]=image_gray.getRGB(x, y);
                pixel_status[x][y]=0;
                edges[x][y]=0;
            }
        }

        seedFill(raster, seed_point, (int)Math.round(seed_point.x), (int)Math.round(seed_point.y), edge_object_color_ratio);

        return edges;
    }

    /**
     * Private Methods
     */

    /**
     * This method returns a grayscale image of the current image
     * @return Grayscale image
     */

    private BufferedImage getGrayScale() {
        BufferedImage image_original=image.getCurrentImage();
        BufferedImage image_gray=new BufferedImage(image_original.getWidth(), image_original.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x=0;x<image_original.getWidth();x++) {
            for (int y=0;y<image_original.getHeight();y++) {
                int rgb=image_original.getRGB(x, y);
                int r=(rgb>>16) & 0xFF;
                int g=(rgb>>8) & 0xFF;
                int b=(rgb & 0xFF);

                int gray=(r + g + b)/3;
                image_gray.setRGB(x, y, gray);
            }
        }
        return image_gray;
    }

    private void seedFill(int[][] raster, JImagePoint seed_point, int x, int y, double edge_object_color_ratio) {
        double color_diff=Math.abs(raster[x][y]-raster[(int)Math.round(seed_point.x)][(int)Math.round(seed_point.y)]);
        double color_diff_ratio=color_diff/255;

        //Check whether the current pixel has been visited earlier
        if (pixel_status[x][y]==1) {
            return;
        }

        //Mark the cell as visited
        pixel_status[x][y]=1;

        if (color_diff_ratio>edge_object_color_ratio || x==0 || y==0 || x==raster.length-1 || y==raster[0].length-1) {
            edges[x][y]=1;
            return;
        }

        seedFill(raster, seed_point, x-1, y, edge_object_color_ratio); // Left
        seedFill(raster, seed_point, x+1, y, edge_object_color_ratio); // Right
        seedFill(raster, seed_point, x, y-1, edge_object_color_ratio); // Up
        seedFill(raster, seed_point, x, y+1, edge_object_color_ratio); // Down
    }

}
