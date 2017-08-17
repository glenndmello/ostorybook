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

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import jortho.SpellChecker;
import jortho.UserDictionaryProvider;
import org.miginfocom.swing.MigLayout;
import storybook.i18n.I18N;
import storybook.ui.MainFrame;
import storybook.ui.dialog.AbstractDialog;

/**
 *
 * @author favdb
 */
public class EditDictionaryDlg extends AbstractDialog {
	JButton btAdd;
	JButton btRemove;
	JButton btSearch;
	JList<String> jList1;
	JTextField txWord;
	private boolean isModify;
	
	public EditDictionaryDlg(MainFrame parent) {
		super(parent);
		initAll();
	}

	@Override
	public void init() {
		String iconDir="/storybook/resources/icons/16x16/";
		JLabel jLabel1=new JLabel(I18N.getMsg("search.for"));
		txWord=new JTextField(20);
        txWord.addCaretListener((javax.swing.event.CaretEvent evt) -> {
			txWordCaretUpdate();
		});
		JLabel jLabel2=new JLabel(I18N.getMsg("dict.user"));
		jList1 = new javax.swing.JList<>();
		loadWordList();
        jList1.addListSelectionListener((javax.swing.event.ListSelectionEvent evt) -> {
			btRemove.setEnabled(true);
		});
		JScrollPane scroller1=new JScrollPane();
		scroller1.setViewportView(jList1);
		//scroller1.setMinimumSize(new Dimension(jLabel1.getWidth()+txWord.getWidth(),202));
		
		btSearch=new JButton();
        btSearch.setIcon(new ImageIcon(getClass().getResource(iconDir+"search.png")));
        btSearch.setToolTipText(I18N.getMsg("jortho.searchword"));
        btSearch.setEnabled(false);
		btSearch.setMargin(new Insets(0,0,0,0));
        btSearch.addActionListener((ActionEvent evt) -> {
			searchAction();
		});

        btRemove=new JButton();
		btRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource(iconDir+"minus.png")));
        btRemove.setToolTipText(I18N.getMsg("jortho.delete"));
        btRemove.setEnabled(false);
		btRemove.setMargin(new Insets(0,0,0,0));
        btRemove.addActionListener((java.awt.event.ActionEvent evt) -> {
			removeAction();
		});
		
		btAdd=new JButton();
        btAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource(iconDir+"plus.png")));
        btAdd.setToolTipText(I18N.getMsg("jortho.addToDictionary"));
        btAdd.setEnabled(false);
		btAdd.setMargin(new Insets(0,0,0,0));
        btAdd.addActionListener((ActionEvent evt) -> {
			addAction();
		});
		
		//layout
		setLayout(new MigLayout("wrap 4"));
		setTitle(I18N.getMsg("jortho.userDictionary"));
		add(jLabel1);add(txWord);add(btSearch);add(btAdd,"wrap");
		add(scroller1,"span 2,growx");add(btRemove,"span 2,top,left, wrap");
		add(getCloseButton(),"span,right");
		pack();
		setLocationRelativeTo(mainFrame);
		this.setModal(true);		
	}
	
	@SuppressWarnings("unchecked")
	private void loadWordList() {
		DefaultListModel data = new DefaultListModel();
		UserDictionaryProvider provider = SpellChecker.getUserDictionaryProvider();
		if (provider != null) {
			Iterator<String> userWords = provider.getWords(SpellChecker.getCurrentLocale());
			if (userWords != null) {
				ArrayList<String> wordList = new ArrayList<>();
				while (userWords.hasNext()) {
					String word = userWords.next();
					if (word != null && word.length() > 1) {
						wordList.add(word);
					}
				}
				// Liste alphabetical sorting with the user language
				Collections.sort(wordList, Collator.getInstance());
				for (String str : wordList) {
					data.addElement(str);
				}
			}
		}
		jList1.setModel(data);
	}

	@Override
    public void dispose(){
        super.dispose();
        if (isModify) {
            // save the user dictionary
            UserDictionaryProvider provider = SpellChecker.getUserDictionaryProvider();
            if (provider != null) {
                ListModel model = jList1.getModel();
                StringBuilder builder = new StringBuilder();
                for (int i=0; i<model.getSize(); i++){
                    if( builder.length() != 0 ){
                        builder.append( '\n' );
                    }
                    builder.append(model.getElementAt(i));
                }
                provider.setUserWords(builder.toString());
            }
        }
    }

	private boolean wordExists(String tx) {
		ListModel model=jList1.getModel();
		for( int i=0; i<model.getSize(); i++){
			if (model.getElementAt(i).toString().equals(tx)) {
				jList1.setSelectedValue(model.getElementAt(i).toString(),true);
				return(true);				
			}
		}
		return(false);
	}
	private void errorMessage(String s) {
		JOptionPane.showMessageDialog(this,
			I18N.getMsg(s),
			I18N.getMsg("error"), JOptionPane.ERROR_MESSAGE);
	}

	private void jList1ValueChanged() {
		
	}

	private void searchAction() {
		String tx=txWord.getText();
		if (tx.isEmpty()) return;
		ListModel model=jList1.getModel();
		for( int i=0; i<model.getSize(); i++){
			if (model.getElementAt(i).toString().equals(tx)) {
				jList1.setSelectedValue(model.getElementAt(i).toString(),true);
				break;				
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addAction() {
		String tx=txWord.getText();
		if (tx.isEmpty()) return;
		if(tx.contains(" ")) {
			errorMessage(I18N.getMsg("jortho.word.nospace"));
			return;
		}
		if (wordExists(tx)) {
			jList1.setSelectedValue(tx, true);
		}
		((DefaultListModel) jList1.getModel()).addElement(tx);
		ArrayList<String> l = new ArrayList();
		ListModel model = jList1.getModel();
		for( int i=0; i<model.getSize(); i++){
			l.add((String) model.getElementAt(i));
		}
		Collections.sort(l, Collator.getInstance());
		DefaultListModel data = new DefaultListModel();
		for (String str : l) {
			data.addElement(str);
		}
		jList1.setModel(data);
		jList1.setSelectedValue(tx, true);
		isModify=true;
	}
	
	private void removeAction() {
		int[] selected = jList1.getSelectedIndices();
		Arrays.sort(selected);
		for (int i = selected.length - 1; i >= 0; i--) {
			((DefaultListModel) jList1.getModel()).remove(selected[i]);
			isModify = true;
			btRemove.setEnabled(false);
		}
	}
	
	private void txWordCaretUpdate() {
		if (txWord.getText().isEmpty()) {
			btSearch.setEnabled(false);
			btAdd.setEnabled(false);
		} else {
			btSearch.setEnabled(true);
			btAdd.setEnabled(true);
		}
	}

}
