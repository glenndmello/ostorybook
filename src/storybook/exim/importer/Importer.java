/*
 * Copyright (C) 2016 favdb
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
package storybook.exim.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Session;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;

import storybook.SbApp;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.handler.AbstractEntityHandler;
import storybook.model.hbn.dao.SbGenericDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.i18n.I18N;
import storybook.model.hbn.dao.AttributeDAOImpl;
import storybook.ui.MainFrame;
import storybook.ui.dialog.ExceptionDlg;

/**
 *
 * @author favdb
 */
public class Importer {

	public String fileName;
	private final String fileExtension;
	private boolean fileOpened=false;
	private Document document;
	public String rootName;
	public Element rootNode;
	private DocumentBuilder documentBuilder;
	BookModel bookModel;
	private final MainFrame			mainFrame;
	private BookController	currentCtrl;
	private SbGenericDAOImpl<?, ?>	currentDao;
	private String msgCheck;
	private BookModel model;
	
	public Importer(String n, MainFrame m) {
		fileName=n;
		fileExtension=fileName.substring(fileName.lastIndexOf("."));
		mainFrame=m;
		currentCtrl=mainFrame.getBookController();
		SbApp.trace("fileName="+fileName+", ext="+fileExtension);
	}
	
	public boolean open() {
		SbApp.trace("Importer.open()");
		boolean rc=fileName.endsWith(".xml")?openXml():openSb();
		return(rc);
	}
	
	public boolean isOpened() {
		return(fileOpened);
	}
	
	public String getType() {
		return(fileExtension);
	}
	
	public boolean isXml() {
		return(fileExtension.equals(".xml"));
	}
	
	public void close() {
		SbApp.trace("Importer.close()");
		if (fileName.endsWith(".xml")) {
			closeXml();
		} else {
			closeSb();
		}
		BookModel model = mainFrame.getBookModel();
		AttributeDAOImpl dao = new AttributeDAOImpl(model.beginTransaction());
		dao.deleteOrphans();
		model.commit();
	}

	private boolean openXml() {
		SbApp.trace("Importer.openXml()");
		fileOpened=false;
		documentBuilder = null;
		try {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
			ExceptionDlg.show("", new Exception(I18N.getMsg("import.dlg.open.docbuilder")));
			System.err.println("DocumentBuilder error:\n"+ex.getLocalizedMessage());
			return(false);
		}
		document = readDom();
		if (document==null) {
			rootNode=null;
			return(false);
		}
		rootNode=document.getDocumentElement();
		Element n=(Element)rootNode.getElementsByTagName("book").item(0);
		if (n!=null) {
			rootName = n.getFirstChild().getNodeName();
		}
		fileOpened=true;
		return(true);
	}
	
	public Document readDom() {
		SbApp.trace("Importer.readDom()");
		Document rc=null;
		try {
			rc=documentBuilder.parse(new File(fileName));
		} catch (SAXException e) {
			ExceptionDlg.show("", new Exception(I18N.getMsg("import.dlg.open.parsing")));
			System.err.println("Parsing error for " + fileName + "\n" + e.getMessage());
		} catch (IOException e) {
			ExceptionDlg.show("", new Exception(I18N.getMsg("import.dlg.open.io")));
			System.err.println("I/O error for " + fileName + "\n" + e.getMessage());
		}
		return(rc);
	}

	public void closeXml() {
		SbApp.trace("Importer.closeXml()");
		if (fileOpened) {
			fileOpened=false;
			document=null;
			documentBuilder=null;
		}
	}

	private boolean openSb() {
		SbApp.trace("Importer.openSb()");
		boolean rc=false;
		if (rc) fileOpened=true;
		return(rc);
	}

	private void closeSb() {
		SbApp.trace("Importer.closeSb()");
		if (isOpened()) {
			
		}
		fileOpened=false;
	}

	/* return true if entity exists and not force */
	public boolean write(ImportEntity entity, boolean force) {
		boolean isNew=false;
		SbApp.trace("Importer.write("+EntityUtil.getEntityName(entity.entity)+") "+(force?"force update":""));
		AbstractEntity n;
		AbstractEntity old=null;
		if (check(entity)) {
			if (!force) return(true);
			old=(AbstractEntity) currentDao.find(entity.entity.getId());
		}
		n=ImportUtil.updateEntity(mainFrame,entity,old);
		if (old==null) currentCtrl.newEntity(n);
		else currentCtrl.updateEntity(n);
		return(false);
	}

	public boolean writeAll(List<ImportEntity> entities, boolean force) {
		if (entities.isEmpty()) return(false);
		SbApp.trace("Importer.writeAll("+EntityUtil.getEntityTitle(entities.get(0).entity)+") "+(force?"force update":""));
		boolean rc=false;
		AbstractEntityHandler handler = EntityUtil.getEntityHandler(mainFrame, entities.get(0).entity);
		model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		currentDao = handler.createDAO();
		currentDao.setSession(session);
		currentCtrl=mainFrame.getBookController();
		for (ImportEntity entity:entities) {
			if (write(entity, force)) {
				JOptionPane.showMessageDialog(null,
					I18N.getMsg("import.dlg.notok",EntityUtil.getEntityTitle(entity.entity)+" : "+EntityUtil.getEntityName(entity.entity)),
					I18N.getMsg("import"),
					JOptionPane.ERROR_MESSAGE);
				rc=true;
				break;
			}
			//model.commit();
		}
		return(rc);
	}
	
	/* check if the entity exists already, return String if yes */
	boolean check(ImportEntity entity) {
		SbApp.trace("Importer.check("+EntityUtil.getEntityName(entity.entity)+")");
		boolean rc=false;
		if (currentDao.find(entity.entity.getId())!=null) {
			rc=true;
		}
		return(rc);
	}
	
	String checkAll(List<ImportEntity> entities) {
		if (entities.isEmpty()) return("");
		SbApp.trace("Importer.checkAll("+EntityUtil.getEntityClass(entities.get(0).entity).getName()+")");
		msgCheck="";
		AbstractEntityHandler handler = EntityUtil.getEntityHandler(mainFrame, entities.get(0).entity);
		model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		currentDao = handler.createDAO();
		currentDao.setSession(session);
		for (ImportEntity entity:entities) {
			boolean msg=check(entity);
			if (msg) {
				msgCheck+=EntityUtil.getEntityName(entity.entity)+", ";
			}
		}
		if (!msgCheck.isEmpty()) {
			msgCheck=msgCheck.substring(0, msgCheck.length()-2);
		}
		model.commit();
		return(msgCheck);
	}
	
	List<ImportEntity> list(String tobj) {
		return(ImportUtil.list(this,tobj));
	}
	
}
