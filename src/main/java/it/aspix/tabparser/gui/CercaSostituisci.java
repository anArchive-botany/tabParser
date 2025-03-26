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

import it.aspix.tabparser.tabella.ControllerTabella.Area;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/****************************************************************************
 * Finestra di dialogo per cerca & sostituisci
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class CercaSostituisci extends JDialog{

	private static final long serialVersionUID = 1L;
	
	private JTextField cerca = new JTextField();
	private JTextField sostituisci = new JTextField();
	private DefaultComboBoxModel<Area> modelloArea = new DefaultComboBoxModel<>(Area.values());
	private JComboBox<Area> area = new JComboBox<>(modelloArea);
	private JCheckBox parziale = new JCheckBox("anche parziale");
	private boolean chiusoEsegui = false;
	
	public CercaSostituisci(){
		JPanel principale = new JPanel(new GridBagLayout());
		JButton esegui = new JButton("esegui");
		JButton annulla = new JButton("annulla");
		
		principale.add(new JLabel("cerca:"),       new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		principale.add(cerca,                      new GridBagConstraints(1,0,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		principale.add(new JLabel("sostituisci:"), new GridBagConstraints(0,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		principale.add(sostituisci,                new GridBagConstraints(1,1,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		principale.add(new JLabel("area:"),        new GridBagConstraints(0,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		principale.add(area,                       new GridBagConstraints(1,2,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		principale.add(parziale,                   new GridBagConstraints(1,3,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		principale.add(annulla,                    new GridBagConstraints(0,4,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		principale.add(esegui,                     new GridBagConstraints(1,4,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		principale.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		annulla.addActionListener(e->{chiusoEsegui=false;CercaSostituisci.this.setVisible(false);});
		esegui.addActionListener(e->{chiusoEsegui=true;CercaSostituisci.this.setVisible(false);});
		this.setTitle("Cerca & sostituisci");
		this.getContentPane().add(principale);
		this.pack();
		this.setModal(true);
	}
	
	public String getCerca(){
		return cerca.getText();
	}
	
	public String getSostituisci(){
		return sostituisci.getText();
	}
	
	public Area getArea(){
		return (Area) area.getSelectedItem();
	}
	
	public boolean isParziale(){
		return parziale.isSelected();
	}
	
	public boolean isEsegui(){
		return chiusoEsegui;
	}
	
}
