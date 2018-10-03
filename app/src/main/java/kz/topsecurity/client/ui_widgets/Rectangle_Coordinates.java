package kz.topsecurity.client.ui_widgets;

public class Rectangle_Coordinates {
    private int left_corner;
    private int top_corner;
    private int bottom_corner;
    private int right_corner;
    private int corner_radius;

    public Rectangle_Coordinates(int left_corner, int top_corner, int bottom_corner, int right_corner, int corner_radius) {
        this.left_corner = left_corner;
        this.top_corner = top_corner;
        this.bottom_corner = bottom_corner;
        this.right_corner = right_corner;
        this.corner_radius = corner_radius;
    }

    public int getLeft_corner() {
        return left_corner;
    }

    public void setLeft_corner(int left_corner) {
        this.left_corner = left_corner;
    }

    public int getTop_corner() {
        return top_corner;
    }

    public void setTop_corner(int top_corner) {
        this.top_corner = top_corner;
    }

    public int getBottom_corner() {
        return bottom_corner;
    }

    public void setBottom_corner(int bottom_corner) {
        this.bottom_corner = bottom_corner;
    }

    public int getRight_corner() {
        return right_corner;
    }

    public void setRight_corner(int right_corner) {
        this.right_corner = right_corner;
    }

    public int getCorner_radius() {
        return corner_radius;
    }

    public void setCorner_radius(int corner_radius) {
        this.corner_radius = corner_radius;
    }

    public void updateCornerRadius(){

    }
}
