package com.axiohelix.oist.JImage;

import javax.swing.*;
import java.awt.*;

public class JImageRenderer extends JPanel {

    /**
     * This class is for rendering the JImage
     */

    private final JImage image;
    private JComponent parentComponent;
    private JImagePoint startPoint;

    private JImageRendererRunnable onPaintComponent;

    public JImageRenderer(JImage image) {
        setLayout(null);
        this.image = image;

        this.setBackground(Color.RED);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image.getCurrentImage(), 0, 0, null);

        if (onPaintComponent != null) {
            onPaintComponent.setRendererGraphics(g);
            onPaintComponent.run();
        }
    }

    public void setPaintComponentEvent(JImageRendererRunnable onPaintComponent) {
        this.onPaintComponent = onPaintComponent;
    }

    /**
     * Renders the current view with the image on the provided parent component.
     * @param parentComponent The JComponent where the renderer should render itself.
     * @param startPoint The position on the parentComponent where the renderer's top left corner should be at
     */
    public void renderOnComponent(JComponent parentComponent, JImagePoint startPoint) {
        int startX = (int)startPoint.x;
        int startY = (int)startPoint.y;

        int imageMode = image.getImageMode();

        if (imageMode == JImage.MODE_CONTAINER_SIZED_IMAGE) {
            this.setBounds(startX, startY, this.getWidth(), this.getHeight());
        }
        else if (imageMode == JImage.MODE_IMAGE_SIZED_CONTAINER) {
            this.setBounds(startX, startY, image.getCurrentImageWidth(), image.getCurrentImageHeight());
        }
        else {
            // Do nothing
        }

        parentComponent.add(this);
        parentComponent.repaint();

        this.parentComponent = parentComponent;
        this.startPoint = startPoint;
    }

    /**
     * Resizes and repaints the container.
     * This method is used to update the viewport after making changes to the contents related to the image.
     * Works only if parentComponent is set.
     */
    public void refreshRenderer() {
        if (parentComponent != null) {
            parentComponent.remove(this);
            renderOnComponent(parentComponent, startPoint);
            this.repaint();
        }
    }

    /**
     * Getter methods
     */

    public JComponent getParentComponent() {
        return parentComponent;
    }

    public JImagePoint getStartPoint() {
        return startPoint;
    }

}
