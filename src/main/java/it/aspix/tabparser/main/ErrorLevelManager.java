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

import it.aspix.sbd.obj.Message;
import it.aspix.sbd.obj.MessageType;

import java.util.HashMap;
import java.util.logging.Level;

/****************************************************************************
 * Converte i livelli di errore it.aspix.sbd.obj.Message in java.util.logging.Level
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class ErrorLevelManager {
	private static HashMap<MessageType, Level> mappa = new HashMap<MessageType, Level>();
	private HashMap<String, Level> mappaCodici = new HashMap<String, Level>();
	
	static {
		mappa.put(MessageType.INFO, Level.FINEST);
		mappa.put(MessageType.WARNING, Level.WARNING);
		mappa.put(MessageType.ERROR, Level.SEVERE);
	}
	
	/************************************************************************
	 * @param mt l'ogetto da convertire
	 * @return un Level corrispondente a mt
	 ***********************************************************************/
	public static Level mappaLivelloMessaggio(MessageType mt){
		return mappa.get(mt);
	}
	
	/************************************************************************
	 * Questo metodo consente di mappare specifici codici di errore in
	 * specifici livelli
	 * 
	 * @param codice
	 * @param livello
	 ***********************************************************************/
	public void associaCodiceLivello(String codice, Level livello){
		mappaCodici.put(codice, livello);
	}
	
	/************************************************************************
	 * Se il codice del messaggio è stato mappato da associaCodiceLivello()
	 * ritorna il valore impostato altrimenti converte il livello impostato 
	 * 
	 * @param m il messaggio di cui calcolare il livello di errore
	 * @return il livello di errore
	 ***********************************************************************/
	public Level calcolaLivelloMessaggio(Message m){
		if( m.getCode()!=null && mappaCodici.containsKey(m.getCode()) ){
			return mappaCodici.get(m.getCode());
		}else{
			if( m.getType()!=null && mappa.containsKey(m.getType()) ){
				return mappa.get(m.getType());
			}else{
				// in pratica non ha trovato nulla
				return Level.SEVERE;
			}
		}
	}
	
	/************************************************************************
	 * Considera anche i codici dei messaggi nel calcolo
	 * 
	 * @param al un elenco di messaggi di errore
	 * @return il livello massimo di errore contenuto
	 ***********************************************************************/
	public Level calcolaLivelloMassimo(Message[] al){
		// per default tutto bene
		Level massimo = Level.ALL; // XXX: non esiste un livello che esplicitamente indica TUTTO_OK
		Level attuale;
		for(Message m: al){
			attuale = calcolaLivelloMessaggio(m);
			if(attuale.intValue()>massimo.intValue()){
				massimo = attuale;
			}
		}
		return massimo;
	}
	
	/************************************************************************
	 * Considera anche i codici dei messaggi nel calcolo
	 * 
	 * @param al un elenco di messaggi di errore
	 * @return il messaggio legato al livello di errore più alto
	 ***********************************************************************/
	public String cercaTestoMassimo(Message[] al){
		// per default tutto bene
		Level massimo = Level.ALL; // XXX: non esiste un livello che esplicitamente indica TUTTO_OK
		Level attuale;
		String testo = null;
		for(Message m: al){
			attuale = calcolaLivelloMessaggio(m);
			if(attuale.intValue()>massimo.intValue()){
				massimo = attuale;
				testo = m.getText(0).getText();
			}
		}
		if(al.length>2){
			testo += " ...e altri";
		}
		return testo;
	}
}
