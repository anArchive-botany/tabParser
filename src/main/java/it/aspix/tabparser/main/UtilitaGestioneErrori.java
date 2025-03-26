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

import javax.swing.JLabel;
import javax.swing.JOptionPane;

/****************************************************************************
 * Metodi di utilità per comunicare situazioni di errore all'utente
 * http://stackoverflow.com/questions/115008/how-can-we-print-line-numbers-to-the-log-in-java
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class UtilitaGestioneErrori{

    public static String informazioniDebug(){
    	StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        return ste.getClassName()+"."+ste.getMethodName()+"()@" + ste.getLineNumber();
    }
    
    /************************************************************************
     * mostra un testo di errore
     * @param s1 il testo descrittivo del messaggio 
     * @param s2 un testo che verrà formattato con <pre>
     ***********************************************************************/
    public static void mostraErrore(String s1, String s2){
    	String title = "<html><body style='width: 200px; padding: 5px;'>"
                + "<p>"
                + s1+"</p><pre>"
                + s2+"</pre></body></html>";
            JLabel textLabel = new JLabel(title);
            JOptionPane.showMessageDialog(null, textLabel, "Errore", JOptionPane.ERROR_MESSAGE);
    }
    
}
