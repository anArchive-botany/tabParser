package it.aspix.tabparser.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

// http://tutiez.com/how-make-jcombobox-drop-down-width-as-wide-as-needed.html
public class WiderDropDownCombo<E> extends JComboBox<E> {
	 
	private static final long serialVersionUID = 1L;
	private boolean layingOut = false;
	private int widestLengh = 0;
	private boolean wide = false;
	 
	public WiderDropDownCombo() {
		super();
	}
	
	public WiderDropDownCombo(DefaultComboBoxModel<E> cbm) {
		super(cbm);
	}
	 
	public boolean isWide() {
		return wide;
	}
	
	//Setting the JComboBox wide
	public void setWide(boolean wide) {
		this.wide = wide;
		widestLengh = getWidestItemWidth();
	}
	
	public Dimension getSize(){
		Dimension dim = super.getSize();
		if(!layingOut && isWide())
			dim.width = Math.max( widestLengh, dim.width );
		return dim;
	}
	 
	public int getWidestItemWidth(){
		int numOfItems = this.getItemCount();
		Font font = this.getFont();
		FontMetrics metrics = this.getFontMetrics( font );
		int widest = 0;
		for ( int i = 0; i < numOfItems; i++ ){
			String item = this.getItemAt( i ).toString();
			int lineWidth = metrics.stringWidth( item );
			widest = Math.max( widest, lineWidth );
		}
		return widest + 25;
	}
	
	public void doLayout(){
		try{
			layingOut = true;
			super.doLayout();
		}finally{
			layingOut = false;
		}
	}

}
