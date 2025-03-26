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
package it.aspix.tabparser.info;

import it.aspix.tabparser.tabella.ContenutoTabella;

import javax.swing.JPanel;

public interface InfoPanel {

	/************************************************************************
	 * in alcuni casi le informazioni dipendano della casella selezionata
	 * @param ct
	 * @param riga
	 * @param colonna
	 ***********************************************************************/
	public void setValore(ContenutoTabella ct, int riga, int colonna);
	
	/************************************************************************
	 * @return il pannello che contiene le informazioni
	 ***********************************************************************/
	public JPanel getPannello();
	
	/************************************************************************
	 * @return un identificativo per le informazioni visualizzate
	 ***********************************************************************/
	public String getNome();
}
