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

import it.aspix.archiver.componenti.ComboSuggerimenti;
import it.aspix.archiver.componenti.FornitoreGestoreMessaggi;
import it.aspix.archiver.componenti.GestoreMessaggi;
import it.aspix.archiver.componenti.StatusBar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/****************************************************************************
 * Permette l'inserimento del nome del progetto e del sottoprogetto
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DialogoProgettoSottoprogetto extends JDialog implements FornitoreGestoreMessaggi{

	private static final long serialVersionUID = 1L;
	
	JTextField progetto = new JTextField();
	JPanel pannelloSottoprogetto = new JPanel(new BorderLayout());
	ComboSuggerimenti sottoprogetto;
	
	StatusBar barraDiStato = new StatusBar(); // fittizia, non viene inserita nell'interfaccia ma serve per intercettare eventuali errori
	boolean chiusoConOK = true;
	String progettoInUso="";
	
	public DialogoProgettoSottoprogetto(){	
		this.setTitle("Progetto e sottoprogetto");
		JPanel principale = new JPanel(new GridBagLayout());
		JLabel eProgetto = new JLabel("progetto:");
		JLabel eSottoprogetto = new JLabel("sottoprogetto:");
		
		JButton annulla = new JButton("annulla");
		JButton ok = new JButton("ok");
		annulla.addActionListener(e->{chiusoConOK = false; setVisible(false);});
		ok.addActionListener(e->{chiusoConOK = true; setVisible(false);});

		principale.add(eProgetto,     			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		principale.add(progetto,      			new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 200, 0));
		principale.add(eSottoprogetto,			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		principale.add(pannelloSottoprogetto,	new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
		principale.add(annulla, 				new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		principale.add(ok, 						new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		
		progetto.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent arg0) {
				variazioneProgetto();
			}
			public void keyPressed(KeyEvent arg0) {
				variazioneProgetto();
			}
		});
		pannelloSottoprogetto.setMinimumSize(new Dimension(10,30));
		pannelloSottoprogetto.setPreferredSize(new Dimension(10,30));
		this.getContentPane().add(principale);
		this.pack();
		pannelloSottoprogetto.setOpaque(false);
		this.setModal(true);
		
	}
	
	/************************************************************************
	 * Chiede nuovi possibili sottoprogetti al server
	 ***********************************************************************/
	private void variazioneProgetto(){
		String x = progetto.getText();
		if(!x.equals(progettoInUso)){
			progettoInUso = x;
			if(progettoInUso.length()>4){
				try{
					sottoprogetto = new ComboSuggerimenti(progettoInUso, "subContainer", ComboSuggerimenti.SOLO_AVVIO, true, false, "", true);
				}catch(Exception ex){
					; // qui la cosa che può non andare è il progetto che non esiste: in questo caso non importa
				}
				pannelloSottoprogetto.removeAll();
	            pannelloSottoprogetto.add(sottoprogetto, BorderLayout.CENTER);
	            pannelloSottoprogetto.updateUI();
			}
		}
	}
	
	@Override
	public GestoreMessaggi getGestoreMessaggi() {
		return barraDiStato;
	}
	
	/************************************************************************
	 * @return il nome del progetto inserito nell'interfaccia
	 ***********************************************************************/
	public String getProgetto(){
		return progetto.getText().trim();
	}

	/************************************************************************
	 * @return il nome del sottoprogetto inserito nell'interfaccia
	 ***********************************************************************/
	public String getSottoprogetto(){
		return sottoprogetto.getText().trim();
	}

}