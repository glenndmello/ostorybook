package storybook.ui.panel;

import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;
import storybook.SbApp;
import storybook.ui.MainFrame;

import storybook.ui.interfaces.IPaintable;
import storybook.ui.interfaces.IRefreshable;

@SuppressWarnings("serial")
public abstract class AbstractPanel extends JPanel implements IRefreshable, IPaintable {

	public MainFrame mainFrame;

	public AbstractPanel() {
	}

	public AbstractPanel(MainFrame mainframe) {
		mainFrame = mainframe;
	}
	@Override
	public abstract void init();

	@Override
	public abstract void initUi();

	public abstract void modelPropertyChange(PropertyChangeEvent evt);

	public void initAll() {
		init();
		initUi();
	}

	@Override
	public void refresh() {
		SbApp.trace("AbstractPanel.refresh() panel="+this.getClass().getSimpleName());
		removeAll();
		initAll();
		invalidate();
		validate();
		repaint();
	}

	public MainFrame getMainFrame() {
		return mainFrame;
	}
}
