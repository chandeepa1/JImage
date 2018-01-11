public class JImagePosition {

    public int TOP_LEFT_X, TOP_LEFT_Y;
    public int TOP_RIGHT_X;
    public int BOTTOM_LEFT_Y;

    public JImagePosition(int TOP_LEFT_X, int TOP_LEFT_Y, int TOP_RIGHT_X, int BOTTOM_LEFT_Y) {
        this.TOP_LEFT_X = TOP_LEFT_X;
        this.TOP_LEFT_Y = TOP_LEFT_Y;
        this.TOP_RIGHT_X = TOP_RIGHT_X;
        this.BOTTOM_LEFT_Y = BOTTOM_LEFT_Y;
    }

    public int getWidth() {
        return TOP_RIGHT_X-TOP_LEFT_X;
    }

    public int getHeight() {
        return BOTTOM_LEFT_Y-TOP_LEFT_Y;
    }

    public void setParams(int TOP_LEFT_X, int TOP_LEFT_Y, int TOP_RIGHT_X, int BOTTOM_LEFT_Y) {
        this.TOP_LEFT_X = TOP_LEFT_X;
        this.TOP_LEFT_Y = TOP_LEFT_Y;
        this.TOP_RIGHT_X = TOP_RIGHT_X;
        this.BOTTOM_LEFT_Y = BOTTOM_LEFT_Y;
    }

    public void setTopLeftX(int topLeftX) {
        TOP_LEFT_X=topLeftX;
    }

    public void setTopRightX(int topRightX) {
        TOP_RIGHT_X=topRightX;
    }

    public void setTopLeftY(int topLeftY) {
        TOP_LEFT_Y=topLeftY;
    }

    public void setBottomLeftY(int bottomLeftY) {
        BOTTOM_LEFT_Y=bottomLeftY;
    }
}
