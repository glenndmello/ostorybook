/*
 Storybook: Scene-based software for novelists and authors.
 Copyright (C) 2008 - 2011 Martin Mustun

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package storybook.i18n;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.UIManager;

import storybook.SbApp;
import storybook.toolkit.DateUtil;

public class I18N {

	public final static String TIME_FORMAT = "HH:mm:ss";
	//public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static ResourceBundle iconResourceBundle = null;
	private static ResourceBundle messageBundle = null;
	private static Properties propMsg;
	private static String propMsgName;
	private static Properties propFile;
	private static String propFileName;
	private static String msgType;//0=not init, i=internal, x=external, f=file

	public static String getCountryLanguage(Locale locale) {
		return locale.getLanguage() + "_" + locale.getCountry();
	}

	public static String getDateTime(Date date) {
		return DateUtil.simpleDateTimeToString(date);
	}

	public static DateFormat getDateTimeFormatter() {
		return DateFormat.getDateTimeInstance();
	}

	public static DateFormat getShortDateFormatter() {
		return DateFormat.getDateInstance(DateFormat.SHORT);
	}

	public static DateFormat getMediumDateFormatter() {
		return DateFormat.getDateInstance(DateFormat.MEDIUM);
	}

	public static DateFormat getLongDateFormatter() {
		return DateFormat.getDateInstance(DateFormat.LONG);
	}

	public static final String getMsg(String resourceKey, Object arg) {
		Object[] args = new Object[]{arg};
		return getMsg(resourceKey, args);
	}

	public static final String getMsg(String resourceKey, Object[] args) {
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(Locale.getDefault());
		String pattern = getMsg(resourceKey);
		formatter.applyPattern(pattern);
		return formatter.format(args);
	}

	public static char getMnemonic(String key) {
		String s = getMsg(key + ".mnemonic");
		if (s != null && s.length() > 0) {
			return s.charAt(0);
		}
		return '!';
	}

	public static String getLanguage(String key) {
		ResourceBundle rb = ResourceBundle.getBundle("storybook.msg.language", Locale.getDefault());
		try {
			return rb.getString(key);
		} catch (Exception ex) {
			System.err.println("language exception:"+ex.getLocalizedMessage()+"\nlanguage="+key);
			return '!' + key + '!';
		}
	}

	public static final void initMessages(Locale locale) {
		msgType="0";
		initMsgInternal(locale);
		initMsgProp();
		if (propFileName!=null) initMsgFile();
	}
	
	public static void setFileMessages(String nf) {
		System.out.println("setFileMessages="+nf);
		File file=new File(nf);
		if (file.exists()) {
			propFileName=nf;
			msgType="f";
		} else {
			propFileName=null;
			msgType="i";
			initMsgProp();
		}
	}
	
	// an external properties file somewhere
	public static final void initMsgFile() {
		propMsg = null;
		if (propFileName==null) {
			return;
		}
		propMsg = new Properties();
		InputStream input;
		try {
			input = new FileInputStream(propFileName);
			propMsg.load(input);
			input.close();
		} catch (IOException ex) {
			System.err.println("messages file error ");
		}
	}

	// an external file inside the install sub-directory resources/i18n
	public static final Properties initMsgProp() {
		if (propMsg == null) {
			try {
				propMsg = new Properties();
				InputStream input;
				// messages localized
				String dir=System.getProperty("user.dir")+File.separator+"resources"+File.separator+"i18n";
				propMsgName=dir+File.separator+"messages_"+Locale.getDefault().getLanguage()+".properties";
				File file=new File(propMsgName);
				if (!file.exists()) {
					propMsgName=dir+File.separator+"messages.properties";
					file=new File(propMsgName);
					if (!file.exists()) {
						propMsgName="";
						propMsg=null;
						return(null);
					}
				}
				input = new FileInputStream(propMsgName);
				propMsg.load(input);
				input.close();
				msgType="x";
				return (propMsg);
			} catch (IOException ex) {
				System.out.println("external resources missing");
				propMsg=null;
				propMsgName="";
				return (null);
			}
		}
		return(propMsg);
	}

	public static final Properties getExternalResources() {
		if (propMsg == null) {
			initMsgFile();
		}
		return propMsg;
	}

	public static String getMsgFile(String key) {
		try {
			String r=propFile.getProperty(key);
			if (r==null) {
				r=getMsgExternal(key);
			}
			return(r);
		} catch (Exception ex) {
			return(getMsgInternal(key));
		}
	}

	public static String getMsgExternal(String key) {
		try {
			String r=propMsg.getProperty(key);
			if (r==null) {
				r=getMsgInternal(key);
			}
			return(r);
		} catch (Exception ex) {
			return(getMsgInternal(key));
		}
	}

	public static String getMsgInternal(String key) {
		ResourceBundle rb = getResourceBundle();
		try {
			return(rb.getString(key));
		} catch (Exception ex) {
			return '!' + key + '!';
		}
	}

	public static final void initMsgInternal(Locale locale) {
		ResourceBundle.clearCache();
		messageBundle = null;
		Locale.setDefault(locale);
		UIManager.getDefaults().setDefaultLocale(locale);
		SbApp.getInstance().setLocale(locale);
	}
	
	public static final ResourceBundle getResourceBundle() {
		if (messageBundle == null) {
			messageBundle = ResourceBundle.getBundle("storybook.msg.messages", Locale.getDefault());
		}
		return messageBundle;
	}

	public static String getMsg(String key) {
		String r="???";
		if (msgType==null) msgType="0";
		switch(msgType) {
			case "f":
				r=getMsgFile(key);
				break;
			case "x":
				r=getMsgExternal(key);
				break;
			default:
				r=getMsgInternal(key);
				break;
		}
		return(r);
	}
	
	public static String getRequiredMsg(String key, boolean required) {
		ResourceBundle rb = getResourceBundle();
		StringBuilder buf = new StringBuilder();
		if (required) {
			buf.append('*');
		}
		buf.append(getMsg(key));
		return buf.toString();
	}

	public static String getColonMsg(String key) {
		return getColonMsg(key, false);
	}

	public static String getColonMsg(String resourceKey, boolean required) {
		StringBuilder buf = new StringBuilder();
		if (required) {
			buf.append('*');
		}
		buf.append(getMsg(resourceKey));
		buf.append(':');
		return buf.toString();
	}

	public static final ResourceBundle getIconResourceBundle() {
		if (iconResourceBundle == null) {
			iconResourceBundle = ResourceBundle.getBundle("storybook.resources.icons.icons", Locale.getDefault());
		}
		return iconResourceBundle;
	}

	public static JLabel getIconLabel(String resourceKey) {
		return new JLabel(getIcon(resourceKey));
	}

	public static Icon getIcon(String resourceKey) {
		Dimension size = new Dimension(16, 16);
		if (resourceKey.contains("medium")) {
			size.height = 32;
			size.width = 32;
		}
		if (resourceKey.contains("large")) {
			size.height = 64;
			size.width = 64;
		}
		return getImageIcon(resourceKey, size);
	}

	public static Icon getIcon(String resourceKey, Dimension size) {
		return getImageIcon(resourceKey, size);
	}

	public static ImageIcon getImageIcon(String resourceKey) {
		return getImageIcon(resourceKey, new Dimension(16, 16));
	}

	public static ImageIcon getImageIcon(String resourceKey, Dimension size) {
		ResourceBundle rb = getIconResourceBundle();
		String name = rb.getString(resourceKey);
		ImageIcon icon = createImageIcon(SbApp.class, name);
		if (size == null) {
			return (icon);
		}
		return resizeIcon(icon, size);
	}

	public static Image getImage(String resourceKey) {
		ResourceBundle rb = getIconResourceBundle();
		String name = rb.getString(resourceKey + "png");
		java.net.URL imgURL = SbApp.class.getResource(name);
		ImageIcon icon = new ImageIcon(imgURL);
		return (icon.getImage());
	}

	public static ImageIcon resizeIcon(ImageIcon icon, Dimension size) {
		ImageIcon rs = icon;
		if ((icon.getIconHeight() != size.height) || (icon.getIconWidth() != size.width)) {
			int iw = icon.getIconHeight();
			int ih = icon.getIconWidth();
			int ph = size.height;
			int pw = size.width;
			double scale;
			if (2.0 * pw / iw < 2.0 * ph / ih) {
				scale = 1.0 * pw / iw;
			} else {
				scale = 1.0 * ph / ih;
			}
			int scaledw = (int) (iw * scale);
			int scaledh = (int) (ih * scale);
			Image imageicon = icon.getImage().getScaledInstance(scaledw, scaledh, Image.SCALE_DEFAULT);
			rs = new ImageIcon(imageicon);
		}
		return (rs);
	}

	public static ImageIcon createImageIcon(Class<?> c, String path) {
		java.net.URL imgURL = c.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static Image getIconImage(String resourceKey) {
		ImageIcon icon = (ImageIcon) I18N.getIcon(resourceKey);
		return icon.getImage();
	}

	public static Icon getIconExternal(String filename, Dimension size) {
		ImageIcon iconext = new ImageIcon(filename);
		return (iconext);
	}
}
