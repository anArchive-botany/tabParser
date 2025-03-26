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
package it.aspix.tabparser.inputassistito;

import javax.swing.JDialog;

import it.aspix.tabparser.tabella.ContenutoTabella;
import it.aspix.tabparser.tabella.DatoTabella;

/****************************************************************************
 * L'interfaccia che gli editor dei singoli valori devono implementare 
 * per comparire come editor nel menu popup della tabella
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public interface Editor {
	// lo coordinate riguardano l'area dati (non sono quelle di JTable)
	public void setValore(ContenutoTabella ct, int riga, int colonna);
	public JDialog getDialogo();
	public DatoTabella getValore();
	public String getVoceMenu();
}
