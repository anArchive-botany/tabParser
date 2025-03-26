package it.aspix.tabparser.convertitori;


public class ConvertitoreInclinazione {
    
	// converte in ogni caso perché nelle tabelle ci si aspetta di avere numeri senza segno % o °
	private static String daTestoANumero(String testo){
	    int angolo=(int)(Math.atan2(Double.parseDouble(testo),100)*180/Math.PI+0.5);
	    return ""+angolo;   
    }
	
    public String analizzaTesto(String s) throws Exception{
    	String x = daTestoANumero(s);
        if(x!=null){
        	return x;
        }
        throw new Exception("Inclinazione \""+s+"\" non comprensibile.");
    }
}
