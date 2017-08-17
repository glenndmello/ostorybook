/*
 * Created on Jan 24, 2006
 *
 */
package shef.ui.text.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import shef.ui.HeaderPanel;
import shef.ui.ShefUtils;
import shef.ui.text.Entities;
import storybook.i18n.I18N;

public class SpecialCharDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static Icon icon = ShefUtils.getIconX32("copyright");
	private static String title = I18N.getMsg("shef.special_character");
	private static String desc = I18N.getMsg("shef.special_character_desc");

	private final Font plainFont = new Font("Dialog", Font.PLAIN, 12);
	private final Font rollFont = new Font("Dialog", Font.BOLD, 14);

	private final MouseListener mouseHandler = new MouseHandler();
	private final ActionListener buttonHandler = new ButtonHandler();

	private boolean insertEntity;

	private JTextComponent editor;

	public SpecialCharDialog(Frame parent, JTextComponent ed) {
		super(parent, title);
		editor = ed;
		init();
	}

	public SpecialCharDialog(Dialog parent, JTextComponent ed) {
		super(parent, title);
		editor = ed;
		init();
	}

	private void init() {
		JPanel headerPanel = new HeaderPanel(title, desc, icon);

		JPanel charPanel = new JPanel(new GridLayout(8, 12, 2, 2));
		charPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		for (int i = 160; i <= 255; i++) {
			String ent = "&#" + i + ";";
			JButton chLabel = new JButton(Entities.HTML32.unescape(ent));
			chLabel.setFont(plainFont);
			chLabel.setOpaque(true);
			chLabel.setToolTipText(ent);
			chLabel.setBackground(Color.white);
			chLabel.setHorizontalAlignment(SwingConstants.CENTER);
			chLabel.setVerticalAlignment(SwingConstants.CENTER);
			chLabel.addActionListener(buttonHandler);
			chLabel.addMouseListener(mouseHandler);
			chLabel.setMargin(new Insets(0, 0, 0, 0));
			charPanel.add(chLabel);
		}

		JButton close = new JButton(I18N.getMsg("shef.close"));
		close.addActionListener((ActionEvent e) -> {
			setVisible(false);
		});
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(close);
		this.getRootPane().setDefaultButton(close);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(headerPanel, BorderLayout.NORTH);
		getContentPane().add(charPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		pack();
		setResizable(false);
	}

	public void setJTextComponent(JTextComponent ed) {
		editor = ed;
	}

	public JTextComponent getJTextComponent() {
		return editor;
	}

	private class MouseHandler extends MouseAdapter {

		@Override
		public void mouseEntered(MouseEvent e) {
			JButton l = (JButton) e.getComponent();
			l.setFont(rollFont);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			JButton l = (JButton) e.getComponent();
			l.setFont(plainFont);

		}
	}

	private class ButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton l = (JButton) e.getSource();
			if (editor != null) {
				if (!editor.hasFocus()) {
					editor.requestFocusInWindow();
				}
				if (insertEntity) {
					editor.replaceSelection(l.getToolTipText());
				} else {
					editor.replaceSelection(l.getText());
				}
			}
		}

	}

	public boolean isInsertEntity() {
		return insertEntity;
	}

	public void setInsertEntity(boolean insertEntity) {
		this.insertEntity = insertEntity;
	}

}
