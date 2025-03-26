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

import it.aspix.tabparser.info.InfoPanel;
import it.aspix.tabparser.info.InfoPanelFactory;
import it.aspix.tabparser.inputassistito.Editor;
import it.aspix.tabparser.inputassistito.EditorFactory;
import it.aspix.tabparser.main.AzioneAnnullabile;
import it.aspix.tabparser.main.GestoreMessaggi;
import it.aspix.tabparser.main.MessaggioErrore;
import it.aspix.tabparser.main.MessaggioErrore.GeneratoreErrore;
import it.aspix.tabparser.main.TabParser;
import it.aspix.tabparser.tabella.ContenutoTabella;
import it.aspix.tabparser.tabella.ControllerTabella;
import it.aspix.tabparser.tabella.DatoTabella;
import it.aspix.tabparser.tabella.HeaderColonna;
import it.aspix.tabparser.tabella.TabellaComposta;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.dialoghi.ComunicazioneEccezione;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.sbd.InformazioniTipiEnumerati;
import it.aspix.sbd.ValoreEnumeratoDescritto;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.obj.SimpleBotanicalData;
import it.aspix.sbd.obj.SurveyedSpecie;
import it.aspix.sbd.scale.sample.GestoreScale;
import it.aspix.sbd.scale.sample.ScalaSample;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;

