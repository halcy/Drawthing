import java.awt.Color;

public class Point {
	private Color brushColor;
	private int brushSize;
	private double posX;
	private double posY;
	private boolean endPoint;

	public Point( double x, double y, Color brushColor, int size,
		boolean endPoint ) {
		this.posX = x;
		this.posY = y;
		this.brushColor = brushColor;
		this.brushSize = size;
		this.endPoint = endPoint;
	}

	Color getColor() {
		return( this.brushColor );
	}
	int getBrushSize() {
		return( this.brushSize );
	}
	double getX() {
		return( this.posX );
	}
	double getY() {
		return( this.posY );
	}
	boolean isEndPoint() {
		return( this.endPoint );
	}
}