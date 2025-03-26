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

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import it.aspix.tabparser.tabella.ContenutoTabella;
import it.aspix.tabparser.tabella.DatoTabella;
import it.aspix.archiver.componenti.FornitoreGestoreMessaggi;
import it.aspix.archiver.componenti.StatusBar;
import it.aspix.archiver.editor.SurveyedSpecieEditor;
import it.aspix.sbd.obj.SurveyedSpecie;

/****************************************************************************
 * Editor per la specie rilevata 
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SurveyedSpecieDialog extends JDialog implements Editor, FornitoreGestoreMessaggi{

	private static final long serialVersionUID = 1L;
	
	private SurveyedSpecieEditor sse = new SurveyedSpecieEditor(SurveyedSpecieEditor.Layout.VERTICALE);
	StatusBar sb = new StatusBar();
	boolean chiusoConOK = true;
	
	/*
	@Override 
	public void setVisible(boolean x){
		System.out.println("FFF finestra visibile "+x);
		super.setVisible(x);
	}
	*/
	
	public SurveyedSpecieDialog(){
		JPanel pPrincipale = new JPanel(new BorderLayout());
		pPrincipale.add(sse, BorderLayout.NORTH);
		JPanel pulsantiera = new JPanel(new GridLayout(1,2));
		JButton annulla = new JButton("annulla");
		annulla.addActionListener(e->{this.chiusoConOK=false;this.setVisible(false);});
		JButton ok = new JButton("ok");
		ok.addActionListener(e->{this.chiusoConOK=true;this.setVisible(false);});
		pulsantiera.add(annulla);
		pulsantiera.add(ok);
		pPrincipale.add(pulsantiera, BorderLayout.SOUTH);
		this.getContentPane().add(pPrincipale);
		this.setTitle("Specie rilevata");
		this.pack();
		this.setModal(true);
	}

	@Override
	public void setValore(ContenutoTabella ct, int riga, int colonna) {
		DatoTabella o = (DatoTabella)(ct.getValore(riga, colonna));
		if(o.dato instanceof SurveyedSpecie){
			sse.setSurveyedSpecie((SurveyedSpecie) o.dato);
		}		
	}

	@Override
	public DatoTabella getValore() {
		if(chiusoConOK){
			DatoTabella dt = new DatoTabella();
			SurveyedSpecie sspe = sse.getSurveyedSpecie();
			sspe.setAbundance(null);
			dt.dato = sspe; 
			return dt;
		}else{
			return null;
		}
	}

	@Override
	public String getVoceMenu() {
		return "specie rilevata";
	}

	@Override
	public JDialog getDialogo() {
		return this;
	}

	@Override
	public it.aspix.archiver.componenti.GestoreMessaggi getGestoreMessaggi() {
		return sb;
	}

}