/****************************************************************************
 * La finestra principale dell'applicazione
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class Finestra extends JFrame implements GestoreMessaggi{

	private static final long serialVersionUID = 1L;
	
	// true=modifica|false=non_modifica => colore da usare
	private static HashMap<Boolean, Color>coloreBase = new HashMap<>();
	private static HashMap<Boolean, Color>coloreEvidenziato = new HashMap<>();
	private static HashMap<Boolean, Color>coloreIcona = new HashMap<>();
	{
		coloreBase.put(true, new Color(255,120,0) ); // modifica i dati
		coloreBase.put(false, new Color(154,191,65) ); // NON modifica i dati
		
		coloreEvidenziato.put(true, new Color(255,24,0) ); // modifica i dati
		coloreEvidenziato.put(false, new Color(71,176,24) ); // NON modifica i dati
		
		coloreIcona.put(true, new Color(77,3,17) ); // modifica i dati
		coloreIcona.put(false, new Color(28,77,7) ); // NON modifica i dati
	}
	
	// lo scroll sta qui perché serve di aggiongerci la tabelle in risposta ad alcuni eventi
	// JScrollPane scrollTabella = new JScrollPane();
	JPanel pannelloTabella = new JPanel(new BorderLayout());
	// l'oggetto che mantiene tutti i dati
	ContenutoTabella contenutoTabella;
	// la tabella
	TabellaComposta tabella;
	MouseListener ascoltatoreMouseSuTabella;
	// il nome del file in elaborazione
	String nomeFile = null;
	// combo/liste con relativi modelli
	DefaultListModel<MessaggioErrore> contenutoListaMessaggi;
	JList<MessaggioErrore> listaMessaggi;
	DefaultComboBoxModel<ValoreEnumeratoDescritto> modelloStratificazione;
	WiderDropDownCombo<ValoreEnumeratoDescritto> stratificazione;
	DefaultComboBoxModel<ValoreEnumeratoDescritto> modelloScalaAbbondanze;
	WiderDropDownCombo<ValoreEnumeratoDescritto> scalaAbbondanze;
	// contiene le ultime azioni eseguite
	Stack<AzioneAnnullabile> pilaAnnulla = new Stack<>();
	JLabel pulsanteAnnulla;
	JComboBox<String> comboAiuti;
	
	JLabel coordinateSelezione = new JLabel();
	
	Point puntoPopup = new Point();
	ArrayList<JLabel> daEvidenziare = new ArrayList<>();
	
	/************************************************************************
	 * Crea la finestra e i contenuti dei combo
	 ***********************************************************************/
	public Finestra(){
		// =========== creo una tabella fittizia iniziale ===================
		// più utile per fare debug che altro
		String temp_m[][] = new String[30][30];
		for(int i=0; i<temp_m.length;i++){
			for(int j=0; j<temp_m[0].length;j++){
				temp_m[i][j] = "";
			}
		}
		ArrayList<String[][]> temp_a = new ArrayList<>();
		temp_a.add(temp_m);
		contenutoTabella = new ContenutoTabella(temp_a);
		tabella = new TabellaComposta(contenutoTabella);
		// === i pulsanti per le azioni e relativi link ferso le azioni =====
		JPanel pannelloPulsanti = new JPanel(new GridLayout(0,7,2,2));
		pannelloPulsanti.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		// prima riga
		pannelloPulsanti.add( jButtonDueRighe("salva", null, false, false,e->azioneSalva()) );
		pulsanteAnnulla = jButtonDueRighe("annulla", null, true, false, e->azioneAnnulla() );
		pulsanteAnnulla.setFont(new Font("Arial", Font.PLAIN, 10));
		pannelloPulsanti.add( pulsanteAnnulla );
		pannelloPulsanti.add( jButtonDueRighe("cerca e","sostituisci", true, false, e->azioneCercaSostituisci()) );
		pannelloPulsanti.add( jButtonDueRighe("simula", "invio", false, false, e->azioneInviaRilievi(true)) );
		JLabel inviaAlServer = jButtonDueRighe("invia", "al server", false, false, e->azioneInviaRilievi(false));
		pannelloPulsanti.add( inviaAlServer );
		pannelloPulsanti.add( jButtonDueRighe("visualizza","rilievo", false, false, e->azioneVisualizzaRilievo()) );
		DefaultComboBoxModel<String> aiuti = new DefaultComboBoxModel<>();
		aiuti.addElement("aiuto");
		for(String nomeInfo: InfoPanelFactory.getNomi()){
			aiuti.addElement(nomeInfo);
		}
		comboAiuti = new JComboBox<>(aiuti);
		comboAiuti.addActionListener(e->selezionatoAiuto());
		pannelloPulsanti.add( comboAiuti );		
		// seconda riga
		pannelloPulsanti.add( jButtonDueRighe("autocorrezione","abbondanze", true, false, e->azionePatchAbbondanze()) );
		pannelloPulsanti.add( jButtonDueRighe("trasforma","coordinate", true, false, e->azioneCalcolaCoordinate()) );
		pannelloPulsanti.add( jButtonDueRighe("correggi", "coordinate", true, true, e->azioneCorreggiCoordinate()) );
		pannelloPulsanti.add( jButtonDueRighe("converti", "date", true, true, e->azioneConvertiDate()) );
		pannelloPulsanti.add( jButtonDueRighe("converti", "esposizione", true, true, e->azioneConvertiEsposizione()) );
		pannelloPulsanti.add( jButtonDueRighe("converti", "inclinazione", true, true, e->azioneConvertiInclinazione()) );
		pannelloPulsanti.add( new JLabel() );
		// terza riga
		pannelloPulsanti.add( jButtonDueRighe("copia", "verso destra", true, false, e->azioneCopiaDestra()) );
		pannelloPulsanti.add( jButtonDueRighe("copia", "verso il basso", true, false, e->azioneCopiaBasso()) );		
		pannelloPulsanti.add( jButtonDueRighe("inserisci","riga", true, false, e->azioneInserisciRiga()) );
		pannelloPulsanti.add( jButtonDueRighe("inserisci","colonna", true, false, e->azioneInserisciColonna()) );
		pannelloPulsanti.add( jButtonDueRighe("elimina","riga", true, false, e->azioneEliminaRiga()) );
		pannelloPulsanti.add( new JLabel() );
		pannelloPulsanti.add( new JLabel() );
		// quarta riga
		pannelloPulsanti.add( jButtonDueRighe("controlla","abbondanze", false, true, e->azioneScalaAbbondanze()) );
		pannelloPulsanti.add( jButtonDueRighe("controlla","specie", true, true, e->azioneControllaSpecie()) );
		pannelloPulsanti.add( jButtonDueRighe("annota", "specie", true, false, e->azioneAnnotaSpecie()) );
		pannelloPulsanti.add( jButtonDueRighe("inserisci","dati progetto", true, true, e->azioneDatiProgetto()) );
		pannelloPulsanti.add( jButtonDueRighe("inserisci", "diritti", true, true, e->azioneAggiungiDiritti()) );
		pannelloPulsanti.add( jButtonDueRighe("pulisci", "colori", false, false, e->azionePulisciColori()) );
		pannelloPulsanti.add( new JLabel() );
		
		inviaAlServer.setBackground(Color.WHITE);
		
		JButton pulisciMessaggi = new JButton("pulisci");
		pulisciMessaggi.addActionListener(e->azionePulisciMessaggi());
		
		// ============== riempio le caselle con i modelli ==================
		ArrayList<ValoreEnumeratoDescritto> valoriStratificazione = InformazioniTipiEnumerati.getElementiDescritti("modelOfTheLevels","it");
		modelloStratificazione = new DefaultComboBoxModel<>();
		for(int i=0; i<valoriStratificazione.size(); i++){
			modelloStratificazione.addElement(valoriStratificazione.get(i));
		}
		stratificazione = new WiderDropDownCombo<ValoreEnumeratoDescritto>(modelloStratificazione);
		stratificazione.setWide(true);
		ArrayList<ValoreEnumeratoDescritto> valoriScalaAbbondanze = InformazioniTipiEnumerati.getElementiDescritti("abundanceScale","it");
		modelloScalaAbbondanze = new DefaultComboBoxModel<>();
		for(int i=0; i<valoriScalaAbbondanze.size(); i++){
			modelloScalaAbbondanze.addElement(valoriScalaAbbondanze.get(i));
		}
		scalaAbbondanze = new WiderDropDownCombo<ValoreEnumeratoDescritto>(modelloScalaAbbondanze);
		scalaAbbondanze.setWide(true);
		scalaAbbondanze.addActionListener(e->azioneScalaAbbondanze());

		// ============ inserisco i pulsanti nel pannello ===================
		JPanel pannelloParametri = new JPanel(new GridBagLayout());
		pannelloParametri.add(new JLabel("stratificazione:"),  new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
		pannelloParametri.add(stratificazione,                 new GridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		stratificazione.setPreferredSize(new Dimension(100,stratificazione.getPreferredSize().height));
		pannelloParametri.add(new JLabel("scala abbondanze:"), new GridBagConstraints(2,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,       new Insets(0, 10, 0, 0), 0, 0));
		pannelloParametri.add(scalaAbbondanze,                 new GridBagConstraints(3,0,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		// ===================== pannello degli aiuti =======================
		/*JPanel pannelloHelp = new JPanel(new GridBagLayout());
		int posX=1;
		pannelloHelp.add(new JLabel("help!"),  new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		for(String nomeInfo: InfoPanelFactory.getNomi()){
			JButton infoX = new JButton(nomeInfo);
			// costruisco la finestra
			InfoPanel ip = InfoPanelFactory.getInfoPanel(nomeInfo);
			JDialog dialogo = new JDialog();
			JPanel principale = new JPanel(new BorderLayout());
			JButton chiudi = new JButton("chiudi");
			principale.add(ip.getPannello(), BorderLayout.CENTER);
			principale.add(chiudi, BorderLayout.SOUTH);
			dialogo.getContentPane().add(principale);
			dialogo.pack();
			dialogo.setTitle("Info: "+ip.getNome());
			UtilitaGui.centraDialogoAlloSchermo(dialogo, UtilitaGui.CENTRO);
			chiudi.addActionListener(ac->{
				dialogo.setVisible(false);
			});
			infoX.addActionListener(e->{
				dialogo.setVisible(true);
			});
			pannelloHelp.add(infoX,  new GridBagConstraints(posX++,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}
		// filler!
		pannelloHelp.add(new JLabel(),  new GridBagConstraints(posX,0,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		*/
		// ============== costruisco la barra di stato ======================
		JPanel statusBar = new JPanel(new GridBagLayout());
		statusBar.add(new JLabel("selezione:"),  new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		statusBar.add(coordinateSelezione,       new GridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 100, 0));
		statusBar.add(new JPanel(),              new GridBagConstraints(2,0,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 100, 0));
		statusBar.setBorder(BorderFactory.createBevelBorder( BevelBorder.LOWERED ));
		
		// =========== costruisco il pannello principale ====================
		pannelloTabella.add(tabella, BorderLayout.CENTER);
		JScrollPane scrollListaMessaggi = new JScrollPane();
		JPanel pannelloMessaggi = new JPanel(new BorderLayout());
		pannelloMessaggi.add(scrollListaMessaggi, BorderLayout.CENTER);
		pannelloMessaggi.add(pulisciMessaggi, BorderLayout.EAST);
		pannelloMessaggi.add(statusBar, BorderLayout.SOUTH);
		// pannelloAzioni contiene i pulsanti e i combo per i parametri di elaborazione
		JPanel pannelloAzioni = new JPanel(new BorderLayout()); // la parte superiore della finestra
		pannelloAzioni.add(pannelloPulsanti, BorderLayout.NORTH);
		// pannelloAzioni.add(pannelloHelp, BorderLayout.CENTER);
		pannelloAzioni.add(pannelloParametri, BorderLayout.SOUTH);
		// pannelloElaborazione contiene sia la tabella che l'elenco dei messaggi
		JSplitPane pannelloElaborazione = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pannelloTabella, pannelloMessaggi); // la parte centrale della finestra
		JPanel principale = new JPanel(new BorderLayout());
		principale.add(pannelloAzioni, BorderLayout.NORTH);
		principale.add(pannelloElaborazione, BorderLayout.CENTER);
		
		// ============== impostazioni della lista dei messaggi =============
		contenutoListaMessaggi = new DefaultListModel<>();
		listaMessaggi = new JList<>(contenutoListaMessaggi);
		scrollListaMessaggi.getViewport().add(listaMessaggi);
		listaMessaggi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					azioneSelezioneMessaggio();
				}
			}
		});
		listaMessaggi.addKeyListener(new KeyAdapter(){
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getExtendedKeyCode()==KeyEvent.VK_DELETE || e.getExtendedKeyCode()==KeyEvent.VK_BACK_SPACE){
					int iSelezionato = listaMessaggi.getSelectedIndex();
					contenutoListaMessaggi.remove(iSelezionato);
					listaMessaggi.setSelectedIndex(iSelezionato);
				}
			}
		});
		listaMessaggi.setCellRenderer(new MessaggioErroreRenderer());
		
		// =================== impostazioni generali della finestra =========
		ToolTipManager.sharedInstance().setInitialDelay(100);
		this.setTitle(TabParser.NOME_PROGRAMMA);
		this.setSize(1015, 750);
		pannelloTabella.setMinimumSize( new Dimension(100, 300) );
		// pannelloHelp.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
		pannelloParametri.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pannelloParametri.setBackground(Color.ORANGE);
		this.getContentPane().add(principale);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		aggiornaAnnulla();
		
		// ====================== menu pop-up delle caselle =================
		JPopupMenu menu = new JPopupMenu();
		for(String x: EditorFactory.getNomi()){
			JMenuItem item = new JMenuItem(x);
			item.addActionListener(e->{
				apriEditor(((JMenuItem)e.getSource()).getText(), puntoPopup);
			});
			menu.add(item) ;
		}
		ascoltatoreMouseSuTabella = new MouseAdapter(){
			private void selezionaCasella(MouseEvent e){
				JTable source = (JTable)e.getSource();
                int row = source.rowAtPoint( e.getPoint() );
                int column = source.columnAtPoint( e.getPoint() );
                if (! source.isRowSelected(row)){
                    source.changeSelection(row, column, false, false);
                }
			}
			private void gestisci(MouseEvent e){
				coordinateSelezione.setText((tabella.getSelectedRow()+1)+";"+(tabella.getSelectedColumn()+1));
				// popuptrigger può essere generato sia dalla pressione che dal rilascio
				// del mouse, dipende dalla piattaforma
				if (e.isPopupTrigger()){
					System.out.println("FFF gestisci "+e.getLocationOnScreen());
                	selezionaCasella(e);
                    // registro il punto in cui si è aperto il popup per aprire poi il dialogo
                    puntoPopup.x=e.getXOnScreen();
                    puntoPopup.y=e.getYOnScreen();                    
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
			}
			@Override
            public void mousePressed(MouseEvent e){
                gestisci(e); 
            }
			@Override
            public void mouseReleased(MouseEvent e){
                gestisci(e);
            }
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount()==2){
					selezionaCasella(e);
					System.out.println("FFF gestisci");
					DatoTabella dt = contenutoTabella.getValore(tabella.getRigaSelezionata(),tabella.getColonnaSelezionata());
					if(dt.dato instanceof SurveyedSpecie){
	                    puntoPopup.x=e.getXOnScreen();
	                    puntoPopup.y=e.getYOnScreen();  
						apriEditor("specie rilevata", puntoPopup);
					}
				}
			}
        };
        
        tabella.tabellaDati.addMouseListener(ascoltatoreMouseSuTabella);
        tabella.tabellaDati.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				coordinateSelezione.setText((tabella.getSelectedRow()+1)+";"+(tabella.getSelectedColumn()+1));
			}
		});
		// ============================ DnD =================================
		DropTargetListener dtl = new DropTargetAdapter() {
						
			@Override
			public void drop(DropTargetDropEvent e) {
				File file;
		        try {
		            Transferable tr = e.getTransferable();
		            if(e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
		                e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		                @SuppressWarnings("unchecked")
		                java.util.List<File> elenco = (java.util.List<File>) (tr.getTransferData(DataFlavor.javaFileListFlavor));
		                file = elenco.get(0);
		                if(file.getPath().endsWith("ods") || file.getPath().endsWith("xls") || file.getPath().endsWith("xlsx")){
		                	JPanel grigio = jAttesa();
		                	SwingWorkerCaricamento swc = new SwingWorkerCaricamento(file, grigio);
		                	nomeFile = file.toString();
		                	swc.addPropertyChangeListener(new PropertyChangeListener() {
								@Override
								public void propertyChange(PropertyChangeEvent evt) {
									if(evt.getPropertyName().equals("completato")){
					                	Finestra.this.setTitle(TabParser.NOME_PROGRAMMA+" - "+nomeFile);
					                	contenutoListaMessaggi.removeAllElements();
					            		contenutoTabella = swc.contenutoTabella;		            		
					            		String nomeScala = swc.nomeScala;
					            		for(int i=0; i<modelloScalaAbbondanze.getSize(); i++){
					            			if(modelloScalaAbbondanze.getElementAt(i).enumerato.equals(nomeScala)){
					            				// il setSelectedIndex fa lanciare l'evento "selezione cambiata"
					            				scalaAbbondanze.setSelectedIndex(i);
					            				break;
					            			}
					            		}
					            		aggiornaTabella(contenutoTabella);
					                	principale.updateUI();
					                	for(JLabel jb: daEvidenziare){
					                		jb.setFont(jb.getFont().deriveFont(Font.BOLD));
					                		jb.setBackground( coloreEvidenziato.get(jb.getClientProperty("modificaDati")) );
					                	}
									}
								}
							});
		                	swc.execute();
		            		Finestra.this.setGlassPane(grigio); // mostra messaggio di attesa
		            		grigio.setVisible(true);
		            		e.dropComplete(true);
		                }else{
		                	JOptionPane.showMessageDialog(null, "File non utilizzabile", "Errore", JOptionPane.ERROR_MESSAGE);
		                }
		            }else {
		                e.rejectDrop();
		            }
		        }catch(Exception | Error err) {
		        	ComunicazioneEccezione ce = new ComunicazioneEccezione(err);
                	ce.setVisible(true); 
		        }
			}
		};
		new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, dtl );
	}
	
	/************************************************************************
	 * Apre un particolare editor 
	 * @param nome dell'editor da aprire definiti in it.aspix.gui.inputassistito
	 * @param punto in cui risiede il dato da editare
	 ***********************************************************************/
	private void apriEditor(String nome, Point punto){
		Editor editor = EditorFactory.getEditor(nome);
		editor.setValore(contenutoTabella, tabella.getRigaSelezionata(), tabella.getColonnaSelezionata()); 
		editor.getDialogo().setLocation(punto.x, punto.y);
		editor.setValore(contenutoTabella, tabella.getRigaSelezionata(), tabella.getColonnaSelezionata());
		editor.getDialogo().setVisible(true);
		if(editor.getValore()!=null){
			contenutoTabella.dati[tabella.getRigaSelezionata()][tabella.getColonnaSelezionata()] = editor.getValore();
			tabella.updateUI();
		}
	}
	
	/************************************************************************
	 * Salva i dati in formato ods, mettendo un suffisso al nome del file
	 * originale (se il suffisso non è già presente)
	 ***********************************************************************/
	private void azioneSalva(){
		JPanel grigio = jAttesa();
		File fileTarget;
		if(System.getenv("DEBUG_ENV_NOME")!=null){
			// si suppone che sia edoardo, salviamo in ramdisk!
			fileTarget = new File("/Volumes/ramdisk/x.xls");
		}else{
			System.out.println("nomefile "+nomeFile);
			if(!nomeFile.endsWith(TabParser.SUFFISSO_FILE)){
				int ultimoPunto = nomeFile.lastIndexOf('.');
				fileTarget = new File(nomeFile.substring(0,ultimoPunto)+TabParser.SUFFISSO_FILE);
			}else{
				fileTarget = new File(nomeFile);
			}
		}
		System.out.println("salvo in "+fileTarget);
    	SwingWorkerSalvataggio sws = new SwingWorkerSalvataggio(fileTarget, contenutoTabella, grigio);
    	Finestra.this.setGlassPane(grigio); // mostra messaggio di attesa
		grigio.setVisible(true);
    	sws.execute();
	}
	
	/************************************************************************
	 * Aggiunge una riga alla tabella alla posisione in cui si trova
	 * quella attualmente selezionata
	 ***********************************************************************/
	private void azioneInserisciRiga(){
		contenutoTabella.addRow(tabella.getRigaSelezionata());
		aggiornaTabella(contenutoTabella);
	}
	
	/************************************************************************
	 * Aggiunge una colonna alla tabella alla posisione in cui si trova
	 * quella attualmente selezionata
	 ***********************************************************************/
	private void azioneInserisciColonna(){
		contenutoTabella.addColumn(tabella.getColonnaSelezionata());
		aggiornaTabella(contenutoTabella);
	}
	
	/************************************************************************
	 * elimina la riga dealla tabella alla posisione in cui si trova
	 * quella attualmente selezionata
	 ***********************************************************************/
	private void azioneEliminaRiga(){
		contenutoTabella.deleteRow(tabella.getRigaSelezionata());
		aggiornaTabella(contenutoTabella);
	}
	
	/************************************************************************
	 * Copia in tutte le caselle a destra di quella selezionata il valore
	 * presente in quella selezionata
	 ***********************************************************************/
	private void azioneCopiaDestra(){
		int riga = tabella.getRigaSelezionata();
		int colonna = tabella.getColonnaSelezionata();
		DatoTabella originale = contenutoTabella.getValore(riga, colonna);
		for(int i=colonna+1; i<contenutoTabella.getNumeroColonne(); i++){
			contenutoTabella.setValore(riga, i, originale.clone());
		}
		tabella.updateUI();
	}

	/************************************************************************
	 * Copia in tutte le caselle al disotto di quella selezionata il valore
	 * presente in quella selezionata
	 ***********************************************************************/
	private void azioneCopiaBasso(){
		int riga = tabella.getRigaSelezionata();
		int colonna = tabella.getColonnaSelezionata();
		DatoTabella originale = contenutoTabella.getValore(riga, colonna);
		for(int i=riga+1; i<contenutoTabella.getNumeroRighe(); i++){
			contenutoTabella.setValore(i, colonna, originale.clone());
		}
		tabella.updateUI();
	}
	
	/************************************************************************
	 * Controlla le abbondanze presenti nella tabella
	 ***********************************************************************/
	private void azioneScalaAbbondanze(){
		ValoreEnumeratoDescritto ved = (ValoreEnumeratoDescritto) scalaAbbondanze.getSelectedItem();
		ControllerTabella.checkAbbondanze(contenutoTabella, ved.enumerato, this);
		tabella.updateUI();
	}
	
	/************************************************************************
	 * corregge piccoli errori che possono trovarsi nelle abbondanze
	 ***********************************************************************/
	private void azionePatchAbbondanze(){
		ControllerTabella.autopatchAbbondanze(contenutoTabella);
		tabella.updateUI();
	}
	
	/************************************************************************
	 * La selezione di un messaggio nella lista degli errori
	 ***********************************************************************/
	private void azioneSelezioneMessaggio(){
		MessaggioErrore me = listaMessaggi.getSelectedValue();
		tabella.setCellaSelezionata(me.riga, me.colonna);
	}
	
	/************************************************************************
	 * Rimozione di tutti i messaggi di errore
	 ***********************************************************************/
	private void azionePulisciMessaggi(){
		contenutoListaMessaggi.removeAllElements();
	}
	
	/************************************************************************
	 * Il controllo delle specie richiede tempo perché viene fatto dal server
	 * per questo motivo si usa uno SwingWorker e il lavoro viene diviso in due parti:
	 * - invio della richiesta al server
	 * - elaborazione della risposta
	 ***********************************************************************/
	private void azioneControllaSpecie(){
		Component grigio = jAttesa();
		pilaAnnulla.push(new AzioneAnnullabile("controllo specie", contenutoTabella.clone()));
		aggiornaAnnulla();
		SwingWorker<SimpleBotanicalData[], Void> lavoratore = new SwingWorker<SimpleBotanicalData[], Void>(){
			SimpleBotanicalData[] risposta;
			@Override
			protected SimpleBotanicalData[] doInBackground() throws Exception {
				risposta = ControllerTabella.controllaSpecieRichiesta(contenutoTabella); 
				return risposta;
			}
			@Override
		    public void done() {
				ControllerTabella.controllaSpecieElaboraRisposta(contenutoTabella, risposta, Finestra.this);
				tabella.updateUI();
				grigio.setVisible(false);
			}
			
		};
		lavoratore.execute();
		this.setGlassPane(grigio); // mostra messaggio di attesa
		grigio.setVisible(true);
	}
	
	/************************************************************************
	 * visualizza il rilievo presente nella colonna selezionata
	 ***********************************************************************/
	public void azioneVisualizzaRilievo(){
		int colonna = tabella.getColonnaSelezionata();
		ValoreEnumeratoDescritto s = (ValoreEnumeratoDescritto) stratificazione.getSelectedItem();
		ValoreEnumeratoDescritto abbondanza = (ValoreEnumeratoDescritto) scalaAbbondanze.getSelectedItem();
		ScalaSample scalaAbbondanze = GestoreScale.buildForName(abbondanza.enumerato);		
		try{
			Sample rilievo = ControllerTabella.getRilievo(contenutoTabella, colonna, s.enumerato, scalaAbbondanze);
			DialogoIspezioneRilievo dialogo = new DialogoIspezioneRilievo(rilievo);
			UtilitaGui.centraDialogoAlloSchermo(dialogo, UtilitaGui.CENTRO);
			dialogo.setVisible(true);
		}catch(Exception ex){
			ComunicazioneEccezione ce = new ComunicazioneEccezione(ex);
        	ce.setVisible(true); 
			contenutoListaMessaggi.addElement(new MessaggioErrore(-1, colonna, GeneratoreErrore.COTRUZIONE_RILIEVO, ex.getLocalizedMessage(), Level.SEVERE));
		}
	}
	
	/************************************************************************
	 * Rimuove tutte le colorazioni delle caselle
	 ***********************************************************************/
	private void azionePulisciColori(){
		pilaAnnulla.push(new AzioneAnnullabile("pulizia colori", contenutoTabella.clone()));
		for(int iRiga=0; iRiga<contenutoTabella.getNumeroRighe(); iRiga++){
			for(int iColonna=0; iColonna<contenutoTabella.getNumeroColonne(); iColonna++){
				contenutoTabella.dati[iRiga][iColonna].livello = null;
			}
		}
		tabella.updateUI();
	}
	
	/************************************************************************
	 * Annulla l'utima azione eseguita
	 ***********************************************************************/
	private void azioneAnnulla(){
		AzioneAnnullabile aa = pilaAnnulla.pop();
		// FIXME: mancano gli altri
		if(aa.getContenuto()!=null){
			aggiornaTabella(aa.getContenuto());
		}
		tabella.updateUI();
		aggiornaAnnulla();
	}
	
	/************************************************************************
	 * Cerca & sostituisci
	 ***********************************************************************/
	private void azioneCercaSostituisci(){
		CercaSostituisci cs = new CercaSostituisci();
		UtilitaGui.centraDialogoAlloSchermo(cs, UtilitaGui.CENTRO);
		cs.setVisible(true);
		System.out.println("Chiuso con ok:"+cs.isEsegui());
		if(cs.isEsegui()){
			pilaAnnulla.push(new AzioneAnnullabile("cerca e sostituisci", contenutoTabella.clone()));
			ControllerTabella.cercaSostituisci(contenutoTabella, 
					cs.getCerca(), 
					cs.getSostituisci(), 
					cs.getArea(), 
					cs.isParziale());
			tabella.updateUI();
			aggiornaAnnulla();
		}
	}
	
	/************************************************************************
	 * Calcola le coordinate WGS84 partendo da altri datum
	 ***********************************************************************/
	private void azioneCalcolaCoordinate(){
		pilaAnnulla.push(new AzioneAnnullabile("calcola coordinate", contenutoTabella.clone()));
		ControllerTabella.calcolaCoordinate(contenutoTabella, this);
		aggiornaAnnulla();
	}
	
	/************************************************************************
	 * Porta i dati nel formato ISO 8601
	 ***********************************************************************/
	private void azioneConvertiDate(){
		pilaAnnulla.push(new AzioneAnnullabile("converti date", contenutoTabella.clone()));
		ControllerTabella.convertiDate(contenutoTabella, this);
		tabella.updateUI();
		aggiornaAnnulla();
	}
	
	/************************************************************************
	 * Inserisce le annotazioni nei nomi di specie
	 ***********************************************************************/
	private void azioneAnnotaSpecie(){
		pilaAnnulla.push(new AzioneAnnullabile("annota specie", contenutoTabella.clone()));
		ControllerTabella.annotaSpecie(contenutoTabella, this);
		aggiornaAnnulla();
	}
	
	/************************************************************************
	 * Permette di inserire i dati di progetto sfruttando i 
	 * suggerimenti del server
	 ***********************************************************************/
	private void azioneDatiProgetto(){
		DialogoProgettoSottoprogetto dps = new DialogoProgettoSottoprogetto();
		UtilitaGui.centraDialogoAlloSchermo(dps, UtilitaGui.CENTRO);
		dps.setVisible(true);
		if(dps.chiusoConOK){
			pilaAnnulla.push(new AzioneAnnullabile("dati progetto", contenutoTabella.clone()));
			String progetto = dps.getProgetto();
			String sottoprogetto = dps.getSottoprogetto();
			try {
				ControllerTabella.aggiungiCampo(contenutoTabella, "DirectoryInfo.SubContainerName", sottoprogetto);
				ControllerTabella.aggiungiCampo(contenutoTabella, "DirectoryInfo.ContainerName", progetto);
			} catch (Exception e) {
				ComunicazioneEccezione ce = new ComunicazioneEccezione(e);
	        	ce.setVisible(true); 
			}
			aggiornaTabella(contenutoTabella);
			aggiornaAnnulla();
		}
	}
	
	/************************************************************************
	 * Permette di inserire i diritti di accesso
	 ***********************************************************************/
	private void azioneAggiungiDiritti(){
		pilaAnnulla.push(new AzioneAnnullabile("dati progetto", contenutoTabella.clone()));
		try {
			ControllerTabella.aggiungiCampo(contenutoTabella, "DirectoryInfo.OthersWriteRights", Proprieta.recupera("umask.otherWrite"));
			ControllerTabella.aggiungiCampo(contenutoTabella, "DirectoryInfo.OthersReadRights", Proprieta.recupera("umask.otherRead"));
			ControllerTabella.aggiungiCampo(contenutoTabella, "DirectoryInfo.ContainerWriteRights", Proprieta.recupera("umask.groupWrite"));
			ControllerTabella.aggiungiCampo(contenutoTabella, "DirectoryInfo.ContainerReadRights", Proprieta.recupera("umask.groupRead"));
			ControllerTabella.aggiungiCampo(contenutoTabella, "DirectoryInfo.OwnerWriteRights", Proprieta.recupera("umask.ownerWrite"));
			ControllerTabella.aggiungiCampo(contenutoTabella, "DirectoryInfo.OwnerReadRights", Proprieta.recupera("umask.ownerRead"));			
		} catch (Exception e) {
			ComunicazioneEccezione ce = new ComunicazioneEccezione(e);
        	ce.setVisible(true); 
		}
		aggiornaTabella(contenutoTabella);
		aggiornaAnnulla();
	}
	
	/************************************************************************
	 * L'invio dei rilievi richiede tempo perché viene fatto dal server
	 * per questo motivo si usa uno SwingWorker e il lavoro viene diviso in due parti:
	 * - invio della richiesta al server
	 * - elaborazione della risposta
	 ***********************************************************************/
	private void azioneInviaRilievi(boolean simulazione){
		Component grigio = jAttesa();
		SwingWorker<SimpleBotanicalData[], Void> lavoratore = new SwingWorker<SimpleBotanicalData[], Void>(){
			SimpleBotanicalData[] risposta;
			@Override
			protected SimpleBotanicalData[] doInBackground() throws Exception {
				ValoreEnumeratoDescritto s = (ValoreEnumeratoDescritto) stratificazione.getSelectedItem();
				ValoreEnumeratoDescritto abbondanza = (ValoreEnumeratoDescritto) scalaAbbondanze.getSelectedItem();
				ScalaSample scalaAbbondanze = GestoreScale.buildForName(abbondanza.enumerato);
				
				risposta = ControllerTabella.invioAlServerRichiesta(contenutoTabella, s.enumerato, scalaAbbondanze, simulazione); 
				return risposta;
			}
			@Override
		    public void done() {
				ControllerTabella.invioAlServerElaboraRisposta(contenutoTabella, risposta, Finestra.this);
				tabella.updateUI();
				grigio.setVisible(false);
			}
			
		};
		lavoratore.execute();
		this.setGlassPane(grigio); // mostra messaggio di attesa
		grigio.setVisible(true);
	}
	
	private void azioneCorreggiCoordinate(){
		pilaAnnulla.push(new AzioneAnnullabile("converti coordinate", contenutoTabella.clone()));
		ControllerTabella.correggiCoordinate(contenutoTabella, this);
		tabella.updateUI();
		aggiornaAnnulla();
	}
	
	private void azioneConvertiEsposizione(){
		pilaAnnulla.push(new AzioneAnnullabile("converti esposizione", contenutoTabella.clone()));
		ControllerTabella.convertiEsposizione(contenutoTabella, this);
		tabella.updateUI();
		aggiornaAnnulla();
	}
	
	private void azioneConvertiInclinazione(){
		pilaAnnulla.push(new AzioneAnnullabile("converti esposizione", contenutoTabella.clone()));
		ControllerTabella.convertiInclinazione(contenutoTabella, this);
		tabella.updateUI();
		aggiornaAnnulla();
	}
	
	/************************************************************************
	 * Mostra a schermo un pannello di aiuto
	 ***********************************************************************/
	private void selezionatoAiuto(){
		String selezionato = (String) comboAiuti.getSelectedItem();
		if("aiuto".equals(selezionato)){
			return;
		}
		// costruisco la finestra
		InfoPanel ip = InfoPanelFactory.getInfoPanel(selezionato);
		JDialog dialogo = new JDialog();
		JPanel principale = new JPanel(new BorderLayout());
		JButton chiudi = new JButton("chiudi");
		principale.add(ip.getPannello(), BorderLayout.CENTER);
		principale.add(chiudi, BorderLayout.SOUTH);
		dialogo.getContentPane().add(principale);
		dialogo.pack();
		dialogo.setTitle("Info: "+ip.getNome());
		UtilitaGui.centraDialogoAlloSchermo(dialogo, UtilitaGui.CENTRO);
		chiudi.addActionListener(ac->{
			dialogo.setVisible(false);
		});
		dialogo.setVisible(true);
	}
	
	/************************************************************************
	 * Imposta le dimensioni delle colonne a seconda del contenuto
	 ***********************************************************************/
	private void impostaDimensioniColonne(){
		for(int i=0; i<contenutoTabella.headerColonne.length; i++){
			if(contenutoTabella.headerColonne[i].equals(HeaderColonna.DEFINIZIONI)){
				tabella.setLarghezzaColonna(i, 300);
			}else if(contenutoTabella.headerColonne[i].equals(HeaderColonna.RILIEVO)){
				tabella.setLarghezzaColonna(i, 150);
			}else{
				tabella.setLarghezzaColonna(i, 80);
			}
		}
	}
	
	/************************************************************************
	 * Costruisce un pulsante da applicare alla pulsantiera superiore,
	 * è un pulsate con due righe di testo e un'icona.
	 * L'icona viene cercata automaticamente in base al testo del pulsante
	 * @param r1 la prima rga di testo
	 * @param r2 la seconda riga di testo (può essere null)
	 * @param modificaDati true se l'azione prevede una modifica dei dati
	 * @param toto true se il pulsante deve tener conto del fatto di essere stato premuto
	 * @param al l'ascoltatore per ActionEvent (può essere null)
	 * @return il pulsante costruito
	 ***********************************************************************/
	private JLabel jButtonDueRighe(String r1, String r2, boolean modificaDati, boolean todo, ActionListener al){
		String testo; 
		JLabel jb = new JLabel();
		
		// apparenza
		jb.setFont(new Font("Arial", Font.PLAIN, 12));
		jb.setOpaque(true);
		jb.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(coloreEvidenziato.get(modificaDati), 1, true),
				BorderFactory.createEmptyBorder(3, 3, 3, 3)
		));
		jb.setForeground(coloreIcona.get(modificaDati));
		// tento di caricare una icona
		String nomeFile = "icone/"+r1+(r2==null?"": " "+r2)+".png";
		URL risorsa = Finestra.class.getResource(nomeFile);
		if(risorsa!=null){
			ImageIcon ii = doAlpha(new ImageIcon(risorsa), coloreIcona.get(modificaDati)); 
			jb.setIcon(ii);
		}
		// imposto il testo in modo che vada a capo
		testo = "<html><body style=\"text-align:"+(risorsa==null?"center":"left")+"\">"+r1+(r2!=null?"<br/>"+r2:"")+"</body></html>";
		jb.setText(testo);
		if(todo){
			daEvidenziare.add(jb);
		}
		
		if(al==null){
			jb.setEnabled(false);
		}else{
			jb.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					al.actionPerformed(null);
				}
			});
		}
		jb.putClientProperty("modificaDati", modificaDati);
		if(modificaDati){
			jb.setBackground( coloreBase.get(true) );
		}else{
			jb.setBackground( coloreBase.get(false) );
		}
		return jb;
	}
	
	/************************************************************************
	 * @return un pannello da usare come GlassPane, intercetta il click
	 * del mouse 
	 ***********************************************************************/
	private static JPanel jAttesa(){
		JPanel grigio = new JPanel(new BorderLayout()){
			private static final long serialVersionUID = 1L;
			{
				setOpaque(false);
			}
			@Override
			public void paintComponent(Graphics g){
				Color k = new Color(0.9f,0.9f,0.9f,0.75f);
				g.setColor(k);
				Rectangle r = g.getClipBounds();
				g.fillRect(r.x, r.y, r.width, r.height);
				super.paintComponent(g);
			}
		};
		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// non facio nulla, serve per impedire azioni
			}
		};
		grigio.addMouseListener(ma);
		JLabel attendi = new JLabel("Attendi...");
		attendi.setFont(attendi.getFont().deriveFont(25.00f));
		attendi.setHorizontalAlignment(SwingConstants.CENTER);
		grigio.add(attendi, BorderLayout.CENTER);
		
		return grigio;
	}
	

	@Override
	public void addMessaggio(MessaggioErrore me) {
		contenutoListaMessaggi.addElement(me);
	}

	@Override
	public void rimuoviSelettivo(GeneratoreErrore g) {
		for(int i=0; i<contenutoListaMessaggi.size();i++){
			if(contenutoListaMessaggi.get(i).generatoDa==g){
				contenutoListaMessaggi.removeElementAt(i);
				i--;
			}
		}
		
	}
	
	/************************************************************************
	 * JTabel non aggiorna automaticamente l'interfaccia al variare del
	 * numero di colonne, questo metodo crea una nuova tabella e la 
	 * reinserisce nello scroll
	 * @param ct
	 ***********************************************************************/
	private void aggiornaTabella(ContenutoTabella ct){
		KeyListener kl[] = tabella.tabellaDati.getKeyListeners();
		MouseListener ml[] = tabella.tabellaDati.getMouseListeners();
		contenutoTabella = ct;
		tabella = new TabellaComposta(ct);
		pannelloTabella.removeAll();
		pannelloTabella.add(tabella, BorderLayout.CENTER);
		impostaDimensioniColonne();
		// reinserisco i listeners
		for(KeyListener k: kl){
			tabella.tabellaDati.addKeyListener(k);
		}
		for(MouseListener m: ml){
			tabella.tabellaDati.addMouseListener(m);
		}
	}
	
	/************************************************************************
	 * Aggiorna il pulsante "annulla" in base alle azioni presenti nella pila
	 ***********************************************************************/
	private void aggiornaAnnulla(){
		if(pilaAnnulla.size()==0){
			pulsanteAnnulla.setEnabled(false);
		}else{
			pulsanteAnnulla.setEnabled(true);
			// imposto il testo del pulsante
			AzioneAnnullabile aa = pilaAnnulla.peek();
			String testo = "<html><body style=\"text-align:left\">annulla<br/>"+aa.getNome()+"</body></html>";
			pulsanteAnnulla.setText(testo);
		}
	}
	
	private static ImageIcon doAlpha(ImageIcon originale, Color colore){
		int width = originale.getIconWidth();
		int height = originale.getIconHeight();
		BufferedImage alphaMask = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gAlpha = (Graphics2D) alphaMask.getGraphics();
		ImageIcon risposta;
		
		gAlpha.drawImage(originale.getImage(), 0, 0, null);
	    int[] pixels = alphaMask.getRGB(0, 0, width, height, null, 0, width);
	    int pos,alpha;
	    int coloreInt = (colore.getRed()<<16) + (colore.getGreen()<<8) + (colore.getBlue());
	    
	    for(pos=0; pos<pixels.length; pos++){
	    	// siccome l'immagine è in toni di griglio prendo l'ultimo byte;
	    	alpha = pixels[pos] & 0x000000ff;
			pixels[pos] = (alpha<<24) | coloreInt ;
	    }
		
	    alphaMask.setRGB(0, 0, width, height, pixels, 0, width);
		risposta = new ImageIcon(alphaMask);
		return risposta;
	}

}
