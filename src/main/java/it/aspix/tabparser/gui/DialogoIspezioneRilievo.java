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

import it.aspix.archiver.dialoghi.ComunicazioneEccezione;
import it.aspix.archiver.editor.SampleEditor;
import it.aspix.archiver.editor.SampleEditorLinguette;
import it.aspix.archiver.eventi.SistemaException;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.sbd.obj.Message;
import it.aspix.sbd.obj.Sample;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;

/****************************************************************************
 * Consente di visualizzare un rilievo
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DialogoIspezioneRilievo extends JDialog{

	private static final long serialVersionUID = 1L;
	int rilievoVisualizzato = 0;
	Sample[] elenco;
	Message[] esito;
	SampleEditor se = new SampleEditorLinguette();
	DefaultListModel<String> elencoProblemi = new DefaultListModel<>();
	JSlider slider;
	JList<String> lista;
	
	public DialogoIspezioneRilievo(Sample rilievo){
		JPanel pPrincipale = new JPanel(new BorderLayout());

		pPrincipale.add(se, BorderLayout.CENTER);
		try {
			se.setSample(rilievo);
		} catch (SistemaException | ValoreException e) {
			ComunicazioneEccezione ce = new ComunicazioneEccezione(e);
        	ce.setVisible(true); 
		}
		
		this.getContentPane().add(pPrincipale);
		this.pack();
		this.setModal(true);

	}

}
