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

import java.awt.Color;
import java.util.HashMap;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

/****************************************************************************
 * Costanti di varia utilità
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class Costanti {
	public static Border bordoSelezionato = BorderFactory.createLineBorder(Color.BLACK, 3);
	// public static Border bordoNonSelezionato = BorderFactory.createLineBorder(new Color(230,230,230), 1);
	public static Border bordoNonSelezionato = BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(230,230,230));
	protected static final Color COLORE_CELLE = Color.WHITE;
	protected static final Color COLORE_HEADER = new Color(200,200,200);
	protected static final Color COLORE_HEADER_NON_USARE = new Color(240,240,240);
	
	protected static final Color COLORE_CELLA_SENZA_LIVELLO = Color.WHITE; 
	
	protected static final Color COLORE_TESTO_DATI = Color.BLACK;
	protected static final Color COLORE_TESTO_DATI_NON_INVIATI = new Color(170,170,170);
	
	public static HashMap<Level, Color> mappaturaLivelliColori = new HashMap<>();
	static {
		mappaturaLivelliColori.put(Level.SEVERE, Color.RED);
		mappaturaLivelliColori.put(Level.WARNING, Color.ORANGE); // Color(255, 200, 0); 
		mappaturaLivelliColori.put(Level.INFO, new Color(255,230,0));
		mappaturaLivelliColori.put(Level.CONFIG, new Color(255,230,0));
		mappaturaLivelliColori.put(Level.FINE, Color.YELLOW); // Color(255, 255, 0);
		mappaturaLivelliColori.put(Level.FINER, new Color(255, 255, 160));
		mappaturaLivelliColori.put(Level.FINEST, new Color(255, 255, 220));
		// sebbene la mappatura a null non dia errore non funziona neanche cioè restituisce comunque null
	}
	
}
