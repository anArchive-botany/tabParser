package it.aspix.tabparser.convertitori;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class ConvertitoreCoordinata {
	
    private Tentativo tentativi[];
	
    private abstract class Tentativo{
        public abstract Pattern getPattern();
        public abstract double converti(Matcher m);
    }
    
    private boolean isNegativo(String x){
    	return !segni.get(x);
    }
    
    HashMap<String, Boolean> segni;
    // Questa roba è davvero poco efficiente
    private void calcolaSegni(String pos, String neg){
    	segni = new HashMap<>();
    	String parti[];
    	parti = pos.split("\\|");
    	for(String p: parti){
    		segni.put(p, true);
    	}
    	parti = neg.split("\\|");
    	for(String p: parti){
    		segni.put(p, false);
    	}
    }
	
	protected void init(String positivo, String negativo, String aliasPositivo, String aliasNegativo){
        String pos;
        String neg;
        pos = positivo+"|"+aliasPositivo;
        neg = negativo+"|"+aliasNegativo;
        String direzioni = "("+pos+"|"+neg+")";
        calcolaSegni(pos, neg);
        tentativi = new Tentativo[]{
            new Tentativo(){ // 18° 26' 16.67"
                @Override
                public Pattern getPattern() {
                    return Pattern.compile("^(\\d+)\u00b0 ?(\\d+)' ?(\\d+(?:\\.\\d+)?)\" "+direzioni+"$");
                }
                @Override
                public double converti(Matcher m) {
                    double gradi = Double.parseDouble(m.group(1))+Double.parseDouble(m.group(2))/60+Double.parseDouble(m.group(3))/3600;
                    if(m.group(4)!=null && isNegativo(m.group(4)) )
                        gradi=-gradi;
                    return gradi;
                }                
            },
            new Tentativo(){ // 18 12 45.56
                @Override
                public Pattern getPattern() {
                    return Pattern.compile("^(\\d+) (\\d+) (\\d+(?:\\.\\d+)?) "+direzioni+"$");
                }
                @Override
                public double converti(Matcher m) {
                    double gradi = Double.parseDouble(m.group(1))+Double.parseDouble(m.group(2))/60+Double.parseDouble(m.group(3))/3600;
                    if(m.group(4)!=null && isNegativo(m.group(4)) )
                        gradi=-gradi;
                    return gradi;
                }                
            },
            new Tentativo(){ // 18° 16.2345'
                @Override
                public Pattern getPattern() {
                    return Pattern.compile("^(\\d+)\u00b0 ?(\\d+(?:\\.\\d+)?)' "+direzioni+"$");
                }
                @Override
                public double converti(Matcher m) {
                    double gradi = Double.parseDouble(m.group(1))+Double.parseDouble(m.group(2))/60;
                    if(m.group(3)!=null && isNegativo(m.group(3)) )
                        gradi=-gradi;
                    return gradi;
                }                
            },
            new Tentativo(){ // 18 26.763
                @Override
                public Pattern getPattern() {
                    return Pattern.compile("^(\\d+) (\\d+(?:\\.\\d+))? "+direzioni+"$");
                }
                @Override
                public double converti(Matcher m) {
                    double gradi = Double.parseDouble(m.group(1))+Double.parseDouble(m.group(2))/60;
                    if( m.group(3)!=null && isNegativo(m.group(3)) )
                        gradi=-gradi;
                    return gradi;
                }                
            },
            new Tentativo(){ // 26.763
                @Override
                public Pattern getPattern() {
                    return Pattern.compile("^(\\d+(?:\\.\\d+))? "+direzioni+"$");
                }
                @Override
                public double converti(Matcher m) {
                    double gradi = Double.parseDouble(m.group(1));
                    if( m.group(2)!=null && isNegativo(m.group(2)) )
                    	gradi=-gradi;
                    return gradi;
                }                
            },
            new Tentativo(){ // un numero con segno
                @Override
                public Pattern getPattern() {
                    return Pattern.compile("^([\\-+]?\\d+(?:\\.\\d+))?$");
                }
                @Override
                public double converti(Matcher m) {
                    double gradi = Double.parseDouble(m.group(1));
                    return gradi;
                }                
            }
            
        };
    }
	
    public String analizzaTesto(String s) throws Exception{
        Matcher m;
        
        if(s!=null && s.length()>0){
        	s = s.trim().replace('“', '"').replace('”', '"').replace('‟','"');
            for(int i=0 ; i<tentativi.length ; i++){
                m = tentativi[i].getPattern().matcher(s);
                if(m.find()){
                    return "" + tentativi[i].converti(m);
                }
            }
        }
        throw new Exception("Coordinata \""+s+"\" non comprensibile.");
    }
}
