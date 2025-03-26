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
package it.aspix.tabparser.main;

import it.aspix.archiver.nucleo.Dispatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

/****************************************************************************
 * Contiene un unico metodo accessibile: loadTable che permette di caricare 
 * una tabella xls, xlsx o odt
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class TableLoader {
	/************************************************************************
	 * Carica un file utilizzando POI o ODF Toolkit
	 * @param f nome del file da caricare
	 * @return un modello da usare in una JTable
	 * @throws Exception
	 ***********************************************************************/
	public static ArrayList<String[][]> loadTable(File f) throws Exception{
		String estensione = f.toString();
		ArrayList<String[][]> tabelle = null;
		
		estensione = estensione.substring(estensione.lastIndexOf('.')+1);
		if(estensione.equalsIgnoreCase("xls") || estensione.equalsIgnoreCase("xlsx")){
			tabelle = loadTableXls(f, estensione);
		}
		if(estensione.equalsIgnoreCase("ods")){
			tabelle = loadTableOds(f);
		}
		// controllo per eventuali caselle null
		for(String[][] contenutoTabella: tabelle){
			for(int riga=0 ; riga<contenutoTabella.length ; riga++){
				for(int colonna=0 ; colonna<contenutoTabella[0].length; colonna++){
					if(contenutoTabella[riga][colonna]==null){
						contenutoTabella[riga][colonna] = "";
					}
				}
			}
		}
		return tabelle;
	}
	
	/************************************************************************
	 * Legge un file utilizzando POI
	 * @param f nome del file
	 * @param estensione usata per discriminare tra vecchi e nuovi file di excel
	 * @param pagina il numero della pagina da leggere
	 * @return 
	 * @throws IOException
	 ***********************************************************************/
	private static ArrayList<String[][]> loadTableXls(File f, String estensione) throws IOException{
		ArrayList<String[][]> cartella = new ArrayList<>();
		InputStream s;
		Sheet foglio;
		Row riga;
		Cell cella;
		s = new FileInputStream(f);
		int max=0;
		double d;
		Workbook wb;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		int ultimaRigaUtile = 0;
		int ultimaColonnaUtile = 0;
		String matrice[][];
		
		// il workbook cambia in funzione dell'estensione
		if(estensione.equals("xls")){
			wb = new HSSFWorkbook(s);
		}else{
			wb = new XSSFWorkbook(s);
		}
		// mi interessa soltnato il primo foglio
		foglio = wb.getSheetAt(0);
		

		// per calcolare la larghezza del foglio guardo le prime 20 righe
		// non si può usare semplicemnete getLastRowNum() perché conta anche le celle vuote
		int maxSullaRiga;
		for(int iRiga=0; iRiga<foglio.getLastRowNum(); iRiga++){
			System.err.println(iRiga+" "+foglio.getLastRowNum());
			riga = foglio.getRow(iRiga);
			maxSullaRiga=0;
			for(int i=0; riga!=null && i<riga.getLastCellNum(); i++){
				if(riga.getCell(i)!=null && riga.getCell(i).toString().length()>0){
					maxSullaRiga = i;
				}
			}
			if(maxSullaRiga>max){
				max=maxSullaRiga;
			}
		}
		
		ultimaRigaUtile = foglio.getLastRowNum();
		ultimaColonnaUtile = max;
		
		for(int iFoglio = 0; iFoglio<wb.getNumberOfSheets(); iFoglio++){
			// carico i dati
			foglio = wb.getSheetAt(iFoglio);
			matrice = new String[ultimaRigaUtile+1][ultimaColonnaUtile+1];
			for(int i=0; i<=foglio.getLastRowNum(); i++){
				riga = foglio.getRow(i);
				if(riga!=null){
					for(int j=0; j<=max; j++){
						cella = riga.getCell(j);
						if(cella!=null){
							cella.getCellType();
							CellType peppe = cella.getCellType();
							switch(cella.getCellType()){
							case STRING:
								matrice[i][j] = cella.getStringCellValue();
								break;
							case NUMERIC:
								// in questo caso cadono anche i tipi data
							    if (DateUtil.isCellDateFormatted(cella)) {
							    	matrice[i][j] = sdf.format(cella.getDateCellValue());
							    } else {
									d = cella.getNumericCellValue();
									if(d != (int)d){
										matrice[i][j] = ""+cella.getNumericCellValue();
									}else{
										matrice[i][j] = ""+ ( (int) cella.getNumericCellValue() );
									}
							    }
								break;
							case BLANK:
								matrice[i][j] = "";
								break;
							default:
								Dispatcher.consegna(null, new Exception("Non gestisco la cella di tipo "+cella.getCellType()));
							}
							
						}
					}
				}
			}
			cartella.add(matrice);
		}
		return cartella;
	}

	
	/************************************************************************
	 * Legge un file utilizzando ODF Toolkit
	 * @param f nome del file
	 * @param pagina il numero della pagina da leggere
	 * @return
	 * @throws Exception
	 ***********************************************************************/
	private static ArrayList<String[][]> loadTableOds(File f) throws Exception{
		ArrayList<String[][]> cartella = new ArrayList<>();
		SpreadsheetDocument documento;
		int colonneDaControllare;
		int righeDaControllare;
		int ultimaRigaUtile = 0;
		int ultimaColonnaUtile = 0;
		org.odftoolkit.simple.table.Row riga;
		int lun;
		Table foglio;
		String matrice[][];
		
		documento = SpreadsheetDocument.loadDocument(f);
		
		// dimensiono il foglio in base al contenuto della prima pagina
		foglio = documento.getSheetByIndex(0);

		// è possibile che il numero riornato sia molto più grande delle righe/colonne
		// realmente utili, per queste si cercano una specie di bounding box
		colonneDaControllare = foglio.getColumnCount()<3 ? foglio.getColumnCount() : 3;
		for(int i=0; i<foglio.getRowCount() ; i++){
			riga = foglio.getRowByIndex(i);
			lun = 0;	// la riga non contiene nulla
			for(int j=0 ; j<colonneDaControllare ; j++){
				lun += riga.getCellByIndex(j).getDisplayText().length(); // sommo la lunghezza della cella
			}
			if(lun!=0){	
				// se la riga contiene qualcosa allora è utile
				ultimaRigaUtile = i;
			}
			if(i-ultimaRigaUtile>3){
				// XXX: se trovo 3 righe vuote smetto di cercare 
				break;
			}
		}
		righeDaControllare = foglio.getRowCount()<2 ? foglio.getRowCount() : 2;
		for(int i=0; i<foglio.getColumnCount() ; i++){
			lun = 0;
			for(int j=0 ; j<righeDaControllare ; j++){
				lun += foglio.getRowByIndex(j).getCellByIndex(i).getDisplayText().length();
			}
			if(lun!=0){
				// se la colonna contiene qualcosa è utile
				ultimaColonnaUtile = i;
			}
			if(i-ultimaColonnaUtile>3){
				// XXX: se trovo 3 colonne vuote smetto di cercare
				break;
			}
		}
		// a questo punto righeUtili e colonneUtili contengono i valori che servono
		
		for(int iFoglio=0; iFoglio<documento.getSheetCount(); iFoglio++){
			foglio = documento.getSheetByIndex(iFoglio);
			matrice = new String[ultimaRigaUtile+1][ultimaColonnaUtile+1];
			for(int iRiga=0; iRiga<=ultimaRigaUtile ; iRiga++){
				riga = foglio.getRowByIndex(iRiga);
				for(int iColonna=0 ; iColonna<=ultimaColonnaUtile; iColonna++){
					matrice[iRiga][iColonna] = riga.getCellByIndex(iColonna).getDisplayText();
				}
			}
			cartella.add(matrice);
		}
		
		return cartella;
	}
}
