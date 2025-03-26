package it.aspix.tabparser.convertitori;

import it.aspix.archiver.componenti.CoppiaCSTesto;
import it.aspix.archiver.componenti.CoppiaED;

public class ConvertitoreEsposizione {
    
	/* lo implemento in quest amaniera non proprio efficace per poter poi creare anche un metodo tipo
	 * getTraduzioni che permetta di vedere come documentazione (html?) cosa fa
	 */
    private static final CoppiaED traduzione[] = {
        new CoppiaCSTesto("N","0"),new CoppiaCSTesto("NNE","23"),new CoppiaCSTesto("NE","45"),new CoppiaCSTesto("ENE","68"),
        new CoppiaCSTesto("E","90"),new CoppiaCSTesto("ESE","113"),new CoppiaCSTesto("SE","135"),new CoppiaCSTesto("SSE","158"),
        new CoppiaCSTesto("S","180"),new CoppiaCSTesto("SSO","203"),new CoppiaCSTesto("SO","225"),new CoppiaCSTesto("OSO","248"),
        new CoppiaCSTesto("O","270"),new CoppiaCSTesto("ONO","293"),new CoppiaCSTesto("NO","315"),new CoppiaCSTesto("NNO","337")
    };
    // fortuna vuole che le sigle duplicate tipo "ESE" siano uguali in italiano e in inglese
    private static final CoppiaED traduzioneEN[] = {
        new CoppiaCSTesto("N","0"),new CoppiaCSTesto("NNE","23"),new CoppiaCSTesto("NE","45"),new CoppiaCSTesto("ENE","68"),
        new CoppiaCSTesto("E","90"),new CoppiaCSTesto("ESE","113"),new CoppiaCSTesto("SE","135"),new CoppiaCSTesto("SSE","158"),
        new CoppiaCSTesto("S","180"),new CoppiaCSTesto("SSW","203"),new CoppiaCSTesto("SW","225"),new CoppiaCSTesto("WSW","248"),
        new CoppiaCSTesto("W","270"),new CoppiaCSTesto("WNW","293"),new CoppiaCSTesto("NW","315"),new CoppiaCSTesto("NNW","337")
    };
    
    /*************************************************************************
     * @param cardinale un punto espresso come NNE o N o ...
     * @return l'angolo in gradi o null se non trova una corrispondenza
     ************************************************************************/
    private static String daCardinaleANumero(String cardinale){
        for(int i=0;i<traduzione.length;i++){
            if(traduzione[i].getEsterno().equals(cardinale)){
                return traduzione[i].getDescrizione();
            }
        }
        for(int i=0;i<traduzioneEN.length;i++){
            if(traduzioneEN[i].getEsterno().equals(cardinale)){
                return traduzioneEN[i].getDescrizione();
            }
        }
        return null;
    }
	
    public String analizzaTesto(String s) throws Exception{
    	String x = daCardinaleANumero(s);
        if(x!=null){
        	return x;
        }
        throw new Exception("Esposizione \""+s+"\" non comprensibile.");
    }
}
