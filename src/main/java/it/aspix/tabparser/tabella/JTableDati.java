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


import it.aspix.tabparser.gui.WiderDropDownCombo;
import it.aspix.sbd.ValoreEnumeratoDescritto;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/****************************************************************************
 * Estende in alcune funzionalità una JTable ma usa un modello suo 
 * {@link ContenutoTabella} e non uno fornito dall'utente.
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class JTableDati extends JTable{

	private static final long serialVersionUID = 1L;
	
	TableModelDati modello;
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column){
		TableCellRenderer renderer;
		
		renderer = new DatoTabellaRenderer();
        return renderer;
    }
	
	public TableCellEditor getCellEditor(int row, int column){
		TableCellEditor editor;
		if(modello.contenutoTabella.headerRighe[row].describedPath!=null && 
				modello.contenutoTabella.headerRighe[row].describedPath.valoriAmmissibili!=null &&
				modello.contenutoTabella.headerColonne[column].equals(HeaderColonna.RILIEVO)
				){
			// casella con tipo enumerato
			WiderDropDownCombo<String> comboBox = new WiderDropDownCombo<>();
			comboBox.addItem(""); // serve per poter pulire il campo
			for(ValoreEnumeratoDescritto x: modello.contenutoTabella.headerRighe[row].describedPath.valoriAmmissibili){
				comboBox.addItem(x.enumerato);
			}
			comboBox.setWide(true);
			comboBox.setSelectedItem(modello.contenutoTabella.dati[row][column].dato.toString());
			return new DefaultCellEditor(comboBox);
		}else{
			// normale casella di testo
			editor = super.getCellEditor(row, column);
		}
        return editor;
	}
	
	public JTableDati(TableModelDati tm){
		super();
		modello = tm;
		setModel(modello);
		this.setCellSelectionEnabled(true);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.setTableHeader(null);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public void setLarghezzaColonna(int colonna, int larghezza){
		this.getColumnModel().getColumn(colonna).setPreferredWidth(larghezza);
		this.revalidate();
	}
	
	public void setCellaSelezionata(int riga, int colonna){
		// la prima riga/colonna per l'utente sono le intestazioni
		setRowSelectionInterval(riga, riga);
		setColumnSelectionInterval(colonna, colonna);
		Rectangle rect = getCellRect(riga, colonna, true);
		JViewport viewport = (JViewport)getParent();
		Point posizione = new Point(rect.x>100 ? rect.x-100 : rect.x, rect.y>50 ? rect.y-50 : rect.y);
	    viewport.setViewPosition(posizione);
	}

}
