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
package it.aspix.tabparser.inputassistito;

import it.aspix.tabparser.tabella.ContenutoTabella;
import it.aspix.tabparser.tabella.DatoTabella;
import it.aspix.sbd.ValoreEnumeratoDescritto;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/****************************************************************************
 * Permette di inserire un valore scegliendolo in un lenco
 * l'elenco può essere recuperato da SimpleBotanicalData
 * oppure essere passato come parametro
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public abstract class ProprietaDescrittaEditorCallback extends JDialog implements Editor{
	
	private static final long serialVersionUID = 1L;
	DefaultComboBoxModel<ValoreEnumeratoDescritto> contenuto = new DefaultComboBoxModel<>();
	JComboBox<ValoreEnumeratoDescritto> combo = new JComboBox<ValoreEnumeratoDescritto>(contenuto);
	boolean chiusoConOK = true;
	
	public ProprietaDescrittaEditorCallback(){
		String titolo = getTitolo();
		ArrayList<ValoreEnumeratoDescritto> valori = getValori();
		for(ValoreEnumeratoDescritto ved: valori){
			contenuto.addElement(ved);
		}
		JLabel eValore = new JLabel("valore:");
		JButton ok = new JButton("ok");
		ok.addActionListener(e->{chiusoConOK=true;ProprietaDescrittaEditorCallback.this.setVisible(false);});
		JButton annulla = new JButton("annulla");
		annulla.addActionListener(e->{chiusoConOK=false;ProprietaDescrittaEditorCallback.this.setVisible(false);});
		
		JPanel pannello = new JPanel(new GridBagLayout());
		
		pannello.add(eValore, new GridBagConstraints(0,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pannello.add(combo,   new GridBagConstraints(1,1,2,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pannello.add(annulla, new GridBagConstraints(1,2,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		pannello.add(ok,      new GridBagConstraints(2,2,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
				
		this.add(pannello);
		this.setModal(true);
		this.setTitle(titolo);
		pannello.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		this.pack();
	}

	
	// interfaccia Editor (ma la dichiarano le subclassi)
	@Override
	public void setValore(ContenutoTabella ct, int riga, int colonna) {
		// non importa quale valore contiene la tabella
	}

	@Override
	/************************************************************************
	 * @return un DatoTabella che contiene il valore scelto come dato
	 * e la descrizione come tip
	 ***********************************************************************/
	public DatoTabella getValore() {
		if(chiusoConOK){
			DatoTabella dato = new DatoTabella(((ValoreEnumeratoDescritto)(combo.getSelectedItem())).enumerato, 
					((ValoreEnumeratoDescritto)(combo.getSelectedItem())).descrizione, null);
			return dato;
		}else{
			return null;
		}
	}

	@Override
	public JDialog getDialogo() {
		return this;
	}
	
	@Override
	public String getVoceMenu(){
		return getTitolo();
	}
	
	public abstract String getTitolo();
	
	public abstract ArrayList<ValoreEnumeratoDescritto> getValori();

}
