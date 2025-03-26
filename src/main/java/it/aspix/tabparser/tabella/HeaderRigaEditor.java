/****************************************************************************
 * Copyright 2014 studio Aspix 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 ***************************************************************************/
package it.aspix.tabparser.tabella;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/****************************************************************************
 * L'editor per l'intestazione delle righe:
 * un jPanel con due {@link JComboBox}
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class HeaderRigaEditor extends AbstractCellEditor implements TableCellEditor{
	
	private static final long serialVersionUID = 1L;
	// va creato qui per poter fornire alcune informazioni a chi organizza l'interfaccia
	private JPanel editor;
	private DefaultComboBoxModel<String> modelloGruppi;
	private JComboBox<String> gruppi;
	private DefaultComboBoxModel<String> modelloNomi;
	private JComboBox<String> nomi;
	private ArrayList<HeaderRiga> dati;
	// deve stare qui perché l'ascoltatore va disattivato prima di aggiornare
	// il primo combo e poi riattivato
	private ActionListener ascoltatoreGruppo;
	private ActionListener ascoltatoreNome;
	
	/************************************************************************
	 * @param dati che questo editor deve visualizzare
	 ***********************************************************************/
	public HeaderRigaEditor(ArrayList<HeaderRiga> dati) {
		super();
		editor = new JPanel(new GridLayout(1,2));
		modelloGruppi = new DefaultComboBoxModel<>();
		HashSet<String> giaInseriti = new HashSet<>();
		for(HeaderRiga hr: dati){
			if( !giaInseriti.contains(hr.getGruppo()) ){
				modelloGruppi.addElement(hr.getGruppo());
				giaInseriti.add(hr.getGruppo());
			}
		}
		gruppi = new JComboBox<>(modelloGruppi);
		modelloNomi = new DefaultComboBoxModel<String>();
		nomi = new JComboBox<>(modelloNomi);
		editor.add(gruppi);
		editor.add(nomi);
		this.dati = dati;
		ascoltatoreGruppo = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				aggiornaNomi();
			}
		};
		ascoltatoreNome = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}
		};
	}

	
	/************************************************************************
	 * @return un oggetto del tipo GruppoNomeDescrizione
	 ***********************************************************************/
	@Override
	public Object getCellEditorValue() {
		String gruppo = gruppi.getSelectedItem().toString();
		String nome = nomi.getSelectedItem().toString();
		for(HeaderRiga x: dati){
			if(x.getGruppo().equals(gruppo) && x.getNome().equals(nome)){
				return x;
			}
		}
		return null;
	}

	/************************************************************************
	 * @return l'editor
	 ***********************************************************************/
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		HeaderRiga gnd = (HeaderRiga) value;
		gruppi.removeActionListener(ascoltatoreGruppo);
		nomi.removeActionListener(ascoltatoreNome);
		modelloNomi.removeAllElements();
		for(int i=0;i<modelloGruppi.getSize();i++){
			if(modelloGruppi.getElementAt(i).equals(gnd.getGruppo())){
				gruppi.setSelectedIndex(i);
				for(HeaderRiga x: dati){
					if(x.getGruppo().equals(gnd.getGruppo())){
						modelloNomi.addElement(x.getNome());
						if(x.getNome().equals(gnd.getNome())){
							nomi.setSelectedIndex(modelloNomi.getSize()-1);
						}
					}
				}
				break;
			}
		}
		gruppi.addActionListener(ascoltatoreGruppo);
		nomi.addActionListener(ascoltatoreNome);
		return editor;
	}
	
	/************************************************************************
	 * Imserisce dei valori nel combo dei nomi in base al gruppo
	 * selezionato nel combo dei gruppi
	 ***********************************************************************/
	private void aggiornaNomi(){
		String gruppo = (String) gruppi.getSelectedItem();
		nomi.removeActionListener(ascoltatoreNome);
		modelloNomi.removeAllElements();
		for(HeaderRiga x: dati){
			if(x.getGruppo().equals(gruppo)){
				modelloNomi.addElement(x.getNome());
			}
		}
		nomi.addActionListener(ascoltatoreNome);
	}
	
	/************************************************************************
	 * @return l'editor implementato da questo ogetto
	 ***********************************************************************/
	public Component getEditor(){
		return editor;
	}

}
