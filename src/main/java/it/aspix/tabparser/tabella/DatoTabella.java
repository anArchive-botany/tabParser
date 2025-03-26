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

import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.util.logging.Level;

/****************************************************************************
 * Oltre al dato aggiunge delle informazioni alla casella:
 * - un tooltip
 * - un livello di errore {@link java.util.logging.Level}
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DatoTabella implements Cloneable{

	public Object dato;
	public String tip;
	public Level livello;
	
	public DatoTabella clone(){
		DatoTabella x = new DatoTabella();
		if(this.dato instanceof SurveyedSpecie){
			x.dato = ((SurveyedSpecie)(this.dato)).clone();
		}else{
			x.dato = this.dato;
		}
		
		x.tip = this.tip;
		x.livello = this.livello;
		return x;
	}
	
	public DatoTabella(String dato, String tooltip, Level livello) {
		super();
		this.dato = dato;
		this.tip = tooltip;
		this.livello = livello;
	}
	
	public DatoTabella(Object o) {
		super();
		if(o instanceof DatoTabella){
			this.dato = ((DatoTabella) o).dato;
			this.tip = ((DatoTabella) o).tip;
			this.livello = ((DatoTabella) o).livello;
		}else{
			this.dato = o==null ? "" : o;
			this.tip = null;
			this.livello = null;
		}
	}
	
	public DatoTabella() {
		super();
	}
	
	public void setLevel(Level livello){
		this.livello = livello;
	}
	
	public String toString(){
		return dato.toString();
	}
	
	/************************************************************************
	 * Permette di impostare il livello di errore partendo da un oggetto
	 * {@link it.aspix.sbd.obj.MessageType}
	 * @param mt il livello di errore sep SimpleBotanicalData
	 ***********************************************************************/
	public void setLevel(MessageType mt){
		switch(mt){
		case ERROR:
			livello = Level.SEVERE;
			break;
		case WARNING:
			livello = Level.WARNING;
			break;
		case INFO:
			livello = Level.FINE;
			break;
		default:
			break;
		
		}
	}
}
