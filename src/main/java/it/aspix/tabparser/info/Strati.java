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
import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.sbd.InformazioniTipiEnumerati;
import it.aspix.sbd.ValoreEnumeratoDescritto;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.Sample;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/****************************************************************************
 * Informazioni descrittive del sistema degli strati
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class Strati extends JPanel implements InfoPanel{
	
	private static final long serialVersionUID = 1L;

	public Strati(){
		super();
		ArrayList<ValoreEnumeratoDescritto> valoriStratificazione = InformazioniTipiEnumerati.getElementiDescritti("modelOfTheLevels","it");
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");
		sb.append("<p>il codice da inserire come strato è il numero sulla sinistra</p>");
		for(ValoreEnumeratoDescritto ved: valoriStratificazione){
			sb.append("<h2><big>"+ved.enumerato+"</big>: "+ved.descrizione+"</h2>\n");
			Sample rilievo = CostruttoreOggetti.createSimpleSample(ved.enumerato, "-");
			for(Level l: rilievo.getCell().getLevel()){
				sb.append("<p>"+l.getId()+" "+l.getName()+"</p>\n");
			}
		}
		sb.append("</body></html>");
		JLabel l = new JLabel(sb.toString());
		JScrollPane sp = new JScrollPane(l);
		this.setPreferredSize(new Dimension(400,400));
		this.setLayout(new BorderLayout());
		this.add(sp, BorderLayout.CENTER);
	}
	
	public JPanel getPannello(){
		return this;
	}

	@Override
	public void setValore(ContenutoTabella ct, int riga, int colonna) {
		// non serve a 
	}

	@Override
	public String getNome() {
		return "strati";
	}

}
