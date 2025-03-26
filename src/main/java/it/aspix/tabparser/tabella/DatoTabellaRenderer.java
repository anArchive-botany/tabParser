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

import it.aspix.archiver.UtilitaVegetazione;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/****************************************************************************
 * Visualizza un {@link DatoTabella} inserendo il tooltip e con colori
 * di sfondo {@see Costanti} diversi a seconda del livello di errore
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DatoTabellaRenderer extends JLabel implements TableCellRenderer {
	
	private static final long serialVersionUID = 1L;

	public DatoTabellaRenderer() {
		super();
		setOpaque(true);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object object, boolean isSelected, boolean hasFocus, int row, int column) {
		if(object!=null){
			if(object instanceof DatoTabella){
				DatoTabella dt = (DatoTabella) object;
				if(dt.dato instanceof SurveyedSpecie){
					String parti[] = UtilitaVegetazione.calcolaNomeSpecie( (SurveyedSpecie)(dt.dato) );
					this.setText(parti[0]);
				}else{
					this.setText(dt.dato.toString());
				}
				if(dt.tip!=null && dt.tip.length()>0){
					setToolTipText(dt.tip);
				}
				Color c = Costanti.mappaturaLivelliColori.get(dt.livello); 
				if(c==null){
					this.setBackground(Costanti.COLORE_CELLA_SENZA_LIVELLO);
				}else{
					this.setBackground(c);
				}
				TableModelDati ct = (TableModelDati) table.getModel();
				if(ControllerTabella.isScartato(ct.contenutoTabella.headerRighe[row]) || ct.contenutoTabella.headerColonne[column].equals(HeaderColonna.NON_USARE)){
					this.setForeground(Costanti.COLORE_TESTO_DATI_NON_INVIATI);
				}else{
					this.setForeground(Costanti.COLORE_TESTO_DATI);
				}
			}else{
				// XXX: qui non si arriva mai, per via dei dati contenuti nella tabella
				this.setBackground(Costanti.COLORE_CELLE);
				this.setText(object.toString());
			}
		}
		if (isSelected) {
			setBorder(Costanti.bordoSelezionato);
		} else {
			setBorder(Costanti.bordoNonSelezionato);
		}
		
		return this;
	}
}
