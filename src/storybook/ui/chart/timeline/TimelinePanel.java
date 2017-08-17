/*
 * Copyright (C) 2017 favdb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package storybook.ui.chart.timeline;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import storybook.i18n.I18N;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class TimelinePanel extends JPanel {

	private final MainFrame mainFrame;
	private final String titre;
	private final String titreX;
	private final String titreY;
	private final Dataset dataset;
	private JLabel lbXaxis;
	private JLabel lbYaxis;
	private JPanel panel;
	private Timeline timeline;
	
	/**
	 * Creates new form TimelinePanel
	 * @param m
	 * @param titre
	 * @param dataset
	 * @param tX
	 * @param tY
	 */
	public TimelinePanel(MainFrame m, String titre, String tX, String tY, Dataset dataset) {
		setPreferredSize(new Dimension(800, 500));
		this.mainFrame = m;
		this.titre = titre;
		this.titreX=tX;
		this.titreY=tY;
		this.dataset=dataset;
		init();
	}

	private void init() {
		JLabel jTitre = new JLabel(titre);
        jTitre.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		
		panel = new JPanel();
		panel.setBorder(null);
        GroupLayout panelLayout = new GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
		
        lbXaxis = new JLabel();
        lbYaxis = new JLabel();
		lbYaxis.setUI(new VerticalLabelUI());
		if ("date".equals(titreX)) {
			lbXaxis.setText(I18N.getMsg(titreX));
			lbYaxis.setText(I18N.getMsg(titreY));
		} else {
			lbXaxis.setText(" ");
			lbYaxis.setText(" ");
		}
		timeline = new Timeline(titreX, dataset);
		panel.add(timeline);

		GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTitre, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
            .addComponent(lbXaxis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(lbYaxis, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTitre)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbXaxis)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbYaxis, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE))
                .addContainerGap())
        );
	}

	@Override
	public void paintComponent(Graphics g) {
		timeline.setSize(panel.getWidth(),panel.getHeight());
		timeline.redraw();
	}
}
