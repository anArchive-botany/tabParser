package it.aspix.tabparser.tabella;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class TableModelHeaderRighe implements TableModel{
	
	private HeaderRiga headerRighe[];
	
	public TableModelHeaderRighe(ContenutoTabella contenutoTabella) {
		// creo soltanto un link
		headerRighe = contenutoTabella.headerRighe;
	}

	@Override
	public int getRowCount() {
		return headerRighe.length;
	}

	@Override
	public int getColumnCount() {
		return 1;
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
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return headerRighe[rowIndex];
	}

	@Override
	public void setValueAt(Object valore, int rowIndex, int columnIndex) {
		headerRighe[rowIndex] = (HeaderRiga) valore;
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
