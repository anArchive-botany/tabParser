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

import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/****************************************************************************
 * L'editor per l'intestazione: un JComboBox
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class HeaderColonnaEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;
	
	public HeaderColonnaEditor(ArrayList<HeaderColonna> valori){
		super(new JComboBox<String>());
		@SuppressWarnings("unchecked")
		JComboBox<String> combo = (JComboBox<String>) this.getComponent();
		DefaultComboBoxModel<String> modello = (DefaultComboBoxModel<String>) combo.getModel();
		for(HeaderColonna s: valori){
			modello.addElement(s.getValore());
		}
	}
	
}
