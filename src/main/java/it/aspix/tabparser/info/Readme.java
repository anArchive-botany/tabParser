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

import it.aspix.tabparser.tabella.ContenutoTabella;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/****************************************************************************
 * Informazioni sull'uso del programma, vengono lette dal file
 * "readme.html" presente nella stessa cartella 
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class Readme extends JPanel implements InfoPanel{
	
	private static final long serialVersionUID = 1L;

	public Readme(){
		super();
		
		StringBuilder sb = new StringBuilder();
		try {
			char buffer[] = new char[1024];
			int letti;
			InputStream is = Readme.class.getResourceAsStream("readme.html");
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			while( (letti=isr.read(buffer))>0 ){
				sb.append(buffer,0,letti);
			}
			isr.close();
		} catch (IOException e) {
			sb.append("info non disponibili");
		}
		
		JLabel l = new JLabel(sb.toString());
		JPanel unico = new JPanel(new BorderLayout());
		unico.add(l, BorderLayout.NORTH);
		unico.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.setLayout(new BorderLayout());
		this.add(unico, BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(400,500));
	}
	
	public JPanel getPannello(){
		return this;
	}

	@Override
	public void setValore(ContenutoTabella ct, int riga, int colonna) {
		// non serve a 
	}

	@Override
	public String getNome() {
		return "README.txt";
	}
}