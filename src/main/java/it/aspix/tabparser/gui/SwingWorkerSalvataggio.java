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

import it.aspix.tabparser.tabella.ContenutoTabella;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.SwingWorker;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/****************************************************************************
 * Si occupa del salvataggio della tabella, attività piuttosto lunga
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SwingWorkerSalvataggio extends SwingWorker<Void, Void>{
	
	File file;
	Component grigio;
	ContenutoTabella contenutoTabella;
	String nomeScala;
	
	public SwingWorkerSalvataggio(File file, ContenutoTabella contenutoTabella, Component grigio) {
		this.file = file;
		this.contenutoTabella = contenutoTabella;
		this.grigio = grigio;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		// SpreadsheetDocument ods = contenutoTabella.getODF();
		// ods.save(file);
		
		HSSFWorkbook xls = contenutoTabella.getXLS();
		FileOutputStream fileOut = new FileOutputStream(file);
        xls.write(fileOut);
        fileOut.close();
		
		return null;
	}
	@Override
    public void done() {
		grigio.setVisible(false);
	}
	
}
