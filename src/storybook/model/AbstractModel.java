package storybook.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.hibernate.Session;

import storybook.SbApp;
import storybook.model.hbn.SbSessionFactory;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.ui.MainFrame;

public abstract class AbstractModel {

	protected PropertyChangeSupport propertyChangeSupport;
	protected SbSessionFactory sessionFactory;
	MainFrame mainFrame;

	public AbstractModel(MainFrame m) {
		mainFrame=m;
		propertyChangeSupport = new PropertyChangeSupport(this);
		sessionFactory = new SbSessionFactory();
	}

	public abstract void fireAgain();


	public void initSession(String dbName) {
		SbApp.trace("AbstractModel.initSession("+dbName+")");
		sessionFactory.init(dbName);
	}

	public void initDefault() {
		fireAgain();
	}

	public Session beginTransaction() {
		SbApp.trace("AbstractModel.beginTransaction()");
		Session session = sessionFactory.getSession();
		session.beginTransaction();
		return session;
	}

	public Session getSession() {
		return sessionFactory.getSession();
	}

	public void commit() {
		Session session = sessionFactory.getSession();
		session.getTransaction().commit();
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		SbApp.trace("AbstractModel.addPropertyChangeListener("+l.toString()+")");
		propertyChangeSupport.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(l);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		SbApp.trace("AbstractModel.firePropertyChange("+
			"propertyName="+propertyName+
			", oldValue="+(oldValue!=null?oldValue.toString():"null")+
			", newValue="+(newValue!=null?newValue.toString():"null")+")");
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	public SbSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void editEntity(AbstractEntity entity) {
		SbApp.trace("AbstractModel.editEntity("+entity.toString()+")");
		mainFrame.showEditorAsDialog(entity);
	}
	
	public void closeSession() {
		sessionFactory.closeSession();
	}
}
