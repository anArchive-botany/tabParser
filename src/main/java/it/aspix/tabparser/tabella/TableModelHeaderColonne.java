package it.aspix.tabparser.tabella;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class TableModelHeaderColonne implements TableModel{
	
	private HeaderColonna headerColonne[];
	
	public TableModelHeaderColonne(ContenutoTabella contenutoTabella) {
		// creo soltanto un link
		headerColonne = contenutoTabella.headerColonne;
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public int getColumnCount() {
		return headerColonne.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return null;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return HeaderColonna.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return headerColonne[columnIndex];
	}

	@Override
	public void setValueAt(Object valore, int rowIndex, int columnIndex) {
		headerColonne[columnIndex].setValore( valore.toString() );
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
