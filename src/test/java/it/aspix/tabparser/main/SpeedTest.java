package it.aspix.tabparser.main;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

// http://jopendocument.org/downloads.html

public class SpeedTest {
	public static void main(String args[]) throws Exception{
		int NTEST = 4;
		int righe = 50;
		int colonne = 50;
		long start;
		SpreadsheetDocument documento = null;
		documento = SpreadsheetDocument.newSpreadsheetDocument();
		Table tabella;
		
		
		tabella = documento.appendSheet("dati");
		tabella.appendColumns(colonne);
		tabella.appendRows(righe);
		for(int nt=0; nt<NTEST; nt++){
			start = System.currentTimeMillis();
			for(int r=0;r<righe;r++){
				for(int c=0; c<colonne; c++){
					tabella.getCellByPosition(r, c).setStringValue("ciao");
				}
			}
			System.out.println(nt+" tempo:"+(System.currentTimeMillis()-start));
		}
		
		tabella = documento.appendSheet("dati2");
		tabella.appendColumns(colonne);
		tabella.appendRows(righe);		
		for(int nt=0; nt<NTEST; nt++){
			start = System.currentTimeMillis();
			for(int r=0;r<righe;r++){
				for(int c=0; c<colonne; c++){
					tabella.getCellByPosition(r, c).setStringValue("ciao");
				}
			}
			System.out.println(nt+" tempo:"+(System.currentTimeMillis()-start));
		}
		
		tabella = documento.appendSheet("dati3");
		tabella.appendColumns(colonne);
		tabella.appendRows(righe);		
		Row riga;
		for(int nt=0; nt<NTEST; nt++){
			start = System.currentTimeMillis();
			for(int r=0;r<righe;r++){
				riga = tabella.getRowByIndex(r);
				for(int c=0; c<colonne; c++){
					riga.getCellByIndex(c).setStringValue("ciao");
				}
			}
			System.out.println(nt+" tempo:"+(System.currentTimeMillis()-start));
		}
		
		
	}
}
