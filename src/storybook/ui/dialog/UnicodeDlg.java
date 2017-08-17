package storybook.ui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import storybook.i18n.I18N;

/**
 * This class provides the functionality to insert an special character into the document through a dedicated dialog.
 * Selected character may be passed to listeners if one or more are defined. If no listener is defined, then the
 * character will be sent to the current text element with focus - or at least the last selected one, if it was in the
 * same frame as the button launching the dialog.
 *
 * based for character display on work from Leighton Weymouth
 */
public class UnicodeDlg {

	// Listeners
	private final List<UnicodeListener> listeners = new ArrayList<>();

	private final String[] labels;
	private final JPanel p;
	private String specialChar;
	private final JFrame parent;

	// component to insert character into.
	private JTextComponent textComponent;
	// Position on last caret - where to insert character.
	private int lastPosition = 0;

	// Flag to indicate that the last frame change was to make dialog appear.
	// In such case, we shall not consider that we change the current frame.
	private boolean itIsMe = false;

	/**
	 * Constructs this panel and sets the icon and action listeners.
	 *
	 * @param parent frame.
	 */
	public UnicodeDlg(JFrame parent) {
		this.parent = parent;
		labels = new String[448];
		// create the labels of the special characters
		for (int i = 0; i < 432; i++) {
			labels[i] = "" + ((char) (160 + i));
		}

		labels[432] = "" + ((char) 8211);
		labels[433] = "" + ((char) 8212);
		labels[434] = "" + ((char) 8215);
		labels[435] = "" + ((char) 8216);
		labels[436] = "" + ((char) 8217);
		labels[437] = "" + ((char) 8218);
		labels[438] = "" + ((char) 8220);
		labels[439] = "" + ((char) 8221);
		labels[440] = "" + ((char) 8222);
		labels[441] = "" + ((char) 8224);
		labels[442] = "" + ((char) 8225);
		labels[443] = "" + ((char) 8226);
		labels[444] = "" + ((char) 8230);
		labels[445] = "" + ((char) 8240);
		labels[446] = "" + ((char) 8364);
		labels[447] = "" + ((char) 8482);

		// set up a grid layout
		GridLayout grid = new GridLayout(16, 28);
		grid.setHgap(2);
		grid.setVgap(2);

		p = new JPanel(grid);

		// create a border around the panel
		p.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createBevelBorder(BevelBorder.LOWERED),
			I18N.getMsg("unicode.group")));

		// add the buttons and their labels
		for (String label : labels) {
			p.add(new UnicodeButton(label));
		}

		KeyboardFocusManager focusManager = KeyboardFocusManager
			.getCurrentKeyboardFocusManager();
		focusManager.addPropertyChangeListener((evt) -> {
			Object nObj = evt.getNewValue();
			if ((nObj != null) && (nObj instanceof JTextComponent)) {
				// New focused element is a text
				textComponent = ((JTextComponent) nObj);
				lastPosition = textComponent.getCaretPosition();
			} else {
				Object oObj = evt.getOldValue();
				if ((oObj != null) && (oObj instanceof JTextComponent)) {
					// Old focused element is a text
					textComponent = ((JTextComponent) oObj);
					lastPosition = textComponent.getCaretPosition();
				} else if ((textComponent != null) && (nObj != null)) {
					// changing frame ?
					Component c = (Component) nObj;
					Window newFrame = (Window) SwingUtilities.getRoot(c);
					Window oldFrame = (Window) SwingUtilities.getRoot(textComponent);
					if ((!itIsMe) && (!newFrame.equals(oldFrame))) {
						// yes : forget previous focus
						textComponent = null;
					}
				}
			}
		});
	}

	/**
	 * When a user presses the insert special character button this method is called.
	 */
	public void show() {
		Object[] messages = {p};

		Object[] options = {I18N.getMsg("unicode.insert"), I18N.getMsg("cancel")};

		// show the dialog
		itIsMe = true;
		int result = JOptionPane.showOptionDialog(parent,
			// The parent that the dialog blocks
			messages, // The dialog message arry
			I18N.getMsg("unicode.title"), // The title of the dialog window
			JOptionPane.DEFAULT_OPTION, // option type
			JOptionPane.PLAIN_MESSAGE, // message type
			/* ic */
			null, // option icon, null for none
			options, // options string array, get made into buttons
			options[0]); // option to be made default

		itIsMe = false;
		if (result == 0) { // user pressed the insert button
			if (!listeners.isEmpty()) {
				for (UnicodeListener listener : listeners) {
					listener.characterSelected(specialChar);
				}
			} else if (textComponent != null) {
				textComponent.requestFocusInWindow();
				((JTextComponent) textComponent).setCaretPosition(lastPosition);
				Document doc = ((JTextComponent) textComponent).getDocument();
				try {
					doc.insertString(lastPosition, specialChar, null);
				} catch (BadLocationException e) {
					ExceptionDlg.show("DlgUnicode", e);
				}
			}
		}
	}

	public void addListener(UnicodeListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeListener(UnicodeListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	protected class UnicodeButton extends JButton implements ActionListener, FocusListener {

		private static final long serialVersionUID = 1L;

		public UnicodeButton(String label) {
			super(label);
			this.setMargin(new Insets(0, 0, 0, 0));
			this.setFocusPainted(false);
			addActionListener(this);
			addFocusListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			specialChar = this.getText();
		}

		@Override
		public void focusGained(FocusEvent e) {
			this.setBackground(Color.yellow);
		}

		@Override
		public void focusLost(FocusEvent e) {
			this.setBackground(null);
		}

	}

	public String getDescription() {
		return "Special characters";
	}

	public interface UnicodeListener {

		public void characterSelected(String character);
	}
}
