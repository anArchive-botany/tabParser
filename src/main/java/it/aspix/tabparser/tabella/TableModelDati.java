package it.aspix.tabparser.tabella;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class TableModelDati implements TableModel{
	
	// manntengo anche gli header perché per determinare il rendering e gli editor
	// mi servono informazioni sugli header
	protected ContenutoTabella contenutoTabella;
	
	public TableModelDati(ContenutoTabella contenutoTabella) {
		// creo soltanto un link
		this.contenutoTabella = contenutoTabella;
	}

	@Override
	public int getRowCount() {
		return contenutoTabella.dati.length;
	}

	@Override
	public int getColumnCount() {
		return contenutoTabella.dati[0].length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return null;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Object.class; // TODO: mettere qualcosa di più specifico
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return contenutoTabella.dati[rowIndex][columnIndex].dato instanceof String; 
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return contenutoTabella.dati[rowIndex][columnIndex];
	}

	@Override
	public void setValueAt(Object valore, int rowIndex, int columnIndex) {
		contenutoTabella.dati[rowIndex][columnIndex] = new DatoTabella( valore );
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
	}

}
