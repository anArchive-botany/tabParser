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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import org.apache.xmlbeans.SchemaAnnotation;

import it.aspix.archiver.dialoghi.ComunicazioneEccezione;
import it.aspix.sbd.introspection.InformazioniTipiEnumerati;
import it.aspix.sbd.introspection.ReflectUtil;
import it.aspix.sbd.introspection.ValoreEnumeratoDescritto;
import it.aspix.tabparser.tabella.HeaderRiga;

/****************************************************************************
 * Gestisce un elenco degli editor disponibili
 *
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class EditorFactory {

	private static Hashtable<String,Editor> editors = new Hashtable<>();

	/************************************************************************
     * Inizializza tutti gli editor
     * @throws IOException
     * @throws ClassNotFoundException
     ***********************************************************************/
    static{
    	try{
	    	Class<?> classe;
	    	String nomePacchetto = (new EditorFactory()).getClass().getPackage().getName();
	    	Editor e;

	    	ArrayList<String> nomiClassi = ReflectUtil.getClassNamesFromPackage(nomePacchetto);

	    	for (String f: nomiClassi){
	    		classe = Class.forName(nomePacchetto+"."+f);
	    		try{
		    		if(!classe.isInterface() && classe.newInstance() instanceof Editor){
		    			e = (Editor) classe.newInstance();
		    			editors.put(e.getVoceMenu(), e);
		    		}
	    		}catch(InstantiationException ex){
	    			// è normale che alcune classi non possano essere istanziate
	    			// (es: non hanno il costruttore senza argomenti o sono astratte)
	    			// quindi non si fa nulla
	    		}catch(Exception ex){
	    			// fai qualcosa! o magari niente :-)
	    		}
	    	}
	    	// editor per i tipi enumerati
	    	HashSet<String> creati = new HashSet<>();
	    	for(HeaderRiga hr: HeaderRiga.possibili){
	    		if(hr.describedPath!=null && hr.describedPath.tipoSBD!=null){
	    			ArrayList<ValoreEnumeratoDescritto> ved = InformazioniTipiEnumerati.getValoriDescritti(hr.describedPath.tipoSBD, "it");
	    			// SchemaAnnotation sa = InformazioniTipiEnumerati.getDescrizioneTipo(hr.describedPath.tipoSBD);
	    			// String titolo = sa.getNome("it")!=null ? sa.getNome("it") : hr.describedPath.tipoSBD;
	    			// al posto della riga sotto c'erano le due sopra, ma non funzionano più
	    			// perché è stato rimosso InformazioniTipiEnumerati.getDescrizioneTipo
	    			String titolo = hr.describedPath.tipoSBD;
	    		 	if( !creati.contains(titolo) ){
		    			ProprietaDescrittaEditorCallback pdec = new ProprietaDescrittaEditorCallback(){
							private static final long serialVersionUID = 0L;
							@Override
							public String getTitolo() {
								return titolo;
							}
							@Override
							public ArrayList<ValoreEnumeratoDescritto> getValori() {
								return ved;
							}
		    			};
		    			creati.add(titolo);
		    			editors.put(titolo, pdec);
	    		 	}
	    		}
	    	}
    	}catch(Exception ex){
    		ComunicazioneEccezione ce = new ComunicazioneEccezione(ex);
        	ce.setVisible(true);
    	}
    }

    public static String[] getNomi(){
    	String n[] = new String[editors.size()];
    	int i=0;
    	for(String s: editors.keySet()){
    		n[i++] = editors.get(s).getVoceMenu();
    	}
    	return n;
    }

    public static Editor getEditor(String nome){
    	return editors.get(nome);
    }

}
