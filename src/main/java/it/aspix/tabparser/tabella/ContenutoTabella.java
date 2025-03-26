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

import it.aspix.archiver.dialoghi.ComunicazioneEccezione;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;


/****************************************************************************
 * Classe responsabile della memorizzazione dei dati, il modello
 * contiene sia gli header delle righe che quelli delle colonne che i dati,
 * per lavorare con i dati bisogna usare i metodi
 * getValore(int riga, int colonna), setValore(int riga, int colonna, Object valore), 
 * getNumeroColonne(), getNumeroRighe().
 * 
 * I metodi di TableModel sono presenti per interfacciarsi con JTable
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class ContenutoTabella implements Cloneable{
	
	private static final String HEADER_PRESENTI = "ModificA ManualE SconsigliatA";
	
	public HeaderColonna headerColonne[];
	public HeaderRiga headerRighe[];
	public DatoTabella dati[][];
	
	private static String SEPARATORE_DATI_SPECIE = "#@#";
	
	public ContenutoTabella clone(){
		int iRiga, iColonna;
		ContenutoTabella nuova = new ContenutoTabella();
		nuova.headerColonne = new HeaderColonna[headerColonne.length];
		for(iColonna=0; iColonna<headerColonne.length; iColonna++){
			nuova.headerColonne[iColonna] = headerColonne[iColonna].clone();
		}
		nuova.headerRighe = new HeaderRiga[headerRighe.length];
		for(iRiga=0; iRiga<headerRighe.length; iRiga++){
			nuova.headerRighe[iRiga] = headerRighe[iRiga].clone();
		}
		nuova.dati = new DatoTabella [dati.length][dati[0].length];
		for(iRiga=0; iRiga<dati.length; iRiga++){
			for(iColonna=0;iColonna<dati[0].length; iColonna++){
				nuova.dati[iRiga][iColonna] = dati[iRiga][iColonna].clone();
			}
		}
		return nuova;
	}
	
	protected ContenutoTabella(){
		// serve quando si clona
	}
	
	private static HashMap<String, Level> CL = new HashMap<>();
	static{
		CL.put("null", null);
		CL.put("CONFIG", Level.CONFIG);
		CL.put("FINE", Level.FINE);
		CL.put("FINER", Level.FINER);
		CL.put("FINEST", Level.FINEST);
		CL.put("INFO", Level.INFO);
		CL.put("OFF", Level.OFF);
		CL.put("SEVERE", Level.SEVERE);
		CL.put("WARNING", Level.WARNING);
	}
	
	public ContenutoTabella(ArrayList<String[][]> cartella){
		String[][] foglioDati = cartella.get(0);
		
		if( foglioDati[0][0].equals(HEADER_PRESENTI)){
			// la tabella che sto caricando è un dump di una elaborazione precedente
			String[][] foglioLevels = cartella.get(1);
			String[][] foglioTips = cartella.get(2);
			String[][] foglioSpecie = cartella.get(3);
			DatoTabella d;
			SurveyedSpecie ss;
			
			costruisci(foglioDati.length-1, foglioDati[0].length-1);
			for(int i=0; i<headerColonne.length; i++){
				headerColonne[i].setValore( foglioDati[0][i+1] );
			}
			for(int i=0; i<headerRighe.length; i++){
				try {
					headerRighe[i] = HeaderRiga.cercaHeader( foglioDati[i+1][0] );
				} catch (Exception e) {
					ComunicazioneEccezione ce = new ComunicazioneEccezione(e);
		        	ce.setVisible(true); 
				}
			}
			for(int i=0; i<dati.length; i++){
				for(int j=0; j<dati[0].length; j++){
					d = new DatoTabella( foglioDati[i+1][j+1] );
					if(foglioTips!=null){
						d.tip = foglioTips[i+1][j+1];
					}
					if(foglioLevels!=null){
						d.livello = CL.get(foglioLevels[i+1][j+1]);
					}
					if(foglioSpecie!=null ){
						// in ogni caso si tenta lo split, sarà poi il numero di parti risultanti
						// a indicare se si tratta di una specie rilevata o meno
						ss = new SurveyedSpecie();
						String parti[] = foglioSpecie[i+1][j+1].split(SEPARATORE_DATI_SPECIE,-1);
						if(parti.length==5){
							ss.setSpecieRefName(parti[0]);
							ss.setDetermination(parti[1]);
							ss.setNote(parti[2]);
							ss.setJuvenile(parti[3]);
							ss.setIncidence(parti[4]);
							d.dato = ss;
						}
					}
					dati[i][j] = d;
				}
			}
		}else{
			costruisci(foglioDati.length, foglioDati[0].length);
			for(int i=0; i<foglioDati.length; i++){
				for(int j=0; j<foglioDati[0].length; j++){
					dati[i][j] = new DatoTabella( foglioDati[i][j] );
				}
			}
		}
	}
	
	private void costruisci(int righe, int colonne){
		dati = new DatoTabella [righe][colonne];
		headerColonne = new HeaderColonna[colonne];
		for(int i=0;i<colonne; i++){
			headerColonne[i] = HeaderColonna.NON_USARE.clone();
		}
		headerRighe = new HeaderRiga[righe];
		for(int i=0; i<righe; i++){
			headerRighe[i] = HeaderRiga.NON_USARE.clone();
		}
	}
		
	// ==============================================================================================================
	//                                          metodi per recuperare i dati
	// ==============================================================================================================
	
	/************************************************************************
	 * Recupera i dati
	 * @param riga
	 * @param colonna
	 * @return
	 ***********************************************************************/
	public DatoTabella getValore(int riga, int colonna){
		return dati[riga][colonna];
	}
	
	/************************************************************************
	 * Scrive un dato
	 * @param riga
	 * @param colonna
	 * @param valore
	 ***********************************************************************/
	public void setValore(int riga, int colonna, Object valore){
		dati[riga][colonna] = valore instanceof DatoTabella ? (DatoTabella)valore : new DatoTabella( valore );
	}
	
	/************************************************************************
	 * @return il numero di colonne di dati presenti
	 ***********************************************************************/
	public int getNumeroColonne(){
		return dati[0].length;
	}
	
	/************************************************************************
	 * @return il numero di righe di dati presenti
	 ***********************************************************************/
	public int getNumeroRighe(){
		return dati.length;
	}

	// ==============================================================================================================
	//                           metodi per l'analisi degli header di righe e colonne
	// ==============================================================================================================
	
	public boolean isSenzaDefinizioni(){
		int p;
		for(p=0;p<headerColonne.length;p++){
			if(!headerColonne[p].equals(HeaderColonna.NON_USARE)){
				return false;
			}
		}
		for(p=0;p<headerRighe.length;p++){
			if(!headerRighe[p].equals(HeaderRiga.NON_USARE)){
				return false;
			}
		}
		return true;
	}
		
	/************************************************************************
	 * @param tipo da cercare
	 * @return l'indice della colonna che contiene tipo o -1 se non esiste
	 ***********************************************************************/
	public int getColonnaPerTipo(HeaderColonna tipo){
		int p;
		for(p=0;p<headerColonne.length && !headerColonne[p].equals(tipo);p++);
		return p==headerColonne.length ? -1 :  p;
	}
	
	/************************************************************************
	 * Partendo dalla riga indicata cerca indietro e poi in avanti per trovare
	 * gli header richiesti. Si ferma quando trova una riga che non è tra quelle richieste
	 * @param partenza
	 * @param req
	 * @return un arrai i cui elementi sono gli indici corrispondenti a req,
	 * contiene -1 se non è stato trovato l'header
	 ***********************************************************************/
	public int[] cercaGruppo(int partenza, HeaderRiga[] req){
		int iRiga;
		int iHeader;
		int risposta[] = new int[req.length];
		
		// pulisco il vettore di partenza
		for(int i=0;i<risposta.length;i++){
			risposta[i] = -1;
		}
		// controllo gli elementi che precedono la partenza
		iRiga=partenza;
		while(iRiga>=0){
			for(iHeader=0; iHeader<req.length; iHeader++){
				if(headerRighe[iRiga].equals(req[iHeader])){
					risposta[iHeader] = iRiga;
					break;
				}
			}
			iRiga--;
		}
		// controllo gli elementi successivi alla partenza
		iRiga=partenza+1;
		while(iRiga<headerRighe.length){
			for(iHeader=0; iHeader<req.length; iHeader++){
				if(headerRighe[iRiga].equals(req[iHeader])){
					risposta[iHeader] = iRiga;
					break;
				}
			}
			iRiga++;
		}
		return risposta;
	}
	
	/************************************************************************
	 * @return l'indice della riga che contiene la prima specie
	 ***********************************************************************/
	public int getRigaPrimaSpecie(){
		int p;
		for(p=0;p<headerRighe.length && !headerRighe[p].equals(HeaderRiga.SPECIE);p++);
		return p;
	}
	
	/************************************************************************
	 * @return un elenco degli strati contenuti nella tabella, se non esiste
	 * la colonna degli strati ritorna ["0"]
	 ***********************************************************************/
	public String[] getStrati(){
		ArrayList<String> elenco = new ArrayList<>();
		int iStrati = getColonnaPerTipo(HeaderColonna.STRATI);
		
		if(iStrati!=-1){
			String precedente = "fittizio";
			for(int iRiga=0 ; iRiga<headerRighe.length ; iRiga++){
				if(!dati[iRiga][iStrati].dato.toString().equals(precedente)){
					precedente = dati[iRiga][iStrati].dato.toString();
					elenco.add(precedente);
				}
			}
		}else{
			elenco.add("0");
		}
		return elenco.toArray(new String[0]);
	}
	
	/************************************************************************
	 * Aggiunge una riga impostando l'header a NON_USARE
	 * @param index
	 * @throws IndexOutOfBoundsException
	 ***********************************************************************/
	public void addRow(int index) throws IndexOutOfBoundsException{
		if(index<0){
			throw new IndexOutOfBoundsException("Non posso creare la riga "+index);
		}
		int i;
		// le intestazioni delle colonne non serve che vengano modificate
		// copio le intestazioni delle righe
		HeaderRiga headerRigheNuovi[] = new HeaderRiga[headerRighe.length+1];
		for(i=0;i<index;i++){
			headerRigheNuovi[i] = headerRighe[i];
		}
		headerRigheNuovi[index] = HeaderRiga.NON_USARE.clone();
		for(i=index+1;i<headerRigheNuovi.length;i++){
			headerRigheNuovi[i] = headerRighe[i-1];
		}
		headerRighe = headerRigheNuovi;
		// copio i dati
		DatoTabella dataNuovi[][] = new DatoTabella[dati.length+1][];
		for(i=0;i<index;i++){
			dataNuovi[i] = dati[i];
		}
		dataNuovi[index] = new DatoTabella[dati[0].length];
		for(i=0;i<dati[0].length;i++){
			dataNuovi[index][i] = new DatoTabella("");
		}
		for(i=index+1;i<dataNuovi.length;i++){
			dataNuovi[i] = dati[i-1];
		}
		dati = dataNuovi;
	}
	
	/************************************************************************
	 * Elimina una riga
	 * @param index
	 * @throws IndexOutOfBoundsException
	 ***********************************************************************/
	public void deleteRow(int index) throws IndexOutOfBoundsException{
		if(index<0){
			throw new IndexOutOfBoundsException("Non posso eliminare la riga "+index);
		}
		int i;
		// le intestazioni delle colonne non serve che vengano modificate
		// copio le intestazioni delle righe
		HeaderRiga headerRigheNuovi[] = new HeaderRiga[headerRighe.length-1];
		for(i=0;i<index;i++){
			headerRigheNuovi[i] = headerRighe[i];
		}
		for(i=index;i<headerRigheNuovi.length;i++){
			headerRigheNuovi[i] = headerRighe[i+1];
		}
		headerRighe = headerRigheNuovi;
		// copio i dati
		DatoTabella dataNuovi[][] = new DatoTabella[dati.length-1][];
		for(i=0;i<index;i++){
			dataNuovi[i] = dati[i];
		}
		for(i=index;i<dataNuovi.length;i++){
			dataNuovi[i] = dati[i+1];
		}
		dati = dataNuovi;
	}
	
	/************************************************************************
	 * Aggiunge una colonna impostando l'header a NON_USARE
	 * @param index
	 * @throws IndexOutOfBoundsException
	 ***********************************************************************/
	public void addColumn(int index) throws IndexOutOfBoundsException{
		if(index<0){
			throw new IndexOutOfBoundsException("Non posso creare la colonna "+index);
		}
		int i,j;
		// le intestazioni delle righe non serve che vengano modificate
		// copio le intestazioni delle colonne
		HeaderColonna headerColonneNuovi[] = new HeaderColonna[headerColonne.length+1];
		for(i=0;i<index;i++){
			headerColonneNuovi[i] = headerColonne[i];
		}
		headerColonneNuovi[index] = HeaderColonna.NON_USARE.clone();
		for(i=index+1;i<headerColonneNuovi.length;i++){
			headerColonneNuovi[i] = headerColonne[i-1];
		}
		headerColonne = headerColonneNuovi;
		// copio i dati
		DatoTabella datiNuovi[][] = new DatoTabella[dati.length][];
		for(i=0;i<datiNuovi.length;i++){
			datiNuovi[i] = new DatoTabella[dati[0].length+1];
			for(j=0; j<index; j++){
				datiNuovi[i][j] = dati[i][j];
			}
			datiNuovi[i][index] = new DatoTabella( "", null, null);
			for(j=index+1;j<datiNuovi[0].length;j++){
				datiNuovi[i][j] = dati[i][j-1];
			}
		}
		dati = datiNuovi;		
	}
	
	/************************************************************************
	 * Non vengono salvati livelli di errore e tooltip
	 * @return
	 ***********************************************************************/
	public SpreadsheetDocument getODF(){
		SpreadsheetDocument documento = null;
		long start;
		try {
			documento = SpreadsheetDocument.newSpreadsheetDocument();
			// inserisco tutti i dati generici nella prima tabella
			while(documento.getSheetCount()>0){
				documento.removeSheet(0);
			}
			Table t = documento.appendSheet("dati"); 
			t.appendRows(dati.length+1);
			t.appendColumns(dati[0].length+1);
			
			// foglio dei dati
			start = System.currentTimeMillis();
			t.getCellByPosition(0,0).setStringValue(HEADER_PRESENTI);
			t.getCellByPosition(0,0).setCellBackgroundColor(Color.RED);
			for(int i=0;i<headerColonne.length; i++){
				t.getCellByPosition(i+1, 0).setStringValue(headerColonne[i].getValore());
			}
			for(int i=0; i<headerRighe.length; i++){
				t.getCellByPosition(0,i+1).setStringValue(headerRighe[i].getGruppo()+"."+headerRighe[i].getNome());
			}
			for(int iRiga=0;iRiga<dati.length;iRiga++){
				for(int iColonna=0;iColonna<dati[0].length; iColonna++){
					t.getCellByPosition(iColonna+1, iRiga+1).setStringValue(dati[iRiga][iColonna].toString());
				}
			}
			System.out.println("tempo per dati: "+(System.currentTimeMillis()-start));
			
			// i livelli di errore
			start = System.currentTimeMillis();
			Table tLivelli = documento.appendSheet("livelli");
			Table tTips = documento.appendSheet("tips");
			tLivelli.appendColumns(dati[0].length+1);
			tLivelli.appendRows(dati.length+1);
			tTips.appendColumns(dati[0].length+1);
			tTips.appendRows(dati.length+1);
			System.out.println("tempo per colonne livelli di errore: "+(System.currentTimeMillis()-start));
			
			start = System.currentTimeMillis();
			for(int iRiga=0; iRiga<dati.length; iRiga++){
				for(int iColonna=0; iColonna<dati[0].length; iColonna++){
					tLivelli.getCellByPosition(iColonna+1, iRiga+1).setStringValue(dati[iRiga][iColonna].livello==null ? "null" : dati[iRiga][iColonna].livello.getName());
					tTips.getCellByPosition(iColonna+1, iRiga+1).setStringValue(dati[iRiga][iColonna].tip);
				}
			}
			System.out.println("tempo per dati livelli di errore: "+(System.currentTimeMillis()-start));
			
			// le informazioni sulle specie rilevate le inserisco in una tabella a parte
			start = System.currentTimeMillis();
			Table t2 = documento.appendSheet("specie rilevate");
			int indiceSpecie = getColonnaPerTipo(HeaderColonna.DEFINIZIONI);
			SurveyedSpecie s;
			for(int iRiga=0;iRiga<dati.length;iRiga++){
				if(headerRighe[iRiga].equals(HeaderRiga.SPECIE)){
					if(dati[iRiga][indiceSpecie].dato instanceof SurveyedSpecie){
						s = (SurveyedSpecie) dati[iRiga][indiceSpecie].dato;
						t2.getCellByPosition(indiceSpecie+1, iRiga+1).setStringValue(
								s.getSpecieRefName()+SEPARATORE_DATI_SPECIE+
								(s.getDetermination()!=null?s.getDetermination():"")+SEPARATORE_DATI_SPECIE+
								(s.getNote()!=null?s.getNote():"")+SEPARATORE_DATI_SPECIE+
								(s.getJuvenile()!=null?s.getJuvenile():"")+SEPARATORE_DATI_SPECIE+
								(s.getIncidence()!=null?s.getIncidence():"")
						);
						/*
						t2.getCellByPosition(0, iRiga+1).setStringValue(s.getSpecieRefName());
						t2.getCellByPosition(1, iRiga+1).setStringValue(s.getDetermination());
						t2.getCellByPosition(2, iRiga+1).setStringValue(s.getNote());
						t2.getCellByPosition(3, iRiga+1).setStringValue(s.getJuvenile());
						t2.getCellByPosition(4, iRiga+1).setStringValue(s.getIncidence());
						*/
						// FIXME: tanto vale che li metti tutti!
					}
				}
			}
			System.out.println("tempo per specie: "+(System.currentTimeMillis()-start));
		} catch (Exception e) {
			ComunicazioneEccezione ce = new ComunicazioneEccezione(e);
        	ce.setVisible(true); 
		}
		return documento;
	}
	
	/************************************************************************
	 * Non vengono salvati livelli di errore e tooltip
	 * @return
	 ***********************************************************************/
	public HSSFWorkbook getXLS(){
		HSSFWorkbook documento = null;
		long start;
		try {
			documento = new HSSFWorkbook();
			// inserisco tutti i dati generici nella prima tabella
			HSSFSheet foglioDati = documento.createSheet("dati");
			HSSFRow riga;
			
			// foglio dei dati
			start = System.currentTimeMillis();
			riga = foglioDati.createRow(0);
			riga.createCell(0).setCellValue(HEADER_PRESENTI);
			
			for(int i=0;i<headerColonne.length; i++){
				riga.createCell(i+1).setCellValue(headerColonne[i].getValore());
			}
			for(int i=0; i<headerRighe.length; i++){
				riga = foglioDati.createRow(i+1);
				riga.createCell(0).setCellValue(headerRighe[i].getGruppo()+"."+headerRighe[i].getNome());
			}
			for(int iRiga=0;iRiga<dati.length;iRiga++){
				for(int iColonna=0;iColonna<dati[0].length; iColonna++){
					foglioDati.getRow(iRiga+1).createCell(iColonna+1).setCellValue(dati[iRiga][iColonna].toString());
				}
			}
			System.out.println("tempo per dati: "+(System.currentTimeMillis()-start));
			
			// i livelli di errore
			start = System.currentTimeMillis();
			HSSFSheet foglioLivelli = documento.createSheet("livelli");
			HSSFSheet foglioTips = documento.createSheet("tips");
			HSSFRow rigaLivelli;
			HSSFRow rigaTips;
			for(int iRiga=0; iRiga<dati.length; iRiga++){
				rigaLivelli = foglioLivelli.createRow(iRiga+1);
				rigaTips = foglioTips.createRow(iRiga+1);
				for(int iColonna=0; iColonna<dati[0].length; iColonna++){
					rigaLivelli.createCell(iColonna+1).setCellValue(dati[iRiga][iColonna].livello==null ? "null" : dati[iRiga][iColonna].livello.getName());
					rigaTips.createCell(iColonna+1).setCellValue(dati[iRiga][iColonna].tip);
				}
			}
			System.out.println("tempo per dati livelli di errore: "+(System.currentTimeMillis()-start));
			
			// le informazioni sulle specie rilevate le inserisco in una tabella a parte
			start = System.currentTimeMillis();
			HSSFSheet foglioSpecie = documento.createSheet("specie rilevate");
			HSSFRow rigaSpecie;
			int indiceSpecie = getColonnaPerTipo(HeaderColonna.DEFINIZIONI);
			SurveyedSpecie s;
			for(int iRiga=0;iRiga<dati.length;iRiga++){
				if(headerRighe[iRiga].equals(HeaderRiga.SPECIE)){
					if(dati[iRiga][indiceSpecie].dato instanceof SurveyedSpecie){
						s = (SurveyedSpecie) dati[iRiga][indiceSpecie].dato;
						rigaSpecie = foglioSpecie.createRow(iRiga+1);
						rigaSpecie.createCell(indiceSpecie+1).setCellValue(
								s.getSpecieRefName()+SEPARATORE_DATI_SPECIE+
								(s.getDetermination()!=null?s.getDetermination():"")+SEPARATORE_DATI_SPECIE+
								(s.getNote()!=null?s.getNote():"")+SEPARATORE_DATI_SPECIE+
								(s.getJuvenile()!=null?s.getJuvenile():"")+SEPARATORE_DATI_SPECIE+
								(s.getIncidence()!=null?s.getIncidence():"")
						);
						/*
						t2.getCellByPosition(0, iRiga+1).setStringValue(s.getSpecieRefName());
						t2.getCellByPosition(1, iRiga+1).setStringValue(s.getDetermination());
						t2.getCellByPosition(2, iRiga+1).setStringValue(s.getNote());
						t2.getCellByPosition(3, iRiga+1).setStringValue(s.getJuvenile());
						t2.getCellByPosition(4, iRiga+1).setStringValue(s.getIncidence());
						*/
						// FIXME: tanto vale che li metti tutti!
					}
				}
			}
			System.out.println("tempo per specie: "+(System.currentTimeMillis()-start));
		} catch (Exception e) {
			ComunicazioneEccezione ce = new ComunicazioneEccezione(e);
        	ce.setVisible(true); 
		}
		return documento;
	}
	
}
