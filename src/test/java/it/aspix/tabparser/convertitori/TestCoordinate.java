package it.aspix.tabparser.convertitori;

import it.aspix.tabparser.convertitori.ConvertitoreLatitudine;

public class TestCoordinate {

	public static void main(String[] args) {
		String[] test = {"10.24 S","18° 21' 38\" Sud", "43.452188 N","45.333","-45.333"};
		
		ConvertitoreLatitudine cl = new ConvertitoreLatitudine();
		
		for(int i=0; i<test.length; i++){
			System.out.print(test[i]+" -> ");
			try {
				System.out.println(cl.analizzaTesto(test[i]));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}		
	}

}
