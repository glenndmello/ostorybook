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

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JPanel;

import storybook.SbApp;
import storybook.toolkit.DateUtil;
import storybook.toolkit.swing.ColorUtil;

/**
 *
 * @author favdb
 */
public class Timeline extends JPanel {

	private final String type;
	private final Dataset dataset;
	
	public Timeline(String type, Dataset dataset) {
		this.type=type;
		this.dataset=dataset;
		init();
	}

	private void init() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
	}

	public String getType() {
		return type;
	}

	@Override
	public void paintComponent(Graphics g) {
		if ((dataset==null)) return;
		if (dataset.items.isEmpty()) return;
		super.paintComponent(g);
		if ("date".equals(type)) drawDate(g);
		if ("value".equals(type)) drawValue(g);
	}

	public void redraw() {
		Graphics g=this.getGraphics();
		paintComponent(g);
	}

	void drawDate(Graphics g) {
		SbApp.trace("DatasetValue.paint : dataset nb="+dataset.items.size());
		createListId();
		dataset.marginTop=0;
		dataset.marginBottom=this.getHeight();
		String maxStr="";
		for (DatasetItem item:dataset.items) {
			if (maxStr.length()<item.id.length()) maxStr=item.id;
		}
		maxStr+="W";
		Rectangle2D r=g.getFont().getStringBounds(maxStr, g.getFontMetrics().getFontRenderContext());
		dataset.marginLeft=(int) r.getWidth();
		dataset.marginRight=this.getWidth();
		if (!dataset.items.isEmpty()) {
			drawDateXaxis(g);
			drawDateYaxis(g);
			drawDateArea(g);
		}
	}

	private void drawDateXaxis(Graphics g) {
		//Xaxis est la ligne des dates
		Date minDate = null;
		Date maxDate = null;
		for (DatasetItem item : dataset.items) {
			if (minDate==null) minDate=item.debut;
			if (DateUtil.toMinutes(minDate)>DateUtil.toMinutes(item.debut)) minDate=item.debut;
			if (maxDate==null) maxDate=item.fin;
			if (DateUtil.toMinutes(maxDate)<DateUtil.toMinutes(item.fin)) maxDate=item.fin;
		}
		long dif=DateUtil.toMinutes(maxDate)-DateUtil.toMinutes(minDate);
		if (dif<0) dif=10L;
		dataset.intervalDate=dif/10;
		dataset.intervalX=(dataset.marginRight-dataset.marginLeft)/11;
		dataset.firstDate=minDate;
		dataset.lastDate=maxDate;
		String f="yyyy MMM";
		if (dif <(24*60)) f ="HH:mm";
		else if (dif <(7*24*60)) f = "d MMM HH:mm";
		else if (dif <(30*24*60)) f = "d MMM";
		else if (dif <(120*24*60)) f = "d MMM yyyy";
		else if (dif <(365*24*60)) f = "MMM yyyy";
		SimpleDateFormat formatter = new SimpleDateFormat(f);
		for (int i=0; i<11; i++) {
			int x=dataset.marginLeft+(i*dataset.intervalX);
			int y=g.getFont().getSize();
			g.drawString(formatter.format(minDate), x, y);
			minDate=DateUtil.addMinutes(minDate, (int) dataset.intervalDate);
		}
		dataset.marginTop+=(g.getFont().getSize()*2);
		dataset.areaHeight=dataset.marginBottom-dataset.marginTop;
	}
	
	private void drawDateYaxis(Graphics g) {
		//yaxis est la colonne des Id
		int gap=(g.getFont().getSize()*2);
		dataset.intervalY=dataset.areaHeight/(dataset.listId.size());
		if (dataset.intervalY==dataset.areaHeight) dataset.intervalY=dataset.areaHeight/2;
		int i=0;
		for (String strId: dataset.listId) {
			Color s=g.getColor();
			int y=(dataset.intervalY/2)+(i*dataset.intervalY)+gap;
			g.drawString(strId,0, y);
			g.setColor(s);
			i++;
			SbApp.trace("Timeline.drawDateYaxis="+strId);
		}
		dataset.areaWidth=dataset.marginRight-dataset.marginLeft;
	}
	
	private void drawDateArea(Graphics g) {
		g.setColor(Color.lightGray);
		System.out.println(
			"drawArea : top="+dataset.marginTop+
				", left="+dataset.marginLeft+
				", bottom="+dataset.marginBottom+
				", right="+dataset.marginRight+
				", width="+dataset.areaWidth+
				", height="+dataset.areaHeight+
				", intervalDate="+dataset.intervalDate+
				", intervalX="+dataset.intervalX+
				", intervalY="+dataset.intervalY);
		//g.fillRect(marginLeft, marginTop, areaWidth, areaHeight);
		int gap=(g.getFont().getSize()*2);
		int hauteur=(dataset.intervalY/3);
		if (hauteur<g.getFont().getSize()) hauteur=g.getFont().getSize();
		for (int i=0; i<dataset.listId.size();i++) {
			DatasetItem item=dataset.findItem(dataset.listId.get(i));
			//if (item==null) continue;
			if (item.color==null) continue;
			Color s=g.getColor();
			int y=(dataset.intervalY/2)+(i*dataset.intervalY)+gap-(g.getFont().getSize()/3);
			g.setColor(ColorUtil.lighter(Color.gray, 0.7D));
			g.drawLine(dataset.marginLeft, y, dataset.marginRight,y);
			g.setColor(s);
		}

		for (DatasetItem item : dataset.items) {
			if (item.color==null) continue;//nothing to draw
			drawDateItem(g,item,gap, hauteur);
			if ((item.subItems!=null)&&(!item.subItems.isEmpty())) {
				for (DatasetItem subItem:item.subItems) {
					drawDateItem(g,subItem,gap, hauteur);
				}
			}
		}
	}
	
	private void drawDateItem(Graphics g, DatasetItem item, int gap, int hauteur) {
		SbApp.trace("Timeline.drawDateItem(...)");
		System.out.println();
		if (dataset.intervalDate==0) return;
		long amplitude=DateUtil.toMinutes(dataset.lastDate)-DateUtil.toMinutes(dataset.firstDate);
		long debut=DateUtil.toMinutes(item.debut)-DateUtil.toMinutes(dataset.firstDate);
		long fin=DateUtil.toMinutes(item.fin)-DateUtil.toMinutes(dataset.firstDate);
		if (fin<0) fin=amplitude;
		long x=((dataset.areaWidth/amplitude)*debut);
		if (x<0) x=x*(-1);
		int y=(dataset.intervalY/2)+(dataset.getItemInList(item.id)*dataset.intervalY)+gap-(g.getFont().getSize()/3)-hauteur;
		long largeur=(((dataset.areaWidth/amplitude)*(fin-debut)));
		x+=dataset.marginLeft;
		if (largeur>0) {
			SbApp.trace("  --> id="+item.id+", x="+x+", y="+y+", largeur="+largeur+", hauteur="+hauteur);
			drawHorizontal(g,(int)x,(int)y,(int)largeur,hauteur*2,item.color);
		}

	}
	private void drawHorizontal(Graphics g, int x, int y, int largeur, int hauteur, Color c) {
		GradientPaint gp = new GradientPaint(0, 0, ColorUtil.lighter(c, 0.5D), 0, hauteur/2, c, true);
		Graphics2D g2d = (Graphics2D) g;
		Paint op=g2d.getPaint();
		g2d.setPaint(gp);
		g2d.fillRect(x, y, largeur, hauteur);
		g2d.setPaint(op);
		Color oc=g.getColor();
		g2d.setColor(c);
		g.drawRect(x, y, largeur, hauteur);
		g.drawRect(x+1, y+1, largeur-2, hauteur-2);
		g.setColor(c);
	}

	void drawValue(Graphics g) {
		SbApp.trace("Timeline.drawValue : dataset nb=" + dataset.items.size());
		createListId();
		dataset.marginTop = 0;
		String maxStr = "W" + Long.toString(dataset.maxValue);
		Rectangle2D r = g.getFont().getStringBounds(maxStr, g.getFontMetrics().getFontRenderContext());
		dataset.marginLeft = (int) r.getWidth();
		dataset.marginRight = this.getWidth();
		dataset.areaWidth = dataset.marginRight - dataset.marginLeft;
		dataset.marginBottom = this.getHeight() - (g.getFont().getSize() * 2);
		dataset.areaHeight = dataset.marginBottom - dataset.marginTop;
		if (!dataset.items.isEmpty()) {
			drawValueXaxis(g);
			drawValueYaxis(g);
			drawValueArea(g);
		}
	}

	private void drawValueXaxis(Graphics g) {
		//Xaxis est la ligne des Id
		dataset.intervalX = dataset.areaWidth / (dataset.listId.size());
		for (int i = 0; i < dataset.listId.size(); i++) {
			int x = dataset.marginLeft + (i * dataset.intervalX);
			int y = dataset.marginBottom + (g.getFont().getSize() * 2);
			//int y=g.getFont().getSize();
			//g.drawString(listId.get(i), x, y);
		}
	}

	private void drawValueYaxis(Graphics g) {
		//yaxis c'est la colonne des valeurs
		createListId();
		long dif = dataset.maxValue;
		if (dif < 20) {
			dif = 20L;
		}
		dataset.intervalValue = dif / 20;
		if (dataset.intervalValue * 20 > dataset.maxValue) {
			dataset.maxValue = dataset.intervalValue * 20;
		}
		dataset.intervalY = (int) (dataset.areaHeight / dataset.maxValue);
		for (long j = 0; j <= dataset.maxValue; j += dataset.intervalValue) {
			int x = 0;
			int y = (int) (dataset.marginTop + (j * dataset.intervalY)) + (g.getFont().getSize() / 2) + g.getFont().getSize();
			g.drawString(Long.toString(dataset.maxValue - j), x, y);
		}
	}

	private void drawValueArea(Graphics g) {
		int gapX = (dataset.intervalX / 4);
		int gapY = (g.getFont().getSize() / 2);
		SbApp.trace(
			"drawArea : top=" + dataset.marginTop
			+ ", left=" + dataset.marginLeft
			+ ", bottom=" + dataset.marginBottom
			+ ", right=" + dataset.marginRight
			+ ", width=" + dataset.areaWidth
			+ ", height=" + dataset.areaHeight
			+ ", maxValue=" + dataset.maxValue
			+ ", intervalValue=" + dataset.intervalValue
			+ ", intervalX=" + dataset.intervalX
			+ ", intervalY=" + dataset.intervalY
			+ ", gapX=" + gapX);
		Color s = g.getColor();
		//g.fillRect(marginLeft, marginTop, areaWidth, areaHeight);
		for (int y = 0; y <= dataset.marginBottom + dataset.intervalY; y += dataset.intervalY) {
			//int y=(intervalY/2)+(i*intervalY)+(g.getFont().getSize()*4)-(hauteur/2);
			g.setColor(ColorUtil.lighter(Color.gray, 0.7D));
			g.drawLine(dataset.marginLeft, y - gapY, dataset.marginRight, y - (g.getFont().getSize() / 2));
			g.setColor(s);
		}
		Graphics2D g2d = (Graphics2D) g;
		for (DatasetItem item : dataset.items) {
			int i = dataset.getItemInList(item.id);
			int x = dataset.marginLeft + (i * dataset.intervalX) + gapX;
			int largeur = (gapX * 2);
			int j = (int) (item.value / dataset.intervalValue);
			int hauteur = (j * dataset.intervalY);
			int y = (int) (dataset.marginBottom - hauteur) - gapY + (g.getFont().getSize());
			drawVertical(g, x, y, largeur, hauteur, item.color, item.value);
			Font nf = new Font("Sans", Font.BOLD, 15);
			Font of = g.getFont();
			g.setFont(nf);
			int x1 = (x + (gapX)) + (g.getFont().getSize() / 2);
			int y1 = (int) (dataset.marginBottom - g.getFont().getSize());
			drawRotatedString(item.id, g2d, (float) x1, (float) y1);
			g.setFont(of);
			SbApp.trace((">>> id=" + item.id + ", i=" + i + ", x=" + x + ", y=" + y + ", x1=" + x1 + ", y1=" + y1));
		}

	}

	// from org.jfree.jcommon.text.TextUtilities
	/**
	 * A utility method for drawing rotated text.
	 * <P>
	 * A common rotation is -Math.PI/2 which draws text 'vertically' (with the top of the characters on the left).
	 *
	 * @param text the text.
	 * @param g2 the graphics device.
	 * @param textX the x-coordinate for the text (before rotation).
	 * @param textY the y-coordinate for the text (before rotation).
	 */
	public static void drawRotatedString(String text, Graphics2D g2, float textX, float textY) {
		if ((text == null) || (text.equals(""))) return;
		double angle = -Math.PI / 2;
		AffineTransform saved = g2.getTransform();
		AffineTransform rotate = AffineTransform.getRotateInstance(angle, textX, textY);
		g2.transform(rotate);
		g2.drawString(text, textX, textY);
		g2.setTransform(saved);
	}

	private void drawVertical(Graphics g, int x, int y, int largeur, int hauteur, Color c, long v) {
		if (c==null) c=Color.GRAY;
		GradientPaint gp = new GradientPaint(0, 0, ColorUtil.lighter(c, 0.5D), largeur / 4, 0, c, true);
		Graphics2D g2d = (Graphics2D) g;
		Paint op = g2d.getPaint();
		g2d.setPaint(gp);
		g2d.fillRect(x, y, largeur, hauteur);
		g2d.setPaint(op);
		Color oc=g.getColor();
		g.setColor(c);
		g.drawRect(x, y, largeur, hauteur);
		g.drawRect(x+1, y+1, largeur-2, hauteur-2);
		g.setColor(oc);
		Font nf = new Font("Sans", Font.BOLD, 10);
		Font of = g.getFont();
		g.setFont(nf);
		String vx = Long.toString(v);
		Rectangle2D r = g.getFont().getStringBounds(vx, g.getFontMetrics().getFontRenderContext());
		int x1 = (int) (x + largeur - r.getWidth()) - 1;
		int y1 = (int) (y + (r.getHeight()));
		g.drawString(vx, x1, y1);
		g.setFont(of);
	}

	private void createListId() {
		dataset.listId=new ArrayList<>();
		for (DatasetItem item: dataset.items) {
			if (dataset.getItemInList(item.id)==-1) dataset.listId.add(item.id);
			if (item.subItems!=null) for (DatasetItem subItem:item.subItems) {
				if (dataset.getItemInList(subItem.id)==-1) dataset.listId.add(subItem.id);
			}
		}
	}

}
