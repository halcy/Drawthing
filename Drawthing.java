import java.awt.event.*;
import java.io.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.*;

/**
 * A simple drawing app using FreehandDrawPanel.
 * Supports saving to SVG.
 * @author Lorenz Diener
 */
public class Drawthing extends JFrame {
	FreehandDrawPanel canvas;

	public Drawthing() {
		// Listen for events.
		// this.addComponentListener( new MinimumSize( 400, 400 ) );

		// Drawing panel, wrapped in a scrolling container
		this.getContentPane().setLayout( new BoxLayout( this.getContentPane(), BoxLayout.Y_AXIS ) );
		canvas =  new FreehandDrawPanel();
		canvas.setPreferredSize( new Dimension( 400, 400 ) );
		JScrollPane scrollCanvas = new JScrollPane( canvas );
		this.add( scrollCanvas );

		this.add( Box.createRigidArea( new Dimension( 5, 5 ) ) );

		// Interface
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.X_AXIS ) );
		controlPanel.setMaximumSize( new Dimension( Short.MAX_VALUE, 40 ) );
		this.add( controlPanel );

		controlPanel.add( Box.createGlue() );

		JButton colorButton = new JButton( "Color" );
		colorButton.addActionListener( new ColorChangeListener( this.canvas ) );
		controlPanel.add( colorButton );

		controlPanel.add( Box.createRigidArea( new Dimension( 5, 5 ) ) );

		controlPanel.add( new JLabel( "Size: " ) );

		Integer[] brushSizes = new Integer[ 25 ];
		for( int i = 0; i < 25; i++ ) {
			brushSizes[ i ] = new Integer( i + 1 );
		}
		JComboBox brushSizeBox = new JComboBox( brushSizes );
		brushSizeBox.addActionListener( new BrushSizeListener( this.canvas ) );
		brushSizeBox.setMaximumSize( new Dimension( 40, 40 ) );
		controlPanel.add( brushSizeBox );

		controlPanel.add( Box.createRigidArea( new Dimension( 5, 5 ) ) );

		JButton undoButton = new JButton( "Undo" );
		undoButton.addActionListener( new UndoListener( this.canvas ) );
		controlPanel.add( undoButton );

		JButton resetButton = new JButton( "Reset" );
		resetButton.addActionListener( new ResetListener( this.canvas ) );
		controlPanel.add( resetButton );

		controlPanel.add( Box.createRigidArea( new Dimension( 5, 5 ) ) );

		JButton saveButton = new JButton( "Save" );
		saveButton.addActionListener( new SaveListener( this.canvas ) );
		controlPanel.add( saveButton );

		controlPanel.add( Box.createRigidArea( new Dimension( 5, 5 ) ) );

		this.add( Box.createRigidArea( new Dimension( 5, 5 ) ) );

		// Display
		this.pack();
		this.setDefaultCloseOperation( EXIT_ON_CLOSE );
	}

	public static void main( String args[] ) {
		Drawthing thingWhichWeDrawOn = new Drawthing();
		thingWhichWeDrawOn.setVisible( true );
	}

	private class BrushSizeListener implements ActionListener {
		FreehandDrawPanel canvas;

		BrushSizeListener( FreehandDrawPanel canvas ) {
			this.canvas = canvas;
		}

		public void actionPerformed( ActionEvent e ) {
			this.canvas.setBrushSize( ((JComboBox)e.getSource()).getSelectedIndex() + 1 );
		}
	}

	private class ResetListener implements ActionListener {
		FreehandDrawPanel canvas;

		ResetListener( FreehandDrawPanel canvas ) {
			this.canvas = canvas;
		}

		public void actionPerformed( ActionEvent e ) {
			this.canvas.clear();
		}
	}

	private class UndoListener implements ActionListener {
		FreehandDrawPanel canvas;

		UndoListener( FreehandDrawPanel canvas ) {
			this.canvas = canvas;
		}

		public void actionPerformed( ActionEvent e ) {
			this.canvas.undoLastStroke();
		}
	}

	private class SaveListener implements ActionListener {
		FreehandDrawPanel canvas;

		SaveListener( FreehandDrawPanel canvas ) {
			this.canvas = canvas;
		}

		public void actionPerformed( ActionEvent e ) {
			JFileChooser saveFileDialog = new JFileChooser();
			if( saveFileDialog.showSaveDialog( this.canvas ) ==
				JFileChooser.APPROVE_OPTION ) {
				File saveFile = saveFileDialog.getSelectedFile();
				try {
					PrintWriter out = new PrintWriter(
						new BufferedWriter(
							new FileWriter(
								saveFile
							)
						)
					);
					out.print( this.canvas.getSVG() );
					out.flush();
					out.close();
				}
				catch( IOException err ) {
					JOptionPane.showMessageDialog(
						null,
						"Could not save!",
						"IO Error",
						JOptionPane.ERROR_MESSAGE
					);
				}
			}
		}
	}

	private class ColorChangeListener implements ActionListener {
		FreehandDrawPanel canvas;

		ColorChangeListener( FreehandDrawPanel canvas ) {
			this.canvas = canvas;
		}

		public void actionPerformed( ActionEvent e ) {
			Color newColor = JColorChooser.showDialog(
				this.canvas,
				"Select new brush color.",
				this.canvas.getColor()
			);
			this.canvas.setColor( newColor );
		}
	}
}