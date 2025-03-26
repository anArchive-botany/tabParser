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

import it.aspix.tabparser.tabella.ContenutoTabella;
import it.aspix.tabparser.tabella.DatoTabella;
import it.aspix.tabparser.tabella.HeaderRiga;

/****************************************************************************
 * Una azione annullabile, i dati possono essere impostati soltanto 
 * dal costruttore
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class AzioneAnnullabile {
	// il nome dell'azione da annullare
	private String nome;
	// i campi qui sotto sono quello che in C sarebbe una union:
	// se ne può usare uno soltanto
	private DatoTabella dato;
	private String headerColonna;
	private HeaderRiga headerRiga;
	private ContenutoTabella contenuto;
	// i campi sottostanti possono essere o meno sensati a seconda
	// del dato modificato
	int riga;
	int colonna;
	
	public AzioneAnnullabile(String nome, DatoTabella dato, int riga, int colonna) {
		super();
		this.nome = nome;
		this.dato = dato;
		this.riga = riga;
		this.colonna = colonna;
	}

	public AzioneAnnullabile(String nome, String headerColonna, int colonna) {
		super();
		this.nome = nome;
		this.headerColonna = headerColonna;
		this.colonna = colonna;
	}

	public AzioneAnnullabile(String nome, HeaderRiga headerRiga, int riga) {
		super();
		this.nome = nome;
		this.headerRiga = headerRiga;
		this.riga = riga;
	}

	public AzioneAnnullabile(String nome, ContenutoTabella contenuto) {
		super();
		this.nome = nome;
		this.contenuto = contenuto;
	}

	public DatoTabella getDato() {
		return dato;
	}

	public String getHeaderColonna() {
		return headerColonna;
	}

	public HeaderRiga getHeaderRiga() {
		return headerRiga;
	}

	public ContenutoTabella getContenuto() {
		return contenuto;
	}

	public int getRiga() {
		return riga;
	}

	public int getColonna() {
		return colonna;
	}
	
	public String toString(){
		return nome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
}
