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
package storybook.ui.dialog;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import org.miginfocom.swing.MigLayout;
import storybook.SbConstants;
import storybook.SbPref;
import storybook.i18n.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class AboutDlg extends AbstractDialog {

	public AboutDlg(MainFrame mainFrame) {
		super(mainFrame);
		initAll();
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		super.initUi();
		this.setTitle(I18N.getMsg("about.title"));

		MigLayout layout = new MigLayout(
			"flowy",
			"[center]",
			"[]10[]10[]10[]");
		setLayout(layout);
		Container cp = getContentPane();
		cp.setBackground(Color.white);
		setPreferredSize(new Dimension(680, 650));

		// logo
		JLabel lbLogo = new JLabel((ImageIcon) I18N.getIcon("icon.logo.500", null));
		lbLogo.setOpaque(true);
		lbLogo.setBackground(Color.WHITE);

		// application info
		JLabel lbInfo = new JLabel();
		StringBuilder buf = new StringBuilder();
		buf.append(SbConstants.Storybook.PRODUCT_NAME);
		buf.append(" - Version ").append(SbConstants.Storybook.PRODUCT_VERSION);
		buf.append(" - Released on ").append(SbConstants.Storybook.PRODUCT_RELEASE_DATE);
		lbInfo.setText(buf.toString());

		JTabbedPane pane = new JTabbedPane();

		// licenses
		pane.addTab("Copyright (GPL)", initCopyRights());

		// credits
		pane.addTab("Credits", initCredits());

		// system properties
		pane.addTab("System Properties", initSytems());

		// layout
		add(lbLogo);
		add(lbInfo);
		add(pane, "grow");
		add(getCloseButton(), "right");
	}

	private JScrollPane initSytems() {
		JTextPane tx = new JTextPane();
		tx.setEditable(false);
		tx.setContentType("text/html");
		StringBuilder ta = new StringBuilder();
		ta.append("<html><body ").append(SwingUtil.setDefaultFont(mainFrame,tx)).append(">");
		ta.append("<br><b>").append(SbConstants.Storybook.PRODUCT_NAME).append("</b><br>");
		ta.append(SbConstants.Storybook.PRODUCT_VERSION).append("<br>");
		String[] keys = {
			"-Operating system", "os.name", "os.version", "os.arch",
			"-Java", "java.runtime.name", "java.version", "java.vm.version", "java.vm.vendor", "java.class.version",
			"-System file", "path.separator", "line.separator", "file.encoding", "file.separator",
			"-User parameters", "user.country", "user.language", "user.dir", "user.home", "user.timezone"};
		Properties props = System.getProperties();
		for (String key : keys) {
			if (key.charAt(0) == '-') {
				ta.append("<br><b>" + key.substring(1) + "</b><br>");
				continue;
			}
			try {
				String k = props.getProperty(key);
				if (k == null) {
					continue;
				}
				ta.append(key);
				ta.append(": ");
				k = k.replace("\n", "LF").replace("\r", "CR");
				ta.append(k);
				ta.append("<br>\n");
			} catch (Exception e) {
			}
		}
		ta.append("</body></html>");
		tx.setText(ta.toString());
		tx.setCaretPosition(0);
		return new JScrollPane(tx);
	}

	private JScrollPane initCopyRights() {
		JTextPane taGpl = new JTextPane();
		String gpl = "<html><body "+SwingUtil.setDefaultFont(mainFrame,taGpl)+">";
		gpl +="<p>" + I18N.getMsg("about.gpl.intro") + "</p>"
			+ "<p>" + I18N.getMsg("about.gpl.copyright") + SbConstants.Storybook.COPYRIGHT_YEAR + "</p>"
			+ "<p>" + I18N.getMsg("about.gpl.homepage") + SbConstants.URL.HOMEPAGE + "</p>"
			+ "<p>" + I18N.getMsg("about.gpl.distribution") + "</p>"
			+ "<p>" + I18N.getMsg("about.gpl.gpl") + "</p>"
			+ "<p>" + I18N.getMsg("about.gpl.license") + "</p>"
			+ "</body></html>";
		taGpl.setContentType("text/html");
		taGpl.setEditable(false);
		taGpl.setText(gpl);
		JScrollPane scroller = new JScrollPane(taGpl);
		scroller.setBorder(SwingUtil.getBorderEtched());
		taGpl.setCaretPosition(0);
		return (scroller);
	}

	private JScrollPane initCredits() {
		String[] translators = {
			"<p><b>Translators</b>",
			"<b><i>Translation team</i></b>",
			"English: <b>The Storybook Developer Crew</b>",
			"English Proof-Reading: <b>Rory O'Farell, Mark Coolen</b>",
			"French: <b>FaVdB</b>",
			"Hungarian: <b>Sinkovics Vivien</b>",
			"Japanese: <b>Asuka Yuki (飛香宥希/P.N.)</b>",
			"Portuguese: <b>Pedro Albuquerque</b>",
			"Spanish: <b>Stephan Miralles D&iacute;az</b>",
			"</p>"
		};
		String[] dev = {
			"<p><b>Developers</b>",
			"Franz-Albert Van Den Bussche (aka FaVdB)",
			"Jean Rebillat",
			"Bruno Raoult",
			"</p>"
		};
		String[] permondo = {
			"<p><b><i>The Permondo team</i></b> (for other translation)</i>",
			"Arabic: <b>Noha Amr</b>",
			"Bulgarian: <b>Elitsa Stoycheva</b>",
			"Chinese: <b>Feier Yang</b>",
			"Czech: <b>Linda Blättler</b>",
			"Danish: <b>Kira Petersen</b>",
			"Dutch: <b>Surayah de Visser</b>",
			"German: <b>Katharina Staab</b>",
			"Greek: <b>Dimitra Andrikou</b>",
			"Italian: <b>Flavia Guadagnino</b>",
			"Korean : <b>Kyunghee Jung</b>",
			"Polish: <b>Kamila El Khayati</b>",
			"Romanian : <b>Agnes Erika Stan</b>",
			"Russian: <b>Volodymyr Sushkov</b>",
			"Turkish: <b>Şeyma Aydın</b>",
			"Ukrainian : <b>Marina Nochniuk</b>",
			"</p>"
		};
		JTextPane taCredits = new JTextPane();
		Font font=taCredits.getFont();
		String credits = "<html><body "+SwingUtil.setDefaultFont(mainFrame,taCredits)+">";
		for (String s : dev) {
			credits += s + "<br>";
		}
		credits += "<p><b>Logo Designer</b><br>"
			+ "Jose Campoy, modified by FaVdB</p>";
		for (String s : translators) {
			credits += s + "<br>";
		}
		for (String s : permondo) {
			credits += s + "<br>";
		}
		credits += "</body></html>";
		taCredits.setContentType("text/html");
		taCredits.setEditable(false);
		taCredits.setText(credits);
		JScrollPane scroller = new JScrollPane(taCredits);
		scroller.setBorder(SwingUtil.getBorderEtched());
		taCredits.setCaretPosition(0);
		return (scroller);
	}

	public static void show(MainFrame m) {
		SwingUtil.showDialog(new AboutDlg(m), m, true);
	}
}
