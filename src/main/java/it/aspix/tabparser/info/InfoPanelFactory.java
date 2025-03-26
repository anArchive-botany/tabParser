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

import it.aspix.archiver.dialoghi.ComunicazioneEccezione;
import it.aspix.sbd.introspection.ReflectUtil;

import java.util.ArrayList;
import java.util.Hashtable;

/****************************************************************************
 * Gestisce i pannelli di informazione disponibili in questo pacchetto
 *  
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class InfoPanelFactory {
	private static Hashtable<String,InfoPanel> info = new Hashtable<>();
	
	/************************************************************************
     * Inizializzo tutti i pannelli di informazioni
     * @throws IOException 
     * @throws ClassNotFoundException 
     ***********************************************************************/
    static{
    	try{
	    	Class<?> classe;
	    	String nomePacchetto = (new InfoPanelFactory()).getClass().getPackage().getName();
	    	InfoPanel i;
	    	ArrayList<String> nomiClassi = ReflectUtil.getClassNamesFromPackage(nomePacchetto);
	  	
	    	for (String f: nomiClassi){
	    		if(Character.isLowerCase(f.charAt(0))){
	    			// serve per evitare file di iformazioni come readme.html
	    			continue;
	    		}
	    		classe = Class.forName(nomePacchetto+"."+f);
	    		try{
		    		if(!classe.isInterface() && classe.newInstance() instanceof InfoPanel){
		    			i = (InfoPanel) classe.newInstance();
		    			info.put(i.getNome(), i);
		    		}
	    		}catch(InstantiationException ex){
	    			; // è normale che alcune classi non possano essere istanziate (es: non hanno il costruttore senza argomenti)
	    		}
	    	}
    	}catch(Exception ex){
    		ComunicazioneEccezione ce = new ComunicazioneEccezione(ex);
        	ce.setVisible(true); 
    	}
    }
    
    /************************************************************************
     * @return un elenco di nomi di pannelli disponibili
     ***********************************************************************/
    public static String[] getNomi(){
    	String n[] = new String[info.size()];
    	int i=0;
    	for(String s: info.keySet()){
    		n[i++] = s;
    	}
    	return n;
    }
    
    /************************************************************************
     * @param nome del pannello che interessa
     * @return il pannello associato a nome
     ***********************************************************************/
    public static InfoPanel getInfoPanel(String nome){
    	return info.get(nome);
    }

}
