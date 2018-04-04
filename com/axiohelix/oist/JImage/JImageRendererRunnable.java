package com.axiohelix.oist.JImage;

import java.awt.*;

public abstract class JImageRendererRunnable implements Runnable {

    public Graphics rendererGraphics;

    public JImageRendererRunnable() {}

    public void setRendererGraphics(Graphics rendererGraphics) {
        this.rendererGraphics = rendererGraphics;
    }
}
