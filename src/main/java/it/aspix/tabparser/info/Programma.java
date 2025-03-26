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

import javax.swing.JLabel;
import javax.swing.JPanel;

/****************************************************************************
 * Informazioni generali sul programma
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class Programma extends JPanel implements InfoPanel{
	
	private static final long serialVersionUID = 1L;

	public Programma(){
		super();
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");
		sb.append("<h1>TabParser</h1>");
		sb.append("<h1>contiene software di Apache Software Foundation</h1>");
		sb.append("</body></html>");
		JLabel l = new JLabel(sb.toString());
		this.setPreferredSize(new Dimension(300,300));
		this.setLayout(new BorderLayout());
		this.add(l, BorderLayout.NORTH);
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
		return "programma";
	}
}