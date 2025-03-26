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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/****************************************************************************
 * Estende in alcune funzionalità una JTable ma usa un modello suo 
 * {@link ContenutoTabella} e non uno fornito dall'utente.
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class TabellaComposta extends JPanel{

	private static final long serialVersionUID = 1L;
	
	JTableHeaderColonne tabellaHeaderColonne;
	JTableHeaderRighe tabellaHeaderRighe;
	public JTableDati tabellaDati;
	ContenutoTabella modello;
	
	
	public TabellaComposta(ContenutoTabella tm){
		super();
		modello = tm;
		
		// creo tutte le tabelle con i relativi scroll
		TableModelHeaderColonne mhc = new TableModelHeaderColonne(tm);
		tabellaHeaderColonne = new JTableHeaderColonne(mhc);
		JScrollPane scrollColonne = new JScrollPane(tabellaHeaderColonne);
		
		TableModelHeaderRighe mhr = new TableModelHeaderRighe(tm);
		tabellaHeaderRighe = new JTableHeaderRighe(mhr);
		JScrollPane scrollRighe = new JScrollPane(tabellaHeaderRighe);
		
		TableModelDati md = new TableModelDati(tm);
		tabellaDati = new JTableDati(md);
		JScrollPane scrollDati = new JScrollPane(tabellaDati);
        // scroll dei dati sempre visibili 		
        scrollDati.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollDati.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
		// collego gli scroll
		scrollRighe.getVerticalScrollBar().setModel(scrollDati.getVerticalScrollBar().getModel());
		scrollRighe.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollRighe.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
		scrollColonne.getHorizontalScrollBar().setModel(scrollDati.getHorizontalScrollBar().getModel());

		// altezza header delle colonne
		int altezzaHeaderColonne = (new HeaderColonnaEditor(HeaderColonna.possibili)).getComponent().getPreferredSize().height;
        tabellaHeaderColonne.setRowHeight(altezzaHeaderColonne);
        scrollColonne.setMinimumSize(new Dimension(-1,altezzaHeaderColonne+5));
        // altezza singole righe
        int altezzaRiga = (new HeaderRigaEditor(HeaderRiga.possibili)).getEditor().getPreferredSize().height;
        tabellaHeaderRighe.setRowHeight(altezzaRiga);
        tabellaDati.setRowHeight(altezzaRiga);
        // colonna degli header delle righe
        int larghezzaHeaderRiga = (new HeaderRigaEditor(HeaderRiga.possibili)).getEditor().getPreferredSize().width;
		scrollRighe.setMinimumSize(new Dimension(larghezzaHeaderRiga+5,-1));
        tabellaHeaderRighe.setLarghezzaColonna(0, larghezzaHeaderRiga);
        // collego le barre di scorrimento
        scrollColonne.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollColonne.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        		
		this.setLayout(new GridBagLayout());
        this.add(scrollColonne, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,   GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        this.add(scrollRighe,   new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER,   GridBagConstraints.VERTICAL,   new Insets(0, 0, 0, 0), 0, 0));
        this.add(scrollDati,    new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,   GridBagConstraints.BOTH,       new Insets(0, 0, 0, 0), 0, 0));

        
        setLarghezzaColonna(0,300);
		for(int i=1; i<tm.headerColonne.length; i++){
			setLarghezzaColonna(i,100);
		}

        
        this.setBackground(Color.ORANGE);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}
	
	public int getColonnaSelezionata(){
		return tabellaDati.getSelectedColumn();
	}
	
	public int getRigaSelezionata(){
		return tabellaDati.getSelectedRow();
	}
	
	public void setLarghezzaColonna(int colonna, int larghezza){
		tabellaHeaderColonne.setLarghezzaColonna(colonna, larghezza);
		tabellaDati.setLarghezzaColonna(colonna, larghezza);
		this.revalidate();
	}
	
	public void setCellaSelezionata(int riga, int colonna){
		tabellaDati.setCellaSelezionata(riga, colonna);
	}
	
	public int getSelectedRow(){
		return tabellaDati.getSelectedRow();
	}
	
	public int getSelectedColumn(){
		return tabellaDati.getSelectedColumn();
	}
	
	@Override
	public void updateUI(){
		if(tabellaHeaderColonne!=null){
			tabellaHeaderColonne.updateUI();
			tabellaHeaderRighe.updateUI();
			tabellaDati.updateUI();
		}
		super.updateUI();
		/*
	JTableHeaderColonne tabellaHeaderColonne;
	JTableHeaderRighe tabellaHeaderRighe;
	public JTableDati tabellaDati;
	ContenutoTabella modello;
		 * */
	}

}
