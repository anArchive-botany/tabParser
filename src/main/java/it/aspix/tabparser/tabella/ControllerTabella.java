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

import it.aspix.tabparser.convertitori.ConvertitoreEsposizione;
import it.aspix.tabparser.convertitori.ConvertitoreInclinazione;
import it.aspix.tabparser.convertitori.ConvertitoreLatitudine;
import it.aspix.tabparser.convertitori.ConvertitoreLongitudine;
import it.aspix.tabparser.main.ErrorLevelManager;
import it.aspix.tabparser.main.GestoreMessaggi;
import it.aspix.tabparser.main.MessaggioErrore;
import it.aspix.tabparser.main.MessaggioErrore.GeneratoreErrore;
import it.aspix.tabparser.main.UtilitaGestioneErrori;
import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.dialoghi.ComunicazioneEccezione;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.introspection.ReflectUtil;
import it.aspix.sbd.obj.Classification;
import it.aspix.sbd.obj.Message;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.obj.SimpleBotanicalData;
import it.aspix.sbd.obj.SpecieSpecification;
import it.aspix.sbd.obj.SurveyedSpecie;
import it.aspix.sbd.obj.Text;
import it.aspix.sbd.scale.sample.GestoreScale;
import it.aspix.sbd.scale.sample.ScalaSample;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.xml.sax.SAXException;

