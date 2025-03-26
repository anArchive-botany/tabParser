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

import java.util.logging.Level;

/****************************************************************************
 * Un messaggio da visualizzare nell'elenco degli errori
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class MessaggioErrore {
	
	public static enum GeneratoreErrore {
		CONTROLLO_SPECIE("controllo specie"),
		CONTROLLO_ABBONDANZE("controllo abbondanze"),
		COTRUZIONE_RILIEVO("costruzione rilievo"),
		ANNOTAZIONE_SPECIE("annotazione specie"),
		CONVERSIONE_COORDINATE("conversione coordinate"),
		INVIO_RILIEVI("invio rilievi");
		
		private final String descrizione;
		GeneratoreErrore(String descrizione) {
	        this.descrizione = descrizione;
	    }
		
		public String toString(){
			return descrizione;
		}
	}
	
	public int riga;
	public int colonna;
	public GeneratoreErrore generatoDa;
	public String messaggio;
	public Level livello;
	public MessaggioErrore(int riga, int colonna, GeneratoreErrore generatoDa, String messaggio, Level livello) {
		super();
		this.riga = riga;
		this.colonna = colonna;
		this.generatoDa = generatoDa;
		this.messaggio = messaggio;
		this.livello = livello;
	}
	
	public String toString(){
		// incremento di 1 riga e colonna per comodità di lettura degli utenti
		if(riga!=-1 && colonna!=-1){
			return generatoDa+":"+messaggio+" @ "+(riga+1)+";"+(colonna+1);
		}else{
			if(colonna!=-1){
				return generatoDa+":"+messaggio+" @ col_"+(colonna+1);
			}else if(riga!=-1){
				return generatoDa+":"+messaggio+" @ rig_"+(colonna+1);
			}else{
				return generatoDa+":"+messaggio;
			}
		}		
	}
	
}
