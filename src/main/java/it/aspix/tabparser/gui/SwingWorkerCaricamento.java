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
package it.aspix.tabparser.gui;

import it.aspix.tabparser.main.TableLoader;
import it.aspix.tabparser.tabella.ContenutoTabella;
import it.aspix.tabparser.tabella.ControllerTabella;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;

import javax.swing.SwingWorker;

/****************************************************************************
 * Si occupa del caricamento della tabella, attività piuttosto lunga
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SwingWorkerCaricamento extends SwingWorker<ArrayList<String[][]>, Void>{
	
	File file;
	Component grigio;
	ContenutoTabella contenutoTabella;
	String nomeScala;
	
	public SwingWorkerCaricamento(File file, Component grigio) {
		this.file = file;
		this.grigio = grigio;
	}
	
	@Override
	protected ArrayList<String[][]> doInBackground(){
		ArrayList<String[][]> cartella = null;
		try{
			cartella = TableLoader.loadTable(file);
			
			contenutoTabella = new ContenutoTabella(cartella);
			if(contenutoTabella.isSenzaDefinizioni()){
				ControllerTabella.analisiAutomaticaColonne(contenutoTabella);
				ControllerTabella.analisiAutomaticaRighe(contenutoTabella);
			}		            		
			nomeScala = ControllerTabella.supponiScalaAbbondanza(contenutoTabella);
			firePropertyChange("completato", null, null);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return cartella;
	}
	@Override
    public void done() {
		grigio.setVisible(false);
	}
	
}
