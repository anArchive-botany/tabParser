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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.xml.sax.SAXException;

import it.aspix.archiver.dialoghi.ComunicazioneEccezione;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.introspection.DescribedPath;
import it.aspix.sbd.introspection.PropertyFinder;
import it.aspix.sbd.obj.AttributeInfo;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.obj.SimpleBotanicalData;

/****************************************************************************
 * Contiene i dati descrittivi di una riga
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class HeaderRiga implements Cloneable{
	
	private String gruppo;
	private String nome;
	private String descrizione;
	public DescribedPath describedPath;
	
	
	public HeaderRiga(String gruppo, String nome, String descrizione) {
		super();
		this.gruppo = gruppo;
		this.nome = nome;
		this.descrizione = descrizione;
	}

	public HeaderRiga clone(){
		HeaderRiga hr = new HeaderRiga(gruppo, nome, descrizione);
		return hr;
	}
	
	public String toString(){
		return descrizione;
	}
	
	public static final String PREFISSO_NOME_SPECIALE = "SPE#";

	public static final String GRUPPO_LAYER = "LAYERS";
	private static final String SPECIALE_LAYER = PREFISSO_NOME_SPECIALE;  // non serve altro suffisso perché i dati degli strati stanno in un gruppo a se
	
	private static final String GRUPPO_ATTRIBUTO = "ATTRIBUTI";
	private static final String SPECIALE_ATTRIBUTO = PREFISSO_NOME_SPECIALE; // non serve altro suffisso perché gli attributi estesi stanno in un gruppo a se
	
	private static final String SPECIALE_X = PREFISSO_NOME_SPECIALE+"X";
	private static final String SPECIALE_Y = PREFISSO_NOME_SPECIALE+"Y";
	private static final String SPECIALE_EPSG = PREFISSO_NOME_SPECIALE+"EPSG";
	private static final String SPECIALE_ASSOCIAZIONE_NOME = PREFISSO_NOME_SPECIALE+"ASSOCIAZIONE";
	private static final String SPECIALE_ASSOCIAZIONE_TYPUS = PREFISSO_NOME_SPECIALE+"TYPUS";
	private static final String SPECIALE_ASSOCIAZIONE_TIPO = PREFISSO_NOME_SPECIALE+"TIPO";
	private static final String SPECIALE_NOTE_SPECIE = PREFISSO_NOME_SPECIALE+"NOTE SPECIE";
	private static final String SPECIALE_SPECIE = PREFISSO_NOME_SPECIALE+"SPECIE";
	
	private static final String GRUPPO_GENERALE = "Generale";
	
	public static final HeaderRiga NON_USARE = new HeaderRiga("Generale", "non usare", "non usare");
	public static final HeaderRiga NOTE_SPECIE = new HeaderRiga("Generale",SPECIALE_NOTE_SPECIE,"note specie");
	public static final HeaderRiga SPECIE = new HeaderRiga("Generale",SPECIALE_SPECIE,"specie");
	public static final HeaderRiga CLASSIFICAZIONE_NOME = new HeaderRiga("Generale",SPECIALE_ASSOCIAZIONE_NOME,"classificazione");
	public static final HeaderRiga CLASSIFICAZIONE_TYPUS = new HeaderRiga("Generale",SPECIALE_ASSOCIAZIONE_TYPUS,"typus");
	public static final HeaderRiga CLASSIFICAZIONE_TIPO = new HeaderRiga("Generale",SPECIALE_ASSOCIAZIONE_TIPO,"tipo classificazione");
	
	public static final HeaderRiga EPSG = new HeaderRiga("Place",SPECIALE_EPSG,"codice EPSG (per conversione)");
	public static final HeaderRiga X = new HeaderRiga("Place",SPECIALE_X,"X (da convertire)");
	public static final HeaderRiga Y = new HeaderRiga("Place",SPECIALE_Y,"Y (da convertire)");
	
	
	public static ArrayList<HeaderRiga> possibili = new ArrayList<>();
	static{
		possibili.add(NON_USARE);
		String proprietaGruppo;
		String proprietaNome;
		HeaderRiga inCostruzione;
		HashSet<String> daEvitare = new HashSet<>();
		daEvitare.add("Accesso");
		daEvitare.add("Proprieta");
		daEvitare.add("CoperturaBoschi");
		daEvitare.add("CorpiIdrici");
		daEvitare.add("Margini");
		daEvitare.add("MicroHabitat");
		daEvitare.add("Giacitura");
		daEvitare.add("SpecieEsotiche");
		daEvitare.add("Pattern"); // sempre "relevèe" in questo programma
		daEvitare.add("Cell.ModelOfTheLevels"); // è presente in un menu per tutta la tabella
		daEvitare.add("Cell.AbundanceScale"); // è presente in un menu per tutta la tabella
		daEvitare.add("ClassificazioneUtile.Name"); // FIXME: questo è pre via di un errore di sbd
		daEvitare.add("ClassificazioneUtile.Type"); // queste proprietà risultano rw (e invece sono fittizie)
		daEvitare.add("ClassificazioneUtile.Typus");
		
		try{
			DescribedPath[] dp = PropertyFinder.getInjectedFieldList(Sample.class);
			for(int i=0; i<dp.length; i++){
				if( !(dp[i].isReadable) || !(dp[i].isWritable) || daEvitare.contains(dp[i].getGetterPath()) || dp[i].detailLevel>1){
					// System.out.println("escludo "+dp[i].getterPath+ "   onlySearch="+dp[i].onlySearch+"   isSettable="+dp[i].isSettable);
					continue;
				}
				if(dp[i].getGetterPath().indexOf('.')!=-1){
					proprietaGruppo = dp[i].getGetterPath().substring(0, dp[i].getGetterPath().indexOf('.'));
					proprietaNome = dp[i].getGetterPath().substring(dp[i].getGetterPath().indexOf('.')+1);
				}else{
					proprietaGruppo = GRUPPO_GENERALE;
					proprietaNome = dp[i].getGetterPath();
				}
				inCostruzione = new HeaderRiga(proprietaGruppo, proprietaNome, dp[i].getNome()); 
				inCostruzione.describedPath = dp[i];
				possibili.add(inCostruzione);
			}
			// integro le informazioni nelle classificazioni inserite staticamente
			CLASSIFICAZIONE_TYPUS.describedPath = new DescribedPath();
			CLASSIFICAZIONE_TYPUS.describedPath.tipoSBD = "ReleveeTypus";
			CLASSIFICAZIONE_TIPO.describedPath = new DescribedPath();
			CLASSIFICAZIONE_TIPO.describedPath.tipoSBD = "ClassificationType";
			// inserisco staticamente alcune classificazioni
			possibili.add( NOTE_SPECIE );
			possibili.add( SPECIE );
			possibili.add( CLASSIFICAZIONE_NOME );
			possibili.add( CLASSIFICAZIONE_TYPUS );
			possibili.add( CLASSIFICAZIONE_TIPO );
			
			possibili.add( EPSG );
			possibili.add( X );
			possibili.add( Y );
			
			// i dati per gli strati
			// XXX: anche questi compaiono sparsi qui e in entwash, non sarebbe male averli in un unico punto, magari con le traduzioni
			addSerieAttributiStrato("1", "alberi");
			addSerieAttributiStrato("1.1", "alberi alti");
			addSerieAttributiStrato("1.2", "alberi medi");
			addSerieAttributiStrato("1.3", "alberi bassi");
			addSerieAttributiStrato("2", "arbusti");
			addSerieAttributiStrato("2.1", "arbusti alti");
			addSerieAttributiStrato("2.2", "arbusti medi");
			addSerieAttributiStrato("2.3", "arbusti bassi");
			addSerieAttributiStrato("3", "erbe");
			addSerieAttributiStrato("3.1", "erbe alte");
			addSerieAttributiStrato("3.2", "erbe medie");
			addSerieAttributiStrato("3.3", "erbe basse");
			addSerieAttributiStrato("4", "tallofite");
			addSerieAttributiStrato("4.1", "funghi");
			addSerieAttributiStrato("4.2", "briofite");
			addSerieAttributiStrato("4.3", "licheni");
			addSerieAttributiStrato("5", "liane");
			addSerieAttributiStrato("6", "idrofite");
			addSerieAttributiStrato("6.1", "idrofite galleggianti");
			addSerieAttributiStrato("6.2", "idrofite sommerse");
			addSerieAttributiStrato("6.3", "idrofite radicanti");
			addSerieAttributiStrato("7", "acqua");
			addSerieAttributiStrato("8", "lettiera");
			addSerieAttributiStrato("9", "rocce");
			addSerieAttributiStrato("10", "pietre");

		}catch(Exception ex){
			ComunicazioneEccezione ce = new ComunicazioneEccezione(ex);
        	ce.setVisible(true); 
		}
		
		SimpleBotanicalData risposta;
		try {
			risposta = Stato.comunicatore.recuperaInformazioniAttributi();
	        for(AttributeInfo ai: risposta.getAttributeInfo()){
	        	if(ai.getValidIn().contains("vegetation")){
	        		possibili.add(new HeaderRiga(GRUPPO_ATTRIBUTO, SPECIALE_ATTRIBUTO+ai.getName(), ai.getName()));
	        	}
	        }
		} catch (SAXException | IOException e) {
			ComunicazioneEccezione ce = new ComunicazioneEccezione(e);
        	ce.setVisible(true); 
		}
	}
	
	private static void addSerieAttributiStrato(String numero, String descrizione){
		possibili.add(new HeaderRiga(GRUPPO_LAYER, SPECIALE_LAYER+numero+"-coverage", descrizione+": copertura"));
		possibili.add(new HeaderRiga(GRUPPO_LAYER, SPECIALE_LAYER+numero+"-height", descrizione+": altezza media"));
		possibili.add(new HeaderRiga(GRUPPO_LAYER, SPECIALE_LAYER+numero+"-heightMin", descrizione+": altezza minima"));
		possibili.add(new HeaderRiga(GRUPPO_LAYER, SPECIALE_LAYER+numero+"-heightMax", descrizione+": altezza massima"));
	}
	

	
	/************************************************************************
	 * Cerca un elemento utilizzando il suo path
	 * @param path
	 * @return
	 * @throws Exception
	 ***********************************************************************/
	public static HeaderRiga cercaHeader(String path) throws Exception{
		int posPunto = path.indexOf('.');
		if(posPunto==-1){
			return cerca(GRUPPO_GENERALE, path);
		}else{
			String gruppo = path.substring(0,posPunto);
			String nome = path.substring(posPunto+1);
			return cerca(gruppo, nome);
		}
	}
	
	/************************************************************************
	 * nomeComune2Path contiene i nomi descrittivi sempre in tutto minuscolo
	 * per facilitare il confronto
	 ***********************************************************************/
	private static HashMap<String, HeaderRiga> nomeComune2Path = new HashMap<>();
	static {
		try{
			nomeComune2Path.put("epsg", HeaderRiga.EPSG);
			nomeComune2Path.put("x", HeaderRiga.X);
			nomeComune2Path.put("y", HeaderRiga.Y);
			// TODO: se queste corrispondenze si potgessero mettere nello schema SBD non sarebbe male
			nomeComune2Path.put("data", cercaHeader("Date"));
			nomeComune2Path.put("rilevatore", cercaHeader("Surveyer"));
			nomeComune2Path.put("note originali", cercaHeader("OriginalNote"));
			nomeComune2Path.put("note", cercaHeader("Note"));
			nomeComune2Path.put("parole chiave", cercaHeader("Keywords"));
			nomeComune2Path.put("nome provvisorio", cercaHeader("Community"));
			
			nomeComune2Path.put("classificazione", CLASSIFICAZIONE_NOME);
			
			// FIXME: commentate per evitare un errore
			// nomeComune2Path.put("riferimento lisy", cercaHeader("PublicationRef.Reference"));
			nomeComune2Path.put("citazione", cercaHeader("PublicationRef.Citation"));
			nomeComune2Path.put("numero tabella", cercaHeader("PublicationRef.Table"));
			nomeComune2Path.put("numero rilievo nella tabella", cercaHeader("PublicationRef.Number"));
			nomeComune2Path.put("numero del rilievo nella tabella", cercaHeader("PublicationRef.Number"));
			
			nomeComune2Path.put("località", cercaHeader("Place.Name"));
			nomeComune2Path.put("comune", cercaHeader("Place.Town"));
			nomeComune2Path.put("provincia", cercaHeader("Place.Province"));
			nomeComune2Path.put("regione", cercaHeader("Place.Region"));
			nomeComune2Path.put("stato", cercaHeader("Place.Country"));
			nomeComune2Path.put("macro località", cercaHeader("Place.MacroPlace"));
			nomeComune2Path.put("nome area protetta", cercaHeader("Place.ProtectedAreaName"));
			nomeComune2Path.put("tipo di area protetta", cercaHeader("Place.ProtectedAreaType"));
			nomeComune2Path.put("sorgente del punto", cercaHeader("Place.PointSource"));
			nomeComune2Path.put("latitudine", cercaHeader("Place.Latitude"));
			nomeComune2Path.put("longitudine", cercaHeader("Place.Longitude"));
			nomeComune2Path.put("precisione del punto", cercaHeader("Place.PointPrecision"));
			
			nomeComune2Path.put("tipo dell'area protetta", cercaHeader("Place.ProtectedAreaType"));
			nomeComune2Path.put("nome dell'area protetta", cercaHeader("Place.ProtectedAreaName"));
			
			nomeComune2Path.put("reticolo", cercaHeader("Place.MainGrid"));
			nomeComune2Path.put("reticolo cartografico regionale", cercaHeader("Place.MainGrid"));
			nomeComune2Path.put("altitudine", cercaHeader("Place.Elevation"));
			nomeComune2Path.put("esposizione", cercaHeader("Place.Exposition"));
			nomeComune2Path.put("inclinazione", cercaHeader("Place.Inclination"));
			nomeComune2Path.put("substrato", cercaHeader("Place.Substratum"));
			nomeComune2Path.put("habitat", cercaHeader("Place.Habitat"));
			
			nomeComune2Path.put("copertura totale", cercaHeader("Cell.TotalCovering"));
			nomeComune2Path.put("forma del rilievo", cercaHeader("Cell.ShapeName"));
			nomeComune2Path.put("area", cercaHeader("Cell.ShapeArea"));
			nomeComune2Path.put("dimensione 1", cercaHeader("Cell.ShapeDimension1"));
			nomeComune2Path.put("dimensione 2", cercaHeader("Cell.ShapeDimension1"));
			
			// nomeComune2Path.put("", cercaHeader(""));
			 
				/*
				 * Classificazione

				 */
		}catch(Exception e){
			ComunicazioneEccezione ce = new ComunicazioneEccezione(e);
        	ce.setVisible(true); 
		}
	}
	
	/************************************************************************
	 * Cerca un elemento conoscendo il suo nome comune (i nomi normalmente
	 * usati nelle tabelle excel)
	 * @param nome
	 * @return
	 * @throws Exception
	 ***********************************************************************/
	public static HeaderRiga cercaPerNomeComune(String nome){
		HeaderRiga trovato = nomeComune2Path.get(nome.toLowerCase());
		return trovato;
	}				
	
	public static HeaderRiga cerca(String gruppo, String nome) throws Exception{
		for(HeaderRiga hr: possibili){
			if(gruppo.equals(hr.getGruppo()) && nome.equals(hr.getNome())){
				return hr;
			}
		}
		throw new Exception("non trovo "+gruppo+"."+nome);
	}
	

	public boolean isSpeciale(){
		return nome.startsWith(PREFISSO_NOME_SPECIALE);
	}
	
	public String getPath(){
		if(gruppo.equals(GRUPPO_GENERALE)){
			return nome;
		}else{
			return gruppo+"."+nome;
		}
	}

	public String getGruppo() {
		return gruppo;
	}

	public String getNome() {
		return nome;
	}

	public String getDescrizione() {
		return descrizione;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof HeaderRiga){
			return this.gruppo.equals(((HeaderRiga)o).gruppo) && this.nome.equals(((HeaderRiga)o).nome);
		}else{
			return false;
		}
	}

}
