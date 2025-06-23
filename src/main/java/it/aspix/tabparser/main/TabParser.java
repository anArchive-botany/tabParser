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
package it.aspix.tabparser.main;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import it.aspix.archiver.Utilita;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.dialoghi.AperturaApplicazione;
import it.aspix.archiver.nucleo.Comunicatore;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.tabparser.gui.Finestra;

/****************************************************************************
 * Chiede le cradenziali e avvia l'applicazione
 *
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class TabParser {

    public static ImageIcon splashTabParser = new ImageIcon(TabParser.class.getResource("splash.jpg"));
	public static final String NOME_PROGRAMMA = "tabParser";
	public static final String SUFFISSO_FILE = "-"+NOME_PROGRAMMA+".xls";

	public static void main(String[] args) throws IOException {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // lasciamo quello di default
		}

		System.setProperty("com.apple.mrj.application.apple.menu.about.name","TabParser");
        System.setProperty("apple.laf.useScreenMenuBar", "true");

		if( System.getenv("DEBUG_ENV_NOME")!=null && System.getenv("DEBUG_ENV_PASSWORD")!=null ){
			Proprieta.caricaProprieta();
			Proprieta.aggiorna("connessione.nome", System.getenv("DEBUG_ENV_NOME"));
			Proprieta.aggiorna("connessione.password", System.getenv("DEBUG_ENV_PASSWORD"));
			Comunicatore comunicatore=new Comunicatore("TabParser","dev");
	        Stato.comunicatore = comunicatore;
		}else{
	        AperturaApplicazione attesaApertura = new AperturaApplicazione(
	        		splashTabParser,
	                "Versione ["+Stato.buildTimeStamp+"] http://www.anarchive.it",
	                Color.WHITE,
	                5
	            );
            attesaApertura.setVisible(true);
            // recupera le proprieta
            attesaApertura.setAvanzamento("Recupero le preferenze...",1);
            Proprieta.caricaProprieta();
            // richiesta della password
            attesaApertura.setAvanzamento("Richiesta password...",2);
            attesaApertura.chiediPassword();
            attesaApertura.aggiornaConnessione();
            Proprieta.check();


            Comunicatore comunicatore=new Comunicatore("TabParser", Stato.versioneTools);
            Stato.comunicatore = comunicatore;
            // controllo i diritti di accesso

            attesaApertura.setAvanzamento("Chiedo verifica diritti di accesso...",3);

            boolean esito;
            String messaggioErrore = "Utente non riconosciuto";
            try{
                esito = comunicatore.login();
            }catch(Exception ex){
                // costruisco un messaggio fittizio da accodare, serve se ad esempio non c'è risposta
                messaggioErrore = "Eccezione nella comunicazione: "+Utilita.getSpiegazione(ex);
                esito = false;
            }
            if(! esito){
                UtilitaGui.mostraMessaggioAndandoACapo(messaggioErrore, "errore", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }

            // apre la finestra degli aggiornamenti (che si apre solo se serve)
            // XXX: in TabParser la finestra non la apro
            /* ComunicazioneAggiornamenti da = new ComunicazioneAggiornamenti(Stato.versione);
            da.setVisible(true); */
            //apre la finestra principale
            attesaApertura.setAvanzamento("Creo la finestra principale...",4);

            attesaApertura.setAvanzamento("Avvio completato",5);
            attesaApertura.setVisible(false);
            attesaApertura.dispose();
		}
		Finestra cs = new Finestra();
		UtilitaGui.centraDialogoAlloSchermo(cs, UtilitaGui.CENTRO);
		cs.setVisible(true);
	}

}
