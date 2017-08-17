/*
 * Copyright (C) 2016 favdb
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package storybook.ui.dialog.chooser;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import storybook.i18n.I18N;
import storybook.toolkit.FileFilter;

/**
 *
 * @author favdb
 */
public class IconChooserDlg extends JFileChooser {
	public IconChooserDlg() {
		final ImagePanel preview=new ImagePanel();
		preview.setSize(new Dimension(64,64));
		preview.setPreferredSize(new Dimension(64,64));
		setAccessory(preview);
		FileFilter filter=new FileFilter(FileFilter.PNG,I18N.getMsg("file.type.png"));
		this.setFileFilter(filter);
		addPropertyChangeListener((PropertyChangeEvent e) -> {
			String propertyName=e.getPropertyName();
			if (propertyName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
				File selection= (File) e.getNewValue();
				String name1;
				if (selection==null) {
					return;
				} else {
					name1 = selection.getAbsolutePath();
				}
				ImageIcon icon = new ImageIcon(name1);
				Image newImage=icon.getImage();
				preview.setImage(newImage);
			}
		});
	}

	private static class ImagePanel extends JPanel {
		private Image image;
		private Image scaledCache;

		public ImagePanel() {
			setBorder(BorderFactory.createEtchedBorder());
		}

		private void setImage(Image image) {
			this.image=image;
			scaledCache=null;
			repaint();
		}
		
		private Image getScaled() {
			int iw=image.getWidth(this);
			int ih=image.getHeight(this);
			int pw=getWidth();
			int ph=getHeight();
			double scale;
			if (2.0*pw/iw < 2.0*ph/ih) {
				scale=1.0*pw/iw;
			} else {
				scale=1.0*ph/ih;
			}
			int scaledw=(int) (iw*scale);
			int scaledh=(int) (ih*scale);
			if (scaledCache!=null) {
				if (scaledCache.getWidth(this)==scaledw && 
					scaledCache.getHeight(this)==scaledh) {
					return(scaledCache);
				}
			}
			scaledCache=image.getScaledInstance(scaledw,scaledh,Image.SCALE_DEFAULT);
			return(scaledCache);
		}
	
		@Override
		public void paintComponent(Graphics g) {
			if (g!=null) {
				Graphics scratch=g.create();
				scratch.setColor(getBackground());
				scratch.fillRect(0, 0, getWidth(), getHeight());
				if (image!=null) {
					Image scaled=getScaled();
					scratch.drawImage(scaled, getWidth()/2-scaled.getWidth(this)/2, 
						getHeight()/2-scaled.getHeight(this)/2, this);
				}
			}
		}
	}
}