/****************************************************************************
 * Esegue le operazioni su ContenutoTabella
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class ControllerTabella {
	
	/************************************************************************
	 * Una area di interesse per le operazioni eseguite 
	 * @author Edoardo Panfili, studio Aspix
	 ***********************************************************************/
	public enum Area {
		TABELLA("intera tabella"),
		HEADER_DATA("campi degli header"),
		NOMI_SPECIE("nomi specie"),
		STRATI("strati"),
		CLASSIFICAZIONI("classificazioni"),
		ABBONDANZE("abbondanze");
		
		private final String descrizione;
		Area(String descrizione) {
	        this.descrizione = descrizione;
	    }
		
		public String toString(){
			return descrizione;
		}
	}
	
	/************************************************************************
	 * @param gnd il parametro da testare
	 * @return true se il dato relativo non va utilizzato per costruire un rilievo
	 ***********************************************************************/
	public static boolean isScartato(HeaderRiga gnd){
		return gnd.equals(HeaderRiga.NON_USARE) || gnd.equals(HeaderRiga.NOTE_SPECIE) ||
				gnd.equals(HeaderRiga.X) || gnd.equals(HeaderRiga.Y) || gnd.equals(HeaderRiga.EPSG );
	}
	
	/************************************************************************
	 * @param contenuto tabella dei dati
	 * @param iColonna la colonna in cui reperire i dati del rilievo
	 * @param modelloStrati
	 * @return il rilievo costruito
	 * @throws ValoreException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 ***********************************************************************/
	public static Sample getRilievo(ContenutoTabella contenuto, int iColonna, String modelloStrati, ScalaSample ss) throws ValoreException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Sample rilievo = CostruttoreOggetti.createSimpleSample(modelloStrati, ss.getNome());
		HeaderRiga hr;
		int iStrati = contenuto.getColonnaPerTipo(HeaderColonna.STRATI);
		int iDefinizioni = contenuto.getColonnaPerTipo(HeaderColonna.DEFINIZIONI);
		
		for(int iRiga=0;iRiga<contenuto.headerRighe.length;iRiga++){
			hr = contenuto.headerRighe[iRiga];
			if( !isScartato(hr) ){
				if(contenuto.headerRighe[iRiga].isSpeciale()){
					if(hr.equals(HeaderRiga.SPECIE)){
						if(contenuto.dati[iRiga][iDefinizioni].dato instanceof SurveyedSpecie){
							String abbondanza = contenuto.dati[iRiga][iColonna].toString();
							String strato = iStrati==-1 ? "0": contenuto.dati[iRiga][iStrati].toString();
							if(abbondanza.length()==0 || abbondanza.equals(".")){
								// do nothing
							}else{
								SurveyedSpecie sspe = ((SurveyedSpecie)(contenuto.dati[iRiga][iDefinizioni].dato)).clone();
								sspe.setAbundance(abbondanza);
								getLevel(rilievo, strato).addSurveyedSpecie(sspe);
							}
						}else{
							throw new ValoreException(contenuto.dati[iRiga][iDefinizioni].dato+" non è una specie rilevata");
						}
					}else if(hr.equals(HeaderRiga.CLASSIFICAZIONE_NOME)){
						HeaderRiga daTrovare[] = {HeaderRiga.CLASSIFICAZIONE_NOME, HeaderRiga.CLASSIFICAZIONE_TYPUS, HeaderRiga.CLASSIFICAZIONE_TIPO};
						int indici[] = contenuto.cercaGruppo(iRiga, daTrovare);
						Classification c = new Classification();
						c.setName(contenuto.dati[ indici[0] ][iColonna].toString());
						if(indici[1]!=-1){
							c.setTypus(contenuto.dati[ indici[1] ][iColonna].toString());
						}else{
							c.setTypus("void");
						}
						if(indici[2]!=-1){
							c.setType(contenuto.dati[ indici[2] ][iColonna].toString());
						}else{
							c.setType("actual");
						}						
						rilievo.addClassification(c);
					}else if(hr.equals(HeaderRiga.CLASSIFICAZIONE_TYPUS) || hr.equals(HeaderRiga.CLASSIFICAZIONE_TIPO)){
						; // typus viene trattato con l'associazione
					}else if(hr.getGruppo().equals(HeaderRiga.GRUPPO_LAYER)){
						String parti[] = hr.getNome().substring(HeaderRiga.PREFISSO_NOME_SPECIALE.length()).split("-");
						switch(parti[1]){
						case "coverage":
							getLevel(rilievo, parti[0]).setCoverage(contenuto.dati[iRiga][iColonna].toString());
							break;
						case "height":
							getLevel(rilievo, parti[0]).setHeight(contenuto.dati[iRiga][iColonna].toString());
							break;
						case "heightMin":
							getLevel(rilievo, parti[0]).setHeightMin(contenuto.dati[iRiga][iColonna].toString());
							break;
						case "heightMax":
							getLevel(rilievo, parti[0]).setHeightMax(contenuto.dati[iRiga][iColonna].toString());
							break;
						default:
							UtilitaGestioneErrori.mostraErrore("Attributo non gestito", "La riga "+(iRiga+1)+": "+hr.getDescrizione()+" verrà ignorata");
						}
						
					}else{						
						UtilitaGestioneErrori.mostraErrore("Attributo non gestito", "La riga "+(iRiga+1)+": "+hr.getDescrizione()+" verrà ignorata");
					}
				}else{
					ReflectUtil.setViaReflection(rilievo, contenuto.headerRighe[iRiga].getPath(), contenuto.dati[iRiga][iColonna].toString());
				}
			}
		}
		CostruttoreOggetti.rimuoviLivelliVuoti(rilievo.getCell());
		return rilievo;
	}
	
	
	/************************************************************************
	 * La prima colonna sono le definizioni e poi tutti rilievi
	 ***********************************************************************/
	public static void analisiAutomaticaColonne(ContenutoTabella contenuto){
		contenuto.headerColonne[0] = HeaderColonna.DEFINIZIONI;
		for(int i=1; i<contenuto.headerColonne.length; i++){
			contenuto.headerColonne[i] = new HeaderColonna(HeaderColonna.RILIEVO.getValore());
		}
	}

	
	/************************************************************************
	 * NB: questa analisi va fatta dopo aver marcato le colonne
	 ***********************************************************************/
	public static void analisiAutomaticaRighe(ContenutoTabella contenuto){
		int iDefinizioni = -1;
		HeaderRiga tmp;
		int i;

		iDefinizioni = contenuto.getColonnaPerTipo(HeaderColonna.DEFINIZIONI);
		
		// faccio il lavoro soltanto se c'è una colonna con le definizioni
		if(iDefinizioni != -1){
			for(i=0; i<contenuto.headerRighe.length && contenuto.dati[i][iDefinizioni].toString().length()>0; i++){
				tmp = HeaderRiga.cercaPerNomeComune(contenuto.dati[i][iDefinizioni].toString());
				if(tmp==null){
					tmp = HeaderRiga.NON_USARE;
				}else{
					// trovato il tipo di dato, ora lo imposto se nelle colonne è presente qualcosa
					boolean presente=false;
					for(int col=iDefinizioni+1;col<contenuto.dati[i].length && !presente;col++){
						if(contenuto.headerColonne[col].equals(HeaderColonna.RILIEVO)){
							presente |= contenuto.dati[i][col].dato!=null && contenuto.dati[i][col].dato.toString().length()>0;  
						}
					}
					if(presente){
						contenuto.headerRighe[i] = tmp;
					}
				}
			}
			contenuto.headerRighe[i] = HeaderRiga.NON_USARE.clone();
			for(i++;i<contenuto.headerRighe.length;i++){
				if(contenuto.dati[i][iDefinizioni].toString().length()>0){
					// non marco le classi perché non so se la prima riga di un gruppo
					// è una specie oppure una classificazione
					contenuto.headerRighe[i] = HeaderRiga.SPECIE.clone();
				}
			}
		}
	}
	
	
	/************************************************************************
	 * Considerando tutta l'area delle abbondanze suppone quale
	 * scala sia in uso nella tabella
	 * 
	 * @return la scala più probabile
	 ***********************************************************************/
	public static String supponiScalaAbbondanza(ContenutoTabella contenuto){
		ArrayList<ScalaPresenze> scale = new ArrayList<>();
		for(String nome: GestoreScale.getAvailableNames()){
			scale.add(new ScalaPresenze(GestoreScale.buildForName(nome), 0));
		}
		int primaColonna = contenuto.getColonnaPerTipo(HeaderColonna.RILIEVO);
		int primaRiga = contenuto.getRigaPrimaSpecie();
		String abbondanza;
		ScalaPresenze sp;
		for (int iRiga=primaRiga; iRiga<contenuto.headerRighe.length; iRiga++){
			for(int iColonna=primaColonna; iColonna<contenuto.headerColonne.length; iColonna++){
				abbondanza = contenuto.dati[iRiga][iColonna].toString();
				if(abbondanza.length()>0){
					for(int iScala=0; iScala<scale.size(); iScala++){
						sp = scale.get(iScala);
						if(sp.scala.isValid(abbondanza)){
							sp.presenze++;
						}
					}
				}
			}
		}
		Collections.sort(scale);
		for(int iScala=0; iScala<scale.size(); iScala++){
			sp = scale.get(iScala);
		}
		return scale.get(0).scala.getNome();
	}
	
	
	/************************************************************************
	 * @param nomeScala da applicare per il controllo
	 * @param gm a cui comunicare i problemi
	 ***********************************************************************/
	public static void checkAbbondanze(ContenutoTabella contenuto, String nomeScala, GestoreMessaggi gm){
		ScalaSample scala = GestoreScale.buildForName(nomeScala);
		int primaColonna = contenuto.getColonnaPerTipo(HeaderColonna.RILIEVO);
		int primaRiga = contenuto.getRigaPrimaSpecie();
		String abbondanza;
		gm.rimuoviSelettivo(GeneratoreErrore.CONTROLLO_ABBONDANZE);
		for (int iRiga=primaRiga; iRiga<contenuto.headerRighe.length; iRiga++){
			for(int iColonna=primaColonna; iColonna<contenuto.headerColonne.length; iColonna++){
				abbondanza = contenuto.dati[iRiga][iColonna].toString();
				if(abbondanza.length()>0 && !scala.isValid(abbondanza)){
					contenuto.dati[iRiga][iColonna] = new DatoTabella(abbondanza, null, Level.SEVERE);
					if(gm!=null){
						gm.addMessaggio(new MessaggioErrore(iRiga, iColonna, GeneratoreErrore.CONTROLLO_ABBONDANZE, "abbondanza non valida: "+abbondanza, Level.SEVERE));
					}
				}else{
					contenuto.dati[iRiga][iColonna] = new DatoTabella(abbondanza, null, Level.OFF);
				}
			}
		}
	}
	
	
	/************************************************************************
	 * Serve a correggere eventuali letture errate dell'ocr:
	 * "t"→"+"; "l"→"1"
	 ***********************************************************************/
	public static void autopatchAbbondanze(ContenutoTabella contenuto){
		int primaColonna = contenuto.getColonnaPerTipo(HeaderColonna.RILIEVO);
		int primaRiga = contenuto.getRigaPrimaSpecie();
		String abbondanza;
		String abbondanzaNuova;
		for (int iRiga=primaRiga; iRiga<contenuto.headerRighe.length; iRiga++){
			for(int iColonna=primaColonna; iColonna<contenuto.headerColonne.length; iColonna++){
				abbondanza = contenuto.dati[iRiga][iColonna].toString();
				abbondanzaNuova = abbondanza.replace('t', '+').replace('l', '1').trim();
				if(!abbondanzaNuova.equals(abbondanza)){
					contenuto.dati[iRiga][iColonna] = new DatoTabella(abbondanzaNuova, null, Level.FINE);
					contenuto.dati[iRiga][iColonna].tip = "era: \""+abbondanza+"\"";
				}
			}
		}
	}
	
	
	/************************************************************************
	 * Controlla i nomi di specie inviandoli al server, 
	 * questa funzione è migliorabile, invia una lista completa
	 * per ogni strato inserendo nomi "" nelle righe che non interessano
	 * @param contenuto tabella dei dati
	 ***********************************************************************/
	public static SimpleBotanicalData[] controllaSpecieRichiesta(ContenutoTabella contenuto){
		int colonna = contenuto.getColonnaPerTipo(HeaderColonna.DEFINIZIONI);
		String[] elenco;
		int colonnaStrati = contenuto.getColonnaPerTipo(HeaderColonna.STRATI);
		int iStrato;
		
		String nomiStrati[] = contenuto.getStrati();
		SimpleBotanicalData rispostaServer[] = new SimpleBotanicalData[nomiStrati.length];
		
		for(iStrato=0; iStrato<nomiStrati.length ; iStrato++){
			elenco = new String[contenuto.getNumeroRighe()+1];
			elenco[0] = ""; // il server controlla le righe numerandole da zero, 
			// per me la specie uno è la prima quindi uso questo workaround
			for(int i=0;i<contenuto.dati.length; i++){
				if(contenuto.headerRighe[i].equals(HeaderRiga.SPECIE) && 
						(colonnaStrati==-1 || contenuto.dati[i][colonnaStrati].dato.toString().equals(nomiStrati[iStrato])) ){
					elenco[i+1] = contenuto.dati[i][colonna].toString();
				}else{
					elenco[i+1] = "";
				}
			}
			try {
				rispostaServer[iStrato] = it.aspix.archiver.nucleo.Stato.comunicatore.controllaListaNomiSpecie(elenco);
			} catch (SAXException | IOException e) {
				ComunicazioneEccezione ce = new ComunicazioneEccezione(e);
	        	ce.setVisible(true);
			}
		}
		return rispostaServer;
	}
	
	/************************************************************************
	 * Controlla i nomi di specie inviandoli al server
	 * @param contenuto tabella dei dati
	 * @param rispostaServer l'elenco ricevuto dal server
	 * @param contenutoListaMessaggilista in cui inserire gli errori
	 ***********************************************************************/
	public static void controllaSpecieElaboraRisposta(ContenutoTabella contenuto, SimpleBotanicalData rispostaServer[], GestoreMessaggi gm){
		int colonna = contenuto.getColonnaPerTipo(HeaderColonna.DEFINIZIONI);
		DatoTabella d;
		SpecieSpecification sp;
		int colonnaStrati = contenuto.getColonnaPerTipo(HeaderColonna.STRATI);
		String nomiStrati[] = contenuto.getStrati();
		int iStrato;
		
		ErrorLevelManager managerLivelli = new ErrorLevelManager();
		managerLivelli.associaCodiceLivello("1011", Level.WARNING);
		managerLivelli.associaCodiceLivello("2032", Level.FINE);
		gm.rimuoviSelettivo(GeneratoreErrore.CONTROLLO_SPECIE);
		
		for(iStrato=0; iStrato<nomiStrati.length ; iStrato++){
			for(int i=0;i<contenuto.dati.length; i++){
				if(contenuto.headerRighe[i].equals(HeaderRiga.SPECIE) && 
						(colonnaStrati==-1 || contenuto.dati[i][colonnaStrati].dato.toString().equals(nomiStrati[iStrato])) ){
					sp = rispostaServer[iStrato].getSpecieSpecification(i+1);
					d = new DatoTabella();
					if(sp.getNome()!=null && !sp.getNome().equals("null")){
						d.livello = managerLivelli.calcolaLivelloMassimo(sp.getMessage());
						
						d.dato = CostruttoreOggetti.createSurveyedSpecie(sp.getNome(), true, "sure") ;
						if(contenuto.dati[i][colonna].toString().equals(d.dato.toString())){
							d.tip = managerLivelli.cercaTestoMassimo(sp.getMessage());
						}else{
							d.tip = "era: "+contenuto.dati[i][colonna].toString();
						}
						
						contenuto.dati[i][colonna] = d;
						for(int j=0; j<sp.getMessageSize(); j++){
							gm.addMessaggio(new MessaggioErrore(
									i, 
									colonna, 
									GeneratoreErrore.CONTROLLO_SPECIE, 
									sp.getMessage(j).toString(), 
									managerLivelli.calcolaLivelloMessaggio(sp.getMessage(j))
							));
						}
					}else{
						// il server non ha identificato il nome
						contenuto.dati[i][colonna].livello = Level.SEVERE;
						gm.addMessaggio(new MessaggioErrore(
								i, 
								colonna, 
								GeneratoreErrore.CONTROLLO_SPECIE, 
								"Nome di specie non identificabile", 
								Level.SEVERE
						));
					}
				}
			}
		}
	}
	
	public static void cercaSostituisci(ContenutoTabella contenuto, String cerca, String sostituisci, Area areaRichiesta, boolean parziale){
		int iRiga, iColonna;
		HeaderRiga hr; 
		HeaderColonna hc;
		int tipoRiga;
		boolean sostituzioneApplicabile;
		String valoreIniziale;
		String valoreFinale;
		for(iRiga=0; iRiga < contenuto.dati.length; iRiga++){
			hr = contenuto.headerRighe[iRiga];
			// per la riga definisco il gruppo a cui appartiene
			if(hr.equals(HeaderRiga.NON_USARE)){
				tipoRiga = 0; // inutile
			}else if(hr.equals(HeaderRiga.CLASSIFICAZIONE_NOME)){
				tipoRiga = 1; // classificazioni
			}else if(hr.equals(HeaderRiga.SPECIE)){
				tipoRiga = 2; // specie
			}else{
				tipoRiga = 3; // headers
			}
			for(iColonna=0; iColonna< contenuto.dati[0].length; iColonna++){
				hc = contenuto.headerColonne[iColonna]; 
				sostituzioneApplicabile = false;
				switch(areaRichiesta){
				case ABBONDANZE:
					sostituzioneApplicabile = hc.equals(HeaderColonna.RILIEVO) && tipoRiga==2;
					break;
				case CLASSIFICAZIONI:
					sostituzioneApplicabile = hc.equals(HeaderColonna.DEFINIZIONI) && tipoRiga==1;
					break;
				case HEADER_DATA:
					sostituzioneApplicabile = hc.equals(HeaderColonna.RILIEVO) && tipoRiga==3;
					break;
				case NOMI_SPECIE:
					sostituzioneApplicabile = hc.equals(HeaderColonna.DEFINIZIONI) && tipoRiga==2;
					break;
				case TABELLA:
					sostituzioneApplicabile = true;
					break;
				case STRATI:
					sostituzioneApplicabile = hc.equals(HeaderColonna.STRATI) && (tipoRiga==2 || tipoRiga==1);
					break;
				// il default non c'è perché i casi sopra devono coprire tutto
				}
				if(sostituzioneApplicabile){
					valoreIniziale = contenuto.dati[iRiga][iColonna].toString();
					valoreFinale = valoreIniziale;
					if(parziale){
						valoreFinale=valoreIniziale.replaceAll( Pattern.quote(cerca), sostituisci);
					}else{
						if(valoreIniziale.equals(cerca)){
							valoreFinale=sostituisci;
						}
					}
					if(!valoreIniziale.equals(valoreFinale)){
						contenuto.dati[iRiga][iColonna] = new DatoTabella(valoreFinale, valoreIniziale, Level.WARNING);
					}
				}
			}
		}
	}
	
	public static void calcolaCoordinate(ContenutoTabella contenuto, GestoreMessaggi gm){
		int indiceEpsg = -1;
		int indiceX = -1;
		int indiceY = -1;
		int indiceLatitudine = -1;
		int indiceLongitudine = -1;
		
		boolean errori = false;
		
		for(int i=0; i<contenuto.headerRighe.length;i++){
			if(contenuto.headerRighe[i].equals(HeaderRiga.EPSG)){
				indiceEpsg = i;
			}
			if(contenuto.headerRighe[i].equals(HeaderRiga.X)){
				indiceX = i;
			}
			if(contenuto.headerRighe[i].equals(HeaderRiga.Y)){
				indiceY = i;
			}
			if(contenuto.headerRighe[i].getPath().equals("Place.Latitude")){
				indiceLatitudine = i;
			}
			if(contenuto.headerRighe[i].getPath().equals("Place.Longitude")){
				indiceLongitudine = i;
			}
		}
		System.out.println("indiceEpsg:"+indiceEpsg);
		System.out.println("indiceX:"+indiceX);
		System.out.println("indiceY:"+indiceY);
		System.out.println("indiceLatitudine:"+indiceLatitudine);
		System.out.println("indiceLongitudine:"+indiceLongitudine);		
		if(indiceEpsg==-1){
			gm.addMessaggio(new MessaggioErrore(-1, -1, MessaggioErrore.GeneratoreErrore.CONVERSIONE_COORDINATE, "Manca riga codice EPSG", Level.SEVERE));
			errori = true;
		}
		if(indiceX==-1){
			gm.addMessaggio(new MessaggioErrore(-1, -1, MessaggioErrore.GeneratoreErrore.CONVERSIONE_COORDINATE, "Manca riga coordinata X", Level.SEVERE));
			errori = true;
		}
		if(indiceY==-1){
			gm.addMessaggio(new MessaggioErrore(-1, -1, MessaggioErrore.GeneratoreErrore.CONVERSIONE_COORDINATE, "Manca riga coordinata Y", Level.SEVERE));
			errori = true;
		}
		if(indiceLatitudine==-1){
			gm.addMessaggio(new MessaggioErrore(-1, -1, MessaggioErrore.GeneratoreErrore.CONVERSIONE_COORDINATE, "Manca riga per Latitudine", Level.SEVERE));
			errori = true;
		}
		if(indiceLongitudine==-1){
			gm.addMessaggio(new MessaggioErrore(-1, -1, MessaggioErrore.GeneratoreErrore.CONVERSIONE_COORDINATE, "Manca riga per Longitudine", Level.SEVERE));
			errori = true;
		}
		if(errori){
			// se ci sono errori annullo la traduzione
			return;
		}
		for(int iColonna=0 ; iColonna<contenuto.dati[0].length; iColonna++){
			if(contenuto.headerColonne[iColonna].equals(HeaderColonna.RILIEVO)){
				System.out.println("rilievo nella colonna "+iColonna);
				SimpleBotanicalData risposta;
				try {
					risposta = Stato.comunicatore.conversioneCoordinate(
							contenuto.dati[indiceEpsg][iColonna].dato.toString(), 
							contenuto.dati[indiceX][iColonna].dato.toString(),
							contenuto.dati[indiceY][iColonna].dato.toString());
					if(risposta.getPlaceSize()==1){
						contenuto.dati[indiceLatitudine][iColonna].dato = new DatoTabella(risposta.getPlace(0).getLatitude());
						contenuto.dati[indiceLongitudine][iColonna].dato = new DatoTabella(risposta.getPlace(0).getLongitude());
					}else{
						gm.addMessaggio(new MessaggioErrore(iColonna, -1, MessaggioErrore.GeneratoreErrore.CONVERSIONE_COORDINATE, "coordinate non convertibili", Level.SEVERE));
					}
				} catch (SAXException | IOException e) {
					gm.addMessaggio(new MessaggioErrore(iColonna, -1, MessaggioErrore.GeneratoreErrore.CONVERSIONE_COORDINATE, "coordinate non convertibili", Level.SEVERE));
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void convertiDate(ContenutoTabella contenuto, GestoreMessaggi gm){
		String path;
		String originale;
		String parti[];
		String nuovo;
		
		for(int iRiga=0; iRiga<contenuto.headerRighe.length;iRiga++){
			path = contenuto.headerRighe[iRiga].getPath();
			if(path.equals("Date")){

				for(int iColonna=0 ; iColonna<contenuto.dati[0].length; iColonna++){
					if(contenuto.headerColonne[iColonna].equals(HeaderColonna.RILIEVO)){
						originale = contenuto.dati[iRiga][iColonna].dato.toString();
						parti = originale.split("[\\-/]");
						nuovo = null;
						if(parti.length==3){
							nuovo = parti[2]+"-"+(parti[1].length()==2 ? parti[1] : "0"+parti[1]) +"-"+(parti[0].length()==2 ? parti[0] : "0"+parti[0]);
						}else if(parti.length==2){
							nuovo = parti[1]+"-"+(parti[0].length()==2 ? parti[0] : "0"+parti[0]);
						}
						if(nuovo!=null){
							contenuto.dati[iRiga][iColonna].dato = nuovo;
							contenuto.dati[iRiga][iColonna].livello = Level.WARNING;
							contenuto.dati[iRiga][iColonna].tip = "era: "+originale;
						}
					}
				}
			}
		}
	}
	
	public static void convertiEsposizione(ContenutoTabella contenuto, GestoreMessaggi gm){
		String path;
		String originale;
		String nuovo;
		ConvertitoreEsposizione ce = new ConvertitoreEsposizione();
		
		for(int iRiga=0; iRiga<contenuto.headerRighe.length;iRiga++){
			path = contenuto.headerRighe[iRiga].getPath();
			if(path.equals("Place.Exposition")){
				System.out.println("BECCATO!");
				for(int iColonna=0 ; iColonna<contenuto.dati[0].length; iColonna++){
					if(contenuto.headerColonne[iColonna].equals(HeaderColonna.RILIEVO)){
						originale = contenuto.dati[iRiga][iColonna].dato.toString();
						try{
							nuovo = ce.analizzaTesto(originale);
							if(nuovo!=null){
								contenuto.dati[iRiga][iColonna].dato = nuovo;
								contenuto.dati[iRiga][iColonna].livello = Level.WARNING;
								contenuto.dati[iRiga][iColonna].tip = "era: "+originale;
							}
						}catch(Exception ex){
							gm.addMessaggio(new MessaggioErrore(iColonna, -1, MessaggioErrore.GeneratoreErrore.CONVERSIONE_COORDINATE, "esposizione non convertibili", Level.SEVERE));
							// TODO: che fare?
							ex.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public static void convertiInclinazione(ContenutoTabella contenuto, GestoreMessaggi gm){
		String path;
		String originale;
		String nuovo;
		ConvertitoreInclinazione ci = new ConvertitoreInclinazione();
		
		for(int iRiga=0; iRiga<contenuto.headerRighe.length;iRiga++){
			path = contenuto.headerRighe[iRiga].getPath();
			if(path.equals("Place.Inclination")){
				System.out.println("BECCATO!");
				for(int iColonna=0 ; iColonna<contenuto.dati[0].length; iColonna++){
					if(contenuto.headerColonne[iColonna].equals(HeaderColonna.RILIEVO)){
						// faccio la conversione soltanto se la cella è "pulita"
						if(contenuto.dati[iRiga][iColonna].livello==null){
							originale = contenuto.dati[iRiga][iColonna].dato.toString();
							try{
								nuovo = ci.analizzaTesto(originale);
								if(nuovo!=null){
									contenuto.dati[iRiga][iColonna].dato = nuovo;
									contenuto.dati[iRiga][iColonna].livello = Level.WARNING;
									contenuto.dati[iRiga][iColonna].tip = "era: "+originale;
								}
							}catch(Exception ex){
								gm.addMessaggio(new MessaggioErrore(iColonna, -1, MessaggioErrore.GeneratoreErrore.CONVERSIONE_COORDINATE, "esposizione non convertibili", Level.SEVERE));
								ex.printStackTrace();
							}
						}else{
							gm.addMessaggio(new MessaggioErrore(iColonna, -1, MessaggioErrore.GeneratoreErrore.CONVERSIONE_COORDINATE, "questa casella ha subito una elaborazione precedente", Level.SEVERE));
						}
					}
				}
			}
		}
	}
	
	public static void correggiCoordinate(ContenutoTabella contenuto, GestoreMessaggi gm){
		String path;
		String originale;
		String nuovo;
		
		ConvertitoreLatitudine convertitoreLatitudine = new ConvertitoreLatitudine();
		ConvertitoreLongitudine convertitoreLongitudine = new ConvertitoreLongitudine();
		
		for(int iRiga=0; iRiga<contenuto.headerRighe.length;iRiga++){
			path = contenuto.headerRighe[iRiga].getPath();
			if(path.equals("Place.Latitude")){
				for(int iColonna=0 ; iColonna<contenuto.dati[0].length; iColonna++){
					if(contenuto.headerColonne[iColonna].equals(HeaderColonna.RILIEVO)){
						originale = contenuto.dati[iRiga][iColonna].dato.toString();
						try{
							nuovo = convertitoreLatitudine.analizzaTesto(originale);
							if(nuovo!=null && !nuovo.equals(originale)){
								contenuto.dati[iRiga][iColonna].dato = nuovo;
								contenuto.dati[iRiga][iColonna].livello = Level.WARNING;
								contenuto.dati[iRiga][iColonna].tip = "era: "+originale;
							}
						}catch(Exception ex){
							contenuto.dati[iRiga][iColonna].livello = Level.SEVERE;
							gm.addMessaggio(new MessaggioErrore(iRiga, iColonna, GeneratoreErrore.CONVERSIONE_COORDINATE, ex.getMessage(), Level.SEVERE));
						}
					}
				}
			}
			if(path.equals("Place.Longitude")){
				for(int iColonna=0 ; iColonna<contenuto.dati[0].length; iColonna++){
					if(contenuto.headerColonne[iColonna].equals(HeaderColonna.RILIEVO)){
						originale = contenuto.dati[iRiga][iColonna].dato.toString();
						try{
							nuovo = convertitoreLongitudine.analizzaTesto(originale);
							if(nuovo!=null && !nuovo.equals(originale)){
								contenuto.dati[iRiga][iColonna].dato = nuovo;
								contenuto.dati[iRiga][iColonna].livello = Level.WARNING;
								contenuto.dati[iRiga][iColonna].tip = "era: "+originale;
							}
						}catch(Exception ex){
							contenuto.dati[iRiga][iColonna].livello = Level.SEVERE;
							gm.addMessaggio(new MessaggioErrore(iRiga, iColonna, GeneratoreErrore.CONVERSIONE_COORDINATE, ex.getMessage(), Level.SEVERE));
						}
					}
				}
			}
		}
	}
	
	public static void annotaSpecie(ContenutoTabella contenuto, GestoreMessaggi gm){
		int iDefinizioni = contenuto.getColonnaPerTipo(HeaderColonna.DEFINIZIONI);
		if(iDefinizioni==-1){
			gm.addMessaggio(new MessaggioErrore(-1,-1,MessaggioErrore.GeneratoreErrore.ANNOTAZIONE_SPECIE,
					"Manca la colonna con le definizioni.",Level.INFO));
		}else{
			String note = null;
			for(int iRiga=0 ; iRiga<contenuto.dati.length ; iRiga++){
				if( contenuto.headerRighe[iRiga].equals(HeaderRiga.NOTE_SPECIE) ){
					// FIXME: TabImport inseriva un prefisso "caratteristico_di: "
					note = contenuto.dati[iRiga][iDefinizioni].dato.toString();
				}else if( contenuto.headerRighe[iRiga].equals(HeaderRiga.SPECIE) ){
					if(note!=null){
						if(contenuto.dati[iRiga][iDefinizioni].dato instanceof SurveyedSpecie ){
							((SurveyedSpecie) contenuto.dati[iRiga][iDefinizioni].dato).setNote(note);
						}else{
							gm.addMessaggio(new MessaggioErrore(iRiga,-1,MessaggioErrore.GeneratoreErrore.ANNOTAZIONE_SPECIE,
									"L'oggetto contenuto non è una specie rilevata, non inserisco le annotazioni.",Level.FINER));
						}
					}
				}else{
					note = null;
				}
			}
		}
	}
	
	/************************************************************************
	 * Invia i rilievi al server
	 * @param contenuto tabella dei dati
	 * @param modelloStrati
	 * @param sa la scala di abbondanza
	 * @param simulazione se true i dati vengono inviati come simulazione
	 ***********************************************************************/
	public static SimpleBotanicalData[] invioAlServerRichiesta(ContenutoTabella contenuto, 
			String modelloStrati, ScalaSample sa, boolean simulazione){
		int iColonna;
		Sample rilievo;
		SimpleBotanicalData risposte[] = new SimpleBotanicalData[contenuto.headerColonne.length];
		Message messaggio;
		
		for(iColonna=0; iColonna<contenuto.headerColonne.length; iColonna++){
			if(contenuto.headerColonne[iColonna].equals(HeaderColonna.RILIEVO)){
				try {
					rilievo = getRilievo(contenuto, iColonna, modelloStrati, sa);
					risposte[iColonna] = Stato.comunicatore.inserisci(rilievo, null, simulazione);
				} catch (ValoreException e) {
					risposte[iColonna] = new SimpleBotanicalData();
					messaggio = new Message();
					messaggio.setType(MessageType.ERROR);
					messaggio.addText(new Text("it","rilievo non inviato al server: \""+e.getMessage()+"\""));
					risposte[iColonna].addMessage(messaggio);
				} catch (SAXException | IOException e) {
					risposte[iColonna] = new SimpleBotanicalData();
					messaggio = new Message();
					messaggio.setType(MessageType.ERROR);
					messaggio.addText(new Text("it",e.getMessage()));
					risposte[iColonna].addMessage(messaggio);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// errori dovuti all'impostazione delle proprietà usando reflection
					risposte[iColonna] = new SimpleBotanicalData();
					messaggio = new Message();
					messaggio.setType(MessageType.ERROR);
					messaggio.addText(new Text("it",e.getMessage()));
					risposte[iColonna].addMessage(messaggio);
				}
			}else{
				risposte[iColonna] = null; // inutile per via dell'inizializzazione degli array ma così è più esplicito
			}
		}
		return risposte;
	}
	
	/************************************************************************
	 * Controlla le risposte del server sull'invio dei rilievi
	 * @param contenuto tabella dei dati
	 * @param rispostaServer l'elenco ricevuto dal server
	 * @param gm contenutoListaMessaggilista in cui inserire gli errori
	 ***********************************************************************/
	public static void invioAlServerElaboraRisposta(ContenutoTabella contenuto, SimpleBotanicalData[] rispostaServer, GestoreMessaggi gm){
		int iRisposta;
		
		ErrorLevelManager managerLivelli = new ErrorLevelManager();
		// managerLivelli.associaCodiceLivello("1011", Level.WARNING); 
		gm.rimuoviSelettivo(GeneratoreErrore.INVIO_RILIEVI);
		
		for(iRisposta=0; iRisposta<contenuto.headerColonne.length; iRisposta++){
			// può succedere che la risposta sia null se per esempio la colonna
			// non contiene un rilievo
			if(rispostaServer[iRisposta]!=null){
				for(Message m: rispostaServer[iRisposta].getMessage()){
					gm.addMessaggio(new MessaggioErrore(
							-1, 
							iRisposta, 
							GeneratoreErrore.INVIO_RILIEVI,
							m.toString(),
							managerLivelli.calcolaLivelloMessaggio(m)
					));
				}
			}
		}
	}

	public static void aggiungiCampo(ContenutoTabella contenuto, String path, String valore) throws Exception{
		contenuto.addRow(0);
		contenuto.headerRighe[0] = HeaderRiga.cercaHeader(path);
		for(int i=0;i<contenuto.dati[0].length;i++){
			if(contenuto.headerColonne[i].equals(HeaderColonna.RILIEVO)){
				contenuto.dati[0][i] = new DatoTabella(valore,null,Level.OFF);
			}
		}
	}
	
	/************************************************************************
	 * Serve per contare quante presenze sono imputabili ad una scala
	 * 
	 * @author Edoardo Panfili, studio Aspix
	 ***********************************************************************/
	private static class ScalaPresenze implements Comparable<ScalaPresenze>{
		ScalaSample scala;
		int presenze;
		public ScalaPresenze(ScalaSample scala, int presenze) {
			super();
			this.scala = scala;
			this.presenze = presenze;
		}
		private static HashMap<String, Integer> preferenzeScale;
		static {
			preferenzeScale = new HashMap<String, Integer>();
			preferenzeScale.put("braun-blanquet-2",10);
			preferenzeScale.put("braun-blanquet-ab",5);
			preferenzeScale.put("braun-blanquet-mab",0); // devi sprofondare!
		}
		
		@Override
		public int compareTo(ScalaPresenze o) {
			if(this.scala.equals("none") && o.scala.getNome().equals("non")){
				return 0;
			}
			if(this.scala.getNome().equals("none")){
				return 1;
			}
			if(o.scala.getNome().equals("none")){
				return -1;
			}
			if(this.presenze==o.presenze){
				int thisPeso;
				int oPeso;
				try{ thisPeso= preferenzeScale.get(this.scala.getNome()); }catch(Exception e){thisPeso=1;}
				try{ oPeso = preferenzeScale.get(o.scala.getNome()); }catch(Exception e){oPeso=1;}
				return oPeso-thisPeso;
			}
			return o.presenze-this.presenze;
		}
	}
	
	/************************************************************************
	 * @param s il rilievo in cui cercare
	 * @param id l'id dello strato
	 * @return lo strato associato
	 * @throws ValoreException se non trova lo strato
	 ***********************************************************************/
	private static it.aspix.sbd.obj.Level getLevel(Sample s, String id) throws ValoreException{
		for(int i=0; i<s.getCell().getLevelCount() ; i++){
			if(s.getCell().getLevel(i).getId().equals(id)){
				return s.getCell().getLevel(i);
			}
		}
		throw new ValoreException("Strato non trovato: \""+id+"\"");
	}
}
