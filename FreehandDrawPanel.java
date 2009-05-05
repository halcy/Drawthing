import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;

/**
 * A Jpanel that one can draw on with the mouse.
 * @author Lorenz Diener
 */
class FreehandDrawPanel extends JPanel implements MouseMotionListener, MouseListener {

	// Drawn points
	private LinkedList<Point> pointList = new LinkedList<Point>();

	// Brush status
	private Color currentColor;
	private int currentBrushSize = 1;
	private boolean currentlyDrawing = false;

	// Canvas size
	private int width;
	private int height;

	/**
	 * Create a new default FreehandDrawPanel.
	 * Default pen: Black, 1px;
	 * Default width/height: 400x400.
	 */
	public FreehandDrawPanel() {
		this.addMouseMotionListener( this );
		this.addMouseListener( this );
		this.setBackground( Color.white );
		this.setOpaque( true );
		this.width = 400;
		this.height = 400;
		this.currentColor = Color.BLACK;
		this.currentBrushSize = 1;
		this.setPreferredSize( new Dimension( this.width, this.height ) );
		this.repaint();
	}

	/**
	 * Getter for the brush size.
	 * @return The brush size.
	 */
	public int getBrushSize() {
		return( this.currentBrushSize );
	}
	public void setBrushSize( int newBrushSize ) {
		this.currentBrushSize = newBrushSize;
	}
	public Color getColor() {
		return( this.currentColor );
	}
	public void setColor( Color newColor ) {
		this.currentColor = newColor;
	}

	private void updateSize() {
		this.setPreferredSize( new Dimension( this.width, this.height ) );
		Toolkit tk = Toolkit.getDefaultToolkit();
		EventQueue evtQ = tk.getSystemEventQueue();
		evtQ.postEvent( new ComponentEvent( this, ComponentEvent.COMPONENT_RESIZED ) );
		repaint();
	}

	public LinkedList<Point> getPoints() {
		return( pointList );
	}

	public void mousePressed(MouseEvent e) {
		currentlyDrawing = true;
		pointList.add( new Point( e.getX(), e.getY(),
			this.currentColor, this.currentBrushSize, false ) );
	}

	public void mouseReleased(MouseEvent e) {
		currentlyDrawing = false;
		pointList.add( new Point( e.getX(), e.getY(),
			this.currentColor, this.currentBrushSize, true ) );
	}

	public void mouseEntered(MouseEvent e) {
		// Empty
	}

	public void mouseExited(MouseEvent e) {
		currentlyDrawing = false;
		pointList.add( new Point( e.getX(), e.getY(),
			this.currentColor, this.currentBrushSize, true ) );
	}

	public void mouseClicked(MouseEvent e) {
		// Empty
	}

	public void mouseMoved( MouseEvent e ) {
		// Empty.
	}

	public void mouseDragged( MouseEvent e ) {
		if( currentlyDrawing ) {
			pointList.add( new Point( e.getX(), e.getY(), this.currentColor, this.currentBrushSize, false ) );
		}
		this.paintLastLineSegment( this.getGraphics() );
		if( e.getX() > this.width ) {
			this.width = e.getX();
			updateSize();
		}
		if( e.getY() > this.height ) {
			this.height = e.getY();
			updateSize();
		}
	}

	public void undoLastStroke() {
		ListIterator<Point> l = this.pointList.listIterator(
			this.pointList.size()
		);
		if( !l.hasPrevious() ) {
			return;
		}

		// Remove all end-of-stroke marks.
		while( l.hasPrevious() && l.previous().isEndPoint() ) {
			l.remove();
		}
		if( !l.hasPrevious() ) {
			return;
		}
		l.remove();

		// Remove things up to the next end of stroke mark.
		while( l.hasPrevious() && !l.previous().isEndPoint() ) {
			l.remove();
		}
		this.repaint();
	}

	public void clear() {
		this.pointList.clear();
		this.repaint();
	}

	/**
	 * Return an SVG representation of this canvas's points.
	 * @return A string containing this canvas's points as SVG.
	 */
	public String getSVG() {
		String retStr = "";

		// Header.
		retStr += 
			"<?xml version=\"1.0\" standalone=\"no\"?>" +
			"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" " +
			"\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\"> " +
			"<svg width=\"" + this.width + "\" height=\"" + this.height + "\" viewBox=\"0 0 " +
			this.width + " " +
			this.height +
			"\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n";

		// Dump points.
		ListIterator<Point> l = this.pointList.listIterator();
		if( l.hasNext() ) {
			Point prevPoint = l.next();
			System.out.println( "" );
			while( l.hasNext() ) {
				Point curPoint = l.next();
				if( !prevPoint.isEndPoint() ) {
					retStr += 
						"<line x1=\"" + prevPoint.getX()  +
						"\" y1=\"" + prevPoint.getY() +
						"\" x2=\"" + curPoint.getX() +
						"\" y2=\"" + curPoint.getY() +
						"\" stroke-width=\"" + curPoint.getBrushSize() +
						"\" stroke=\"rgb(" +
						curPoint.getColor().getRed() + "," +
						curPoint.getColor().getGreen() + "," +
						curPoint.getColor().getBlue() + ")" +
						"\" stroke-linecap=\"round" +
						"\" stroke-linejoin=\"round" +
						"\" />\n";
				}
				prevPoint = curPoint;
			}
		}

		// Footer
		retStr += "</svg>\n";
		return( retStr );
	}

	/**
	 * 
	 */
	public void paintComponent( Graphics g ) {
		Graphics2D g2d = ( Graphics2D )g;
		g2d.setColor( Color.white );
		g2d.fillRect( 0, 0, (int)this.getSize().getWidth(), (int)getSize().getHeight() );
		ListIterator<Point> l = this.pointList.listIterator();

		if( !l.hasNext() ) {
			return;
		}
		Point prevPoint = l.next();

		while( l.hasNext() ) {
			Point curPoint = l.next();
			if( !prevPoint.isEndPoint() ) {
				g2d.setStroke( new BasicStroke( curPoint.getBrushSize(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND  ) );
				g2d.setColor( curPoint.getColor() );
				Shape line = new Line2D.Double(
					prevPoint.getX(),
					prevPoint.getY(),
					curPoint.getX(),
					curPoint.getY()
				);
				g2d.draw( line );
			}
			prevPoint = curPoint;
		}
	}

	public void paintLastLineSegment( Graphics g ) {
		Graphics2D g2d = ( Graphics2D )g;

		if( this.pointList.size() <= 1 ) {
			return;
		}
		Point prevPoint = this.pointList.get( this.pointList.size() - 2 );
		Point curPoint = this.pointList.get( this.pointList.size() - 1 );

		if( !prevPoint.isEndPoint() ) {
			g2d.setStroke( new BasicStroke( curPoint.getBrushSize(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND  ) );
			g2d.setColor( curPoint.getColor() );
			Shape line = new Line2D.Double(
				prevPoint.getX(),
				prevPoint.getY(),
				curPoint.getX(),
				curPoint.getY()
			);
			g2d.draw( line );
		}
	}
}