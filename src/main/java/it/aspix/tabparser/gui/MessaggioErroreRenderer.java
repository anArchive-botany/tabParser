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

import it.aspix.tabparser.main.MessaggioErrore;
import it.aspix.tabparser.tabella.Costanti;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/****************************************************************************
 * Il render per evidenziare i diversi tipi di errore
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class MessaggioErroreRenderer extends JLabel implements ListCellRenderer<Object>{
	private static final long serialVersionUID = 1L;
	protected static final Color COLORE_LISTA_SELEZIONATA = new Color(91,207,250);
	
	public Component getListCellRendererComponent( JList<?> list, Object object, int index, boolean isSelected, boolean cellHasFocus){
		if(object!=null){
			if(object instanceof MessaggioErrore){
				MessaggioErrore me = (MessaggioErrore) object;
				this.setText(me.toString());
				this.setBackground(Costanti.mappaturaLivelliColori.get(me.livello));
			}else{
				this.setBackground(Costanti.mappaturaLivelliColori.get(null));
				this.setText(object.toString());
			}
		}
		setEnabled(list.isEnabled());
		setOpaque(true);
		if(isSelected){
			this.setBackground(COLORE_LISTA_SELEZIONATA);
		}
		setFont(list.getFont());
		
		return this;
	}
}
