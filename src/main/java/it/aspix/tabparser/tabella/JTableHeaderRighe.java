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

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/****************************************************************************
 * Estende in alcune funzionalità una JTable ma usa un modello suo 
 * {@link ContenutoTabella} e non uno fornito dall'utente.
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class JTableHeaderRighe extends JTable{

	private static final long serialVersionUID = 1L;
	HeaderRigaEditor headerRigaEditor;
	TableModelHeaderRighe modello;
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column){
		TableCellRenderer renderer;
		renderer = new HeaderRigaRenderer();
        return renderer;
    }
	
	public TableCellEditor getCellEditor(int row, int column){
		TableCellEditor editor;
		editor = headerRigaEditor;
        return editor;
	}
	
	public JTableHeaderRighe(TableModelHeaderRighe tm){
		super();
		modello = tm;
		setModel(modello);
		headerRigaEditor = new HeaderRigaEditor(HeaderRiga.possibili);
		this.setCellSelectionEnabled(false);
		this.setTableHeader(null);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
	
	public void setLarghezzaColonna(int colonna, int larghezza){
		// XXX: ha una sola colonna
		this.getColumnModel().getColumn(colonna).setMinWidth(larghezza);
		this.revalidate();
	}
    
}
