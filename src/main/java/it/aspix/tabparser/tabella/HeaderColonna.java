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

import java.util.ArrayList;

/****************************************************************************
 * Il dato contenuto in una colonna è rappresentato da un String
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class HeaderColonna implements Cloneable{
	private String valore;
	
	public HeaderColonna clone(){
		HeaderColonna hc = new HeaderColonna(valore);
		return hc;
	}

	public HeaderColonna(String nome) {
		super();
		this.valore = nome;
	}
	
	public String toString(){
		return valore;
	}
	
	public String getValore() {
		return valore;
	}

	public void setValore(String valore) {
		this.valore = valore;
	}
	
	public static final HeaderColonna NON_USARE = new HeaderColonna("non usare");
	public static final HeaderColonna RILIEVO = new HeaderColonna("rilievo");
	public static final HeaderColonna DEFINIZIONI  = new HeaderColonna("definizioni");
	public static final HeaderColonna STRATI  = new HeaderColonna("strati");
	
	protected static ArrayList<HeaderColonna> possibili = new ArrayList<>();
	static{
		possibili.add(NON_USARE);
		possibili.add(RILIEVO);
		possibili.add(DEFINIZIONI);
		possibili.add(STRATI);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof HeaderColonna){
			return this.valore.equals(((HeaderColonna)o).valore);
		}else{
			return false;
		}
	}
	
}
