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

import it.aspix.sbd.ValoreEnumeratoDescritto;

import java.util.ArrayList;

/****************************************************************************
 * Permette di inserire alcuni codici epsg
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class CodiceEPSGDialog extends ProprietaDescrittaEditorCallback {

	private static final long serialVersionUID = 1L;
	
	private static ArrayList<ValoreEnumeratoDescritto> valori = new ArrayList<ValoreEnumeratoDescritto>();
	static {
		valori.add(new ValoreEnumeratoDescritto("32632","WGS 84 / UTM zone 32N"));
		valori.add(new ValoreEnumeratoDescritto("32633","WGS 84 / UTM zone 33N"));
		valori.add(new ValoreEnumeratoDescritto("23032","ED50 / UTM zone 32N"));
		valori.add(new ValoreEnumeratoDescritto("23033","ED50 / UTM zone 33N"));
		valori.add(new ValoreEnumeratoDescritto("3003","Monte Mario / Italy zone 1"));
		valori.add(new ValoreEnumeratoDescritto("3004","Monte Mario / Italy zone 2"));
	}
	@Override
	public String getTitolo() {
		return "codici EPSG";
	}
	@Override
	public ArrayList<ValoreEnumeratoDescritto> getValori() {
		return valori;
	}

}
